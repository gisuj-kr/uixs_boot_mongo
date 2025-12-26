<template>
    <div class="container">
        <form name="loginForm" id="loginForm" method="post" @submit.prevent="login">
            <div class="content">
                <div class="content_inner">
                    <div class="tit_area">
                        <h2>로그인</h2>
                    </div>
                    <div class="input_section mt20" :style="isMobile && 'padding: 0 40px'">
                        <ul class="input_wrap" style="padding-top: 0;">
                            <li class="input_area">
                                <label for="userid">아이디</label>
                                <input
                                    ref="userid"
                                    type="text"
                                    name="userid"
                                    id="userid"
                                    placeholder="아이디"
                                    v-model="userid"
                                />
                            </li>
                            <li class="input_area">
                                <label for="password">비밀번호</label>
                                <input
                                    ref="password"
                                    type="password"
                                    name="password"
                                    id="password"
                                    placeholder="비밀번호"
                                    autocomplete="off"
                                    v-model="password"
                                    @keyup.enter="login"
                                />
                            </li>
                        </ul>
                    </div>
                    <div class="btn_area">
                        <a href="#" class="btn_large_type01 login" @click.prevent="login">로그인</a>
                    </div>
                </div>
            </div>
        </form>
    </div>
</template>

<script>
export default {
    mixins: [channelMixin],
    data() {
        return {
            userid: '',
            password: '',
        };
    },
    methods: {
        async login() {
            if (this.userid.trim() === '') {
                alert('아이디를 입력해 주세요.');
                return;
            }
            if (this.password.trim() === '') {
                alert('비밀번호를 입력해 주세요.');
                return;
            }
            try {
                const response = await axios.post(API_URL+'/member/login.dat', null, {
                    params: {
                        userid: this.userid,
                        password: this.password,
                    },
					withCredentials: true
                });

                const data = response.data;
                if (data.LOGIN === 'SUCCESS') {
                    this.$router.push('/work');
                    // location.href = '/work/list.do';
                } else {
                    alert(data.FAIL_TYPE === 'ID' ? '사용자정보가 없습니다.' : '비밀번호가 잘못되었습니다.');
                }
            } catch (error) {
                alert('알 수 없는 오류가 발생했습니다. 관리자에게 문의하세요.');
            }
        },
    },
};
</script>

<style scoped>

</style>