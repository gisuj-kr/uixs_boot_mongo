<template>
	<!--container-->
	<div class="container">
		
		<div class="content">
			<div class="content_inner">
				<div class="tit_area">
					<h2>사용자목록</h2>
					<span class="tit_btn_area">
						<a href="/member/insert" class="btn_mid_type01" id="btn-reg-cont">사용자등록</a>
<!--                             <a href="#none" class="btn_mid_type01" id="btn-edit-cont">권한수정</a> -->
					</span>
				</div>
				<div class="board_table"> 
					<table class="tbl_st1">
						<colgroup>
							<!-- <col class="col-01" width="4%"> -->
							<col class="col-01" width="10%">
							<col class="col-02" width="12%">
							<col class="col-03" width="15%">
							<col class="col-03" width="15%">
							<col class="col-03" width="*">
							<col class="col-03" width="20%">
						</colgroup>
						<thead>
							<tr>
								<!-- <th scope="col"></th> -->
								<th scope="col"><span>이름</span></th>
								<th scope="col"><span>부서</span></th>
								<th scope="col"><span>담당업무</span></th>
								<th scope="col"><span>연락처</span></th>
								<th scope="col"><span>이메일</span></th>
								<th scope="col"><span>권한여부</span></th>
							</tr>
						</thead>
						<tbody id="user-list-wrap">
							<!-- start : 유저 리스트  -->
							<tr 
								class="user-list" 
								:data-userid="user.userid" 
								v-for="user in userList" 
								@click="moveModifyPage(user.userid)"
								style="cursor:pointer">
								<!-- 
								<td>
									<div class="input_wrap" style="padding: 0 0; max-width: none;">
										<div class="input_area cal_check">
											<div class="check" style="bottom: 0; top: -3px;">
												<input type="checkbox" id="cont_01">
												<label for="cont_01"></label>
											</div>
										</div>
									</div>
								</td> 
								-->
								<td>{{user.username == null ? '' : user.username}}</td>
								<td>{{user.team == null ? '' : user.team}}</td>
								<td>{{user.part == null ? '' : user.part}}</td>
								<td>
								{{user.tel == null ? '' : user.tel}}
								</td>
								<td>{{user.email == null ? '' : user.email}}</td>
								<td>
									<ul>
										<li style="text-align: center;">{{authkr[user.auth]}}</li>
										<!-- <li style="text-align: center;">파일관리</li> -->
									</ul>
								</td>
							</tr><!-- end : 유저 리스트  -->
							
							</tbody>
					</table>
					<div class="paging-area">
						<ul>
<!--                                 <li><a href="#none" class="prev-first"></a></li> -->
							<li><a href="#none" class="prev" :data-page="prevPage" v-on:click.prevent="movePage"></a></li>
							<li v-for="i in totalPage">
								<a href="#none" :data-page="i" :class="{'active': i==page}" v-on:click.prevent="movePage">{{i}}</a>
							</li>
							<li><a href="#none" class="next" :data-page="nextPage" v-on:click.prevent="movePage"></a></li>
<!--                                 <li><a href="#none" class="next-last"></a></li> -->
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!--// container-->

</template>


<script>
export default{
	mixins: [channelMixin],
	data() {
		return {
			userList: [], // 사용자 목록 데이터
			start: 0,
			limit: 20,
			page: 1,
			totalLen: 0,
			totalPage: 0,
			authkr: {
				MANAGER: '담당자',
				WORKER: '작업자',
				USER: '일반사용자',
				ADMIN: '관리자'
			},
			userid: null
		}
	},
	mounted() {
		(async () => {
			await this.app__init();
		})();

		this.page = this.$route.params.page ? this.$route.params.page : 1;

		// 멤버 데이터 초기조화 묶음
		this.getMemberInit();
		console.log("page: ", this.page)
	},
	computed: {
		prevPage: function () {
			let page = this.page - 1;
			
			if (page <= 0) page = 1;
			
			return page;
		},
		nextPage: function () {
			let page = this.page + 1;
			
			if (page >= this.totalPage) page = this.totalPage;
			
			return page;
		}
	},
	methods: {
		// 멤버 전체수 + 멤버 리스트 초기 조회
		async getMemberInit() {
			// 전체 게시물수
			await this.getMemberTotalCount();
			await this.getMemberList();
		}, //end: 멤버 전체수 + 멤버 리스트 초기 조회
		// 멤버 목록 조회
		async getMemberList(page=1) {
			this.page = page;
			this.start = (this.page-1) * this.limit;
			
			console.log(page, this.page)
			
			try {
				const response = await axios.get(API_URL+'/member/list.dat', {params: {start: this.start, limit: this.limit}});
				
				this.userList = response.data;
				
// 				makePaging(totalPage, page);
// 				addEvent(page);
			}
			catch(error) {
				console.log(error)
			}
		}, // end: 멤버 목록 조회
		// 멤버 전체 수 조회
		async getMemberTotalCount() {
			try {
				const response = await axios.get(API_URL+'/member/total_cnt.dat');
				
				this.totalLen = response.data;
				this.totalPage = Math.floor((this.totalLen-1) / this.limit) + 1;
			}
			catch(error) {
				console.log(error);
			}
		},// end: 멤버 전체 수 조회
		
		//end : 수정화면 이동
		moveModifyPage(userid) {
			this.$router.push({name: 'MemberUpdate', params: {
				vmode: 'modify',
				userid: userid,
				page: this.page
			}});
			// document.querySelector("input[name=userid]").value = userid;
			
			// document.getElementById('modiForm').submit();
		}, //end : 수정화면 이동
		// 목록 페이지 이동
		movePage(e) {
			let page = e.target.dataset.page <= 0 ? 
					1 : e.target.dataset.page;
			
			if (page > this.totalPage) page = this.totalPage; 
			
			console.log(page)
			this.getMemberList(e.target.dataset.page);
		}, // end: 목록 페이지 이동
	}
};
</script>
