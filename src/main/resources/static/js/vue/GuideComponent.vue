<template>
	<div class="wrapper" id="v-app">
		<!--container-->
		<iframe 
			style="width: 100%; height: 100%; overflow: hidden;"
			scrolling ="no"
			src="/static/uxguide/guide01_01.html"
			id="myiframe"
		></iframe>
		<!--// container-->
	</div>
</template>

<script>
var _app = null;

export default {
	mixins: [channelMixin],
	mounted: function () {
		_app = this;
		(async () => {
			await this.app__init();
		})();

        let iframe = document.getElementById('myiframe');

        iframe.addEventListener('load', () => {
            iframe.style.height = iframe.contentDocument.body.scrollHeight + 'px';
        });
	}
};
</script>
