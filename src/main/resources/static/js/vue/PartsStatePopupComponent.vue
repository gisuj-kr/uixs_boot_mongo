<!-- // 파트별 작업현황 등록 팝업 컴포넌트 -->
<template>
	<div id="modal-set-work-state">
		<article>
		<header>
			<button
			aria-label="Close"
			rel="prev"
			data-target="modal-example"
			@click="closeWithDim"
			></button>
			<h3>{{partToKr[thisPart]}}</h3>
			<div>
				<a href="#none" role="button" class="add-work-state-worker" @click.prevent="addWorkStateWorker">담당자 추가+</a>
			</div>
		</header>
		
		<div class="modal-content" v-for="(data, i) in partData">
			<hr class="hr-gubun" v-if="i > 0" />
			<div class="work-part-info-wrap">
				<div class="input-group">
					<label>담당자</label>
					<select name="worker" title="담당자" @change.prevent="selectChange">
						<option value="지정안됨" >담당자</option>
						<option 
							:value="mem.username" 
							:selected="mem.username == data.worker"
							v-for="mem in workers">{{mem.username}}</option>
						<option value="강도희" :selected="'강도희' == data.worker">강도희</option>
						<option value="정기수" :selected="'정기수' == data.worker">정기수</option>
					</select>
				</div>
				<div class="input-group">
					<label>작업현황</label>
					<select name="state" title="작업상태" @change.prevent="selectChange">
						<option value="">진행상태</option>
						<option value="PENDING" :selected="data.state == 'PENDING'">대기중</option>
						<option value="WORKING" :selected="data.state == 'WORKING'">작업중</option>
						<option value="CONFIRM" :selected="data.state == 'CONFIRM'">컨펌중</option>
						<option value="EDIT" :selected="data.state == 'EDIT'">수정중</option>
						<option value="COMPLETE" :selected="data.state == 'COMPLETE'">완료</option>
						<option value="NONE">해당없음</option>
					</select>
				</div>
				<div class="input-group">
					<label :for="'part_work_rday'+i">업무요청일</label>
					<input 
						:id="'part_work_rday'+i"
						type="text" 
						class="cal_input" 
						name="part_work_rday"
						:value="data.part_work_rday" 
						placeholder="업무요청일"
						autocomplete="off"/>
				</div>
				<div class="input-group">
					<label :for="'part_work_crday'+i">완료요청일</label>
					<input 
						:id="'part_work_crday'+i"
						type="text" 
						class="cal_input" 
						name="part_work_crday" 
						:value="data.part_work_crday" 
						placeholder="완료 요청일"
						autocomplete="off"/>
				</div>
				<div class="input-group del-worker-area" v-if="i > 0">
					<button 
						:data-worker="data.worker"
						type="button" 
						class="add-work-state-worker" 
						@click.prevent="delWorker">담당자 삭제</button>
				</div>
			</div>
			
			<label>작업내용</label>
			<div class="work-part-info-wrap mt10 only-input-inline">
				<input 
					type="text" 
					class="work_content origin input-origin" 
					name="work_content" 
					placeholder="작업내용" 
					:data-worker="data.worker"/>
				<input 
					type="text" 
					class="cal_input w20per part_work_sday origin input-origin" 
					name="part_work_sday" 
					placeholder="작업 착수일"
					autocomplete="off" />
				<input 
					type="text" 
					class="cal_input w20per part_work_eday origin input-origin" 
					name="part_work_eday" 
					placeholder="작업 종료일"
					autocomplete="off" />
				<div class="dummy-like-btn"></div>
			</div>
			<!--<label>수정 및 추가 작업내용 입력</label>-->
			<div class="work-history-box">
				<div class="work-part-info-wrap mt10 only-input-inline" v-for="work_history in data.work_content">
					<input 
						type="text" 
						class="work_content" 
						style="flex: 2;"
						name="work_content" 
						placeholder="작업내용입력" 
						:data-worker="data.worker" 
						:value="work_history.content"/>
					<input 
						type="text" 
						class="cal_input w20per part_work_sday" 
						style="flex: 1;"
						name="part_work_sday" 
						placeholder="작업 착수일" 
						:value="work_history.part_work_sday"
						autocomplete="off"/>
					<input 
						type="text" 
						class="cal_input w20per part_work_eday" 
						style="flex: 1;"
						name="part_work_eday" 
						placeholder="작업 종료일"
						:value="work_history.part_work_eday"
						autocomplete="off" />
					<button type="button" class="add-work-state-worker" style="margin-left: 5px;" @click="delWorkHistory">삭제</button>
				</div>
			</div> <!--//end: work-history-box-->
			<div class="input-area">
				<label :for="'part_bigo'+i">비고</label>
				<input 
					:id="'part_bigo'+i"
					type="text" 
					class="" 
					name="bigo" 
					placeholder="비고"
					:value="data.bigo" />
			</div>
		</div>
	</article>
	<footer class="mt20">
		<button autofocus data-target="modal-example" @click.prevent="editWorkState">
		저장
		</button>
	</footer>
	</div>
