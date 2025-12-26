/**
 * 공통 mixins
 */
var channelMixin = {
	data: function () {
		return {
			selectedChannelIndex: 0,
			selectedChannel: {},
			showChannelList: false,
		}
	},
	watch: {
		channels: {
			handler: function (newChannels) {
				var channelMatchCnt = 0, channelCode;

				newChannels.forEach(channel => {
					if (channel.code == localStorage.getItem('channel')) {
						channelMatchCnt += 1;
					}
				});

				if (!channelMatchCnt) {
					channelCode = newChannels[0].code;
				}
				else {
					channelCode = localStorage.getItem('channel') == null ? newChannels[0].code : localStorage.getItem('channel');
				}

				this.selectedChannel = this.getSelectedChannel(channelCode);
				// 세션 스토리지에 채널 코드 저장
				this.setSessionChannel(channelCode);
			},
			immediate: false
		},
	},
	computed: {
		...mapState(useCommonStore, ['isMobile']),
		...mapState(useChannelStore, ['channels']),
		...mapState(useLoginInfoStore, ['loginInfo']),
		getChannelCode: function () {
			return localStorage.getItem('channel') == null ?
				(Object.keys(this.selectedChannel).length ? this.selectedChannel.code : 'PB_M') :
				localStorage.getItem('channel');
		},
	},
	mounted: function () {
		const commonStore = useCommonStore();

		if (window.innerWidth < 760) {
			commonStore.isMobile = true;
			document.querySelector('.container').classList.add('mobile-container');
			document.querySelector('.wrapper').classList.add('mobile-wrapper');
		}
		else {
			commonStore.isMobile = false;
			document.querySelector('.wrapper').classList.remove('mobile-container');
			document.querySelector('.wrapper').classList.remove('mobile-wrapper');
		}

		window.addEventListener('resize', this.sizeCheck, true);
	},
	destroyed: function () {
		window.removeEventListener('resize', this.sizeCheck, true);
	},
	methods: {
		...mapActions(useChannelStore, ['read__channels']),
		...mapActions(useLoginInfoStore, ['read__loginInfo']),
		app__init: async function () {
			// 채널 로드
			//			await this.read__channels();
			// 로그인정보 로드
			await this.read__loginInfo();
		},
		setSessionChannel: function (code) {
			sessionStorage.setItem('channel', code);
			window.localStorage.setItem('channel', code);
		},
		getSelectedChannel: function (code) {
			var app = this;
			return this.channels.filter(function (channel, index) {
				if (channel.code == code) {
					app.selectedChannelIndex = index;

					return channel;
				}
			})[0];
		},
		openChannelList: function () {
			this.showChannelList = !this.showChannelList;
		},
		// 채널 변경
		changeChannel: function (code) {
			console.log(code)
			if (code !== 'all') {
				console.log('change code all??')
				//this.listAllChannel = false;
				//				document.getElementById('listAllChannel')
			}
			this.selectedChannel = this.getSelectedChannel(code);

			// 채널 리스트 닫기
			this.showChannelList = false;

			// 세션 스토리지에 채널 코드 저장
			this.setSessionChannel(code);

			const eventBus = useEventBusStore();
			// 전체 리스트 조회
			eventBus.event.loadList();

			// 검수요청수 다시 조회
			eventBus.event.setConfirmCount();

			// work 의 모든 리스트 load
			//			this.allChannelWork();

			//			location.reload();
		},


		sizeCheck: function (e) {
			const commonStore = useCommonStore();

			if (e.target.innerWidth <= 760) {
				//				this.setIsMobile(true);
				commonStore.isMobile = true;
				document.querySelector('.container').classList.add('mobile-container');
				document.querySelector('.wrapper').classList.add('mobile-wrapper');
			}
			else {
				//				this.setIsMobile(false);
				commonStore.isMobile = false;
				document.querySelector('.container').classList.remove('mobile-container');
				document.querySelector('.wrapper').classList.remove('mobile-wrapper');
			}
		},
		// ajax loading
		loading: function (flag) {

			var loadingContainer = $('<div/>', { class: 'loading-container2' });
			var loading = $('<div/>', { class: 'loading' });
			var loadingText = $('<div/>', { id: 'loading-text' }).css({ 'color': '#fff' }).text('loading');

			loading.appendTo(loadingContainer);
			loadingText.appendTo(loadingContainer);

			if (flag == 'start' || flag == true) {
				console.log('loading start')
				// 딤드영역 생성
				var dimd = document.createElement('div');
				dimd.id = 'loading-dim';
				dimd.style.backgroundColor = '#000';
				dimd.style.opacity = '0.5';
				dimd.style.position = 'fixed';
				dimd.style.width = '100%';
				dimd.style.height = '100%';
				dimd.style.left = 0;
				dimd.style.top = 0;
				// 딤드 바디에 추가
				document.body.appendChild(dimd);

				// 로딩바 추가
				loadingContainer.appendTo('body');
			}
			else {
				// 로딩바 제거
				$('.loading-container2').remove();
				$('#loading-dim').remove();
				console.log('loading end')
				// 딤드 대상
				//	            var dimd = document.getElementById('loading-dim');
				//	            // 딤드 제거	
				//	            document.body.removeChild(dimd);
			}
		},
		// ia 목록 생성
		getMenuPathString: function (requestIaList) {
			var path = requestIaList.map(function (item) {
				var itemArr = item.PATH.split('[>]');
				var itemArrRevers = itemArr.reverse();

				return itemArrRevers.join(' > ');
			});

			return path;
		}
	}
};

var resizeMixin = {
	mounted: function () {
		const commonStore = useCommonStore();

		if (window.innerWidth < 760) {
			commonStore.isMobile = true;
			document.querySelector('.container').classList.add('mobile-container');
			document.querySelector('.wrapper').classList.add('mobile-wrapper');
		}
		else {
			commonStore.isMobile = false;
			document.querySelector('.wrapper').classList.remove('mobile-container');
			document.querySelector('.wrapper').classList.remove('mobile-wrapper');
		}

		window.addEventListener('resize', this.sizeCheck, true);
	},
	destroyed: function () {
		window.removeEventListener('resize', this.sizeCheck, true);
	},
	methods: {
		sizeCheck: function (e) {
			const commonStore = useCommonStore();

			if (e.target.innerWidth <= 760) {
				//				this.setIsMobile(true);
				commonStore.isMobile = true;
				document.querySelector('.container').classList.add('mobile-container');
				document.querySelector('.wrapper').classList.add('mobile-wrapper');
			}
			else {
				//				this.setIsMobile(false);
				commonStore.isMobile = false;
				document.querySelector('.container').classList.remove('mobile-container');
				document.querySelector('.wrapper').classList.remove('mobile-wrapper');
			}
		},
		setIsMobile: function (payload) {
			this.$store.commit('setIsMobile', payload);
		}
	}
};

//app.mixin([channelMixin, resizeMixin]);