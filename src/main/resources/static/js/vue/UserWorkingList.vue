<template>
	<div class="container" :class="{'mobile-container': isMobile}">
		
		<div class="content">
			<div class="content_inner">
	
				
				<ul class="view_list_wrap bor_top">
					<li>
						<div class="grey_list_type white none">
							<ul class="progress-container">
								<li class="progress-item">
									<dl class="progress-card">
										<dt class="progress-header">
											<p class="progress-title">
												<i class="icon">⚡</i>
												진행중인 작업
											</p>
										</dt>
										<dd class="progress-content">
											<div class="worker-list">
												<div
													v-for="(tasks, worker) in workerGroups" 
													:key="worker"
													class="worker-section"
												>
													<h3 
														class="worker-name"
														@click="toggleWorker(worker)"
                										:class="{ 'expanded': expandedWorkers[worker] }"
													>
														<div class="worker-name-wrap">
														<div class="worker-info">
															<i class="worker-icon" @click.stop="showWorkerImage(worker)"><img :src="`/static/img/character/${worker}.png`" alt=""></i>
															{{ worker }}
															<i class="chevron-icon" :class="{ 'rotated': expandedWorkers[worker] }">▼</i>
														</div>
														</div>
														<span class="task-count">{{ tasks.length }}개 작업</span>
													</h3>
													<ul class="task-list" :class="{ 'expanded': expandedWorkers[worker] }">
														<li 
															v-for="(task, index) in tasks"
															:key="index"
															class="task-item"
															:class="getTaskPriorityClass(task.workDays)"
														>
															<div class="task-info" @click="processWorkDetail(task.workingId)">
																<div class="task-title">{{ task.requestTitle }}</div>
																<div class="task-details">
																	<span class="work-days">
																		<i class="clock-icon">⏰</i>
																		작업중 {{ task.workDays }}일째
																	</span>
																	<span class="due-date">
																		<i class="calendar-icon">📅</i>
																		완료요청일: {{ formatDate(task.requestCompleteDate) }}
																	</span>
																</div>
															</div>
															<div class="progress-indicator">
																<div class="progress-bar">
																	<div 
																		class="progress-fill" 
																		:style="{ width: getProgressWidth(task) + '%' }"
																	></div>
																</div>
															</div>
														</li>
													</ul>
												</div>
											</div>
										</dd>
									</dl>
								</li>
							</ul>
						</div>
					</li>
				</ul>
			
			</div>
		</div>
	</div>
	<!--// container-->

	<!--start: 진행중인 작업 상세보기 팝업 -->
	<process-work-detail 
		v-if="showProcessWorkDetail"
		@show-detail="processWorkDetail"
		@close-detail="processWorkDetailClose"
		:opener="processWorkDetailCaller"
		:id="processWordDetailId"
		:key="processWordkDetailKey">
	</process-work-detail>
	<!--//end: 진행중인 작업 상세보기 팝업 -->

	<!-- 캐릭터 팝업 레이어 -->
	<div 
		v-if="selectedWorkerImage" 
		class="image-popup-overlay"
		@click="closeWorkerImage"
	>
		<div class="image-popup-content" @click.stop>
		<button class="close-button" @click="closeWorkerImage">✕</button>
		<div class="worker-image-container">
			<img 
			:src="`/static/img/character/${selectedWorkerImage}.png`" 
			:alt="selectedWorkerImage"
			class="worker-image-large"
			>
			<h3 class="worker-name-popup">{{ selectedWorkerImage }}</h3>
		</div>
		</div>
	</div>
</template>

<script>
// 진행중인 작업내역 상세보기 팝업 컴포넌트 로드
const ProcessWorkDetailComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/ProcessWorkDetailComponent.vue',vue3LoadOption);
});

