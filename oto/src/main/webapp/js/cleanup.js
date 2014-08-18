/**
 * validate input
 */
function validateInput() {
	// get variables
	var dataset = $("#datasetname").val();
	var tcTable = $("#cleantermcategorytable").val();
	var synTable = $("#cleansynstable").val();

	/**
	 * client validate
	 */
	if (dataset == "") {
		alert("Dataset name cannot be empty");
		return;
	}

	if (tcTable == "") {
		alert("Term Category table cannot be empty");
		return;
	}

	/**
	 * server validate
	 */
	// prepare sending string
	var sendStr = dataset + ";" + tcTable;
	if (synTable != "") {
		sendStr += ";" + synTable;
	}

	// send to server to validate
	$
			.ajax({
				url : "preCheckCleanup.do",
				data : "value=" + sendStr,
				beforeSend : function() {
					$("#processingMsg").html("Validating input ...");
					$("#creationNote").show();
				},
				success : function(msg) {
					$("#creationNote").hide();
					if (msg.indexOf("Error") >= 0) {
						alert(msg);
					} else {// success, return dataset name
						if (confirm(msg
								+ "\nDo you want to continue to clean up the dataset with these glossary records? ")) {
							cleanupDataset(sendStr);
						}
					}
				}
			});
}

/**
 * do the cleaning up
 * 
 * @param value
 */
function cleanupDataset(value) {
	$
			.ajax({
				url : "cleanupDatasetWithGlossary.do",
				data : "value=" + value,
				beforeSend : function() {
					$("#processingMsg")
							.html(
									"Cleaning up dataset ... This may take a while. Please wait ...");
					$("#creationNote").show();
				},
				success : function(msg) {
					$("#creationNote").hide();
					if (msg.indexOf("error") >= 0) {
						alert("Cleaning up dataset failed. Please try again later. ");
					} else {// success, return dataset name
						alert("Dataset has been cleaned up successfully. \n\n"
								+ value);
						location.reload();
					}
				}
			});
}