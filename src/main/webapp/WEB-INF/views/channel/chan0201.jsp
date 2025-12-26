<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="../include/html_head.jsp" %>
</head>
<body>
	<div class="wrapper" id="v-app">
		<!-- header-->
		<header-component></header-component>
		<!--// header-->
		
		<!--container-->
		<div class="container">
			
			<div class="content">
				<div class="content_inner">
					<div class="tit_area">
                        <h2>채널 등록</h2>
                    </div>
					<div class="complete_area">
                        <dl>
                            <dt>등록이 완료되었습니다.</dt>
                            <dd>
                                <div class="grey_list_type">
                                    <ul class="list_type01">
                                        <li>
                                            <dl>
                                                <dt>채널명</dt>
                                                <dd>${name}</dd>
                                            </dl>
                                        </li>
                                        <li>
                                            <dl>
                                                <dt>코드</dt>
                                                <dd>${code}</dd>
                                            </dl>
                                        </li>
                                        <li>
                                            <dl>
                                                <dt>html 기본경로</dt>
                                                <dd>${doc_base}</dd>
                                            </dl>
                                        </li>
                                        <li>
                                            <dl>
                                                <dt>메뉴정보 파일경로</dt>
                                                <dd>${ia_filepath}</dd>
                                            </dl>
                                        </li>
                                        <li>
                                            <dl>
                                                <dt>사용환경</dt>
                                                <dd>${device}</dd>
                                            </dl>
                                        </li>
                                        <li>
                                            <dl>
                                                <dt>사용자구분</dt>
                                                <dd>${cuser}</dd>
                                            </dl>
                                        </li>
                                    </ul>
                                </div>
                            </dd>
                        </dl>
                    </div>
                    <div class="btn_area">
                        <a href="/channel/list.do" class="btn_large_type01">목록보기</a>
                    </div>
				</div>
			</div>
		</div>
		<!--// container-->
		
	</div>
	
</body>
<script>
var _app = null;
createApp({
	mixins: [channelMixin],
	mounted: function () {
		_app = this;
		
		(async () => {
			await this.app__init();
		})();
	}
})
.component('header-component', HeaderComponent)
.use(pinia)
.mount('#v-app');
</script>
</html>
