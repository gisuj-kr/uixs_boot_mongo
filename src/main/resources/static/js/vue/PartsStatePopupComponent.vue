<!-- // 파트별 작업현황 등록 팝업 컴포넌트 -->
<template>
    <div id="modal-set-work-state" class="z-[100] fixed inset-0 flex items-center justify-center bg-on-background/20 backdrop-blur-sm p-4" @click.self="closeWithDim">
        <div class="bg-surface-container-lowest w-full max-w-2xl rounded-xl shadow-[0_24px_48px_-12px_rgba(44,47,49,0.06)] overflow-hidden flex flex-col max-h-[95vh] animate-in fade-in zoom-in duration-300">
            <!-- Header -->
            <header class="flex items-center justify-between px-6 py-3 bg-surface border-b border-outline-variant/15">
                <h1 class="text-xl font-bold font-headline tracking-tight text-primary">작업 진행 정보 입력 ({{partToKr[thisPart]}})</h1>
                <button class="p-2 text-on-surface-variant hover:bg-surface-container transition-colors rounded-full active:scale-95" @click="closeWithDim">
                    <span class="material-symbols-outlined block">close</span>
                </button>
            </header>

            <!-- Assignee Tab Bar -->
            <nav class="bg-surface px-6 pt-1 flex items-center gap-2 border-b border-outline-variant/15">
                <div class="flex items-center gap-1 overflow-x-auto no-scrollbar pb-px flex-1">
                    <button v-for="(data, index) in partData" :key="index"
                        class="px-5 py-3 text-sm whitespace-nowrap transition-colors relative"
                        :class="activeTabIndex === index ? 'font-bold border-b-2 border-primary text-primary' : 'font-medium border-b-2 border-transparent text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high/30'"
                        @click="selectTab(index)">
                        {{ data.worker || '지정안됨' }}
                    </button>
                </div>
                <button class="mb-2 p-1.5 rounded-full bg-primary/5 text-primary hover:bg-primary/10 transition-all active:scale-90 flex items-center justify-center shrink-0" 
                    title="담당자 추가" @click="addWorkerTab">
                    <span class="material-symbols-outlined text-[20px]">add</span>
                </button>
            </nav>

            <!-- Modal Content Scrollable Area -->
            <div ref="scrollArea" class="flex-1 overflow-y-auto p-6 space-y-5 no-scrollbar pt-0">
                <template v-if="partData[activeTabIndex]">
                    <!-- Assignee & Status Grid -->
                    <section class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div class="space-y-2">
                            <label class="block text-xs font-semibold font-label text-on-surface-variant uppercase tracking-wider">담당자</label>
                            <div class="relative">
                                <select v-model="partData[activeTabIndex].worker" 
                                    class="w-full bg-surface-container-low border-b-2 border-outline-variant/30 focus:border-primary focus:ring-0 rounded-t-lg py-2.5 px-4 appearance-none transition-all text-sm font-bold text-on-surface"
                                    :class="partData[activeTabIndex].worker !== '지정안됨' ? 'cursor-default' : ''">
                                    <option value="지정안됨">담당자 지정</option>
                                    <option v-for="mem in workers" :key="mem.username" :value="mem.username">
                                        {{ mem.username }}
                                    </option>
                                </select>
                                <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-on-surface-variant/50">
                                    {{ partData[activeTabIndex].worker !== '지정안됨' ? 'person' : 'expand_more' }}
                                </span>
                            </div>
                        </div>
                        <div class="space-y-2">
                            <label class="block text-xs font-semibold font-label text-on-surface-variant uppercase tracking-wider">작업현황</label>
                            <div class="relative">
                                <select v-model="partData[activeTabIndex].state" @change="onStateChange"
                                    class="w-full bg-surface-container-low border-b-2 border-outline-variant/30 focus:border-primary focus:ring-0 rounded-t-lg py-2.5 px-4 appearance-none transition-all text-sm font-medium">
                                    <option value="PENDING">대기중</option>
                                    <option value="WORKING">작업중</option>
                                    <option value="CONFIRM">컨펌중</option>
                                    <option value="EDIT">수정중</option>
                                    <option value="COMPLETE">완료</option>
                                    <option value="NONE">해당없음 (공수제외)</option>
                                </select>
                                <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-on-surface-variant">expand_more</span>
                            </div>
                        </div>
                    </section>

                    <!-- Date Range Grid -->
                    <section class="grid grid-cols-1 md:grid-cols-2 gap-6 !mt-0">
                        <div class="space-y-2">
                            <label class="block text-xs font-semibold font-label text-on-surface-variant uppercase tracking-wider">업무요청일</label>
                            <div class="relative">
                                <input v-model="partData[activeTabIndex].part_work_rday" 
                                    class="w-full bg-surface-container-low border-b-2 border-outline-variant/30 focus:border-primary focus:ring-0 rounded-t-lg py-2.5 px-4 text-sm" type="date"/>
                                <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-on-surface-variant text-lg">calendar_today</span>
                            </div>
                        </div>
                        <div class="space-y-2">
                            <label class="block text-xs font-semibold font-label text-on-surface-variant uppercase tracking-wider">완료요청일</label>
                            <div class="relative">
                                <input v-model="partData[activeTabIndex].part_work_crday" 
                                    class="w-full bg-surface-container-low border-b-2 border-outline-variant/30 focus:border-primary focus:ring-0 rounded-t-lg py-2.5 px-4 text-sm" type="date"/>
                                <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-on-surface-variant text-lg">event_available</span>
                            </div>
                        </div>
                    </section>

                    <!-- Input Area -->
                    <section id="input-form-area" class="bg-surface-container-low p-6 rounded-xl border border-outline-variant/10 space-y-4">
                        <div class="flex items-center justify-between mb-1">
                            <div class="flex items-center gap-2">
                                <span class="material-symbols-outlined text-primary text-xl">{{ editingIndex !== null ? 'edit_note' : 'add_task' }}</span>
                                <h2 class="text-sm font-bold font-headline text-on-surface">
                                    {{ editingIndex !== null ? '작업내용 수정' : '작업내용 상세 입력' }}
                                </h2>
                            </div>
                            <button v-if="editingIndex !== null" @click="resetEntryForm"
                                class="text-[11px] font-bold text-on-surface-variant hover:text-primary flex items-center gap-1 transition-colors">
                                <span class="material-symbols-outlined text-sm">close</span> 수정 취소
                            </button>
                        </div>
                        <div class="space-y-1 !mt-0">
                            <label class="block text-xs font-semibold font-label text-on-surface-variant">작업 상세 설명</label>
                            <textarea v-model="entryForm.content" 
                                class="w-full bg-surface-container-lowest border-b-2 border-outline-variant/30 focus:border-primary focus:ring-0 rounded-t-lg p-3 text-sm resize-none" 
                                placeholder="작업한 상세 내용을 입력하세요..." rows="3"></textarea>
                        </div>
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div class="space-y-1">
                                <label class="block text-xs font-semibold font-label text-on-surface-variant">작업 착수일</label>
                                <div class="relative">
                                    <input v-model="entryForm.sday" class="w-full bg-surface-container-lowest border-b-2 border-outline-variant/20 focus:border-primary focus:ring-0 rounded-t-md p-2 text-sm" type="date"/>
                                </div>
                            </div>
                            <div class="space-y-1">
                                <label class="block text-xs font-semibold font-label text-on-surface-variant">작업 종료일</label>
                                <div class="relative">
                                    <input v-model="entryForm.eday" class="w-full bg-surface-container-lowest border-b-2 border-outline-variant/20 focus:border-primary focus:ring-0 rounded-t-md p-2 text-sm" type="date"/>
                                </div>
                            </div>
                        </div>
                        <div class="flex justify-between items-center pt-2">
                            <button v-if="activeTabIndex > 0 || partData.length > 1" @click="delWorker"
                                class="text-xs font-bold text-error hover:bg-error/5 px-3 py-1.5 rounded-md transition-colors flex items-center gap-1">
                                <span class="material-symbols-outlined text-sm">person_remove</span> 담당자 삭제
                            </button>
                            <div class="flex-1"></div>
                            <button @click="addOrUpdateEntry"
                                class="btn-primary-gradient px-6 py-2.5 rounded-md text-on-primary text-sm font-bold shadow-sm hover:brightness-110 active:scale-95 transition-all flex items-center gap-2">
                                <span class="material-symbols-outlined text-lg">{{ editingIndex !== null ? 'check' : 'add' }}</span>
                                {{ editingIndex !== null ? '수정 완료' : '기록 추가' }}
                            </button>
                        </div>
                    </section>

                    <!-- History List -->
                    <section class="space-y-4">
                        <div class="flex items-center justify-between">
                            <h3 class="text-sm font-bold font-headline text-on-surface">작업 이력 내역 ({{ partData[activeTabIndex].worker }})</h3>
                            <span class="px-2 py-0.5 bg-tertiary-container text-on-tertiary-container text-[10px] font-bold rounded-full">
                                {{ (partData[activeTabIndex].work_content || []).length }} ENTRIES
                            </span>
                        </div>
                        <div class="space-y-3">
                            <div v-for="(history, hIndex) in partData[activeTabIndex].work_content" :key="hIndex"
                                class="bg-surface-container-lowest p-4 rounded-xl border border-outline-variant/10 hover:border-primary/20 transition-all group shadow-sm"
                                :class="editingIndex === hIndex ? 'ring-2 ring-primary/30 border-primary/30' : ''">
                                <div class="flex justify-between items-start mb-2">
                                    <span class="text-[10px] font-bold text-primary px-2 py-0.5 bg-primary/10 rounded uppercase">
                                        {{ history.isOrigin ? 'New Record' : 'Saved History' }}
                                    </span>
                                    <div class="flex items-center gap-3">
                                        <span class="text-xs text-on-surface-variant font-medium">
                                            {{ history.part_work_sday || '?' }} - {{ history.part_work_eday || '?' }}
                                        </span>
                                        <div class="flex items-center gap-1.5 ">
                                            <button @click="loadEntryForEdit(hIndex)" class="text-on-surface-variant/60 hover:text-primary transition-colors p-1" title="수정">
                                                <span class="material-symbols-outlined text-[18px]">edit</span>
                                            </button>
                                            <button @click="removeEntry(hIndex)" class="text-on-surface-variant/60 hover:text-error transition-colors p-1" title="삭제">
                                                <span class="material-symbols-outlined text-[18px]">delete</span>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                                <p class="text-sm text-on-surface leading-relaxed">{{ history.content || '(내용 없음)' }}</p>
                            </div>
                            <div v-if="!(partData[activeTabIndex].work_content || []).length" 
                                class="py-10 text-center border-2 border-dashed border-outline-variant/20 rounded-xl">
                                <span class="material-symbols-outlined text-4xl text-outline-variant/30 block mb-2">history</span>
                                <p class="text-sm text-on-surface-variant text-center">등록된 작업 이력이 없습니다.</p>
                            </div>
                        </div>
                    </section>
                </template>
            </div>

            <!-- Footer -->
            <footer class="px-6 py-4 bg-surface-container border-t border-outline-variant/10 flex items-center justify-end gap-3 shrink-0 shadow-[0_-4px_12px_rgba(0,0,0,0.03)]">
                <button @click="closeWithDim" class="px-6 py-3 rounded-md text-on-surface-variant font-semibold text-sm hover:bg-surface-container-high transition-all active:scale-95">
                    취소
                </button>
                <button @click="editWorkState" class="btn-primary-gradient px-8 py-3 rounded-md text-on-primary font-bold text-sm shadow-md hover:shadow-lg hover:brightness-110 active:scale-95 transition-all">
                    확인 및 저장
                </button>
            </footer>
        </div>
    </div>
