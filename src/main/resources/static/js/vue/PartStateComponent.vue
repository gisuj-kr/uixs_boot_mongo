<template>
<div 
    class="flex flex-col items-center p-4 rounded-xl transition-all cursor-pointer relative overflow-hidden bg-surface-container-lowest"
    :class="cardClasses"
    @click.prevent="isNotWorking ? setWorkStatePopupOpen(part) : confirmAddWorker()">
    
    <!-- 작업중 배경 효과 -->
    <div v-if="isNotWorking && state == 'WORKING'" class="absolute inset-0 bg-primary/5"></div>
    
    <!-- 아이콘 영역 -->
    <div class="relative z-10 w-12 h-12 flex items-center justify-center rounded-full mb-3" :class="iconContainerClasses">
        <span class="material-symbols-outlined">{{partIcon}}</span>
        <!-- 완료율 뱃지 -->
        <div v-if="isNotWorking" 
            class="absolute -bottom-1 -right-1 text-[8px] font-bold px-1.5 rounded-full border-2 border-white"
            :class="badgeClasses">
            {{thisPercent}}%
        </div>
    </div>
    
    <!-- 텍스트 영역 -->
    <span class="relative z-10 text-xs font-bold" :class="textTitleClasses">{{partList[part]}}</span>
    <span class="relative z-10 text-[10px]" :class="textStatusClasses">{{isNotWorking ? stateAttr.text : '대기'}}</span>
</div>
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
            
            // 현재파트 상태 지정 (상태 우선순위: COMPLETE > EDIT > CONFIRM > WORKING > PENDING)
            if (partLen > 0) {
                const statePriority = { 'COMPLETE': 5, 'EDIT': 4, 'CONFIRM': 3, 'WORKING': 2, 'PENDING': 1 };
                this.thisPartState = this.detailData.part
                                    .filter(part => part.name == this.part)
                                    .sort((a, b) => {
                                        return (statePriority[b.state] || 0) - (statePriority[a.state] || 0);
                                    })[0];
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
            const workers = this.detailData.part.filter(p => p.name == this.part).map(p => p.worker);
            if (workers.length > 1) {
                return workers[0] + ' 외 ' + (workers.length - 1) + '명';
            }
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
            let latestDate = null;
            
            // 해당 파트의 모든 담당자 데이터 수집
            const allWorkersOfPart = this.detailData.part.filter(p => p.name == this.part);
            
            allWorkersOfPart.forEach(workerData => {
                if (workerData.work_content && workerData.work_content.length > 0) {
                    workerData.work_content.forEach(content => {
                        if (content.part_work_eday) {
                            // 문자열인 경우 Date 객체로 변환 시도
                            const edate = new Date(content.part_work_eday);
                            if (!isNaN(edate.getTime())) {
                                if (!latestDate || edate > latestDate) {
                                    latestDate = edate;
                                }
                            }
                        }
                    });
                }
            });
            
            return latestDate ? latestDate.format('yyyy.MM.dd') : '';
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
        },
        // 디자인 적용을 위한 추가 계산된 속성들
        cardClasses: function() {
            if (!this.isNotWorking) return 'border border-outline-variant/10 shadow-sm opacity-60';
            if (this.state === 'COMPLETE') return 'border border-outline-variant/10 shadow-sm';
            if (this.state === 'WORKING' || this.state === 'CONFIRM') return 'border-2 border-primary shadow-md';
            return 'border border-outline-variant/10 shadow-sm';
        },
        iconContainerClasses: function() {
            if (!this.isNotWorking) return 'bg-surface-variant text-on-surface-variant';
            if (this.state === 'COMPLETE') return 'bg-secondary-container text-on-secondary-container';
            if (this.state === 'WORKING' || this.state === 'CONFIRM') return 'bg-primary text-on-primary shadow-lg shadow-primary/20';
            return 'bg-secondary-container text-on-secondary-container';
        },
        badgeClasses: function() {
            if (this.thisPercent === 100) return 'bg-primary text-white';
            return 'bg-tertiary-container text-on-tertiary-container';
        },
        textTitleClasses: function() {
            if (!this.isNotWorking) return 'text-on-surface';
            if (this.state === 'WORKING' || this.state === 'CONFIRM') return 'text-primary';
            return 'text-on-surface';
        },
        textStatusClasses: function() {
            if (!this.isNotWorking) return 'text-on-surface-variant';
            if (this.state === 'WORKING' || this.state === 'CONFIRM') return 'text-primary-dim font-medium';
            return 'text-on-surface-variant';
        },
        partIcon: function() {
            const icons = {
                plan: 'lightbulb',
                design: 'palette',
                publish: 'code'
            };
            return icons[this.part] || 'help';
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
        confirmAddWorker: function () {
            if (confirm(`'${this.partList[this.part]}' 파트에 업무를 추가 하시겠습니까?`)) {
                this.addNeedWorker();
            }
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