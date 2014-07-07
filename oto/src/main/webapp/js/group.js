/**
 * this file will no longer be userd any more
 */

/* All AJAX calls are in this file*/
/* Author : Partha Pratim Sanyal*/

var XHR;
var fileToShow;
var fileText;
var TimeToFade;
/* This variable is for the divId in the admin page thatshows the status */
var divId;
var adminFlag;

function unhide(object){
   	var x=document.getElementById(object);
    x.style.visibility='visible';
}
 function hide(object){
     var x=document.getElementById(object);
    x.style.visibility='hidden';
 }

 /* Admin approval or revocation */
 function updateStatus(userid, div, flag) {
	 
	 divId = div;
	 adminFlag = flag;
	 var request = userid + " " + flag;
	 XHR = createXHR();
	 if(XHR) {
	  		XHR.onreadystatechange = approveReject;
			XHR.open("POST", 'approveRevoke.do', true);
			XHR.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	        var x = 'value='+ request;
			XHR.send(x);
	 }
 }
 
 function approveReject() {
	 var newDiv = document.getElementById(divId);
	  if (XHR.readyState == 4) {
	        // only if "OK"
	        if (XHR.status == 200) {
	        	var response = XHR.responseXML.getElementsByTagName("response")[0].childNodes[0].nodeValue;
	        	if(response.charAt(0) != 'E') {
		        	if(adminFlag) {
		        		newDiv.innerHTML = '<a href="#" onclick="updateStatus('+response +',\'' + divId+'\', false)">Revoke</a>';
		        		
		        	} else {
		        		newDiv.innerHTML = '<a href="#" onclick="updateStatus('+response +',\'' + divId+'\', true)">Approve</a>';
		        	}
	        	} else {
	        		document.getElementById("serverMessage").innerHTML = response;
	        	}

	        }
	  } 
 }
 
 function setTermAndSource(obj) {
	document.getElementById("sourceFiles").innerHTML = obj.id;
	document.getElementById("glossaryTerm").innerHTML = obj.innerHTML;
	
	 var current = document.getElementsByClassName('currentContext')[0];
	 if (current.id == "context") {
		 getContext();
	 } else {
		 getGlossary();
	 }
 }
 
 function showContext() {
	 var context = document.getElementById("context");
	 if (context.className != "currentContext") {
		 context.className = "currentContext";
		 document.getElementById("glossary").className = "backContext";
		 
		 getContext();
	 }
 }
 
 function showGlossary() {
	 var glossary = document.getElementById("glossary");
	 if (glossary.className != "currentContext") {
		 glossary.className = "currentContext";
		 document.getElementById("context").className = "backContext";
		 
		 getGlossary();
	 }
 }
 
 function getGlossary() {
	 var term = document.getElementById("glossaryTerm").innerHTML;
	 if (term == "" || term == null) {
		 return;
	 }
	  XHR = createXHR();
	  if(XHR) {
		  XHR.onreadystatechange = processGlossary;
		  XHR.open("POST", 'glossary.do', true);
		  XHR.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	      var x = 'value=' + term;
	      XHR.send(x);
	  }
 }
 
 /* Process Context file Look Up START */
 function getContext() {
	 var sourceFiles = document.getElementById("sourceFiles").innerHTML;
  XHR = createXHR();
  if(XHR) {
  		XHR.onreadystatechange = processContext;
		XHR.open("POST", 'context.do', true);
		XHR.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        var x = 'value='+ sourceFiles;
		XHR.send(x);
  }
 }
 
 /*
	 * This Ajax call is made to check if the user entered a mail id that is
	 * already there in the system, during registration
	 */
 function checkEmail(email) {
	 XHR = createXHR();
	 if(XHR) {
	  		XHR.onreadystatechange = emailCheck;
			XHR.open("POST", 'checkEmail.do', true);
			XHR.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	        var x = 'value='+ stringTrim(email);
			XHR.send(x);
	 }
 }
 
 function emailCheck(){
	  if (XHR.readyState == 4) {
	        // only if "OK"
	        if (XHR.status == 200) {
	        var emailResponse = XHR.responseXML.getElementsByTagName("response")[0].childNodes[0].nodeValue;
		        if(emailResponse != 'Available') {
		        	document.getElementById("emailCheck").innerHTML 
		        			="<font class=\"font-text-style\">" + emailResponse + "</font>";
		        	document.getElementById("emailId").value="";
		        }
	         
	        } 	   
	   } 
 }
 
  function createXHR() {
	req = false;
    // branch for native XMLHttpRequest object
    if(window.XMLHttpRequest && !(window.ActiveXObject)) {
    	try {
			req = new XMLHttpRequest();
			// alert("XHR created!");
        } catch(e) {
			req = false;
        }
    // branch for IE/Windows ActiveX version
    } else if(window.ActiveXObject) {
       	try {
        	req = new ActiveXObject("Msxml2.XMLHTTP");
      	} catch(e) {
        	try {
          		req = new ActiveXObject("Microsoft.XMLHTTP");
        	} catch(e) {
          		req = false;
        	}
		}
    }
    return req;
} 
  
  function processGlossary() {
	  if (XHR.readyState == 4) {
	        // only if "OK"
	        if (XHR.status == 200) {
	        	var div = document.getElementById("contextSentences");
				var tab = document.getElementById("contextTable");
				
				document.getElementById("th_context_1").innerHTML = "Category (of " + document.getElementById("glossaryTerm").innerHTML + ")";
				document.getElementById("th_context_2").innerHTML = "Definition (of " + document.getElementById("glossaryTerm").innerHTML + ")";
				
				div.removeChild(tab);
				var tab1 = document.createElement("table");
				tab1.setAttribute("width", "100%");
				tab1.setAttribute("id", "contextTable");
		        // process the response here!
		  		var response = XHR.responseXML;
		  		var glossaries = response.getElementsByTagName("glossary");
		  		 //alert(XHR.responseText);
	  		    var flag = true;
		  		for(var i = 0 ; i < glossaries.length; i++) {
		  			var tr = document.createElement("tr");
		  			var td = document.createElement("td");
		  			
		  			if(flag) {
		  				tr.setAttribute("class", "d0");
		  				flag = false;
		  			} else {
		  			   tr.setAttribute("class", "d1");
		  			   flag = true;
		  			}
		  			
		  			td.setAttribute("width", "15%");
		  			var category = glossaries[i].childNodes[0].childNodes[0].nodeValue;
		  			td.innerHTML = "<font class=\"font-text-style\">" + category + "</font>";
	  			
		  			var td1 = document.createElement("td");
		  			td1.innerHTML = "<font class=\"font-text-style\">" + glossaries[i].childNodes[1].childNodes[0].nodeValue + "</font>";
		  			
		  			tr.appendChild(td);
		  			tr.appendChild(td1);
		  			tab1.appendChild(tr);	  		
		  		}
		  		div.appendChild(tab1);
	        } else {
		        document.getElementById("serverMessage").innerHTML='<label>The server encountered an internal error while processing your request. '+
		        'The response returned by the server is: ' +req.statusText +"</label>";
	        }
	   
	   } 
	}

 function processContext() {
   
  if (XHR.readyState == 4) {
        // only if "OK"
        if (XHR.status == 200) {
			var div = document.getElementById("contextSentences");
			var tab = document.getElementById("contextTable");
			
			document.getElementById("th_context_1").innerHTML = "Source (of " + document.getElementById("glossaryTerm").innerHTML + ")";
			document.getElementById("th_context_2").innerHTML = "Sentence";
			
			div.removeChild(tab);
			var tab1 = document.createElement("table");
			tab1.setAttribute("width", "100%");
			tab1.setAttribute("id", "contextTable");
	        // process the response here!
	  		var response = XHR.responseXML;
	  		var contexts = response.getElementsByTagName("context");
	  		// alert(XHR.responseText);
  		    var flag = true;
	  		for(var i = 0 ; i < contexts.length; i++) {
	  			var tr = document.createElement("tr");
	  			var td = document.createElement("td");
	  			
	  			if(flag) {
	  				tr.setAttribute("class", "d0");
	  				flag = false;
	  			} else {
	  			   tr.setAttribute("class", "d1");
	  			   flag = true;
	  			}
	  			
	  			td.setAttribute("width", "15%");
	  			var sourceFileName = contexts[i].childNodes[0].childNodes[0].nodeValue;
	  			td.innerHTML = "<font class=\"font-text-style\"><a href=\"#\" onClick=\"showFile('"+sourceFileName+"')\">" 
	  			+ sourceFileName + "</a></font>";
  			
	  			var td1 = document.createElement("td");
	  			td1.innerHTML = "<font class=\"font-text-style\">" + contexts[i].childNodes[1].childNodes[0].nodeValue + "</font>";
	  			
	  			tr.appendChild(td);
	  			tr.appendChild(td1);
	  			tab1.appendChild(tr);	  		
	  		}
	  		div.appendChild(tab1);
        } else {
	        document.getElementById("serverMessage").innerHTML='<label>The server encountered an internal error while processing your request. '+
	        'The response returned by the server is: ' +req.statusText +"</label>";
        }
   
   } 
}

