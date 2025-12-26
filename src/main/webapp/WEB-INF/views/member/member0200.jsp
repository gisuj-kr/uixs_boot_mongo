<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="../include/html_head.jsp" %>
</head>
<!-- 사용자 등록화면 -->
<body>
	<div class="wrapper" id="v-app">
		<!-- header-->
		<header-component></header-component>
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
                                <input type="text" 
                                	name="userid" 
                                	id="userid" 
                                	v-model="userid" 
                                	placeholder="아이디를 입력하세요" 
                                	maxlength="10" 
                                	required
                                	v-on:blur="userIdOverWriteCheck"
                                	ref="userid">
                            </li>
                            <li class="input_area" v-if="vmode == 'modify'">
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
                                <label for="password">비밀번호</label>
                                <input type="password" ref="password" name="password" id="password" v-model="password" placeholder="비밀번호를 입력하세요" maxlength="10" required>
                                
                            </li>
                            <li class="input_area">
                                <label for="username">이름</label>
                                <input type="text" ref="username" name="username" id="username" v-model="username" placeholder="이름을 입력하세요" maxlength="10" required>
                            </li>
                            <li class="input_area">
                                <label for="team">부서</label>
                                <input type="text" ref="team" name="team" id="team" v-model="team" placeholder="부서를 입력하세요" maxlength="20">
                            </li>
                            <li class="input_area">
                                <label for="part">담당업무</label>
                                <select class="selectbox" name="part" id="part" v-model="part">
                                    <option value="MANAGER" selected>담당자(현업)</option>
                                    <option value="PLAN">기획</option>
                                    <option value="DESIGN">디자인</option>
                                    <option value="PUBLISH">퍼블리셔</option>
                                </select>
                            </li>
                            <li class="input_area">
                                <label for="tel1">전화번호</label>
                                <div class="tel_box">
	                                <input type="tel" name="tel1" id="tel1" maxlength="3" v-model="tel.tel1" v-on:blur="inputTel">-
	                                <input type="tel" name="tel2" id="tel2" maxlength="4" v-model="tel.tel2" v-on:blur="inputTel">-
	                                <input type="tel" name="tel3" id="tel3" maxlength="4" v-model="tel.tel3" v-on:blur="inputTel">
                                </div>
                            </li>
                            <li class="input_area">
                                <label for="email">이메일</label>
                                <input type="text" ref="email" name="email" id="email" v-model="email" placeholder="이메일을 입력하세요" maxlength="50">
                            </li>
                            <li class="input_area">
                                <label for="auth">권한</label>
                                <select class="selectbox" name="auth" id="auth" v-model="auth">
                                    <option value="MANAGER">관리자(현업)</option>
                                    <option value="WORKER" selected>작업자(UI/UX 담당)</option>
                                	<option value="ADMIN">관리자(ADMIN)</option>
                                </select>
                            </li>
                        </ul>
                    </div>
                    <div class="btn_area">
                        <a href="/member/list.do?page=${page}"class="btn_large_type02">목록</a>
                        <a href="#none" 
                        	class="btn_large_type02" 
                        	id="btn_user_del" 
                        	v-if="loginInfo.auth=='ADMIN' && vmode=='modify'" 
                        	v-on:click.prevent="memberDelete">삭제</a>
                        <a href="#none" 
                        	class="btn_large_type01" 
                        	id="btn_user_insert"
                        	v-if="vmode == 'modify'"
                        	@click.prevent="modifyMemberData">수정</a>
                        <a href="#none" 
                        	class="btn_large_type01" 
                        	id="btn_user_insert" 
                        	@click.prevent="insertMemberData"
                        	v-if="vmode=='insert'">등록</a>
                    </div>
                    </form>
                </div>
            </div>
        </div>
        <!--// container-->
	</div>
	
