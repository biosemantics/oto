/* All AJAX calls are in this file*/
/* Author : Partha Pratim Sanyal*/

/*comment by Fengqiong: This file will be used to get context and glossary with AJAX. 
 * Shared by categorizing, hierarchy tree and terms order pages. */
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
	        		document.getElementById("serverMessage").className = "error";
	        		document.getElementById("serverMessage").innerHTML = response;
	        	}

	        }
	  } 
 }
 
 /**
  * this is for categorizing page only. Besides setTerm, it needs to set reviewedTerms
  * also, if current is not context or glossary 
  * @param term
  */
 function setTerm_categorizing(term) {
	 //console.log("click: show term context");
	 markTermReviewed(term);
	 setTerm(term);
 }
 
 function getOriginalTermName(name) {
		var termName = name.replace(/(^_+)|(_+$)|(_\d\d?$)/g, "");
		//alert("original term is : " + termName);
		return termName;
	}
 
 function setTerm(term) {
	 //console.log("show context of " + term);
	 document.getElementById("glossaryTerm").innerHTML = term;
	 var current = document.getElementsByClassName('currentContext')[0];
	 if (current.id == "context") {
		 getContext();
	 } else if (current.id == "glossary"){
		 getGlossary();
	 } else if (current.id == "termLocations"){
		 showTermLocations();
	 }
 }
 
 function showContext() {
	 var context = document.getElementById("context");
	 if (context.className != "currentContext") {
		 context.className = "currentContext";
		 document.getElementById("glossary").className = "backContext";
		 if (document.getElementById("termLocations") != null) {
			 document.getElementById("termLocations").className = "backContext";	 
		 }
		 getContext();
	 }
 }
 
 function showGlossary() {
	 var glossary = document.getElementById("glossary");
	 if (glossary.className != "currentContext") {
		 glossary.className = "currentContext";
		 document.getElementById("context").className = "backContext";
		 if (document.getElementById("termLocations") != null) {
			 document.getElementById("termLocations").className = "backContext";	 
		 }
		 
		 getGlossary();
	 }
 }
 
 //create contextTable when switching tabs
 function getContextTable() {
	 var tab = document.getElementById("contextTable");
	 if (tab == null) {
		 var tab1 = document.createElement("table");
			tab1.setAttribute("width", "100%");
			tab1.setAttribute("id", "contextTable");
			var div = document.getElementById("contextSentences");
			if (div != null) {
				div.appendChild(tab1);
			} else {
				alert("null div");
			}
	 }
 }
 
 function showTermLocations() {
	 var term = document.getElementById("glossaryTerm").innerHTML;
	 if (term == "" || term == null) {
		 //alert("null");
		 return;
	 }	 
	document.getElementById("glossary").className = "backContext";
	document.getElementById("context").className = "backContext";
	document.getElementById("termLocations").className = "currentContext";
	var div = document.getElementById("contextSentences");
	var tab = document.getElementById("contextTable");
	if (tab != null) {
	 	div.removeChild(tab);
	}
			
	document.getElementById("th_context_1").innerHTML = "Copy (of " + getOriginalTermName(term) + ")";
	document.getElementById("th_context_2").innerHTML = "Current Location";
				 
	var tab1 = document.createElement("table");
	tab1.setAttribute("width", "100%");
	tab1.setAttribute("id", "contextTable");
	
	var flag = true;
	
	//for each term == termName or termName_?, list it 
	var j = 0;
	var termName = term.replace(/(_\d\d?$)/g, "");
	var nameOfCopy = termName;
	//get locations of copies of term
	for (j = 0; j < 100; j++) {
		if (j != 0) {
			nameOfCopy = termName + "_" + j;
		}
		ex = document.getElementById(nameOfCopy);
		if (ex == null) {
			break;			
		} else {
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
  			td.innerHTML = "<font class=\"font-text-style\">" + nameOfCopy + "</font>";
			var td1 = document.createElement("td");
  			var category = "uncategorized";
  			
  			var temp = ex.parentNode;
  			while (temp.className != "categoryTable" && temp.id != "availableTerms") {
  				temp = temp.parentNode;
  			}
  			if (temp.className == "categoryTable") {
  				category = temp.id;
  			}
  			
  			td1.innerHTML = "<font class=\"font-text-style\">" + category + "</font>";
  			tr.appendChild(td);
  			tr.appendChild(td1);
  			tab1.appendChild(tr);	 
		}
	}	
	div.appendChild(tab1);
}
 
 function getGlossary() {
	 var term = document.getElementById("glossaryTerm").innerHTML;
	 if (term == "" || term == null) {
		 return;
	 }
	 term = getOriginalTermName(term);
	 
	 document.getElementById("th_context_1").innerHTML = "";
	 document.getElementById("th_context_2").innerHTML = "";
	 var tab = document.getElementById("contextTable");
	 tab.innerHTML = "<font class='font-text-style'>&nbsp;&nbsp;&nbsp;&nbsp;Looking up glossaries for '" + term + "'...</font>";
	 
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
	 var term = document.getElementById("glossaryTerm").innerHTML;
	 
	 term = getOriginalTermName(term);
	 if (term == "" || term == null) {
		 //alert("null");
		 return;
	 }
	 
	 document.getElementById("th_context_1").innerHTML = "";
	 document.getElementById("th_context_2").innerHTML = "";
	 var tab = document.getElementById("contextTable");
	 //alert(tab);
	 tab.innerHTML = "<font class='font-text-style'>&nbsp;&nbsp;&nbsp;&nbsp;Looking up context for '" + term + "'...</font>";
	 
	 
	 XHR = createXHR();
	 if(XHR) {
  		XHR.onreadystatechange = processContext;
		XHR.open("POST", 'context.do', true);
		XHR.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        var x = 'value='+ term;
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
	        	var termName = document.getElementById("glossaryTerm").innerHTML;
	        	termName = getOriginalTermName(termName);
	        	document.getElementById("th_context_1").innerHTML = "Category (of " + termName + ")";
				document.getElementById("th_context_2").innerHTML = "Definition (of " + termName + ")";
				
				var div = document.getElementById("contextSentences");
				var tab = document.getElementById("contextTable");
				if (tab != null) {
					div.removeChild(tab);
				}
				
				var tab1 = document.createElement("table");
				tab1.setAttribute("width", "100%");
				tab1.setAttribute("id", "contextTable");
				div.appendChild(tab1);
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
		  			var def = glossaries[i].childNodes[1].childNodes[0].nodeValue;
		  			def = (def == "null" ? "" : def);
		  			td1.innerHTML = "<font class=\"font-text-style\">" + def + "</font>";
		  			
		  			tr.appendChild(td);
		  			tr.appendChild(td1);
		  			tab1.appendChild(tr);	  		
		  		}
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
				if (tab != null) {
				 	div.removeChild(tab);
				 }
				
				var termName = document.getElementById("glossaryTerm").innerHTML;
	        	termName = getOriginalTermName(termName);
				document.getElementById("th_context_1").innerHTML = "Source (of " + termName + ")";
				document.getElementById("th_context_2").innerHTML = "Detail Sentence";
				
				var tab1 = document.createElement("table");
				tab1.setAttribute("width", "100%");
				tab1.setAttribute("id", "contextTable");
				div.appendChild(tab1);
		        // process the response here!
				//alert(XHR.responseText);
		  		var response = XHR.responseXML;
		  		var contexts = response.getElementsByTagName("context");
		  		 //alert(XHR.responseText);
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
		  			var normalizedTermName = termName.split('_').join('-');
					td1Content = "";				
					/*td1Content += "<font class=\"font-text-style\">"
						+ contexts[i].childNodes[1].childNodes[0].nodeValue
						+ "</font>";*/
					var splitRegex = new RegExp(normalizedTermName, "i"); // -> /normalizedTermName/i
					var splits = contexts[i].childNodes[1].childNodes[0].nodeValue.split(splitRegex);
					//console.log(splitRegex);
					for(var j=0; j<splits.length; j++) {
						//console.log(splits[j]);
						td1Content += "<font class=\"font-text-style\">"
							+ splits[j]
							+ "</font>";
						if(j < splits.length - 1) {
							td1Content += "<font class=\"font-text-style\" color=\"red\">"
								+ normalizedTermName
								+ "</font>";
						}
					}
					td1.innerHTML = td1Content;
		  			
		  			tr.appendChild(td);
		  			tr.appendChild(td1);
		  			tab1.appendChild(tr);	  		
		  		}
		  		
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
	        	//alert(XHR.responseText + "test");
	        	fileText = XHR.responseText;
	        	if (fileText != "") {
	        		alert(fileText);
	        	}
	        //fileText = XHR.responseXML.getElementsByTagName("text")[0].childNodes[0].nodeValue;
	         //window.open('jsp/openFile.jsp?f='+fileToShow.substring(0,
			 //fileToShow.indexOf('.')),
	         //fileToShow,'height=500,width=700, toolbar=no, location=no,
			 //menubar=no,resizable=yes,scrollbars=yes, statusbar=no');
	        //grayOut(true, '', fileText, fileToShow);
	           
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