export default {
	mixins: [channelMixin],
	components: {
		'process-work-detail': ProcessWorkDetailComponent, // 진행중인 작업 상세보기 팝업 컴포넌트
	},
	data() {
		return {
			workerGroups: {},
			showProcessWorkDetail: false,
			processWordDetailId: '',
			processWordkDetailKey: 0,
			processWorkDetailCaller: 'process',
			expandedWorkers: {},
			selectedWorkerImage: null,
		}
	},
	async mounted() {
		await this.read__loginInfo();

		try {
			const memberList = await axios.get(`${API_URL}/member/list.dat`, {params: {start: 0, limit: 30}});
			const passMember = ['양여현', '박석', '백희경', '이화영', '최아란'];

			memberList.data.forEach((user) => {
				if (!passMember.includes(user.username)) {
					this.workerGroups[user.username] = [];
				}
			});

		} catch(e) {
			console.log(e.message);
		}

		await this.getDbData();

		// 초기에 모든 작업자를 펼쳐놓기
		Object.keys(this.workerGroups).forEach(worker => {
			this.expandedWorkers[worker] = false;
		});
	},
	computed: {
	
	},
	methods: {
		...mapActions(useCommonStore, ['dateDiff']),
		toggleWorker(worker) {
			this.expandedWorkers[worker] = !this.expandedWorkers[worker];
		},
		showWorkerImage(worker) {
			this.selectedWorkerImage = worker;
		},
		closeWorkerImage() {
			this.selectedWorkerImage = null;
		},
		/**
		 * MongoDB에서 조회한 데이터를 가공하여 작업자별 목록 생성
		 * @param {Array} mongoData - MongoDB에서 조회한 데이터
		 * @returns {Object} 작업자별 그룹화된 데이터
		 */
		processWorkingData(mongoData) {
			const workerGroups = {...this.workerGroups};
			
			// 각 문서를 순회하면서 처리
			for (let i = 0; i < mongoData.length; i++) {
				const document = mongoData[i];

				// request_state가 "WORKING"인지 확인
				if (document.request_state !== "WORKING") {
					continue;
				}
				
				// part 배열에서 state가 "WORKING"인 것만 필터링
				const workingParts = [];
				if (document.part && Array.isArray(document.part)) {
					for (let j = 0; j < document.part.length; j++) {
						if (document.part[j].state === "WORKING") {
							workingParts.push(document.part[j]);
						}
					}
				}
				
				// 각 working part를 처리
				for (let k = 0; k < workingParts.length; k++) {
					const part = workingParts[k];
					const worker = part.worker || "지정안됨";
					
					// 작업 시작일 구하기 (work_content의 첫번째 인덱스의 part_work_sday)
					let workStartDate = null;
					if (part.work_content && Array.isArray(part.work_content) && part.work_content.length > 0) {
						workStartDate = part.work_content[0].part_work_sday;
					}
					
					// 작업자별 그룹 초기화
					if (!workerGroups[worker]) {
						workerGroups[worker] = [];
					}
					
					// 작업 정보 추가
					workerGroups[worker].push({
						requestTitle: document.request_title,
						requestCompleteDate: document.request_complete_date,
						workStartDate: workStartDate,
						partName: part.name,
						workDays: this.calculateWorkDays(workStartDate),
						workingId: document.id
					});
				}
			}

			return workerGroups;
		},
		/**
		 * 작업 시작일부터 현재까지의 일수 계산
		 * @param {Date|Object|String} startDate - 작업 시작일
		 * @returns {number} 작업 일수
		 */
		calculateWorkDays(startDate) {
			if (!startDate) return 0;
			
			let start;
			// MongoDB Date 객체 처리
			if (startDate && startDate.$date) {
				start = new Date(startDate.$date);
			} else if (typeof startDate === 'string') {
				start = new Date(startDate);
			} else {
				start = new Date(startDate);
			}
			
			const today = new Date();
			const diffTime = today - start;
			const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
			
			return diffDays > 0 ? diffDays : 1;
		},

		/**
		 * 날짜를 한국어 형식으로 포맷팅 (YY/MM/DD)
		 * @param {Date|Object|String} date - 포맷팅할 날짜
		 * @returns {string} 포맷된 날짜 문자열
		 */
		formatDate(dateString) {
			const date = new Date(dateString);

			return date.toLocaleDateString('ko-KR', {
				month: 'short',
				day: 'numeric'
			});
		},

		/**
		 * 메인 실행 함수
		 * @param {Array} mongoData - MongoDB에서 조회한 원본 데이터
		 */
		setWorkerList(mongoData) {
			// 1단계: 데이터 가공
			this.workerGroups = this.processWorkingData(mongoData);
			
			/*
			// 콘솔에도 출력 (디버깅용)
			console.log('=== 작업자별 현황 ===');
			const workers = Object.keys(this.workerGroups);
			for (let i = 0; i < workers.length; i++) {
				const worker = workers[i];
				const tasks = this.workerGroups[worker];
				
				console.log('\n' + worker);
				for (let j = 0; j < tasks.length; j++) {
					const task = tasks[j];
					const completeDate = this.formatDate(task.requestCompleteDate);
					console.log('(작업중 ' + task.workDays + '일째) : ' + task.requestTitle + ' (완료요청일: ~' + completeDate + ')');
				}
			}
			*/
		},

		async getDbData() {
			try {
				const response = await axios.post(`${API_URL}/work/user_working_list.dat`);

				this.setWorkerList(response.data);
			} catch (e) {
				console.log(e.message);
			}
		},
		getTaskPriorityClass(workDays) {
			if (workDays >= 7) return 'high-priority';
			if (workDays >= 4) return 'medium-priority';

			return 'low-priority';
		},
		getProgressWidth(task) {
			const startDate = new Date(task.workStartDate);
			const dueDate = new Date(task.requestCompleteDate);
			const today = new Date();
			
			const totalDays = (dueDate - startDate) / (1000 * 60 * 60 * 24);
			const passedDays = (today - startDate) / (1000 * 60 * 60 * 24);
			
			return Math.min((passedDays / totalDays) * 100, 100);
		},

		processWorkDetail: function (id, caller='process') {
			const eventBus = useEventBusStore();
			
			this.showProcessWorkDetail = true;
			// 진행중인 작업 상세보기할 아이디
			this.processWordDetailId = id;
			// 진행중인 작업 상세보기 컴포넌트 키 증가
			this.processWordkDetailKey += 1;
			// store workStore 실행
			
// 			this.$store.commit('workStore/setWorkDetailOpener', caller);
			this.processWorkDetailCaller = caller;
			
			eventBus.workDetailOpener = caller;
		},
		processWorkDetailClose: function () {
			this.showProcessWorkDetail = false;
		},
	}
}
</script>

