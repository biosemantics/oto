/**
 * This file contains all the dragging and saving functions in hierarchy page.
 * Author: Fengqiong
 */

var is_drag = false;
var obj_x, obj_y;
var old_x, old_y;
var drag_obj;
var drag_clone;
var drag_clone_content; // the content in drag_clone div
var target_node;
var old_target_node;
var current_obj; // the object clicked on
var tag_chosen; // get the whole chosen tag div
var drag_from;
var i;
var target_div_name = "dTreeNode";
var maskName = "dragging_mask"; // <div id="dragging_mask">

/**
 * calculate if clicked is a draggable
 * 
 * @param mouseOnObj
 */
var _isDraggable = false;
function isDraggable(mouseOnObj) {
	var test = mouseOnObj.className;
	if (test == "tag" || test == "structure" || test == "structure_label") {
		_isDraggable = true;
		return true;
	} else {
		_isDraggable = false;
		return false;
	}
}

/**
 * calculate tag_chosen
 * @param obj
 */
function calc_tag_chosen(obj) {
	var classname = obj.className;
	if (classname == "structure") {
		tag_chosen = obj;
	} else if (classname == "tag") {
		tag_chosen = obj.getElementsByClassName("structure")[0];
	} else if (classname == "structure_label") {
		tag_chosen = obj.parentNode;
	} else {
		alert("error");
	}
}

/**
 * mouse down event handler record the old position of the obj and cursor
 */
function mouse_down_handler(e) {
	//console.log("mouse down");
	var evn;
	evn = e || event;
	event = evn;

	if (evn.target) {
		current_obj = evn.target;
	} else {
		current_obj = evn.srcElement;
	}
	//console.log("current obj classame: " + current_obj.className);
	if (isDraggable(current_obj)) {
		calc_tag_chosen(current_obj);		
		set_drag_from();
		if (drag_from == "right") {
			if (tag_chosen.nextSibling != null
					&& tag_chosen.nextSibling.className == "clip") {
				alert("Only leaf node can be dragged.");
				return false;
			}
		}

		is_drag = true;
		getClone();

		// set drag_clone position and visibility
		obj_x = current_obj.offsetLeft
		obj_y = tag_chosen.offsetTop;
		var temp = current_obj;
		while (temp.offsetParent) {
			temp = temp.offsetParent;
			obj_x = obj_x + temp.offsetLeft;
			obj_y = obj_y + temp.offsetTop;
		}

		if (drag_from == "left") {
			obj_y = obj_y - document.getElementById('availableTags').scrollTop;
		} else {
			obj_y = obj_y - document.getElementById('hierarchyTree').scrollTop;
		}
		drag_clone.style["top"] = obj_y + "px";// bug
		drag_clone.style["left"] = obj_x + "px";
		$("#" + maskName).hide();
	}
	return false;
}

function getLastChild(node) {
	// alert(node.className);
	var temp = node.firstChild;
	var last = null;
	while (temp != null) {
		// alert(temp.id);
		if (temp.className == target_div_name) {
			last = temp;
		}
		temp = temp.nextSibling;
	}
	// alert(last.id);
	return last;
}

/**
 * update img src for node and its childen
 * 
 * @param node
 * @param type:
 *            "add" or "remove" (add: joinbottom->join empty->line; remove:
 *            opposite)
 */
function updateNodeImg(node, type) {
	 //alert(node.id);
	// update lastChild: join->joinbottom
	var img_seq = node.getElementsByTagName('img').length - 1;	
	if (node.getElementsByTagName('img')[img_seq].src.indexOf("page.gif") > -1) {
		img_seq = img_seq - 1;
	}

	var img_to_update = node.getElementsByTagName('img')[img_seq];
	
	if (type == "add") {
		img_to_update.src = img_to_update.src.replace(/joinbottom.gif/,
				"join.gif");
	} else {// remove
		img_to_update.src = img_to_update.src.replace(/join.gif/,
				"joinbottom.gif");
	}

	// if previous sibling has childen, update all childen of previous sibling
	if (node.nextSibling != null && node.nextSibling.className == "clip") {
		var childen = node.nextSibling.getElementsByClassName(target_div_name);
		for (i = 0; i < childen.length; i++) {
			img_to_update = childen[i].getElementsByTagName('img')[img_seq];
			// alert(img_to_update.src);
			if (type == "add") {
				img_to_update.src = img_to_update.src.replace(/empty.gif/,
						"line.gif");
			} else {
				img_to_update.src = img_to_update.src.replace(/line.gif/,
						"empty.gif");
			}
		}
	}
}

