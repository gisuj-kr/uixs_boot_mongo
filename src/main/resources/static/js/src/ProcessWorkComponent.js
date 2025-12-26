var processWorkListTemplate = `
<dl>
	<dt>
		<p>작업진행내역
		<!--
		<select class="selectbox" v-model="searchKey" name="" id="" style="height: 40px; width: 120px; background: #fff url(/static/img/ico_select_arrow1.png) no-repeat right 15px top 50%;">
			<option value="">전체보기</option>
			<option value="PLAN">기획</option>
			<option value="DESIGN">디자인</option>
			<option value="PUBLISH">퍼블</option>
		</select>
		-->
		</p>
		<span class="list-count">{{totWorkingCnt}}건</span>
	</dt>
	<dd>
	    <div class="work_factor working" v-for="(item, index) in selectedList">
	        <dl>
	            <dt>
	                <div>ID {{item.site_name != null ? '['+item.site_name+']' : ''}} {{item.request_id}}</div>
	                <a href="#none" @click="openDetail(item.id)">{{item.request_title}}</a>
	            </dt>
	            <dd>
	                <div class="work_area" v-html="getReplaceContent(item.request_content)"></div>
	                <div class="date_area">
	                    <span>~ {{new Date(item.request_complete_date).format('yyyy.MM.dd')}} 까지</span>
	                </div>
	                <div class="state_step_area">
	                	<template v-if="item.need_workers.includes(part.name)" v-for="part in item.part">
	                    <span
	                    	v-if="part.state == 'CONFIRM'" 
	                    	class="step02" 
	                    	style="background: #e55d9c; margin-left: 5px">
	                    	{{partKr[part.name]}} {{stateKr[part.state]}}
	                    </span>
	                    <span
	                    	v-if="part.state == 'COMPLETE'"
	                    	class="step02" 
	                    	style="background: #e55d9c; margin-left: 5px">
	                    	{{partKr[part.name]}} {{stateKr[part.state]}}
	                    </span>
	                    <span
	                    	v-else-if="part.state == 'PENDING'"
	                    	class="step02" 
	                    	style="background: #ff6637; margin-left: 5px">
	                    	{{partKr[part.name]}} {{stateKr[part.state]}}
	                    </span>
	                    <span
	                    	v-else-if="part.state == 'WORKING'"
	                    	class="step02" 
	                    	style="background: #0565f0; margin-left: 5px">
	                    	{{partKr[part.name]}} {{stateKr[part.state]}}
	                    </span>
	                    <span
	                    	v-else-if="part.state == 'EDIT'"
	                    	class="step02" 
	                    	style="background: #0565f0; margin-left: 5px">
	                    	{{partKr[part.name]}} {{stateKr[part.state]}}
	                    </span>
	                    </template>
	                </div>
	            </dd>
	        </dl>
	    </div>
	</dd>
</dl>
`;
/*
var ProcessWorkList = {
    template: processWorkListTemplate,
    mixins:[channelMixin],
    props: ['propList', 'listAllChannel'],
    data: function () {
        return {
            list: this.propList,
            selectedList: this.propList,
            partKr: {
                "plan": '기획',
                "design": '디자인',
                "publish": '퍼블'
            },
            stateKr: {
				PENDING: '대기중',
				WORKING: '진행중',
				CONFIRM: '컨펌중',
				EDIT: '수정중',
				COMPLETE: '완료'
			},
            searchKey: '',
            totWorkingCnt: 0
        }
    },
    mounted() {
		axios.post('/work/working_cnt.dat', {
			site_code: !this.listAllChannel ? localStorage.channel : null, 
			request_state: 'WORKING'
		})
		.then(response => {
			this.totWorkingCnt = response.data;
		}).catch(error => {console.log(error)});
		
		this.selectedList = this.list.map(list => {
			const dupIndex = [];
			const newPart = [];
			
			// 중복파트가 있으면 해당 파트의 배열 인덱스 따로 저장
			if (list.part != null && list.part.length > 0) {
				const plaPart = list.part.filter(part => part.name == 'plan').sort().reverse();
				const desPart = list.part.filter(part => part.name == 'design').sort().reverse();
				const pubPart = list.part.filter(part => part.name == 'publish').sort().reverse();
				
				if (plaPart.length > 0) {
					newPart.push(plaPart[0]);
				}
				if (desPart.length > 0) {
					newPart.push(desPart[0]);
				}
				if (pubPart.length > 0) {
					newPart.push(pubPart[0]);
				}
				
				list.part.forEach((part, index) => {
					if(!newPart.some(npart => npart.name == part.name && npart.worker == part.worker)) {
						dupIndex.push(index);
					}
				})
				
				dupIndex.forEach(i => {
					list.part.splice(i, 1);	
				})
			}
			
			return list;
		})
	},
    watch: {
    },
    methods: {
		
		getReplaceContent: function (str) {
			return str.replace(/\r\n/g, '<br>');
		},
        openDetail: function (id) {
			//uijs.processWork.detailPopup(id);
			this.$emit('show-detail', id);
		},
		
    }
};
// ==============================================
*/

/**
 * 진행중인작업 상세보기 팝업
 */
