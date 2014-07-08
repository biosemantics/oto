/**
 * This file contains all the dragging and saving functions for the categorizing
 * page. Author: Fengqiong
 */

var moveFlagToDetermineDragVsClick = 0; 
var mouseDownPositionX = -1;
var mouseDownPositionY = -1;
var distanceThresholdDragClick = 10;
var dragAfterSave = false;
var dragGroupTable = false;

var is_drag = false; // whether the obj is being dragged, be true when have
// term selected and current_obj is the dragme icon
var obj_x, obj_y; // position of group_chosen before dragged
var old_x, old_y; // old position of cursor // not in use now
var drag_obj; // obj being dragged //seems no use at all
var drag_clone; // obj clone to show dragging process //<div id="dragging_mask">
var drag_clone_content; // content to be added into drag_clone //should be <div
// class="dragGroupTable">
var target_category; // the category table being dragged into
var target_term;
var old_target_category; // the old expanded category table
var current_obj; // the object clicked on

var target_div_name = "categories_div"; // <div id="categories_div">
var target_table_name = "categories_table"; // <table id="categories_table">
var maskName = "dragging_mask"; // <div id="dragging_mask">
var group_table_name = "dragGroupTable"; // <div class="dragGroupTable">

var drag_from = ""; // 'left' means available terms, 'right' means categories
var group_chosen, old_group_chosen;// for control options, the chosen group
var term_selected = false, has_term_left = false;
var i = 0;
var targetbox;
var event;
var restTermsCount;
var categoryInRow = 6;

var is_makeSorE = false; // making synonym/exclusive
var is_changeDecision = false;
var term_Chosen; // choose a term to make synonym or exclusive

var category_from; // the old category of the term, derived from term_Chosen
var category_from_top; // the top of category_from, used to detect out of box
var category_from_bottom; // the bottom of category_from, used to detect out
// of box

var reviewedTerms = ""; // hold all the terms touched since last page loading

/**
 * delete user created category when there is no term in it
 * 
 * @param category
 * @param uploadID
 */
function deleteCategory(categoryName, uploadID) {
	var confirmed = confirm("Are you sure you want to delete category '"
			+ categoryName + "'?");
	if (confirmed) {
		XHR = createXHR();
		if (XHR) {
			XHR.onreadystatechange = function() {
				if (XHR.readyState == 4) {
					if (XHR.status == 200) {
						var tbs = document
								.getElementsByClassName("categoryTable");
						var i;
						for (i = 0; i < tbs.length; i++) {
							if (tbs[i].id == categoryName) {
								tbs[i].parentNode.removeChild(tbs[i]);
							}
						}
						alert("Category '" + categoryName
								+ "' was successfully deleted.");

					} else {
						alert("Failed to delete category '" + categoryName
								+ "'. Please try again later. ");
					}
				}
			};
			XHR.open("POST", 'deleteCategory.do', true);
			XHR.setRequestHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			var x = 'value=' + categoryName + "," + uploadID;
			XHR.send(x);
		}
	}
}

/**
 * collapse or expand the groups on he left side of the page
 * 
 * @param obj_img
 */
function expandTerms(obj_img) {
	var img_src = obj_img.src;
	var trs = obj_img.parentNode.parentNode.parentNode
			.getElementsByTagName("tr");
	var terms_tr = trs[1];
	if (terms_tr) {
		if (img_src.indexOf("expand") > 0) {
			// to expand
			terms_tr.style.display = "block";
			obj_img.src = "images/icon_collapse.gif";
		} else {
			// to collapse
			terms_tr.style.display = "none";
			obj_img.src = "images/icon_expand.gif";
		}
	}
}

/**
 * add touched term into reviewTerms
 * 
 * @param termName
 */
function markTermReviewed(termName) {
	if (reviewedTerms.indexOf(" " + termName + ";") == -1) {
		reviewedTerms += " " + termName + ";"; // add space before term to
		// prevent situation like
		// 'duplicate' and 'plicate'
		// alert(reviewedTerms);
	}
}

function checkAll(obj_checkbox) {
	var i;
	var val = obj_checkbox.checked;
	var boxes = obj_checkbox.parentNode.parentNode.parentNode.parentNode
			.getElementsByTagName("input");
	for (i = 0; i <= boxes.length; i++) {
		boxes[i].checked = val;
	}
}

/**
 * reset reviewedTerms must be done after each saving or page loading
 */
function clearReviewedTerms() {
	reviewedTerms = "";
}

function showSavedTerms(decision) {
	var left = event.clientX + 20;
	var top = event.clientY + 30;

	window
			.open(
					'jsp/savedTerms.jsp?decision=' + decision,
					"",
					'height=150,width=130,directories=no,toolbar=no,location=no,menubar=no,resizable=no,scrollbars=yes,status=no,left='
							+ left + ',top=' + top);
}

function expandRow() {
	var i;
	if (old_target_category != null) {
		var row = old_target_category.parentNode;
		if (row.tagName == "TR") {
			var target_terms = row.getElementsByClassName('categoryTerms');
			for (i = 0; i < target_terms.length; i++)
				if (target_terms[i] != null) {
					target_terms[i].style["height"] = 0 + "px";
				}
		}
	}
	if (target_category != null) {
		var row = target_category.parentNode;
		if (row.tagName == "TR") {
			var target_terms = row.getElementsByClassName('categoryTerms');
			for (i = 0; i < target_terms.length; i++)
				if (target_terms[i] != null) {
					target_terms[i].style["height"] = 150 + "px";
				}
		}

	}
}

function doclickRow() {
	var i;
	// if same row, if fold, expand, else fold
	if (old_target_category != null
			&& old_target_category.parentNode == target_category.parentNode) {
		var row = target_category.parentNode;
		if (row.tagName == "TR") {
			var target_terms = row.getElementsByClassName('categoryTerms');
			for (i = 0; i < target_terms.length; i++)
				if (target_terms[i] != null) {
					if (target_terms[i].style["height"] == "150px") {
						target_terms[i].style["height"] = 0 + "px";
					} else {
						target_terms[i].style["height"] = 150 + "px";
					}

				}
		}
	} else {
		// if not same row, fold the old one, expand the new one
		if (old_target_category != null) {
			var row = old_target_category.parentNode;
			if (row.tagName == "TR") {
				var target_terms = row.getElementsByClassName('categoryTerms');
				for (i = 0; i < target_terms.length; i++)
					if (target_terms[i] != null) {
						target_terms[i].style["height"] = 0 + "px";
					}
			}
		}
		if (target_category != null) {
			var row = target_category.parentNode;
			if (row.tagName == "TR") {
				var target_terms = row.getElementsByClassName('categoryTerms');
				for (i = 0; i < target_terms.length; i++)
					if (target_terms[i] != null) {
						target_terms[i].style["height"] = 150 + "px";
					}
			}
		}
	}
}

function mouse_click_handler(e) {
	var evn;
	evn = e || event;

	getTargetCategory(evn);
	// document.getElementById('serverMessage').innerHTML = '&nbsp;';
	doclickRow();
	old_target_category = target_category;
	target_category = null;
	return false;
}

/**
 * After append to changedUnit, changed all color to be red
 * 
 * @param changedUnit
 */
