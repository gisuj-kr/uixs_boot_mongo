/**
 * 
 */
const { createWebHistory, createRouter } = VueRouter;

const LoginComponent = defineAsyncComponent(
	() => loadModule('/static/js/vue/LoginComponent.vue', vue3LoadOption)
);
// 작업 레이아웃 컴포넌트
const WorkLayout = defineComponent({
	template: '<router-view></router-view>'
});
// 작업 목록 메인 컴포넌트
const WorkComponent = defineAsyncComponent(
	() => loadModule('/static/js/vue/WorkComponent.vue', vue3LoadOption)
);
// 작업 신규 요청 컴포넌트
const WorkRequest = defineAsyncComponent(
	() => loadModule('/static/js/vue/WorkRequestComponent.vue', vue3LoadOption)
);
// html 코딩 리스트 메인 컴퍼넌트
const HtmlComponent = defineAsyncComponent(
	() => loadModule('/static/js/vue/HtmlComponent.vue', vue3LoadOption)
);
// 가이드 메인 컴퍼넌트
const GuideComponent = defineAsyncComponent(
	() => loadModule('/static/js/vue/GuideComponent.vue', vue3LoadOption)
);
// 채널 관리 메인 컴퍼넌트
const ChannelLayout = defineComponent({
	template: '<router-view></router-view>'
});
const ChannelListComponent = defineAsyncComponent(
	() => loadModule('/static/js/vue/ChannelListComponent.vue', vue3LoadOption)
);
const ChannelUpdateComponent = defineAsyncComponent(
	() => loadModule('/static/js/vue/ChannelUpdateComponent.vue', vue3LoadOption)
);
const ChannelInsertComponent = defineAsyncComponent(
	() => loadModule('/static/js/vue/ChannelInsertComponent.vue', vue3LoadOption)
);

// 회원 관리 컴포넌트
const MemberLayout = defineComponent({
	template: '<router-view></router-view>'
});

// 회원목록
const MemberListComponent = defineAsyncComponent(
	() => loadModule('/static/js/vue/MemberListComponent.vue', vue3LoadOption)
);
const MemberInsertComponent = defineAsyncComponent(
	() => loadModule('/static/js/vue/MemberInsertComponent.vue', vue3LoadOption)
);
const MemberModifyComponent = defineAsyncComponent(
	() => loadModule('/static/js/vue/MemberModifyComponent.vue', vue3LoadOption)
);

const routes = [
	{
		path: '/',
		component: () => loadModule('/static/js/vue/LoginComponent.vue', vue3LoadOption)
	},
	{
		path: '/login',
		component: () => loadModule('/static/js/vue/LoginComponent.vue', vue3LoadOption)
	},
	{
		path: '/work',
		component: () => loadModule('/static/js/vue/BlankLayoutComponent.vue', vue3LoadOption),
		children: [
			{
				path: '',
				name: 'WorkList',
				component: () => loadModule('/static/js/vue/WorkComponent.vue', vue3LoadOption)
			},
			{
				path: 'request',
				name: 'InsertWorkRequest',
				component: () => loadModule('/static/js/vue/WorkRequestComponent.vue', vue3LoadOption)
			},
			{
				path: 'request/:id',
				name: 'ModifyWorkRequest',
				component: () => loadModule('/static/js/vue/ModifyWorkRequestComponent.vue', vue3LoadOption)
			},
			{
				path: 'users',
				name: 'UserWorkingList',
				component: () => loadModule('/static/js/vue/UserWorkingList.vue', vue3LoadOption)
			},
		]
	},
	//	{
	//		path: '/work/request',
	//		component: WorkRequest
	//	},
	{
		path: '/html',
		component: () => loadModule('/static/js/vue/HtmlComponent.vue', vue3LoadOption)
	},
	{
		path: '/guide',
		component: () => loadModule('/static/js/vue/GuideComponent.vue', vue3LoadOption)
	},
	{
		path: '/channel',
		component: () => loadModule('/static/js/vue/BlankLayoutComponent.vue', vue3LoadOption),
		children: [
			{
				path: '',
				component: () => loadModule('/static/js/vue/ChannelListComponent.vue', vue3LoadOption)
			},
			{
				path: 'update/:id',
				name: 'ChannelUpdate',
				component: () => loadModule('/static/js/vue/ChannelUpdateComponent.vue', vue3LoadOption)
			},
			{
				path: 'insert',
				component: () => loadModule('/static/js/vue/ChannelInsertComponent.vue', vue3LoadOption)
			}
		]
	},
	{
		path: '/member',
		component: () => loadModule('/static/js/vue/BlankLayoutComponent.vue', vue3LoadOption),
		children: [
			{
				path: '',
				name: 'MemberListBase',
				component: () => loadModule('/static/js/vue/MemberListComponent.vue', vue3LoadOption)
			},
			{
				path: 'insert',
				component: () => loadModule('/static/js/vue/MemberInsertComponent.vue', vue3LoadOption)
			},
			{
				path: 'update/:vmode/:userid/:page',
				name: 'MemberUpdate',
				component: () => loadModule('/static/js/vue/MemberModifyComponent.vue', vue3LoadOption)
			}
		]
	},
	{
		path: '/member/:page',
		component: () => loadModule('/static/js/vue/BlankLayoutComponent.vue', vue3LoadOption),
		children: [
			{
				path: '',
				name: 'MemberList',
				component: () => loadModule('/static/js/vue/MemberListComponent.vue', vue3LoadOption)
			},
		]
	},
];

const router = createRouter({
	history: createWebHistory(),
	routes,
});