/**
 * 
 * @param node:
 *            'dTreeNode'
 * @returns {Boolean}
 */
function removeNode(node) {
	// remove the node from tree
	var parent = node.parentNode;
	parent.removeChild(node);
	var lastChild = getLastChild(parent);
	if (lastChild == null) {
		parent.parentNode.removeChild(parent);
	} else {
		updateNodeImg(lastChild, "remove");
	}
	return false;
}

/**
 * re insert node in tagList when deleting nodes
 */
function revertNode(nodeID) {
	var hasMoreThanOne = false; // whether there are more than one same tag in
								// the tree
	var nodelist = document.getElementById('hierarchyTree')
			.getElementsByClassName('dTreeNode');
	for (i = 0; i < nodelist.length; i++) {
		if (nodelist[i].id == nodeID) {
			hasMoreThanOne = true;
		}
	}

	// locate the tag from taglist: compare one by one
	var tags = document.getElementById('availableTags').getElementsByClassName(
			"structure");
	var tag_tr, tag;
	for (i = 0; i < tags.length; i++) {
		if (tags[i].id == nodeID) {
			tag = tags[i];
			tag_tr = tag.parentNode.parentNode;
			break;
		}
	}
	if (tag_tr != null) {
		if (tag_tr.style.visibility != null
				&& tag_tr.style.visibility == 'hidden') {
			tag_tr.style.visibility = 'visible';
		}
		if (hasMoreThanOne) {
			// set color grey
			tag.getElementsByTagName("label")[0].style.color = "grey";
		} else {
			tag.getElementsByTagName("label")[0].style.color = "black";
		}
	}
}

/**
 * delete node when click the little cross after the node
 * 
 * @param node:
 *            thhe param is the label of 'x'
 */
function deleteNode(node) {
	var treeNode = node.parentNode.parentNode; // div
	var nodeID = treeNode.id;

	// if has children, delete all the children, which is div 'clip'
	var clip;
	if (treeNode.nextSibling != null
			&& treeNode.nextSibling.className == "clip") {
		var confirmed = confirm("This action will also remove the children nodes. Do you still want to remove the node? ");
		if (!confirmed) {
			return; // do nothing
		}
		clip = treeNode.nextSibling;
		clip.parentNode.removeChild(clip);
	}

	removeNode(treeNode);
	revertNode(nodeID);

	if (clip != null) {
		var children = clip.getElementsByClassName('dTreeNode');
		if (children.length > 0) {
			for ( var j = children.length - 1; j >= 0; j--) {
				// alert(j + ": " + children[j].id);
				revertNode(children[j].id);
			}
		}
	}
	clip = null;
}

/**
 * mouse move event handler update position of the dragged obj
 */
function mouse_move_handler(e) {
	var evn;
	evn = e || event;
	if (is_drag) {
		$("#" + maskName).show();
		drag_clone.style["visibility"] = "visible";
		var y = evn.clientY
				+ document.getElementsByTagName('html')[0].scrollTop;
		
		var browser = navigator.userAgent.toLowerCase();
		if (window.chrome != null || browser.indexOf('safari') > -1) {
			// if chrome or safari, add window's scrollTop()
			y = y + $(window).scrollTop();
		}

		drag_clone.style["top"] = (y - 5) + "px";
		drag_clone.style["left"] = (evn.clientX - 5) + "px";
	}
	return false;
}

/**
 * mouse up event handler insert a cell with the obj behind the pointed cell if
 * the destination is inside the table range
 */
