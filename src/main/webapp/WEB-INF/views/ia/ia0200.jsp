<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="../include/html_head.jsp" %>
</head>
<body>
	<div class="wrapper" id="v-app">
		<!--header-->
		<header-component></header-component>
		<!--// header-->
		
		<!--container-->
		<div class="container no-max-value">
            
            <div class="content">
                <div class="content_inner">
                	<div class="html-list-util-gnb-area" v-if="ia_tabs.length > 1">
                		<a 
                			href="#none"
                			 v-for="(tab, index) in ia_tabs" 
                			 :class="{selected : tabIndex == index}" 
                			 @click.prevent="siteMapChange(index)">{{tab.tab_name}}</a>
                	</div>
                    <div class="tit_area" >
                        <h2 style="">화면목록</h2>
                        <span class="tit_btn_area" v-if="!isMobile">
                            <a :href="iaDownloadUrl" target="_blank" class="btn_mid_type01">메뉴 다운로드(excel)</a>
                        </span>
                    </div>
                    
                    <ul class="view_list_wrap bor_top">
                        <li>
                        	<ul class="pb_w_tab">
                        		<li v-for="(header, index) in EXCEL_JSON_HEADER">
                        			<a :href="'#'+header">{{header}}</a>
                        		</li>
                        	</ul>
                        	
                            <div class="grey_list_type white" style="padding: 0">
                                <div class="con_area">
                                    <div class="tit_area" style="display: ">
                                        <h2 style="height: 50px"></h2>
                                        <div class="input_section" style="display: ">
                                            <ul class="input_wrap">
                                                <li class="input_area search_case">
                                                    <label for="search-string">검색</label>
                                                    <input type="text" placeholder="화면명을 입력하세요" v-model="searchText" @keyup.enter="searchHandler" :style="isMobile && 'width: 82%'">
                                                    <button type="button" class="btn_search" @click.prevent="searchHandler"></button>
                                                </li>
                                            </ul>
                                        </div>
                                    </div>
                                    
                                    <template v-for="(data, index) in searched_data">
                                    <div class="menu_wrap" v-if="data.length > 0">
	                                    <div class="menu_tit on">
											<button 
												type="button"
												:id="EXCEL_JSON_HEADER[index]" 
												aria-expanded="true"
												:aria-controls="'view-list-table'+index"
												@click="accordion">{{EXCEL_JSON_HEADER[index]}}</button>
										</div>
	                                    <div :id="'view-list-table'+index" >
		                                    <table border="0" class="working-list">
											    <colgroup>
											      <col style="width:13%;" v-if="!isMobile"/>
											      <col style="width:14%;"/>
											      <col style="width:13%;" v-if="!isMobile"/>
											      <col style="width:13%;" v-if="!isMobile"/>
											      <col style="width:13%;" v-if="!isMobile"/>
											      <template v-if="!isMobile && EXCEL_JSON_HEADER[index] == '금융상품'">
											      	<col style="width:13%;"/>
											      </template>
											      <col style="width:8%;" v-if="!isMobile"/>
											      <col style="width:*;"/>
											    </colgroup>
											    <thead>
												    <tr>
												      <th v-if="!isMobile">1depth</th>
												      <th>2depth</th>
												      <th v-if="!isMobile">3depth</th>
												      <th v-if="!isMobile">4depth</th>
												      <th v-if="!isMobile">5depth</th>
												      <template v-if="!isMobile && EXCEL_JSON_HEADER[index] == '금융상품'">
													      <th>6depth</th>
												      </template>
												      <th v-if="!isMobile">화면형태</th>
												      <th>파일명</th>
												    </tr>
											    </thead>
											    <tbody>
											    	<tr v-for="json in data" 
											    		:class="{
											    			'new_line': json['type'] == 'N' || json['type'] == 'NN', 
											    			'toss': json['type'] == 'C',
											    			'none': json['del'] == '1',
											    			'cl_m' : json['type'] == 'M'
											    		}">
											    		<td v-if="!isMobile" :class="{'new': json['type'] == 'N'}">{{ json['1depth'] }}</td>
											    		<td>{{ json['2depth'] }}</td>
											    		<td v-if="!isMobile" 
											    			:class="{'bank': json['type'] == 'S', 'bank_none': json['type'] == 'SX'}">{{ json['3depth'] }}</td>
											    		<td v-if="!isMobile">{{ json['4depth'] }}</td>
										    			<td v-if="!isMobile">{{ json['5depth'] }}</td>
											    		<template v-if="!isMobile && EXCEL_JSON_HEADER[index] == '금융상품'">
											    			<td>{{ json['6depth'] }}</td>
											    		</template>
											    		<td  v-if="!isMobile">{{ json['화면형태'] }}</td>
											    		<td><a :href="makeLink(json['경로'], json['파일명'])" @click.prevent="openHtml">{{ json['파일명'] }}</a></td>
											    	</tr>
											    	
											    	
											    	<!-- 	<template v-for="(ia, index) in searchList"> -->
													<!--     <tr v-if="ia.parent == '#'" class="linetitle"> -->
													<!--       	<td :colspan="!isMobile ? 6 : ''"> -->
													<!--         	<div class="sub-tit">{{ia.text}}</div> -->
													<!--       	</td> -->
													<!--     </tr> -->
													<!--     <tr v-else> -->
													<!--     	<td v-if="!isMobile">{{index}}</td> -->
													<!--       	<td style="text-align: left;" v-if="ia.link != '#' && ia.link != '' && ia.link"> -->
													<!--       		<a :href="docRoot+ia.link"  @click.prevent="openHtml(docRoot+ia.link, $event)">{{iaPath(ia.parent)}} / {{ia.text}}</a> -->
													<!--       	</td> -->
													<!--       	<td style="text-align: left;" v-else> -->
													<!--       		 {{iaPath(ia.parent)}}  / {{ia.text}} -->
													<!--       	</td> -->
													<!--       	<td v-if="!isMobile"><a :href="docRoot+ia.link" @click.prevent="openHtml(docRoot+ia.link, $event)">{{ia.link}}</a></td> -->
													<!--       	<td v-if="!isMobile">{{ia.link == '#' || !ia.link ? '' : (new Date(ia.update_date)).format('yyyy-MM-dd')}}</td> -->
													<!--       	<td v-if="!isMobile">{{ia.link == '#' || !ia.link ? '' : confirmText(ia.confirm)}}</td> -->
													<!--       	<td v-if="!isMobile">{{ia.link == '#' || !ia.link ? '' : publText(ia.publ)}}<span class="state"><span class="complete"></span></span></td> -->
													<!--     </tr> -->
													<!--     </template> -->
											    </tbody>
		                                   	</table>
	                                    </div> <!-- end. #view-list-table -->
	                                </div>
                                    </template>
                                </div>
                            </div>
                        </li>
                        
                    </ul>
                </div>
            </div>
        </div>
		<!--// container-->
		
		<!-- 모바일 html 미리보기 -->
		<div class="modal_layer_pop" v-show="htmlShow" @click="closeHtmlFromDim">
			<div class="mobile_view" style="position:absolute">
	            <ul class="box_type_check" style="position: absolute; top: -58px; left: 5px;">
	                <li>
	                    <input type="radio" id="resize-viewer1" name="resize-viewer" value="375" class="resize-viewer" checked>
	                    <label for="resize-viewer1">375px</label>
	                </li>
	                <li >
	                    <input type="radio" id="resize-viewer2" name="resize-viewer"   value="360" class="resize-viewer">
	                    <label for="resize-viewer2">360px</label>
	                </li>
	                <li>
	                    <input type="radio" id="resize-viewer3" name="resize-viewer" value="320" class="resize-viewer">
	                    <label for="resize-viewer3">320px</label>
	                </li>
	                <li>
	                    <input type="text" readonly id="resize-viewer-custom" name="resize-viewer" value="375px" class="resize-viewer" style="font-size: 12px; border: 1px solid #ccc">
	                </li>
	            </ul>
	            <iframe src="about:blank" frameborder="0" id="html-view-area" style="width: 100%; height: 100%; display: none; overflow: hidden;"></iframe>
	        </div>
        </div>
		
		<div class="ia-go-top"><button type="button" @click="goTop">top</button></div>
	</div> <!-- // end : wrapper -->

