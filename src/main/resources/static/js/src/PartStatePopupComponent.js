function PartStatePopupComponent() {
	return {
		template: `
			<dialog id="modal-set-work-state">
		      <article>
		        <header>
		          <button
		            aria-label="Close"
		            rel="prev"
		            data-target="modal-example"
		            onclick="toggleModal(event)"
		          ></button>
		          <h3>기획</h3>
		        </header>
		        <div class="modal-content">
		        	<select name="worker" title="담당자" v-model="worker">
		        		<option value="">담당자</option>
		        		<option value="">담당자</option>
		        	</select>
		        	
		        	<select name="state" title="작업상태" v-model="state">
		        		<option value="">대기중</option>
		        		<option value="">작업중</option>
		        		<option value="">컨펌중</option>
		        		<option value="">수정중</option>
		        		<option value="">완료</option>
		        	</select>
		        	
		        	<input type="text" name="work_sdate" placeholder="착수일" v-model="work_sdate" />
		        	<input type="text" name="work_temp_edate" placeholder="완료 예정일" v-model="work_temp_edate" />
		        	<input type="text" name="work_edate" placeholder="완료일" v-model="work_edate"/>
		        </div>
		        <footer>
		          <button autofocus data-target="modal-example">
		            등록하기
		          </button>
		        </footer>
		      </article>
		    </dialog>
		 `,
		 props: ['id'],
		 data: function() {
			return {
				part_users: [],
				worker: '',
				state: '',
				work_sdate: new Date().format('yyyy.MM.dd'),
				work_temp_edate: new Date().format('yyyy.MM.dd'),
				work_edate: new Date().format('yyyy.MM.dd')
			}
		},
	} // end return
}// end
