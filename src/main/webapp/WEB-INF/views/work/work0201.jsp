<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="../include/html_head.jsp" %>
<script>
 /*    $(document).ready(function () {
        $('#calendar-pop').find('.btn_edit_close, td > div').click(function () {
            $('#calendar-pop').hide();
        });
    }); */
</script>
</head>
<body>
	<div class="wrapper" id="v-app">
		<%@ include file="../include/header.jsp" %>
		
		<!--container-->
		<div class="container">
			<form name="work_request_form" id="work_request_form" method="post" enctype="multipart/form-data" action="/work/request/insert.data2">
			<div class="content">
				<div class="content_inner">
					<div class="tit_area">
                        <h2>작업요청하기</h2>
                    </div>
					
					<div class="input_section mt20">
                        <ul class="input_wrap" style="padding-top: 0;">
                           <li class="input_area">
                                <label for="file">파일</label>
                                <input type="file" name="files" placeholder="파일을 선택해주세요">
                            </li>
                          
                            
                        </ul>
                    </div>
                    <div class="btn_area">
                        <a href="/work/list.view" class="btn_large_type02">취소하기</a>
                        <input type="submit" value="전송">
                    </div>
				</div>
			</div>
			</form>
		</div>
		<!--// container-->
	</div>

	<script type="text/javascript">
	var _app = null;
	new Vue({
		el: '#v-app',
		store: _store,
		mixins: [channelMixin],
		data: {
			files: [],
			lastFilesIndex: 1,
			clicked: false
		},
		created: function () {
			_app = this;
			
			//this.app__init();
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
			insert__request: function (e) {
				this.clicked = true;
				
				if(this.loginInfo.auth !== 'MANAGER' && this.loginInfo.auth !== 'ADMIN') {
					alert('요청 권한이 없습니다.');
					
					this.clicked = false;
					return false;
				}
				
				/*
				if (rmSpace($('input[name=request_title]').val()) == '') {
					alert('제목을 입력해 주세요.');
					$('input[name=request_title]').focus();
					
					this.clicked = false;
					return false;
				}
				
				if (rmSpace($('textarea[name=request_content]').val()) == '') {
					alert('수정사항을 입력해 주세요.');
					$('textarea[name=request_content]').focus();
					
					this.clicked = false;
					return false;
				}
				
				if (rmSpace($('input[name=end_date]').val()) == '') {
					alert('작업완료일을 입력해 주세요.');
					$('input[name=end_date]').focus();
					
					this.clicked = false;
					return false;
				}
				*/
				var requestIaData = $('#request_ia_form').serializeArray();
				
				var iaDataList = requestIaData.map(function (v, k) {
					return v.value;
				});
				
				
				var formData = new FormData($('#work_request_form')[0]);
				var sendData = new FormData();
				
				/*
				 * v: array value, k: array key
				 * new Date 를적용하기위해 새로운 FormData 를 생성후 form 의 내용을 삽입
				*/
				formData.forEach(function (v, k) {
					if (k == 'end_date') {
						sendData.append(k, new Date(v));
					}
					else {
						sendData.append(k, v);
					}
				});
				sendData.append('site_code', this.getChannelCode);
				sendData.append('username', this.loginInfo.username);
				
				// 선택한 메뉴가 있는경우
				if (iaDataList.length) {
					sendData.append('req_ia', iaDataList);
				}
				
				// 첨부파일 추가 
				sendData = uijs.addFileData(sendData, $('#work_request_form'));
				
				var sendData2 = new FormData();
				sendData2 = uijs.addFileData(sendData2, $('#work_request_form'));
				
				sendData.forEach(function (v, k) {
					console.log(v, k);
				});
			
				uijs.ajaxFormData({
					url: '/work/request/insert.data2',
					data: sendData2,
					successCallback: function (data) {
						
						// 새글알림 메세지 전송
// 						_app.$sendMsg([
// 							_app.loginInfo.userid, 
// 							_app.selectedChannel.name + ' 채널에 새로운 작업요청이 등록 되었어요!'
// 						]);
						
						alert('작업요청이 완료 되었습니다.');
						
						//location.href = "/work/list.view";
					},
					errorCallback: function (error) {
						console.log(error);
						this.clicked = false;
					}
				});
			}
		}
	});
    //<![CDATA[
	$(function () {
		//작업완료일 달력 선택
		$('input[name=end_date]').datepicker({
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