</body>
<script src="/static/js/xlsx.full.min.js"></script>
<script>

var _app = null;

createApp({
	mixins: [channelMixin],
	data: {
		list: [],
		iaList: [], // ia list
		searchList: [],
		searchText: '',
		htmlShow: false, // html 클릭하여 미리보기 여부
		resizing: false, // html 미리보기 팝업창 리사이징 여부
		perList: [], // 개이뱅킹
		bizList: [], // 기업뱅킹
		pdtList: [], // 금융상품몰 리스트
		autList: [], // 인증센터
		serviceList: [], // 서비스
		tabGubun: '개인뱅킹', // 개인뱅킹 선택인경우
		tabChannel: false,
		EXCEL_JSON_HEADER: [],
		EXCEL_JSON_DATA: [],
		searched_data: [],
		domain: 'http://13.125.6.144:7001',
		iaDownloadUrl: '',
		tabIndex: 0,
		ia_tabs: []
	},
	created: function () {
		_app = this;
		
		(async () => {
			await this.read__channels();;
			
			this.ia_tabs = this.selectedChannel.ia_tabs;
			
			try {
				
				var iafile = this.selectedChannel.doc_base + '/iaxlsx/ia_'+this.selectedChannel.code+'.xlsx';
				
				if (this.ia_tabs[0].ia_file != '') {
					iafile = this.ia_tabs[0].ia_file;
				}
				
// 				const url = 'https:/static/iaxlsx/menu_tree.xlsx';
				const url = this.domain+iafile;
				const workbook = XLSX.read(await (await fetch(url)).arrayBuffer());
				
			  	//엑셀파일의 시트 정보를 읽어서 JSON 형태로 변환한다.
				workbook.SheetNames.forEach((item, index, array) => {
	       	  		this.EXCEL_JSON_HEADER.push(workbook.SheetNames[index]);
	             	this.EXCEL_JSON_DATA.push(XLSX.utils.sheet_to_json(workbook.Sheets[item]));
// 	             	XLSX.utils.sheet_to_json(worksheet, {header:1}); // 1번 포함해서 읽음
	          	});//end. forEach
				
	          	this.searched_data = this.EXCEL_JSON_DATA;
	          	this.iaDownloadUrl = url;
			}
			catch(error) {
				console.log(error)
			}
		})();
	},
	computed: {
		docRoot: function () {
			var channel = this.getChannelCode;
			var url = '';
			
			return url;
		},
	},
	mounted: function () {
		// 미리보기 영역 resize
	    $('.mobile_view').resizable({
	        minWidth: 320,
	        minHeight: 640,
	        handles: 'w,e',
	        start: function(event, ui) {
	            $('#html-view-area').css('pointer-events', 'none');
	            
	            _app.resizing = true; // html 미리보기 팝업창 리사이징 여부
	        },
	        stop: function (event, ui) {
	            $('#html-view-area').css('pointer-events', 'auto');
	            $('#resize-viewer-custom').val(ui.size.width + 'px');
	            
	            ui.element[0].style.left = 'auto';
	            
	            setTimeout(function () {
	            	_app.resizing = false; // html 미리보기 팝업창 리사이징 여부
	            })
	        }
	    });
	    
	    // 미리보기 화면 리사이즈
	    $('.resize-viewer').on('change', function () {
	        var size = $(this).val();

	        $('.mobile_view').width(size);
	        
	        $('#resize-viewer-custom').val(size+'px');
	    })
	    .on('focus', function () {
	    	$('.box_type_check').find('input:radio').each(function () {
	    		$(this).prop('checked', false);
	    	})
	    });
	    
	 	// 미리보기영역 사이즈 커스텀 input 에서 엔터 친 경우 미리보기창 사이즈 조절
	    $('#resize-viewer-custom').on('keyup', function () {
	    	var size = parseInt($(this).val());
	    	
	    	$('.mobile_view').width(size);
	    });
	},
	methods: {
		matchType: function (type, matchStr) {
			var rtn = false;
			
			if (type != undefined && type != null){
				var typeArr = type.split('-');
				
				if (type.length > 0) {
					var matchedTxt = type.find((item) => item == matchStr);
					
					if (matchedTxt != undefined && matchedTxt == matchStr) rtn = true;
				}
			}
			
			return rtn;
		},
		makeLink: function (path, filename) {
			if (path !== null && path != undefined && path != '') {
				path =  path.trim();
				filename = filename && filename.trim();
				
				if (path.charAt(path.length - 1) == '/') path = path.substr(0, path.length-1); 
				
				var pathTmp = path.substr(1);
				var pathArr = pathTmp.split('/');
				
				path = pathArr.filter(item => {
					if (item == this.selectedChannel.doc_base.substr(1)) {
						return false;
					}
					else {
						return true;
					}
				}).join('/');
				
				if (path.charAt(0) != '/') path = '/'+path;
				
				if (filename != undefined && filename != '') {
					if (filename.substr(-5) != '.html') {
						filename = filename + '.html';
					}
				}
				
				return this.selectedChannel.doc_base + path + '/' + filename;
			}
			
			else {
				return '';
			}
		},
		
		
		// 모바일 html 미리보기 팝업 열기
		openHtml: function (e) {
	        var pathname = this.domain+$(e.target).attr('href');
	        var channel = this.getChannelCode;
	        
	        if (e.shiftKey || this.isMobile) {
	        	window.open(pathname + '?' + Date.now());
	        	e.preventDefault();
	        }
	        else {
		        $('#html-view-area').attr('src', pathname + '?' + Date.now());
		        setTimeout(function () {
		            $('#html-view-area').show();
		        }, 50);
		        
		        this.htmlShow = true;
				this.resizing = false;
	        }
		},
		// 모바일 html 미리보기 팝업 딤드영역 클릭으로 닫기
		closeHtmlFromDim: function (e) {
			if (e.target.getAttribute('class') == 'modal_layer_pop' && !this.resizing) {
				this.htmlShow = false;
			}
		},
		iaPath: function (parent) {
			var path = this.makePath(parent);
			
			return path.reverse().join('/');
		},
		makePath: function (parent, path=[]) {
			this.searchList.forEach(ia => {
				
				if (ia.id != parent) return;
				
				if (ia.id == parent) {
					path.push(ia.text);
					
					this.makePath(ia.parent, path);
				}
			});
			
			return path; //newPath;
		},
		accordion: function (e) {
			var isOpen = e.target.getAttribute('aria-expanded');
			var targetId = e.target.getAttribute('aria-controls');
			var target = document.getElementById(targetId);
			
			if (isOpen == 'true') {
				target.style.display = 'none';
				e.target.setAttribute('aria-expanded', false);
			}
			else {
				target.style.display = 'block';
				e.target.setAttribute('aria-expanded', true);
			}
		},
		searchHandler: function () {
			var app = this;
			
			if (this.searchText.replace(/\s/g, '') == '') {
				this.searched_data = this.EXCEL_JSON_DATA;
			}
			else {
				this.searched_data = [];
				
				this.EXCEL_JSON_DATA.forEach(item => {
					var tmpArr = item.filter(filteredItem => {
						return (
							filteredItem['1depth'] && filteredItem['1depth'].indexOf(this.searchText) != -1 || 
							filteredItem['2depth'] && filteredItem['2depth'].indexOf(this.searchText) != -1 || 
							filteredItem['3depth'] && filteredItem['3depth'].indexOf(this.searchText) != -1 || 
							filteredItem['4depth'] && filteredItem['4depth'].indexOf(this.searchText) != -1 || 
							filteredItem['화면명'] && filteredItem['화면명'].indexOf(this.searchText) != -1 || 
							filteredItem['파일명'] && filteredItem['파일명'].indexOf(this.searchText) != -1);
					});
					
					this.searched_data.push(tmpArr);
				}); //end. forEach
			}
		},
		// 탑으로 이동
		goTop: function () {
			window.scrollTo({
			  top: 100,
			  behavior: "smooth",
			});
		},
		siteMapChange: async function (tabIndex) {
			this.tabIndex = tabIndex;
			
			try {
				var iafile = this.selectedChannel.doc_base + '/iaxlsx/ia_'+this.selectedChannel.code+'.xlsx';
				
				if (this.ia_tabs[tabIndex].ia_file != '') {
					iafile = this.ia_tabs[tabIndex].ia_file;
				}
				
				const url = this.domain+iafile;
				const workbook = XLSX.read(await (await fetch(url)).arrayBuffer());
				
				this.EXCEL_JSON_HEADER = [];
				this.EXCEL_JSON_DATA = [];
			  	//엑셀파일의 시트 정보를 읽어서 JSON 형태로 변환한다.
				workbook.SheetNames.forEach((item, index, array) => {
	       	  		this.EXCEL_JSON_HEADER.push(workbook.SheetNames[index]);
	             	this.EXCEL_JSON_DATA.push(XLSX.utils.sheet_to_json(workbook.Sheets[item]));
	          	});//end. forEach
				
	          	this.searched_data = this.EXCEL_JSON_DATA;
	          	this.iaDownloadUrl = url;
			}
			catch(error) {
				console.log(error)
			}
		}, //end siteMapChange
	}
})
.component('header-component', HeaderComponent)
.use(pinia)
.mount('#v-app');

</script>
</html>