function showFile(fileName) {
  XHR = createXHR();
  fileToShow = fileName;
  if(XHR) {
  		XHR.onreadystatechange = showFileText;
		XHR.open("POST", 'file.do', true);
		XHR.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        var x = 'value='+ fileName;
		XHR.send(x);
  }
}

function showFileText(){
	  if (XHR.readyState == 4) {
	        // only if "OK"
	        if (XHR.status == 200) {
	        fileText = XHR.responseXML.getElementsByTagName("text")[0].childNodes[0].nodeValue;
	        // window.open('jsp/popup.jsp?f='+fileToShow.substring(0,
			// fileToShow.indexOf('.')),
	        // fileToShow,'height=500,width=700, toolbar=no, location=no,
			// menubar=no,resizable=yes,scrollbars=yes, statusbar=no');
	        grayOut(true, '', fileText, fileToShow);
	           
	        } else {
		        document.getElementById("serverMessage").innerHTML='<label>The server encountered an internal error while processing your request. '+
		        'The response returned by the server is: ' +req.statusText +"</label>";
	        }
	   
	   } 
	}
/* Process Context file Look Up END */

function checkDataset(object) {
	var dataset = object.value;
	if(dataset == 'select') {
		unhide("submitbutton");
		document.getElementById("serverMessage").innerHTML = '&nbsp;';
		return;
	}
	XHR = createXHR();
	if(XHR) {
  		XHR.onreadystatechange = processDataSetCheckResponse;
		XHR.open("POST", 'checkDataSet.do', true);
		XHR.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		var x = 'value=' + dataset;
		XHR.send(x);
	}	
}

