<template>
    <!--container-->
    <div class="container">
        <form name="work_request_form" id="work_request_form" method="post" onsubmit="return false;">
        <input type="hidden" name="userid" :value="loginInfo.userid" />
        <div class="content">
            <div class="content_inner">
                <div class="tit_area">
                    <h2>작업요청하기</h2>
                </div>
                
                <div class="input_section mt20">
                    <ul class="input_wrap" style="padding-top: 0;">
						<li>
							<div class="request-pg-checkbox-group">
								<div v-for="channel in channels" :key="channel.code">
									<input type="checkbox" 
										name="channel" 
										:id="channel.code" 
										:value="channel.code" 
										v-model="checkedChannelCodes"
										readonly> <label :for="channel.code">{{channel.name}}</label>
								</div>
							</div>
							<div v-if="Object.keys(checkedChannel).length > 0 && checkedChannelCodes.includes('SH_PA')" class="chennel-sub-radio">
								<template v-for="(tab, tabIndex) in checkedChannel.ia_tabs" :key="tabIndex">
									<div v-if="tab.ia_file.trim() != ''">
										<input type="radio" 
											name="channel-sub"
											:value="tabIndex" 
											:id="tab.tab_name"
											v-model="checkedChannelSubIdx"> <label :for="tab.tab_name">파트너 {{ tab.tab_name }}</label>
									</div>
								</template>
							</div>
						</li>
                        <li class="input_area" style="margin-top: 20px" v-if="checkedChannels.length === 1 && checkedChannelCodes.includes('SH_PA')">
							<div class="input_area-label-group">
                            	<label for="request_title">경로</label> <button type="button" class="btn-open-ia" @click="openTreeModal">IA 열기</button>
							</div>
                            <input type="text" 
								id="path" 
								name="path" 
								v-model="formData.path" 
								placeholder="경로를 선택 or 입력 하세요">
                        </li>
                        <li class="input_area" style="margin-top: 20px">
                            <label for="request_title">제목</label>
                            <input type="text" 
								id="request_title" 
								name="request_title" 
								placeholder="제목을 입력하세요" 
								v-model="formData.request_title">
                        </li>
                        <li class="input_area">
                            <label for="request_content">업무(요건) 내용</label>
                            <textarea 
								id="request_content" 
								name="request_content" 
								placeholder="내용을 입력해주세요." 
								v-model="formData.request_content"></textarea>
                        </li>
                        <li class="input_area" style="margin-top: 20px">
                            <label for="request_title">요청자</label>
                            <input type="text" 
								id="requestor_name" 
								name="requestor_name" 
								placeholder="요청자를 입력하세요" 
								v-model="formData.requestor_name">
                        </li>
                        <li class="input_area half">
                            <div class="input-inner half">
                                <label for="request_date">업무 요청일</label>
                                <input type="text" 
									id="request_date" 
									name="request_date" 
									placeholder="날짜를 선택해주세요" 
									class="datepick"  
									data-type="date" 
									autocomplete="off"
									:value="formData.request_date">
                            </div>
                            <div class="input-inner half">
                                <label for="request_complete_date">완료 요청일</label>
                                <input type="text" 
									id="request_complete_date" 
									name="request_complete_date" 
									placeholder="날짜를 선택해주세요" 
									class="datepick"  
									data-type="date" 
									autocomplete="off"
									:value="formData.request_complete_date">
                            </div>
                        </li>
                        <li class="input_area">
                            <label  class="mb0">예상공수</label>
                            <ul class="box_type_check">
                                <li>
                                    <input type="checkbox" v-model="need_workers" name="need_workers" id="team01" value="plan">
                                    <label for="team01">기획</label>
                                </li>
                                <li>
                                    <input type="checkbox" v-model="need_workers" name="need_workers" id="team02" value="design">
                                    <label for="team02">디자인</label>
                                </li>
                                <li>
                                    <input type="checkbox" v-model="need_workers" name="need_workers" id="team03" value="publish">
                                    <label for="team03">퍼블</label>
                                </li>
                            </ul>
                        </li>
                        <li class="input_area edit_pop" style="width: 100%; box-shadow: none; padding: 0 0; top: 0; display: none;">
                            <label for="">첨부파일
                                <span>
                                    <button @click="addFileInput" class="btn_tiny01">파일추가</button>
                                </span>
                            </label>
                            
                            <div class="edit_data" v-for="(file, index) in files" :key="file.id">
                                <input type="file" :id="'file_'+ file.id" v-on:change="fileChange" readonly>
                                <label :for="'file_'+ file.id">파일선택</label>
                                
                                <div class="file_text">
                                    <input type="text" :id="'file_'+ file.id + '_text'" placeholder="첨부파일 설명">
                                    <a href="#none" class="btn_small02" @click="delFileInput(index)">삭제</a> 
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
                <div class="btn_area">
                    <router-link to="/work" class="btn_large_type02">취소하기</router-link>
                    <button type="button" class="btn_large_type01" @click.prevent="modify__request">수정하기</button>
                </div>
            </div>
        </div>
        </form>
    </div>
    <!--// container-->

	<tree-component 
		v-if="showTreeModal"  
		@close="showTreeModal = false" 
		:checked-channel="checkedChannel"
		:checked-channel-sub-idx="checkedChannelSubIdx"
	></tree-component>
	
