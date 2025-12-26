<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="../include/html_head.jsp" %>
</head>
<body>
	<div class="wrapper" id="v-app">
		<!-- header-->
		<%@ include file="../include/header.jsp" %>
		<!--// header-->
		
		<!--container-->
        <div class="container">
            
            <div class="content">
                <div class="content_inner">
                	<form name="userForm" id="userForm">
                    <div class="tit_area">
                        <h2>사용자 등록</h2>
                    </div>
                    <div class="input_section">
                        <ul class="input_wrap">
                            <li class="input_area">
                                <label for="userid">아이디</label>
                                <input 
                                	type="text" 
                                	name="userid" 
                                	ref="userid" 
                                	id="userid" 
                                	placeholder="아이디를 입력하세요" 
                                	maxlength="10" 
                                	v-model="userid" 
                                	readonly>
                            </li>
                            <li class="input_area">
                                <label for="password">이전비밀번호</label>
                                <input 
                                	type="password" 
                                	name="old_password" 
                                	ref="old_password" 
                                	id="old_password" 
                                	placeholder="비밀번호를 입력하세요" 
                                	maxlength="10" 
                                	v-model="old_password"
                                	v-on:blur="passwordCheck">
                            </li>
                            <li class="input_area">
                                <label for="password">새비밀번호</label>
                                <input 
                                	type="password" 
                                	name="password" 
                                	ref="password" 
                                	id="password" 
                                	placeholder="비밀번호를 입력하세요"
                                	maxlength="10" 
                                	v-model="password">
                            </li>
                            <li class="input_area">
                                <label for="username">이름</label>
                                <input type="text" 
                                	name="username" 
                                	ref="username" 
                                	id="username" 
                                	placeholder="이름을 입력하세요" 
                                	maxlength="10" 
                                	v-model="username">
                            </li>
                            <li class="input_area">
                                <label for="team">부서</label>
                                <input type="text" name="team" ref="team" id="team" placeholder="부서를 입력하세요" maxlength="20" v-model="team">
                            </li>
                            <li class="input_area">
                                <label for="part">담당업무</label>
                                <select class="selectbox" name="part" id="part" v-model="part">
                                    <option value="MANAGER">담당자(현업)</option>
                                    <option value="PLAN">기획</option>
                                    <option value="DESIGN">디자인</option>
                                    <option value="PUBLISH">퍼블리셔</option>
                                    
                                </select>
                            </li>
                            <li class="input_area">
                                <label for="tel1">전화번호</label>
                                <div class="tel_box">
	                                <input type="tel" name="tel1" id="tel1" maxlength="3" v-model="tel.tel1">-
	                                <input type="tel" name="tel2" id="tel2" maxlength="4" v-model="tel.tel2">-
	                                <input type="tel" name="tel3" id="tel3" maxlength="4" v-model="tel.tel3">
                                </div>
                            </li>
                            <li class="input_area">
                                <label for="email">이메일</label>
                                <input type="text" name="email" ref="email" id="email" placeholder="이메일을 입력하세요" maxlength="50" v-model="email">
                            </li>
                            <li class="input_area">
                                <label for="auth">권한</label>
                                <select class="selectbox" name="auth" id="auth" v-model="auth">
                                    <option value="MANAGER">관리자(현업)</option>
                                    <option value="WORKER">작업자(UI/UX 담당)</option>
                                    <option value="ADMIN">관리자</option>
<!--                                     <option value="USER">일반사용자(파일리스트 뷰)</option> -->
                                </select>
                            </li>
                        </ul>
                    </div>
                    <div class="btn_area" id="action-btn">
                        <a href="/user/list.view?page=${page}"class="btn_large_type02">목록</a>
                        <a href="#none" class="btn_large_type02" id="btn_user_del" v-if="loginInfo.auth=='ADMIN'" v-on:click.prevent="memberDelete">삭제</a>
                        <a href="#none" class="btn_large_type01" id="btn_user_insert">수정</a>
                    </div>
                    </form>
                </div>
            </div>
        </div>
        <!--// container-->
	</div>
	
