<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../include/html_head.jsp" %>
</head>
<body>
<div class="wrapper" id="v-app" v-cloak>
<%-- 	<%@ include file="../include/header.jsp" %> --%>
	<header-component></header-component>
	<div class="container" :class="{'mobile-container': isMobile}">
		
		<div class="content">
			<div class="content_inner">
				<div class="tit_area flex-betw" >
					<h2 style="border-bottom: 0;">
                       	작업관리
                       	<input 
                       		type="checkbox" 
                       		name="listAllChannel" 
                       		id="listAllChannel" 
                       		v-model="listAllChannel"> <label for="listAllChannel">전체채널 조회</label>
                    </h2>
					<div class="input_section right_type flex-end" :style="isMobile && 'width: 100%;'">
						<label for="workYear" v-if="!isMobile">작업 년/월</label> 
						<select id="workYear" class="selectbox w120 ">
							<option :value="year" v-for="year in startYearOfWork">{{year}}년</option>
						</select>
						<select id="workMonth" class="selectbox w120">
							<option 
								:value="month" 
								:selected="month == toMonth"
								v-for="month in 12">{{month}}월</option>
						</select>
						
						<button type="button" class="btn btn_small03 excel-down" @click.prevent="excelDownload">월간작업 다운로드</button>
						
					</div>
                </div>
				
				<ul class="view_list_wrap bor_top">
					<li>
						<div class="list">
							<div class="ch_tit">
								<strong class="tit-channel-type">{{selectedChannel.name}}</strong> <!--<span class="bul_type01">개인</span>-->
								<button type="button" class="btn_small03 work work-inspection" @click="goWorkRequest">작업요청</button>
							</div>
							
							<div class="btn_edit_area">
								<ul class="input_wrap no-pd" :style="isMobile && 'width: 100%;max-width:none;'">
									<li class="input_area search_case" :style="isMobile && 'display: flex;justify-content:right;'">
										<label for="" v-if="!isMobile">검색</label>
										<select class="selectbox" id="search_list" v-model="searchKey" :style="isMobile && 'width: 30%'">
											<option value="ALL">전체</option>
											<option value="username">작성자</option>
											<option value="request_title">제목</option>
											<option value="request_content">내용</option>
											<option value="request_id">화면아이디</option>
										</select>
										<input type="text" class="search-string" placeholder="검색어를 입력하세요" v-model="searchWord" @keyup.enter="listSearch" :style="isMobile && 'width: 70%'">
										<button type="button" class="btn_search" @click.prevent="listSearch"></button>
									</li>
								</ul>
							</div>
						</div>
						<div class="grey_list_type white none">
							<ul class="work_list_wrap">
								<li class="work_inner" id="request_list">
									<dl  v-show="requestListShow">
										<dt><p>작업요청내역</p>
											<span>{{totRequestCnt}}건</span>
										</dt>
										<dd>
											<div class="work_factor request" style="position: relative" v-for="item in requestList">
												<span class="new_sticker" 
													v-if="dateDiff(new Date(), item.regdate) >= -2">
												</span>
												<dl>
													<dt>
														<div class="bid">ID {{item.site_name != null ? '['+item.site_name+']' : ''}} {{item.request_id}} 
															<a href="#none" 
																v-if="loginInfo.userid == item.userid || loginInfo.userid == 'admin'"
																class="request_work_del" 
																:data-reuqest-id="item.request_id" 
																:data-state="item.request_state"
																@click="requestWorkDelete(item.id)"></a>
														</div>
														<a href="#none" @click="requestWorkDetail(item.id)">{{item.request_title}}</a>
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
								<li class="work_inner" id="request_list" >
									<process-work-list 
										:prop-list="processList" 
										:list-all-channel="listAllChannel"
										@show-detail="processWorkDetail" 
										:key="processListKey">
									</process-work-list>
										
									<div class="btn_more_area" v-if="processList.length >= limit">
										<a href="#none" class="btn-more-view" @click="getProcessListMore">더보기</a>
									</div>
								</li>
								<li class="work_inner">
									<complete-work-list 
										:prop-list="completeList" 
										:list-all-channel="listAllChannel"
										@show-detail="processWorkDetail"
										:key="completeKey">
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
	<request-work-detail 
		:id="requestWorkDetailId" 
		:key="requestWorkDetailKey" 
		v-if="requestWorkDetailId"
		@close="requestWorkDetailClose">
	</request-work-detail>
	
	
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
	
