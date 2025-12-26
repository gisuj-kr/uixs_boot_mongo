<template>
<li 
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
</li>
</template>
	
<script>
/** start: 진행중인작업 상세보기 팝업 - 요청작성 상태 컴포넌트 */
const eventBus = useEventBusStore();

export default {
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
    mounted() {
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
		isAdmin: function () {
			return this.$checkAuth();
		},
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
            const needWorkers = this.detailData.need_workers || [];
            needWorkers.push(this.part);
			const part = this.detailData.part?.filter(part => part.name != this.part);

			part.push({
				name: this.part,
				worker : '지정안됨',
				state: 'PENDING',
			});
            
            const data = {
                id: this.detailData.id,
                need_workers: needWorkers,
				part
            };
            
            axios.post(API_URL+'/work/modify_request.dat', data)
            .then(response => {
                console.log(response.data)
                // 데이터 다시로드
                eventBus.event.read__data();
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
            eventBus.event.open__work_state_popup(part);
//				}
        },
        // 검수요청
        confirmRequest: async function (e) {
            
            if (!this.isAdmin) {
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
                        eventBus.event.loadList(['process']);
                        // 데이터 다시로드
                        eventBus.event.read__data();
                        // 작업요청 갯수 다시로드
                        eventBus.event.setConfirmCount();
                        
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
                        eventBus.event.read__data();
                            
                        // 작업내역 다시 검색
                        eventBus.event.loadList(['process']);
                    })
                    .catch(error => {
                        console.log(error);
                    });
                    
                } // end: if
            } // end : if
        }, // end: 검수 - 수정요청
    }
};//end: 진행중인작업 상세보기 팝업 - 요청작성 상태 컴포넌트
</script>