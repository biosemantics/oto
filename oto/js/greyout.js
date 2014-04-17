function grayOut(vis, options, text, fileName) {
  // Pass true to gray out screen, false to ungray
  // options are optional.  This is a JSON object with the following (optional) properties
  // opacity:0-100         // Lower number = less grayout higher = more of a blackout 
  // zindex: #             // HTML elements with a higher zindex appear on top of the gray out
  // bgcolor: (#xxxxxx)    // Standard RGB Hex color code
  // grayOut(true, {'zindex':'50', 'bgcolor':'#0000FF', 'opacity':'70'});
  // Because options is JSON opacity/zindex/bgcolor are all optional and can appear
  // in any order.  Pass only the properties you need to set.
  var options = options || {}; 
  var zindex = options.zindex || 50;
  var opacity = options.opacity || 70;
  var opaque = (opacity / 100);
  var bgcolor = options.bgcolor || '#D6F8CD';
  var dark = document.getElementById('darkenScreenObject');
  if (!dark) {
    // The dark layer doesn't exist, it's never been created.  So we'll
    // create it here and apply some basic styles.
    // If you are getting errors in IE see: http://support.microsoft.com/default.aspx/kb/927917
    var tbody = document.getElementsByTagName("body")[0];
    var tnode = document.createElement('div');           // Create the layer.
        tnode.style.position='absolute';                 // Position absolutely
        tnode.style.top='0px';                           // In the top
        tnode.style.left='0px';                          // Left corner of the page
        tnode.style.overflow='hidden';                   // Try to avoid making scroll bars            
        tnode.style.display='none';                      // Start out Hidden
        tnode.id='darkenScreenObject';
        tnode.width ='100%';
        tnode.height = '100%' ;// Name it so we can find it later
        
    	var msgnode = document.createElement('div');           // Create the box layer.
		msgnode.style.position='fixed';                 // Position fixed
        msgnode.style.display='none';                      // Start out Hidden
        msgnode.id='box';                   				// Name it so we can find it later
		// give it a size and align it to center
		msgnode.style.width = "500px";
		msgnode.style.height = "300px";
		msgnode.style.marginLeft= "-200px";      
		msgnode.style.marginTop= "-200px"; 
		//msgnode.style.textAlign = 'center';
		msgnode.style.top= "50%";                           // In the top	
		msgnode.style.left="50%";                          // Left corner of the page	
	tbody.appendChild(msgnode);
        
    tbody.appendChild(tnode);                            // Add it to the web page
    dark = document.getElementById('darkenScreenObject');  // Get the object.
  }
  if (vis) {
    // Calculate the page width and height 
    if( document.body && ( document.body.scrollWidth || document.body.scrollHeight ) ) {
        var pageWidth = document.body.scrollWidth+'px';
        var pageHeight = document.body.scrollHeight+'px';
    } else if( document.body.offsetWidth ) {
      var pageWidth = document.body.offsetWidth+'px';
      var pageHeight = document.body.offsetHeight+'px';
    } else {
       var pageWidth='100%';
       var pageHeight='100%';
    }   
    //set the shader to cover the entire page and make it visible.
    dark.style.opacity=opaque;                      
    dark.style.MozOpacity=opaque;                   
    dark.style.filter='alpha(opacity='+opacity+')'; 
    dark.style.zIndex=zindex;        
    dark.style.backgroundColor=bgcolor;  
    dark.style.width= pageWidth;
    dark.style.height= pageHeight;
    dark.style.display='block'; 
	document.body.style.overflow =  'hidden';

	document.getElementById("box").style.zIndex = zindex+10;
	document.getElementById("box").style.border = "#000 solid 1px";
	document.getElementById("box").style.display = "block";
	//fade('darkenScreenObject', 3500);

	document.getElementById("box").onclick = function() //attach a event handler to hide both div
	{
		dark.style.display="none";
		document.getElementById("box").style.display = "none";
		document.body.style.overflow = 'auto';

	}
	document.getElementById("box").style.backgroundColor="#FFF";
	document.getElementById("box").style.overflow = 'auto';
	var existingTable = document.getElementById("fileTextTable");
	if(existingTable) {
	 document.getElementById("box").removeChild(existingTable);
	}
	
	var tab1 = document.createElement("table");
	tab1.setAttribute("id", "fileTextTable");
	tab1.setAttribute("width", "100%");
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	td.setAttribute("width", "95%");
	td.innerHTML = "<label><b>"+ fileName+ "</b></label>";
	var td1 = document.createElement("td");
	var label = document.createElement("label");
	label.setAttribute("class", "deleteAction stat_elem UIImageBlock_Ext uiCloseButton uiCloseButton uiCloseButton");
	td1.appendChild(label);	
	tr.appendChild(td);
	tr.appendChild(td1);
	var tr1 = document.createElement("tr");
	var td3 = document.createElement("td");
	td3.setAttribute("border", "1");
	td3.innerHTML = "<font color=\"green\" class=\"font-text-style\">"
		+ text+ "</font>";
	var td4 = document.createElement("td");
	td4.innerHTML = "&nbsp;";
	tr1.appendChild(td3);
	tr1.appendChild(td4);	
	tab1.appendChild(tr);
	tab1.appendChild(tr1);
	
	document.getElementById("box").appendChild(tab1);
  } else {
     dark.style.display='none';
  }
}
