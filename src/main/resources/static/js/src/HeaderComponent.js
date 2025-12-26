/**
 * 페이지 헤더
 */
const HeaderComponent = function () {
	const headerTemplete = `
	<div class="header" :class="{'mobile': isMobile, 'active': isOpen}">
		<div >
			<h1 v-show="!isMobile"><a href="/work/list.do"></h1>
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
									<li v-for="item in channels"><a href="#none" @click.prevent="changeChannel(item.code)">{{item.name}} <!-- <span class="bul_type01">{{item.cuser}}</span>--></a></li>
									
									<li><a href="/channel/list.do" v-if="!isMobile">채널관리</a></li>
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
					<li><a href="/work/list.do" :class="{on: selectedMenu == 'work'}">작업관리</a></li>
					<li><a href="/ia/html_list.do" :class="{on: selectedMenu == 'menu'}">화면목록</a></li>
					<li><a href="/guide/guide.do" :class="{on: selectedMenu == 'guide'}">UX/UI 가이드</a></li>
					<li v-if="!isMobile"><a href="/channel/list.do" :class="{on: selectedMenu == 'channel'}">채널관리</a></li>
					<li v-if="!isMobile"><a href="/member/list.do" :class="{on: selectedMenu == 'member'}">사용자</a></li>
				</ul>
			</nav>
			<a href="#none" class="btn_logout" style="display: none;">로그아웃</a>
		</div>
		
	</div>`;
	
	
	return {
		template: headerTemplete,
		mixins: [resizeMixin, channelMixin],
		data: function () {
			return {
				isOpen: false
			}
		},
		created: function() {
			
		},
		mounted: function () {
			var app = this;
			const btnWrapper = document.createElement('div');
			const btnBefore = document.querySelector('.container');
			
			document.querySelector('.wrapper').insertBefore(btnWrapper, btnBefore);
			
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
		},
		computed: {
			selectedMenu: function () {
				if (location.pathname.indexOf('/work') != -1) {
					return 'work';
				}
				else if (location.pathname.indexOf('/ia') != -1) {
					if (location.pathname.indexOf('cal') != -1) {
						return 'cal';
					}
					else {
						return 'menu'
					}
				}
				else if (location.pathname.indexOf('/guide') != -1) {
					return 'guide'
				}
				else if (location.pathname.indexOf('/channel') != -1) {
					return 'channel'
				}
				else if (location.pathname.indexOf('/member') != -1) {
					return 'member';
				}
			}
		},
		methods: {
			closeMobileMenu: function () {
				this.isOpen = false;
			},
			openMenu: function () {
				this.isOpen = true;
			},
			logout: function () {
				axios.post('/member/logout.dat')
				.then(response => {
					location.href = '/member/login.do';
				})
				.catch(error => {
					console.log(error);
				});
			}
		}
	};
};
		
app.component('header-component', HeaderComponent());