</div>
</body>
</html>

<%-- <script type="text/javascript" src="/static/js/src/PartStatePopupComponent.js?v=${datetime}"></script> --%>
<%-- <script type="text/javascript" src="/static/js/src/RequestWorkComponent.js?v=${datetime}"></script> --%>
<%-- <script type="text/javascript" src="/static/js/src/ProcessWorkComponent.js?v=${datetime}"></script> --%>
<%-- <script type="text/javascript" src="/static/js/src/CompleteWorkComponent.js?v=${datetime}"></script> --%>
<script src="/static/js/xlsx.full.min.js"></script>
<script src="/static/js/FileSaver.min.js"></script>

<script>
// vue 2.0 의 EventBus 대체용 store
const useEventBusStore = defineStore('eventBus', {
	state: () => {
		return {
			isMobile: false,
			workDetailOpener: null,
			event: {}, // 함수실행
		}
	},
	actions: {
		addEvent(fnName, fn) {
			if (typeof fn === 'function') {
				this.event[fnName] = fn;
// 				fn.apply();
			} else {
				console.warn('전달된 인자값이 함수가 아닙니다.');
			}
		},
	}
});
// 작업요청 상세보기 팝업 컴포넌트 로드
const RequestWorkDetailComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/RequestWorkDetailComponent.vue',vue3LoadOption);
});
// 진행중인 작업내역 컴포넌트 로드
const ProcessWorkListComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/ProcessWorkListComponent.vue',vue3LoadOption);
});
// 진행중인 작업내역 상세보기 팝업 컴포넌트 로드
const ProcessWorkDetailComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/ProcessWorkDetailComponent.vue',vue3LoadOption);
});
// 완료작업 목록 컴포넌트
const CompleteWorkListComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/CompleteWorkListComponent.vue', vue3LoadOption);
});

