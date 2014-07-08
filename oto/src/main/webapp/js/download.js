var downloadURL = "";

function getDownloadableFiles() {
	// clear download information for last dataset
	$("#SQL_ZIP_TITLE").hide();
	$("#CSV_CURRENT_TITLE").hide();
	$("#CSV_ARCHIVED_TITLE").hide();
	$("#SQL_ZIP_LINKS").html("");
	$("#CSV_CURRENT_LINKS").html("");
	$("#CSV_ARCHIVED_LINKS").html("");

	// update msg
	var msglabel = $("#serverMsg");
	if (msglabel != null) {
		msglabel.html("Waiting for the server to respond.");
	}
	var dataset = document.getElementById("dataset").value;
	if (dataset == "") {
		msglabel.html("Select a dataset first. ");
		return;
	}
	var x = "value=" + dataset;
	XHR = createXHR();
	if (XHR) {
		XHR.onreadystatechange = displayDownloadableFiles;
		XHR.open("POST", 'download.do', true);
		XHR.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		XHR.send(x);
	}
}

function displayDownloadableFiles() {
	var i, j;
	if (XHR.readyState == 4) {
		var msglabel = $("#serverMsg");
		var hasDownload = false;
		if (XHR.status == 200) {
			// alert(XHR.responseText);
			var response = XHR.responseXML;

			// get url
			var url = response.getElementsByTagName("url")[0].childNodes[0].nodeValue;
			url = url.replace(/SLASH/g, '/');

			if (response.getElementsByTagName("msg").length > 0) {
				var msg = response.getElementsByTagName("msg")[0].childNodes[0].nodeValue;
				if (msg == "error") {
					msglabel.toggleClass("error");
					msglabel
							.html("No downloadable files were found for this dataset. Please try again later. ");
					return;
				}
			}

			// get merged into information
			if (response.getElementsByTagName("mergedInto").length > 0) {
				var mergedInto = response.getElementsByTagName("mergedInto")[0].childNodes[0].nodeValue;
				// update server msg
				msglabel.html("This dataset has been merged into '"
						+ mergedInto
						+ "'. Both datasets are available for download. ");
			} else {
				msglabel.html("");
				msglabel.toggleClass("info");
			}

			// get zipped file
			if (response.getElementsByTagName("zipfile").length > 0) {
				var zipFileName = response.getElementsByTagName("zipfile")[0].childNodes[0].nodeValue;
				$("#SQL_ZIP_TITLE").show();
				$("#SQL_ZIP_LINKS").html(
						"<li><a href='" + url + zipFileName + "'>"
								+ zipFileName + "</a></li>");
				hasDownload = true;
			}

			// get latest versin files
			if (response.getElementsByTagName("latestVersion").length > 0) {
				$("#CSV_CURRENT_TITLE").show();
				var files = response.getElementsByTagName("latestVersion")[0].childNodes;
				var links = "";
				for (i = 0; i < files.length; i++) {
					var file = files[i].childNodes[0].nodeValue;
					links += "<a href='" + url + file + "'>" + file
							+ "</a>&nbsp;&nbsp;"
				}
				$("#CSV_CURRENT_LINKS").html(
						$("#CSV_CURRENT_LINKS").html() + "<li>" + links
								+ "</li>");
				hasDownload = true;
			}

			// get archived versions
			var oldVersions = response.getElementsByTagName("oldVersion");
			if (oldVersions.length > 0) {
				$("#CSV_ARCHIVED_TITLE").show();
				for (j = 0; j < oldVersions.length; j++) {
					oldVersion = oldVersions[j];
					var files = oldVersion.childNodes;
					var links = "";
					for (i = 0; i < files.length; i++) {
						var file = files[i].childNodes[0].nodeValue;
						links += "<a href='" + url + file + "'>" + file
								+ "</a>&nbsp;&nbsp;"
					}
					$("#CSV_ARCHIVED_LINKS").html(
							$("#CSV_ARCHIVED_LINKS").html() + "<li>" + links
									+ "</li>");

				}
				hasDownload = true;
			}

			if (!hasDownload) {
				msglabel.html("There are no downloads available for the selected dataset at this time.");
			}
		} else {
			if (msglabel != null) {
				document.getElementById("serverMsg").innerHTML = '<label>The server encountered an internal error while processing your request. '
						+ 'The response returned by the server is: '
						+ req.statusText + "</label>";
			}
		}
	}
}

function download() {
	var dataset = document.getElementById("dataset").value;
	startDownload(dataset);
}

function startDownload(dataset) {
	var msglabel = document.getElementById("serverMsg");
	if (msglabel != null) {
		msglabel.innerHTML = "Waiting for the server to respond.";
	}
	var x = "value=" + dataset;
	XHR = createXHR();
	if (XHR) {
		XHR.onreadystatechange = onDownload;
		XHR.open("POST", 'download.do', true);
		XHR.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		// alert(x);
		XHR.send(x);
	}
}

function onDownload() {
	if (XHR.readyState == 4) {
		var msglabel = document.getElementById("serverMsg");
		if (XHR.status == 200) {
			// alert(XHR.responseText);
			var response = XHR.responseXML;
			var msg = response.getElementsByTagName("msg")[0].childNodes[0].nodeValue;

			if (msg != "error") {
				if (msglabel != null) {
					msglabel.toggleClass("error");
					msglabel.innerHTML = msg;
				}

				var mergedInto = response.getElementsByTagName("mergedInto");
				if (mergedInto.length > 0) {
					var mergedDataset = mergedInto[0].childNodes[0].nodeValue;
					var confirmed = confirm("The dataset you selected has been merged into '"
							+ mergedDataset
							+ "'. Both datasets are available for download. \n\n"
							+ "Are you sure you want to continue downloading this dataset? ");
					if (!confirmed) {
						msglabel.innerHTML = "";
						msglabel.toggleClass("info");
						return;
					}
				}

				var url = response.getElementsByTagName("url")[0].childNodes[0].nodeValue;
				url = url.replace(/SLASH/g, '/');
				window.open(url);
				if (msglabel != null) {
					msglabel.innerHTML = "";
				}
			} else {
				msglabel.innerHTML = "The requested file was not found. Please try again later. ";
			}
		} else {
			if (msglabel != null) {
				document.getElementById("serverMsg").innerHTML = '<label>The server encountered an internal error while processing your request. '
						+ 'The response returned by the server is: '
						+ req.statusText + "</label>";
			}
		}
	}
}