function setRed(changedUnit) {
	var j;
	var obj_as = changedUnit.getElementsByTagName("a");
	for (j = 0; j < obj_as.length; j++) {
		obj_as[j].style["color"] = "red";
	}
}

function setTermColor() {

	// clear old group color
	if (old_group_chosen && old_group_chosen != group_chosen) {
		var terms = old_group_chosen.getElementsByClassName('dragTerm');
		var i = 0;
		for (i = 0; i < terms.length; i++) {
			terms[i].style.color = "black";
		}
		current_obj.style.color = "red";
	} else {
		// set current_clicked term color
		if (current_obj.style.color == "red") {
			current_obj.style.color = "black";
		} else {
			current_obj.style.color = "red";
		}
	}
}

/**
 * loop the dragGroupTable, if has checked box, set term_selected = true if has
 * unchecked box, set has_term_left = true
 */
function updateSelectedFlags() {
	var i;
	term_selected = false;
	has_term_left = false;
	if (group_chosen) {
		var terms = group_chosen.getElementsByClassName('term');
		for (i = 0; i < terms.length; i++) {
			var checkbox = terms[i].getElementsByTagName('input');
			if (checkbox && checkbox[0].type == "checkbox") {
				if (term_selected == false) {
					if (checkbox[0].checked == true) {
						// has checked terms
						term_selected = true;
						// alert("find checked");
					}
				}
				if (has_term_left == false) {
					if (checkbox[0].checked == false) {
						has_term_left = true;
						// alert("find unchecked");
					}
				}
			}
		}
	}
}

/**
 * clone term when drag term for making synonym/exclusive
 */
function getTermClone() {

	delete_drag_clone();

	// clone the whole group, and then delete unselected ones
	drag_clone = document.getElementById(maskName);
	drag_clone_content = term_Chosen.cloneNode(true);
	drag_clone.appendChild(drag_clone_content);
}

/**
 * get drag_clone get drag_clone_content from group_chosen loop all rows
 * (class="term_row") if unchecked, remove from content append
 * drag_clone_content into drag_clone
 */
function getGroupClone() {
	var i;
	delete_drag_clone();

	// clone the whole group, and then delete unselected ones
	drag_clone = document.getElementById(maskName);
	drag_clone_content = group_chosen.cloneNode(true);

	// set text left align
	var th = drag_clone_content.getElementsByTagName("th");
	if (th[0]) {
		th[0].parentNode.removeChild(th[0]);
		// th[0].style.textAlign = "left";
	}

	if (term_selected) {
		// if selected some terms, remove unselected ones
		var term_rows = drag_clone_content.getElementsByClassName('term_row');
		var termsCount = term_rows.length;
		restTermsCount = term_rows.length;
		for (i = term_rows.length - 1; i >= 0; i--) {
			var checkbox = term_rows[i].getElementsByTagName('input');
			if (checkbox[0] && checkbox[0].type == "checkbox") {
				if (checkbox[0].checked == false) {
					term_rows[i].parentNode.removeChild(term_rows[i]);
					termsCount--;
				}
			}
		}
		// update the dragging img height
		var img_cp = drag_clone_content.getElementsByClassName("dragme")[0];
		img_cp.style["height"] = termsCount * 21 + "px";

		restTermsCount = restTermsCount - termsCount;
	} else {
		// if no one selected, consider everyone selected
	}
	drag_clone.appendChild(drag_clone_content);
}

/**
 * clear checkbox before drop into target table
 */
function clearCheckboxBeforeDrop() {
	var i;
	var term_rows = drag_clone_content.getElementsByClassName('term_row');
	for (i = 0; i < term_rows.length; i++) {
		// mark reviewed for dropping unsaved terms
		markTermReviewed(term_rows[i].id);
		var checkbox = term_rows[i].getElementsByTagName('input');
		if (checkbox[0] && checkbox[0].type == "checkbox") {
			checkbox[0].checked = false;
		}
	}
}

/**
 * update group_chosen after drop if has_term_left, remove those selected terms
 * else remove the whole group
 */
function update_group_chosen() {
	var i;
	if (has_term_left && term_selected) {
		// selected some and left some
		var term_rows = group_chosen.getElementsByClassName('term_row');

		for (i = term_rows.length - 1; i >= 0; i--) {
			var checkbox = term_rows[i].getElementsByTagName('input');
			if (checkbox && checkbox[0].type == "checkbox") {
				if (checkbox[0].checked == true) {
					term_rows[i].parentNode.removeChild(term_rows[i]);
				}
			}
		}
		group_chosen.style["visibility"] = "visible";
		current_obj.style["height"] = restTermsCount * 21 + "px";
	} else {
		// no one selected, or no one left
		group_chosen.parentNode.removeChild(group_chosen);
	}
	drag_clone.style["visibility"] = "hidden";
}

/**
 * get the drag_from
 */
function set_drag_from() {
	var temp = current_obj;
	// alert(temp.tagName);
	while (temp.tagName != "HTML") {
		temp = temp.parentNode;
		if (temp.id == "categories_table") {
			// alert("drag from right");
			drag_from = "right";
			break;
		} else if (temp.id == "availableTerms") {
			// alert("drag from left");
			drag_from = "left";
			break;
		}
	}
}

/*
 * remove from the synonym list when hit the little cross, and add it to the end
 * of the term list
 */
function removeTerm(obj_cross) {
	var j;
	var term_to_remove = obj_cross.parentNode; // tag = label
	var tr = document.createElement("tr");
	tr.className = "term_row_saved";
	tr.id = term_to_remove.id;
	tr.setAttribute("termName", term_to_remove.id);
	tr.setAttribute("valign", "middle");
	var td = document.createElement("td");
	td.setAttribute("valign", "middle");
	var div = document.createElement("div");
	div.setAttribute("class", "mainTerm");
	div.setAttribute("onmouseover", "displayFixTypoIcon(this)");
	div.setAttribute("onmouseout", "hideFixTypoIcon(this)");
	td.appendChild(div);

	//div.innerHTML = "<img class='dragAfterSave' src='images/drag.jpg' width='10px;'></img> ";
	
	// mark reviewed
	markTermReviewed(term_to_remove.id);

	// append term and view sign
	var objs_a = term_to_remove.getElementsByTagName("a");
	var term = objs_a[0].cloneNode(true);// term name
	term.style["color"] = "red";
	div.appendChild(term);
	div.innerHTML += "<img align='bottom' class='fixTypoIcon' "
			+ "src='images/edit.png' height='14px' title='Fix Typo' "
			+ "style='display: none;' onclick=\"fixTypo('" + term_to_remove.id
			+ "')\" /> ";
	tr.appendChild(td);
	var mainTerm = term_to_remove.parentNode.parentNode.parentNode;// tag = tr
	var changedDecision = mainTerm.parentNode.parentNode.parentNode
			.getElementsByClassName("changedDecision")[0];
	// remove term
	term_to_remove.parentNode.parentNode.removeChild(term_to_remove.parentNode); // remove
	// div
	var obj_as = mainTerm.getElementsByTagName("a");
	for (j = 0; j < obj_as.length; j++) {
		obj_as[j].style["color"] = "red";
	}
	// move both the main term and removed synonym to the changed decision part
	changedDecision.appendChild(mainTerm);
	changedDecision.appendChild(tr);
}