function processDataSetCheckResponse() {
	  if (XHR.readyState == 4) {
	        // only if "OK"
	        if (XHR.status == 200) {
	        	var response = XHR.responseXML.getElementsByTagName("response")[0].childNodes[0].nodeValue;
	        	if (response != 'present') {
	        		document.getElementById("serverMessage").innerHTML = response;
	        		hide("submitbutton");
	        	} else {
	        		document.getElementById("serverMessage").innerHTML = '&nbsp;';
	        		unhide("submitbutton");
	        	}
	        } 
	  }
	        	
}


function showNew(object){

	 var newDecision = document.getElementById('newDecision');
	 if (object.value == "New")
	 {
	   newDecision.value="";
	   unhide('newDecision');
	 }
	 
	 if((object.value != "New") && (object.value != "Select"))
	  {
	   	hide('newDecision');
	   	newDecision.value="";
	  }
	  if(object.value == "Select")
	  {
	   hide('newDecision');
	   newDecision.value="";
	  }
}

function stringTrim(str)
{
    if(str.length > 0)
        while(str.indexOf(' ') == 0)
            str = str.substr(1);
    if (str.length > 0)
        while(str.lastIndexOf(' ') == str.length-1)
            str = str.substr(0, str.length-1);
    return str;
}

// to be replaced --by f.huang
function checkDecision()  {
	  var decision = document.getElementById("decisions").value;
	  if (decision == 'Select') {
	    return false;
	  } else {
	    if (decision == 'New'){
	     var newDecision = stringTrim(document.getElementById('newDecision').value);
	     if (newDecision == '') {
	      return false;
	     } else {
	      return true;
	     }
	   } else {
	    return true;
	   }
	 } 
}




