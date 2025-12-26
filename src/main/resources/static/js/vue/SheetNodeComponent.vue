<template>
	<div class="tree-node sheet-node">
		<div class="tree-item sheet-item">
			<button 
				class="tree-toggle" 
				:class="{ 'expanded': isExpanded, 'no-children': !hasChildren }"
				@click="toggleExpanded"
			></button>
			<div class="tree-label sheet-label" v-html="highlightedSheetName"></div>
		</div>
		
		<!-- 시트 하위 노드들 -->
		<div v-if="hasChildren" class="tree-children sheet-children" :class="{ 'expanded': isExpanded }">
			<tree-node-component 
				v-for="([childName, childData]) in sortedChildren"
				:key="childName"
				:name="childName"
				:data="childData"
				:depth="1"
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
		sheetName: String,
		treeData: Object,
		searchTerm: String
	},
	data() {
		return {
			isExpanded: true // 시트는 기본적으로 펼쳐진 상태
		};
	},
	computed: {
		hasChildren() {
			return Object.keys(this.treeData || {}).length > 0;
		},
		sortedChildren() {
			return Object.entries(this.treeData || {});
			// return Object.entries(this.treeData || {})
			// 	.sort(([a], [b]) => a.localeCompare(b));
		},
		highlightedSheetName() {
			if (!this.searchTerm) return this.sheetName;
			const regex = new RegExp(`(${this.searchTerm})`, 'gi');
			return this.sheetName.replace(regex, '<span class="highlight">$1</span>');
		}
	},
	methods: {
		toggleExpanded() {
			if (this.hasChildren) {
				this.isExpanded = !this.isExpanded;
			}
		}
	},
	mounted() {
		// console.log(this.treeData)
	}
}
</script>