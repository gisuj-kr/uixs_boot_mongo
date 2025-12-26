<template>
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
    <div style="text-align: right; margin-top: 10px;" v-if="loginInfo.userid === comment.writer && !isCompleteWork">
        <a href="#" class="btn_small01 edit" @click.prevent="editComment">수정</a>
        <a href="#" class="btn_small01 delete" @click.prevent="comment__delete">삭제</a>
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