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
								<template v-for="channel in channels">
									<div v-if="['SH_PA', 'SH_IN'].includes(channel.code) && channel.ia_tabs.length > 0" class="select-channel-wrap">
										<div v-for="(tab, tabIndex) in channel.ia_tabs" :key="tabIndex" class="channel-sub-group">
											<div v-if="tab.ia_file.trim() != ''" class="channel-sub-group__front">
												<div class="channel-sub-group__front channel-sub-group__front-checkbox">
													<input type="checkbox" 
														name="channel"
														:value="`${channel.code},${tab.tab_name}`" 
														:id="`${channel.code}_${tab.tab_name}`"
														v-model="checkedChannelCodes"
														@change="checkChannel"
													> <label :for="`${channel.code}_${tab.tab_name}`">{{ channel.name }} {{ tab.tab_name }}</label>
												</div>
												<button 
													type="button" 
													class="btn-open-ia" 
													@click="openTreeModal(channel, tabIndex)" >IA 열기</button>
											</div>
											<div class="channel-sub-group__back">
												<input 
													type="text" 
													name="path" 
													:value="getPathValue(channel, tab)" 
													@input="setPathValue(channel, tab, $event.target.value)" 
													placeholder="IA 경로를 입력하세요"
												>
											</div>
										</div>
									</div>

									<div class="select-channel-wrap--row" v-else>
										<div class="channel-sub-group__front">
											<input type="checkbox" 
												name="channel" 
												:id="channel.code" 
												:value="channel.code" 
												v-model="checkedChannelCodes"
												@change="checkChannel"
											> <label :for="channel.code">{{channel.name}}</label>
										</div>
										<div class="channel-sub-group__back">
											<input 
												type="text" 
												name="path" 
												:value="getPathValue(channel, null)" 
												@input="setPathValue(channel, null, $event.target.value)" 
												placeholder="IA 경로를 입력하세요"
											>
										</div>
									</div>
								</template>
							</div>
							
						</li>
                      
                        <li class="input_area" style="margin-top: 20px; padding-top: 20px; border-top: 2px solid #666;">
                            <label for="request_title">제목</label>
                            <input 
								type="text" 
								id="request_title" 
								name="request_title" 
								placeholder="제목을 입력하세요"
								v-model="formData.request_title"
							>
                        </li>
                        <li class="input_area">
                            <label for="request_content">업무(요건) 내용</label>
                            <textarea 
								id="request_content" 
								name="request_content" 
								placeholder="내용을 입력해주세요."
								v-model="formData.request_content"
							></textarea>
                        </li>
                        <li class="input_area" style="margin-top: 20px">
                            <label for="request_title">요청자</label>
                            <input 
								type="text" 
								id="requestor_name" 
								name="requestor_name" 
								placeholder="요청자를 입력하세요"
								v-model="formData.requestor_name"
							>
                        </li>
                        <li class="input_area half">
                            <div class="input-inner half">
                                <label for="request_date">업무 요청일</label>
                                <input 
									type="text" 
									id="request_date" 
									name="request_date" 
									placeholder="날짜를 선택해주세요" 
									class="datepick"  
									data-type="date" 
									autocomplete="off"
								>
                            </div>
                            <div class="input-inner half">
                                <label for="request_complete_date">완료 요청일</label>
                                <input 
									type="text" 
									id="request_complete_date" 
									name="request_complete_date" 
									placeholder="날짜를 선택해주세요" 
									class="datepick"  
									data-type="date" 
									autocomplete="off"
								>
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
                    <button type="button" class="btn_large_type01" @click.prevent="insert__request">등록하기</button>
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
			checkedChannelSubs: [],
			pathList: [],
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
    async mounted() {
        console.log('mounted')
		await this.app__init();

		this.$nextTick(() => {
			// jQuery UI datepicker의 경우
			$('.hasDatepicker').removeClass('hasDatepicker').removeData('datepicker');
			
			// datepicker 초기화
			this.initDatepickers();
		});

		this.channelStore = useChannelStore();
      
		// 요청작업 메뉴 선택
		$('#selectmenu').on('click', function () {
			uijs.requestWork.requestTypePopup();
		});
   
    },
	watch: {
		channelsForWatch : {
			handler: function (nChannels, oChannel) {
				if (nChannels.length > 0) {
					// 채널을 순회 하면서 채널의 {code: 채널.code, path: ''} 으로 이루어진 배열을 생성
					// 채널의 ia_tabs 에 값이 있다면 code 는 채널.code + '_' + tab.tab_name 으로 생성
					// 그 외에는 code 로 생성
					this.pathList = nChannels.flatMap(channel => {
						if (['SH_PA', 'SH_IN'].includes(channel.code) && channel.ia_tabs.length > 0) {
							return channel.ia_tabs.map(tab => ({
								code: `${channel.code},${tab.tab_name}`,
								path: ''
							}));
						} else {
							return [{
								code: channel.code,
								path: ''
							}];
						}
					});
				}
			}
		},
	},
	computed: {
		channelsForWatch: function() {
			return this.channelStore?.channels || [];
		}
	},
	
    methods: {
		initDatepickers() {
			this.$nextTick(() => {
				setTimeout(() => {
					this.setupDatepicker('request_date');
					this.setupDatepicker('request_complete_date');
				}, 100);
			});
		},
    
		setupDatepicker(fieldName) {
			const self = this;
			const input = $(`input[name=${fieldName}]`);
			
			if (input.length === 0) return;
			
			// 완전히 정리
			this.cleanupDatepicker(input);
			
			// 새로 생성
			input.datepicker({
				beforeShow: function(input, inst) { 
					setTimeout(function () {
						inst.dpDiv.css({"z-index":1000});
					});
				},
				onSelect(dateText) {
					self.formData[fieldName] = dateText;
				}
			});
		},
    
		cleanupDatepicker(input) {
			try {
				input.val('');
				if (input.hasClass('hasDatepicker')) {
					input.datepicker('destroy');
				}
				input.off('.datepicker').removeData('datepicker').removeClass('hasDatepicker');
			} catch (e) {
				// 무시
			}
		},
    
		safeDestroyDatepickers() {
			try {
				const inputs = $('input[name=request_date], input[name=request_complete_date]');
				inputs.each((index, element) => {
					this.cleanupDatepicker($(element));
				});
			} catch (e) {
				// 무시
			}
		},
		getPathValue(channel, tab) {
			const targetCode = `${channel.code}${tab ? `,${tab.tab_name}` : ''}`
			const found = this.pathList.find(item => item.code === targetCode)
			return found ? found.path : ''
		},
		setPathValue(channel, tab, newValue) {
			const targetCode = `${channel.code}${tab ? `,${tab.tab_name}` : ''}`
			const foundIndex = this.pathList.findIndex(item => item.code === targetCode)
			
			if (foundIndex !== -1) {
				this.pathList[foundIndex].path = newValue
			} else {
				this.pathList.push({ code: targetCode, path: newValue })
			}

			//this.checkedChannelCodes 에 targetCode 가 없으면 추가
			if (!this.checkedChannelCodes.includes(targetCode)) {
				this.checkedChannelCodes.push(targetCode)
			}
		},
		bindFormData: function(data) {
            this.formData.request_title = data.request_title || '';
            this.formData.request_content = data.request_content || '';
            this.formData.requestor_name = data.requestor_name || '';
            this.formData.request_date = data.request_date || '';
            this.formData.request_complete_date = data.request_complete_date || '';
            this.formData.channel = data.site_code || '';

			this.need_workers = data.need_workers;
        },
        addFileInput: function () {
            this.files.push({id: this.lastFilesIndex, value: ''});
            this.lastFilesIndex++;
        },
        delFileInput: function (index) {
            this.files.splice(index, 1);
        },
        fileChange: function (e) {
            document.getElementById(e.target.id+'_text').value = e.target.files[0].name;
        },
        insert__request: async function (e) {
            this.clicked = true;
            this.loading('start');
            
			if (this.checkedChannelCodes.length <= 0) {
				alert('하나이상의 채널을 선택해 주세요');
				this.clicked = false;
				this.loading('stop');
				return false;
			}
	            
            if (this.need_workers.length <= 0) {
                alert('예상 공수를 선택해 주세요');
				this.loading('stop');
                return false;
            }
        
            if (this.formData.request_title.trim() == '') {
                alert('제목을 입력해 주세요.');
                
                this.clicked = false;
				this.loading('stop');
                return false;
            }
            
            if (this.formData.requestor_name.trim() == '') {
                alert('작업 요청자를 입력해 주세요.');
                
                this.clicked = false;
				this.loading('stop');
                return false;
            }
            
            if (this.formData.request_content.trim() == '') {
                alert('수정사항을 입력해 주세요.');
                
                this.clicked = false;
				this.loading('stop');
                return false;
            }
            
            if (this.formData.request_date.trim() == '') {
                alert('업무 요청일을 입력해 주세요.');
                
                this.clicked = false;
				this.loading('stop');
                return false;
            }
            if (this.formData.request_complete_date.trim() == '') {
                alert('업무 요청일을 입력해 주세요.');
                
                this.clicked = false;
				this.loading('stop');
                return false;
            }
            
			var {channel, ...originalFormData} = this.formData;

			// 공수 데이터 추가
			originalFormData.need_workers = this.need_workers;

			// 선택된 공수 만큼 임시 파트 데이터 생성
			var part = [];
			this.need_workers.forEach((item, i) => {
				part.push({name: item, state: 'PENDING', 'worker': '지정안됨'});
			});

			// 작업공수 (기획, 디자인, 퍼블) 에 따라 생성된 공수 정보 추가
			// originalFormData.part = part; 
			originalFormData.username = this.loginInfo.username;

			const channelArr = this.checkedChannelCodes;

			try {
				const originalTitle = this.formData.request_title;

				const allAxios = await channelArr.reduce(async (prevPromise, code, index) => {
					const results = await prevPromise;
					
					console.log(`=== 반복 ${index} ===`);
					console.log('원본 code:', code);
					
					// code 는 SH_IN,기업 과 같이 "," 로 구분 되어 있기 때문에 split 으로 잘라서 0번째 index 만 가져옴
					const [channelCode, tabName] = code.split(',');
					console.log('channelCode:', channelCode);
					console.log('tabName:', tabName);
					
					// 각 반복마다 새로운 FormData 생성
					const formData = new FormData();
					
					// 공통 데이터 추가
					Object.keys(originalFormData).forEach(key => {
						if (key === 'need_workers') {
							formData.append(key, originalFormData[key] || []);
							// 배열이나 객체는 JSON 문자열로 변환
							this.need_workers.forEach((part, i) => {
								formData.append('part['+i+'].name', part);
								formData.append('part['+i+'].state', 'PENDING');
								formData.append('part['+i+'].worker', '지정안됨');
							});
							// formData.append(key, JSON.stringify(originalFormData[key]));
						} else if (key === 'files') {
							// 파일은 따로 처리
							if (originalFormData[key] && originalFormData[key].length > 0) {
								originalFormData[key].forEach(file => {
									formData.append('files', file);
								});
							}
						} else if (key === 'path') {
							return;
						} else {
							formData.append(key, originalFormData[key] || '');
						}
					});

								
					// 경로 설정
					const pathItem = this.pathList.find(item => item.code === code);
					if (pathItem && pathItem.path) {
						formData.append('path', pathItem.path);
					} else {
						formData.append('path', ''); // 경로가 없으면 빈 문자열로 설정
					}

					const channelForName = this.channels.find((item) => Object.values(item).includes(channelCode));
					
					// 제목 설정
					let finalTitle = originalTitle;
					if (tabName && tabName.trim() !== '') {
						finalTitle = `[${tabName}] ${originalTitle}`;
					}
					formData.delete('request_title');
					formData.append('request_title', finalTitle);
					
					// 채널 정보 추가
					formData.append('site_code', channelCode);
					formData.append('site_name', channelForName ? channelForName.name : '');

					// FormData 내용 확인 (디버깅용)
					console.log('FormData 내용:');
					for (let [key, value] of formData.entries()) {
						console.log(`${key}: ${value}`);
					}

					const result = await axios.post(API_URL + '/work/request_insert.dat', formData, {
						headers: {
							'Content-Type': 'multipart/form-data'
						}
					});

					return [...results, result];
				}, Promise.resolve([]));
				// const allAxios = await Promise.all(axiosPromises);

				// response.status를 직접 확인할 수 있습니다.
				const hasError = allAxios.some((item) => item.status > 200);

				if (!hasError) {
					this.loading('stop');
					alert('업무 요청이 완료되었습니다.');
					this.$router.push('/work');
				} else {
					this.loading('stop');
					alert('업무 요청 중 일부 오류가 발생했습니다.');
				}
			} catch (error) {
				this.loading('stop');
				alert('업무 요청 중 오류가 발생했습니다.');
				console.error('업무 요청 오류:', error);
			}
        },
		openTreeModal(channel, tabIdx) {
			// channel-sub 라디오가 동적으로 추가된 경우 체크 여부 확인
			this.checkedChannel = channel;
			this.checkedChannelSubIdx = tabIdx;
				
			this.showTreeModal = true;
        },
		closeTreeModal() {
            this.showTreeModal = false;
        },
		// request 에 ia tree 경로 추가
		insertIaPath(path) {
			this.setPathValue(this.checkedChannel, this.checkedChannel.ia_tabs[this.checkedChannelSubIdx], path);
		},
		checkChannel(e) {
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
.select-channel-wrap--row {
	display: flex;
	flex-direction: column;
	/* align-items: center; */
	margin-top: 2px;
	justify-content: flex-start;
}
.select-channel-wrap {
	display: flex;
	flex-direction: column;
	margin-top: 2px;
	justify-content: flex-start;
}
.channel-sub-group__front {
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.channel-sub-group__back {
	width: 100%;
}
.channel-sub-group {
	display: flex;
	flex-direction: column;
	width: 100%;
	gap: 4px;
}
.channel-sub-group + .channel-sub-group {
	margin-top: 10px;
}
</style>
