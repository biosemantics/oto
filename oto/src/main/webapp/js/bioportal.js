/*js for bioportal interface*/

var defaultOlsURL = "http://www.ebi.ac.uk/ontology-lookup/";
var poroURL = "http://purl.bioontology.org/ontology/PORO";
var OLSURL = "http://www.ebi.ac.uk/ontology-lookup/";


/**
 * append scrollTop as a parameter of the url
 * @param term
 * @param location
 */
function updateURL(term, location) {
	var scrollTop = $("#DIV_TERMLIST").scrollTop();
	var obj_id = "LEFTLIST_a_" + location + "_" + term;
	$("#" + obj_id).attr("href", $("#" + obj_id).attr("href") + "&scroll=" + scrollTop);
}

/**
 * update the scroll top of term list on the left
 * @param scrollValue
 */
function updateScrollBar_left(scrollValue) {
	$("#DIV_TERMLIST").scrollTop(scrollValue);
}

/**
 * check all pending terms, try to get permanent ids for them
 */
function UpdateAdoptions() {
	
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
				var msg = xmlhttp.responseXML.getElementsByTagName("response")[0].childNodes[0].nodeValue;
				if (msg.indexOf("Error") > 0) {
					alert(msg);
				} else {
					alert(msg + " terms approved since last check. ");	
				}
			} else {
				alert("Failed to update term adoptions. Please try again later. ");
			}
		}
	}

	xmlhttp.open("POST", 'checkApprovedterms.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8");
	xmlhttp.send('');
}

/**
 * delete an existing submission
 */
function deleteSubmission() {
	var confirmed = confirm("Are you sure you want to delete this submission? ");
	if (confirmed) {
		$("#submission_action").val("delete");
		$("#submissonForm").submit();	
	}
}

/**
 * update OLS url
 */
function setOntology() {
	var ontology = $("#ontology").val();
	switch (ontology) {
	case "":
		OLSURL = defaultOlsURL;
		break;
	case "PORO":
		OLSURL = poroURL;
		break;
	default:
		OLSURL = defaultOlsURL + "browse.do?ontName=" + ontology;
		break;
	}

}

/**
 * validate required fields
 */
function validateSubmission() {
	if ($("#termName").val() == "") {
		alert("Term name cannot be empty.");
		return;
	}

	if ($("#definition").val() == "") {
		alert("Definition cannot be empty.");
		return;
	}

	var ontology = $("#ontology").val();
	if (ontology == "") {
		alert("Please select an ontology.");
		return;
	}

	var superClassID = $("#superClassID").val();
	if (superClassID == "") {
		alert("Super Class ID cannot be empty. You can get a valid super class ID from 'Ontology Lookup Service'. ");
		return;
	}

	// check the format of super class ID
	var pattern = new RegExp("[A-Z]+:\\d+");
	if (!pattern.test(superClassID)) {
		alert("Super Class ID should look like: " + ontology
				+ ":0000001. Please make sure you copied it correctly. ");
		return;
	}

	if ($("#category").val() == "") {
		alert("Category cannot be empty.");
		return;
	}

	$("#submissonForm").submit();
}

/**
 * display the submission form with pre-filled values
 * 
 * @param categoryName
 */
function setCategory(categoryName, src) {
	$("#submissonTD").css({
		"visibility" : "visible"
	});
	$("#btn_submit").val("Submit");
	$("#submission_action").val("submit");
	
	$("#btn_delete").css({
		"visibility" : "hidden"
	});
	
	//set class to be current
	$(".current").removeClass("current");
	$("#CATEGORY_LI_" + categoryName).addClass("current");
	
	updateSubmissionTable("", "", "", "", src, categoryName, "", "");
}

/**
 * set values of the submission table
 * @param def
 * @param syns
 * @param ont
 * @param superClass
 * @param src
 * @param category
 * @param localID
 * @param tmpID: temporary ID from bioportal
 */
function updateSubmissionTable(def, syns, ont, superClass, src, category,
		localID, tmpID) {
	$("#definition").val(def);
	$("#syns").val(syns);
	$("#ontology").val(ont);
	$("#superClassID").val(superClass);
	$("#source").val(src);
	$("#category").val(category);
	$("#localID").val(localID);
	$("#tmpID").val(tmpID);
}

