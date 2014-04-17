/**
 * This files contains the AJAX of getting reports for the report page. 
 * @param obj
 * Author: Fengqiong
 */
//type: 1-categorizing; 2-hierarchy tree; 3-orders
function getReport(obj) {
	var type;
	var dataset;
	var value = "";
	if (obj.id == "USERS_LOG") {
		value = obj.id;
	} else {
		type = obj.id;
		dataset = obj.parentNode.id;
		var value = dataset + "::" + type;
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
				document.getElementById('reportContent').innerHTML = xmlhttp.responseText;
				//mark selected
				var nodes = document.getElementsByClassName('dtree');
				for (var i = 0; i < nodes.length; i++) {
					nodes[i].style.color = '';
				}
				document.getElementById('USERS_LOG').style.color = 'black';
				obj.style.color = 'purple';
			} else {
				document.getElementById('reportContent').innerHTML = "something wrong";
			}
		}
	}

	xmlhttp.open("POST", 'getUserReport.do', true);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded; charset=UTF-8")
	xmlhttp.send('value=' + value);
}