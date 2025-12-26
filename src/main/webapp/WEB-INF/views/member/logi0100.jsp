<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="../include/html_head.jsp" %>

</head>
<body class="set-admin-body">
	<div class="wrapper " id="v-app">
		<login-component></login-component>
	</div>
	
	<script>
	// 비동기 컴포넌트 로드
    const LoginComponent = defineAsyncComponent(() => loadModule('/static/js/vue/Login.vue', vue3LoadOption));
	
	app.component('login-component', LoginComponent);
	app.mount('#v-app');
    </script>
</body>

</html>
