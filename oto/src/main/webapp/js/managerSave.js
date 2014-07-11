/**
 * This file deals with the management of final decisions of all three pages.
 * 
 * @author Fengqiong
 */
var decision_cell;// the cell to be updated after saving
var decision_form;// the cell maybe updating after saving (previous accepted
// category will come back to this col)
var acceptance_pic = "up";
var term;
var decision;
var dataset;
var tr_to_append;
var i;

// the following three variables are used when accepted term is replaced by new
// accepted term
var replace;
var old_position;
var old_cell;
var isAccept = false;
var orderBody;

// variables for check merge status
var merge_target_name;
var merge_isSystemMerge;
var check_status_count = 0;

function revokePath(btn) {
	var p_form = btn.parentNode.parentNode.parentNode.parentNode;
	decision_form = p_form.parentNode.parentNode;
	decision = p_form.getAttribute("decision");
	term = p_form.id;
	decision_cell = document.getElementById(term + "_others");
	dataset = p_form.getAttribute("dataset");
	tr_to_append = document.createElement("tr");
	tr_to_append.innerHTML = "<td style='padding: 0px'>" + "<table " + "id='"
			+ term + "' dataset='" + dataset + "'decision='" + decision
			+ "'><tr><td style='padding: 0px'><font "
			+ "class='font-text-style'>" + decision + "</font></td>"
			+ "<td style='padding: 0px'>" + "<img src='images/accept.jpg' "
			+ "height='13px' title='Accept' onclick='acceptPath(this)'"
			+ "style='cursor: pointer;'></img>" + "</td></tr></table></td>";
	var request = '<?xml version="1.0" encoding="UTF-8"?><confirm>';
	request += "<type>2</type>";// type: 1-category, 2-hierarchy, 3-order
	request += "<term>" + term + "</term>";
	request += "<dataset>" + dataset + "</dataset>";
	request += "<decision>" + decision + "</decision>";
	request += "<accept>n</accept>";
	request += "</confirm>";
	save_path(request, decision);
}

function acceptPath(btn) {
	var p_form = btn.parentNode.parentNode.parentNode.parentNode;
	decision_form = p_form.parentNode.parentNode;
	decision = p_form.getAttribute("decision");
	term = p_form.id;
	decision_cell = document.getElementById(term + "_acceptedDecisions");
	dataset = p_form.getAttribute("dataset");
	tr_to_append = document.createElement("tr");
	tr_to_append.innerHTML = "<td style='padding: 0px'>" + "<table " + "id='"
			+ term + "' dataset='" + dataset + "'decision='" + decision
			+ "'><tr><td style='padding: 0px'><font "
			+ "class='font-text-style'>" + decision + "</font></td>"
			+ "<td style='padding: 0px'>" + "<img src='images/revoke.jpg' "
			+ "height='12px' title='Revoke' onclick='revokePath(this)'"
			+ "style='cursor: pointer;'></img>" + "</td></tr></table></td>";
	var request = '<?xml version="1.0" encoding="UTF-8"?><confirm>';
	request += "<type>2</type>";// type: 1-category, 2-hierarchy, 3-order
	request += "<term>" + term + "</term>";
	request += "<dataset>" + dataset + "</dataset>";
	request += "<decision>" + decision + "</decision>";
	request += "<accept>y</accept>";
	request += "</confirm>";
	save_path(request, decision);
}

function getDecision_tr(term, dataset, decision, decidedBy, isAccepted) {
	var tr = document.createElement("tr");
	tr.className = "decision_tr";

	// img
	var img = "accept.jpg";
	if (isAccepted) {
		img = "revoke.jpg"
	}

	// img size
	var imgSize = "13px";
	if (isAccepted) {
		imgSize = "12px";
	}

	// function name
	var functionName = "acceptCategory(this)";
	if (isAccepted) {
		functionName = "revokeCategory(this)";
	}

	var color = "";
	if (decision == "discarded") {
		color = "red";
	}

	tr.innerHTML = "<td style='padding: 0px'>" + "<table " + "id='" + term
			+ "' term='" + term + "' dataset='" + dataset + "' decision='"
			+ decision + "' decidedBy='" + decidedBy
			+ "'><tr><td width='140px' style='padding: 0px' title='"
			+ decidedBy + "'><font " + "class='font-text-style' color='"
			+ color + "'>" + decision + "</font></td>"
			+ "<td style='padding: 0px'>" + "<img src='images/" + img + "' "
			+ "decidedBy='" + decidedBy + "' " + "height='" + imgSize
			+ "' title='Revoke' onclick='" + functionName + "'"
			+ "style='cursor: pointer;'></img>" + "</td></tr></table></td>";

	return tr;

}

/**
 * accept category decision
 * 
 * @param btn
 */
