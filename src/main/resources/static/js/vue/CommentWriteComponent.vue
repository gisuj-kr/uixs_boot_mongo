
<template>
<div class="bg-surface-bright/50 rounded-2xl p-4 border border-outline/10 mb-6">
    <form ref="form" name="comment_form" id="comment_form" method="post" enctype="multipart/form-data" @submit.prevent>
        <div class="flex items-center gap-3 mb-4">
            <span class="material-symbols-outlined text-primary-fixed-dim">account_circle</span>
            <select 
                class="bg-transparent text-sm font-semibold text-on-surface border-none focus:ring-0 cursor-pointer" 
                name="writer_type" 
                v-model="form.writerType"
            >
                <option value="MN">담당자</option>
                <option value="PN">기획</option>
                <option value="DS">디자인</option>
                <option value="PB">퍼블</option>
            </select>
        </div>
        
        <div class="relative group">
            <textarea 
                class="w-full bg-white rounded-xl border-outline/20 p-4 min-h-[120px] text-sm text-on-surface placeholder:text-on-surface-variant/50 focus:border-primary-fixed-dim focus:ring-4 focus:ring-primary-fixed-dim/10 transition-all resize-none shadow-sm"
                placeholder="내용을 입력해주세요" 
                name="content" 
                v-model="form.content" 
                ref="content"
            ></textarea>
            
            <div class="flex flex-wrap gap-2 mt-3 px-1">
                <div v-for="(file, index) in savedFiles" :key="'saved_'+file.id" 
                    class="flex items-center gap-2 bg-secondary-fixed/10 px-3 py-1.5 rounded-full border border-secondary-fixed/20 group/file">
                    <span class="material-symbols-outlined text-sm text-secondary-fixed">attach_file</span>
                    <span class="text-xs font-medium text-secondary-fixed">{{file.original_filename}}</span>
                    <button @click.prevent="deleteFile(index, file.id)" class="material-symbols-outlined text-xs text-on-surface-variant hover:text-error transition-colors">close</button>
                </div>
                <div v-for="(file, index) in form.files" :key="'new_'+index" 
                    class="flex items-center gap-2 bg-primary-fixed-dim/10 px-3 py-1.5 rounded-full border border-primary-fixed-dim/20 group/file">
                    <span class="material-symbols-outlined text-sm text-primary-fixed-dim">add_circle</span>
                    <span class="text-xs font-medium text-primary-fixed-dim">{{file.name}}</span>
                    <button @click.prevent="deleteFile(index)" class="material-symbols-outlined text-xs text-on-surface-variant hover:text-error transition-colors">close</button>
                </div>
            </div>

            <div class="flex justify-end gap-2 mt-4" v-if="!isCompleteWork">
                <button 
                    v-if="mode=='edit'" 
                    @click.prevent="cancelEdit"
                    class="px-5 py-2.5 rounded-xl text-sm font-bold text-on-surface-variant hover:bg-surface-variant/50 transition-all"
                >
                    취소
                </button>
                <button 
                    @click.prevent="comment__insert" 
                    class="px-6 py-2.5 rounded-xl text-sm font-extrabold text-white bg-primary-fixed-dim shadow-lg shadow-primary-fixed-dim/20 hover:scale-[1.02] active:scale-[0.98] transition-all"
                >
                    {{registBtnText}}
                </button>
            </div>
        </div>
    </form>
</div>
</template>

<script>
const eventBus = useEventBusStore();

// 진행중인 작업 상세보기 > 댓글 쓰기 컴퍼넌트
export default {
    mixins: [channelMixin],
    props: ['workId', 'mode', 'editItem'],
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
        switch(this.loginInfo.part) {
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
            return eventBus.workDetailOpener == 'complete' ? true : false;
            //return this.$store.getters['workStore/getWorkDetailOpener'] == 'complete' ? true : false;
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
            
            sendFormData.append('writer', this.loginInfo.userid);
            sendFormData.append('writer_type', this.form.writerType);
            sendFormData.append('ref_id', this.form.refId);
            sendFormData.append('content', this.form.content);
            sendFormData.append('writer_name', this.loginInfo.username);
            // 파일 삽입
            // this.form.files.forEach(function (file) {
            //     sendFormData.append('files', file);
            // });
            
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
</script>