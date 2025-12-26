<template>
	<div class="wrapper" id="v-app">
		<!--container-->
		<div class="container">
			
			<div class="content">
				<div class="content_inner">
					<div class="tit_area">
                        <h2>채널관리</h2>
						<span class="tit_btn_area">
							<a href="/channel/insert.do" class="btn_mid_type01">채널 등록하기</a>
						</span>
                    </div>
					<ul class="view_list_wrap arrow" id="late_list">
						<!-- start for -->
						<li class="channel-item" :data-code="channel.code" v-for="channel in channels">
							<div class="list link " @click="toggleChannel">
								<a href="#none"><strong>{{channel.name}}</strong> <span :class="bulTypeClass(channel.cuser)">{{channel.cuser}}</span></a>
								<div class="btn_edit_area">
									<button type="button"  class="btn_small01 write btn-channel-modi" @click="goChannelEdit($event, channel.id)" >수정</button>
									<button type="button" class="btn_small01 delete btn-channel-del" v-on:click.prevent="delChannel(channel.id)">삭제</button>
								</div>
							</div>
							<div class="grey_list_type" style="display: none">
								<div class="point_area">
									<strong>작업현황</strong>
									<ul>
										<li><span>신청중</span> <em>({{channel.req_pending_cnt}}건)</em></li>
										<li><span>작업중</span> <em>({{channel.req_working_cnt}}건)</em></li>
										<li><span>작업완료</span> <em>({{channel.req_complete_cnt}}건)</em></li>
									</ul>
									<div class="point_btn_area">
<!-- 										<a href="#none" class="btn_small01 go work-list" :data-code="channel.code">바로가기</a> -->
									</div>
								</div>
								<ul class="list_type01 mt20">
									<li>
										<dl>
											<dt>등록일자</dt>
											<dd>{{(new Date(channel.regdate)).format('yyyy.MM.dd')}}</dd>
										</dl>
									</li>
<!-- 									<li> -->
<!-- 										<dl> -->
<!-- 											<dt>화면목록</dt> -->
<!-- 											<dd> -->
<!-- 												<a href="#none" class="btn_small01 go ia-insert"  @click="goIaInsert($event, channel.code)" :data-code="channel.code">바로가기</a> -->
<!-- 											</dd> -->
<!-- 										</dl> -->
<!-- 									</li> -->
								</ul>
							</div>
						</li> <!-- end for -->
						
					</ul>
				</div>
			</div>
		</div>
		<!--// container-->
		
	</div>
	
</template>

<script>
var _app = null;
export default {
	mixins: [channelMixin],
	data: function () {
		return {
			work_cnt: {}
		}
	},
	mounted: function () {
		_app = this;
		(async () => {
			// await this.app__init();
		})();
	},
	methods: {
		bulTypeClass: function(cuser) {
			switch(cuser) {
				case '개인':
					return 'bul_type01';
					break;
				case '통합' :
					return 'bul_type02';
					break;
				case '기업' :
					return 'bul_type03';
					break;
				default: return '';
			}
		},
		goWorkList: function (e, channelCode) {
			e.preventDefault();
			
			this.selectedCode = this.getSelectedCode(channelCode);
			
			location.href = '/work/list.do';
		},
		goIaInsert: function (e, channelCode) {
			e.preventDefault();
			
			this.selectedCode = this.getSelectedCode(channelCode);
			
			location.href = '/ia/manage';
		},
		goChannelEdit: function (e, id) {
			console.log(id)
			var form = $('<form>', {
				method: 'post'
				, name: 'channelUpdateForm'
				, id: 'channelUpdateForm'
				, action: '/channel/update.do'
			});
			
			var inputCode = $('<input>', {
				name: 'id'
				, type: 'hidden'
				, value: id
			}).appendTo(form);
			
			form.appendTo('body');
			
			form.submit();
		},
		toggleChannel: function (e) {
			var tg = $(e.target);
			
			var site_code = tg.parent().attr('data-code');
			
			if (tg.hasClass('active')) {
				tg.removeClass('active').next('.grey_list_type').hide();
			}
			else {
				tg.addClass('active').next('.grey_list_type').show();
			}
		},
		delChannel: function (id) {
			var data = {
				id: id
			};
			
			if (confirm("채널을 삭제 하시겠습니까?")) {
				axios.post("/channel/delete.dat", data)
				.then((response) => {
					if (response.data > 0) {
						this.read__channels();
					}
				})
				.catch((error) => {
					console.log(error);
				});
			}
		}
	}
};
</script>