const vapp = createApp({
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
		processListKey: 0,
		completeList: [], // 완료 목록
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
		listAllChannel: true,
	},
	created: function () {
		(async () => {
			await this.app__init();
		})();
		
		const eventBus = useEventBusStore();
		
		eventBus.addEvent('setConfirmCount', this.setConfirmCount);
		eventBus.addEvent('loadList', this.loadList);
	},
	mounted: function () {
// 		this.$socket.onmessage = this.onMessage;
		axios.post('/work/working_cnt.dat', {site_code: localStorage.channel, request_state: 'PENDING'})
		.then(response => {
			this.totRequestCnt = response.data;
		}).catch(error => {console.log(error)});
		
// 		const eventBus = useEventBusStore();
// 		eventBus.isMobile = this.isMobile;
	},
	computed: {
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
		}
	},
	watch: {
		channels: {
			handler: function() {
				this.loadList();
			},
			deep: true
		},
		requestList: {
			handler: function () {
				this.requestListShow = true;
			},
			deep: false
		},
		processList: {
			handler: function () {
				this.processListKey += 1;
				// 감수요청, 완료 카운팅
				this.setConfirmCount();
			},
			deep: true
		},
		completeList: {
			handler: function () {
				this.completeKey += 1;
			},
			deep: true
		},
		listAllChannel: {
			handler: function(e) {
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
		        for (var i=0; i<s.length; i++) view[i] = s.charCodeAt(i) & 0xFF; //convert to octet
		        return buf;
		    }
			
			try {
				let workYear = document.getElementById('workYear').value;
				let workMonth = document.getElementById('workMonth').value;
				
				if (parseInt(workMonth) < 10) workMonth = '0'+workMonth;
				
				const lastDayOfMonth = new Date(workYear, workMonth, 0).getDate();
				
				const startDate = workYear+"-"+workMonth+"-01";
				const endDate = workYear+"-"+workMonth+"-"+lastDayOfMonth;
				
					
				const res = await axios.get('/work/month_work_list.dat?startDate='+startDate+'&endDate='+endDate);
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
				
				console.log(data)
				
				data.forEach((item) => {
					
					let row = [];
					
					row.push(item.site_name);
					row.push(item.request_title);
					row.push(item.requestor_name);
					
// 					item.part.sort()
					let planPart = item.part.filter((part) => part.name == 'plan');
					let designPart = item.part.filter((part) => part.name == 'design');
					let publishPart = item.part.filter((part) => part.name == 'publish');
					
					item.part = [...planPart, ...designPart, ...publishPart];

					item.part.forEach(part => {
						let newRow = [
							...row, 
							dateOrEmpty(part.part_work_rday), // 작업 요청일
							dateOrEmpty(part.part_work_crday), // 완료 요청일
							partToKr[part.name],
							part.worker
// 							statePercent[part.state] + '%'
						];
						
						if (part.work_content != null && part.work_content.length > 0) {
							part.work_content.reverse().forEach((work) => {
								//if (work.part_work_sday != null) {
									let rsRow = [
										...newRow,
										work.content,
										dateOrEmpty(work.part_work_sday),
										dateOrEmpty(work.part_work_eday),
										statePercent[part.state] + '%',
										part.bigo
									];	
									
									renew_data.push(rsRow);
								//}
							});
						}
						else {
							let rsRow = [
								...newRow,
								'',
								'',
								'',
								statePercent[part.state] + '%',
								part.bigo
							];	
							
							renew_data.push(rsRow);
						}
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
			    const wbout = XLSX.write(wb, {bookType:'xlsx',  type: 'binary'});

			    // 파일 다운로드
			    saveAs(new Blob([s2ab(wbout)],{type:"application/octet-stream"}), workYear+"년"+workMonth+'월_월간작업내역.xlsx');
			  
			}
			catch(error) {
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
			// 전체 리스트 조회
			this.loadList();
			
			// 검수요청수 다시 조회
			this.setConfirmCount();
		},
		// 목록 조회 통합
		loadList: async function(target = null) {
			this.loading('start');
			
			var get = {request: false, process: false, complete: false};
			
			if (target == null) {
				get = {request: true, process: true, complete: true};
			}
			else {
				target.forEach(function (item) {
					get[item] = true;
				});
			}
			
			// 요청된 작업목록
			await this.getRequestWorkList(get.request, this.listAllChannel)
			.then(response => {
				this.requestList = response.data;
				
				this.loading('stop');
			})
			.catch(error => {
				console.log(error);
				this.loading('stop');
			});
			
			// 진행중인 작업 목록
			await this.getProcessWorkList(get.process, this.listAllChannel)
			.then(response => {
				this.processList = response.data;
			})
			.catch(error => {
				console.log(error);
				this.loading('stop');
			});
			
			// 완료된 작업 조회
			await this.getCompleteWorkList(get.complete, this.listAllChannel)
			.then(response => {
				this.completeList = response.data;
			})
			.catch(error => {
				console.log(error);
				this.loading('stop');
			});
		},
		// 진행중인 작업내역 조회
		getProcessWorkList: function (listAllChannel = false) {
            var app = this;
            
            var sendParam = {
   				site_code: !listAllChannel ? this.getChannelCode : null,
   				start: this.start.processWork,
   				limit: this.limit
   			};
            
   			if (this.searched) {
   				sendParam.search_key = this.searchKey;
   				sendParam.search_word = this.searchWord;
   			};
   			
   			console.log(sendParam);
   			
    		return axios.get("/work/work_ing_list.dat", {params: sendParam});
		},
		// 요청작업 목록
		getRequestWorkList: function (listAllChannel = false) {
			var sendParam = {
				site_code: !listAllChannel ? this.getChannelCode : null,
				start: this.start.requestWork,
				limit: this.limit
			};
			
			if (this.searched) {
				sendParam.search_key = this.searchKey;
				sendParam.search_word = this.searchWord;
			};
			
			return axios.post("/work/request_list.dat", sendParam, {
					params: {
						start: this.start.requestWork, 
						limit: this.limit
					}
				});
		},
		// 작업완료 목록 조회
		getCompleteWorkList: function (listAllChannel = false) {
			var sendParam = {
   				site_code: !listAllChannel ? this.getChannelCode : null,
   				start: this.start.completeWork,
   				limit: this.limit,
   				orderkey: 'regdate',
				listsort: 'd',
   			};
			
   			if (this.searched) {
   				sendParam.search_key = this.searchKey;
   				sendParam.search_word = this.searchWord;
   			};
   			
   			return axios.get('/work/complete_list.dat', {params: sendParam});
		},
		listSearch: function (e) {
// 			if (this.searchKey === 'ALL') {
// 				this.searched = false;
				
// 				this.searchWord = '';
// 			}
// 			else {
// 				this.searched = true;
// 			}

			this.searched = true;
			
			// page 초기화
			for(var page in this.page) {
				this.page[page] = 1;
			}
			
			// start 초기화
			for(var start in this.start) {
				this.start[start] = 0;
			}
			
			this.loadList();
		},
		// 진행중인 작업 목록 더보기
		getProcessListMore: async function () {
			this.page.processWork += 1;
			this.start.processWork = (this.page.processWork - 1) * this.limit;
			
			this.loading('start');
			
			await this.getProcessWorkList(this.listAllChannel).then((response) => {
				if (!response.data.length) {
					alert('더이상 목록이 없습니다.');
					this.page.processWork -= 1;
				}
				else {
					this.processList.push(...response.data);	
				}
			});
			
			this.loading('stop');
		},
		// 요청 작업 목록 더보기
		getRequestListMore: async function () {
			this.page.requestWork += 1;
			this.start.requestWork = (this.page.requestWork - 1) * this.limit;
			
			this.loading('start');
			
			await this.getRequestWorkList(this.listAllChannel).then(response => {
				if (!response.data.length) {
					alert('더이상 목록이 없습니다.');
					this.page.requestWork -= 1;
				}
				else {
					this.requestList.push(...response.data);	
				}
			});
			
			this.loading('stop');
		},
		// 완료된 작업목록 더보기
		getCompleteListMore: async function () {
			this.page.completeWork += 1;
			this.start.completeWork = (this.page.completeWork - 1) * this.limit;
			
			this.loading('start');
			
			await this.getCompleteWorkList(this.listAllChannel).then(response => {
				if (!response.data.length) {
					alert('더이상 목록이 없습니다.');
					this.page.completeWork -= 1;
				}
				else {
					this.completeList.push(...response.data);	
				}
			});
			
			this.loading('stop');
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
		requestWorkDelete: function (id) {
			if(confirm('게시글을 삭제 하시겠습니까?')) {
				var data = {'id': id}
			
				axios.post('/work/request_delete.dat', data)
				.then(reponse => {
					this.loadList(['request']);
				})
				.catch(error => {
					console.log(error);
				});
			}
		},
		// 작업 컨펌 요청 갯수
		setConfirmCount: function () {
			var params = {
				site_code : !this.listAllChannel ? localStorage.channel : null
			}
			
   			axios.get('/work/confirm_cnt.dat', {params: params})
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
			location.href = '/work/request_insert.do';
		}
	}
})
.component('header-component', HeaderComponent)
.use(pinia)
.mount('#v-app');
</script>