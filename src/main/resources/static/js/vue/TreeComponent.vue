<template>
	<div class="tree-wrap" @click.self="closeModal" tabindex="0" @keydown.esc="closeModal">
		<div class="tree-modal">
		<!-- 헤더 -->
			<div class="tree-header">
				<div class="header-content">
				<h1>🌲 IA 메뉴</h1>
					<div class="stats">
						<div class="stat-item">
							<span>노드:</span>
							<span class="stat-number">{{ stats.totalNodes }}</span>
						</div>
						<div class="stat-item">
							<span>파일:</span>
							<span class="stat-number">{{ stats.totalFiles }}</span>
						</div>
						<div class="stat-item">
							<span>1depth:</span>
							<span class="stat-number">{{ stats.depth1Count }}</span>
						</div>
						<div class="stat-item">
							<span>최대깊이:</span>
							<span class="stat-number">5</span>
						</div>
					</div>
				</div>
				<button class="close-btn" @click="closeModal">×</button>
			</div>

			<!-- 검색 -->
			<div class="search-section">
				<div class="search-box">
					<div class="search-icon">🔍</div>
					<input 
						type="text" 
						class="search-input" 
						placeholder="메뉴명, 파일명으로 검색..." 
						v-model="searchInput"
						@keyup.enter="onSearchInput"
					>
				</div>
			</div>

			<!-- 트리 컨테이너 -->
			<div class="tree-content">
				<div class="tree-container">
					<div v-if="loading" class="loading-tree">
						데이터를 로드하는 중...
					</div>
					
					<div v-else-if="error" class="error">
						{{ error }}
					</div>
					
					<div v-else>
						<sheet-node-component
							v-for="(sheetData, sheetName) in filteredSheetData" 
							:key="sheetName"
							:sheet-name="sheetName"
							:tree-data="sheetData"
							:search-term="searchTerm"
						></sheet-node-component>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
const SheetNodeComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/SheetNodeComponent.vue',vue3LoadOption);
});

