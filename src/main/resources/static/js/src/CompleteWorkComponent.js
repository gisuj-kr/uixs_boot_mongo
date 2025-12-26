var competeWorkListTemplate = `
<dl >
	<dt>
		<p>작업완료</p>
		<span class="list-count">{{totCompleteCnt}}건</span>
	</dt>
	<dd v-if="show">
	    <div class="work_factor complete" v-for="(item, index) in list">
	        <dl>
	            <dt>
	                <div>ID {{item.site_name != null ? '['+item.site_name+']' : ''}} {{item.request_id}}</div>
	                <ul class="work_label">
						<li><a href="#none" class="work_label04">작업완료</a></li>
					</ul>
	                <a href="#none" @click="openDetail(item.id)">{{item.request_title}}</a>
	            </dt>
	            <dd>
	                <div class="work_area" v-html="getReplaceContent(item.request_content)"></div>
	                <div class="date_area">
	                    <span>{{new Date(item.complete_date).format('yyyy.MM.dd')}} 완료</span>
	                </div>
	            </dd>
	        </dl>
	    </div>
	</dd>
</dl>`;

var CompleteWorkList = {
    template: competeWorkListTemplate,
    mixins:[channelMixin],
    props: ['propList', 'listAllChannel'],
    data: function () {
        return {
            list: this.propList,
            show: false,
            totCompleteCnt: 0
        }
    },
    created: function () {
		if (this.list.length > 0){
			this.show = true;
		}
	},
	mounted: function () {
		axios.post('/work/working_cnt.dat', {
			site_code: !this.listAllChannel ? localStorage.channel : null, 
			request_state: 'COMPLETE'
		})
		.then(response => {
 			this.totCompleteCnt = response.data;
		}).catch(error => {console.log(error)});
	},
    methods: {
		getReplaceContent: function (str) {
			return str.replace(/\r\n/g, '<br>');
		},
        openDetail: function (id) {
			this.$emit('show-detail', id, 'complete');
			//uijs.processWork.detailPopup(id, 'COMPLETE')
		}
    }
}