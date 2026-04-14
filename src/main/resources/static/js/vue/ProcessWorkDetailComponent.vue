<template>
<div class="fixed inset-0 z-[100] flex items-center justify-center bg-on-background/20 backdrop-blur-sm p-4" @click.self="closeDetail" v-cloak v-if="isShow">
    <!-- Task Detail Modal -->
    <section class="relative w-full max-w-2xl bg-surface-container-lowest rounded-xl shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-300 flex flex-col max-h-[90vh]">
        <!-- Header Section -->
        <header class="flex items-start justify-between p-6 bg-surface-container-lowest border-b border-outline-variant/10 flex-shrink-0">
            <div class="space-y-1 flex-1">
                <p class="font-headline font-bold text-primary tracking-tight text-sm">
                    [{{detailData.site_name}}] {{detailData.request_id}}
                </p>
                <!-- 제목 수정 모드 -->
                <h2 v-if="!isTitEdit" 
                    @click="edit_mode('isTitEdit', 'request_title')" 
                    class="font-headline font-extrabold text-on-surface text-xl leading-tight cursor-pointer hover:text-primary transition-colors">
                    {{detailData.request_title}}
                </h2>
                <input v-else 
                    type="text" 
                    :value="detailData.request_title" 
                    ref="request_title" 
                    @blur="edit_request('isTitEdit', 'request_title')"
                    @keyup.enter="$event.target.blur()"
                    class="w-full font-headline font-extrabold text-on-surface text-xl border-b-2 border-primary outline-none bg-transparent">
            </div>
            <div class="flex items-center gap-2">
                <button @click="requestWorkDelete(detailData.id)" class="p-2 text-error hover:bg-error-container/10 rounded-full transition-colors flex items-center justify-center" title="삭제">
                    <span class="material-symbols-outlined text-[20px]">delete</span>
                </button>
                <button @click="closeDetail" class="p-2 text-on-surface-variant hover:bg-surface-container-high rounded-full transition-colors flex items-center justify-center" title="닫기">
                    <span class="material-symbols-outlined text-[24px]">close</span>
                </button>
            </div>
        </header>

        <!-- Content Area -->
        <div class="p-6 space-y-8 overflow-y-auto custom-scrollbar">
            <!-- 요약 정보 섹션 -->
            <div class="grid grid-cols-3 gap-4 bg-surface-container-low p-5 rounded-xl">
                <div class="space-y-1">
                    <p class="text-[11px] font-bold text-on-surface-variant uppercase tracking-wider">요청자</p>
                    <div class="flex items-center gap-2 cursor-pointer" @click="edit_mode('isRequestorEdit', 'requestor_name')">
                        <div class="w-6 h-6 rounded-full bg-primary-fixed-dim flex items-center justify-center text-[10px] text-white font-bold">
                            {{detailData.requestor_name && detailData.requestor_name.substring(0,1)}}
                        </div>
                        <span v-if="!isRequestorEdit" class="text-sm font-semibold text-on-surface">{{detailData.requestor_name}}</span>
                        <input v-else 
                            type="text" 
                            :value="detailData.requestor_name" 
                            ref="requestor_name" 
                            @blur="edit_request('isRequestorEdit', 'requestor_name')" 
                            @keyup.enter="$event.target.blur()"
                            class="text-sm font-semibold text-on-surface bg-transparent border-b border-primary outline-none w-20">
                    </div>
                </div>
                <div class="space-y-1">
                    <p class="text-[11px] font-bold text-on-surface-variant uppercase tracking-wider">업무요청일</p>
                    <div v-if="!dateEditMode" @click="dateEdit" class="text-sm font-semibold text-on-surface cursor-pointer hover:text-primary">
                        {{new Date(request_date).format('yyyy-MM-dd')}}
                    </div>
                    <div v-else class="flex items-center gap-1">
                        <input v-model="request_date" ref="dateInput" class="text-xs border rounded px-1 w-24">
                        <button @click="edit__date" class="text-[10px] bg-primary text-white px-1 rounded">확인</button>
                    </div>
                </div>
                <div class="space-y-1">
                    <p class="text-[11px] font-bold text-on-surface-variant uppercase tracking-wider">완료 요청일</p>
                    <div v-if="!dateEditMode2" @click="dateEdit2" class="text-sm font-semibold text-error cursor-pointer hover:underline">
                        ~ {{new Date(request_complete_date).format('yyyy-MM-dd')}}
                    </div>
                    <div v-else class="flex items-center gap-1">
                        <input v-model="request_complete_date" ref="dateInput2" class="text-xs border rounded px-1 w-24">
                        <button @click="edit__date" class="text-[10px] bg-primary text-white px-1 rounded">확인</button>
                    </div>
                </div>
            </div>

            <!-- 현재 진행 단계 섹션 -->
            <div class="space-y-4">
                <h3 class="font-headline font-bold text-sm text-on-surface flex items-center gap-2">
                    <span class="material-symbols-outlined text-primary text-lg">analytics</span>
                    현재 진행 단계
                </h3>
                <div class="grid grid-cols-4 gap-4">
                    <!-- 파트별 작업 컴포넌트 -->
                    <part-state-component 
                        v-for="part in partItems"
                        :part="part" 
                        :detail-data="detailData" 
                        :key="partStateComponentKey + '_'+part">
                    </part-state-component>

                    <!-- 최종 진행중/완료 카드 -->
                    <div class="flex flex-col items-center p-4 bg-surface-container-lowest rounded-xl border border-outline-variant/10 shadow-sm"
                        :class="{'border-primary border-2 shadow-md': workCompleted, 'opacity-60': !workCompleted}">
                        <div class="relative w-12 h-12 flex items-center justify-center rounded-full mb-3"
                            :class="workCompleted ? 'bg-primary text-on-primary' : 'bg-surface-variant text-on-surface-variant'">
                            <span class="material-symbols-outlined">{{workCompleted ? 'check_circle' : 'running_with_errors'}}</span>
                            <div class="absolute -bottom-1 -right-1 bg-primary text-[8px] font-bold text-white px-1.5 rounded-full border-2 border-white">
                                {{completePercent}}%
                            </div>
                        </div>
                        <span class="text-xs font-bold text-on-surface">진행중</span>
                        <span class="text-[10px] text-on-surface-variant">{{workCompleted ? '완료' : '결과대기'}}</span>
                    </div>
                </div>
            </div>

            <!-- 업무 내용 섹션 -->
            <div class="space-y-2">
                <h3 class="font-headline font-bold text-sm text-on-surface flex items-center gap-2">
                    <span class="material-symbols-outlined text-primary text-lg">description</span>
                    업무 내용
                </h3>
                <div v-if="!isContEdit" 
                    class="p-4 bg-surface-container-low rounded-xl text-sm text-on-surface-variant leading-relaxed cursor-pointer hover:bg-surface-container-high transition-colors"
                    v-html="detailContent"
                    @click="edit_mode('isContEdit', 'request_content')">
                </div>
                <div v-else class="space-y-2">
                    <textarea 
                        ref="request_content"
                        @blur="edit_request('isContEdit', 'request_content')"
                        class="w-full h-32 p-4 bg-surface-container-lowest border-2 border-primary rounded-xl text-sm outline-none resize-none mx-0">{{detailData.request_content}}</textarea>
                </div>
            </div>

            <!-- 작업 내역 섹션 (아코디언) -->
            <div class="space-y-0 pt-4 border-t border-outline-variant/10">
                <button class="w-full flex items-center justify-between py-2 hover:bg-surface-container-low/50 rounded-lg transition-colors group focus:outline-none" 
                    @click="$refs.commentWrap.classList.toggle('hidden'); $refs.chevron.classList.toggle('rotate-180')">
                    <h3 class="font-headline font-bold text-sm text-on-surface flex items-center gap-2">
                        <span class="material-symbols-outlined text-primary text-lg">history_edu</span>
                        작업내역
                    </h3>
                    <span ref="chevron" class="material-symbols-outlined transition-transform duration-200 text-on-surface-variant">expand_more</span>
                </button>
                <div ref="commentWrap" class="mt-4 hidden">
                    <comment-component 
                        v-if="Object.keys(detailData).length" 
                        :work-id="detailData.id">
                    </comment-component>
                </div>
            </div>
        </div>

        <!-- Footer / Action Area -->
        <footer class="p-6 bg-surface-container-low border-t border-outline-variant/10 flex justify-end gap-3 flex-shrink-0">
            <button @click="goModifyRequest" class="px-6 py-2.5 text-sm font-bold text-primary border border-primary/20 bg-white rounded-md hover:bg-primary/5 transition-colors">
                요청수정
            </button>
            <button v-show="!workCompleted" 
                @click="setWorkComplete" 
                class="px-6 py-2.5 text-sm font-bold bg-gradient-to-br from-primary to-primary-dim text-on-primary rounded-md shadow-md shadow-primary/20 hover:scale-[0.98] transition-transform">
                작업완료 업데이트
            </button>
        </footer>
    </section>

    <!-- 파트별 상세 팝업 -->
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