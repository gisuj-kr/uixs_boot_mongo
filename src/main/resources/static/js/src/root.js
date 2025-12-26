'use strict'

// 개발 api url
const API_URL = window.location.hostname == "localhost" ? 'http://localhost:7001' : '';

window.vue3LoadOption = {
	moduleCache: {
		vue: Vue
	},
	async getFile(url) {

		const res = await fetch(url);
		if (!res.ok)
			throw Object.assign(new Error(res.statusText + ' ' + url), { res });
		return {
			getContentData: asBinary => asBinary ? res.arrayBuffer() : res.text(),
		}
	},
	addStyle(textContent) {

		const style = Object.assign(document.createElement('style'), { textContent });
		const ref = document.head.getElementsByTagName('style')[0] || null;
		document.head.insertBefore(style, ref);
	},
}

const { loadModule } = window['vue3-sfc-loader'];

const { createApp, defineComponent, defineAsyncComponent } = Vue; // vue = Vue.createApp
const { createPinia, defineStore, mapState, mapActions } = Pinia;
const pinia = createPinia();

if (!String.prototype.startsWith) {
	String.prototype.startsWith = function (search, pos) {
		return this.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
	};
}

const useCommonStore = defineStore('common', {
	state: () => {
		return {
			isMobile: false,
			hasMessage: false,
			loginInfo: {}
		}
	},
	actions: {
		dateDiff(_date1, _date2) {
			// var diffDate_1 = _date1 instanceof Date ? _date1 : new Date(_date1);
			// var diffDate_2 = _date2 instanceof Date ? _date2 : new Date(_date2);

			// diffDate_1 = new Date(diffDate_1.getFullYear(), diffDate_1.getMonth() + 1, diffDate_1.getDate());
			// diffDate_2 = new Date(diffDate_2.getFullYear(), diffDate_2.getMonth() + 1, diffDate_2.getDate());

			// var diff = diffDate_2.getTime() - diffDate_1.getTime();
			// diff = Math.ceil(diff / (1000 * 3600 * 24));

			// return diff;

			const d1 = moment(_date1);
			const d2 = moment(_date2);

			return d2.diff(d1, 'days');
		},
		dateOrNull(date) {
			if (date.trim() != '') {
				return moment(new Date(date)).format('YYYY-MM-DD');
			}
			else {
				return null;
			}
		},
		dateOrEmpty(date) {
			if (date != null) {
				return moment(new Date(date)).format('YYYY-MM-DD');
			}
			else {
				return '';
			}
		},
		dateOrNullnfm(date) {
			if (date.trim() != '') {
				return new Date(date);
			}
			else {
				return null;
			}
		},
		rmSpace(value) {
			if (value == undefined) return '';

			return value.replace(/\s/g, '');
		},
		validEmail(email) {
			var re = /\S+@\S+\.\S+/;
			return re.test(email);
		}
	}
});

const useChannelStore = defineStore('channel', {
	state: () => {
		return {
			channels: [],
			selectedHeaderChannel: 'all',
			channel: 'all'
		}
	},
	actions: {
		//start:  채널정보 읽어오기
		async read__channels() {
			try {
				const response = await axios.post(API_URL + '/channel/list.dat');

				this.channels = response.data;
				//				console.log('channelsread')
			}
			catch (error) {
				console.log('channel read::' + error);
			}
		},//end: read__channels
	},
	setChannel(val) {
		this.channel = val;
	}
});

const { useRouter } = VueRouter;

const useLoginInfoStore = defineStore('logininfo', {
	state: () => {
		return {
			loginInfo: {},
		}
	},
	actions: {
		//start: 로그인 정보 read
		async read__loginInfo() {
			const router = useRouter();

			try {
				const response = await axios.get(API_URL + '/member/login_info.dat', { withCredentials: true });
				if (response.data.userid == null) {
					//					if (location.href !== '/member/login.do') {
					if (router.currentRoute.value.path != '/') {
						router.push('/');
					}
				}
				else {
					this.loginInfo = response.data;
					//					context.commit('setLoginInfo', response.data);
				}
			}
			catch (error) {
				console.log(error);
			}
		}, //end: read__loginInfo
	},
});

// vue 2.0 의 EventBus 대체용 store
const useEventBusStore = defineStore('eventBus', {
	state: () => {
		return {
			isMobile: false,
			workDetailOpener: null,
			event: {}, // 함수실행
		}
	},
	actions: {
		addEvent(fnName, fn) {
			if (typeof fn === 'function') {
				this.event[fnName] = fn;
				// 				fn.apply();
			} else {
				console.warn('전달된 인자값이 함수가 아닙니다.');
			}
		},
	}
});

//const MessageComponent = {
//	template: `<div class="msg-alert" v-if="on" role="button" @click="openMessage" style="cursor:pointer"></div>`,
//	data: function () {
//		return {}
//	},
//	computed: {
//		on: function () {
//			return this.$store.getters.getHasMessage
//		}
//	},
//	methods: {
//		openMessage: function () {
//			uijs.viewMsg();
//			this.$store.commit('setHasMessage', false);
//		}
//	}
//};

//공통 헤더 컴포넌트 정의
const HeaderComponent = defineAsyncComponent(() => loadModule('/static/js/vue/HeaderComponent.vue', vue3LoadOption));

//app.component('msg-component', MessageComponent);




