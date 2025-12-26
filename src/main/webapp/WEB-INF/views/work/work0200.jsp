<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="../include/html_head.jsp" %>
</head>
<body>
	<div class="wrapper" id="v-app">
		<header-component></header-component>
		
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
	                                <div v-for="channel in channels">
	                                	<input type="checkbox" name="channel" :id="channel.code" :value="channel.code"> <label :for="channel.code">{{channel.name}}</label>
	                                </div>
                                </div>
                          	</li>
							<li class="input_area" style="margin-top: 20px">
                                <label for="request_title">제목</label>
                                <input type="text" id="request_title" name="request_title" placeholder="제목을 입력하세요">
                            </li>
							<li class="input_area">
                                <label for="request_content">업무(요건) 내용</label>
                                <textarea id="request_content" name="request_content" placeholder="내용을 입력해주세요."></textarea>
                            </li>
                            <li class="input_area" style="margin-top: 20px">
                                <label for="request_title">요청자</label>
                                <input type="text" id="requestor_name" name="requestor_name" placeholder="요청자를 입력하세요">
                            </li>
							<li class="input_area half">
								<div class="input-inner half">
	                                <label for="request_date">업무 요청일</label>
	                                <input type="text" id="request_date" name="request_date" placeholder="날짜를 선택해주세요" class="datepick"  data-type="date" autocomplete="off">
								</div>
                                <div class="input-inner half">
                                	<label for="request_complete_date">완료 요청일</label>
                                	<input type="text" id="request_complete_date" name="request_complete_date" placeholder="날짜를 선택해주세요" class="datepick"  data-type="date" autocomplete="off">
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
							<li class="input_area edit_pop" style="width: 100%; box-shadow: none; padding: 0 0; top: 0;">
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
                        <a href="/work/list.do" class="btn_large_type02">취소하기</a>
                        <button type="button" class="btn_large_type01" @click.prevent="insert__request">등록하기</button>
                    </div>
				</div>
			</div>
			</form>
		</div>
		<!--// container-->
	</div>

<script type="text/javascript">
	var _app = null;
	createApp({
		mixins: [channelMixin],
		data: {
			files: [],
			lastFilesIndex: 1,
			clicked: false,
			need_workers: [],
			form_channel: []
		},
		mounted: function () {
			_app = this;
			
			(async () => {
				await this.app__init();
			})();
			
		},
		methods: {
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
				
// 				if(this.loginInfo.auth !== 'MANAGER' && this.loginInfo.auth !== 'ADMIN') {
// 					alert('요청 권한이 없습니다.');
					
// 					this.clicked = false;
// 					return false;
// 				}
				// 채널 선택 경고
				if (Object.values(document.querySelectorAll('input[name=channel]:checked')).length <= 0) {
					alert('하나인상의 채널을 선택해 주세요');
					return false;
				}
				
				if (Object.values(document.querySelectorAll('input[name=need_workers]:checked')).length <= 0) {
					alert('예상 공수를 선택해 주세요');
					return false;
				}
			
				if (rmSpace($('input[name=request_title]').val()) == '') {
					alert('제목을 입력해 주세요.');
					$('input[name=request_title]').focus();
					
					this.clicked = false;
					return false;
				}
				
				if (rmSpace($('input[name=requestor_name]').val()) == '') {
					alert('작업 요청자를 입력해 주세요.');
					$('input[name=requestor_name]').focus();
					
					this.clicked = false;
					return false;
				}
				
				if (rmSpace($('textarea[name=request_content]').val()) == '') {
					alert('수정사항을 입력해 주세요.');
					$('textarea[name=request_content]').focus();
					
					this.clicked = false;
					return false;
				}
				
				if (rmSpace($('input[name=request_date]').val()) == '') {
					alert('업무 요청일을 입력해 주세요.');
					$('input[name=request_date]').focus();
					
					this.clicked = false;
					return false;
				}
				if (rmSpace($('input[name=request_complete_date]').val()) == '') {
					alert('업무 요청일을 입력해 주세요.');
					$('input[name=request_complete_date]').focus();
					
					this.clicked = false;
					return false;
				}
				
				var formData = new FormData($('#work_request_form')[0]);
				var sendData = new FormData();
				
				/*
				 * v: array value, k: array key
				 * new Date 를적용하기위해 새로운 FormData 를 생성후 form 의 내용을 삽입
				*/
				formData.forEach(function (v, k) {
					if (k == 'end_date') {
						sendData.append(k, new Date().toISOString().substring(0,10));
					}
					else {
						sendData.append(k, v);
					}
				});
				
				sendData.append('username', this.loginInfo.username);
				
				// 첨부파일 추가 
				sendData = uijs.addFileData(sendData, $('#work_request_form'));
				
				sendData.append('plan_state.state', 'PENDING');
				sendData.append('publish_state.state', 'PENDING');
				sendData.append('design_state.state', 'PENDING');
				
				// 선택된 공수 만큼 임시 파트 데이터 생성
				document.querySelectorAll('input[name=need_workers]:checked').forEach((part, i) => {
					sendData.append('part['+i+'].name', part.value);
					sendData.append('part['+i+'].state', 'PENDING');
					sendData.append('part['+i+'].worker', '지정안됨');
				});
				
				const channelArr = Object.values(document.querySelectorAll('input[name=channel]:checked'));
				
				const allAxios = await channelArr.reduce(async (promise, channel, index) => {
					let result = await promise.then();
					
					const channelForName = this.channels.find((item) => Object.values(item).includes(channel.value));
					
					sendData.set('site_code', channel.value);
					sendData.set('site_name', channelForName.name);
					
					result[channel.value] = await axios.post('/work/request_insert.dat', sendData, {
						headers: {
			                'Content-Type': 'multipart/form-data' // Content-Type 설정
			            }
					});
					
					return Promise.resolve(result);
				}, Promise.resolve({}));
				
				let result = true;
				
				Object.values(allAxios).forEach((item) => {
					if (item.status > 200) {
						result = false;
						return false;
					}
				});
				 
				if (result) { 
					this.loading('stop');
					alert('업무요청이 완료 되었습니다.');
					location.href = "/work/list.do";
				}
			}
		}
	})
	.component('header-component', HeaderComponent)
	.use(pinia)
	.mount('#v-app');
	
	
    //<![CDATA[
	$(function () {
		//작업완료일 달력 선택
		$('input[name=request_date], input[name=request_complete_date]').datepicker({
			beforeShow: function(input, inst) { 
				setTimeout(function () {
			        inst.dpDiv.css({"z-index":1000});
				});
		    }
		});
		
		// 요청작업 메뉴 선택
		$('#selectmenu').on('click', function () {
			uijs.requestWork.requestTypePopup();
		});
	});	   
    //]]>
   </script>

</body>

</html>
