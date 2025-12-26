<template>
    <dl>
        <dt>
            <p>작업진행내역</p>
            <span class="list-count">{{totWorkingCnt}}건</span>
        </dt>
        <dd>
            <div class="work_factor working" v-for="(item, index) in selectedList" :key="index">
                <dl>
                    <dt>
                        <div>ID {{item.site_name != null ? '['+item.site_name+']' : ''}} {{item.request_id}}</div>
                        <a href="#none" @click="openDetail(item.id)">{{item.request_title}}</a>
                    </dt>
                    <dd>
                        <div class="work_area" v-html="getReplaceContent(item.request_content)"></div>
                        <div class="date_area">
                            <span>~ {{new Date(item.request_complete_date).format('yyyy.MM.dd')}} 까지</span>
                        </div>
                        <div class="state_step_area">
                            <template v-for="part in item.part">
                                <template v-if="item.need_workers.includes(part.name)">
                                <span
                                    v-if="part.state == 'CONFIRM'" 
                                    class="step02" 
                                    style="background: #e55d9c; margin-left: 5px">
                                    {{partKr[part.name]}} {{stateKr[part.state]}}
                                </span>
                                <span
                                    v-if="part.state == 'COMPLETE'"
                                    class="step02" 
                                    style="background: #e55d9c; margin-left: 5px">
                                    {{partKr[part.name]}} {{stateKr[part.state]}}
                                </span>
                                <span
                                    v-else-if="part.state == 'PENDING'"
                                    class="step02" 
                                    style="background: #ff6637; margin-left: 5px">
                                    {{partKr[part.name]}} {{stateKr[part.state]}}
                                </span>
                                <span
                                    v-else-if="part.state == 'WORKING'"
                                    class="step02" 
                                    style="background: #0565f0; margin-left: 5px">
                                    {{partKr[part.name]}} {{stateKr[part.state]}}
                                </span>
                                <span
                                    v-else-if="part.state == 'EDIT'"
                                    class="step02" 
                                    style="background: #0565f0; margin-left: 5px">
                                    {{partKr[part.name]}} {{stateKr[part.state]}}
                                </span>
                                </template>
                            </template>
                        </div>
                    </dd>
                </dl>
            </div>
        </dd>
    </dl>
</template>

<script>
export default {
    // template: processWorkListTemplate,
    // mixins:[channelMixin],
    props: ['propList', 'listAllChannel', 'searchKey', 'searchWord'],
    data: function () {
        return {
            list: this.propList,
            // selectedList: this.propList,
            partKr: {
                "plan": '기획',
                "design": '디자인',
                "publish": '퍼블'
            },
            stateKr: {
				PENDING: '대기중',
				WORKING: '진행중',
				CONFIRM: '컨펌중',
				EDIT: '수정중',
				COMPLETE: '완료'
			},
            totWorkingCnt: 0
        }
    },
    mounted() {
        console.log('processWorkList mounted');
        // 작업진행내역 전체 건수
		axios.post(API_URL+'/work/working_cnt.dat', {
			site_code: this.listAllChannel !== 'all' ? localStorage.channel : null, 
			request_state: 'WORKING',
            search_key: this.searchKey,
            search_word: this.searchWord
		})
		.then(response => {
			this.totWorkingCnt = response.data;
		}).catch(error => {console.log(error)});
	},
    computed: {
        selectedList: function () {
            return this.list.map(item => {
                const newItem = {...item}; // item의 복사본 생성
                const dupIndex = [];
                const newPart = [];
                
                // 중복파트가 있으면 해당 파트의 배열 인덱스 따로 저장
                if (newItem.part != null && newItem.part.length > 0) {
                    if (Array.isArray(newItem.part)) {
                        ['plan', 'design', 'publish'].forEach(type => {
                            const part = newItem.part
                                .filter(part => part.name === type)
                                .sort((a, b) => b - a)[0];
                                
                            if (part) {
                                newPart.push(part);
                            }
                        });
                    }

                    newItem.part = newPart;

                    /*
                    item.part.forEach((part, index) => {
                        if(!newPart.some(npart => npart.name == part.name && npart.worker == part.worker)) {
                            dupIndex.push(index);
                        }
                    })
                    
                    dupIndex.forEach(i => {
                        item.part.splice(i, 1);	
                    })
                    */
                }

                return newItem;
            });
        }
    },
    watch: {
    },
    methods: {
		
		getReplaceContent: function (str) {
			return str.replace(/\r\n/g, '<br>');
		},
        openDetail: function (id) {
			//uijs.processWork.detailPopup(id);
			this.$emit('show-detail', id);
		},
		
    }
}
</script>