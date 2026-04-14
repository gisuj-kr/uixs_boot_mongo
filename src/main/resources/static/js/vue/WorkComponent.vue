<template>

	<div class="container" :class="{ 'mobile-container': isMobile }">

		<div class="content">
			<div class="content_inner">
				<div class="tit_area flex-betw">
					<h2 style="border-bottom: 0;">
						작업관리
						<input type="checkbox" name="listAllChannel" id="listAllChannel" v-model="isAllChecked"> <label
							for="listAllChannel">전체채널 조회</label>
					</h2>
					<div class="input_section right_type flex-end" :style="isMobile && 'width: 100%;'">
						<label for="workYear" v-if="!isMobile">작업 년/월</label>
						<select id="workYear" class="selectbox w120 ">
							<option :value="year" v-for="year in startYearOfWork">{{ year }}년</option>
						</select>
						<select id="workMonth" class="selectbox w120">
							<option :value="month" :selected="month == toMonth" v-for="month in 12">{{ month }}월</option>
						</select>

						<button type="button" class="btn btn_small03 excel-down" @click.prevent="excelDownload">월간작업
							다운로드</button>

					</div>
				</div>

				<ul class="view_list_wrap bor_top">
					<li>
						<div class="list">
							<div class="ch_tit">
								<strong class="tit-channel-type">{{ selectedChannel.name }}</strong>
								<!--<span class="bul_type01">개인</span>-->
								<button type="button" class="btn_small03 work work-inspection"
									@click="goWorkRequest">작업요청</button>
								<button type="button" class="btn_small03 work work-inspection users"
									@click="goUserWorkList">사용자 작업내역</button>
							</div>

							<div class="btn_edit_area">
								<ul class="input_wrap no-pd" :style="isMobile && 'width: 100%;max-width:none;'">
									<li class="input_area search_case"
										:style="isMobile && 'display: flex;justify-content:right;'">
										<label for="" v-if="!isMobile">검색</label>
										<select class="selectbox" id="search_list" v-model="searchKey"
											:style="isMobile && 'width: 30%'">
											<option value="ALL">전체</option>
											<option value="username">작성자</option>
											<option value="request_title">제목</option>
											<option value="request_content">내용</option>
											<option value="request_id">화면아이디</option>
										</select>
										<input type="text" class="search-string" placeholder="검색어를 입력하세요"
											v-model="searchWord" @keyup.enter="listSearch"
											:style="isMobile && 'width: 70%'">
										<button type="button" class="btn_search" @click.prevent="listSearch"></button>
									</li>
								</ul>
							</div>
						</div>
						<div class="grey_list_type white none">
							<ul class="work_list_wrap">
								<li class="work_inner" id="request_list">
									<dl v-show="requestListShow">
										<dt>
											<p>작업요청내역</p>
											<span>{{ totRequestCnt }}건</span>
										</dt>
										<dd>
											<div class="work_factor request" style="position: relative"
												v-for="item in requestList">
												<span class="new_sticker"
													v-if="dateDiff(new Date(), item.regdate) >= -2">
												</span>
												<dl>
													<dt>
														<div class="bid">ID {{ item.site_name != null ?
															'[' + item.site_name+']' : ''}} {{ item.request_id }}
															<a href="#none" v-if="isAdmin" class="request_work_del"
																:data-reuqest-id="item.request_id"
																:data-state="item.request_state"
																@click="requestWorkDelete(item.id)"></a>
														</div>
														<a href="#none"
															@click="requestWorkDetail(item.id)">{{ item.request_title }}</a>
													</dt>
													<dd>
														<div class="work_area"
															v-html="getReplaceContent(item.request_content)">
														</div>
														<div class="state_step_area">
															<span class="step01">
																{{
																	item.request_state == 'PENDING' ?
																		'보류' :
																		('CANCEL' ? '보류' : item.request_state)
																}}
															</span>
														</div>
													</dd>
												</dl>
											</div>
										</dd>
									</dl>
									<div class="btn_more_area" v-if="requestList.length >= limit">
										<a href="#none" class="btn-more-view" @click="getRequestListMore">더보기</a>
									</div>
								</li> <!-- // 미확인 게시물 목록 -->
								<!-- 									<li class="work_inner" id="request_list"></li> -->
								<li class="work_inner" id="process_list">
									<process-work-list v-if="processList.length > 0" :prop-list="processList"
										:list-all-channel="listAllChannel" @show-detail="processWorkDetail"
										:key="processListKey" :search-key="searchKey" :search-word="searchWord">
									</process-work-list>

									<div class="btn_more_area" v-if="processList.length >= limit">
										<a href="#none" class="btn-more-view" @click="getProcessListMore">더보기</a>
									</div>
								</li>
								<li class="work_inner">
									<complete-work-list v-if="completeList.length > 0" :prop-list="completeList"
										:list-all-channel="listAllChannel" @show-detail="processWorkDetail"
										:key="completeKey" :search-key="searchKey" :search-word="searchWord">
									</complete-work-list>

									<div class="btn_more_area" v-if="completeList.length >= limit">
										<a href="#none" class="btn-more-view" @click="getCompleteListMore">더보기</a>
									</div>
								</li>
							</ul>
						</div>
					</li>
				</ul>

			</div>
		</div>
	</div>
	<!--// container-->

	<!-- 요청작업 상세보기 팝업 -->
	<request-work-detail :id="requestWorkDetailId" :key="requestWorkDetailKey" v-if="requestWorkDetailId"
		@close="requestWorkDetailClose">
	</request-work-detail>


	<!--start: 진행중인 작업 상세보기 팝업 -->
	<process-work-detail v-if="showProcessWorkDetail" @show-detail="processWorkDetail"
		@close-detail="processWorkDetailClose" :opener="processWorkDetailCaller" :id="processWordDetailId"
		:key="processWordkDetailKey">
	</process-work-detail>
	<!--//end: 진행중인 작업 상세보기 팝업 -->

