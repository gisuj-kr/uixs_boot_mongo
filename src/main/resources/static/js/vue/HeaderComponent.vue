<template>
<div>
	<div class="header" :class="{'mobile': isMobile, 'active': isOpen}">
		<div>
			<h1 v-show="!isMobile"><router-link to="/work"></router-link></h1>
			<div class="channel_select" :class="{mb40: !isMobile}">
				<div class="input_section">
					<ul class="input_wrap">
						<li class="input_area">
							<label for="">채널명 </label>
							<div class="select_list channel-list" v-bind:class="{on: showChannelList}"> <!--dropdown  열릴때 on클래스 추가, 아래 ul태그 block-->
								<div class="active" @click="openChannelList">
									{{selectedChannel.name}} <!-- span class="bul_type01">{{selectedChannel.cuser}}</span> -->
								</div>
								<ul style="display: none; z-index: 10000;" class="option-list" v-show="showChannelList">
									<li><a href="#none" @click.prevent="changeChannelEvent('all')">전체</a></li>
									<li v-for="item in channels"><a href="#none" @click.prevent="changeChannelEvent(item.code)">{{item.name}} <!-- <span class="bul_type01">{{item.cuser}}</span>--></a></li>
									
									<li><router-link to="/channel" v-if="!isMobile" @click="openChannelList">채널관리</router-link></li>
								</ul>
							</div>
						</li>
					</ul>
				</div>
			</div>
			<a href="#none" class="btn_small01 nav-log-out" 
				:style="!isMobile && 'margin-left: 40px; transform: translateY(-20px);'" @click.prevent="logout">로그아웃</a>
			<a href="#none" class="btn_small01 menu-close" v-if="isMobile" @click="closeMobileMenu">X</a>
			
			<span class="bell" style="
				display: inline-block;
				top: 28px;
				position: absolute;
				margin-left: 20px;
				font-size: 12px; display: none">
					<a href="#" class="alam_msg work_label04" style="border-radius: 10px; padding: 2px 4px;" data-cnt="0">알림 0</a>
			</span>
			<nav>
				<ul>
					<li><router-link to="/work" :class="{on: urlPath == '/work'}">작업관리</router-link><!--<a-- href="/work/list.do" :class="{on: selectedMenu == 'work'}">작업관리</a--></li>
					<li><router-link to="/html" :class="{on: urlPath == '/html'}">화면목록</router-link><!--<a-- href="/ia/html_list.do" :class="{on: selectedMenu == 'menu'}">화면목록</a--></li>
					<li><router-link to="/guide" :class="{on: urlPath == '/guide'}">UX/UI 가이드</router-link><!--<a-- href="/guide/guide.do" :class="{on: selectedMenu == 'guide'}">UX/UI 가이드</a--></li>
					<li v-if="!isMobile"><router-link to="/channel" :class="{on: urlPath == '/channel'}">채널관리</router-link><!--<a-- href="/channel/list.do" :class="{on: selectedMenu == 'channel'}">채널관리</a--></li>
					<li v-if="!isMobile"><router-link to="/member" :class="{on: urlPath == '/member'}">사용자</router-link><!--<a-- href="/member/list.do" :class="{on: selectedMenu == 'member'}">사용자</a--></li>
				</ul>
			</nav>
			<a href="#none" class="btn_logout" style="display: none;">로그아웃</a>
		</div>
	</div>
	<button class="mobile-menu-open" @click="openMenu" v-if="isMobile"></button>
</div>
</template>

<script>
export default {
	props: ['urlPath'],
	mixins: [channelMixin],
	setup() {
		const store = useChannelStore()

		return {
			channel: Vue.computed({
				get: () => store.channel,
				set: val => store.setChannel(val)
			})
		}
	},
	data: function () {
		return {
			isOpen: false,
		}
	},
	mounted: function () {
		(async () => {
			await this.read__channels();
		})();

		// const btnWrapper = document.createElement('div');
		// const btnBefore = document.querySelector('.container');

		// document.querySelector('.wrapper').insertBefore(btnWrapper, btnBefore);
		/*
		var btnComponent = Vue.extend(this);
		new btnComponent({
			template: `<button class="mobile-menu-open" @click="openMenu" v-if="$parent.isMobile"></button>`,
			parent: this,
			methods: {
				openMenu: function () {
					this.$emit('open-menu');
				}
			}
		})
		.$on('open-menu', this.openMenu)
		.$mount(btnWrapper);
		*/
	},
	// computed: {
	// 	channel: {
	// 		get() {
	// 			return useChannelStore().channel
	// 		},
	// 		set(val) {
	// 			useChannelStore().setChannel(val)
	// 		}
	// 	}
	// },
	methods: {
		changeChannelEvent: function (param) {
			const channelStore = useChannelStore();
			// channelStore 의 selectedHeaderChannel 을 변경
			// parma 이 all 이 아닌 경우 는 
			channelStore.selectedHeaderChannel = param;

			if (param !== 'all') {
				// mixin 의 selectedChannel
				this.selectedChannel = this.getSelectedChannel(param);
				// 세션 스토리지에 채널 코드 저장
				this.setSessionChannel(param);
			}
			// 채널 리스트 닫기
			this.showChannelList = false;
		},
		closeMobileMenu: function () {
			this.isOpen = false;
		},
		openMenu: function () {
			this.isOpen = true;
		},
		logout: function () {
			axios.post(API_URL+'/member/logout.dat')
			.then(() => this.$router.push('/'))
			.catch(error => {
				console.log(error);
			});
		}
	}
}
</script>
	
		