function acceptCategory(btn) {
	var p_form = btn.parentNode.parentNode.parentNode.parentNode;
	decision_form = p_form.parentNode.parentNode;
	decision = p_form.getAttribute("decision");
	term = p_form.id;

	decision_cell = document.getElementById(term + "_acceptedDecisions");
	dataset = p_form.getAttribute("dataset");
	tr_to_append = getDecision_tr(term, dataset, decision, btn
			.getAttribute("decidedBy"), true);

	var request = '<?xml version="1.0" encoding="UTF-8"?><confirm>';
	request += "<type>1</type>";// type: 1-category, 2-hierarchy, 3-order
	request += "<term>" + term + "</term>";
	request += "<dataset>" + dataset + "</dataset>";
	request += "<decision>" + decision + "</decision>";
	request += "<accept>y</accept>";
	request += "</confirm>";

	// save
	save_categorize(request, decision, true);

}

function revokeCategory(btn) {
	var p_form = btn.parentNode.parentNode.parentNode.parentNode;
	decision_form = p_form.parentNode.parentNode;
	decision = p_form.getAttribute("decision");
	term = p_form.id;
	decision_cell = document.getElementById(term + "_unConfirmedDecisions");
	dataset = p_form.getAttribute("dataset");
	tr_to_append = getDecision_tr(term, dataset, decision, btn
			.getAttribute("decidedBy"), false);

	var request = '<?xml version="1.0" encoding="UTF-8"?><confirm>';
	request += "<type>1</type>";// type: 1-category, 2-hierarchy, 3-order

	request += "<term>" + term + "</term>";
	request += "<dataset>" + dataset + "</dataset>";
	request += "<decision>" + decision + "</decision>";
	request += "<accept>n</accept>";
	request += "</confirm>";

	// save
	save_categorize(request, decision, false);
}

function copyAcceptedDecisionsTo(toDataset) {
	// construct value
	var value = toDataset;

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
				// alert(xmlhttp.responseText);
				var response = xmlhttp.responseXML
						.getElementsByTagName("response")[0].childNodes[0].nodeValue;
				alert(response);
				window.location.reload();
			} else {
				alert("Failed to copy decisions. Please try again later. ");
				document.getElementById('serverMessage').innerHTML = "";
			}
		}
	}

	xmlhttp.open("POST", 'copyAcceptedCategorizations.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + value);
	$("#serverMessage").html("Copying accepted decisions...");
}

function acceptSynonym(btn) {
	var p_form = btn.parentNode.parentNode.parentNode.parentNode;
	decision_form = p_form.parentNode.parentNode;
	var category = p_form.getAttribute("category");
	decision = p_form.getAttribute("decision"); // synonym
	term = p_form.getAttribute("term");

	decision_cell = document.getElementById(term + "_" + category
			+ "_acceptedDecisions");
	dataset = p_form.getAttribute("dataset");

	tr_to_append = document.createElement("tr");
	tr_to_append.innerHTML = "<td style='padding: 0px'>" + "<table " + "term='"
			+ term + "' dataset='" + dataset + "' category='" + category
			+ "' decision='" + decision
			+ "'><tr><td width='140px' style='padding: 0px' title='"
			+ btn.getAttribute("decidedBy") + "'><font "
			+ "' class='font-text-style'>" + decision + "</font></td>"
			+ "<td style='padding: 0px'>" + "<img src='images/revoke.jpg' "
			+ "height='12px' title='Revoke' onclick='revokeSynonym(this)' "
			+ "decidedBy='" + btn.getAttribute("decidedBy") + "' "
			+ "style='cursor: pointer;'></img>" + "</td></tr></table></td>";

	var request = '<?xml version="1.0" encoding="UTF-8"?><confirm>';
	request += "<type>4</type>";// type: 1-category, 2-hierarchy, 3-order,
	// 4-synonym
	request += "<term>" + term + "</term>";
	request += "<dataset>" + dataset + "</dataset>";
	request += "<category>" + category + "</category>";
	request += "<decision>" + decision + "</decision>";
	request += "<accept>y</accept>";
	request += "</confirm>";

	// save
	save_synonym(request, decision);
}