function mouse_up_handler(e) {
	//console.log("mouse up");
	var evn;
	evn = e || event;

	if (is_drag) {
		getTargetNode(evn);
		if (target_node != null) {
			//console.log("target node: " + target_node.innerHTML);
			var inParent = false;// whether the term is already one of
									// ancestors of the target_node
			var alreadyChild = false;// whether the term is already one of
										// the children of the target_node
			// only check the first level children

			// get inParent
			var temp = target_node;
			while (temp.parentNode != null) {
				temp = temp.parentNode;
				if (temp.id == tag_chosen.id) {
					inParent = true;
					break;
				}
			}

			// get alreadyChild
			var nextSibling = target_node.nextSibling;
			var clip = null;
			if (nextSibling != null && nextSibling.className == 'clip') {
				clip = nextSibling;
				temp = clip.firstChild;
				while (temp != null) {
					if (temp.id == tag_chosen.id) {
						alreadyChild = true;
						break;
					}
					temp = temp.nextSibling;
				}
			}

			if (tag_chosen.id == target_node.id) {
				// node can not be itself's child
				//alert("A term cannot be a child of itself.");
				//console.log(tag_chosen.innerHTML);
				var termname = tag_chosen.getElementsByClassName("structure_label")[0].innerHTML;
				setTerm(termname);
			} else if (inParent) {
				// node cannot be its ancestors' child
				alert("A term cannot be a child of itself. The term is already an ancestor of the target node.");
			} else if (alreadyChild) {
				alert("The term is already an ancestor of the target node.");
			} else {
				// do the adding node
				if (clip != null) {
					// if has child, set the last one joinbottom = join
					var lastChild = getLastChild(clip);
					if (lastChild != null) {
						updateNodeImg(lastChild, "add");
					}
				} else {
					// the target_node is a leaf node: add clip to hold the new
					// added child node
					clip = document.createElement('div');
					clip.className = "clip";
					clip.id = target_node.id;
					target_node.parentNode.insertBefore(clip, nextSibling); // insertBefore(newchild,
																			// refchild)
				}

				/** ***generate newNode***** */
				var newNode = document.createElement('div');
				newNode.className = "dTreeNode";
				newNode.id = tag_chosen.id;
				var imgs = target_node.getElementsByTagName('img');
				for (i = 0; i < imgs.length; i++) {
					var src = imgs[i].src;
					if (src.indexOf("page.gif") > -1 || src.indexOf("globe.gif") > -1) {
						continue;
					}
					var img = document.createElement('img');
					img.src = imgs[i].src.replace(/join.gif/, "line.gif");// if
																			// join.gif->
																			// line.gif;
																			// joinbottom.gif
																			// ->
																			// empty.gif
					img.src = img.src.replace(/joinbottom.gif/, "empty.gif");
					newNode.appendChild(img);
				}// end of getting the images of parent

				var img_joinbottom = document.createElement('img');
				img_joinbottom.src = "images/tree/joinbottom.gif";
				newNode.appendChild(img_joinbottom);
				// alert("with joinbottom: " + newNode.innerHTML);

				// getting dragme image and tagName
				//var img_dragme = current_obj;
				//var text = current_obj.nextSibling.cloneNode(true);
				var text = current_obj.cloneNode(true);
				text.style.color = 'black';
				//newNode.appendChild(img_dragme.cloneNode(true));
				newNode.appendChild(text);
				var cross = document.createElement('a');
				cross.innerHTML = " <font class='cross' onclick='deleteNode(this)'>x</font>";
				newNode.appendChild(cross);
				newNode.onmousedown = mouse_down_handler;
				// alert(newNode.innerHTML);
				clip.appendChild(newNode);
				document.getElementById("hierarchyTree").scrollTop = newNode.offsetTop;

				/** *********update source********** */
				if (withKeyControl(e) || e.metaKey) {
					// mark the selected one
					if (drag_from == 'left') {
						// tag_chosen.style.color = 'grey';
						current_obj.style.color = 'grey';
						newNode.setAttribute("rel", "keep");
					} else {
						// if drag from right, copy the attribute rel

						newNode.setAttribute("rel", tag_chosen
								.getAttribute('rel'));
						// alert(newNode.getAttribute('rel'));
					}
				} else {
					if (drag_from == 'left') {
						var tagTR;
						tagTR = tag_chosen.parentNode.parentNode;
						 tagTR.style.visibility = 'hidden';
						// tagTR.parentNode.removeChild(tagTR);
						//update_term_color(tag_chosen);

					} else {
						removeNode(tag_chosen);
					}
				}
			}
		}
	}

	function update_term_color(object) {
		var nodeID = object.id;
		//console.log("nodeid: " + nodeID);

		var hasMoreThanOne = false; // whether there are more than one same tag
									// in the tree
		var nodelist = document.getElementById('hierarchyTree')
				.getElementsByClassName('dTreeNode');
		var count = 0;
		for (i = 0; i < nodelist.length; i++) {
			if (nodelist[i].id == nodeID) {
				count++;
				if (count > 1) {
					hasMoreThanOne = true;
					break;
				}
			}
		}

		// locate the tag from taglist: compare one by one
		var tags = document.getElementById('availableTags')
				.getElementsByClassName("structure");
		var tag_tr, tag;
		for (i = 0; i < tags.length; i++) {
			if (tags[i].id == nodeID) {
				tag = tags[i];
				tag_tr = tag.parentNode.parentNode;
				break;
			}
		}
		if (tag_tr != null) {
			if (tag_tr.style.visibility != null
					&& tag_tr.style.visibility == 'hidden') {
				tag_tr.style.visibility = 'visible';
			}
			if (hasMoreThanOne) {
				// set color grey
				tag.getElementsByTagName("label")[0].style.color = "grey";
			} else {
				tag.getElementsByTagName("label")[0].style.color = "black";
			}
		}
	}

	old_target_node = target_node;
//	delete_drag_clone();
//	if (drag_clone) {
//		drag_clone.style["visibility"] = "hidden";
//	}
//	if (tag_chosen) {
//		tag_chosen.style.cursor = 'auto';
//	}
//	is_drag = false;
//	target_node = null;
//	tag_chosen = null;

	clearDraggingTrack();
	return false;
}

