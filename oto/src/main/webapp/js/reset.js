function resetOTODemo(pageIndex) {
	if (!confirm("Resetting this page to its initial state will delete all the decisions that have been made by all users. \n\n"
			+ "Are you sure you want to proceed?")) {
		return;
	}

	window.onbeforeunload = null;

	$.ajax({
		url : "resetOTODemo.do",
		data : "value=" + pageIndex,
		success : function(msg) {
			if (msg.indexOf("error") >= 0) {
				alert("Failed to reset dataset. Please try again later. ");
			} else {// success
				location.reload();
			}
		}
	});
}