function revokeSynonym(btn) {
	var p_form = btn.parentNode.parentNode.parentNode.parentNode;
	decision_form = p_form.parentNode.parentNode;
	decision = p_form.getAttribute("decision");
	var category = p_form.getAttribute("category");
	term = p_form.getAttribute("term");
	decision_cell = document.getElementById(term + "_" + category
			+ "_unConfirmedDecisions");
	dataset = p_form.getAttribute("dataset");
	tr_to_append = document.createElement("tr");
	tr_to_append.innerHTML = "<td style='padding: 0px'>" + "<table " + "term='"
			+ term + "' dataset='" + dataset + "' category='" + category
			+ "' decision='" + decision
			+ "'><tr><td width='140px' style='padding: 0px' title='"
			+ btn.getAttribute("decidedBy") + "'><font "
			+ "class='font-text-style'>" + decision + "</font></td>"
			+ "<td style='padding: 0px'>" + "<img src='images/accept.jpg' "
			+ "height='13px' title='Accept' onclick='acceptSynonym(this)' "
			+ "decidedBy='" + btn.getAttribute("decidedBy") + "' "
			+ "style='cursor: pointer;'></img>" + "</td></tr></table></td>";
	var request = '<?xml version="1.0" encoding="UTF-8"?><confirm>';
	request += "<type>4</type>";// type: 1-category, 2-hierarchy, 3-order,
	// 4-synonym

	request += "<term>" + term + "</term>";
	request += "<dataset>" + dataset + "</dataset>";
	request += "<category>" + category + "</category>";
	request += "<decision>" + decision + "</decision>";
	request += "<accept>n</accept>";
	request += "</confirm>";
	// alert(request);

	// save
	save_synonym(request, decision);
}

function saveHierarchyByManager(btn) {
	// get decision, term, accept?,
	var p_form = btn.parentNode;
	decision_form = p_form.parentNode.parentNode;
	var t = p_form.getElementsByTagName('select')[0];
	var accept = t.options[t.selectedIndex].value;
	var decision = p_form.getAttribute("decision");
	var term = p_form.getAttribute("term");

	// get decision_cell
	if (accept == "") {
		alert("Select to accept or decline this category decision. ");
		return;
	} else if (accept == "y") {
		decision_cell = document.getElementById(term + '_acceptedDecisions');
	} else {
		decision_cell = document.getElementById(term + '_declinedDecisions');//
	}

	var request = '<?xml version="1.0" encoding="UTF-8"?><confirm>';
	request += "<type>2</type>";// type: 1-category, 2-hierarchy, 3-order
	var dataset = p_form.getAttribute("dataset");
	request += "<term>" + term + "</term>";
	request += "<dataset>" + dataset + "</dataset>";
	request += "<decision>" + decision + "</decision>";
	request += "<accept>" + accept + "</accept>";
	request += "</confirm>";
	// alert(request);

	var confirmed = confirm("NOTE: Decisions cannot be modified after saving. Are you sure you want to save?");
	
	if (!confirmed) {
		return;
	}
	// save
	save(request, decision);
}

function acceptTermOfOrder(btn) {
	term = btn.parentNode.id;
	var distance = btn.parentNode.parentNode.id;
	var orderID = btn.parentNode.parentNode.parentNode.id;
	dataset = btn.getAttribute("rel");
	decision_form = btn.parentNode;
	orderBody = btn.parentNode.parentNode.parentNode.parentNode; // the order
	// group
	// body
	var acceptRow = orderBody.getElementsByClassName("acceptedRow")[0];
	decision_cell = orderBody.getElementsByClassName(distance + "_accepted")[0];

	replace = false;
	isAccept = true;
	var existDivs = acceptRow.getElementsByTagName("div");
	for (i = 0; i < existDivs.length; i++) {
		if (existDivs[i].id == term) {
			tr_to_append = existDivs[i];
			replace = true;
			old_position = tr_to_append.parentNode.id;
			old_cell = orderBody.getElementsByClassName(old_position
					+ "_others")[0];
			break;
		}
	}

	if (!replace) {
		tr_to_append = document.createElement("div");
		tr_to_append.id = term;
		tr_to_append.className = "accepted_term";
		tr_to_append.style["padding"] = "0px 2px 0px 2px";
		tr_to_append.innerHTML = term + " <img rel='" + dataset
				+ "' src='images/revoke.jpg' width='11px' title='Revoke'"
				+ "onclick='revokeTermOFOrder(this)'></img>";
	}

	var request = "<?xml version='1.0' encoding='UTF-8'?><confirm>";
	request += "<type>3</type>";// type: 1-category, 2-hierarchy, 3-order
	request += "<term>" + term + "</term>";
	request += "<dataset>" + dataset + "</dataset>";
	request += "<distance>" + distance + "</distance>";
	request += "<orderID>" + orderID + "</orderID>";
	request += "<accept>y</accept>";
	request += "</confirm>";
	// alert(request);
	// save
	save_termOfOrder(request, decision);
}