/* Group Save functionality --- to be replaced--by f.huang */
function save(flag){
	 if (!checkDecision()) {
	  document.getElementById("alert").innerHTML = 'Please choose a decision for this group';
	  return;
	 }
	 /* Make the saving image visible */
	 document.getElementById("saveImage").style.visibility='visible';
	 
	 var decision;
	 if(document.getElementById("decisions").value != 'New') {
	  decision = document.getElementById("decisions").value;
	 } else {
	  decision = stringTrim(document.getElementById('newDecision').value);
	 }
	 var termRows = document.getElementById("count").value;
	 var request = '<?xml version="1.0" encoding="UTF-8"?>';
	 request += '<group><groupname>'+ document.getElementById("value").value+'</groupname>';
	 // add the decision
	  request += '<decision>'+ decision +'</decision><rows>';
	  for(i = 1; i <= termRows; i++) {
	   var termDiv = document.getElementById("term"+i).style.opacity;
	   var termFlag = 'Y';
	   if (termDiv == '1') {
	    termFlag = 'Y';
	   } else {
	    termFlag = 'N';
	   }
	   
	   var term = document.getElementById("termText"+i).value;   
	   var coTermDiv = document.getElementById("coTerm"+i).style.opacity;
	   var coTermFlag = 'Y';
	   
	   if (coTermDiv == '1') {
	    coTermFlag = 'Y';
	   } else {
	    coTermFlag = 'N';
	   }
	   var coTerm = document.getElementById("coTermText"+i).value;
	   request += "<row><term><name>"+term+"</name><remove>"+termFlag+"</remove></term>";
	   request += "<coTerm><name>"+coTerm+"</name><remove>"+coTermFlag+"</remove></coTerm>";
	   var sourceFiles = document.getElementById("context"+i).value;
	   var frequency = document.getElementById("frequency"+i).innerHTML;
	   request += "<sourceFiles>"+sourceFiles+"</sourceFiles>";
	   request +="<frequency>"+frequency+"</frequency></row>";
	 }
	  
	 request += '</rows></group>';
	 if(flag == 'session') {
		   XHR = createXHR();
			  if(XHR) {
			  		XHR.onreadystatechange = saveGroup;
					XHR.open("POST", 'saveGroupSession.do', true);
					XHR.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			        var x = 'value='+ request;
					XHR.send(x);
			  }
	 } else {
		 document.getElementById('hiddenvalue').value = request;
		 document.getElementById('submitForm').submit();
	 }

	}

	function saveGroup(){
		  if (XHR.readyState == 4) {
	        // only if "OK"
	        if (XHR.status == 200) {
	        	var response = XHR.responseXML;
	        /* Make the image invisible */
	 		document.getElementById("saveImage").style.visibility='hidden';
	 		document.getElementById("serverMessage").innerHTML
	 			= response.getElementsByTagName("message")[0].childNodes[0].nodeValue;
	 		
	 		/*
			 * Create the Processed Groups table here - This is no longer
			 * required!!
			 */
	 		/*
			 * if(response.getElementsByTagName("processed") != null ||
			 * response.getElementsByTagName("processed") != '') { var div =
			 * document.getElementById("processedGroups"); var tab =
			 * document.getElementById("processedTable"); div.removeChild(tab);
			 * var tab1 = document.createElement("table");
			 * tab1.setAttribute("width", "100%"); tab1.setAttribute("id",
			 * "processedTable");
			 * 
			 * var processed =
			 * response.getElementsByTagName("processed")[0].childNodes[0].nodeValue;
			 * var processedGroups = processed.split(' '); var flag = true;
			 * for(var i = 0 ; i < processedGroups.length; i++) { var tr =
			 * document.createElement("tr"); if(flag) { tr.setAttribute("class",
			 * "d0"); flag = false; } else { tr.setAttribute("class", "d1");
			 * flag = true; }
			 * 
			 * var td = document.createElement("td"); td.setAttribute("align",
			 * "center"); var processedGroupName = processedGroups[i];
			 * td.innerHTML = processedGroupName;
			 * 
			 * tr.appendChild(td); tab1.appendChild(tr); }
			 * div.appendChild(tab1); }
			 */
			
	 		
	        } else {
	        document.getElementById("serverMessage").innerHTML='<label>The server encountered an internal error while processing your request. '+
	        'The response returned by the server is: ' +req.statusText +"</label>";
	                /* Make the image invisible */
	 				document.getElementById("saveImage").style.visibility='hidden';
	        }
	     }
	}