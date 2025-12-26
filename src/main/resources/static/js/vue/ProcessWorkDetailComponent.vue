<template>
<div class="modal_layer_pop work_type"  @click="closeFromDim" v-cloak v-if="isShow">
    <div class="modal_layer_inner" style="background:#fff">
        <div class="pop_tit add-btn-edit">
			<button class="btn_edit" title="수정" @click="requestWorkDelete(detailData.id)">삭제&#9998;</button>
			<a href="#none" class="btn_pop_close" title="레이어팝업 닫기" @click="closeDetail"></a>
		</div>
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
                                    @blur="edit_request('isTitEdit', 'request_title')"
								>
                            </dt>
                            <dd>
                                <div 
                                    class="work_area" 
                                    v-html="detailContent"
                                    v-if="!isContEdit"
                                    @click="edit_mode('isContEdit', 'request_content')"></div>
                                <div 
                                    v-else
                                    class="work_area">
                                    <textarea 
                                        ref="request_content"
                                        @blur="edit_request('isContEdit', 'request_content')">{{detailData.request_content}}</textarea>
                                </div>
                                
                                <div class="work_section">
                                    <div class="sec_02">
                                        <p>요청자</p>
                                        <div class="work_day" v-if="!isRequestorEdit" @click="edit_mode('isRequestorEdit', 'requestor_name')">
                                            <span>{{detailData.requestor_name}}</span>
                                        </div>
										<div class="work_day" style="display:flex;align-content:center;padding:4px" v-else>
                                            <input 
												type="text" 
												:value="detailData.requestor_name"  
												ref="requestor_name" 
												@blur="edit_request('isRequestorEdit', 'requestor_name')"
											>
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
								<!--
                                <div class="work_file">
                                    <p>첨부파일</p>
                                    <ul class="mt20">
                                        <li v-for="file in uploadFiles" :key="file.file_id">
                                            <strong>{{file.original_filename}}</strong>
                                            <a :href="'/file/download?file_id='+file.file_id" class="btn_text down">다운로드</a>
                                        </li>
                                    </ul>
                                </div>
								-->
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
                                <part-state-component 
                                    v-for="part in partItems"
                                    :part="part" 
                                    :detail-data="detailData" 
                                    :key="partStateComponentKey + '_'+part">
                                </part-state-component>
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
                            <comment-component 
                                v-if="Object.keys(detailData).length" 
                                :work-id="detailData.id">
                            </comment-component>
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
        v-if="isSetPopup">
    </parts-state-popup-component>
</div>    
</template>

<script>
const eventBus = useEventBusStore();

export default {
    mixins:[channelMixin],
    components: {
        // 파트별 작업상태 컴퍼넌트
        'part-state-component': defineAsyncComponent(() => {
            return loadModule('/static/js/vue/PartStateComponent.vue',vue3LoadOption);
        }),
        'comment-component': defineAsyncComponent(() => {
            return loadModule('/static/js/vue/CommentComponent.vue',vue3LoadOption);
        }),
        // 파트별 작업상태 상세 팝업 컴포넌트
        'parts-state-popup-component': defineAsyncComponent(() => {
            return loadModule('/static/js/vue/PartsStatePopupComponent.vue',vue3LoadOption);
        })
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
            isContEdit: false,
			isRequestorEdit: false,
        }
    },
	inject: ['requestWorkDelete'],
    created: function () {
        // 초기 상세데이터 read
        this.read__data();
    },
    mounted: function () {
        this.channelName = this.selectedChannel.name;

         // 이벤트 버스 등록
        eventBus.addEvent('read__data', this.read__data);
        eventBus.addEvent('open__work_state_popup', this.openWorkStatePopup);
        eventBus.addEvent('close__work_state_popup', this.closeWorkStatePopup);
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
                
                axios.post(API_URL + '/work/modify_request.dat', data)
                .then(response => {
                    this.read__data();
                    eventBus.event.loadList(['process']);
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
            
            axios.get(API_URL + "/work/work_ing_detail.dat?id="+this.id)
            .then(response => {
                var data = response.data;
                
                this.detailData = data.REQUEST_WORK;
                this.uploadFiles = data.FILES;
                
                // 종료일 수정을위한 임시 종료일 설정
                this.request_date = new Date(this.detailData.request_date).format('yyyy-MM-dd');
                this.request_complete_date = new Date(this.detailData.request_complete_date).format('yyyy-MM-dd');
                
                // 전체작업완료 버튼 노출 여부
                if(this.detailData.plan_state?.state == 'CONFIRM_COMPLETE' && 
                    this.detailData.design_state?.state == 'CONFIRM_COMPLETE' &&
                    this.detailData.publish_state?.state == 'CONFIRM_COMPLETE') {
                        
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
        
        // start: 전체작업 완료 설정
        setWorkComplete: function () {
            // 작업이 완료된 요청인 경우 누를수없음
            if (this.workCompleted) return;

			if (this.completePercent < 100) {
                alert('진행 중인 작업입니다. 100%진행 후 완료 버튼을 눌러주세요');
                return;
            }
            
            var sendData = {
                "id": this.detailData.id
                , "request_id": this.detailData.request_id
                , "request_state": 'COMPLETE'
                , "complete_date": new Date()
            };
            
            axios.post( API_URL + '/work/modify_request.dat', sendData)
            .then(response => {
                // 작업 리스트 전체 다시 로드
                eventBus.event.loadList(['process', 'complete']);
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
            
            axios.post(API_URL + '/work/modify_request.dat', sendData)
			.then(() => {
				// 작업 리스트 전체 다시 로드
				eventBus.event.loadList(['process']);
			})
            .catch(error => {
                console.log(error);
            });
            
            this.dateEditMode = false;
            this.dateEditMode2 = false;
        }, //end: edit__date
		goModifyRequest: function () {
			if(confirm('수정시 지정된 작업 자및 진행내역 이 모두 초기화 됩니다, 계속 하시겠습니까?')) {
				this.$router.push({ name: 'ModifyWorkRequest', params: { id: this.id } });
			}
        }
    }
}
</script>