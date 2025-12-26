<template>
<!--container-->
<div class="container">
	<form name="channelForm" id="channelForm" method="post" >
	<input type="hidden" name="useyn" value="Y" />
	<input type="hidden" name="mongoId" v-model="$data.id" />
	<input type="hidden" name="id" v-model="$data.id" />
	<div class="content">
		<div class="content_inner">
			<div class="tit_area">
				<h2>채널 수정</h2>
			</div>
			<div class="input_section">
				<ul class="input_wrap">
					<li class="input_area">
						<label for="name">채널명</label>
						<input type="text" name="name" id="name" placeholder="채널이름을 입력하세요" v-model="channelName">
					</li>
					<li class="input_area">
						<label for="code">코드</label>
						<input 
							type="text" 
							name="code" 
							id="code" 
							placeholder="채널 코드를 입력하세요" 
							maxlength="10" 
							style="text-transform: uppercase;" 
							pattern="[A-Z]+" 
							v-model="channelCode"
							readonly>
					</li>
					<li class="input_area">
						<label for="name">html 기본경로 (ex: http://url/"기본경로"/파일경로.html)</label>
						<input 
							type="text" 
							name="doc_base" id="doc_base" 
							placeholder="ex: /doc_base" 
							v-model="doc_base"
							required>
					</li>
					<li class="input_area">
						<button type="button" class="add-menu-tab" @click="addIaTab">메뉴 TAB추가</button>
						<template v-for="(tab, index) in ia_tabs">
						<label 
							:for="'tab_name'+index" 
							v-if="addTab || ia_tabs[index].tab_name != ''"><span>TAB 이름 {{index + 1}}</span> <button type="button" class="add-menu-tab" @click="delIaTab(index)">삭제</button></label> 
						<input 
							type="text" 
							name="tab_name" 
							:id="'tab_name'+index"
							placeholder="tab 이름" 
							v-model="ia_tabs[index].tab_name"
							v-if="addTab || ia_tabs[index].tab_name != ''">
						<label :for="'ia_file'+index">메뉴(IA) 파일경로 (default: /doc_base/iaxlsx/ia_코드.xlsx)</label>
						<input 
							type="text" 
							name="ia_file" 
							:id="'ia_file'+index" 
							placeholder="ex: /doc_base/iaxlsx/파일명.xlsx" 
							v-model="ia_tabs[index].ia_file">
						</template>
						<p style="color: red">*메뉴(IA) 는 "doc_base" 의 "iaxlsx" 폴더에 "ia_코드.xlsx" 파일을 읽습니다. <br> 변경을 원하시면 "/doc_base" 를 반드시 포함한 경로와 파일명을 입력해주세요</p>
						<br>
						<p style="color: red">*반드시 (<a href="/static/iaxlsx/sample.xlsx" target="_blank">sample.xlsx</a>) 와 같은 형태로 작성 하여 기본폴더 또는 변경된 폴더에 위치 시켜 주세요.</p>
					</li>
					<li class="input_area">
						<label for="device">사용환경</label>
						<select class="selectbox" name="device" id="device" v-model="device">
							<option value="MOBILE">MOBILE</option>
							<option value="PC">PC</option>
						</select>
					</li>
					<li class="input_area">
						<label for="cuser">사용자구분</label>
						<select class="selectbox" name="cuser" id="cuser" v-model="cuser">
							<option value="개인">개인</option>
							<option value="기업">기업</option>
							<option value="통합">통합</option>
						</select>
					</li>
				</ul>
			</div>
			<div class="btn_area">
				<router-link to="/channel" class="btn_large_type02" id="cancel-insert">취소하기</router-link>
				<a href="#none" class="btn_large_type01" id="channel-insert" v-on:click.stop.prevent="sendSubmit">수정하기</a>
			</div>
		</div>
	</div>
	
	</form>
</div>
<!--// container-->
</template>

<script>
var _app = null;
export default {
	mixins: [channelMixin],
	data: function () {
		return {
			id: '${channel.id}',
			channelCode: '${channel.code}',
			channelName: '${channel.name}',
			device: '${channel.device}',
			cuser: '${channel.cuser}',
			doc_base: '${channel.doc_base}',
			ia_filepath: '${channel.ia_filepath}',
			ia_tabs: [
				{
					tab_name: '',
					ia_file: ''
				}
			],
			addTab: false,
		}
	},
	created: function () {
		_app = this;
		
	},
	mounted: function () {
		(async () => {
			await this.app__init();
		})();
		
		// param test
		console.log('Query Params:', this.$route.query);
		console.log('Route Params:', this.$route.params);

		// query string param
		if (this.$route.query.someParam) {
			console.log('Received Query Param:', this.$route.query.someParam);
		}

		// route param
		if (this.$route.params.id) {
			this.id = this.$route.params.id;
			console.log('Received Route Param (id):', this.$route.params.id);
		}

		axios.post(API_URL+'/channel/channel_one.dat', { code: this.id })
			.then(response => {
				console.log(response.data)
				if (response.data) {
					const data = response.data;

					this.id = data.id;
					this.channelCode = data.code;
					this.channelName = data.name;
					this.device = data.device;
					this.cuser = data.cuser;
					this.doc_base = data.doc_base;

					if (data.ia_tabs != null) this.ia_tabs = data.ia_tabs;
				}
			})
			.catch(error => {
				console.log(error);
			});
	},
	methods: {
		addIaTab: function () {
			this.addTab = true;
// 			this.ia_tabs[0].tab_name = this.channelName;
			
			this.ia_tabs.push({tab_name: '', ia_file: ''});
		},
		delIaTab: function (index) {
			if (this.ia_tabs.length == 1) {
				this.addTab = false;
				this.ia_tabs[0].tab_name = '';
			}
			else {
				this.ia_tabs.splice(index, 1);
			}
		},
		sendSubmit: function (e) {
			e.preventDefault();
			
			var fData = new FormData();
			
			var channelName = rmSpace(this.channelName);
			var channelCode = rmSpace(this.channelCode);
			var device = rmSpace(this.device);
			var cuser = rmSpace(this.cuser);
			var docBaseValue = rmSpace(this.doc_base);
			
			if (channelCode == '') {
				uijs.msg.alert('채널코드를 입력해주세요.');
				$('#code').val('').focus();
				return false;
			}
			
			if (channelName == '') {
				uijs.msg.alert('채널명을 입력해주세요.');
				$('#name').val('').focus();
				return false;
			}
			
// 			if (docBaseValue == '') {
// 				uijs.msg.alert('html 기본경로를 입력해 주세요.');
				
// 				$('#doc_base').val('').focus();
				
// 				return false;
// 			}
			
			if (docBaseValue != '' && docBaseValue.charAt(0) !== '/') {
				uijs.msg.alert('html 기본경로는 "/" + 영문 으로 입력해주세요.');
				
				$('#doc_base').val('').focus();
				
				return false;
			}
			
			if (device == '') {
				uijs.msg.alert('사용환경을 선택해주세요.');
				$('#device').val('').focus();
				return false;
			}
			
			if (cuser == '') {
				uijs.msg.alert('사용자구분을 선택해주세요.');
				$('#cuser').val('').focus();
				return false;
			}
			
			if(confirm('수정 하시겠습니까?')) {
				$('#code').val(channelCode.toUpperCase());
				
				var data = {
					id: this.id,
					code: channelCode.toUpperCase(),
					name: channelName,
					cuser: cuser,
					device: device,
					doc_base: docBaseValue,
					useyn: 'Y',
					ia_tabs: this.ia_tabs
				}
				
				axios.post(API_URL+"/channel/updateJson.dat", data)
				.then( () => {
					alert('수정이 완료 되었습니다.');
					// location.href = "/channel/list.do";
					this.read__channels();
					this.$router.push('/channel');
				})
				.catch(function (error) {
					console.log(error);
				});
// 				document.getElementById('channelForm').submit();
				return;
			}
			else {
				return false;
			}
		}
	}
}
</script>