</body>
<script>
var _app = null;
createApp({
	mixins: [channelMixin],
	data() {
		return {
			vmode: '${vmode}',
			userid: '${userid}',
			username: '',
			password: '',
			team: '',
			part: 'MANAGER',
			tel: {
				tel1: '',
				tel2: '',
				tel3: ''
			},
			email: '',
			auth: 'WORKER',
			// start: 수정시에만 사용
			old_password: '', 
			passwordMatch: false,
			member: {}
			// end: 수정시에만 사용
		}
	},
	mounted: function () {
		_app = this;
		
		(async () => {
			await this.app__init();
			
			// 화면 모드가 수정인 경우만 실행
			if (this.vmode == 'modify') {
//	 			setTimeout(() => {
					if (this.loginInfo.userid !=  this.userid && this.loginInfo.auth != 'ADMIN') {
						alert('수정 권한이 없습니다.');
						history.back(-1);
					}
//	 			}, 100);
				
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
			}// end: 화면 모드가 수정인 경우만 실행
		})();
	},
	computed: {
		telSum: function() {
			return this.tel.tel1 + '-' + this.tel.tel2 +'-' + this.tel.tel3;
		}
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
		
		// 아이디 중복 체크
		userIdOverWriteCheck(event) {
			var value = event.target.value;
			var emptyCheckedValue = rmSpace(value);
			
			if (emptyCheckedValue != '') {
				axios.post('/member/id_check.dat', null, {params: {userid: this.userid}})
				.then(response => {
					if (response.data.userid == emptyCheckedValue) {
						alert('사용중인 아이디 입니다.');
						
						this.userid = '';
						event.target.focus();
					}
				})
				.catch(error => {
					console.log(error);
				});
			}
		},// end: 아이디 중복 체크
		
		// 새 사용자 저장
		insertMemberData() {
			var _focus = (target) => {
				this.$refs[target].focus();
				this.$data[target] = null;
			}
			
			if (rmSpace(this.userid) == '') {
				alert('아이디를 입력해 주세요.');
				_focus('userid');
				return false;
			}
			
			if (rmSpace(this.username) == '') {
				alert('이름을 입력해 주세요.');
				_focus('username');
				return false;
			}
			
			if (rmSpace(this.password) == '') {
				alert('비밀번호를 입력해 주세요.');
				_focus('password');
				return false;
			}
			
			if (this.password.length < 4 || this.password.length > 10) {
				alert('비밀번호는 최소 4자리 최대 10자리 까지 입력 가능합니다.');
				_focus('password');
				return false;
			}
			
			if (rmSpace(this.email) !== '') {
				if (!validEmail(this.email)) {
					alert('이메일 형식이 잘못 되었습니다.');
					_focus('email');
					return false;
				}
			}
			
			var paramData = {
				userid: this.userid,
				username: this.username,
				password: this.password,
				team: this.team,
				part: this.part,
				tel: this.telSum,
				email: this.email,
				auth: this.auth
			}
			
			axios.post('/member/member_insert.dat', null, {params: paramData})
			.then(response => {
				alert('사용자 등록이 완료 되었습니다.');
				location.href = '/member/list.do';
			})
			.catch(error => {
				console.log(error);
			});
			
		}, // end: insertMemberData 새맴버 저장
		// 멤버 삭제
		memberDelete() {
			if (this.loginInfo.userid !=  this.userid && this.loginInfo.auth != 'ADMIN') {
				alert('수정 권한이 없습니다.');
				history.back(-1);
			}
			
			if (confirm('사용자를 삭제 하시겠습니까?')) {
				
				axios.post('/member/member_delete.dat', null, {params: {id: this.member.id}})
				.then(response=>{
					if(response.data) {
						alert('삭제가 완료되었습니다.');
						location.href = '/member/list.do';							
					}
					else {
						alert('사용자 삭제가 실패 했습니다.');
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
		
		modifyMemberData() {
			var _focus = (target) => {
				this.$refs[target].focus();
				this.$data[target] = null;
			}
			
			if (rmSpace(this.username) == '') {
				alert('이름을 입력해 주세요.');
				_focus('username');
				return false;
			}
			
			if (rmSpace(this.old_password) != '' && this.passwordMatch) {
				if (rmSpace(this.password) == '') {
					alert('비밀번호를 입력해 주세요.');
					_focus('password');
					return false;
				}
				
				if (this.password.length < 4 || this.password.length > 10) {
					alert('비밀번호는 최소 4자리 최대 10자리 까지 입력 가능합니다.');
					_focus('password');
					return false;
				}
			}
			else {
				this.password = null;
			}
			
			if (rmSpace(this.email) !== '') {
				if (!validEmail(this.email)) {
					alert('이메일 형식이 잘못 되었습니다.');
					_focus('email');
					return false;
				}
			}
			
			var paramData = {
				userid: this.userid,
				username: this.username,
				team: this.team,
				part: this.part,
				tel: this.telSum,
				email: this.email,
				auth: this.auth
			}
			// 비밀번호 데이터는 비밀번호가 null 이 아닌경우만 추가
			if (this.password != null && rmSpace(this.password) != '') paramData.password = this.password;
			
			axios.post('/member/member_update.dat', null, {params: paramData})
			.then(response => {
				if (response.data) {
					alert('사용자 수정이 완료 되었습니다.');
					location.href = '/member/list.do';
				}
				else {
					alert('사용자 수정중 오류가 발생하였습니다.');
				}
			})
			.then(error => {
				console.log(error)
			});
			
		}, //end: 멤버 수정
	}
})
.component('header-component', HeaderComponent)
.use(pinia)
.mount('#v-app');
</script>
</html>
