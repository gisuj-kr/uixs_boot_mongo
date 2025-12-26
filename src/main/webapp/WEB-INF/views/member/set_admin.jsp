<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="../include/html_head.jsp" %>

</head>
<body class="set-admin-body">
	<div class="wrapper" id="v-app">
		
		<!--container-->
		<div class="container">
			<form name="loginForm" id="loginForm" method="post" onsubmit="return false">
			<div class="content">
				<div class="content_inner">
					<div class="tit_area">
                        <h2 :style="isMobile && 'text-align: center'">관리자 설정</h2>
                    </div>
					
					<div class="input_section mt20" :style="isMobile && 'padding: 0 40px'">
                        <ul class="input_wrap" style="padding-top: 0;">
                          
							<li class="input_area">
                                <label for="userid">아이디</label>
                                <input ref="userid" type="text" name="userid" id="userid" placeholder="아이디" v-model="userid" readonly>
                            </li>
							<li class="input_area">
                                <label for="password">비밀번호</label>
                                <input ref="password" type="password" @keyup.enter="login" name="password" id="password" placeholder="비밀번호" autocomplete="off" v-model="password">
                            </li>
                        </ul>
                    </div>
                    <div class="btn_area">
                        <!-- <a href="work0100.html" class="btn_large_type02">취소</a> -->
                        <a href="#" class="btn_large_type01"  @click="login">저장</a>
                    </div>
				</div>
			</div>
			</form>
		</div>
		<!--// container-->
	</div>

	<script>
	new Vue({
		el: '#v-app',
		store: _store,
		mixins: [resizeMixin, channelMixin],
		data: function () {
			return {
				userid: 'admin',
				password: '',
			}
		},
		methods: {
			login: function (e) {
				e.preventDefault();
				
				if (rmSpace($('input[name=password]').val()) == '') {
					alert('비밀번호를 입력해 주세요.');
					this.$refs.password.focus();
					return false;
				}
				
				if (confirm("관리자 비밀번호는 변경할 수 없습니다. 저장 하시겠습니까?")) {
					axios.post('/member/admin_insert.dat', null, {
						params: {userid: this.userid, password: this.password}
					})
					.then(function (response) {
						var data = response.data;
						
						if (response.status < 500) {
							alert("관리자 설정이 완료 되었습니다.");
							location.href = "/member/login.do";
						}
					});
				}
				else {
					return false;
				}
			}
		} 
	});
    </script>

</body>

</html>