function revokeTermOFOrder(btn) {
	term = btn.parentNode.id;
	var distance = btn.parentNode.parentNode.id;
	var orderID = btn.parentNode.parentNode.parentNode.id;
	dataset = btn.getAttribute("rel");
	decision_form = btn.parentNode;
	orderBody = btn.parentNode.parentNode.parentNode.parentNode;
	decision_cell = orderBody.getElementsByClassName(distance + "_others")[0];
	isAccept = false;
	/*
	 * tr_to_append = document.createElement("div"); tr_to_append.id = term;
	 * tr_to_append.style["padding"] = "0px 2px 0px 2px"; tr_to_append.innerHTML =
	 * term + " <img rel='" + dataset + "' src='images/accept.jpg' width='11px'
	 * title='Accept'" + "onclick='acceptTermOFOrder(this)'></img>";
	 */
	var request = "<?xml version='1.0' encoding='UTF-8'?><confirm>";
	request += "<type>3</type>";// type: 1-category, 2-hierarchy, 3-order
	request += "<term>" + term + "</term>";
	request += "<dataset>" + dataset + "</dataset>";
	request += "<distance>" + distance + "</distance>";
	request += "<orderID>" + orderID + "</orderID>";
	request += "<accept>n</accept>";
	request += "</confirm>";
	// alert(request);
	// save
	save_termOfOrder(request, decision);
}

function saveTermInOrderByManager(btn) {
	// get decision, term, accept?,
	var p_form = btn.parentNode;
	decision_form = p_form.parentNode.parentNode;
	var t = p_form.getElementsByTagName('select')[0];
	var accept = t.options[t.selectedIndex].value;
	var orderID = p_form.getAttribute("orderID");
	var distance = p_form.getAttribute("distance");
	var term = p_form.getAttribute("term");

	// get decision_cell
	if (accept == "") {
		alert("Select to accept or decline this category decision. ");
		return;
	} else if (accept == "y") {
		decision_cell = document.getElementById(orderID + "_" + distance
				+ '_acceptedDecisions');
		acceptance_pic = "up";
	} else {
		decision_cell = document.getElementById(orderID + "_" + distance
				+ '_declinedDecisions');//
		acceptance_pic = "down";
	}

	var request = '<?xml version="1.0" encoding="UTF-8"?><confirm>';
	request += "<type>3</type>";// type: 1-category, 2-hierarchy, 3-order
	var dataset = p_form.getAttribute("dataset");
	request += "<term>" + term + "</term>";
	request += "<dataset>" + dataset + "</dataset>";
	request += "<distance>" + distance + "</distance>";
	request += "<orderID>" + orderID + "</orderID>";
	request += "<accept>" + accept + "</accept>";
	request += "</confirm>";
	alert(request);

	var confirmed = confirm("NOTE: Decisions cannot be modified after saving. Are you sure you want to save?");
	if (!confirmed) {
		return;
	}

	// save
	save(request, term);
}

/**
 * delete dataset
 * 
 * @param dataset
 */
function doDeleteDataset(dataset, fromMerge) {
	document.getElementById('serverMessage').innerHTML = "<label>deleting dataset '"
			+ dataset + "'... </label>"

	var value = dataset;
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
				// alert(xmlhttp.responseText);
				var response = xmlhttp.responseText;
				if (fromMerge) {
					$("#serverMessage").toggleClass("info");
					document.getElementById('serverMessage').innerHTML = "The newly created dataset '"
							+ dataset + "' was deleted. ";
				} else {
					alert(response);
					window.location.reload();
				}
			} else {
				if (fromMerge) {
					$("#serverMessage").toggleClass("error");
					$("#serverMessage").html(
							"Failed to delete dataset '" + dataset
									+ "'. You can delete it later. ");
				} else {
					alert("Failed to delete dataset. Please try again later. ");
					document.getElementById('serverMessage').innerHTML = "";
				}
			}
		}
	}

	xmlhttp.open("POST", 'deleteDataset.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + value);
}

/**
 * delete dataset after confirmed with user
 * 
 * @param dataset
 * @param type
 */
function deleteDataset(dataset) {
	var confirmed = confirm("Warning: This will permanently remove all data in this dataset. This operation cannot be undone. Are you sure you want to delete this dataset?");
	if (!confirmed) {
		return;
	}
	doDeleteDataset(dataset, false);
}

/**
 * do finalize
 * 
 * @param dataset
 * @param type
 */
function doFinalize(dataset, type) {
	check_status_count = 0;
	var value = dataset + "::" + type;
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
				var response = xmlhttp.responseXML
						.getElementsByTagName("response")[0].childNodes[0].nodeValue;
				alert(response);
				window.location.reload();
			} else {
				alert("Failed to finalize dataset. Please try again later. ");
			}
		}
	}

	xmlhttp.open("POST", 'finishConfirming.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + value);
	setTimeout(checkFinalizeStatus(), 1000);
}

// invoked when hit btn finish confirming.
function finalizeDataset(dataset, type) {
	// type: 1-categorizing; 2-hierarchy tree; 3-orders
	var bothPageConfirmed = confirm("Categorization and synonym decisions are displayed in separate tabs. "
			+ "This will finalize both 'Categories' and 'Synonyms' tabs.\n"
			+ "Please make sure you have checked both tabs for correctness. \n\n"
			+ "Click 'OK' to continue. ");
	if (!bothPageConfirmed) {
		return;
	}

	var confirmed = confirm("After this dataset is finalized, it will be available for download on Github (https://github.com/biosemantics/glossaries) and no further changes will be possible. "
			+ "Are you sure you want to finalize this dataset?");
	if (confirmed) {
		document.getElementById('serverMessage').innerHTML = "<label>Finalizing the dataset ... This may take a while ... Please wait ...</label>";
		doFinalize(dataset, type);
	}
}