</template>

<script>
// 작업요청 상세보기 팝업 컴포넌트 로드
const RequestWorkDetailComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/RequestWorkDetailComponent.vue', vue3LoadOption);
});
// 진행중인 작업내역 컴포넌트 로드
const ProcessWorkListComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/ProcessWorkListComponent.vue', vue3LoadOption);
});
// 진행중인 작업내역 상세보기 팝업 컴포넌트 로드
const ProcessWorkDetailComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/ProcessWorkDetailComponent.vue', vue3LoadOption);
});
// 완료작업 목록 컴포넌트
const CompleteWorkListComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/CompleteWorkListComponent.vue', vue3LoadOption);
});

export default {
	mixins: [channelMixin],
	components: {
		'request-work-detail': RequestWorkDetailComponent, // 요청작업 상세보기 팝업 컴포넌트
		'process-work-list': ProcessWorkListComponent, // 진행중인작업 목록 컴포넌트
		'process-work-detail': ProcessWorkDetailComponent, // 진행중인 작업 상세보기 팝업 컴포넌트
		'complete-work-list': CompleteWorkListComponent,
	},
	data: {
		requestList: [], // 미확인 게시물 목록
		processList: [], // 확인한 게시물 목록
		completeList: [], // 완료 목록
		processListKey: 0,
		completeKey: 0, // 완료목록 갱신 키
		confirmCount: {
			REQUEST_CNT: 0,
			COMPLETE_CNT: 0
		},
		requestListShow: false,
		searchKey: 'ALL', // 검색 필터
		searchWord: '', // 검색어
		searched: false, // 검색했는지 안했는지
		page: {
			requestWork: 1,
			processWork: 1,
			completeWork: 1
		},
		start: {
			requestWork: 0,
			processWork: 0,
			completeWork: 0
		},
		limit: 10,
		showProcessWorkDetail: false,
		processWordDetailId: '',
		processWordkDetailKey: 0,
		processWorkDetailCaller: 'process',
		requestWorkDetailId: '', // 작업요청 아이디
		requestWorkDetailKey: 0, // 작업요청 상세보기 팝업 고유키
		totRequestCnt: 0,
		isAllChecked: true, // 전체채널 조회 체크박스 model 연결
		loginStore: null,
		isAdmin: false, // 관리자 체크
		// listAllChannel: true,
	},
	mounted: async function () {

		await this.read__loginInfo();

		this.loginStore = useLoginInfoStore();

		this.loadList();

		this.isAdmin = await this.$checkAuth();

		const eventBus = useEventBusStore();

		eventBus.addEvent('setConfirmCount', this.setConfirmCount);
		eventBus.addEvent('loadList', this.loadList);
	},
	provide() {
		return {
			requestWorkDelete: this.requestWorkDelete
		}
	},
	computed: {
		listAllChannel() {
			const channelStore = useChannelStore();

			console.log(channelStore.selectedHeaderChannel, 'listAllChannel')
			//if (isAllChecked) channelStore.selectedHeaderChannel = 'all'; 

			return channelStore.selectedHeaderChannel;
		},
		lastDayOfMonth: function () {
			const date = new Date();
			const year = date.getFullYear();
			const month = date.getMonth() + 1;

			return new Date(year, month, 0).getDate();
		},
		startYearOfWork: function () {
			const date = new Date();
			var year = date.getFullYear();

			const workTerm = year - 2024;
			const minYear = year - workTerm;

			const yearList = [];

			do {
				yearList.push(year);

				year = year - 1
			} while (minYear <= year);

			return yearList;
		},
		toMonth: function () {
			return new Date().format('MM');
		},
	},
	watch: {
		requestList: {
			handler: function () {
				this.requestListShow = true;
			},
			deep: false
		},
		processList: {
			handler: function (newVal, oldVal) {
				this.processListKey += 1;
				// 검수요청, 완료 카운팅
				// this.setConfirmCount();
			},
			deep: false
		},
		completeList: {
			handler: function () {
				this.completeKey += 1;
			},
			deep: false
		},
		isAllChecked: {
			handler: function (newVal, oldVal) {
				console.log(newVal, 'isAllChecked')
				if (newVal) {
					const channelStore = useChannelStore();

					channelStore.selectedHeaderChannel = 'all';
				}
			},
			deep: false
		},
		listAllChannel: {
			handler: function (newVal, oldVal) {
				if (newVal !== 'all') {
					this.isAllChecked = false;
				} else {
					this.isAllChecked = true;
				}
				console.log(this.listAllChannel, 'listAllChannel 채널 감시중')
				this.allChannelWork();
			},
			deep: false
		}
	},
	methods: {
		...mapActions(useCommonStore, ['dateDiff']),
		excelDownload: async function () {
			// ArrayBuffer 만들어주는 함수
			function s2ab(s) {
				var buf = new ArrayBuffer(s.length); //convert s to arrayBuffer
				var view = new Uint8Array(buf);  //create uint8array as viewer
				for (var i = 0; i < s.length; i++) view[i] = s.charCodeAt(i) & 0xFF; //convert to octet
				return buf;
			}

			try {
				let workYear = document.getElementById('workYear').value;
				let workMonth = document.getElementById('workMonth').value;

				if (parseInt(workMonth) < 10) workMonth = '0' + workMonth;

				const lastDayOfMonth = new Date(workYear, workMonth, 0).getDate();

				const startDate = workYear + "-" + workMonth + "-01";
				const endDate = workYear + "-" + workMonth + "-" + lastDayOfMonth;

				const res = await axios.get(API_URL + '/work/month_work_list.dat?startDate=' + startDate + '&endDate=' + endDate);
				const data = res.data;
				const statePercent = {
					PENDING: 0,
					WORKING: 30,
					CONFIRM: 50,
					EDIT: 80,
					COMPLETE: 100
				};
				const partToKr = {
					plan: '기획',
					design: '디자인',
					publish: '퍼블'
				};
                const stName = { 'PENDING': '대기', 'WORKING': '작업중', 'CONFIRM_REQUEST': '컨펌요청', 'CONFIRM_COMPLETE': '컨펌완료' };
                const dOrEmpty = (val) => val ? val : "";

				let renew_data = [
					[
						'채널',
						'업무내용',
						'요청자',
						'요청일',
						'완료요청일',
						'과업',
						'담당자',
						'작업현황',
						'착수일',
						'실제 종료일',
						'진척률',
						'비고'
					]
				];

			data.forEach((item) => {
				item.part.forEach(part => {

					// 날짜 문자열에서 'YYYYMM' 형식으로 연월을 추출하는 헬퍼 함수
					const getNumYM = (dateStr) => {
						if (!dateStr) return "";
						const match = dateStr.match(/(\d{4})[.-](\d{2})/);
						return match ? match[1] + match[2] : "";
					};

					// workMonth는 335라인에서 이미 '04' 형태 문자열로 변환됨
					// String으로 그대로 합쳐서 '202604' 형태의 비교 키 생성
					const targetYM = String(workYear) + String(workMonth); // 예: '202604'

					// [핵심 필터링 조건]
					// 착수일(part_work_sday) 또는 실제 종료일(part_work_eday)이
					// 선택한 월에 속하는 work_content 행만 엑셀에 포함합니다.
					// work_content가 없는 항목(착수일/종료일 미입력)은 제외합니다.
					if (part.work_content != null && part.work_content.length > 0) {
						part.work_content.reverse().forEach((work) => {
							const sYM = getNumYM(work.part_work_sday);
							const eYM = getNumYM(work.part_work_eday);

							// 착수일 또는 실제 종료일 중 하나라도 선택 월과 일치하면 포함
							if (sYM === targetYM || eYM === targetYM) {
								renew_data.push([
									item.site_name,
									item.request_title,
									item.requestor_name,
									dOrEmpty(part.part_work_rday),
									dOrEmpty(part.part_work_crday),
									partToKr[part.name] || part.name,
									part.worker || '지정안됨',
									work.content || '',
									dOrEmpty(work.part_work_sday),
									dOrEmpty(work.part_work_eday),
									(statePercent[part.state] || 0) + '%',
									part.bigo || ''
								]);
							}
						});
					}
					// work_content가 없는 항목은 착수일/종료일이 없으므로 엑셀에서 제외

				});
			});

				// workbook 생성
				const wb = XLSX.utils.book_new();

				// sheet명 생성
				wb.SheetNames.push("sheet 1");
				// wb.SheetNames.push("sheet 2"); // 시트가 여러개인 경우

				// 이중 배열 형태로 데이터가 들어간다.
				// 			    let wsData = renew_data;
				// var wsData2 = [['가1' , '가2', '가3'],['나1','나2','나3']];	// 시트가 여러개인 경우

				// 배열 데이터로 시트 데이터 생성
				const ws = XLSX.utils.aoa_to_sheet(renew_data);
				// var ws2 = XLSX.utils.aoa_to_sheet(wsData2); 	//시트가 여러개인 경우

				// 시트 데이터를 시트에 넣기 ( 시트 명이 없는 시트인경우 첫번째 시트에 데이터가 들어감 )
				wb.Sheets["sheet 1"] = ws;
				// wb.Sheets["sheet 2"] = ws2;	//시트가 여러개인 경우

				// 엑셀 파일 쓰기
				const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'binary' });

				// 파일 다운로드
				saveAs(new Blob([s2ab(wbout)], { type: "application/octet-stream" }), workYear + "년" + workMonth + '월_월간작업내역.xlsx');

			}
			catch (error) {
				console.log(error);
			}

		},
		onMessage: function (evt) {
			var data = evt.data;

			// 메세지 있음 표시
			this.$store.commit('setHasMessage', true);

			$('.message-alam').remove();

			var toastWrapper = $('<div>', {
				css: {
					position: 'fixed'
					, left: 0
					, top: 0
					, height: 50
					, width: '100%'
					, zIndex: 99999
					, background: 'rgba(0, 0, 0, 0.7)'
					, textAlign: 'center'
					, color: '#fff'
					, lineHeight: '50px'
				}
				, class: 'message-alam'
			})
				.html(data)
				.on('click', function () {
					toastWrapper.remove();
				})
				.appendTo('body');

			//uijs.setMsgCnt();
		},
		allChannelWork: function () {
			console.log('allChannelWork 실행');
			// 전체 리스트 조회
			this.loadList();

			// 검수요청수 다시 조회
			this.setConfirmCount();
		},
		// 목록 조회 통합
		loadList: async function (target = null) {
			console.log('loadList 실행');
			this.loading('start');

			var get = { request: false, process: false, complete: false };

			if (target == null) {
				get = { request: true, process: true, complete: true };
			}
			else {
				target.forEach(function (item) {
					get[item] = true;
				});
			}

			// 요청된 작업목록
			try {
				if (!get.request) {
					console.warn('🚩요청된 작업목록은 조회 항목에서 제외되었습니다.');
				} else {
					const response = await this.getRequestWorkList()
					this.requestList = response.data;

					// 요청된 작업 갯수
					const selectChannelCode = this.listAllChannel !== 'all' ? localStorage.channel : null;
					axios.post(API_URL + '/work/working_cnt.dat', {
						site_code: selectChannelCode,
						request_state: 'PENDING',
						search_key: (this.searched !== undefined && this.searched) ? this.searchKey : null,
						search_word: (this.searched !== undefined && this.searched) ? this.searchWord : null
					})
						.then(response => {
							this.totRequestCnt = response.data;
						}).catch(error => { console.log(error) });
				}
			} catch (error) {
				console.log(error);
				this.loading('stop');
			}

			// 진행중인 작업 목록
			try {
				if (!get.process) {
					console.warn('🚩진행중인 작업 목록은 조회 항목에서 제외되었습니다.');
				} else {
					const response = await this.getProcessWorkList();
					this.processList = response.data;
				}
				// 감수요청, 완료 카운팅
				// this.setConfirmCount();
			} catch (error) {
				console.log(error);
			}

			// 완료된 작업 조회
			try {
				if (!get.complete) {
					console.warn('🚩완료된 작업 목록은 조회 항목에서 제외되었습니다.');
				} else {
					const response = await this.getCompleteWorkList();
					this.completeList = response.data;
				}
			} catch (error) {
				console.log(error);
			}

			this.loading('stop');
		},
		// 진행중인 작업내역 조회
		getProcessWorkList: function () {
			var sendParam = {
				site_code: this.listAllChannel !== 'all' ? this.getChannelCode : null,
				start: this.start.processWork,
				limit: this.limit
			};

			if (this.searched !== undefined && this.searched) {
				sendParam.search_key = this.searchKey;
				sendParam.search_word = this.searchWord;
			};

			return axios.get(API_URL + "/work/work_ing_list.dat", { params: sendParam });
		},
		// 요청작업 목록
		getRequestWorkList: function () {
			var sendParam = {
				site_code: this.listAllChannel !== 'all' ? this.getChannelCode : null,
				start: this.start.requestWork,
				limit: this.limit
			};

			if (this.searched !== undefined && this.searched) {
				sendParam.search_key = this.searchKey;
				sendParam.search_word = this.searchWord;
			};

			return axios.post(API_URL + "/work/request_list.dat", sendParam);
		},
		// 작업완료 목록 조회
		getCompleteWorkList: function () {
			var sendParam = {
				site_code: this.listAllChannel !== 'all' ? this.getChannelCode : null,
				start: this.start.completeWork,
				limit: this.limit,
				orderkey: 'regdate',
				listsort: 'd',
			};

			if (this.searched !== undefined && this.searched) {
				sendParam.search_key = this.searchKey;
				sendParam.search_word = this.searchWord;
			};

			return axios.get(API_URL + '/work/complete_list.dat', { params: sendParam });
		},
		listSearch: function (e) {
			this.searched = true;

			// page 초기화
			for (var page in this.page) {
				this.page[page] = 1;
			}

			// start 초기화
			for (var start in this.start) {
				this.start[start] = 0;
			}

			this.loadList();
		},
		// 진행중인 작업 목록 더보기
		getProcessListMore: async function () {
			this.page.processWork += 1;
			this.start.processWork = (this.page.processWork - 1) * this.limit;

			this.loading('start');
			try {
				const response = await this.getProcessWorkList();

				if (!response.data.length) {
					alert('더이상 목록이 없습니다.');
					this.page.processWork -= 1;
				}
				else {
					this.processList.push(...response.data);
				}

			} catch (error) {
				console.log(error);
				this.loading('stop');
			}

			this.loading('stop');
		},
		// 요청 작업 목록 더보기
		getRequestListMore: async function () {
			this.page.requestWork += 1;
			this.start.requestWork = (this.page.requestWork - 1) * this.limit;

			this.loading('start');

			try {
				const response = await this.getRequestWorkList();

				if (!response.data.length) {
					alert('더이상 목록이 없습니다.');
					this.page.requestWork -= 1;
				}
				else {
					this.requestList.push(...response.data);
				}
			} catch (error) {
				console.log(error);
				this.loading('stop');
			}

			this.loading('stop');
		},
		// 완료된 작업목록 더보기
		getCompleteListMore: async function () {
			this.page.completeWork += 1;
			this.start.completeWork = (this.page.completeWork - 1) * this.limit;

			this.loading('start');

			try {
				const response = await this.getCompleteWorkList();

				if (!response.data.length) {
					alert('더이상 목록이 없습니다.');
					this.page.completeWork -= 1;
				}
				else {
					this.completeList.push(...response.data);
				}

			} catch (error) {
				console.log(error);
				this.loading('stop');
			}
			// await this.getCompleteWorkList(this.listAllChannel).then(response => {
			// 	if (!response.data.length) {
			// 		alert('더이상 목록이 없습니다.');
			// 		this.page.completeWork -= 1;
			// 	}
			// 	else {
			// 		this.completeList.push(...response.data);	
			// 	}
			// });
			this.loading('stop');
		},
		processWorkDetail: function (id, caller = 'process') {
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
		requestWorkDetailClose: function () {
			this.requestWorkDetailId = null;
		},
		// 요청작업 상세보기
		requestWorkDetail: function (id) {
			console.log(id);
			// 			if (this.isMobile) {
			// 				alert('PC버전 에서 확인해 주세요.');
			// 				return;
			// 			}
			this.requestWorkDetailId = id;
		},
		// 요청작업 삭제
		requestWorkDelete: async function (id) {
			if (!this.isAdmin) {
				alert('삭제 권한이 없습니다.');
				return false;
			}

			if (confirm('게시글을 삭제 하시겠습니까?')) {
				var data = { 'id': id }

				axios.post(API_URL + '/work/request_delete.dat', data)
					.then(reponse => {
						// 요청 목록과 진행중 목록 모두 새로고침
						this.loadList(['request', 'process']);
						// 상세보기 모달 닫기
						this.showProcessWorkDetail = false;
					})
					.catch(error => {
						console.log(error);
					});
			}
		},
		// 작업 컨펌 요청 갯수
		setConfirmCount: function () {
			var params = {
				site_code: this.listAllChannel !== 'all' ? localStorage.channel : null
			}

			console.log(params, 'setConfirmCount');

			axios.get(API_URL + '/work/confirm_cnt.dat', { params: params })
				.then(response => {
					this.confirmCount.REQUEST_CNT = response.data;
				})
				.catch(error => {
					console.log(error);
				});
		},
		getReplaceContent: function (str) {
			return str.replace(/\r\n/g, '<br>');
		},
		goWorkRequest() {
			this.$router.push('/work/request');
			// location.href = '/work/request';
		},
		goUserWorkList() {
			this.$router.push('/work/users');
		}
	}
};
// .component('header-component', HeaderComponent)
// .use(pinia)
// .mount('#v-app');
</script>