/**
 * remove related terms
 */
function removeTermWithName(term) {
	// find the term to remove by id and classname
	var termToRemove = null;
	var relationList = document.getElementById('categories_div')
			.getElementsByClassName('newRelation');
	// alert(relationList.length);
	var i;
	for (i = 0; i < relationList.length; i++) {
		if (relationList[i].id == term) {
			termToRemove = relationList[i];
			break;
		}
	}

	var tr = document.createElement("tr");
	tr.className = "term_row_saved";
	tr.id = term;
	var td = document.createElement("td");
	td.innerHTML = "<img class='dragAfterSave' src='images/drag.jpg' width='10px;'></img>"
			+ term;
	var sign = termToRemove.getElementsByTagName("a")[0].cloneNode(true);
	td.appendChild(sign);
	tr.appendChild(td);
	termToRemove.parentNode.parentNode.parentNode.parentNode.parentNode
			.appendChild(tr);
	
	// to do: update scrolltop

	// remove term
	termToRemove.parentNode.parentNode.removeChild(termToRemove.parentNode);

}

/**
 * mouse down event handler record the old position of the obj and cursor
 */
function mouse_down_handler(e) {
	//console.log("mouse down");
	moveFlagToDetermineDragVsClick = 0;
	
	//firefox uses layerX/Y instead
	mouseDownPositionX = e.offsetX==undefined?e.layerX:e.offsetX;
	mouseDownPositionY = e.offsetY==undefined?e.layerY:e.offsetY;
	
	// alert("mouse down");
	var evn;
	evn = e || event;
	event = evn;

	current_obj = evn.target || evn.srcElement;
	/*
	 * if (evn.target) { current_obj = evn.target; } else { current_obj =
	 * evn.srcElement; }
	 */
	// document.getElementById('serverMessage').innerHTML = '&nbsp;';
	set_drag_from();

	//alert(document.getElementById("UPLOADID")); //.innerHTML = current_obj.className;
	//document.getElementById("UPLOADID").setAttribute("test", current_obj.className);
	//document.getElementById("UPLOADID").setAttribute("test", current_obj.parentNode.className);
	dragGroupTable = current_obj.className != "checkbox" && 
		(current_obj.className == "termsTable dragme" || 
				current_obj.parentNode.className == "termsTable dragme" || 
				current_obj.parentNode.parentNode.className == "termsTable dragme" ||
				current_obj.parentNode.parentNode.parentNode.className == "termsTable dragme" ||
				current_obj.parentNode.parentNode.parentNode.parentNode.className == "termsTable dragme");
				
		/*		; //|| current_obj.className == "decisionRemoved termLabel dragme"; /* ||
		current_obj.parentNode.className == "termsTable dragme" ||
		current_obj.parentNode.parentNode.className == "termsTable dragme" ||
		current_obj.parentNode.parentNode.parentNode.className == "termsTable dragme" ||
		current_obj.parentNode.parentNode.parentNode.parentNode.className == "termsTable dragme" ||
		current_obj.parentNode.parentNode.parentNode.parentNode.parentNode.className == "termsTable dragme" ||
		current_obj.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.className == "termsTable dragme" ||
		current_obj.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.className == "termsTable dragme" ||
		current_obj.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.className == "termsTable dragme" ||
		current_obj.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.className == "termsTable dragme"; */
	//document.getElementById("UPLOADID").setAttribute("test", current_obj.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.className);
	//document.getElementById("UPLOADID").setAttribute("debug", dragGroupTable);
	
	/*console.log(current_obj.className);
	console.log(current_obj.parentNode.className);
	console.log(current_obj.parentNode.parentNode.className);
	console.log(current_obj.parentNode.parentNode.parentNode.className);
	console.log(current_obj.parentNode.parentNode.parentNode.parentNode.className);
	*/
	
	dragAfterSave = current_obj.className != "fixTypoIcon" && 
		(current_obj.className == "term_row_saved" ||
				current_obj.parentNode.className == "term_row_saved" ||
				current_obj.parentNode.parentNode.className == "term_row_saved" || 
				current_obj.parentNode.parentNode.parentNode.className == "term_row_saved");
	
	//console.log("dragAfterSave " + dragAfterSave);
	//console.log("dragGroupTable " + dragGroupTable);
	
	if (dragGroupTable) {
		
		//alert(current_obj.className);
		// get group_chosen: <div class="dragGroupTable">
		group_chosen = current_obj;
		while (group_chosen.tagName != "HTML"
				&& group_chosen.className != "dragGroupTable") {
			group_chosen = group_chosen.parentNode;
		}

		updateSelectedFlags();// see if uses the checkbox

		// is dragme and has term selected
		is_drag = true;

	} else if (dragAfterSave) {
		//console.log("drag after save");
		
		// alert(current_obj.className);
		is_makeSorE = false;// why exist?

		term_Chosen = current_obj;
		while (term_Chosen.tagName != "HTML" && term_Chosen.tagName != "TH"
				&& term_Chosen.className != "term_row_saved") {
			term_Chosen = term_Chosen.parentNode;
		}
		
		//console.log(term_Chosen);

		if (term_Chosen.className == "term_row_saved") {
			is_drag = true;
			//console.log("is_drag " + is_drag);

			/*
			 * var label = term_Chosen.getElementsByTagName('label'); if
			 * (label.length > 0) { return false; // if is the host of a
			 * existent relationship, // cannot be dragged }
			 * 
			 * var newrelation =
			 * term_Chosen.getElementsByClassName('newRelation'); if
			 * (newrelation.length > 0) { return false; // if is the host of a
			 * new relationship, cannot // be dragged }
			 */
		}
	} else {
		// alert("else");
	}

	//console.log("end mouse down " + is_drag);
	return false;
}

/**
 * mouse move event handler update position of the dragged obj
 */