// validate the inputted name for merged dataset
function isValidMerge(glossID, name, isNameVerified) {
	var i;
	var count = 0;
	var datasetString = "";
	var dss = document.getElementsByName("datasetsToMerge_" + glossID);
	for (i = 0; i < dss.length; i++) {
		if (dss[i].checked == true) {
			count++;
			if (datasetString == "") {
				datasetString += dss[i].value;
			} else {
				datasetString += ";" + dss[i].value;
			}
		}
	}

	if (count < 1) {
		alert("Select the datasets you want to merge. ");
		return "";
	}

	if (isNameVerified) {
		return datasetString;
	}

	// name cannot be empty
	if (name == null || name == "") {
		alert("Please specify the name of merged dataset.");
		return "";
	}

	// check name rules
	var re = /^[\w_]+$/g;
	var accepted = re.exec(name);
	if (!accepted) {
		alert("Names may only contain 'A'-'Z', 'a'-'z', '0'-'9' and '_'. \n "
				+ "Please choose another name. ");
		return "";
	}

	return datasetString; // when it is a valid merge, return the datasets
}

/**
 * merge into system reserved dataset: static dataset name
 * 
 * @param glossID
 * @param glossName
 */
function mergeIntoSystemDataset(glossID, glossName) {
	var name = glossName + "_glossary";
	// validate the input
	var datasetsString = isValidMerge(glossID, name, true);

	if (datasetsString == "") {
		return;
	}
	var confirmed = confirm("The system dataset for " + glossName + " is '"
			+ name + "'. \n\n"
			+ "Are you sure you want to merge selected datasets into '" + name
			+ "'? ");
	if (!confirmed) {
		return;
	}

	if (datasetsString != "") {
		backupBeforeMerge(glossID, name, datasetsString, false, true);
	}
}

// check if the dataset already exists or not
function mergeDatasets(glossID, glossName) {
	var name = document.getElementById("new_dataset_name_" + glossID).value;

	// validate the input
	var datasetsString = isValidMerge(glossID, name, false);
	if (datasetsString == "") {
		return;
	}

	name = glossName + "_" + name;
	var confirmed = confirm("The name of the merged dataset will be '" + name + "'. \n"
			+ "Press 'OK' to continue with the modified name.");
	if (!confirmed) {
		return;
	}

	// accidentally input the system reserved dataset name
	if (name == glossName + "_" + "glossary") {
		alert("Dataset name '"
				+ name
				+ "' is a system reserved dataset which you cannot write into. \n"
				+ "Please type in another name. ");
		return;
	}

	if (datasetsString == name) {// only one dataset selected and equal to
		// the target name
		alert("Please select at least one other dataset to merge into '"
				+ name + "'. ");
		return;
	}

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
				// alert(xmlhttp.responseText);
				var response = xmlhttp.responseXML
						.getElementsByTagName("response")[0].childNodes[0].nodeValue;

				// check if it is merged into an existing dataset or merged into
				// a new one
				if (response == "yes-merge") {
					var mergedIntoConfirmed = confirm("Dataset '"
							+ name
							+ "' already exists. \n\n"
							+ "Press 'OK' to merge selected datasets into dataset '"
							+ name + "'");
					if (mergedIntoConfirmed) {
						backupBeforeMerge(glossID, name, datasetsString, false,
								false);
					}
				} else if (response == "yes-rename") {
					alert("Dataset '" + name
							+ "' already exists. Please type in another name. ");
					$("#serverMessage").html("");
				} else if (response == "no") {
					backupBeforeMerge(glossID, name, datasetsString, true,
							false);
				} else {
					alert(response);
					$("#serverMessage").toggleClass("info");
					$("#serverMessage").html("");

				}
			} else {
				document.getElementById('serverMessage_' + glossID).innerHTML = "";
				alert("Failed to check if dataset exists. Please try again later. ");
			}
		}
	}

	xmlhttp.open("POST", 'checkDatasetExistence.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + name);
	document.getElementById('serverMessage').innerHTML = "Validating dataset name...";
}