function clearDraggingTrack() {
	delete_drag_clone();
	if (drag_clone) {
		drag_clone.style["visibility"] = "hidden";
	}
	if (tag_chosen) {
		tag_chosen.style.cursor = 'auto';
	}
	is_drag = false;
	target_node = null;
	tag_chosen = null;
}

function getClone() {
	delete_drag_clone();
	drag_clone = document.getElementById(maskName);
	if (drag_from == "left") {
		drag_clone_content = tag_chosen.cloneNode(true);
	} else {
		drag_clone_content = document.createElement('div');
		drag_clone_content.className = "structure";

		//var img_dragme = current_obj;
		var text = current_obj.nextSibling;

		//drag_clone_content.appendChild(img_dragme.cloneNode(true));
		drag_clone_content.appendChild(text.cloneNode(true));

		/*
		 * var img_dragme = document.createElement('img'); img_dragme.className =
		 * "dragme"; img_dragme.src = "images/drag.jpg";
		 * img_dragme.style["width"] = 12 + "px";
		 * drag_clone_content.appendChild(img_dragme);
		 * 
		 * var text = document.createElement("a"); text.innerHTML =
		 * tag_chosen.childNodes[1].nodeValue;
		 * drag_clone_content.appendChild(text);
		 */
		// drag_clone_content =
		// tag_chosen.getElementsByClassName('structure')[0].cloneNode(true);
	}

	drag_clone.appendChild(drag_clone_content);
}

function delete_drag_clone() {
	if (drag_clone) {
		drag_clone.innerHTML = "";
	}
}

function set_drag_from() {
	var temp = tag_chosen;
	while (temp.tagName != "HTML") {
		if (temp.className == "structure") {
			// alert("drag from right");
			drag_from = "left";
			break;
		} else if (temp.className == "dTreeNode") {
			// alert("drag from left");
			drag_from = "right";
			break;
		}
		temp = temp.parentNode;
	}
}

