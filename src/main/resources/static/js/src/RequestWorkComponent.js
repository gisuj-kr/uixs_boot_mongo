/**
 * 작업요청 관련 컨퍼넌트
 */
function RequestWorkDetail() {
	return {
		template: `
		<div class="modal_layer_pop work_type" id="vue-popup" @click="closeWithDim" v-if="active">
			<div class="modal_layer_inner" style="background:#fff">
				<div class="pop_tit"><a href="#none" class="btn_pop_close" title="레이어팝업 닫기" @click="popupClose"></a></div>
					<div class="pop_content" style="height: 750px;">
						<ul class="work_temp">
							<li class="cont">
								<div class="work_factor">
									<dl>
										<dt>
											<div>{{workRequestData.site_name != null ? '['+workRequestData.site_name+']' : ''}} {{workRequestData.request_id}}</div>
											<a href="#none">{{compuUsernameContent}}</a>
										</dt>
										<dd>
	                    					<div class="work_area" v-html="compuContent"></div>
		                    				<div class="work_section">
		                    					<div class="sec_01">
		                    						<p>요청자</p>    
		                    						<div class="work_area">{{workRequestData.requestor_name}}</div>
		                                        </div>
		                    					<div class="sec_02">
		                    						<p>업무요청일</p>    
		                    						<div class="work_day" v-if="!dateEditMode" @click="dateEdit">
		                                                <span>{{request_date}}</span>
		                                           	</div>
		                    						<div class="work_day" style="display:flex;align-content:center;padding:4px" v-else>
			                                           	<input v-model="request_date" ref="dateInput"/>
			                    						<button type="button" class="btn_small01  mr4" @click="editDate">수정</button>
			                    						<button type="button" class="btn_small01  mr4" @click="dateEditMode=false">취소</button>
		                                           	</div>
		                                        </div>
		                    					<div class="sec_02">
		                    						<p>완료요청일</p>    
		                    						<div class="work_day" v-if="!dateEditMode2" @click="dateEdit2">
		                                                <span>~{{request_complete_date}} 까지</span>
		                                           	</div>
		                    						<div class="work_day" style="display:flex;align-content:center;padding:4px" v-else>
			                                           	<input v-model="request_complete_date" ref="dateInput2"/>
			                    						<button type="button" class="btn_small01  mr4" @click="editDate">수정</button>
			                    						<button type="button" class="btn_small01  mr4" @click="dateEditMode2=false">취소</button>
		                                           	</div>
		                                        </div>
		                                    </div>
		                    				<div class="work_section">
		                    					<div class="sec_01">
		                    						<p>예상공수</p>    
		                    						<div class="work_area">
			                    						<ul>
			                    							<li class="input_area">
								                            	<ul class="box_type_check">
								                                    <li>
								                                    	<input type="checkbox"  name="need_workers" v-model="workRequestData.need_workers" id="team01" value="plan" readonly>
								                                        <label for="team01">기획</label>
																	</li>
																	<li>
								                                    	<input type="checkbox" name="need_workers" v-model="workRequestData.need_workers" id="team02" value="design" readonly>
								                                    	<label for="team02">디자인</label>
																	</li>
																	<li>
								                                    	<input type="checkbox" name="need_workers" v-model="workRequestData.need_workers" id="team03" value="publish" readonly>
								                                    	<label for="team03">퍼블</label>
																	</li>
																</ul>
								                            </li>
								                        </ul>
		                    						</div>
		                                        </div>
		                    			
		                                    </div>
		                                    <div class="work_file">
		                                        <p>첨부파일</p>
		                                        <ul class="mt20">
							                       <li v-for="file in files" :key="file.id">
					                                  <strong>{{file.original_filename}}</strong>
					                                  <a :href="'/file/download?file_id='+file.id" class="btn_text down">다운로드</a>
					                               </li>
		                                		</ul>
		                                    </div>
		                                    <div class="work_section">
		                    					<div class="sec_01">
		                    						<p>작업진행</p>    
		                    						<div class="work_area">
			                    						<ul class="box_type_check">
				                                            <li>
				                                                <input type="radio" v-model="requestState" name="request_state" id="team04" value="PENDING">
				                                                <label for="team04">대기</label>
				                                            </li>
				                                            <li>
				                                                <input type="radio" v-model="requestState" name="request_state" id="team05" value="WORKING">
				                                                <label for="team05">시작</label>
				                                            </li>
				                                        </ul>
		                    						</div>
		                    					</div>
		                    					<div class="sec_02">
			                    					<div class="input_section">
			                                            <ul class="input_wrap" style="max-width:100%">
			                                                <li class="input_area">
			                                                    <textarea v-model="cancelContent"
			                    								type="text"
			                    								 name="cancel_content"
			                    								 placeholder="보류 사유 입력"></textarea>
			                                                </li>
			                                            </ul>
			                                        </div>
		                    					</div>
		                    				</div>
		                                    <div class="mt20" style="margin:20px 20px 0 20px">
		                                        <div class="pop_btn_area">
		                                            <a href="#none" class="btn_large_type02 btn-cancel" @click="popupClose">닫기</a>
		                                            <a href="#none" class="btn_large_type01 btn-confirm" @click.prevent="confirmRequest">확인하기</a>
		                                        </div>
		                                    </div>
	                                	</dd>
	                            	</dl>
	                        	</div>
                    		</li>
                    	</ul>
					</div>
				</div>
			</div>
		</div>
		`,
		mixins: [channelMixin],
		props: ['id'],
		data: function (){
			return {
				dateEditMode: false,
				dateEditMode2: false,
				workRequestData: {},
				requestIaList: null,
				requestIaPath: [],
				files: [],
				endDate: '',
				request_date: '',
				request_complete_date: '',
				requestState: 'WORKING',
				cancelContent: '',
				active: false,
			}
		},
		created: function () {
			this.loading('start');
		},
		computed: {
			compuUsernameContent: function () {
				return (
					this.workRequestData.site_name ? 
					'['+this.workRequestData.site_name+']' + this.workRequestData.request_title : 
					this.workRequestData.request_title
				);
			},
			compuContent: function () {
				return this.workRequestData.request_content ? this.workRequestData.request_content.replace(/\r\n/g, '<br>') : '';
			},
			requestIas: function () {
				var path = '';
				
				if (this.requestIaPath.length) {
					for(var item of this.requestIaPath) {
						path += '<ul class="polder"><li>'+item+'</li></ul>\n';	
					}
				}
				else {
					path = '<ul class="polder"><li>없음</li></ul>';	
				}
				
				return path;
			},
		},
		mounted: function () {
			this.getData().then(data => {
				this.workRequestData = data.REQUEST_WORK;
				this.files = data.FILES;
				
				if (data.REQUEST_IA_LIST) {
					this.requestIaPath = this.getMenuPathString(data.REQUEST_IA_LIST);
				}
				
				this.request_date = this.workRequestData.request_date;
				this.request_complete_date = this.workRequestData.request_complete_date;
				this.requestState = this.workRequestData.request_state;
				
				this.loading('stop');
				
				this.active = true;
			});
		},
		methods: {
			dateEdit: function () {
				var app = this;
				
				this.dateEditMode = true;
				
				// 인풋에 데이트피커 설정
				setTimeout(function () {
					$(app.$refs.dateInput).datepicker({
						beforeShow: function(input, inst) { 
							setTimeout(function () {
						        inst.dpDiv.css({"z-index":1000});
							});
					    },
					    // date picker 날짜 변경시 ia data 변경
					    onSelect: function(dateText, inst) {
					    	app.request_date = dateText;
					    }
					});
				})
			},
			dateEdit2: function () {
				
				this.dateEditMode2 = true;
				
				// 인풋에 데이트피커 설정
				setTimeout(() => {
					$(this.$refs.dateInput2).datepicker({
						beforeShow: (input, inst) => { 
							setTimeout(() => {
						        inst.dpDiv.css({"z-index":1000});
							});
					    },
					    onSelect: (dateText, inst) => {
					    	this.request_complete_date = dateText;
					    }
					});
				})
			},
			// 상세데이터 조회
			getData: async function () {
				var url = '/work/request_detail.dat?id='+this.id;
				
				const response = await axios.get(url);
				
				return response.data;
			},
			// 업무요청일 수정
			editDate: function () {
				var param = {
					'id': this.workRequestData.id,
					'request_state': this.workRequestData.request_state,
					'request_date': new Date(this.request_date),
					'request_complete_date': new Date(this.request_complete_date)
				};
				
				axios.post('/work/request_change_state.dat', param)
				.catch(error => {
					console.log(error);
				});
				
				this.dateEditMode = false;
				this.dateEditMode2 = false;
			},
			// 데이트 피커 제거
			datepickerDel: function () {
				$(this.$refs.dateInput).datepicker("destroy");
				$('.ui-datepicker').remove();
			},
			popupClose: function () {
				this.datepickerDel();
				this.$emit('close');
			},
			// 딤드영역 클릭하여 닫기
			closeWithDim: function (e) {
				if (e.target.classList[0] == 'modal_layer_pop') {
					this.popupClose();
				}
			},
			confirmRequest: function (e) {
				e.preventDefault();
				
//				if (this.loginInfo.auth !== 'WORKER' && this.loginInfo.auth != 'ADMIN' ) {
//					alert('작업자 또는 관리자 권한이 아닙니다.');
//					return false;
//				}
				
				var request_state = this.requestState;
				var cancel_content = this.cancelContent;
				
				var sendData = {
					"id": this.id,
					"request_id": this.workRequestData.request_id,
					"request_state": request_state,
					"cancel_content": cancel_content,
					"response_date": new Date(),
					"working_part": "PLAN",
				};
				
				if (request_state == 'PENDING') {
					if(cancel_content.replace(/\s/g, '') == '') {
						alert('대기 사유를 입력해 주세요');
						return;
					}
				}
				
				axios.post('/work/request_change_state.dat', sendData)
				.then(response => {
					EventBus.$emit('loadList', ['request', 'process']);
						
					this.popupClose();
				})
				.catch(error => {
					console.log(error);
				});
				
			}
		}//end component
	}//end return
};