/**
 * vue2.0 에서 Vuex 를 사용한 store
 */

 
const _store = new Vuex.Store({
	state: {
		hasMessage: false,
		channels: [],
		loginInfo: {},
		isMobile: false,
		urlPrefix: '/uixs_boot'
	},
	mutations: {
		setChannels: function (state, payload) {
			state.channels = payload;
		},
		setLoginInfo: function (state, payload) {
			state.loginInfo = payload;
		},
		setIsMobile: function (state, payload) {
			state.isMobile = payload;
		},
		setHasMessage: function (state, payload) {
			state.hasMessage = payload;
		}
	},
	actions: {
		//start:  채널정보 읽어오기
		read__channels: async function(context) {
			try {
				const response = await axios.post('/channel/list.dat');
				context.commit('setChannels', response.data);
				console.log('channelsread')
			}
			catch(error) {
				console.log('channel read::'+error);
			}
		},//end: read__channels
		
		//start: 로그인 정보 read
		read__loginInfo: async function (context) {
			try {
				const response = await axios.get('/member/login_info.dat');
				
				if (response.data.userid == null) {
					if (location.pathname !== '/member/login.do') {
						location.href = '/member/login.do';		
					}
				}
				else {
					context.commit('setLoginInfo', response.data);
				}
			}
			catch(error) {
				console.log(error);
			}
		}, //end: read__loginInfo
	},
	getters: {
		getChannels(state) {
			return state.channels;
		},
		getLoginInfo(state) {
			return state.loginInfo;
		},
		getIsMobile(state) {
			return state.isMobile;
		},
		getHasMessage(state) {
			return state.hasMessage;
		}
	},
	modules: {
		workStore: {
			namespaced: true,
			state: {
				workDetailOpener: null
			},
			mutations: {
				setWorkDetailOpener: function (state, payload) {
					state.workDetailOpener = payload;
				}
			},
			actions: {
				
			},
			getters: {
				getWorkDetailOpener: function (state) {
					return state.workDetailOpener;
				}
			}
		}
	}
});