function getTargetNode(evn) {

	target_node = null;
	var i, top, bottom, left, right;
	var cursor_x = evn.clientX;
	var cursor_y = evn.clientY
			+ document.getElementById('hierarchyTree').scrollTop
			+ document.getElementsByTagName('html')[0].scrollTop;
	// alert("cursor_y: " + cursor_y);
	
	var browser = navigator.userAgent.toLowerCase();
	if (window.chrome != null || browser.indexOf('safari') > -1) {
		// if chrome or safari, add window's scrollTop()
		cursor_y = cursor_y + $(window).scrollTop();
	}

	var nodesList = document.getElementsByClassName('dTreeNode');

	if (nodesList != null) {
		for (i = 0; i < nodesList.length; i++) {
			var node = nodesList[i];

			top = 0;
			bottom = 0;
			left = 0;
			right = 0;
			var calc_cell = node;
			while (calc_cell.offsetParent) {
				top += calc_cell.offsetTop;
				left += calc_cell.offsetLeft;
				calc_cell = calc_cell.offsetParent;
			}

			bottom = top + node.offsetHeight;
			right = left + node.offsetWidth;
			// alert("top: " + top + "; bottom: " + bottom);

			if (cursor_y >= top && cursor_y <= bottom && cursor_x >= left
					&& cursor_x <= right) {
				target_node = nodesList[i];
				break;
			}
		}
	}
}

function withKeyControl(e) {
	var evn;
	evn = e || event;
	if (evn.ctrlKey) {
		//console.log("control key pressed");
		return true;
	} else {
		return false;
	}
	return false;
}

function showReport(tag) {
	window
			.open(
					'comment.do?tag=' + tag,
					'',
					'height=500,width=1000, directories=no, toolbar=no, location=no, menubar=no,resizable=yes,scrollbars=yes, statusbar=no, left=0,top=0');
}

function hierarchy_init() {
	var element_list = document.getElementsByClassName('tag');
	for (i = 0; i < element_list.length; i++) {
		element_list[i].onmousedown = mouse_down_handler;
	}
	document.onmousemove = mouse_move_handler;
	document.onmouseup = mouse_up_handler;
	
	$(".node").click(function () {
		setTerm($(this).html());
	});
	
	$(".node").dblclick(function() {
		showReport($(this).parent().attr("id") + ":" + $(this).html());
	});
	
	$(".node").css("cursor", "pointer");
}

function hasDataToSave() {
	var tag_list = document.getElementById('hierarchyTree')
			.getElementsByClassName('structure_label');
	if (tag_list.length > 0) {
		return true;
	}
	return false; // no new term to save
}

function save_tree(flag) {
	if (!hasDataToSave()) {
		// if no data to save, alert notification and return
		alert("No changes have been made. Click and drag structures onto the tree to make changes.");
		return;
	}

	var confirmed = confirm("NOTE: Decisions cannot be modified after saving the hierarchy tree. Are you sure you want to save?");
	if (!confirmed) {
		return;
	} else {
		window.onbeforeunload = null;
	}

	// the saving processing image visible
	document.getElementById("processingSaveImage").style.visibility = 'visible';

	var nodes, node, i;
	var request = '<?xml version="1.0" encoding="UTF-8"?>';
	request += "<nodes>";
	nodes = document.getElementById('hierarchyTree').getElementsByClassName(
			'structure_label');
	for (i = 0; i < nodes.length; i++) {
		node = nodes[i];
		request += "<node>";
		request += "<id>" + node.parentNode.id + "</id>";
		request += "<pid>" + node.parentNode.parentNode.id + "</pid>";
		request += "<name>" + node.firstChild.nodeValue + "</name>";
		// removed???
		if (node.parentNode.getAttribute("rel") == "keep") {
			request += "<keep>yes</keep>";
		} else {
			request += "<keep>no</keep>";
		}
		request += "</node>";
	}
	request += "</nodes>";
	// alert(request);

	/*
	 * var confirmed = confirm("NOTE: Decisions CANNOT be changed after saved.
	 * Are you sure to you want to save the hierarchy tree?"); if (!confirmed) {
	 * return; }
	 */

	document.getElementById('hiddenvalue').value = request;
	document.getElementById('submitForm').submit();

	document.getElementById("processingSaveImage").style.visibility = 'hidden';
}

/* if there are changes, alert changes before close page */
window.onbeforeunload = function() {
	if (hasDataToSave()) {
		return ("You have unsaved changes.");
	}
}