/**
 * update submission form when a new submission has been selected
 * 
 * @param term
 * @param def
 * @param syns
 * @param ont
 * @param superClass
 * @param src
 * @param category
 * @param localID
 */
function submissionDetail(term, def, syns, ont, superClass, src, category,
		localID, tmpID, isAdopted) {
	$("#termName").val(term);
	$("#submissonTD").css({
		"visibility" : "visible"
	});
	$("#btn_submit").val("Update");
	if (isAdopted == 'false') {
		//update action
		$("#submission_action").val("update");
		//show action buttons
		$("#btn_delete").css({
			"visibility" : "visible"
		});
		$("#btn_submit").css({
			"visibility" : "visible"
		});
	} else {
		$("#btn_delete").css({
			"visibility" : "hidden"
		});
		$("#btn_submit").css({
			"visibility" : "hidden"
		});
	}
	
	//set background color to highlight current
	var obj_id = "SUBMISSION_TR_" + localID;
	$(".current").removeClass("current");
	$("#" + obj_id).addClass("current");

	updateSubmissionTable(def, syns, ont, superClass, src, category, localID, tmpID);
}

/**
 * display website OLS: http://www.ebi.ac.uk/ontology-lookup/ so that users can
 * browse the Super Class ID
 */
function openOLS() {
	window.open(OLSURL, '_blank');
}

/**
 * toggle expand/collapse of the terms list
 * 
 * @param obj_expand_img
 */
function expandTerms(obj_img) {
	var img_src = obj_img.src;
	var rowIndex = obj_img.parentNode.parentNode.rowIndex;
	var terms_tr = obj_img.parentNode.parentNode.parentNode.rows[rowIndex + 1];

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
 * remove term from the regular list, put it into the removed term list (left
 * side of the page)
 * 
 * @param term
 * @param j: determine if the term is a structure (j = 0) or a character (j =
 *            1)
 */
function removeTerm(obj_term, term, i, j, glossTypeID) {
	// update the db in server, if success, clone the term and dump it into the
	// removed list
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
				// get the object to move
				var term_to_append = obj_term.parentNode.parentNode;
				var old_ij = ", " + i + ", " + j;
				var new_i = (Number(i) == 0 ? 1 : 0);
				var new_ij = ", " + new_i + ", " + j;
				term_to_append.innerHTML = term_to_append.innerHTML.replace(
						old_ij, new_ij);
				
				// find destination table
				var dest_table;
				var obj_srcSize, obj_destSize;
				if (i == 0 && j == 0) {
					dest_table = $("#RemovedStructuresTable");
					obj_srcSize = document.getElementById("SIZE_OF_LIST_1");
					obj_destSize = document.getElementById("SIZE_OF_LIST_3");
				} else if (i == 0 && j == 1) {
					dest_table = $("#RemovedCharactersTable");
					obj_srcSize = document.getElementById("SIZE_OF_LIST_2");
					obj_destSize = document.getElementById("SIZE_OF_LIST_4");
				} else if (i == 1 && j == 0) {
					dest_table = $("#regStructuresTable");
					obj_srcSize = document.getElementById("SIZE_OF_LIST_3");
					obj_destSize = document.getElementById("SIZE_OF_LIST_1");
				} else {
					dest_table = $("#regCharactersTable");
					obj_srcSize = document.getElementById("SIZE_OF_LIST_4");
					obj_destSize = document.getElementById("SIZE_OF_LIST_2");
				}

				dest_table.append(term_to_append);
				
				//modify the numbers: src--, dest++
				obj_srcSize.innerHTML = Number(obj_srcSize.innerHTML) - 1;
				obj_destSize.innerHTML = Number(obj_destSize.innerHTML) + 1;
			} else {
				alert("Failed to move the term. Please try again later. ");
			}
		}
	}

	xmlhttp.open("POST", 'bioportalMoveTerm.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + term + "&glossaryType=" + glossTypeID);
}
