<template>
	<div :id="'view-list-table'+idx">
		<table class="working-list">
			<colgroup>
				<col style="width:8%;" v-if="!isMobile"/>
				<col style="width:14%;"/>
				<col style="width:13%;" v-if="!isMobile"/>
				<col style="width:13%;" v-if="!isMobile"/>
				<col style="width:15%;" v-if="!isMobile"/>
				<col style="width:16%;"/>
				<col style="width:6%;" v-if="!isMobile"/>
				<col style="width:*;"/>
			</colgroup>
			<thead>
				<tr>
					<th v-if="!isMobile">1depth</th>
					<th>2depth</th>
					<th v-if="!isMobile">3depth</th>
					<th v-if="!isMobile">4depth</th>
					<th v-if="!isMobile">화면1</th>
					<th>화면2</th>
					<th v-if="!isMobile">화면형태</th>
					<th>파일명</th>
				</tr>
			</thead>
			<tbody v-if="!isResizing">
				
				<tr v-for="json in item" 
					:class="{
						'row-color__renewal-1st': json['type'] == '1차리뉴얼', 
						'row-color__renewal-2st': json['type'] == '2차리뉴얼',
						'row-color__self--modify': json['type'] == '개선작업',
						'row-color__renewal-1st--modify' : json['type'] == '1차_개선',
						'row-color__as-is--modify' : json['type'] == 'as-is_개선'
					}">
					<td v-if="!isMobile" :class="{'new': json['type'] == 'N'}">{{ json['1depth'] }}</td>
					<td>{{ json['2depth'] }}</td>
					<td v-if="!isMobile" 
						:class="{'bank': json['type'] == 'S', 'bank_none': json['type'] == 'SX'}">{{ json['3depth'] }}</td>
					<td v-if="!isMobile">{{ json['4depth'] }}</td>
					<td v-if="!isMobile">{{ json['화면1'] }}</td>
					<td>{{ json['화면2'] }}</td>
					<td  v-if="!isMobile" style="text-align: center;">{{ json['화면형태'] }}</td>
					<td><a :href="makeLink(json['경로'], json['파일명'])" @click.prevent="openHtmlP">{{ json['파일명'] }}</a></td>
				</tr>
				
			</tbody>
		</table>
	</div> <!-- end. #view-list-table -->
</template>

<script>
export default {
	props: {
		docBase : {
			type: String,
			default: ''
		},
		isMobile: {
			type: Boolean,
			default: false
		},
		excelJsonHeader: {
			type: Array,
			default: () => []
		},
		idx: {
			type: Number,
			required: true
		},
		item: {               // data 대신 item 으로 바꿔 봤습니다
			type: Array,
			default: () => [],
			required: true
		}
	},
  	emits: ['open-html'],
	// ← 여기!
	data() {
		return {
			isResizing: false,
			isOpen: true, // 기본값
		// 필요하다면 내부 상태를 여기에 선언
		}
	},
	mounted () {
		let timer;
		window.addEventListener('resize', () => {
			this.isResizing = true;
			clearTimeout(timer);
			timer = setTimeout(() => this.isResizing = false, 150);
		});
	},
	methods: {
		toggle() {
			this.isOpen = !this.isOpen;
		},
		openHtmlP: function (e) {
			this.$emit('open-html', e);
		},
		makeLink: function (path, filename) {
			if (path !== null && path != undefined && path != '') {
				path =  path.trim();

				filename = filename && filename.trim();
				
				if (path.charAt(path.length - 1) == '/') path = path.substr(0, path.length-1); 
				
				var pathTmp = path.substr(1);
				var pathArr = pathTmp.split('/');
				
				path = pathArr.filter(item => {
					if (item == this.docBase.substr(1)) {
						return false;
					}
					else {
						return true;
					}
				}).join('/');
				
				if (path.charAt(0) != '/') path = '/'+path;
				
				if (filename != undefined && filename != '') {
					if (filename.substr(-5) != '.html') {
						filename = filename + '.html';
					}
				}

				return this.docBase + path + '/' + filename;
			}
			
			else {
				return '';
			}
		},
	}
}
</script>