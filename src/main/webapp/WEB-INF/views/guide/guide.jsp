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
		<header-component></header-component>
		<!--// header-->
		
		<!--container-->
		<iframe 
			style="width: 100%; height: 100%; overflow: hidden;"
			scrolling ="no"
			src="/static/uxguide/guide01_01.html"
			id="myiframe"
		></iframe>
		
		<!--// container-->
		
	</div>
	
</body>

<script>
var _app = null;
createApp({
	mixins: [channelMixin],
	mounted: function () {
		_app = this;
		(async () => {
			await this.app__init();
		})();
	}
})
.component('header-component', HeaderComponent)
.use(pinia)
.mount('#v-app');

let iframe = document.getElementById('myiframe');

iframe.addEventListener('load', () => {
	iframe.style.height = iframe.contentDocument.body.scrollHeight + 'px';
});

</script>
</html>
