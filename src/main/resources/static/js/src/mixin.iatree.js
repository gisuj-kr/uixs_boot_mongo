const iatreeMixin = {
	methods: {
		makeTreeData: function(dataArr, parentId = '#', result = []) {
	        
	        dataArr.forEach($.proxy(function (el) {
	            if (el.parent === parentId) {
	                result.push(el);
	                this.makeTreeData(dataArr, el.id, result);
	            }
	        }, this));
	        
	        return result;
	    },
	    getSiblings: function (tree, nodeID, parent) {
	        var tree = tree.jstree(true),
	            parentNode = tree.get_node(parent),
	            aChildren = parentNode.children,
	            aSiblings = [];
	    
	        aChildren.forEach(function(c){
	            if(c !== nodeID) aSiblings.push(c);
	        });
	        return aSiblings;
	    },
	    addNode: function (obj, callback) {
	        var node = obj.node;
	        
	        var text = node.text;
	        var parent = node.parent == '#' ? 0 : node.parent;
	        var name = node.text;
	        var link = '#';
	        var site_code = uijs.channel.get();
	     
	        uijs.ajaxDef({
	            ajaxOption: {
	                url: '/ia/add',
	                data: {'parent': parent, 'text': text, 'name': name, 'link': link, 'site_code': site_code},
	            }
	            , callback: function (data) {
	                if (data) {
	                    callback.call(this, data);
	                }
	            }
	        });
	    },
	    updateNode: function (obj, mode) {
	        var node = obj.node;
	        var data = {'id': node.id};
	        
	        if (obj.old_parent != undefined) {
	            if (obj.old_parent != obj.parent) {
	                data.parent = obj.parent;
	            }
	        }
	        
	        if (obj.old != undefined) {
	            if (obj.text != obj.old) {
	                data.text = obj.text;
	            }
	        }
	        
	        if (mode != undefined && mode == 'move') {
	            data.update_date = new Date();
	            data.sort = obj.position;
	            
	            data.sort_list = [];
	            
	            $('#'+node.id).closest('ul.jstree-children').find('.jstree-node').each(function () {
	                data.sort_list.push({
	                    id: $(this).attr('id')
	                    , sort: $(this).index()
	                });
	            });
	        }
	        
	         uijs.ajaxDef({
	            ajaxOption: {
	                url: '/ia/update',
	                data: data
	            }
	        });
	    },
	    deleteNode: function (obj) {
	        var node = obj.node;
	        
	         uijs.ajaxDef({
	            ajaxOption: {
	                url: '/ia/delete',
	                data: {'id': node.id}
	            }
	        });
	    }
	}
};