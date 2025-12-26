
<template>
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
                    <!-- <a href="#none" class="btn_small01" style="position: relative">파일업로드+
                        <input 
                            type="file" 
                            id="cfile" 
                            name="cfile"
                            @change="addFile" 
                            style="opacity: 0; width: 100%; position: absolute; left:0; top: 0">
                    </a> -->
                    <a href="#none" class="btn_small02 btn-regist" @click.prevent="comment__insert">{{registBtnText}}</a>
                    <a href="#none" class="btn_small01 btn-regist" v-if="mode=='edit'" @click.prevent="cancelEdit">취소</a>
                </div>
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