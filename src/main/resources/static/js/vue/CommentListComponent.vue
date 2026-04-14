<template>
<li class="group/comment py-5 border-b border-outline/5 last:border-0" :key="commentKey">
    <div class="flex gap-4">
        <!-- 아바타 영역 -->
        <div class="flex-shrink-0">
            <div 
                class="w-10 h-10 rounded-full flex items-center justify-center text-[10px] font-extrabold shadow-sm border border-black/5"
                :class="{
                    'bg-primary-fixed-dim text-white': comment.writer_type === 'MN',
                    'bg-orange-100 text-orange-600': comment.writer_type === 'PN',
                    'bg-purple-100 text-purple-600': comment.writer_type === 'DS',
                    'bg-blue-100 text-blue-600': comment.writer_type === 'PB'
                }"
            >
                {{writerType[comment.writer_type]}}
            </div>
        </div>

        <!-- 내용 영역 -->
        <div class="flex-grow min-w-0">
            <div class="flex items-center justify-between mb-1">
                <div class="flex items-center gap-2">
                    <span class="text-sm font-bold text-on-surface">{{comment.writer_name}}</span>
                    <span class="text-[11px] text-on-surface-variant font-medium">{{new Date(comment.regdate).toLocaleString()}}</span>
                </div>
                
                <!-- 액션 버튼 -->
                <div class="flex items-center gap-2 opacity-0 group-hover/comment:opacity-100 transition-opacity" v-if="loginInfo.userid === comment.writer && !isCompleteWork">
                    <button @click.prevent="editComment" class="p-1.5 rounded-lg hover:bg-surface-bright text-on-surface-variant hover:text-primary transition-all">
                        <span class="material-symbols-outlined text-[18px]">edit_note</span>
                    </button>
                    <button @click.prevent="comment__delete" class="p-1.5 rounded-lg hover:bg-error/5 text-on-surface-variant hover:text-error transition-all">
                        <span class="material-symbols-outlined text-[18px]">delete</span>
                    </button>
                </div>
            </div>

            <div class="text-sm leading-relaxed text-on-surface-variant mb-3 break-words" v-html="content"></div>

            <!-- 첨부파일 -->
            <div class="flex flex-wrap gap-2">
                <a 
                    v-for="file in comment.files" 
                    :key="file.id"
                    :href="'/file/download?file_id='+file.id" 
                    class="inline-flex items-center gap-2 px-3 py-1 bg-surface-bright border border-outline/10 rounded-lg text-xs font-semibold text-on-surface-variant hover:bg-primary-fixed-dim/5 hover:text-primary hover:border-primary-fixed-dim/20 transition-all"
                >
                    <span class="material-symbols-outlined text-[16px]">download</span>
                    {{file.original_filename}}
                </a>
            </div>
        </div>
    </div>
</li>
</template>

<script>
// 진행중인 작업 상세보기 > 댓글 목록 컴퍼넌트

const eventBus = useEventBusStore();

export default {
    mixins: [channelMixin],
    props: [
        'comment', //comment one
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
            return eventBus.workDetailOpener == 'complete' ? true : false;
            //return this.$store.getters['workStore/getWorkDetailOpener'] == 'complete' ? true : false;
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
</script>