</template>

<script>
const eventBus = useEventBusStore();

export default {
    props: ['id', 'thisPart', 'detailData'],
    data: function() {
        return {
            activeTabIndex: 0,
            partToKr: {
                plan: '기획',
                design: '디자인',
                publish: '퍼블'
            },
            workers: [],
            partData: [],
            entryForm: {
                content: '',
                sday: '',
                eday: ''
            },
            editingIndex: null
        }
    },
    mounted: function () {
        (async () => {
            // 1. 담당 멤버 로드
            await axios.get(API_URL + '/member/memberWithPart.dat', {params: {part: this.thisPart.toUpperCase()}})
                .then(response => {
                    this.workers = response.data;
                })
                .catch(error => console.log(error));
            
            // 2. 기존 데이터 파싱
            if (this.detailData.part != null) {
                const filtered = this.detailData.part.filter(item => item.name == this.thisPart);
                
                if (filtered.length > 0) {
                    this.partData = filtered.map(item => ({
                        ...item,
                        part_work_rday: this.formatDateForInput(item.part_work_rday || this.detailData.request_date),
                        part_work_crday: this.formatDateForInput(item.part_work_crday || this.detailData.request_complete_date),
                        work_content: (item.work_content || []).map(h => ({
                            ...h,
                            part_work_sday: this.formatDateForInput(h.part_work_sday),
                            part_work_eday: this.formatDateForInput(h.part_work_eday)
                        }))
                    }));
                }
            }

            // 3. 데이터가 없을 경우 기본 템플릿 생성
            if (this.partData.length === 0) {
                const oldState = this.detailData[this.thisPart + '_state'];
                this.partData.push({
                    name: this.thisPart,
                    worker: oldState?.worker || '지정안됨',
                    state: oldState?.state || 'PENDING',
                    bigo: oldState?.bigo || '',
                    part_work_rday: this.formatDateForInput(this.detailData.request_date),
                    part_work_crday: this.formatDateForInput(this.detailData.request_complete_date),
                    work_content: (oldState?.work_sdate && oldState?.work_edate) ? [{
                        worker: oldState.worker,
                        content: '',
                        part_work_sday: this.formatDateForInput(oldState.work_sdate),
                        part_work_eday: this.formatDateForInput(oldState.work_edate),
                        isOrigin: false
                    }] : []
                });
            }
        })();
    },
    methods: {
        formatDateForInput(date) {
            if (!date) return '';
            const d = new Date(date);
            if (isNaN(d.getTime())) return '';
            return d.toISOString().split('T')[0];
        },
        selectTab(index) {
            this.activeTabIndex = index;
            this.resetEntryForm();
        },
        addWorkerTab() {
            const newWorker = {
                name: this.thisPart,
                worker: '지정안됨',
                state: 'PENDING',
                bigo: '',
                work_content: [],
                part_work_rday: this.formatDateForInput(this.detailData.request_date),
                part_work_crday: this.formatDateForInput(this.detailData.request_complete_date),
            };
            this.partData.push(newWorker);
            this.activeTabIndex = this.partData.length - 1;
            this.resetEntryForm();
        },
        delWorker() {
            if (confirm('해당 담당자 정보를 삭제하시겠습니까?')) {
                this.partData.splice(this.activeTabIndex, 1);
                if (this.activeTabIndex >= this.partData.length) {
                    this.activeTabIndex = Math.max(0, this.partData.length - 1);
                }
                // 담당자가 하나도 없으면 기본 생성
                if (this.partData.length === 0) {
                    this.addWorkerTab();
                }
                this.resetEntryForm();
            }
        },
        addOrUpdateEntry() {
            const currentWorker = this.partData[this.activeTabIndex];
            if (!this.entryForm.content.trim() && !this.entryForm.sday && !this.entryForm.eday) {
                alert('작업 내용을 입력하거나 날짜를 선택해주세요.');
                return;
            }

            const entry = {
                worker: currentWorker.worker,
                content: this.entryForm.content,
                part_work_sday: this.entryForm.sday,
                part_work_eday: this.entryForm.eday,
                isOrigin: true
            };

            if (this.editingIndex !== null) {
                currentWorker.work_content.splice(this.editingIndex, 1, entry);
            } else {
                currentWorker.work_content.push(entry);
            }

            this.resetEntryForm();
            
            // 기록이 추가/수정된 즉시 저장하지만 팝업은 닫지 않음
            this.editWorkState(false);
        },
        loadEntryForEdit(index) {
            const entry = this.partData[this.activeTabIndex].work_content[index];
            this.entryForm = {
                content: entry.content,
                sday: entry.part_work_sday,
                eday: entry.part_work_eday
            };
            this.editingIndex = index;
            
            // 상단으로 부드럽게 스크롤
            this.$nextTick(() => {
                if (this.$refs.scrollArea) {
                    this.$refs.scrollArea.scrollTo({ top: 0, behavior: 'smooth' });
                }
            });
        },
        removeEntry(index) {
            if (confirm('해당 이력을 삭제하시겠습니까?')) {
                this.partData[this.activeTabIndex].work_content.splice(index, 1);
                if (this.editingIndex === index) this.resetEntryForm();
                
                // 삭제 후 즉시 저장, 팝업은 유지
                this.editWorkState(false);
            }
        },
        resetEntryForm() {
            this.entryForm = { content: '', sday: '', eday: '' };
            this.editingIndex = null;
        },
        onStateChange(e) {
            if (e.target.value === 'NONE') {
                if (confirm(this.partToKr[this.thisPart] + ' 파트가 전체 공수에서 제외됩니다. 진행하시겠습니까?')) {
                    this.removeFromProject();
                } else {
                    this.partData[this.activeTabIndex].state = 'PENDING';
                }
            }
        },
        async removeFromProject() {
            const needWorkers = (this.detailData.need_workers || []).filter(p => p !== this.thisPart);
            const part = (this.detailData.part || []).filter(p => p.name !== this.thisPart);
            
            try {
                const response = await axios.post(API_URL + '/work/modify_request.dat', {
                    id: this.id,
                    need_workers: needWorkers,
                    part: part
                });
                if (response.data) {
                    eventBus.event.read__data();
                    eventBus.event.close__work_state_popup();
                }
            } catch (error) {
                console.error('Error removing part:', error);
            }
        },
        editWorkState(closeAfterSave = true) {
            if (this.detailData.request_state === 'COMPLETE') {
                alert('완료된 작업은 수정할 수 없습니다.');
                return;
            }

            // 검증 로직
            for (let part of this.partData) {
                if (part.state === 'COMPLETE') {
                    if (part.worker === '지정안됨') {
                        alert(`담당자가 지정되지 않은 항목이 있습니다.`);
                        return;
                    }
                    if (part.work_content.length === 0) {
                        alert(`${part.worker} 담당자의 작업 완료 기록이 없습니다.`);
                        return;
                    }
                }
                // 담당자 동기화
                part.work_content.forEach(h => {
                    h.worker = part.worker;
                });
            }

            // 전체 데이터 병합 (다른 파트 데이터 유지)
            let otherParts = (this.detailData.part || []).filter(p => p.name !== this.thisPart);
            const finalData = [...otherParts, ...this.partData];

            axios.post(API_URL + '/work/modify_request.dat', {
                id: this.id,
                part: finalData
            })
            .then(() => {
                eventBus.event.read__data();
                
                // 파라미터에 따라 팝업 닫기 또는 알림 처리
                if (closeAfterSave === true) {
                    eventBus.event.close__work_state_popup();
                } else {
                    alert('저장되었습니다.');
                }
            })
            .catch(err => console.error(err));
        },
        closeWithDim: function () {
            eventBus.event.close__work_state_popup();
        }
    }
}
</script>

<style scoped>
.btn-primary-gradient {
    background: linear-gradient(135deg, #215aaa 0%, #73a3f8 100%);
}
.no-scrollbar::-webkit-scrollbar {
    display: none;
}
.no-scrollbar {
    -ms-overflow-style: none;
    scrollbar-width: none;
}

/* 아이콘 겹침 현상 해결 (기존 CSS 및 Tailwind Forms 대응) */
select {
    appearance: none !important;
    -webkit-appearance: none !important;
    background-image: none !important; /* 기본 화살표 제거 */
    cursor: pointer;
}

/* 날짜 선택기 기본 아이콘 숨기기 및 영역 확장 */
input[type="date"]::-webkit-calendar-picker-indicator {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0;
    cursor: pointer;
    opacity: 0; /* 투명하게 만들어 소재 아이콘이 보이게 함 */
    z-index: 10;
}

/* 날짜 입력칸 내 텍스트 정렬 보정 및 상대 경로 설정 */
input[type="date"] {
    position: relative;
    display: flex;
    align-items: center;
}

/* 포커스 시 배경색 미세 조정 */
input:focus, select:focus, textarea:focus {
    background-color: white !important;
}
</style>