function backupBeforeMerge(glossID, datasetName, datasetsString, toCreateNewDB,
		isSystemMerge) {
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
				// alert(xmlhttp.responseText);
				var response = xmlhttp.responseXML
						.getElementsByTagName("response")[0].childNodes[0].nodeValue;
				if (response == "success") {
					document.getElementById('serverMessage').innerHTML = "Database backed up successfully. ";
					if (toCreateNewDB) {
						createMergedDataset(glossID, datasetName,
								datasetsString);
					} else {
						doMerge(glossID, datasetName, datasetsString,
								isSystemMerge, false);
					}
				} else {
					alert("Failed to back up database. Please try again later. ");
					$("#serverMessage").html("");
				}
			} else {
				$("#serverMessage").html("");
				alert("Failed to back up database. Please try again later. ");
			}
		}
	}
	xmlhttp.open("POST", 'backupDB.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=beforeMerge');
	document.getElementById('serverMessage').innerHTML = "Backing up database...";
}

/**
 * create dataset to be merged into before merge data
 * 
 * @param glossID
 * @param datasetName
 * @param datasetsString
 */
function createMergedDataset(glossID, datasetName, datasetsString) {
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
				// alert(xmlhttp.responseText);
				var response = xmlhttp.responseXML
						.getElementsByTagName("response")[0].childNodes[0].nodeValue;
				if (response == "successful") {
					doMerge(glossID, datasetName, datasetsString, false, true);
				} else if (response == "failed") {
					alert("Failed at creating dataset '" + datasetName
							+ "'. Please try again later. ");
					$("#serverMessage").toggleClass("error");
					$("#serverMessage").html("");
				} else {
					alert(response);
					$("#serverMessage").toggleClass("info");
					$("#serverMessage").html("");
				}
			} else {
				$("#serverMessage").html("");
				alert("Failed to create dataset '" + datasetName
						+ "'. Please try again later. ");
			}
		}
	}
	xmlhttp.open("POST", 'createDataset.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + glossID + ";" + datasetName);
	document.getElementById('serverMessage').innerHTML = "Creating dataset '"
			+ datasetName + "' ...";
}

/**
 * after validation, do merge
 * 
 * @param glossID
 * @param target_name:
 *            the name of the merged dataset
 * @param datasets:
 *            the string that holds all the source datasets
 * @param isSystemMerge
 * @param isTargetNewlyCreated:
 *            when true and merge failed, delete newly created target
 */
function doMerge_bak(glossID, target_name, datasets, isSystemMerge,
		isTargetNewlyCreated) {
	merge_target_name = target_name;
	merge_isSystemMerge = isSystemMerge;
	check_status_count = 0;

	var value = "";
	if (isSystemMerge) {
		value = "System;"; // system merge
	} else {
		value = "Normal;" // normal merge
	}
	value += glossID + ";" + target_name + ";" + datasets; // glossaryID is

	// alert(value);
	var msgLabel = $("#serverMessage");

	/*
	 * if (isSystemMerge) { msgLabel = $("#serverMessage_" + glossID +
	 * "_system"); } else { msgLabel = $("#serverMessage_" + glossID); }
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
		alert("readySate is : " + xmlhttp.readyState + "; and status is: "
				+ xmlhttp.status);
		if (xmlhttp.readyState == 4) {
			if (xmlhttp.status == 200) {
				// alert(xmlhttp.responseText);
				var response = xmlhttp.responseXML
						.getElementsByTagName("response")[0].childNodes[0].nodeValue;
				if (response == "success") {
					msgLabel.toggleClass("info");
					msgLabel.html("Successfully merged datasets. ");
					if (isSystemMerge) {
						msgLabel
								.html("Finalizing dataset '"
										+ target_name
										+ "'... This may take a while. Please wait ...");
						doFinalize(target_name, "1");
					} else {
						alert("Datasets were successfully merged into '" + target_name
								+ "'.");
						msgLabel.toggleClass("info");
						msgLabel.html("");
						window.location.reload();
					}
				} else {
					msgLabel.toggleClass("error");
					msgLabel
							.html("Failed to insert source data into merged dataset. Please try again later.");
					doDeleteDataset(target_name, true);
					alert("Failed to merge dataset. Please try again later. ");
					msgLabel.html("");
					window.location.reload();
				}
			} else {
				checkMergeStatus();
			}
		} else {
			checkMergeStatus();
		}
	}

	xmlhttp.open("POST", 'mergingDatasets.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8");
	xmlhttp.send('value=' + value);
	msgLabel
			.html("Merging datasets. ... This may take a while. ... Please wait. ...");
}

/**
 * test version of doMerge: directly use checkMergeStatus
 * 
 * @param glossID
 * @param target_name
 * @param datasets
 * @param isSystemMerge
 * @param isTargetNewlyCreated
 */
function doMerge(glossID, target_name, datasets, isSystemMerge,
		isTargetNewlyCreated) {
	merge_target_name = target_name;
	merge_isSystemMerge = isSystemMerge;
	check_status_count = 0;

	var value = "";
	if (isSystemMerge) {
		value = "System;"; // system merge
	} else {
		value = "Normal;" // normal merge
	}
	value += glossID + ";" + target_name + ";" + datasets; // glossaryID is
	var msgLabel = $("#serverMessage");

	var xmlhttp;
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}

	xmlhttp.open("POST", 'mergingDatasets.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8");
	xmlhttp.send('value=' + value);
	msgLabel
			.html("Merging datasets. ... This may take a while. ... Please wait. ...");
	setTimeout(checkMergeStatus, 1000);
}

/**
 * finalize may take a long, periodically send query to check finalize status.
 */
function checkFinalizeStatus() {
	check_status_count++;
	var msgLabel = $("#serverMessage");
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
				var response = xmlhttp.responseXML
						.getElementsByTagName("response")[0].childNodes[0].nodeValue;

				if (response == "null") {
					// merging not initialized yet, wait for 5s to check again
					setTimeout(checkFinalizeStatus, 1000);
				} else if (response == "processing") {
					msgLabel.toggleClass("info");
					msgLabel
							.html("("
									+ check_status_count
									+ " s) Still finalizing datasets ... Please wait ...");
					setTimeout(checkFinalizeStatus, 1000);
				} else if (response == "success") { // copied from doMerge
					msgLabel.toggleClass("info");
					msgLabel.html("Successfully finalized datasets. ");
				} else { // error
					msgLabel.toggleClass("error");
					msgLabel
							.html("Failed to finalize datasets. Please try again later.");
					alert("Failed to finalize datasets. Please try again later. ");
					msgLabel.html("");
					window.location.reload();
				}
			} else {
				alert("Failed to check status of finalizing process. (This does not mean that finalizing has failed. "
						+ "It could be that the process is taking longer than expected.) Please come back later to view the results. ");
			}
		}
	}

	xmlhttp.open("POST", 'checkMergeStatus.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8");
	xmlhttp.send('value=finalizeStatus');
}