</template>	

<script>
const eventBus = useEventBusStore();

export default {
	props: ['id', 'thisPart', 'detailData'],
	data: function() {
		return {
			part_users: [],
			worker: '지정안됨',
			state: '',
			work_sdate: '',
			work_temp_edate: '',
			work_edate: '',
			partToKr: {
				plan: '기획',
				design: '디자인',
				publish: '퍼블'
			},
			workers: [],
			partData: [
				
			],
			partWorkContentData: []
		}
	},
	mounted: function () {
		
		(async () => {
			await axios.get(API_URL + '/member/memberWithPart.dat', {params: {part: this.thisPart.toUpperCase()}})
				.then(response => {
					this.workers = response.data;
				})
				.catch(error => {
					console.log(error);
				});
				
			const oldData = this.detailData[this.thisPart+'_state'] || null;
			const sdate = this.detailData[this.thisPart+'_state']?.work_sdate || null;
			const temp_edate = this.detailData[this.thisPart+'_state']?.work_temp_edate || null;
			const edate = this.detailData[this.thisPart+'_state']?.work_edate || null;
			
			this.worker = this.detailData[this.thisPart+'_state']?.worker == null ? '지정안됨' : this.detailData[this.thisPart+'_state'].worker;		
			this.state = this.detailData[this.thisPart+'_state']?.state || null;	
			this.work_sdate	= sdate != null ? new Date(sdate).format('yyyy-MM-dd') : '';
			this.work_temp_edate = temp_edate != null ? new Date(temp_edate).format('yyyy-MM-dd') : '';
			this.work_edate = edate != null ? new Date(edate).format('yyyy-MM-dd') : '';
			
			if (this.detailData.part != null){
				// 현재 파트데이터만 가져옴....
				this.partData = this.detailData.part.filter(item => item.name == this.thisPart);
				
				if (this.partData.length > 0) {
					// 날짜 형식 수정
					this.partData.map((item) => {
						if (item.part_work_rday != null) {
							item.part_work_rday = dateOrEmpty(item.part_work_rday);
						}
						else {
							item.part_work_rday = dateOrEmpty(this.detailData.request_date);
						}
						
						if (item.part_work_crday != null) {
							item.part_work_crday = dateOrEmpty(item.part_work_crday);
						}
						else {
							item.part_work_crday = dateOrEmpty(this.detailData.request_complete_date);
						}
						
						if (item.work_content != null) {
							item.work_content.map((obj) => {
								obj.part_work_sday = dateOrEmpty(obj.part_work_sday);
								obj.part_work_eday = dateOrEmpty(obj.part_work_eday);
								
								return obj;	
							});
						}
						
						return item;
					});
					
				} 
			}
			
			if (oldData != null && this.partData.length <= 0) {
				const tmpPartData = {
					worker: oldData.worker == null ? '지정안됨' : oldData.worker,
					state: oldData.state,
					part_work_rday: dateOrEmpty(this.detailData.request_date),
					part_work_crday: dateOrEmpty(this.detailData.request_complete_date)
				};
				
				if (oldData.work_sdate != null || oldData.work_edate != null) {
					tmpPartData.work_content = [{
						worker: oldData.worker,
						content: '',
						part_work_sday: dateOrEmpty(oldData.work_sdate),
						part_work_eday: dateOrEmpty(oldData.work_edate)
					}];
				}
				
				this.partData = [{...tmpPartData}];
			}
			
			// 인풋에 데이트피커 설정
			setTimeout(() => {
				$('.cal_input').datepicker({
					beforeShow: (input, inst) => { 
						setTimeout(() => {
							inst.dpDiv.css({"z-index":1000});
						});
					}
				});
			}, 500);
	//					this.$refs.sel_worker.value = this.worker;
	//					this.$refs.sel_state.value = this.state;
		})();
	},
	methods: {
		closeWithDim: function (e) {
			eventBus.event.close__work_state_popup();
		},
		// 담당자, 작업상태 변경 이벤트 함수 (작업상태는 NONE 인경우만 실행 - 다른항목은 팝업의 저장 눌러야 저장됨)
		selectChange: async function (e) {
			// NONE = 해당없음 = 초기화 = 업무 분야에서 제거됨
			if (e.target.value == 'NONE') {
				if (confirm(this.partToKr[this.thisPart] + '파트가 공수에서 제외 됩니다.'))
				{
					const needWorkers = this.detailData.need_workers.filter((arr) => {
						return arr != this.thisPart;
					});

					const part = this.detailData.part.filter((arr) => {
						return arr.name != this.thisPart;
					});
					
					const data = {
						id : this.id,
						need_workers: needWorkers,
						part
					};

					try {
						const response = await axios.post(API_URL + '/work/modify_request.dat', data);
	
						if (response.data) {
							eventBus.event.read__data();
							eventBus.event.close__work_state_popup();
						}
						
					} catch (e) {
						console.log(e.message);
					}
				}
			}
			
			if (e.target.getAttribute('name') == 'worker') {
				const siblingsEl = e.target.closest('.modal-content');
				const inputWorkContent = siblingsEl.querySelectorAll("input[name=work_content]");
				
				inputWorkContent.forEach((input) => {
					input.setAttribute("data-worker", e.target.value);
				});
			} 
		},
		addWorkStateWorker: function () {
			const tmpData = {
				name: this.thisPart,
				worker : '지정안됨',
				state: 'PENDING',
				bigo: '',
				work_content: [],
				part_work_rday: dateOrEmpty(this.detailData.request_date),
				part_work_crday: dateOrEmpty(this.detailData.request_complete_date),
			}
			
			const sameDataIndex = this.partData.findIndex((item) => {
				return (
					item.name == tmpData.name && 
					item.worker == tmpData.worker
				);
			});
			
			if (sameDataIndex != -1) {
				alert('담당자가 지정 안되거나 동일한 담당자가 지정되어 있습니다. 기존 담당자를 수정후 추가해 주세요');
			}
			else {
				this.partData.push(tmpData);
			}
		},
		// 담당자 삭제
		delWorker: function (e) {
			const delWorker = e.target.dataset.worker;
			
			this.partData = this.partData.filter(part => part.name == this.thisPart && part.worker != delWorker);
			
			if (this.detailData.part != null) {
				this.detailData.part = this.detailData.part.filter(part => {
					if (part.name == this.thisPart && part.worker == delWorker) {
						return false;
					}
					else {
						return true;
					}
				});
			}
		},
		editWorkState: function () {
			const selWorker = document.querySelectorAll("select[name=worker]");
			const selState = document.querySelectorAll("select[name=state]");
			const inpBigo = document.querySelectorAll("input[name=bigo]");
			const inpWork_content = document.querySelectorAll("input[name=work_content]");
			const inpPart_work_rday = document.querySelectorAll("input[name=part_work_rday]");
			const inpPart_work_crday = document.querySelectorAll("input[name=part_work_crday]");
			const inpPart_work_sday = document.querySelectorAll("input[name=part_work_sday]");
			const inpPart_work_eday = document.querySelectorAll("input[name=part_work_eday]");
			
			this.partData = [];
			
			if(this.detailData.request_state == 'COMPLETE') {
				alert('완료된 작업은 수정할 수 없습니다.');
				return false;					
			}
			
			// 작업자 수 만큼 반복
			selWorker.forEach((item, i) => {
				const worker = item.value; // 작업자
				const state = selState[i].value; // 작업상태
				const bigo = inpBigo[i].value; 
				const part_work_rday = inpPart_work_rday[i].value;
				const part_work_crday = inpPart_work_crday[i].value;
				
				this.partWorkContentData = [];
				// 작업내용 저장
				inpWork_content.forEach((input, index) => {
					
					const value = input.value;
					const dataWorker = input.dataset.worker;
					const sday = inpPart_work_sday[index].value;
					const eday = inpPart_work_eday[index].value;
					
					if (worker !== dataWorker) return false;
					
					
					if (value.trim() != '' || sday.trim() != '' || eday.trim() != '') {
						
						const workContentItem = {
							worker: dataWorker,
							content: value, // work_content input value
							part_work_sday: dateOrNullnfm(sday),
							part_work_eday: dateOrNullnfm(eday)
						};
						this.partWorkContentData.push(workContentItem);	
						
					}
				});
				
				const partItem = {
					name: this.thisPart,
					worker,
					state,
					bigo,
					work_content: this.partWorkContentData,
					part_work_rday: dateOrNullnfm(part_work_rday),
					part_work_crday: dateOrNullnfm(part_work_crday),
				}
				
				this.partData.push(partItem);
			});
			
			// 작업내용 입력폼 초기화
			document.querySelectorAll('.origin').forEach((inp) => {
				inp.value = "";
			});
		

			// 기존 데이터에 지금 입력한 데이터와 동일한 담당자 + 파트 가 있는지 확인...
			// 방금 입력한 데이터와 동일한 데이터는 제외
			let dupData = [];
			if (this.detailData.part != null) {

				dupData = this.detailData.part.filter(data => data.name != this.thisPart);
			
			}
			
			this.partData = this.partData.filter((item, i) => {
				return (
					this.partData.findIndex((item2, j) => {
						return item.worker === item2.worker;
					}) === i
				);
			});
			
			console.log(dupData);
			
			// 기존 데이터에서 날짜 를 new Date 타입으로 수정
			dupData.map((arr) => {
				arr.part_work_rday = dateOrNullnfm(dateOrEmpty(arr.part_work_rday));
				arr.part_work_crday = dateOrNullnfm(dateOrEmpty(arr.part_work_crday));
				
				if (arr.work_content != null) {
					arr.work_content.map(arr2 => {
						arr2.part_work_sday = dateOrNullnfm(dateOrEmpty(arr2.part_work_sday));
						arr2.part_work_eday = dateOrNullnfm(dateOrEmpty(arr2.part_work_eday));
						
						return arr2;
					});
				}
				
				return arr;
			});
			
			const newData = [...dupData, ...this.partData];
			
			console.log(newData);
			
			const data = {id: this.id, part: newData};
			
			axios.post(API_URL + '/work/modify_request.dat', data)
			.then(response => {
				eventBus.event.read__data();
				eventBus.event.close__work_state_popup();
			})
			.catch(error => {
				console.log(error);
			});
		},
		// 작업내역 삭제
		delWorkHistory: function (e) {
			e.target.closest('.work-part-info-wrap').remove();
		},
	}
}
</script>

<style scoped>
footer {
	position: fixed;
	width: 100%;
	bottom: 40px;
}
#modal-set-work-state header button {
	position: fixed;
}
</style>