/**
 * the js file for manageDataset.jsp page by Fengqiong Huang
 */

$(function() {
	/**
	 * set display or hide tasks for datasets
	 */
	$(".nodeStatus").click(toggleDisplayOfTasks);
	$(".datasetStatus").click(toggleDisplayOfTasks);
	$("label.datasetName").click(toggleDisplayOfTasks);
	$("label.systemReserved").click(toggleDisplayOfTasks);

	/**
	 * set onchange attribute for select file button
	 */

	$(".selectFileInput").change(fileSelectedChanged);
});

/**
 * check file extension: only .csv files are allowed
 */
function fileSelectedChanged() {
	var filePath = $(this).attr("value");

	var ext = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
	if (ext != 'csv') {
		alert('Only .csv files are allowed.');
		$(this).attr("value", "");
	}
}

/**
 * toggle display of tasks when dataset name is clicked
 */
function toggleDisplayOfTasks() {
	var e_li = $(this).parent();
	$('ul:first', e_li).toggle();
	if ($('.nodeStatus:first', e_li).attr('src').indexOf("open") >= 0) {
		$('.nodeStatus:first', e_li).attr('src', "images/node_closed.png")
		$(e_li).find(".actionbuttons").toggle();
	} else {
		$('.nodeStatus:first', e_li).attr('src', "images/node_open.png")
		$(e_li).find(".actionbuttons").toggle();
	}
}

/**
 * create an empty dataset for current user
 */
function create_dataset() {
	// validate input
	var prefix = $("#datasetPrefix").val();
	var glossaryID = $("#glossaryID").val();

	if (prefix == "") {
		alert("Please input a dataset name. ");
		return;
	}

	if (glossaryID == "") {
		alert("Please select a taxon group.");
		return;
	}

	// validate input: start with letters, can contain letters, numbers,
	// underscore
	var regex = /^[a-zA-Z][a-zA-Z\d_]+$/;
	if (!regex.test(prefix)) {
		alert("Dataset names must starts with letter and may contain letters, numbers and underscores. \n\nPlease input a valid order name.");
		return;
	}

	// send to server
	$
			.ajax({
				url : "createDatasetForUser.do",
				data : "value=" + prefix + ";" + glossaryID,
				beforeSend : function() {
					$("#creationNote").show();
				},
				success : function(msg) {
					$("#creationNote").hide();
					if (msg.indexOf("error") >= 0) {
						alert("Failed to create dataset. Please try again later. ");
					} else {// success, return dataset name
						alert("Dataset '"
								+ msg
								+ "' was created successfully. "
								+ "You may import tasks on the right side of this page.");
						location.reload();
					}
				}
			});

}

/**
 * set the privacy level of the dataset
 * 
 * @param btn
 * @param datasetName
 * @param privacy
 */
function setDatasetPrivacy(btn, datasetName, privacy) {
	$.ajax({
		url : "setDatasetPrivacy.do",
		data : "value=" + datasetName + ";" + privacy,
		success : function(msg) {
			if (msg.indexOf("error") >= 0) {
				alert("Failed to set dataset privacy. Please try again later. ");
			} else {// success
				// change privacy sign
				var signName = (privacy == "1" ? "private" : "public");
				$(btn).parent().find(".datasetStatus").attr("src",
						"images/" + signName + ".png");

				// change the [set privacy] button value
				$(btn).attr("value",
						"Make " + (privacy == "1" ? "Public" : "Private"));

				// change the [set privacy] button onclick function
				$(btn).attr(
						"onclick",
						"setDatasetPrivacy(this, '" + datasetName + "', '"
								+ (privacy == "1" ? "0" : "1") + "')");
			}
		}
	});
}

/**
 * delete a dataset
 * 
 * @param datasetName
 */
function deleteDataset(btn, datasetName) {
	if (!confirm("Warning: This will permanently remove all data in this dataset. This operation cannot be undone. \n\n"
			+ "Are you sure you want to delete dataset '"
			+ datasetName
			+ "'?")) {
		return;
	}

	$.ajax({
		url : "deleteDataset.do",
		data : "value=" + datasetName,
		beforeSend : function() {
			$(btn).parent().find(".processingNote").show();
			$(btn).parent().find(".processingNote")
					.html("Deleting dataset ...");
			$(btn).parent().find(".processingSign").show();
		},
		success : function(msg) {
			$(btn).parent().find(".processingNote").hide();
			$(btn).parent().find(".processingSign").hide();
			if (msg.indexOf("Error") >= 0) {
				alert("Failed to delete dataset. Please try again later. ");
			} else {// success
				alert("Dataset '" + datasetName
						+ "' was deleted sucessfully.");
				location.reload();
			}
		}
	});
}

/**
 * hide the html defined choose file button and use import button to trigger it
 * 
 * @param importBtn
 */
function selectFile(importBtn) {
	var uploadForm = $(importBtn).parent().find(".uploadForm").first();
	uploadForm.show();
	$(importBtn).hide();
	// $(importBtn).next().hide();

	uploadForm.find(".selectFileInput").first().trigger("click");

}

/**
 * submit the import form: check selected file before submit
 * 
 * note: can be done with ajax but not browser compatible
 * (http://stackoverflow.com/questions/166221/how-can-i-upload-files-asynchronously-with-jquery)
 * 
 * @param submitBtn
 */
function submitImport(submitBtn) {
	if ($(submitBtn).parent().find(".selectFileInput").first().val() == "") {
		alert("No file chosen. Please select a .csv file to import. ");
		return;
	}

	$(submitBtn).parent().submit();
}

/**
 * alert message if exists when loading this page in order to display import
 * result
 * 
 * @param msg
 */
function loadMessage(msg) {
	if (msg != "null") {
		alert(msg);
	}
}