</template>

<script>
const TreeComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/TreeComponent.vue',vue3LoadOption);
});

export default {
	components: {
		TreeComponent
	},
    mixins: [channelMixin],
    data() {
		return {
			id: null,
			files: [],
			lastFilesIndex: 1,
			clicked: false,
			need_workers: [],
			form_channel: [],
			showTreeModal: false,  // 모달 표시 여부
			checkedChannel: {},  // 채널 선택
			checkedChannels: [],  // 채널들 - checkbox 선택
			checkedChannelCodes: [],  // 선택 채널 이름들 - checkbox 와 bind
			checkedChannelSubIdx: null,
			formData: {
				path: '',
				request_title: '',
				request_content: '',
				requestor_name: '',
				request_date: '',
				request_complete_date: '',
				channel: ''
			},
			channelStore: null,
		}
    },
	provide() {
		return {
			closeTreeModal: this.closeTreeModal,
			insertIaPath: this.insertIaPath
		}
	},
	watch: {
		channelsForWatch : {
			handler: function (nChannels, oChannel) {
				if (nChannels.length > 0) {
					this.checkedChannel = nChannels.filter((channel) => {
						return channel.code == this.formData.channel;
					})[0];

					this.checkedChannelCodes.push(this.checkedChannel.code);
				}
			}
		}
	},
	computed: {
		channelsForWatch: function() {
			if(this.channelStore && this.formData.channel.trim() != '') {
				return this.channelStore.channels;
			} 
		}
	},
    mounted() {
        (async () => {
            await this.app__init();

			this.channelStore = useChannelStore();

			// router param id 설정
			this.id = this.$route.params.id;

			// db 데이터 조회
			const defaultData = await this.getData();
			// db 데이터를 formData 에 bind
			this.bindFormData(defaultData);

			const self = this;
			$('input[name=request_date]').datepicker({
				beforeShow: function(input, inst) { 
					setTimeout(function () {
						inst.dpDiv.css({"z-index":1000});
					});
				},
				onSelect(dateText) {
					self.formData.request_date = dateText;
				}
			});

			$('input[name=request_complete_date]').datepicker({
				beforeShow: function(input, inst) { 
					setTimeout(function () {
						inst.dpDiv.css({"z-index":1000});
					});
				},
				onSelect(dateText) {
					self.formData.request_complete_date = dateText;
				}
			});
			
			setTimeout(() => {
				console.log(this.channels)
			}, 1000)

        })();
    },
	
    methods: {
		 // 상세데이터 조회
        getData: async function () {
            var url = API_URL +'/work/request_detail.dat?id='+this.id;
            
            const response = await axios.get(url);
            
            return response.data.REQUEST_WORK;
        },
        bindFormData: function(data) {
            this.formData.path = data.path || '';
            this.formData.request_title = data.request_title || '';
            this.formData.request_content = data.request_content || '';
            this.formData.requestor_name = data.requestor_name || '';
            this.formData.request_date = data.request_date || '';
            this.formData.request_complete_date = data.request_complete_date || '';
            this.formData.channel = data.site_code || '';

			this.need_workers = data.need_workers;
        },
        modify__request: async function (e) {
            this.clicked = true;
            this.loading('start');
            
// 				if(this.loginInfo.auth !== 'MANAGER' && this.loginInfo.auth !== 'ADMIN') {
// 					alert('요청 권한이 없습니다.');
                
// 					this.clicked = false;
// 					return false;
// 				}
            // 채널 선택 경고
            if (this.formData.channel.trim() == '') {
                alert('하나이상의 채널을 선택해 주세요');
                return false;
            }
            
            if (this.need_workers.length <= 0) {
                alert('예상 공수를 선택해 주세요');
                return false;
            }
        
            if (this.formData.request_title.trim() == '') {
                alert('제목을 입력해 주세요.');
                
                this.clicked = false;
                return false;
            }
            
            if (this.formData.requestor_name.trim() == '') {
                alert('작업 요청자를 입력해 주세요.');
                
                this.clicked = false;
                return false;
            }
            
            if (this.formData.request_content.trim() == '') {
                alert('수정사항을 입력해 주세요.');
                
                this.clicked = false;
                return false;
            }
            
            if (this.formData.request_date.trim() == '') {
                alert('업무 요청일을 입력해 주세요.');
                
                this.clicked = false;
                return false;
            }
            if (this.formData.request_complete_date.trim() == '') {
                alert('업무 요청일을 입력해 주세요.');
                
                this.clicked = false;
                return false;
            }
            
			var {channel, ...sendData} = this.formData;
			// id 추가
			sendData.id = this.id;
			// 공수 데이터 추가
			sendData.need_workers = this.need_workers;

            // 선택된 공수 만큼 임시 파트 데이터 생성
			var part = [];
            this.need_workers.forEach((item, i) => {
				part.push({name: item, state: 'PENDING', 'worker': '지정안됨'});
            });

			// 작업공수 (기획, 디자인, 퍼블) 에 따라 생성된 공수 정보 추가
			sendData.part = part; 

			// const sendData = new FormData(); // sendData는 이전에 정의되었거나 새롭게 생성되어야 합니다.
			sendData.site_code = channel; // channelArr[0].value 대신 channel 직접 사용
			sendData.site_name = this.checkedChannel.name;

			
            try {
				const channelArr = this.checkedChannelCodes;
                const axiosPromises = channelArr.map((code) => {

                    const channelForName = this.channels.find((item) => Object.values(item).includes(code));

					sendData.site_code = code;
					sendData.site_name = channelForName.name

                    return axios.post(API_URL + '/work/request_insert.dat', channelSendData);
                });

				const allAxios = await Promise.all(axiosPromises);

				// response.status를 직접 확인할 수 있습니다.
                const hasError = allAxios.some((item) => item.status > 200);

                if (!hasError) {
                    this.loading('stop');
                    alert('업무 요청 수정이 완료되었습니다.');
                    this.$router.push('/work');

                } else {
                    this.loading('stop');
                    alert('업무 요청 수정중 일부 오류가 발생했습니다.');
                }
            } catch (error) {
                this.loading('stop');
                alert('업무 요청 수정중 오류가 발생했습니다.');
                console.error('업무 요청 수정 오류:', error);
            }
        },
		openTreeModal() {
            if (
                Object.keys(this.checkedChannel).length > 0 &&
                Array.isArray(this.checkedChannel.ia_tabs) &&
                this.checkedChannel.ia_tabs.length > 0 &&
                this.checkedChannel.ia_tabs[0].tab_name.trim() !== '' &&
                this.checkedChannel.ia_tabs[0].ia_file.trim() !== ''
            ) {
                // channel-sub 라디오가 동적으로 추가된 경우 체크 여부 확인
                const subChannelRadios = document.querySelectorAll('input[name="channel-sub"]');
                if (subChannelRadios.length > 0) {
                    const isSubChannelChecked = Array.from(subChannelRadios).some(radio => radio.checked);
                    if (!isSubChannelChecked) {
                        alert('채널 - 서브 를 선택해 주세요');
                        // 포커스를 channel 라디오의 첫번째로 이동
                        const channelRadios = document.querySelectorAll('input[name="channel"]');
                        if (channelRadios.length > 0) {
                            channelRadios[0].focus();
                        }
                        return;
                    }
					this.showTreeModal = true;
                }
            } else {
				alert('사이트를 선택해야 합니다.')
			}
        },
		closeTreeModal() {
            this.showTreeModal = false;
        },
		// request 에 ia tree 경로 추가
		insertIaPath(path) {
			this.formData.path = path;
		},
		checkChannelFn(e) {  
			if (e.target.checked && e.target.value === 'SH_PA') {
				this.checkedChannel = this.channels.find(channel => channel.code === 'SH_PA');
			}

			this.checkedChannels = this.channels.filter((channel) => {
				return this.checkedChannelCodes?.includes(channel.code);
			});
			console.log(this.checkedChannels, this.checkedChannelCodes);
		}
    }
}
</script>
<style scoped>
.input_area-label-group {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.input_area-label-group .input_area > label {
  margin-right: 10px;
  white-space: nowrap;
}
.input_area label {margin-bottom: 0 !important;}

.btn-open-ia {
  border-radius: 6px;
  height: 24px;
  font-size: 14px;
  background-color: #6a4baf; /* purple shade */
  color: white;
  border: none;
  padding: 0 12px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s ease;
  margin-left: 10px;
}

.btn-open-ia:hover {
  background-color: #8c6cd8; /* lighter purple on hover */
}
</style>