function mouse_move_handler(e) {
	//firefox uses layerX/Y instead
	var posX = e.offsetX==undefined?e.layerX:e.offsetX;
	var posY = e.offsetY==undefined?e.layerY:e.offsetY;
		
	//console.log("mouse move "  + e);
	
	/*if(is_drag) {
		console.log(posX);
		console.log(posY);
		console.log(distanceThresholdDragClick);
		console.log(mouseDownPositionX);
		console.log(mouseDownPositionY);
	}*/
	
	if(mouseDownPositionX != -1 && mouseDownPositionY != -1 && (Math.abs(mouseDownPositionX - posX) + Math.abs(mouseDownPositionY - posY) > distanceThresholdDragClick)) {
		//alert("here");
		
		//console.log("moveFlag = 1");
		moveFlagToDetermineDragVsClick = 1;
		
		var evn;
		evn = e || event;

		if (is_drag) {
			
			if(dragGroupTable) {
				//console.log("dragGroupTable " + dragGroupTable);
				
				getGroupClone();// get dragging clone content

				// set drag_clone position and visibility
				obj_x = group_chosen.offsetLeft;
				obj_y = group_chosen.offsetTop;
				var temp = group_chosen;
				while (temp.offsetParent) {
					temp = temp.offsetParent;
					obj_x = obj_x + temp.offsetLeft;
					obj_y = obj_y + temp.offsetTop;
				}

				/*
				 * if (drag_from == "left") { obj_y = obj_y -
				 * group_chosen.parentNode.scrollTop; } else { obj_y = obj_y -
				 * group_chosen.parentNode.parentNode.scrollTop; }
				 */
				obj_y = obj_y - group_chosen.parentNode.scrollTop;
				drag_clone.style["top"] = obj_y + "px";// bug
				drag_clone.style["left"] = obj_x + "px";
				drag_clone.style["visibility"] = "visible";
			} else if(dragAfterSave) {
				
				//console.log("dragAfterSave " + dragAfterSave);
				
				// is_makeSorE = true;
				getTermClone();

				// get category_from and its top & bottom
				get_category_from(evn);

				// set drag_clone position and visibility
				obj_x = term_Chosen.offsetLeft;
				obj_y = term_Chosen.offsetTop;
				var temp = term_Chosen;
				while (temp.offsetParent) {
					temp = temp.offsetParent;
					obj_x = obj_x + temp.offsetLeft;
					obj_y = obj_y + temp.offsetTop;
				}

				// alert(term_Chosen.parentNode.parentNode.parentNode.scrollTop);
				obj_y = obj_y
						- term_Chosen.parentNode.parentNode.parentNode.scrollTop;

				drag_clone.style["top"] = obj_y + "px";// bug when scroll
				drag_clone.style["left"] = obj_x + "px";
				drag_clone.style["visibility"] = "visible";
			}

			drag_clone.style["visibility"] = "visible";
			var y = evn.clientY
					+ document.getElementsByTagName('html')[0].scrollTop;

			var browser = navigator.userAgent.toLowerCase();
			if (window.chrome != null || browser.indexOf('safari') > -1) {
				// if chrome or safari, add window's scrollTop()
				y = y + $(window).scrollTop();
			}

			// document.getElementById("dataset_info").innerHTML = "y:" + y
			// + " html:"
			// + document.getElementsByTagName('html')[0].scrollTop
			// + " body:"
			// + document.getElementsByTagName('body')[0].scrollTop
			// + "window: "
			// + $(window).scrollTop()
			// + "document: "
			// + $(document).scrollTop();

			// group_chosen.style["visibility"] = "hidden";
			drag_clone.style["top"] = (y - 2) + "px";
			drag_clone.style["left"] = (evn.clientX - 2) + "px";

			// if cursor position is out of category box, automatically scroll
			// the category box
			// old_target_category
			// the position of old_target_category
			
			dragAfterSave = current_obj.className != "fixTypoIcon" && 
			(current_obj.className == "term_row_saved" ||
					current_obj.parentNode.className == "term_row_saved" ||
					current_obj.parentNode.parentNode.className == "term_row_saved" || 
					current_obj.parentNode.parentNode.parentNode.className == "term_row_saved");
			
			if (dragAfterSave) {
				var cursor_y = evn.clientY
						+ document.getElementById('categories_div').scrollTop
						+ document.getElementsByTagName('html')[0].scrollTop
						+ document.getElementById("dragging_part").scrollTop;

				// if cursor is beyond box, decrease scrollTop to 0
				if (cursor_y < category_from_top && category_from.scrollTop > 0) {
					category_from.scrollTop = category_from.scrollTop - 5;
				} else if (cursor_y > category_from_bottom) {
					category_from.scrollTop = category_from.scrollTop + 5;
				}

				// document.getElementById("dataset_info").innerHTML += "
				// scrollTop:" + category_from.scrollTop;
			}
		}

		return false;
	}	
}

/**
 * mouse up event handler insert a cell with the obj behind the pointed cell if
 * the destination is inside the table range
 */
