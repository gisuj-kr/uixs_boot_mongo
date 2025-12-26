<template>

	<div class="tree-node" :class="'depth-' + depth">
		<div class="tree-item">
			<button 
				class="tree-toggle" 
				:class="{ 'expanded': isExpanded, 'no-children': !hasChildren }"
				@click="toggleExpanded"
			></button>
			<div class="tree-label" v-html="highlightedName" @click="iaSelectHandler"></div>
		</div>
		
		<!-- 파일 정보 -->
		<!-- <div v-if="data.files && data.files.length > 0">
			<div v-for="file in data.files" :key="file.rowIndex" class="file-info">
				<div class="file-name" v-html="highlightFileName(file.fileName)"></div>
				<div class="file-details">
					<span v-if="file.type" class="badge badge-type">{{ file.type }}</span>
					<span v-if="file.screenType" class="badge badge-screen">{{ file.screenType }}</span>
					<span v-if="file.path" class="file-path">📁 {{ file.path }}</span>
				</div>
			</div>
		</div> -->
		
		<!-- 하위 노드들 -->
		<div v-if="hasChildren" class="tree-children" :class="{ 'expanded': isExpanded }">
			<tree-node-component 
				v-for="([childName, childData]) in sortedChildren"
				:key="childName"
				:name="childName"
				:data="childData"
				:depth="depth + 1"
				:search-term="searchTerm"
			></tree-node-component>
		</div>
	</div>

</template>

<script>
const TreeNodeComponent = defineAsyncComponent(() => {
	return loadModule('/static/js/vue/TreeNodeComponent.vue',vue3LoadOption);
});

export default {
	components: {
		TreeNodeComponent
	},
	props: {
		name: String,
		data: Object,
		depth: {
			type: Number,
			default: 1
		},
		searchTerm: String
	},
	data() {
		return {
			isExpanded: false
		};
	},
	computed: {
		hasChildren() {
			return Object.keys(this.data.children || {}).length > 0;
		},
		sortedChildren() {
			return Object.entries(this.data.children || {});
				// .sort(([a], [b]) => a.localeCompare(b));
		},
		highlightedName() {
			if (!this.searchTerm || this.searchTerm.trim().length === 0) {
				return this.name;
			}
			const regex = new RegExp(`(${this.searchTerm})`, 'gi');
			return this.name.replace(regex, '<span class="highlight">$1</span>');
		}
	},
	watch: {
		searchTerm(newTerm) {
			// 검색어가 있을 때 자동으로 노드 확장
			if (newTerm && this.hasChildren) {
				setTimeout(() => {
					this.isExpanded = true;
				}, 100);
			}
		}
	},
	inject: ['closeTreeModal', 'insertIaPath'],
	methods: {
		toggleExpanded() {
			if (this.hasChildren) {
				this.isExpanded = !this.isExpanded;
			}
		},
		highlightFileName(fileName) {
			if (!fileName || !this.searchTerm || this.searchTerm.trim().length === 0) {
				return fileName;
			}
			const regex = new RegExp(`(${this.searchTerm})`, 'gi');
			return fileName.replace(regex, '<span class="highlight">$1</span>');
		},
		iaSelectHandler(e) {
			let parents = [];
			let depth = this.depth;

			while(depth){
				console.log(depth)
				parents.push($(e.target).closest('.depth-'+depth).children('.tree-item').find('.tree-label').text());

				depth--;
			};

			const category = $(e.target).closest('.sheet-node').children('.tree-item').find('.tree-label').text();
			const path = category + '/' + (parents.reverse().join('/'));

			this.insertIaPath(path);
			this.closeTreeModal();
		},
	
	},
	
};
</script>