/**
 * Since merge may take a long time, the server will update a mergeStatus in the
 * session. This function will periodically send query for merge status
 * (interval 1s) to keep the session alive until we get a merge result.
 */
function checkMergeStatus() {
	check_status_count++;
	var msgLabel = $("#serverMessage");
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
				var response = xmlhttp.responseXML
						.getElementsByTagName("response")[0].childNodes[0].nodeValue;

				if (response == "null") {
					// merging not initialized yet, wait for 5s to check again
					setTimeout(checkMergeStatus, 1000);
				} else if (response == "processing") {
					msgLabel.toggleClass("info");
					msgLabel
							.html("("
									+ check_status_count
									+ " s) Still merging datasets ... Please wait ...");
					setTimeout(checkMergeStatus, 1000);
				} else if (response == "success") { // copied from doMerge
					msgLabel.toggleClass("info");
					msgLabel.html("Merge datasets SUCCESSFULLY. ");
					if (merge_isSystemMerge) {
						msgLabel
								.html("Finalizing dataset '"
										+ merge_target_name
										+ "'... This may take a while. Please wait ...");
						doFinalize(merge_target_name, "1");
					} else {
						alert("Datasets were successfully merged into '"
								+ merge_target_name + "'.");
						msgLabel.toggleClass("info");
						msgLabel.html("");
						window.location.reload();
					}
				} else { // copied from doMerge
					msgLabel.toggleClass("error");
					msgLabel
							.html("Failed to insert source data into merged dataset. Please try again later.");
					doDeleteDataset(merge_target_name, true);
					alert("Failed to merge dataset. Please try again later. ");
					msgLabel.html("");
					window.location.reload();
				}
			} else {
				alert("Failed to check merge status. (This does not mean the merge has failed. "
						+ "It could be that the process is taking longer than expected.) Please come back later to view the results. ");
			}
		}
	}

	xmlhttp.open("POST", 'checkMergeStatus.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8");
	xmlhttp.send('value=mergeStatus');
}

// invoked when hit btn reopenDataset.
function reopenDataset(dataset, type) {
	// type: 1-categorizing; 2-hierarchy tree; 3-orders

	var confirmed = confirm("Reopening this dataset will cancel the current download. "
			+ "Are you sure you want to reopen this dataset?");
	if (!confirmed) {
		return;
	}

	document.getElementById('serverMessage').innerHTML = "Reopening the dataset ..."

	var value = dataset + "::" + type;
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
				alert("Successfully reopened dataset.");
				window.location.reload();
			} else {
				alert("Failed to open dataset. Please try again later. ");
			}
		}
	}

	xmlhttp.open("POST", 'reopenDataset.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + value);
}