function mouse_up_handler(e) {
	//console.log("mouse up");
	
	mouseDownPositionX = -1;
	mouseDownPositionX = -1;
	
	if(moveFlagToDetermineDragVsClick == 0) {
		//click only
		return false;
	} else if (moveFlagToDetermineDragVsClick == 1) {
		//drag
		var evn;
		evn = e || event;

		if (is_drag) {
			getTargetCategory(evn);
			
			dragGroupTable = current_obj.className != "checkbox" && 
			(current_obj.className == "termsTable dragme" || 
					current_obj.parentNode.className == "termsTable dragme" || 
					current_obj.parentNode.parentNode.className == "termsTable dragme" ||
					current_obj.parentNode.parentNode.parentNode.className == "termsTable dragme" ||
					current_obj.parentNode.parentNode.parentNode.parentNode.className == "termsTable dragme");
			
			dragAfterSave = current_obj.className != "fixTypoIcon" && 
			(current_obj.className == "term_row_saved" ||
					current_obj.parentNode.className == "term_row_saved" ||
					current_obj.parentNode.parentNode.className == "term_row_saved" || 
					current_obj.parentNode.parentNode.parentNode.className == "term_row_saved");
			
			if (dragGroupTable) { //current_obj.className == "termLabel dragme" || current_obj.className == "decisionRemoved termLabel dragme") {// drag before saving
				if (target_category != null) {// (left -> target category) or
					// (right -> different category)
					if (!(drag_from == "right" && target_category == old_target_category)) {
						var i;
						// find div newDecisions for category
						targetbox = target_category
								.getElementsByClassName("newDecisions")[0];
						// drop the terms_chosen
						if (targetbox != null) {
							// append clone content
							clearCheckboxBeforeDrop();
							drag_clone_content.parentNode
									.removeChild(drag_clone_content);
							append_selected_terms(targetbox);
							setDragMeSign("drag-back");
							expandRow();
							old_target_category = target_category;
							// update group_chosen
							update_group_chosen();
						}
					}
				} else if (drag_from == "right") {// drag from right back to left
					clearCheckboxBeforeDrop();
					drag_clone_content.parentNode.removeChild(drag_clone_content);

					// append selected terms
					append_selected_terms(document.getElementById("availableTerms"));
					setDragMeSign("drag");

					// redo the mouse down event handler
					var element_list = drag_clone_content.childNodes;
					for (i = 0; i < element_list.length; i++) {
						element_list[i].onmousedown = mouse_down_handler;
					}
					update_group_chosen();
				} else {
					// drag from left but no target category
					alert("To categorize terms, drag terms into the categories box.");
				}
				
				
			} else if (dragAfterSave) {
				// drag after decision saved: change decision or make synonym
				if (target_category != null) {
					targetbox = target_category
							.getElementsByClassName("changedDecision")[0];
					if (is_changeDecision) {
						if (targetbox != null) {
							if (!isSingleTerm(term_Chosen) && ctrlKeyPressed(e)) {
								alert("Only single terms can be copied. Click the 'x' to break synonyms first. ");
							} else {
								drag_clone_content.parentNode
										.removeChild(drag_clone_content);
								// can copy here
								var obj_to_append = term_Chosen;
								if (ctrlKeyPressed(e)) {
									// get a new name
									var newName = getNewName(term_Chosen.id);
									if (newName == "null") {
										alert("There are too many copies of term '"
												+ term_Chosen.id
												+ "'. Please work on existing copies.");
									} else {
										obj_to_append = copiedTerm(term_Chosen,
												newName);
										markTermReviewed(newName);
									}
								}
								markTermReviewed(term_Chosen.id);

								obj_to_append.getElementsByTagName("a")[0].style["color"] = "red";
								setRed(obj_to_append);
								targetbox.appendChild(obj_to_append);
								updateLocations(term_Chosen.id);
								expandRow();
								old_target_category = target_category;
								// set scrolltop
								targetbox.parentNode.scrollTop = targetbox.offsetTop
										+ term_Chosen.offsetTop;
							}
						}
					} else if (is_makeSorE) {
						var term_chosen_name = term_Chosen.id;
						if (target_term != null && target_term != term_Chosen) {
							if (!isSingleTerm(term_Chosen)) {
								alert("Only single terms can be dragged to make synonyms. Click the 'x' to break synonyms first. ");
							} else {
								var confirmed = confirm("Are you sure you want to make the two terms synonyms? ");
								if (confirmed) {
									var isToMerge = hasDuplicate(target_term,
											term_chosen_name);
									if (isToMerge) {
										alert("Duplicated terms have been merged.");
									} else {
										var term_td = target_term
												.getElementsByTagName("td")[0];
										// check has duplicate or not
										target_term.getElementsByTagName("a")[0].style["color"] = "red";
										// generate syn
										var new_syn_div = document
												.createElement("div");
										new_syn_div.setAttribute("onmouseover",
												"displayFixTypoIcon(this)");
										new_syn_div.setAttribute("onmouseout",
												"hideFixTypoIcon(this)");

										var new_syn = document
												.createElement("label");
										new_syn_div.appendChild(new_syn);
										new_syn.className = "syn";
										new_syn.id = term_chosen_name;
										new_syn.setAttribute("termName",
												term_chosen_name);
										new_syn.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
										// append term and view sign
										var objs_a = term_Chosen
												.getElementsByTagName("a");
										var term = objs_a[0].cloneNode(true);
										term.style["color"] = "red";
										new_syn.appendChild(term);
										new_syn.innerHTML += " ";

										// append fix typo img
										var objs_img = term_Chosen
												.getElementsByClassName("fixTypoIcon");
										if (objs_img.length > 0) {
											var img = objs_img[0].cloneNode(true);
											new_syn.appendChild(img);
										}

										// add a cross
										new_syn.innerHTML += " <label onclick='removeTerm(this)' class='delete_cross'><font color='blue'>x</font></label>";
										term_td.appendChild(new_syn_div);
										markTermReviewed(term_chosen_name);
										markTermReviewed(target_term.id);
									}
									// any changed decision goes into the
									// changed_decisions list
									targetbox.appendChild(target_term);
									setRed(target_term);
									targetbox.parentNode.scrollTop = targetbox.offsetTop
											+ target_term.offsetTop;
									term_Chosen.parentNode.removeChild(term_Chosen);
								}

								is_makeSorE = false;
							}
						}
						drag_clone_content.parentNode
								.removeChild(drag_clone_content);
						delete_drag_clone();
						drag_clone.style["visibility"] = "hidden";
					}
				} else {
					if (!isSingleTerm(term_Chosen)) {
						alert("Only single terms can be removed from category box. Please remove synonyms first.");
					} else {
						// delete saved decision: will return to left side
						// var confirmed = confirm("Are you sure you want to remove
						// this term from current category?");

						// todo: force comments
						// var comment = prompt("Please explain why you need to
						// uncategorize this term (CANNOT be empty): ", "");
						if (true) {
							var comment = "";
							var deleted_term = document.createElement("div");
							deleted_term.className = "dragGroupTable";
							deleted_term.id = "0";
							markTermReviewed(term_Chosen.id);

							// set comment with the term
							deleted_term.innerHTML = generateDeletedTerm(
									term_Chosen.id, comment);
							var left_column = document
									.getElementById('availableTerms');
							left_column.appendChild(deleted_term);
							left_column.scrollTop = deleted_term.offsetTop;
							term_Chosen.parentNode.removeChild(term_Chosen);
						}
					}
				}

			}
		} else {
			// not dragging draggable items, do nothing
		}

		// no matter what, clean everything in memory
		if (group_chosen != null) {
			group_chosen.style.cursor = 'auto';
		}
		if (term_Chosen != null) {
			term_Chosen.style.cursor = 'auto';
		}
		is_drag = false;
		is_makeSorE = false;
		is_changeDecision = false;
		target_category = null;
		target_term = null;
		term_Chosen = null;
		delete_drag_clone();
		group_chosen = null;

		return false;
	}	
}

function setDragMeSign(picName) {
	var dragmelist = drag_clone_content.getElementsByClassName('dragme');
	if (dragmelist[0]) {
		dragmelist[0].src = "images/" + picName + ".jpg";
	}
}

function isSingleTerm(obj_term) {
	var syns = obj_term.getElementsByClassName("syn");
	// var new_syns = obj_term.getElementsByClassName("syn");
	if (syns.length > 0) {
		return false;
	} else {
		return true;
	}
}

function append_selected_terms(target) {
	var i;
	// alert("append_selected_terms");
	var current_group_name = drag_clone_content.id;

	var group_existed = false;
	var grouplist = target.getElementsByClassName('dragGroupTable');

	for (i = 0; i < grouplist.length; i++) {
		if (grouplist[i].id == current_group_name) {
			group_existed = true;
			break;
		}
	}

	if (group_existed) {

		// insert new terms one by one
		var termlist = drag_clone_content.getElementsByClassName('term_row');
		// loop, insert term one by one
		var j = 0;
		var termsCount = termlist.length;

		var targetGroup = grouplist[i].getElementsByClassName('termsTable');
		for (j = termlist.length - 1; j >= 0; j--) {
			targetGroup[0].appendChild(termlist[j]);
		}
		// set scrolltop
		if (target_category != null) {
			targetbox.parentNode.scrollTop = grouplist[i].offsetTop;
		} else {
			target.scrollTop = grouplist[i].offsetTop;
		}

		// set drag img height
		var dragImg = grouplist[i].getElementsByClassName("dragme")[0];
		var newHeight = termsCount * 21 + dragImg.height;
		dragImg.style["height"] = newHeight + "px";
	} else {

		target.appendChild(drag_clone_content);
		// set crolltop
		if (target_category != null) {
			// if drag into category...
			targetbox.parentNode.scrollTop = drag_clone_content.offsetTop;
		} else {
			target.scrollTop = drag_clone_content.offsetTop;
		}
	}
}

/**
 * delete the content of drag_clone
 */
function delete_drag_clone() {
	if (drag_clone) {
		drag_clone.innerHTML = "";
	}
}

function generateDeletedTerm(termName, comment) {
	var table_html = "<table><tr><td><table class='termsTable dragme'>";
	table_html += "<tr class='term_row' id='" + termName + "' " + " termName='"
			+ termName + "' onmouseover='displayFixTypoIcon(this)' "
			+ "onmouseout='hideFixTypoIcon(this)'>";
	table_html += "<td class='term'><input class='checkbox' type='checkbox'/>";
	table_html += "<label class='decisionRemoved termLabel' id='"
			+ termName
			+ "' comment='"
			+ comment
			+ "' style='cursor: pointer; color: red;' onclick=setTerm_categorizing('"
			+ termName + "'" + ")>" + termName + "</label><img align='bottom' "
			+ "class='fixTypoIcon' src='images/edit.png' "
			+ "height='14px' title='Fix Typo' style='display: none;' "
			+ "onclick=\"fixTypo('" + termName + "')\" />";
	table_html += "</td></tr></table></td>";

	//table_html += "<td><img class='dragme' src='images/drag.jpg' width='20px' height='20px'></img></td>";
	table_html += "</tr></table>";
	return table_html;
}