<style scoped>
i {
	font-style: normal;
}
.container {
	margin-top: 0;
}

.progress-container {
  list-style: none;
  padding: 0;
  margin: 0;
  max-width: 1200px;
}

.progress-item {
  margin-bottom: 20px;
}

.progress-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  margin: 0;
}

.progress-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  margin: 0;
}

.progress-title {
  color: white;
  font-size: 1.4em;
  font-weight: 600;
  margin: 0;
  padding: 20px 25px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon {
  font-size: 1.2em;
}

.progress-content {
  padding: 25px;
  margin: 0;
}

.worker-list {
  display: grid;
  gap: 20px;
}

.worker-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.worker-section:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.worker-name {
  color: #2d3748;
  font-size: 1.2em;
  font-weight: 700;
  margin: 0 0 15px 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 2px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s ease;
  user-select: none;
}
.worker-name:hover {
  background: rgba(102, 126, 234, 0.05);
  border-radius: 8px;
  padding: 12px 16px;
  margin: 0 -16px 15px -16px;
}
.worker-name.expanded {
  border-bottom-color: #667eea;
}
.worker-name-wrap {
	flex: 1;
}
.worker-info {
  display: flex;
  align-items: center;
  gap: 8px; /* ← 추가됨 */
}

.chevron-icon {
  font-size: 0.7em; /* ← 0.8em에서 0.7em으로 변경 */
  color: #718096;
  transition: transform 0.3s ease;
  margin-left: 4px; /* ← 추가됨 */
}

.chevron-icon.rotated {
  transform: rotate(180deg);
}
.worker-icon {
	cursor: pointer;
	margin-right: 8px;
	width: 60px;
	height: 60px;
	border-radius: 50%;
	overflow: hidden;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #f7fafc;
	border: 2px solid #e2e8f0;
	transition: all 0.3s ease;
}
.worker-icon:hover {
  border-color: #667eea;
  transform: scale(1.05);
}

.worker-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  clip-path: circle(50%);
}
.task-count {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  font-size: 0.75em;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 500;
}

.task-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 12px;
  max-height: 0;
  overflow: hidden;
  opacity: 0;
  transition: all 0.4s ease;
  transform: translateY(-10px);
}

.task-list.expanded {
  	max-height: none;
  	opacity: 1;
  	transform: translateY(0);
	margin-top: 15px;
}

.task-item {
  background: #f8fafc;
  border-radius: 10px;
  padding: 16px;
  border-left: 4px solid #cbd5e0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.task-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.task-item.low-priority {
  border-left-color: #48bb78;
  background: linear-gradient(135deg, #f0fff4 0%, #f0fff4 100%);
}

.task-item.medium-priority {
  border-left-color: #ed8936;
  background: linear-gradient(135deg, #fffaf0 0%, #fffaf0 100%);
}

.task-item.high-priority {
  border-left-color: #f56565;
  background: linear-gradient(135deg, #fff5f5 0%, #fff5f5 100%);
}

.task-info {
  margin-bottom: 12px;
}

.task-title {
  font-size: 1em;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 8px;
  line-height: 1.4;
}

.task-details {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 0.85em;
  color: #718096;
}

.work-days, .due-date {
  display: flex;
  align-items: center;
  gap: 4px;
}

.clock-icon, .calendar-icon {
  font-size: 0.9em;
  font-style: normal;
}

.progress-indicator {
  margin-top: 12px;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #48bb78 0%, #38a169 50%, #2f855a 100%);
  border-radius: 3px;
  transition: width 0.8s ease-in-out;
  position: relative;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.4) 50%, transparent 100%);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

/* 이미지 팝업 스타일 */
.image-popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.3s ease;
}

.image-popup-content {
  position: relative;
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 90vw;
  max-height: 90vh;
  animation: popupSlideIn 0.3s ease;
}

.close-button {
  position: absolute;
  top: 15px;
  right: 20px;
  background: none;
  border: none;
  font-size: 24px;
  color: #718096;
  cursor: pointer;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.close-button:hover {
  background: #f7fafc;
  color: #2d3748;
  transform: rotate(90deg);
}

.worker-image-container {
  text-align: center;
}

.worker-image-large {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #667eea;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
  margin-bottom: 20px;
  transition: transform 0.3s ease;
}

.worker-image-large:hover {
  transform: scale(1.05);
}

.worker-name-popup {
  color: #2d3748;
  font-size: 1.5em;
  font-weight: 600;
  margin: 0;
  text-align: center;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes popupSlideIn {
	from { 
		opacity: 0;
		transform: scale(0.8) translateY(-20px);
	}
	to { 
		opacity: 1;
		transform: scale(1) translateY(0);
	}
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .progress-content {
    padding: 15px;
  }
  
  .worker-section {
    padding: 15px;
  }
  
  .task-details {
    flex-direction: column;
    gap: 8px;
  }
  
  .worker-name {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>