</body>
<script>
var _app = new Vue({
	el: '#v-app',
	store: _store,
	mixins: [channelMixin],
	data() {
		return {
			userid: '${userid}',
			username: '',
			old_password: '',
			password: '',
			passwordMatch: false,
			team: '',
			part: 'MANAGER',
			tel: {
				tel1: '',
				tel2: '',
				tel3: ''
			},
			email: '',
			auth: 'WORKER',
			member: {}
		}
	},
	created: function () {
		this.app__init();
		
		axios.post('/member/id_check.dat', null, {params: {userid: this.userid}})
		.then(response => {
			this.member = response.data;
			
			var telArr = this.member.tel == null ? [] : this.member.tel.split('-');
			
			this.username = this.member.username;
			this.team = this.member.team;
			this.part = this.member.part;
			this.tel.tel1 = telArr[0];
			this.tel.tel2 = telArr[1];
			this.tel.tel3 = telArr[2];
			this.email = this.member.email;
			this.auth = this.member.auth;
		})
		.catch(error => {
			console.log(error);
		});
	},
	watch: {
// 		loginInfo: function() {
// 			console.log(this.loginInfo)
// 		}
	},
	methods: {
		//전화번호 입력후 숫자 체크
		inputTel(event) {
			var value = event.target.value;
			var targetName = event.target.name;
			
			var emptyCheckedValue = rmSpace(value);  
			
			if ( emptyCheckedValue != '' && !$.isNumeric(emptyCheckedValue) ) {
				
				alert('숫자만 입력 가능합니다.');
				
				event.target.focus();
				
				this.tel[targetName] = '';
				
				return false;
			}
		},// end: 전화번호 입력후 숫자 체크
		// 멤버 삭제
		memberDelete() {
			if (this.loginInfo.userid !=  this.userid && this.loginInfo.auth != 'ADMIN') {
				alert('수정 권한이 없습니다.');
				history.back(-1);
			}
			
			if (confirm('사용자를 삭제 하시겠습니까?')) {
				
				axios.post('/member/delete.dat', null, {params: this.member.id})
				.then(response=>{
					if(response.data.RESULT === 'SUCCESS') {
						alert('삭제가 완료되었습니다.');
						location.href = '/member/list.do';							
					}
					else {
						alert(response.data.MESSAGE);
					}
				})
				.catch(error=>{
					console.log(error);
				});
				
			}
			else {
				return;
			}
		}, // end: 멤버 삭제
		// 이전 비밀번호 체크
		passwordCheck(event) {
			var target = event.target;
			var old_password = rmSpace(this.old_password);
			
			if (old_password != '') {
				
				axios.post('/member/passwordMatch.dat', null, {
					params: {userid: this.userid, old_password: this.old_password}
				})
				.then(response => {
					if(!response.data) {
						alert('비밀번호가 일치하지 않습니다.');
						this.old_password = '';
						this.$refs.old_password.focus();
					}
					
					this.passwordMatch = response.data;
				})
				.catch(error => {
					console.log(error);
				});
			}
		}, // end: 이전 비밀번호 체크
		
	}
});
$(document).ready(function () {
	
	(function userUpdate() {
		function _focus(target) {
			$('input[name='+target+']').val('').focus();
		}
		
		
		
		
		// 전화번호에 값 입력후 이동시
		$('#tel1, #tel2, #tel3').on('blur', function (e) {
			var $this = $(this);
			var val = rmSpace($this.val());  
			
			if ( val != '' && !$.isNumeric(val) ) {
				uijs.msg.alert('숫자만 입력 가능합니다.');
				$this.val('').focus();
				return false;
			}
		});
		
		$('#password').on('blur', function () {
			if (rmSpace($(this).val()) != '') {
				uijs.msg.alert('비밀번호 변경을 원하시면 이전 비밀번호를 입력해 주세요.');
				_focus('old_password');
				return false;
			}
		});
		
		$('#btn_user_insert').on('click', function (e) {
			e.preventDefault();
			
			var formData = new FormData(document.getElementById('userForm')); 
			var sendData = {}; // ajax 전송용 데이터
			var tel; // 전화번호 tel1 + tel2 + tel3
			
			// formData 를 쪼개서 json 형태로 만들기 = sendData 
			for(var data of formData) {
				// rmSpace 공백 제거
				sendData[data[0]] = rmSpace(data[1]);
			}

			if (sendData.username == '') {
				uijs.msg.alert('이름을 입력해 주세요.');
				_focus('username');
				return false;
			}
			
			if (sendData.old_password != '' && passwordMatch) {
				if (sendData.password == '') {
					uijs.msg.alert('비밀번호를 입력해 주세요.');
					_focus('password');
					return false;
				}
				
				if (sendData.password.length < 4 || sendData.password.length > 10) {
					uijs.msg.alert('비밀번호는 최소 4자리 최대 10자리 까지 입력 가능합니다.');
					_focus('password');
					return false;
				}
			}
			else {
				sendData.password = '';
			}
			
			/*
			if (sendData.tel1 == '') {
				uijs.msg.alert('전화번호 첫번째자리를 입력해 주세요.');
				_focus('tel1');
				return false;
			}
			
			if (sendData.tel2 == '') {
				uijs.msg.alert('전화번호 두번째자리를 입력해 주세요.');
				_focus('tel2');
				return false;
			}
			
			if (sendData.tel3 == '') {
				uijs.msg.alert('전화번호 세번째자리를 입력해 주세요.');
				_focus('tel3');
				return false;
			}
			*/
			
			if (sendData.email !== '') {
				if (!validEmail(sendData.email)) {
					uijs.msg.alert('이메일 형식이 잘못 되었습니다.');
					_focus('email');
					return false;
				}
			}
			
			if (sendData.tel1 != '' && sendData.tel2 != '' && sendData.tel3 != '') {
				tel = sendData.tel1 + '-' + sendData.tel2 + '-' + sendData.tel3;
				sendData.tel = tel;
			}
			else {
				sendData.tel = '';
			}
			
			uijs.ajaxDef({
				ajaxOption: {
					url: '/user/edit.data'
					, data: sendData
				},
				callback: function (data) {
					if (data.result == 'success') {
						uijs.msg.alert('사용자 수정이 완료 되었습니다.');
						location.href = '/user/list.view';
					}
					else {
						uijs.msg.alert('사용자 수정중 오류가 발생하였습니다.');
					}
				}
			});
		});
	})();
	
});
</script>
</html>