/**
 * function: to find the target_category called by mouse_up_handler the big
 * parent: id = target_table_name = "categories_table", get its top and left
 * 
 * @param: evn: to get the cursor position
 */
function getTargetCategory(evn) {
	var i, top = 0, bottom = 0, left = 0, right = 0;
	var cursor_x = evn.clientX;
	var cursor_y = evn.clientY
			+ document.getElementById('categories_div').scrollTop
			+ document.getElementsByTagName('html')[0].scrollTop
			+ document.getElementById("dragging_part").scrollTop;

	var browser = navigator.userAgent.toLowerCase();
	if (window.chrome != null || browser.indexOf('safari') > -1) {
		// if chrome or safari, add window's scrollTop()
		cursor_y = cursor_y + $(window).scrollTop();
	}

	var categories = document.getElementById('categories_table');
	var left_table_categories = 0;
	var top_table_categories = 0;
	var calc_cell = categories;

	while (calc_cell.offsetParent) {
		left_table_categories = calc_cell.offsetLeft + left_table_categories;
		top_table_categories = calc_cell.offsetTop + top_table_categories;
		calc_cell = calc_cell.offsetParent;
	}
	calc_cell = null;

	var category_table_list = categories
			.getElementsByClassName('categoryTable');

	if (category_table_list != null) {
		// loop the term_row, find the one pointed
		for (i = 0; i < category_table_list.length; i++) {
			var cell = category_table_list[i];
			left = cell.offsetLeft + cell.offsetParent.offsetLeft
					+ left_table_categories;
			top = cell.offsetTop + cell.offsetParent.offsetTop
					+ top_table_categories;
			right = left + cell.offsetWidth;
			bottom = top + cell.offsetHeight;
			if (cursor_x > left && cursor_x < right && cursor_y > top
					&& cursor_y < bottom) {
				target_category = category_table_list[i];
				break;
			}
		}
	}

	dragAfterSave = current_obj.className != "fixTypoIcon" && 
	(current_obj.className == "term_row_saved" ||
			current_obj.parentNode.className == "term_row_saved" ||
			current_obj.parentNode.parentNode.className == "term_row_saved" || 
			current_obj.parentNode.parentNode.parentNode.className == "term_row_saved");
	
	if (dragAfterSave) {
		if (target_category != null) {
			// target_category should be the same with current_category
			var currentCategory = term_Chosen;
			while (currentCategory.className != "categoryTable") {
				currentCategory = currentCategory.parentNode;
			}
			if (currentCategory != target_category) {
				is_changeDecision = true;
			} else {
				is_makeSorE = true;
				var c_left = left;
				var c_top = top;
				cursor_y += target_category
						.getElementsByClassName("categoryTerms")[0].scrollTop;
				var terms_list = target_category
						.getElementsByClassName('term_row_saved');
				if (terms_list.length > 0) {
					// loop the term_row, find the one pointed
					for (i = 0; i < terms_list.length; i++) {
						var term_row = terms_list[i];
						left = term_row.offsetLeft;
						top = term_row.offsetTop;

						calc_cell = term_row;
						while (calc_cell.offsetParent
								&& calc_cell.offsetParent.className != 'categoryTable') {
							calc_cell = calc_cell.offsetParent;
							left = left + calc_cell.offsetLeft;
							top = top + calc_cell.offsetTop;
						}
						left += c_left;
						top += c_top;

						right = left + term_row.offsetWidth;
						bottom = top + term_row.offsetHeight;
						// alert(left + "; " + top + ";; " + right + "; " +
						// bottom);
						if (cursor_x > left && cursor_x < right
								&& cursor_y > top && cursor_y < bottom) {
							target_term = term_row;
							// alert(target_term.innerHTML);
							break;
						}
					}
				} else {
					alert("Drag a source term onto a saved term.");
				}
			}
		}
	}
}

function drag_init() {
	var i;
	var element_list = document.getElementById('availableTerms').childNodes;
	for (i = 0; i < element_list.length; i++) {
		element_list[i].onmousedown = mouse_down_handler;
	}
	element_list = document.getElementById('categories_div').childNodes;
	for (i = 0; i < element_list.length; i++) {
		element_list[i].onmousedown = mouse_down_handler;
	}
	element_list = document.getElementById('categories_div')
			.getElementsByTagName('TH');
	for (i = 0; i < element_list.length; i++) {
		element_list[i].onclick = mouse_click_handler;
	}

	// session timeout reminder: promt one at 50 minutes
	setInterval(
			function() {
				alert("Your session will timeout in 10 minutes. Please save any changes you have made. (Otherwise your changes may be lost!)");
			}, 3000000);
}

/**
 * check if there are terms to save if newDecisions has children
 */
function hasTermsToSave() {
	var terms_list = document.getElementsByClassName('newDecisions');
	var i = 0;
	var relation_list = document.getElementsByClassName('newRelation');
	if (relation_list.length > 0) {
		return true;
	}

	for (i = 0; i < terms_list.length; i++) {
		var terms = terms_list[i].getElementsByClassName('term_row');
		if (terms.length > 0) {
			// find at least one term to save
			return true;
			break;
		}
	}
	return false; // no new term to save
}

/* check if there are unsaved data in this page */
function dataChanged() {
	var i;

	// new category
	var new_cats = document.getElementsByClassName('new_added_category');
	if (new_cats.length > 0) {
		return true;
	}
	// removing saved decision part
	var removed_terms = document.getElementById('availableTerms')
			.getElementsByClassName('decisionRemoved');
	if (removed_terms.length > 0) {
		return true;
	}

	// categorizing part
	var categories = document.getElementsByClassName('categoryTable');
	for (i = 0; i < categories.length; i++) {
		var new_terms = categories[i].getElementsByClassName('term_row');
		var changed_terms = categories[i]
				.getElementsByClassName('changedDecision')[0]
				.getElementsByClassName('term_row_saved');
		if ((new_terms.length > 0) || (changed_terms.length > 0)) {
			return true;
		}
	}
}

function replaceSpecialChar(text) {
	var temp = text.trim().replace(/&/g, "&amp;").replace(/ /g, "&nbsp;")
			.replace(/"/g, "&quot;").replace(/'/g, "&quot;").replace(/</g,
					"&lt;").replace(/>/g, "&gt;");
	return temp;
}

/**
 * Save Terms and categories function by f.huang
 * 
 * @param flag:
 *            session: save to session data; submit: save to database called
 *            when click save button
 */