const ProcessWorkDetail = function() {
	/**
	 * start: 진행중인작업 상세보기 팝업 - 요청작성 상태 템플릿
	 */
	var PartStateTemplate = 
	`<li 
		:class="isNotWorking ? stateAttr.class : '' + ' part-state'" 
		:data-state="state" 
		:data-part="part.toUpperCase()">
		<span class="part-title">{{partList[part]}}</span>
	    <div class="work_step_check" v-if="isNotWorking">
	    	<button type="button" class="btn_small01" :id="detailData.request_id + part" @click.prevent="setWorkStatePopupOpen(part)">{{stateAttr.text}}</button>
	    	<em class="sdate" v-if="worker">담당자: {{worker}}</em>
	    	<em class="sdate" v-if="worker && thisPercent < 100">진행율: {{thisPercent}}%</em>
	        <em class="sdate" v-if="workProgDate && thisPercent < 100">{{workProgDate}}일째 작업중</em>
	        <em class="edate" v-if="thisPercent == 100">완료일: {{workCompleteDate}}</em>
	    </div>
	    <div class="work_step_check" v-else>
	    	<button type="button" class="btn_small01">해당없음</button>
	    	<button type="button" class="btn_add_need_worker" @click="addNeedWorker">추가+</button>
	    </div>
		<div class="edit_pop" style="top: 125px;" v-show="confirmAction" >
	    	<div class="edit_tit">
	        	검수확인
	        	<a href="#none" class="btn_edit_close" title="레이어팝업닫기" @click="confirmAction = false"></a>
	    	</div>
	    	<div class="edit_data">
	    		<ul class="box_type_check">
	    			<li>
	    				<input type="radio" 
	    					:id="'state1_'+detailKey" 
	    					:name="'state_'+detailKey" 
	    					value="CONFIRM_COMPLETE" 
	    					v-model="confirmResult"
	    					@change="confirmOrEdit">
	    				<label :for="'state1_'+detailKey" class="btn-confirm-ok" style="cursor: pointer">검수확인</label>
	    			</li>
	    			<li>
	    				<input type="radio" 
	    					:id="'state_2'+detailKey" 
	    					:name="'state_'+detailKey" 
	    					value="WORKING" 
	    					v-model="confirmResult"
	    					@change="confirmOrEdit">
	    				<label :for="'state_2'+detailKey" class="btn-request-edit" style="cursor: pointer">수정요청</label>
	    			</li>
	    		</ul>
	    	</div>
		</div>
	</li>`; //end: 진행중인작업 상세보기 팝업 - 요청작성 상태 템플릿
	
	/**
	 * start: 진행중인작업 상세보기 팝업 - 요청작성 상태 컴포넌트
	 */
	var PartStateComponent = {
		template: PartStateTemplate,
		mixins: [channelMixin],
		props: ['part', 'detailData'],
		data: function () {
			return {
				partList: {
					plan: "기획", 
					design: "디자인",
					publish: '퍼블'
				},
				// 작업상태별 속성
				stateItems: {
					"PENDING": {
						"text": '대기중',
						"class": 'ing',
						"check": false
					}, 
					"WORKING": {
						"text": '작업중', 
						"class": 'ing', 
						"check": false
					}, 
					"CONFIRM": {
						"text": '컨펌중', 
						"class": 'ing', 
						"check": true
					}, 
					"EDIT": {
						"text": '수정중', 
						"class": 'ing', 
						"check": true
					}, 
					"COMPLETE": {
						"text": '완료', 
						"class": 'end', 
						"check": true
					}
				},
				nowPart: 'plan', // 진행중인 작업파트
				state: 'PENDING', // 작업상태
				confirmAction: false, // 검수요청시 - 검수 팝업열기
				confirmResult: '', // 검수결과
				thisPartState: {},
				ingPartState: {},
				statePercent: {
					PENDING: 0,
					WORKING: 30,
					CONFIRM: 50,
					EDIT: 80,
					COMPLETE: 100							
				}
				// confirmBtn: false, // 검수완료 및 수정 판넬 열기 버튼
			}
		},
		created() {
			const workState = [
				{part: 'plan', ...this.detailData.plan_state},
				{part: 'publish', ...this.detailData.publish_state},
				{part: 'design', ...this.detailData.design_state}
			];
			
			if (this.detailData.part != null) { 
				const partLen = this.detailData.part.filter(part => part.name == this.part).length;
				
				// 현재파트 상태 지정
				if (partLen > 0) {
					this.thisPartState = this.detailData.part
										.filter(part => part.name == this.part)
										.sort()
										.reverse()[0];
				}
				else {
					this.thisPartState = {state: 'PENDING', work_content: []};
				}
			}
			else {
				this.thisPartState = {state: 'PENDING', work_content: []};
			}
			
			//this.thisPartState = workState.filter(obj => obj.part == this.part)[0];
			
			// 현재파트 작업 상태
			if (this.thisPartState != undefined) this.state = this.thisPartState.state; // 작업파트 상태
		},
		computed: {
			thisPercent: function () {
				
				const percent = parseInt(this.statePercent[this.thisPartState.state]);
				
				return isNaN(percent) ? 0 : percent; 
			},
			isNotWorking: function () {
				return this.detailData.need_workers.includes(this.part);
			},
			worker: function () {
				return this.thisPartState.worker;
			},
			// 작업 상태별 속성
			stateAttr: function () {
				return Object.keys(this.stateItems).includes(this.state) ? this.stateItems[this.state] : {};
			},
			workProgDate: function () {
				let startDate = null; 
				let toDate = new Date().format('yyyy.MM.dd');
				
				if (this.thisPartState.work_content != null && this.thisPartState.work_content.length > 0)  {
					const work_content = this.thisPartState.work_content.sort((a, b) => {
						return a.part_work_sday - b.part_work_sday;
					})[0];
					
					startDate = new Date(work_content.part_work_sday).format('yyyy.MM.dd');
				}
				
				return startDate == null ? false : dateDiff(startDate, toDate);
			},
			workStartDate: function () {
				
				
				return (
					this.thisPartState.work_content == null ?
					'' :
					new Date(this.thisPartState.work_sdate).format('yyyy.MM.dd')
				);
			},
			workCompleteDate: function () {
				let rs = '';
				
				if (this.thisPartState.work_content.length > 0)  {
					const work_content = this.thisPartState.work_content[this.thisPartState.work_content.length-1];
					
					if (work_content.part_work_eday != null) {
						rs = new Date(work_content.part_work_eday).format('yyyy.MM.dd');
					}
				}
				
				return rs;
			},
			// 검수요청일자
			confirmRequestDate: function() {
//				console.log(new Date(this.thisPartState.work_sdate).format('yyyy.MM.dd'))
				return (
					this.thisPartState.work_sdate == null ?
					'' :
					new Date(this.thisPartState.work_sdate).format('yyyy.MM.dd')
				);
			},
			// 검수완료 일자
			confirmCompleteDate: function () {
				return (
					(this.state == 'CONFIRM_COMPLETE' && this.thisPartState.work_sdate != null) ?
					new Date(this.thisPartState.work_edate).format('yyyy.MM.dd') :
					''
				);
			},
			// html 요소에 쓰이는 공통키
			detailKey: function () {
				return this.detailData.request_id + this.part;
			},
			// 검수작업 창열기 버튼 - 상태에 따라 보임 또는 안보임
			confirmBtn: function () {
				var ret = false;
				
				switch(this.state) {
					case 'CONFIRM_REQUEST':
						ret =  true;
						break;
					default: ret = false;
				}
				
				return ret;
			}
		},
		methods: {
			addNeedWorker: function () {
				const needWorkers = this.detailData.need_workers;
				needWorkers.push(this.part);
				
				const data = {
					id: this.detailData.id,
					need_workers: needWorkers
				};
				
				axios.post('/work/request_update.dat', data)
				.then(response => {
					console.log(response.data)
					// 데이터 다시로드
					EventBus.$emit('read__data');
				})
				.catch(error => {
					console.log(error);
				});
			},
			setWorkStatePopupOpen: function (part) {
//				IF(THIS.DETAILDATA.REQUEST_STATE == 'COMPLETE') {
//					ALERT('완료된 작업은 수정할 수 없습니다.');
//					RETURN FALSE;					
//				}
//				ELSE {
					EventBus.$emit('open__work_state_popup', part);
//				}
			},
			// 검수요청
			confirmRequest: async function (e) {
				var logininfo = this.loginInfo;
				
				if (logininfo.auth != 'WORKER' && logininfo.auth != 'ADMIN') {
					alert('요청 권한이 없습니다.');
					
					this.stateAttr.check = false;
					
					return false;
				}
				
				var sendData = {
					id: this.detailData.id,
					request_id: this.detailData.request_id,
					working_part: this.part
				};
							
				// 검수요청 일자
				var confirmRequestDate = new Date();
							
				// 요청 파트의 상태 검수요청으로 변경
				sendData[this.part+'_state'] = {state: 'CONFIRM_REQUEST', work_sdate: confirmRequestDate};
						
				if (this.stateAttr.check == true && this.state == 'WORKING') {
					
					if (this.stateAttr.class == 'ing') {
						
						axios.post('/work/request_change_state.dat', sendData)
						.then(response => {
//							returnData = data;
							// 리스트 다시로드
							EventBus.$emit('loadList', ['process']);
							// 데이터 다시로드
							EventBus.$emit('read__data');
							// 작업요청 갯수 다시로드
							EventBus.$emit('setConfirmCount');
							
							// 검수요청완료 메세지 팝업
							alert('검수요청이 완료 되었습니다.');
						})
						.catch(error => {
							console.log(error);
						});
					} 
					else {
						return;
					}
				}
				else {
					this.stateAttr.check = true;
				}
			}, // end: 검수요청
			// start : 검수 or 수정요청
			confirmOrEdit: async function () {
				var logininfo = this.loginInfo;
				
				if (logininfo.auth != 'MANAGER' && logininfo.auth != 'ADMIN') {
					alert('검수 권한이 없습니다.');
					
					this.stateAttr.check = false;
					
					return false;
				}
				
				if (this.stateAttr.check) {
					var nextPart;
					var partArray = Object.keys(this.partList);
					
					// 다음작업 1
					for(var index in partArray) {
						var nextIndex = parseInt(index) + 1;
						
						if (nextIndex > partArray.length) break;
						
						if (this.part == partArray[index]) {
							nextPart = partArray[nextIndex];
							break;
						}
					}
					
					// 검수요청 전송 데이터
					var sendData = {
						id: this.detailData.id,
						request_id: this.detailData.request_id,
					};
					
					// 검수완료 날짜
					var confirmCompleteDate = new Date();
					
					// 현재파트의 검수데이터가 검수 완료인경우 데이터의 PART 를 다음 작업파트로 변경
					if (this.confirmResult == 'CONFIRM_COMPLETE') {
						
						// 다음 작업 파트가 없는경우
						if (nextPart === undefined) {
							sendData['working_part'] = this.part;
						}
						// 다음 작업파트가 있는경우
						else {
							sendData['working_part'] = nextPart;
							// 다음파트 작업상태를 working 으로 변경
							sendData[nextPart+"_state"] = {state: 'WORKING'};
						}
						// 현재작업의 검수완료 날짜 삽입
						sendData[this.part+'_state'] = {work_edate: confirmCompleteDate};
					}
					// 검수완료가 아닌경우 = 수정요청
					else {
//						sendData.part = this.part.toUpperCase();
					}
					// 현재파트 작업상태 변경 CONFIRM_REQUESR or CONFIRM_COMPLETE
					sendData[this.part+'_state'] = {state: this.confirmResult, ...sendData[this.part+'_state']};
					
					if (this.state == 'CONFIRM_REQUEST') {
						axios.post('/work/request_change_state.dat', sendData)
						.then(response => {
							EventBus.$emit('read__data');
								
							// 작업내역 다시 검색
							EventBus.$emit('loadList', ['process']);
						})
						.catch(error => {
							console.log(error);
						});
						
					} // end: if
				} // end : if
			}, // end: 검수 - 수정요청
		}
	};//end: 진행중인작업 상세보기 팝업 - 요청작성 상태 컴포넌트

	
	
	/**
	 * start: 댓글 컴포넌트
	 */
	const CommentComponent = function () {
		// 글쓰기 영역 컴포넌트
		const WriteComponent = function () {
			var WriteTmp = `
			<div class="work_form">
			    <form ref="form" name="comment_form" id="comment_form" method="post" enctype="multipart/form-data" onsubmit="return false;">
			        <select class="selectbox" name="writer_type" v-model="form.writerType">
			            <option value="MN">담당자</option>
			            <option value="PN">기획</option>
			            <option value="DS">디자인</option>
			            <option value="PB">퍼블</option>
			        </select>
			        <div class="coment_box">
			            <textarea placeholder="내용을 입력해주세요" name="content" v-model="form.content" ref="content"></textarea>
			            <div class="btn_coment">
			            	<div class="file_info_area">
			            		<span class="file-info"></span>
			            		<p class="file_info" v-for="(file, index) in savedFiles" :key="file.original_filename">
			            			<span>{{file.original_filename}}</span>
			            			<a href="#" class="btn_inline_delete" @click="deleteFile(index, file.id)"></a>
			            		</p>
			            		<p class="file_info" v-for="(file, index) in form.files" :key="file.name + '_' + index">
			            			<span>{{file.name}}</span>
			            			<a href="#" class="btn_inline_delete" @click="deleteFile(index)"></a>
			            		</p>
			            	</div>
			            	<div class="comment_btn_area" v-if="!isCompleteWork">
			                	<a href="#none" class="btn_small01" style="position: relative">파일업로드+
			                		<input 
			                			type="file" 
			                			id="cfile" 
			                			name="cfile"
			                			@change="addFile" 
			                			style="opacity: 0; width: 100%; position: absolute; left:0; top: 0">
			                	</a>
			                    <a href="#none" class="btn_small02 btn-regist" @click.prevent="comment__insert">{{registBtnText}}</a>
			                    <a href="#none" class="btn_small01 btn-regist" v-if="mode=='edit'" @click.prevent="cancelEdit">취소</a>
			                </div>
			            </div>
			        </div>
			    </form>
			</div>
			`;
			// return comment WriteComponent
			return {
				template: WriteTmp,
				mixins: [channelMixin],
				// props.$workId: request_list 게시물 uniquie id (request_id 아님)
				props: ['workId', 'mode', 'editItem', 'logininfo'],
				data: function () {
					return {
						form: {
							content: '',
							refId: this.workId,
							writerType: 'MN',
							files: []
						},
						savedFiles: [],
						registBtnText: '등록',
					};
				},
				created: function () {
					if (this.mode == 'edit') {
						this.form.content = this.editItem.content;
						this.form.writeType = this.editItem.write_type;
						this.savedFiles = this.editItem.files;
						
						this.registBtnText = '수정';
					}
				},
				mounted: function () {
					if (this.mode == 'edit') this.$refs.content.focus();
					switch(this.logininfo.part) {
						case 'PLAN':
							this.form.writerType = 'PN';
							break;
						case 'DESIGN':
							this.form.writerType = 'DS';
							break;
						case 'PUBLISH':
							this.form.writerType = 'PB';
							break;
						default: this.form.writerType = 'MN';
					}
				},
				computed: {
					isCompleteWork: function () {
						return this.$store.getters['workStore/getWorkDetailOpener'] == 'complete' ? true : false;
					}
				},
				methods: {
					// 댓글 등록 and 수정
					comment__insert() {
						var url = '/comment/insert.dat';
						
						if (this.form.content.replace(/\s/g, '') == '') {
							alert('내용을 입력해 주세요');
							this.$refs.content.focus();
							return;
						}
						
						var sendFormData = new FormData();
						
						sendFormData.append('writer', this.logininfo.userid);
						sendFormData.append('writer_type', this.form.writerType);
						sendFormData.append('ref_id', this.form.refId);
						sendFormData.append('content', this.form.content);
						sendFormData.append('writer_name', this.loginInfo.username);
						// 파일 삽입
						this.form.files.forEach(function (file) {
							sendFormData.append('files', file);
						});
						
						// 수정인 경우는 url변경과 댓글 번호 전달
						if (this.mode == 'edit') {
							url = '/comment/update.dat';
							sendFormData.append('id', this.editItem.id);
						}
						
						this.loading('start');
						
						// 댓글 등록 action
						axios.post(url, sendFormData)
						.then(response => {
							var data = response.data;
							
//							if (data.id || data.updateCnt) {
								this.$emit('edit-cancel');
								this.$emit('list-read');
								
								// 작업중인 목록 다시로드
								// EventBus.$emit('loadList', ['process']);
//							}
							
							this.loading('stop');
						})
						.catch(error => {
							console.log(error);
							this.loading('stop');
						});
					}, // end regist
					/** start: add 파일 */
					addFile: function (e) {
						this.form.files.push(e.target.files[0]);
					},// end: add 파일
					
					/** start: del 파일 */
					deleteFile: function (index, fileId=null) {
						console.log(index, fileId)
						if (!fileId) {
							this.form.files.splice(index, 1);
						}
						// fileId 가 있으면 edit 모드임
						else {
							if (confirm('파일을 삭제 하시겠습니까?')) {
								axios.post('/comment/delete_file.dat', null, {params: {fileId: fileId}})
								.then(response => {
									if (response.data == 'SUCCESS') {
										this.savedFiles.splice(index, 1);
									}
								})
								.catch(error => {
									console.log(error)
								});
								
							}//end: confirm
						} //end edit mode if
					},// end: del 파일
					/**start: 수정 취소 */
					cancelEdit: function () {
						this.$emit('edit-cancel');
					}//end: 수정 취소
				}//end: methods
			}
		}();
		
		// 댓글 목록 컴포넌트
		const ListComponent = function() {
			return {
				template: `
				<li class="list" :key="commentKey">
	                <div class="work_member">
	                    <span 
	                    	class="mem" 
	                    	:style="comment.writer_type == 'MN' ? 'color: #fff; background: #00a684': ''">
	                    	{{writerType[comment.writer_type]}}
	                    </span>
	                </div>
	                <div class="work_text">
	                    <p>{{comment.writer_name}}</p>
	                    <em>{{new Date(comment.regdate).toLocaleString()}}</em>
	                    <div class="data" style="position: relative" v-html="content">
	                    </div>
	                    <div class="plus_file" v-for="file in comment.files">
	                    	<a :href="'/file/download?file_id='+file.id" class="btn_text down">{{file.original_filename}}</a>
	                	</div>
	                </div>
					<div style="text-align: right; margin-top: 10px;" v-if="logininfo.userid === comment.writer && !isCompleteWork">
	                    <a href="#" class="btn_small01 edit" @click.prevent="editComment">수정</a>
	                    <a href="#" class="btn_small01 delete" @click.prevent="comment__delete">삭제</a>
	                </div>
	            </li>
	            `,
                mixins: [channelMixin],
                props: [
					'comment', //comment one
                	'logininfo', 
                	'commentKey',
                	'index'
                ],
	        	data: function () {
					return {
						writerType: {
							'MN': '담당자',
							'PN': '기획자',
							'DS': '디자인',
							'PB': '퍼블'
						}
					};
				},
				watch: {
				},
				computed: {
					content: function () {
						return this.comment.content.replace(/\r\n/g, '<br>');
					},
					isCompleteWork: function () {
						return this.$store.getters['workStore/getWorkDetailOpener'] == 'complete' ? true : false;
					}
				},
				methods: {
					/** start: 댓글 삭제 */
					comment__delete: function () {
						if (confirm('삭제 하시겠습니까?')) {
							axios.post('/comment/delete.dat', null, {params: {id: this.comment.id}})
							.then(response => {
								this.$emit('remove-comment-item', this.index);
							})
							.catch(error => {
								console.log(error);
							});
						}
						else {
							return false;
						}
					},// end: 댓글 삭제
					editComment: function () {
						this.$emit('edit-comment', this.comment);
					}
				}
			};//end: return
		}()// end ListComponent function
		
		
		
		/**
		 * start: 댓글 목록 컴포넌트 템플릿
		 */
		const CommentComponentTmp = `
		<ul>
		    <li class="write">
		    	<WriteComponent 
		    		:work-id="workId" 
		    		:mode="writeMode" 
		    		:edit-item="editItem"
		    		:logininfo="logininfo"
		    		:key="writeKey"
		    		@edit-cancel="editCancel"
		    		@list-read="list__read"
		    		v-if="logininfo"></WriteComponent>
		    </li>
	    	<ListComponent 
	    		:comment-key="commentKey"
	    		:key="comment.id"
	    		:comment="comment" 
	    		:index="index"
	    		:logininfo="logininfo"
	    		v-for="(comment, index) in commentList"
	    		@edit-comment="editComment"
	    		@remove-comment-item="removeCommentItem"
	    		v-if="logininfo"></ListComponent>
		</ul>
		`;
	
		return {
			template: CommentComponentTmp,
			props: ['workId'],
			components: {
				'WriteComponent': WriteComponent,
				'ListComponent': ListComponent,
			},
			mixins: [channelMixin],
			data: function (){
				return {
					commentList: [],
					commentKey: 0,
					writeKey: 0,
					editItem: {},
					writeMode: 'write',
					logininfo: null
				};
			},
			created: function () {
				this.logininfo = this.loginInfo;
				
				this.list__read();
			},
			methods: {
				list__read: function () {
					
					this.loading('start');
					
					axios.get(`/comment/list.dat?ref_id=${this.workId}`)
					.then(response => {
						this.commentList = response.data.map((value) => {
							return {...value.comment, files: [...value.files]};
						});
						
						this.commentKey += 1;
						
						this.loading('stop');
					})
					.catch(error => {
						console.log(error);
						this.loading('stop');
					});
				},
				removeCommentItem: function (index) {
					this.commentList.splice(index, 1);
				},
				editComment: function (item) {
					this.editItem = item;
					this.writeMode = 'edit';
					
					this.writeKey += 1;
				},
				editCancel: function () {
					this.writeMode = 'write';
					
					this.writeKey += 1;
				}
			}
		}
	}();
	//end: 댓글 컴포넌트
	
	// 파트별 작업현황 등록 팝업 컴포넌트
	const PartStatePopupComponent = function () {
		return {
			template: `
				<div id="modal-set-work-state">
			      <article>
			        <header>
			          <button
			            aria-label="Close"
			            rel="prev"
			            data-target="modal-example"
			            @click="closeWithDim"
			          ></button>
			          <h3>{{partToKr[thisPart]}}</h3>
			          <div>
			              <a href="#none" role="button" class="add-work-state-worker" @click.prevent="addWorkStateWorker">담당자 추가+</a>
			          </div>
			        </header>
			        
			        <div class="modal-content" v-for="(data, i) in partData">
			        	<hr class="hr-gubun" v-if="i > 0" />
			        	<div class="work-part-info-wrap">
			        		<div class="input-group">
			        			<label>담당자</label>
					        	<select name="worker" title="담당자" @change.prevent="selectChange">
					        		<option value="지정안됨" >담당자</option>
					        		<option 
					        			:value="mem.username" 
					        			:selected="mem.username == data.worker"
					        			v-for="mem in workers">{{mem.username}}</option>
					        		<option value="강도희" :selected="'강도희' == data.worker">강도희</option>
					        		<option value="정기수" :selected="'정기수' == data.worker">정기수</option>
					        	</select>
			        		</div>
			        		<div class="input-group">
			        			<label>작업현황</label>
					        	<select name="state" title="작업상태" @change.prevent="selectChange">
					        		<option value="">진행상태</option>
					        		<option value="PENDING" :selected="data.state == 'PENDING'">대기중</option>
					        		<option value="WORKING" :selected="data.state == 'WORKING'">작업중</option>
					        		<option value="CONFIRM" :selected="data.state == 'CONFIRM'">컨펌중</option>
					        		<option value="EDIT" :selected="data.state == 'EDIT'">수정중</option>
					        		<option value="COMPLETE" :selected="data.state == 'COMPLETE'">완료</option>
					        		<option value="NONE">해당없음</option>
					        	</select>
			        		</div>
			        		<div class="input-group">
			        			<label :for="'part_work_rday'+i">업무요청일</label>
			        			<input 
			        				:id="'part_work_rday'+i"
					        		type="text" 
					        		class="cal_input" 
					        		name="part_work_rday"
					        		:value="data.part_work_rday" 
					        		placeholder="업무요청일"
					        		autocomplete="off"/>
			        		</div>
			        		<div class="input-group">
			        			<label :for="'part_work_crday'+i">완료요청일</label>
			        			<input 
					        		:id="'part_work_crday'+i"
					        		type="text" 
					        		class="cal_input" 
					        		name="part_work_crday" 
					        		:value="data.part_work_crday" 
					        		placeholder="완료 요청일"
					        		autocomplete="off"/>
			        		</div>
			        		<div class="input-group del-worker-area" v-if="i > 0">
			        			<button 
			        				:data-worker="data.worker"
			        				type="button" 
			        				class="add-work-state-worker" 
			        				@click.prevent="delWorker">담당자 삭제</button>
			        		</div>
			        	</div>
			        	
		        		<label>작업내용</label>
			        	<div class="work-part-info-wrap mt10 only-input-inline">
			        		<input 
				        		type="text" 
				        		class="work_content origin input-origin" 
				        		name="work_content" 
				        		placeholder="작업내용" 
				        		:data-worker="data.worker"/>
				        	<input 
				        		type="text" 
				        		class="cal_input w20per part_work_sday origin input-origin" 
				        		name="part_work_sday" 
				        		placeholder="작업 착수일"
				        		autocomplete="off" />
				        	<input 
				        		type="text" 
				        		class="cal_input w20per part_work_eday origin input-origin" 
				        		name="part_work_eday" 
				        		placeholder="작업 종료일"
				        		autocomplete="off" />
				        	<div class="dummy-like-btn"></div>
			        	</div>
			        	<!--<label>수정 및 추가 작업내용 입력</label>-->
			        	<div class="work-history-box">
				        	<div class="work-part-info-wrap mt10 only-input-inline" v-for="work_history in data.work_content">
				        		<input 
					        		type="text" 
					        		class="work_content" 
					        		style="flex: 2;"
					        		name="work_content" 
					        		placeholder="작업내용입력" 
					        		:data-worker="data.worker" 
					        		:value="work_history.content"/>
					        	<input 
					        		type="text" 
					        		class="cal_input w20per part_work_sday" 
					        		style="flex: 1;"
					        		name="part_work_sday" 
					        		placeholder="작업 착수일" 
					        		:value="work_history.part_work_sday"
					        		autocomplete="off"/>
					        	<input 
					        		type="text" 
					        		class="cal_input w20per part_work_eday" 
					        		style="flex: 1;"
					        		name="part_work_eday" 
					        		placeholder="작업 종료일"
					        		:value="work_history.part_work_eday"
					        		autocomplete="off" />
					        	<button type="button" class="add-work-state-worker" style="margin-left: 5px;" @click="delWorkHistory">삭제</button>
				        	</div>
			        	</div> <!--//end: work-history-box-->
			        	<div class="input-area">
		        			<label :for="'part_bigo'+i">비고</label>
				        	<input 
				        		:id="'part_bigo'+i"
				        		type="text" 
				        		class="" 
				        		name="bigo" 
				        		placeholder="비고"
				        		:value="data.bigo" />
		        		</div>
			        </div>
			        <footer class="mt20">
			          <button autofocus data-target="modal-example" @click.prevent="editWorkState">
			            저장
			          </button>
			        </footer>
			      </article>
			    </div>
			 `,
			 props: ['id', 'thisPart', 'detailData'],
			 data: function() {
				return {
					part_users: [],
					worker: '지정안됨',
					state: '',
					work_sdate: '',
					work_temp_edate: '',
					work_edate: '',
					partToKr: {
						plan: '기획',
						design: '디자인',
						publish: '퍼블'
					},
					workers: [],
					partData: [
						
					],
					partWorkContentData: []
				}
			},
			mounted: function () {
				
				(async () => {
					await axios.get('/member/memberWithPart.dat', {params: {part: this.thisPart.toUpperCase()}})
						.then(response => {
							this.workers = response.data;
						})
						.catch(error => {
							console.log(error);
						});
						
					const oldData = this.detailData[this.thisPart+'_state'];
					const sdate = this.detailData[this.thisPart+'_state'].work_sdate;
					const temp_edate = this.detailData[this.thisPart+'_state'].work_temp_edate;
					const edate = this.detailData[this.thisPart+'_state'].work_edate;
					
					this.worker = this.detailData[this.thisPart+'_state'].worker == null ? '지정안됨' : this.detailData[this.thisPart+'_state'].worker;		
					this.state = this.detailData[this.thisPart+'_state'].state;	
					this.work_sdate	= sdate != null ? new Date(sdate).format('yyyy-MM-dd') : '';
					this.work_temp_edate = temp_edate != null ? new Date(temp_edate).format('yyyy-MM-dd') : '';
					this.work_edate = edate != null ? new Date(edate).format('yyyy-MM-dd') : '';
					
					if (this.detailData.part != null){
						// 현재 파트데이터만 가져옴....
						this.partData = this.detailData.part.filter(item => item.name == this.thisPart);
						
						if (this.partData.length > 0) {
							// 날짜 형식 수정
							this.partData.map((item) => {
								if (item.part_work_rday != null) {
									item.part_work_rday = dateOrEmpty(item.part_work_rday);
								}
								else {
									item.part_work_rday = dateOrEmpty(this.detailData.request_date);
								}
								
								if (item.part_work_crday != null) {
									item.part_work_crday = dateOrEmpty(item.part_work_crday);
								}
								else {
									item.part_work_crday = dateOrEmpty(this.detailData.request_complete_date);
								}
								
								if (item.work_content != null) {
									item.work_content.map((obj) => {
										obj.part_work_sday = dateOrEmpty(obj.part_work_sday);
										obj.part_work_eday = dateOrEmpty(obj.part_work_eday);
										
										return obj;	
									});
								}
								
								return item;
							});
							
						} 
					}
					
					if (oldData != null && this.partData.length <= 0) {
						const tmpPartData = {
							worker: oldData.worker == null ? '지정안됨' : oldData.worker,
							state: oldData.state,
							part_work_rday: dateOrEmpty(this.detailData.request_date),
							part_work_crday: dateOrEmpty(this.detailData.request_complete_date)
						};
						
						if (oldData.work_sdate != null || oldData.work_edate != null) {
							tmpPartData.work_content = [{
								worker: oldData.worker,
								content: '',
								part_work_sday: dateOrEmpty(oldData.work_sdate),
								part_work_eday: dateOrEmpty(oldData.work_edate)
							}];
						}
						
						this.partData = [{...tmpPartData}];
					}
					
					// 인풋에 데이트피커 설정
					setTimeout(() => {
						$('.cal_input').datepicker({
							beforeShow: (input, inst) => { 
								setTimeout(() => {
							        inst.dpDiv.css({"z-index":1000});
								});
						    }
						});
					}, 500);
//					this.$refs.sel_worker.value = this.worker;
//					this.$refs.sel_state.value = this.state;
				})();
			},
			methods: {
				closeWithDim: function (e) {
					EventBus.$emit('close__work_state_popup');
				},
				selectChange: function (e) {
					// NONE = 해당없음 = 초기화 = 업무 분야에서 제거됨
					if (e.target.value == 'NONE') {
						if (confirm(this.partToKr[this.thisPart] + '파트가 공수에서 제외 됩니다.'))
						{
							const needWorkers = this.detailData.need_workers.filter((arr) => {
								return arr != this.thisPart;
							});
							
							const data = {
								id : this.id,
								need_workers: needWorkers,
							};
							
							axios.post('/work/request_update.dat', data)
							.then(response => {
								EventBus.$emit('read__data');
								EventBus.$emit('close__work_state_popup');
							})
							.catch(error => {
								console.log(error);
							});
						}
					}
					
					if (e.target.getAttribute('name') == 'worker') {
						const siblingsEl = e.target.closest('.modal-content');
						const inputWorkContent = siblingsEl.querySelectorAll("input[name=work_content]");
						
						inputWorkContent.forEach((input) => {
							input.setAttribute("data-worker", e.target.value);
						});
					} 
				},
				addWorkStateWorker: function () {
					const tmpData = {
						name: this.thisPart,
						worker : '지정안됨',
						state: '',
						bigo: '',
						work_content: [],
						part_work_rday: dateOrEmpty(this.detailData.request_date),
						part_work_crday: dateOrEmpty(this.detailData.request_complete_date),
					}
					
					const sameDataIndex = this.partData.findIndex((item) => {
						return (
							item.name == tmpData.name && 
							item.worker == tmpData.worker
						);
					});
					
					if (sameDataIndex != -1) {
						alert('담당자가 지정 안되거나 동일한 담당자가 지정되어 있습니다. 기존 담당자를 수정후 추가해 주세요');
					}
					else {
						this.partData.push(tmpData);
					}
				},
				// 담당자 삭제
				delWorker: function (e) {
					const delWorker = e.target.dataset.worker;
					
					this.partData = this.partData.filter(part => part.name == this.thisPart && part.worker != delWorker);
					
					if (this.detailData.part != null) {
						this.detailData.part = this.detailData.part.filter(part => {
							if (part.name == this.thisPart && part.worker == delWorker) {
								return false;
							}
							else {
								return true;
							}
						});
					}
				},
				editWorkState: function () {
					const selWorker = document.querySelectorAll("select[name=worker]");
					const selState = document.querySelectorAll("select[name=state]");
					const inpBigo = document.querySelectorAll("input[name=bigo]");
					const inpWork_content = document.querySelectorAll("input[name=work_content]");
					const inpPart_work_rday = document.querySelectorAll("input[name=part_work_rday]");
					const inpPart_work_crday = document.querySelectorAll("input[name=part_work_crday]");
					const inpPart_work_sday = document.querySelectorAll("input[name=part_work_sday]");
					const inpPart_work_eday = document.querySelectorAll("input[name=part_work_eday]");
					
					this.partData = [];
					
					if(this.detailData.request_state == 'COMPLETE') {
						alert('완료된 작업은 수정할 수 없습니다.');
						return false;					
					}
					
					selWorker.forEach((item, i) => {
						const worker = item.value;
						const state = selState[i].value;
						const bigo = inpBigo[i].value;
						const part_work_rday = inpPart_work_rday[i].value;
						const part_work_crday = inpPart_work_crday[i].value;
						
						this.partWorkContentData = [];
						// 작업내용 저장
						inpWork_content.forEach((input, index) => {
							
							const value = input.value;
							const dataWorker = input.dataset.worker;
							const sday = inpPart_work_sday[index].value;
							const eday = inpPart_work_eday[index].value;
							
							if (worker !== dataWorker) return false;
							
							
							if (value.trim() != '' || sday.trim() != '' || eday.trim() != '') {
								
								const workContentItem = {
									worker: dataWorker,
									content: value, // work_content input value
									part_work_sday: dateOrNullnfm(sday),
									part_work_eday: dateOrNullnfm(eday)
								};
								this.partWorkContentData.push(workContentItem);	
								
							}
						});
						
						const partItem = {
							name: this.thisPart,
							worker,
							state,
							bigo,
							work_content: this.partWorkContentData,
							part_work_rday: dateOrNullnfm(part_work_rday),
							part_work_crday: dateOrNullnfm(part_work_crday),
						}
						
						this.partData.push(partItem);
					});
					
					// 작업내용 입력폼 초기화
					document.querySelectorAll('.origin').forEach((inp) => {
						inp.value = "";
					});
				

					// 기존 데이터에 지금 입력한 데이터와 동일한 담당자 + 파트 가 있는지 확인...
					// 방금 입력한 데이터와 동일한 데이터는 제외
					let dupData = [];
					if (this.detailData.part != null) {
//						
//						dupData = this.detailData.part.filter((part) => {
//							return this.partData.filter((item) => {
//								return part.name != item.name && item.worker != part.worker	
//							}).length > 0;
//						});
						
						dupData = this.detailData.part.filter(data => data.name != this.thisPart);
					
					}
					
					this.partData = this.partData.filter((item, i) => {
					  	return (
					    	this.partData.findIndex((item2, j) => {
					      		return item.worker === item2.worker;
					    	}) === i
					  	);
					});
					
					console.log(dupData);
					
					// 기존 데이터에서 날짜 를 new Date 타입으로 수정
					dupData.map((arr) => {
						arr.part_work_rday = dateOrNullnfm(dateOrEmpty(arr.part_work_rday));
						arr.part_work_crday = dateOrNullnfm(dateOrEmpty(arr.part_work_crday));
						
						if (arr.work_content != null) {
							arr.work_content.map(arr2 => {
								arr2.part_work_sday = dateOrNullnfm(dateOrEmpty(arr2.part_work_sday));
								arr2.part_work_eday = dateOrNullnfm(dateOrEmpty(arr2.part_work_eday));
								
								return arr2;
							});
						}
						
						return arr;
					});
					
					const newData = [...dupData, ...this.partData];
					
					console.log(newData);
					
					const data = {id: this.id, part: newData};
					
					axios.post('/work/request_change_state.dat', data)
					.then(response => {
						EventBus.$emit('read__data');
						EventBus.$emit('close__work_state_popup');
					})
					.catch(error => {
						console.log(error);
					});
				},
				// 작업내역 삭제
				delWorkHistory: function (e) {
					e.target.closest('.work-part-info-wrap').remove();
				},
			}
		} // end return
	}();// end : 파트별 작업현황 등록 팝업 컴포넌트
	
	/**
	 * 진행중인작업 상세보기 팝업 템플릿
	 */
	const ProcessDetailTemplate = `
	<div class="modal_layer_pop work_type"  @click="closeFromDim" v-cloak v-if="isShow">
		<div class="modal_layer_inner" style="background:#fff">
			<div class="pop_tit"><a href="#none" class="btn_pop_close" title="레이어팝업 닫기" @click="closeDetail"></a></div>
			<div class="pop_content" style="height: 750px;">
				<ul class="work_temp">
		            <li class="cont">
		                <div class="work_factor">
		                    <dl>
		                        <dt>
		                            <div>ID {{detailData.site_name && '['+detailData.site_name+']'}} {{detailData.request_id}}</div>
		                            <a href="#none" 
		                            	v-if="!isTitEdit" 
		                            	@click="edit_mode('isTitEdit', 'request_title')">{{detailData.request_title}}</a>
		                            <input 
		                            	type="text" 
		                            	v-else
		                            	:value="detailData.request_title" 
		                            	ref="request_title"
		                            	@blur="edit_request('isTitEdit', 'request_title')"/>
		                        </dt>
		                        <dd>
		                            <div 
		                            	class="work_area" 
		                            	v-html="detailContent"
		                            	v-if="!isContEdit"
		                            	@click="edit_mode('isContEdit', 'request_content')"></div>
		                            <div 
		                            	v-else
		                            	class="work_area"">
		                            	<textarea 
		                            		ref="request_content"
		                            		@blur="edit_request('isContEdit', 'request_content')">{{detailData.request_content}}</textarea>
		                            </div>
		                            
		                            <div class="work_section">
		                                <div class="sec_02">
		                                    <p>요청자</p>
		                                    <div class="work_day">
                                                <span>{{detailData.requestor_name}}</span>
                                           	</div>
		                                </div>
		                                <div class="sec_02">
		                                    <p>업무요청일</p>
		                                    <div class="work_day" v-if="!dateEditMode" @click="dateEdit">
                                                <span>{{new Date(request_date).format('yyyy-MM-dd')}} </span>
                                           	</div>
                    						<div class="work_day" style="display:flex;align-content:center;padding:4px" v-else>
	                                           	<input v-model="request_date" ref="dateInput"/>
	                    						<button type="button" class="btn_small01  mr4" @click="edit__date">수정</button>
	                    						<button type="button" class="btn_small01  mr4" @click="cancelEditDate">취소</button>
                                           	</div>
		                                </div>
		                                <div class="sec_02">
		                                    <p>완료 요청일</p>
		                                    <div class="work_day" v-if="!dateEditMode2" @click="dateEdit2">
                                                <span>~ {{new Date(request_complete_date).format('yyyy-MM-dd')}}</span>
                                           	</div>
                    						<div class="work_day" style="display:flex;align-content:center;padding:4px" v-else>
	                                           	<input v-model="request_complete_date" ref="dateInput2"/>
	                    						<button type="button" class="btn_small01  mr4" @click="edit__date">수정</button>
	                    						<button type="button" class="btn_small01  mr4" @click="cancelEditDate">취소</button>
                                           	</div>
		                                </div>
		                            </div> 
		                            <div class="work_file">
										<p>첨부파일</p>
										<ul class="mt20">
					                        <li v-for="file in uploadFiles" :key="file.file_id">
				                               <strong>{{file.original_filename}}</strong>
				                               <a :href="'/file/download?file_id='+file.file_id" class="btn_text down">다운로드</a>
				                            </li>
										</ul>
									</div>
		                        </dd>
		                    </dl>
		        		</div>
	                  	<div class="work_step_area">
	        				<div class="work_part">
		        				<div class="btn_area">
		        					<!--
		                            <a href="#none" 
		                            	class="btn_small01 btn-all-confirm-cancel" 
		                            	v-show="!workCompleted"
		                            	@click.prevent="setWorkComplete">작업완료</a>
		                            -->
		                        </div>
					           	<ul>
						         
						           	<!--start: 파트별 작업 진행 상태-->
						           	<PartStateLi 
						           		v-for="part in partItems"
						           		:part="part" 
						           		:detail-data="detailData" 
						           		:key="partStateComponentKey + '_'+part"></PartStateLi>
						           	<!--//end: 파트별 작업 진행 상태-->
						           	
						           	<li class="fix complete" :class="{'end': workCompleted}">
							           <span>진행중</span>
							           <div class="work_step_check">
							           		<button class="btn_small01" @click.prevent="setWorkComplete">작업완료</button>
							           </div>
							           <p class="work-process-percent">{{completePercent}}%</p>
						           	</li>
					           	</ul>
				           	</div>
				        </div>
						<div class="active_txt">
				           <div class="work_message">
				           		<p>작업내역</p>
				           		<!--start: 댓글 컴포넌트-->
				           		<CommentComponent 
				           			v-if="Object.keys(detailData).length" 
				           			:work-id="detailData.id"></CommentComponent>
				           </div>
				        </div>
		            </li>
		        </ul>
			</div>
		</div>
		<parts-state-popup-component
			:id="id" 
			:this-part="passPart" 
			:detail-data="detailData" 
			v-if="isSetPopup"></parts-state-popup-component>
//		<PartStatePopupComponent 
//			:id="id" 
//			:this-part="passPart" 
//			:detail-data="detailData" 
//			v-if="isSetPopup"></PartStatePopupComponent> 
	</div>
	`;
	
	return {
		template: ProcessDetailTemplate,
		mixins:[channelMixin],
		components: {
			// 파트별 작업상태 컴퍼넌트
			'PartStateLi': PartStateComponent,
			'CommentComponent': CommentComponent,
			'PartStatePopupComponent': PartStatePopupComponent,
			'parts-state-popup-component': httpVueLoader('/static/js/vue/PartsStatePopupComponent.vue')
		},
		props: ['id', 'opener'],
		data: function () {
			return {
				detailData: {}, // 상세내용 
				uploadFiles: [], // 첨부파일
				iaPath: [], // ia 경로
				isShow: false, 
				partStateComponentKey: 0, // 컴퍼넌트 reload key
				confirmCompleteAll: false, // 전체파트의 작업 검수결과
				partItems: ['plan', 'design', 'publish'], // 파트 목록 
				dateEditMode: false,
				dateEditMode2: false,
				endDate: '',
				request_date: '',
				request_complete_date: '',
				isSetPopup: false,
				passPart: '',
				isTitEdit: false,
				isContEdit: false
			}
		},
		created: function () {
			// 초기 상세데이터 read
			this.read__data();
			// 이벤트 버스 등록
			EventBus.$on('read__data', this.read__data);
			EventBus.$on('open__work_state_popup', this.openWorkStatePopup);
			EventBus.$on('close__work_state_popup', this.closeWorkStatePopup);
		},
		mounted: function () {
			this.channelName = this.selectedChannel.name;
		},
		computed: {
			// 상세내용 줄바꿈 처리
			detailContent: function () {
				if (this.detailData.request_content) {
					return this.detailData.request_content.replace(/\r\n/g, '<br/>');
				}
				else {
					return '';
				}
			},
			// 요청작업의 처리상태
			workCompleted: function () {
				return this.detailData.request_state == 'COMPLETE';
			},
			// 완료 퍼센트
			completePercent: function () {
				const statePercent = {
					PENDING: 0,
					WORKING: 30,
					CONFIRM: 50,
					EDIT: 80,
					COMPLETE: 100							
				};
				
				let plaPer = 0;
				let desPer = 0;
				let pubPer = 0;
				
				const modNum = this.detailData.need_workers.length;
				
				if (this.detailData.part == null) return 0;
				
				const plaPart = this.detailData.part.filter((part) => part.name == 'plan') || [];
				const desPart = this.detailData.part.filter((part) => part.name == 'design') || [];
				const pubPart = this.detailData.part.filter((part) => part.name == 'publish') || [];
				
				let plaState = 0, desState = 0, pubState = 0;
				 
				if (plaPart.length > 0){
					plaState = (plaPart.sort().reverse())[0].state;
					plaPer = statePercent[plaState];
				}
				if (desPart.length > 0){
					desState = (desPart.sort().reverse())[0].state;
					desPer = statePercent[desState];
				}
				if (pubPart.length > 0){
					pubState = (pubPart.sort().reverse())[0].state;
					pubPer = statePercent[pubState];
				}
				
				const totPer = (plaPer + desPer + pubPer) / modNum;
				
				return isNaN(totPer) ? 0 : Math.ceil(totPer); 
			}
		},
		methods: {
			edit_mode: function (flag, target) {
				this.$data[flag] = true;
				
				setTimeout(() => {
					this.$refs[target].focus();
//					event.target.focus();
				}, 100);
			},
			// 제목 수정
			edit_request: function (flag, target) {
				const data = {id: this.detailData.id};
				
				if(this.$refs[target].value.trim() != this.detailData[target].trim()) {
					
					data[target] = this.$refs[target].value.trim();
					
					axios.post('/work/request_update.dat', data)
					.then(response => {
						EventBus.$emit('read__data');
						EventBus.$emit('loadList');
					})
					.catch(error => {
						console.log(error);
					})
					.finally(() => {
						this.$data[flag] = false;
					});
				}
				else {
					this.$data[flag] = false;
				}
			},
			// 업무파트별 작업 상태 팝업 띄우기
			openWorkStatePopup: function (part) {
				this.isSetPopup = true;
				this.passPart = part
			},
			// 업무파트별 작업 상태 팝업 띄우기
			closeWorkStatePopup: function (part) {
				this.isSetPopup = false;
			},
			// 팝업의 딤드영역 클릭시 팝업 닫힘 설정
			closeFromDim: function (e) {
				if (e.target.classList[0] == 'modal_layer_pop') {
					this.closeDetail();
				}
			},
			// 팝업 닫기
			closeDetail: function () {
				this.$emit('close-detail');
			},
			// 상세데이터 조회
			read__data: function () {
				// loading 시작
				this.loading('start');
				
				axios.get("/work/work_ing_detail.dat?id="+this.id)
				.then(response => {
					var data = response.data;
					
					this.detailData = data.REQUEST_WORK;
					this.uploadFiles = data.FILES;
					
					// 종료일 수정을위한 임시 종료일 설정
					this.request_date = new Date(this.detailData.request_date).format('yyyy-MM-dd');
					this.request_complete_date = new Date(this.detailData.request_complete_date).format('yyyy-MM-dd');
					
					// 전체작업완료 버튼 노출 여부
					if(this.detailData.plan_state.state == 'CONFIRM_COMPLETE' && 
						this.detailData.design_state.state == 'CONFIRM_COMPLETE' &&
						this.detailData.publish_state.state == 'CONFIRM_COMPLETE') {
							
						this.confirmCompleteAll = true;
					}
					else {
						this.confirmCompleteAll= false;
					} 
					
					// 상세보기 팝업 컴퍼넌트 키값 증가
					this.partStateComponentKey += 1;
					// 팝업 보이기
					this.isShow = true;
					
					// 로딩 중지
					this.loading('stop');
				})
				.catch(error => {
					console.log(error);
					
					// 로딩 중지
					this.loading('stop');
				});
			},
			
			// 검수된 작업 전체 검수취소 취소
			allConfirmCancel: async function () {
						
				var logininfo = this.loginInfo;
				
				if (logininfo.auth != 'MANAGER' && logininfo.auth != 'ADMIN') {
					alert('검수취소 권한이 없습니다.');
					return false;
				}
	
				var sendData = {
					"id": this.detailData.id,
					"request_id": this.detailData.request_id,
					"plan_state": {state: 'WORKING'},
					"design_state": {state: 'PENDING'},
					"publish_state": {state: 'PENDING'},
					"working_part": 'plan'
				};
				
				axios.post('/work/request_change_state.dat', sendData)
				.then(response => {
					EventBus.$emit('read__data');
					EventBus.$emit('loadList');
				})
				.catch(error => {
					console.log(error);
				});
			},// end: allConfirmCancel - 검수된 작업 전체 검수취소 취소
			
			// start: 전체작업 완료 설정
			setWorkComplete: function () {
				// 작업이 완료된 요청인 경우 누를수없음
				if (this.workCompleted) return;
				
				var sendData = {
					"id": this.detailData.id
					, "request_id": this.detailData.request_id
					, "request_state": 'COMPLETE'
					, "complete_date": new Date()
				};
				
				axios.post('/work/request_change_state.dat', sendData)
				.then(response => {
					// 작업 리스트 전체 다시 로드
					EventBus.$emit('loadList');
					// 상세보기 팝업 닫기
					this.$emit('close-detail');
				})
				.catch(error => {
					console.log(error);
				});
			},// end: setWorkComplete - 전체작업 완료 설정
			// 협의에 의한 날짜조정
			dateEdit: function () {
//				return;
				var app = this;
				
				this.dateEditMode = true;
				
				// 인풋에 데이트피커 설정
				setTimeout(() => {
					$(this.$refs.dateInput).datepicker({
						beforeShow: (input, inst) => { 
							setTimeout(function () {
						        inst.dpDiv.css({"z-index":1000});
							});
					    },
					    // date picker 날짜 변경시 ia data 변경
					    onSelect: (dateText, inst) => {
					    	this.request_date = dateText;
					    }
					});
				})
			}, //end : dateEdit
			dateEdit2: function () {
				this.dateEditMode2 = true;
				
				// 인풋에 데이트피커 설정
				setTimeout(() => {
					$(this.$refs.dateInput2).datepicker({
						beforeShow: (input, inst) => { 
							setTimeout(() => {
						        inst.dpDiv.css({"z-index":1000});
							});
					    },
					    // date picker 날짜 변경시 ia data 변경
					    onSelect: (dateText, inst) => {
					    	this.request_complete_date = dateText;
					    }
					});
				})
			}, //end : dateEdit
			cancelEditDate: function () {
				this.dateEditMode = false;
				this.dateEditMode2 = false;
				this.request_date = new Date(this.detailData.request_date).format('yyyy-MM-dd');
				this.request_complete_date = new Date(this.detailData.request_complete_date).format('yyyy-MM-dd');
			}, //end: cancelEditDate
			
			//종료일 데이터 수정
			edit__date: function () {
				var app = this;
				var sendData = {
					id: this.detailData.id,
					request_id: this.detailData.request_id,
					request_state: this.detailData.request_state,
					request_date: new Date(this.request_date),
					request_complete_date: new Date(this.request_complete_date)
				};
				
				axios.post('/work/request_change_state.dat', sendData)
				.catch(error => {
					console.log(error);
				});
				
				this.dateEditMode = false;
				this.dateEditMode2 = false;
			}, //end: edit__date
		}
	};
}();