export default {
	components: {
		SheetNodeComponent
	},
	props: {
		checkedChannel: Object,
		checkedChannelSubIdx: {
			type: Number,
			default: null
		}
	},
	data() {
		return {
			originalSheetData: {},
			filteredSheetData: {},
			searchTerm: '',
			searchInput: '',
			loading: true,
			error: null,
			searchTimeout: null
		};
	},
	computed: {
		stats() {
			let totalNodes = 0;
			let totalFiles = 0;
			let totalSheets = Object.keys(this.originalSheetData).length;
			
			const countStats = (node) => {
				totalNodes++;
				totalFiles += (node.files || []).length;
				Object.values(node.children || {}).forEach(countStats);
			};
			
			Object.values(this.originalSheetData).forEach(sheetData => {
				Object.values(sheetData).forEach(countStats);
			});
			
			return {
				totalNodes,
				totalFiles,
				depth1Count: totalSheets
			};
		}
	},
	methods: {
		async loadExcelData() {
			try {
				const iaFile = this.checkedChannel.ia_tabs[this.checkedChannelSubIdx].ia_file;
				var url = `http://13.125.6.144:7001/${iaFile}`;

				if (this.checkedChannel.code == 'SH_PA' && this.checkedChannelSubIdx == 0) {
					url = `http://13.125.6.144:7001/smart/iaxlsx/ia_smart_new.xlsx`;
				}

				const arrayBuffer = await (await fetch(url)).arrayBuffer();

				const workbook = XLSX.read(arrayBuffer, {
					cellStyles: true,
					cellFormulas: true,
					cellDates: true,
					cellNF: true,
					sheetStubs: true
				});

				const allSheetsData = {};
				
				// 모든 시트를 처리
				workbook.SheetNames.forEach(sheetName => {
					// const sheetName = '공통';
					const worksheet = workbook.Sheets[sheetName];
					const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });

					if (jsonData.length > 1) { // 헤더 제외하고 데이터가 있는 경우만
						allSheetsData[sheetName] = this.buildTreeData(jsonData);
					}
				});
				
				return allSheetsData;
			} catch (error) {
				console.error('파일 로드 실패:', error);
				throw new Error('엑셀 파일을 읽을 수 없습니다.');
			}
		},

		buildTreeData(data) {
			const tree = {};
			let basePath = []; // 1depth~4depth까지의 기본 경로
			let currentScreen1Path = []; // 현재 화면1까지의 경로
			
			for(let i = 1; i < data.length; i++) { // 헤더 행 제외하고 시작
				const row = data[i];
				
				// 빈 행 스킵
				if(!row || row.length === 0) continue;
				
				// 각 depth 컬럼 추출 (1depth~4depth)
				const depths = [
					row[1] ? row[1].toString().trim() : null, // 1depth
					row[2] ? row[2].toString().trim() : null, // 2depth  
					row[3] ? row[3].toString().trim() : null, // 3depth
					row[4] ? row[4].toString().trim() : null, // 4depth
				];
				
				// 화면 컬럼 추출
				const 화면1 = row[5] ? row[5].toString().trim() : null;
				const 화면2 = row[6] ? row[6].toString().trim() : null;
				
				// 파일 정보 추출 (새로운 엑셀 구조에 맞춤)
				const fileInfo = {
					type: row[0],           // 구분 (2차개선 등)
					화면형태: row[7],        // APP, 페이지, 알림팝업 등
					fileName: row[8],       // 파일명
					path: row[9],          // 경로
					del: row[10],          // 삭제 여부
					rowIndex: i + 1        // 원본 행 번호
				};
				
				// 1depth~4depth 중 값이 있는 것을 찾아서 basePath 업데이트
				let newBasePath = [];
				let depthChanged = false;
				
				for(let d = 0; d < depths.length; d++) {
					if(depths[d] !== null) {
						if(!depthChanged) {
							// 처음 값이 나타나는 depth부터 basePath 갱신
							newBasePath = basePath.slice(0, d);
							depthChanged = true;
						}
						newBasePath.push(depths[d]);
					}
				}
				
				// basePath가 변경되면 currentScreen1Path도 업데이트
				if(depthChanged) {
					basePath = newBasePath;
					currentScreen1Path = [...basePath]; // 현재 depth 경로로 화면1 경로 초기화
				}
				
				// 최종 경로 구성
				let finalPath = [];
				
				if(화면1 !== null) {
					// 화면1이 있으면 basePath + 화면1
					finalPath = [...basePath, 화면1];
					currentScreen1Path = [...finalPath]; // 현재 화면1 경로 저장
					
					// 화면2도 있으면 추가
					if(화면2 !== null) {
						finalPath.push(화면2);
					}
				} else if(화면2 !== null) {
					// 화면1은 없고 화면2만 있는 경우 - 현재 화면1 경로에 추가
					finalPath = [...currentScreen1Path, 화면2];
				} else {
					// 둘 다 없으면 basePath만 사용
					finalPath = [...basePath];
				}
				
				// 경로가 없으면 다음 행으로
				if(finalPath.length === 0) continue;
				
				// 트리 구조 생성
				let currentLevel = tree;
				
				for(let d = 0; d < finalPath.length; d++) {
					const depthValue = finalPath[d];
					
					// 노드가 없으면 새로 생성
					if(!currentLevel[depthValue]) {
						currentLevel[depthValue] = {
							children: {},                        // 하위 노드들
							files: [],                          // 해당 노드의 파일들
							depth: d + 1,                       // 현재 depth 레벨 (1부터 시작)
							fullPath: finalPath.slice(0, d + 1) // 루트부터 현재까지의 전체 경로
						};
					}
					
					// 마지막 depth에 파일 정보 추가
					if(d === finalPath.length - 1) {
						currentLevel[depthValue].files.push(fileInfo);
					}
					
					// 다음 레벨로 이동
					currentLevel = currentLevel[depthValue].children;
				}
			}
			
			return tree;
		},

		searchSheets(sheetsData, searchTerm) {
			if (!searchTerm) return sheetsData;
			
			const filteredSheets = {};
			
			Object.entries(sheetsData).forEach(([sheetName, treeData]) => {
				// 시트명 검색
				const sheetMatches = sheetName.toLowerCase().includes(searchTerm.toLowerCase());
				
				// 트리 데이터 검색
				const filteredTreeData = this.searchTree(treeData, searchTerm);
				
				// 시트명이 매치되거나 트리에 매치되는 데이터가 있으면 포함
				if (sheetMatches || Object.keys(filteredTreeData).length > 0) {
					filteredSheets[sheetName] = sheetMatches ? treeData : filteredTreeData;
				}
			});
			
			return filteredSheets;
		},

		searchTree(treeData, searchTerm) {
			if (!searchTerm) return treeData;
			
			const filteredTree = {};
			
			const searchNode = (node, name) => {
				const nameMatches = name.toLowerCase().includes(searchTerm.toLowerCase());
				const fileMatches = (node.files || []).some(file => 
					(file.fileName || '').toLowerCase().includes(searchTerm.toLowerCase())
				);
				
				let hasMatchingChildren = false;
				const filteredChildren = {};
				
				Object.entries(node.children || {}).forEach(([childName, childData]) => {
					const childResult = searchNode(childData, childName);
					if (childResult) {
						filteredChildren[childName] = childResult;
						hasMatchingChildren = true;
					}
				});
				
				if (nameMatches || fileMatches || hasMatchingChildren) {
					return {
						...node,
						children: filteredChildren,
						files: fileMatches ? node.files : []
					};
				}
				
				return null;
			};
			
			Object.entries(treeData).forEach(([name, data]) => {
				const result = searchNode(data, name);
				if (result) {
					filteredTree[name] = result;
				}
			});
			
			return filteredTree;
		},
		onSearchInput() {
			this.searchTerm = this.searchInput.trim(); // Enter 시에만 업데이트
			this.filteredSheetData = this.searchSheets(this.originalSheetData, this.searchTerm);
		},
		closeModal() {
			this.$emit('close');
		},
		async initializeApp() {
			try {
				this.loading = true;
				this.error = null;
				
				this.originalSheetData = await this.loadExcelData();
				this.filteredSheetData = this.originalSheetData;
				
				console.log('Vue 트리 메뉴 로드 완료!', this.originalSheetData);
			} catch (error) {
				console.error('초기화 실패:', error);
				this.error = error.message;
			} finally {
				this.loading = false;
			}
		}
	},

	mounted() {
		this.initializeApp();
	}
};
</script>