function save_categories(uploadID) {
	var dataChanged = false;
	var categories;
	var i, j, k;
	var request = '<?xml version="1.0" encoding="UTF-8"?>';
	request += "<decisions>";
	request += "<uploadID>" + uploadID + "</uploadID>";

	// new category
	var new_cats = document.getElementsByClassName('new_added_category');
	// alert(new_cats.length);

	for (i = 0; i < new_cats.length; i++) {
		dataChanged = true;
		request += "<new_category><name>" + new_cats[i].id + "</name><def>"
				+ new_cats[i].getElementsByClassName("new_cat_th")[0].title
				+ "</def></new_category>";
	}
	// categorizing part
	categories = document.getElementsByClassName('categoryTable');
	for (i = 0; i < categories.length; i++) {
		var new_terms = categories[i].getElementsByClassName('term_row');
		var changed_terms = categories[i]
				.getElementsByClassName('changedDecision')[0]
				.getElementsByClassName('term_row_saved');
		var category_name = categories[i].id;
		if ((new_terms.length > 0) || (changed_terms.length > 0)) {
			dataChanged = true;
			request += "<category>";
			request += "<category_name>" + category_name + "</category_name>";
			if (new_terms.length > 0) {// new_terms_part
				request += "<new_terms>";
				for (j = 0; j < new_terms.length; j++) {
					request += "<term>" + new_terms[j].id + "</term>";
				}
				request += "</new_terms>";
			}

			// changed terms part
			if (changed_terms.length > 0) {
				request += "<changed_terms>";
				var hasSyn = "0";
				var synList = "";
				for (j = 0; j < changed_terms.length; j++) {
					hasSyn = "0";
					synList = "";
					var syns = changed_terms[j].getElementsByClassName("syn");
					if (syns.length > 0) {
						hasSyn = "1";
						for (k = 0; k < syns.length; k++) {
							request += "<term><name>"
									+ syns[k].id
									+ "</name><hasSyn>0</hasSyn>"
									+ "<isAdditional>1</isAdditional><synList>synonym of '"
									+ changed_terms[j].id
									+ "'</synList></term>";
							synList += "<syn>" + syns[k].id + "</syn>";
						}
					}
					request += "<term><name>"
							+ changed_terms[j].id
							+ "</name>"
							+ "<hasSyn>"
							+ hasSyn
							+ "</hasSyn><isAdditional>0</isAdditional><synList>"
							+ synList + "</synList></term>";
				}
				request += "</changed_terms>";
			}
			request += "</category>";
		}
	}

	// removing saved decision part
	var removed_terms = document.getElementById('availableTerms')
			.getElementsByClassName('decisionRemoved');
	if (removed_terms.length > 0) {
		dataChanged = true;
		request += "<removed_decisions>";
		for (j = 0; j < removed_terms.length; j++) {
			request += "<term><termName>" + removed_terms[j].id + "</termName>";
			request += "<comment>uncategorize: "
					+ replaceSpecialChar(removed_terms[j]
							.getAttribute("comment")) + "</comment>"
					+ "</term>";
		}
		request += "</removed_decisions>";
	}

	request += "</decisions>";
	// alert(request);
	if (!dataChanged) {
		alert("You have not made any changes. To categorize listed terms, select terms and drag them into the category boxes.");
		return;
	}
	confirmed = confirm("Are you sure you want to save your decisions?");
	if (confirmed) {
		// the saving processing image visible
		document.getElementById("processingSaveImage").style.visibility = 'visible';
		document.getElementById('hiddenvalue').value = request;
		// document.getElementById('submitForm').submit();
		sendSaveRequest(request);
	}
	document.getElementById("processingSaveImage").style.visibility = 'hidden';
}

function sendSaveRequest(requestXML) {
	var XHR = createXHR();
	if (XHR) {
		XHR.onreadystatechange = function() {
			if (XHR.readyState == 4) {
				if (XHR.status == 200) {
					if (XHR.responseText == "success") {
						window.onbeforeunload = null;
						location.reload();
					} else {
						alert("Failed to save changes. Please try again later. ");
					}
				} else {
					alert("Failed to save changes. Please try again later. ");
				}
			}
		};
		XHR.open("POST", 'saveGroup.do', true);
		XHR.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		XHR.send('value=' + requestXML);
	}
}

function submitGroupForm() {
	var formName = document.getElementById('generalForm');
	formName.submit();
}

function showTermsReport(term) {
	markTermReviewed(term);
	window
			.open(
					'comment.do?term=' + term,
					fileToShow,
					'height=500,width=1000, directories=no, toolbar=no, location=no, menubar=no,resizable=yes,scrollbars=yes, statusbar=no, left=0,top=0');
}
/*
 * calc how many categoryTable, to decide append tr or td
 */
function toStartNewRow() {
	var n = document.getElementsByClassName("categoryTable").length;
	if (n % categoryInRow == 0) {
		return true;
	} else {
		return false;
	}
}

function hasDuplicate(term, termName) {
	var j;
	if (term.id == termName) {
		return true;
	}
	var existedTerms = term.getElementsByClassName("syn");
	for (j = 0; j < existedTerms.length; j++) {
		if (existedTerms[j].id == termName) {
			return true;
			break;
		}
	}
}

/**
 * After drop term, update termLocation at page bottom if current tab is
 * locations only for saved terms since there could be more than one terms when
 * dragging uncategorized terms only for changing category since making synonym
 * will not change the locations
 * 
 * @param termName
 */
function updateLocations(termName) {
	document.getElementById("glossaryTerm").innerHTML = termName;
	var current = document.getElementsByClassName('currentContext')[0];
	if (current.id == "termLocations") {
		showTermLocations();
	}
}

/**
 * add a new category
 */
function newCategory() {
	var i;
	var name = prompt("New category name: ", "");
	if (name) {
		var catExisted = document.getElementsByClassName('categoryTable');
		for (i = 0; i < catExisted.length; i++) {
			if (catExisted[i].id.toLowerCase() == name.toLowerCase()) {
				alert("Category '" + name + "' has already been used. ");
				return;
			}
		}

		var def = prompt("Please provide a definition for category '" + name + "': ", "");
		if (def) {
			var startNewRow = toStartNewRow();

			// append td: last category_row
			// append tr: categories_table
			var td = document.createElement("td");
			td.className = "categoryTable";
			td.id = name;
			td.style["width"] = "16.66%";
			td.style.verticalAlign = "top";

			var div_newCat = document.getElementById("new_category_hidden")
					.cloneNode(true);
			div_newCat.id = name;
			div_newCat.className = "new_added_category";
			div_newCat.style["visibility"] = "visible";

			var th = div_newCat.getElementsByClassName("new_cat_th")[0];
			th.innerHTML = name;
			th.title = def;
			th.onclick = mouse_click_handler;

			td.appendChild(div_newCat);
			if (startNewRow) {
				var tr = document.createElement("tr");
				tr.className = "category_row";
				tr.appendChild(td);
				document.getElementById("categories_table").appendChild(tr);
			} else {
				var rows = document.getElementsByClassName("category_row");
				if (rows.length > 0) {
					rows[rows.length - 1].appendChild(td);
				}
			}
		}

	}
}

/**
 * Check if control key or command key is pressed
 * 
 * @param e
 * @returns {Boolean}
 */
function ctrlKeyPressed(e) {
	var evn;
	evn = e || event;
	if (evn.ctrlKey) {
		return true;
	} else {
		return (false || e.metaKey);
	}
	return (false || e.metaKey);
}

