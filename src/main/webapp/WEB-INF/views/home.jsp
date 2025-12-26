<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="include/html_head.jsp" %>
<script>
// location.href= '/member/login.do';
</script>
</head>
<body>

	<div class="wrapper" id="app">
		<router-view></router-view>
	</div>

<script>
console.log(router)
console.log('sssss')
app.use(router)
app.mount('#app');
</script>

</body>

</html>