<template>
<ul>
    <li class="write">
        <write-component 
            :work-id="workId" 
            :mode="writeMode" 
            :edit-item="editItem"
            :key="writeKey"
            @edit-cancel="editCancel"
            @list-read="list__read"
            v-if="loginInfo">
        </write-component>
    </li>
    <list-component 
        :comment-key="commentKey"
        :key="comment.id"
        :comment="comment" 
        :index="index"
        v-for="(comment, index) in commentList"
        @edit-comment="editComment"
        @remove-comment-item="removeCommentItem"
        v-if="loginInfo">
    </list-component>
</ul>
</template>
	
<script>
// 댓글 쓰기 컴포넌트
const WriteComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/CommentWriteComponent.vue',vue3LoadOption);
});

// 댓글 목록 컴포넌트
const ListComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/CommentListComponent.vue',vue3LoadOption);
});

export default {
    props: ['workId'],
    components: {
        'write-component': WriteComponent,
        'list-component': ListComponent,
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
        // this.logininfo = this.loginInfo;
        
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
</script>