function save_synonym(requestXML, decision) {
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
				// append saved decision in the decision cell and delete
				// decision_form
				var hasAcceptedDecision = false;
				var acceptedDecision = "";
				if (decision_cell.getElementsByTagName("img").length > 0) {
					hasAcceptedDecision = true;
					acceptedDecision = decision_cell
							.getElementsByTagName("img")[0].id;
				}
				decision_cell.appendChild(tr_to_append);
				decision_form.parentNode.removeChild(decision_form);
			} else {
				// show customer error
				alert("We are facing an internal error: " + xmlhttp.status
						+ ". Please try again. ");
			}
		}
	}

	xmlhttp.open("POST", 'saveManagerDecision.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + requestXML);
}

function save_categorize(requestXML, decision, isAccept) {
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
				var response = xmlhttp.responseXML
						.getElementsByTagName("response")[0].childNodes[0].nodeValue;
				if (response.indexOf("ERROR") == 0) {
					alert(response);
				} else {
					// append saved decision in the decision cell and delete
					// decision_form
					var hasAcceptedDecision = false;
					var acceptedDecision = "";
					if (decision_cell.getElementsByTagName("img").length > 0) {
						hasAcceptedDecision = true;
						acceptedDecision = decision_cell
								.getElementsByTagName("img")[0].id;
					}

					// related modification first
					if (isAccept) {
						moveConflictingDecisions(decision);
					}

					decision_cell.appendChild(tr_to_append);
					decision_form.parentNode.removeChild(decision_form);
				}
			} else {
				// show customer error
				alert("An internal error occurred: " + xmlhttp.status
						+ ". Please try again. ");
			}
		}
	}

	xmlhttp.open("POST", 'saveManagerDecision.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + requestXML);
}

/**
 * move conflicting decisions when accepte a decision in finalize categorization
 * page
 * 
 * Basically, when accept "discarded", empty all other accepted decisions
 * otherwise, move back "discarded"
 * 
 * @param decision
 */
function moveConflictingDecisions(decision) {
	var decisionsToMove = decision_cell.getElementsByClassName("decision_tr");
	if (decisionsToMove.length > 0) {
		var i;
		for (i = decisionsToMove.length - 1; i >= 0; i--) {
			var tr = decisionsToMove[i];
			var elements = tr.getElementsByTagName("table");

			if (elements.length > 0) {
				var decisionToMove = elements[0].getAttribute("decision");

				if (decision == 'discarded'
						|| (decision != 'discarded' && decisionToMove == 'discarded')) {
					var append = getDecision_tr(elements[0]
							.getAttribute("term"), elements[0]
							.getAttribute("dataset"), elements[0]
							.getAttribute("decision"), elements[0]
							.getAttribute("decidedBy"), false);

					decision_form.parentNode.appendChild(append);
					tr.parentNode.removeChild(tr);
				}
			}
		}
	}
}

function save_path(requestXML, decision) {
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
				// append saved decision in the decision cell and delete
				// decision_form
				var hasAcceptedDecision = false;
				var acceptedDecision = "";
				if (decision_cell.getElementsByTagName("img").length > 0) {
					hasAcceptedDecision = true;
					acceptedDecision = decision_cell
							.getElementsByTagName("img")[0].id;
				}
				decision_cell.appendChild(tr_to_append);
				decision_form.parentNode.removeChild(decision_form);
			} else {
				// show customer error
				alert("An internal error occurred: " + xmlhttp.status
						+ ". Please try again. ");
			}
		}
	}

	xmlhttp.open("POST", 'saveManagerDecision.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + requestXML);
}

function save_termOfOrder(requestXML, decision) {
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
				if (isAccept) {
					decision_cell.appendChild(tr_to_append);
					setTermsColor(term, "accepted");
				} else {
					decision_form.parentNode.removeChild(decision_form);
					setTermsColor(term, "revoked");
				}
			} else {
				// show customer error
				alert("An internal error occurred: " + xmlhttp.status
						+ ". Please try again. ");
			}
		}
	}

	xmlhttp.open("POST", 'saveManagerDecision.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + requestXML);
}

function setTermsColor(termName, direction) {
	if (orderBody != null) {
		var terms = orderBody.getElementsByClassName("term_" + termName);
		var color = "";
		var src = "images/accept";
		if (direction == "accepted") {
			color = "grey";
			src += "_grey.jpg";
		} else if (direction == "revoked") {
			color = "black";
			src += ".jpg";
		}
		for (i = 0; i < terms.length; i++) {
			terms[i].style["color"] = color;
			terms[i].getElementsByTagName("img")[0].src = src;
		}
	}
}

function save(requestXML, decision) {
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
				// append saved decision in the decision cell and delete
				// decision_form
				decision_cell.innerHTML = decision_cell.innerHTML
						+ "<img src='images/" + acceptance_pic
						+ ".jpg' height='15px'/> " + decision + "<br>";
				decision_form.parentNode.removeChild(decision_form);
			} else {
				// show customer error
			}
		}
	}

	xmlhttp.open("POST", 'saveManagerDecision.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + requestXML);
}