/**
 * generate a copied term based on oldname and newname
 * 
 * @param term
 * @param newName
 * @returns {___copyT14}
 */
function copiedTerm(obj, newName) {
	var objCopy = obj.cloneNode(true);
	changeTermName(objCopy, newName);
	return objCopy;
}

/**
 * get category_from and category_from_top and category_from_bottom
 * 
 * @param term_Chosen
 */
function get_category_from(evn) {
	if (term_Chosen != null) {
		category_from = term_Chosen;
		while (category_from.className != "categoryTerms") {
			category_from = category_from.parentNode;
		}

		// category_from = term_Chosen.parentNode.parentNode.parentNode;
		var calc_cell = category_from;
		category_from_top = 0;
		while (calc_cell.offsetParent) {
			category_from_top = calc_cell.offsetTop + category_from_top;
			calc_cell = calc_cell.offsetParent;
		}
		category_from_bottom = category_from.parentNode.offsetHeight
				+ category_from_top;

	}
}

/**
 * generate a new term name when copy term to another category box
 */
function getNewName(name) {
	var termName = name.replace(/(_\d\d?$)/g, "");
	var j = 1;
	// alert(termName);
	for (j = 1; j < 100; j++) {
		var ex = document.getElementById(termName + "_" + j);
		if (ex == null) {
			return termName + "_" + j;
		}
	}
	return "null";
}

/**
 * display the fix typo icon
 * 
 * @param termLabel
 */
function displayFixTypoIcon(termRow) {
	var imgs = termRow.getElementsByClassName("fixTypoIcon");
	if (imgs.length > 0) {
		var img = imgs[0];
		img.style.display = "inline";
	}
}

/**
 * hide the fix typo icon
 * 
 * @param termLabel
 */
function hideFixTypoIcon(termRow) {
	var imgs = termRow.getElementsByClassName("fixTypoIcon");
	if (imgs.length > 0) {
		var img = imgs[0];
		img.style.display = "none";
	}
}

/**
 * fix typo
 * 
 * @param term:
 *            current term name
 */
function fixTypo(term) {
	// check the original name of the term if this term is a copy
	var patt = /^.+_(\d)+$/;
	if (patt.test(term)) {
		term = term.substring(0, term.lastIndexOf("_"));
	}

	// user input: replacement of the original term
	var replacement = prompt("Modify term '" + term
			+ "':", "");
	if (!replacement) {
		return;
	}

	if (replacement == term) {
		//alert("The correct spell you typed is the same with '" + term + "'!");
		return;
	}

	// if term already exists, do not modify
	if ($("[termName='" + replacement + "']").length > 0) {
		alert("Term '" + replacement
				+ "' already exists. You can ignore the mistyped term '"
				+ term + "' and leave it in the left side. ");
		return;
	}

	var patt2 = /^[a-zA-Z\s_]+$/;
	if (!patt2.test(replacement)) {
		alert("Term names may contain letters, spaces and underscores. Please input a valid term name.");
		return;
	}

	var confirmed = confirm("You are about to correct term '" + term + "' to '"
			+ replacement + "'. "
			+ "This will affect all copies of the term. Are you sure you want to make the change?");

	if (!confirmed) {
		return;
	}

	// sender to server to change: server needs to change both original term and
	// copies
	// if term already exist, delete the term
	var XHR = createXHR();
	if (XHR) {
		XHR.onreadystatechange = function() {
			if (XHR.readyState == 4) {
				if (XHR.status == 200) {
					if (XHR.responseText == "success") {
						updateTermTypoInWebPage(term, replacement);
					} else {
						alert("Failed to update term '" + term + "' to '" + replacement
								+ "'. Please try again later. ");
					}
				} else {
					alert("Failed to update term '" + term + "' to '" + replacement
							+ "'. Please try again later. ");
				}
			}
		};
		XHR.open("POST", 'fixTypo.do', true);
		XHR.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		var x = 'value=' + $("#UPLOADID").html() + ";" + term + ";"
				+ replacement;
		XHR.send(x);
	}
}

function updateTermTypoInWebPage(term, replacement) {
	var index = 0;
	var termCopy = term;
	var replacementCopy = replacement;

	var objs = $("[termName='" + termCopy + "']");
	while (objs.size() > 0) {
		objs.each(function(i, obj) {
			changeTermName(obj, replacementCopy);
		});

		index++;
		termCopy = term + "_" + index;
		replacementCopy = replacement + "_" + index;
		objs = $("[termName='" + termCopy + "']");
	}
}

function changeTermName(obj, replacementName) {
	$(obj).attr("id", replacementName);
	$(obj).attr("termName", replacementName);

	// possible class name: term_row; term_row_saved; syn
	if ($(obj).hasClass("term_row_saved")) {
		obj = $(obj).find(".mainTerm");
	}

	var termLbl = $(obj).find(".termLabel");
	$(termLbl).attr("onclick",
			"setTerm_categorizing('" + replacementName + "')");
	$(termLbl).html(replacementName);
	if ($(termLbl).hasClass("decisionRemoved")) {
		$(termLbl).attr("id", replacementName);
	}

	// find fix typo img
	$(obj).find(".fixTypoIcon").attr("onclick",
			"fixTypo('" + replacementName + "')");
}

/**
 * allow user to input a term and locate the term
 */
function openTermLocator() {
	var name = prompt("Term name: ", "");
	if (name) {
		setTerm(name);
	}
}

/*
 * finish categorizaing
 */
function finishCategorizing() {
	// check changes
	if (dataChanged()) {
		if (!confirm("You have unsaved changes. "
				+ "Do you want to finish categorization without saving the changes? ")) {
			return;
		}
	}

	var XHR = createXHR();
	if (XHR) {
		XHR.onreadystatechange = function() {
			if (XHR.readyState == 4) {
				if (XHR.status == 200) {
					if (XHR.responseText == "success") {
						window.top.postMessage('done', '*');
						alert("Categorization has been finalized. ");
						window.onbeforeunload = null;
						location.reload();
					} else {
						alert("Categorization was not finalized. Please try again later. ");
					}
				} else {
					alert("Categorization was not finalized. Please try again later. ");
				}
			}
		};
		XHR.open("POST", 'finishCategorization.do', true);
		XHR.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		var x = 'value=' + $("#UPLOADID").html();
		XHR.send(x);
	}
}

/* if there are changes, alert changes before close page */
window.onbeforeunload = function() {
	// var evn;
	// evn = e || event;
	// event = evn;
	// alert(typeof evn);
	if (dataChanged()) {
		return ("You have unsaved changes. If you leave the page you will lose both your reviewing history and categorizing decisions.");

		// window.event.returnValue = "You have unsaved changes in current page.
		// Are you sure you want to leave the page without saving?";
		/*
		 * var confirmed = confirm("You have unsaved changes. Do you want to
		 * save changes before leaving?"); if (confirmed) { event.returnValue =
		 * false; save_categories("true"); }
		 */
		// if (confirm("You have unsaved changes. Do you want to save changes
		// before leaving?")) {
		// save_categories('submit');
		// }
	}
};

document.onmousedown = mouse_down_handler;
document.onmouseup = mouse_up_handler;
document.onmousemove = mouse_move_handler;