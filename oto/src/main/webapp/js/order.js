/**
 * This file contains all the dragging and saving functions for terms order
 * page.
 * 
 * Author: Fengqiong
 * 
 * todo:
 * 
 * 1. instructions
 * 
 * 
 * 
 */

var is_drag = false;
var obj_x, obj_y;
var old_x, old_y;
var drag_obj;
var drag_clone;
var drag_clone_content; // the content in drag_clone div
var target_cell;
var old_target_cell;
var current_obj; // the object clicked on
var term_chosen; // get the whole chosen tag div
var cell_chosen;
var drag_from;
var i, j;
var target_div_name = "term_order";
var maskName = "dragging_mask"; // <div id="dragging_mask">
var orders; // the orders list to to-be-saved order group

/**
 * mouse down event handler record the old position of the obj and cursor
 */
function mouse_down_handler(e) {
	// alert("mouse down");
	var evn;
	evn = e || event;
	event = evn;

	if (evn.target) {
		current_obj = evn.target;
	} else {
		current_obj = evn.srcElement;
	}

	if (current_obj.innerHTML == "" || current_obj.innerHTML == "&nbsp;") {
		return false;
	}

	if (current_obj.className == "term_base"
			|| current_obj.className == "term_order"
			|| current_obj.parentNode.className == "term_order") {

		if (current_obj.tagName == "TD") {
			cell_chosen = current_obj;
		} else if (current_obj.tagName == "LABEL") {
			cell_chosen = current_obj.parentNode;
		}
		term_chosen = current_obj;
		is_drag = true;
		set_drag_from();

		// alert("drag_from = " + drag_from);
		getClone();

		// set drag_clone position and visibility
		obj_x = current_obj.offsetLeft
		obj_y = term_chosen.offsetTop;
		var temp = current_obj;
		while (temp.offsetParent) {
			temp = temp.offsetParent;
			obj_x = obj_x + temp.offsetLeft;
			obj_y = obj_y + temp.offsetTop;
		}

		obj_y = obj_y - document.getElementById("dragging_part").scrollTop;

		drag_clone.style["top"] = obj_y + "px";// bug
		drag_clone.style["left"] = obj_x + "px";
		drag_clone.style["visibility"] = "visible";
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

function removeNode(node) {
	// remove the node
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
 * mouse move event handler update position of the dragged obj
 */
function mouse_move_handler(e) {
	var evn;
	evn = e || event;
	if (is_drag) {
		drag_clone.style["visibility"] = "visible";
		var y = evn.clientY
				+ document.getElementsByTagName('html')[0].scrollTop;

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
	var evn;
	evn = e || event;

	if (is_drag) {
		getTargetCell(evn);
		termName = term_chosen.firstChild.nodeValue;
		var termID = term_chosen.id;
		if (target_cell != null) {
			var order_tr = target_cell.parentNode.parentNode.parentNode.parentNode.parentNode;
			if (drag_from == "base") {
				// alert(target_cell.innerHTML);
				var termsInOrder = target_cell.parentNode.innerHTML;
				if (termsInOrder.indexOf(">" + termName + "<") > 0) {
					alert("The term '" + termName
							+ "' is already in this order.");
					var order_label = order_tr.getElementsByTagName('label')[0];
					showSelectedTerms(order_label);
				} else {
					var newTerm = document.createElement('div');
					newTerm.id = termName;
					newTerm.className = "changed";
					var text = term_chosen.firstChild.cloneNode(true);
					newTerm.appendChild(text);
					newTerm.onmousedown = mouse_down_handler;
					// set spliter
					if (target_cell.getElementsByTagName("div").length == 0) {
						target_cell.innerHTML = "";
					}
					target_cell.appendChild(newTerm);
					clearSavingMsg();
					var order_label = order_tr.getElementsByTagName('label')[0];
					showSelectedTerms(order_label);
				}
			} else if (drag_from == "order") {
				if (term_chosen.parentNode != target_cell) {
					// change position
					term_chosen.className = "changed";
					if (target_cell.getElementsByTagName("div").length == 0) {
						target_cell.innerHTML = "";
					}
					target_cell.appendChild(term_chosen);
					clearSavingMsg();
					var order_label = order_tr.getElementsByTagName('label')[0];
					showSelectedTerms(order_label);
				} else {
					// get term info
					setTerm(termName);
					clearSavingMsg();
					highlightThisTerm(term_chosen);
				}
			}
		} else if (drag_from == "order") {
			// remove term from order
			if (term_chosen.getAttribute("rel") == "base") {
				alert("Base term cannot be removed.");
			} else {
				var toRemove = confirm("Remove term from current order?");
				if (toRemove) {
					// remove the term
					var order_tr = term_chosen.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode;
					var td = term_chosen.parentNode;

					// set this order changed
					var order_table = term_chosen.parentNode.parentNode.parentNode.parentNode;
					order_table.className = "changed";

					td.removeChild(term_chosen);
					clearSavingMsg();
					if (td.getElementsByTagName('div').length == 0) {
						td.innerHTML = "&nbsp;";
					}
					// alert(order_tr.innerHTML);
					order_tr.getElementsByTagName('table')[0].className = "changed";
					var order_label = order_tr.getElementsByTagName('label')[0];
					showSelectedTerms(order_label);
				}
			}
		} else {
			// get term info
			setTerm(termName);
			clearSavingMsg();
			highlightThisTerm(cell_chosen);
		}
		// alert(order_tr.innerHTML);
	}

	old_target_cell = target_cell;
	drag_clone_content.parentNode.removeChild(drag_clone_content);
	// alert("cleared");
	delete_drag_clone();
	drag_clone.style["visibility"] = "hidden";
	// term_chosen.style.cursor = 'auto';
	is_drag = false;
	target_cell = null;
	term_chosen = null;

	return false;
}

function clearSavingMsg() {
	var msgs = document.getElementsByClassName("savingMsg");
	for (i = 0; i < msgs.length; i++) {
		msgs[i].innerHTML = "&nbsp;";
	}
}

function getClone() {
	delete_drag_clone();
	drag_clone = document.getElementById(maskName);
	drag_clone_content = term_chosen.cloneNode(true);
	drag_clone.appendChild(drag_clone_content);
}

function delete_drag_clone() {
	if (drag_clone) {
		drag_clone.innerHTML = "";
	}
}

function set_drag_from() {
	if (term_chosen.className == "term_base") {
		drag_from = "base";
	} else {
		drag_from = "order";
	}
}

function getTargetCell(evn) {
	// find class="order_group", get baseOrderID = id
	// order_group.getElementsByClassName "term_order"
	// loop one by one, compare x and y, find target.

	target_cell = null;
	var i, top, bottom, left, right;
	var cursor_x = evn.clientX;
	var cursor_y = evn.clientY
			+ document.getElementById('dragging_part').scrollTop
			+ document.getElementsByTagName('html')[0].scrollTop;
	// alert("cursor_y: " + cursor_y);

	var className = "order_group";
	if (drag_from == "order") {
		className = "terms_order";
	}

	var temp = term_chosen;
	while (temp.parentNode != null && temp.parentNode.className != className) {
		temp = temp.parentNode;
	}

	var order_group = temp.parentNode;
	var cellsList = order_group.getElementsByClassName(target_div_name);
	if (cellsList != null) {
		for (i = 0; i < cellsList.length; i++) {
			var cell = cellsList[i];
			top = 0;
			bottom = 0;
			left = 0;
			right = 0;

			temp = cell;
			top += temp.offsetTop;
			left += temp.offsetLeft;

			while (temp.offsetParent) {
				temp = temp.offsetParent;
				top += temp.offsetTop;
				left += temp.offsetLeft;
			}
			right = left + cell.offsetWidth;
			bottom = top + cell.offsetHeight;
			// alert("top: " + top + "; bottom: " + bottom);

			if (cursor_y >= top && cursor_y <= bottom && cursor_x >= left
					&& cursor_x <= right) {
				target_cell = cell;
				break;
			}

		}
	}
}

function order_init() {
	var element_list = document.getElementsByClassName('term_base');
	for (i = 0; i < element_list.length; i++) {
		element_list[i].onmousedown = mouse_down_handler;
	}
	element_list = document.getElementsByClassName('term_order');
	for (i = 0; i < element_list.length; i++) {
		element_list[i].onmousedown = mouse_down_handler;
	}
	document.onmousemove = mouse_move_handler;
	document.onmouseup = mouse_up_handler;
}

function hasDataToSave(order) {

	// order_group
	var tag_list = order.getElementsByClassName('changed');
	if (tag_list.length > 0) {
		return true;
	}
	return false; // no new term to save
}

/**
 * Highlight all selected terms of this order in the base
 * 
 * @param obj:
 *            the <label> of order name
 */
function showSelectedTerms(obj) {
	// find the div class="order_group", then clearAllTermBGColor
	var order_group = obj;
	while (order_group.className != "order_group") {
		order_group = order_group.parentNode;
	}
	clearAllTermsBGColor(order_group);

	// then highlight the terms in the base: compare one by one in the base, if
	// contains >termName<, highlight
	var term_bases = order_group.getElementsByClassName('term_base');
	var orderHTML = obj.parentNode.parentNode
			.getElementsByClassName('terms_order')[0].innerHTML;
	for (i = 0; i < term_bases.length; i++) {
		var termName = term_bases[i].innerHTML;
		if (orderHTML.indexOf(">" + termName + "<") > 0) {
			term_bases[i].style.backgroundColor = "#C0C0C0";
		}
	}
}

function save_order(btn) {

	var baseOrder = btn;
	while (baseOrder.className != "order_group") {
		baseOrder = baseOrder.parentNode;
	}

	if (!hasDataToSave(baseOrder)) {
		alert("No changes have been made to this order.");
		return;
	}
	// alert(baseOrder.innerHTML);

	// the saving processing image visible
	var orderID = baseOrder.id;
	document.getElementById(orderID + "_processingSaveImage").style.visibility = 'visible';
	var termsBoxes, i, j, k, position = 0;
	var request = '<?xml version="1.0" encoding="UTF-8"?><orders>';

	// base order id of this group
	request += "<baseOrderID>" + baseOrder.id + "</baseOrderID>";

	// new_terms
	var base_terms = baseOrder.getElementsByClassName('term_base');
	for (i = 0; i < base_terms.length; i++) {
		if (base_terms[i].id == "") {
			request += "<new_term>" + base_terms[i].innerHTML + "</new_term>";
		}
	}

	orders = baseOrder.getElementsByClassName('tr_order');

	for (k = 0; k < orders.length; k++) {
		var order = orders[k];
		if (hasDataToSave(order)) {
			position = 0;
			var orderName = order.getElementsByClassName('order')[0].id;
			request += "<order>";
			request += "<orderID>" + order.id + "</orderID>";
			request += "<orderName>" + orderName + "</orderName>";

			// new order has explanation
			if (order.id == null || order.id == "") {
				var labels = order.getElementsByTagName('label');
				if (labels.length > 0) {
					var exp = labels[0].getAttribute('title');
					request += "<exp>" + exp + "</exp>";
				}
			}

			termsBoxes = order.getElementsByClassName('term_order');
			for (i = 0; i < termsBoxes.length; i++) {
				var terms = termsBoxes[i].getElementsByTagName('div');
				if (terms.length > 0) {
					for (j = 0; j < terms.length; j++) {
						request += "<term><name>" + terms[j].id
								+ "</name><position>" + position
								+ "</position></term>";
					}
					position++;
				}
			}
			request += "</order>";
		}
	}

	request += "</orders>";
	// alert(request);

	/*
	 * var confirmed = confirm("NOTE: This order CANNOT be changed after saved.
	 * Are you sure to save this order?"); if (!confirmed) {
	 * document.getElementById(orderID +
	 * "_processingSaveImage").style.visibility = 'hidden'; return; }
	 */

	var xmlhttp;
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4) {
			if (xmlhttp.status == 200) {
				document.getElementById(orderID + "_processingSaveImage").style.visibility = 'hidden';

				// all changed set to be "" for color changing
				var changed = baseOrder.getElementsByClassName('changed');
				for (i = changed.length - 1; i >= 0; i--) {
					changed[i].className = "";
				}
				// set all new_terms to be black
				var baseTerms = baseOrder.getElementsByClassName("term_base");
				for (i = baseTerms.length - 1; i >= 0; i--) {
					if (baseTerms[i].style["color"] == "red") {
						baseTerms[i].style["color"] = "black";
					} else {
						break;
					}
				}

				// give saving message
				document.getElementById(orderID + "_savingMsg").innerHTML = "Data saved successfully. ";

				// set hasconflict sign
				var resultXML = xmlhttp.responseXML;
				var returned_orders = resultXML.getElementsByTagName("orderID");
				for (i = 0; i < returned_orders.length; i++) {
					var returnID = returned_orders.item(i).firstChild.nodeValue;
					for (j = 0; j < orders.length; j++) {
						if (returnID == orders[j].id) {
							var hasConflict = returned_orders.item(i).nextSibling.firstChild.nodeValue;
							if (hasConflict == 'true') {
								orders[j].getElementsByTagName("a")[0].innerHTML = "<img border='0px' style='vertical-align: middle;'"
										+ "src='images/down.jpg' width='12px'></img>";
							} else {
								orders[j].getElementsByTagName("a")[0].innerHTML = "<img border='0px' style='vertical-align: middle;'"
										+ "src='images/view.gif' width='12px'></img>";
							}
						}
					}
				}
			} else {
				// document.getElementById(orderName + "_message").innerHTML =
				// '<label>The server encountered an internal error while
				// processing your request. '
				// + 'The response returned by the server is: '
				// + xmlhttp.statusText + "</label>";
				alert("The server encountered an internal error. Please try again.");
			}
		}
	}

	xmlhttp.open("POST", 'saveOrder.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + request);
}

function newTerm(btn) {
	var name = prompt(
			"Please input term name (may contain letters, numbers, spaces and underscores): ",
			"");

	// validate input
	var regex = /^[a-zA-Z\s\d_]+$/;
	if (!regex.test(name)) {
		alert("Term names may contain letters, numbers, spaces and underscores. Please input a valid term name.");
		return;
	}

	if (name) {
		// name cannot be existed:term_base
		var tr_terms = btn.parentNode.parentNode;
		var terms_existed = tr_terms.getElementsByClassName('term_base');
		var i;
		for (i = 0; i < terms_existed.length; i++) {
			if (name == terms_existed[i].innerHTML) {
				alert("'" + name + "' has already been used in this order.");
				return;
			}
		}

		var td = document.createElement('td');
		td.className = "term_base";
		td.innerHTML = name;
		td.style["color"] = "red";
		td.onmousedown = mouse_down_handler;
		tr_terms.insertBefore(td, btn.parentNode);
		tr_terms.className = "changed";

		var parent = tr_terms.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode;
		var orders = parent.getElementsByClassName('terms_tr');
		var i;
		var emptyCell = document.createElement('td');
		emptyCell.innerHTML = "&nbsp;";
		emptyCell.className = "term_order";
		emptyCell.width = "40px";
		emptyCell.onmousedown = mouse_down_handler;
		for (i = 0; i < orders.length; i++) {
			if (orders[i].getElementsByClassName('term_order').length > 0) {
				orders[i].appendChild(emptyCell.cloneNode(true));
			}
		}
	}
}

function newOrder(btn) {
	var name = prompt(
			"Please provide a name for the order (may contain letters, numbers, spaces, underscores, '(' and ')'): ",
			"");

	// validate input: only allow letters, numbers, space, underscore and ( )
	var regex = /^[a-zA-Z\s\d_\(\)]+$/;
	if (!regex.test(name)) {
		alert("Order names may contain letters, numbers, space, underscores, '(' and ')'. Please input a valid order name.");
		return;
	}

	if (name != null && name != '') {
		var tr_btn = btn.parentNode.parentNode;
		var baseOrder = tr_btn.parentNode.parentNode.parentNode.parentNode.parentNode;
		var baseOrderID = baseOrder.parentNode.parentNode.id;
		var orders_existed = baseOrder.getElementsByClassName('order');
		var i;
		for (i = 0; i < orders_existed.length; i++) {
			if (name == orders_existed[i].id) {
				alert("Order '" + name + "' has already been used.");
				return;
			}
		}

		var explanation = prompt("Please provide a definition for order '"
				+ name + "' (cannot be empty): ");
		if (!explanation || explanation == '') {
			// alert("Please specify functin of order to create a new order. ");
			return;
		}

		var tr = document.createElement('tr');
		tr.className = "tr_order";

		var innerHTML = "<td width='15%' class='order' id='"
				+ name
				+ "' "
				+ "onmouseover='displayEditBtn(this)' onmouseout='hideEditBtn(this)' "
				+ "orderName='"
				+ name
				+ "' orderID=''>"
				+ "<label style='color: black' title = '"
				+ explanation
				+ "' "
				+ "onclick='showSelectedTerms(this)'>"
				+ name
				+ "</label>"
				+ "<img align='bottom' class='editOrderName' src='images/edit.png' "
				+ "height='14px' title='Edit Order Name' style='display: none;'"
				+ "onclick=\"editOrderName('"
				+ baseOrderID
				+ "', '"
				+ name
				+ "')\" />"
				+ "</td>"
				+ "<td class='terms_order' width='85%'><table><tr class='terms_tr'>";
		var count = tr_btn.parentNode.getElementsByClassName('term_base').length;
		// alert(count);
		var i;
		for (i = 0; i < count; i++) {
			innerHTML += "<td width='40px' class='term_order'>&nbsp;</td>"
		}
		tr.innerHTML = innerHTML;
		// alert(innerHTML);
		// tr_btn.parentNode.insertBefore(tr, tr_btn);
		tr_btn.parentNode.parentNode.parentNode.parentNode.parentNode
				.appendChild(tr);

		// clear if there are marked terms
		clearAllTermsBGColor(tr_btn.parentNode);
	}
}

/**
 * Show the comments and decisions when clicking the entropy score
 * 
 * @param order
 */
function showReport(order) {
	window
			.open(
					'comment.do?order=' + order,
					'',
					'height=500,width=1000, directories=no, toolbar=no, location=no, menubar=no,resizable=yes,scrollbars=yes, statusbar=no, left=0,top=0');
}

/**
 * Highlight all the term in this order_group, won't work when click the saved
 * terms because we assume nobody will click the saved orders anymore
 * 
 * @param cell
 */
function highlightThisTerm(cell) {
	var termName = cell.innerHTML;
	if (cell.getElementsByTagName('LABEL').length > 0) {
		termName = cell.getElementsByTagName('LABEL')[0].innerHTML;
	}
	var order_group = cell;
	while (order_group.className != 'order_group') {
		order_group = order_group.parentNode;
	}
	clearBoldTerms(document);// here need to clear all terms since it will
	// also display context
	var term_bases = order_group.getElementsByClassName('term_base');
	var terms = order_group.getElementsByTagName('div');
	for (i = 0; i < term_bases.length; i++) {
		if (term_bases[i].innerHTML == termName) {
			term_bases[i].style.fontWeight = "800";
		}
	}
	for (i = 0; i < terms.length; i++) {
		if (terms[i].id == termName) {
			terms[i].style.fontWeight = "800";
		}
	}
}

/**
 * clear all the background color for specific order group do this before
 * highlight the next term
 * 
 * @param obj:
 *            the div class="order_group"
 */
function clearAllTermsBGColor(obj) {
	var term_bases = obj.getElementsByClassName('term_base');
	var terms_order_saved = obj.getElementsByClassName('term_order_saved');
	var terms_order = obj.getElementsByClassName('term_order');
	for (i = 0; i < term_bases.length; i++) {
		term_bases[i].style.backgroundColor = "";
	}
	for (i = 0; i < terms_order_saved.length; i++) {
		terms_order_saved[i].style.backgroundColor = "";
	}
	for (i = 0; i < terms_order.length; i++) {
		terms_order[i].style.backgroundColor = "";
	}
}

function clearBoldTerms(obj) {
	var term_bases = obj.getElementsByClassName('term_base');
	var terms_order = obj.getElementsByTagName('div');
	for (i = 0; i < term_bases.length; i++) {
		term_bases[i].style.fontWeight = "400";
	}
	for (i = 0; i < terms_order.length; i++) {
		terms_order[i].style.fontWeight = "400";
	}
}

/* if there are changes, alert changes before close page */
window.onbeforeunload = function() {
	if (hasDataToSave(document)) {
		return ("You have unsaved changes.");
	}
}

/* when hover on order name, display edit order name button */
function displayEditBtn(td) {
	var imgs = td.getElementsByClassName("editOrderName");
	if (imgs.length > 0) {
		var img = imgs[0];
		img.style.display = "inline";
	}
}

/* when mouse out order name cell, hide edit order name button */
function hideEditBtn(td) {
	var imgs = td.getElementsByClassName("editOrderName");
	if (imgs.length > 0) {
		var img = imgs[0];
		img.style.display = "none";
	}
}

/* change order name in selected order group */
function editOrderName(groupID, oldName) {
	// user input: replacement of the original term
	var replacement = prompt(
			"New order name for '"
					+ oldName
					+ "' (may contain letters, numbers, spaces, underscores, '(' and ')'): ",
			"");
	if (!replacement) {
		return;
	}

	if (replacement == oldName) {
		return;
	}

	// validate input: only allow letters, numbers, space, underscore and ( )
	var regex = /^[a-zA-Z\s\d_\(\)]+$/;
	if (!regex.test(replacement)) {
		alert("Order names may contain letters, numbers, space, underscores, '(' and ')'. Please input a valid order name.");
		return;
	}

	// check duplication
	if ($("#group_" + groupID).find("[orderName='" + replacement + "']").length > 0) {
		alert("Order name '" + replacement
				+ "' has already been used in this order group. ");
		return;
	}

	// send to server
	var XHR = createXHR();
	if (XHR) {
		XHR.onreadystatechange = function() {
			if (XHR.readyState == 4) {
				if (XHR.status == 200) {
					if (XHR.responseText == "success") {
						updateOrderName(groupID, oldName, replacement);
					} else {
						alert("Failed to change order name from '" + oldName + "' to '"
								+ replacement
								+ "'. Please try again later. ");
					}
				} else {
					alert("Failed to change order name from '" + oldName + "' to '"
							+ replacement
							+ "'. Please try again later. ");
				}
			}
		};
		XHR.open("POST", 'editOrderName.do', true);
		XHR.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		var x = 'value=' + groupID + ";" + oldName + ";" + replacement;
		XHR.send(x);
	}
}

/* update order name after successfully updated server side */
function updateOrderName(groupID, oldName, replacement) {
	// find the cell
	var cell = $("#group_" + groupID).find("[orderName='" + oldName + "']")
			.first();

	// cell id, orderName, inner html of label, edit b
	cell.attr("id", replacement);
	cell.attr("orderName", replacement);
	cell.find("label").first().html(replacement);
	cell.find(".editOrderName").first().attr("onclick",
			"editOrderName('" + groupID + "', '" + replacement + "')");

	// view report link: title, onclick function
	var viewReportLink = cell.find("a").first();
	viewReportLink.attr("title", "View specific order report for "
			+ replacement);
	viewReportLink.attr("onclick", "showReport('" + cell.attr("orderID") + ":"
			+ replacement + "')");
}
