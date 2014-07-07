<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Set"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.DecisionBean"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Random"%>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
<title>OTO</title>


<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript"
	src="http://word-cumulus-goog-vis.googlecode.com/svn/trunk/wordcumulus.js"></script>
<script type="text/javascript"
	src="http://word-cumulus-goog-vis.googlecode.com/svn/trunk/swfobject.js"></script>
<script type="text/javascript"
	src="js/report.js"></script>

<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />

<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
</head>
<body>
<!-- Session Validity check -->
<%
	SessionDataManager sessionDataManager = (SessionDataManager) (session
			.getAttribute("sessionDataMgr"));

	if (sessionDataManager != null) {
		User user = sessionDataManager.getUser();
		HashMap<String, ArrayList<DecisionBean>> decisions = sessionDataManager
				.getUserDecisions();
		Set<String> keys = decisions.keySet();
		ArrayList<DecisionBean> wordCloudTerms = decisions
				.get(sessionDataManager.getDatasets().get(0));
		int i = 0;
		int[] indices = { 10, 20, 30, 40, 50, 60, 70, 80 };
		Random rand = new Random();
%>
<script type="text/javascript">
      google.load("visualization", "1");

      // Set callback to run when API is loaded
      google.setOnLoadCallback(drawVisualization);

      // Called when the Visualization API is loaded.
      function drawVisualization() {

        // Create and populate a data table.
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Tag');
        data.addColumn('number', 'value');
        data.addRows(<%=wordCloudTerms.size()%>);
        <%for (DecisionBean dbean : wordCloudTerms) {%>
        	data.setCell(<%=i%>, 0, '<%=dbean.getTerm().getTerm()%>');
        	data.setCell(<%=i%>, 1, <%=indices[rand.nextInt(7)]%>);
     	<%	
     		i++;
		  }
		%>
		// Instantiate our table object.
		var vis = new gviz_word_cumulus.WordCumulus(document
				.getElementById('mydiv'));

		// Draw our table with the data we created locally.
		vis.draw(data, {
			text_color : '#00ff00',
			speed : 100,
			width : 220,
			height : 220
		});
    }
   </script>

<!-- Session Validity check header End -->
<jsp:include page="header.jsp" />
<table border="1" width="100%">
	<tr>
		<td>
		<table>
			<tr>
				<td><img src="images/fern.jpg" / width="150" height="auto"></td>
				<td><font class="font-text-style"><b>This report
				gives you a history of the terms that have been grouped by you. It
				will also tell you if your grouping was accepted based on inputs of
				other experts in the field.</b><br></br>

				An instance of this class is used to generate a stream of
				pseudorandom numbers. The class uses a 48-bit seed, which is
				modified using a linear congruential formula. (See Donald Knuth, The
				Art of Computer Programming, Volume 2, Section 3.2.1.) If two
				instances of Random are created with the same seed, and the same
				sequence of method calls is made for each, they will generate and
				return identical sequences of numbers. In order to guarantee this
				property, particular algorithms are specified for the class Random.
				Java implementations must use all the algorithms shown here for the
				class Random, for the sake of absolute portability of Java code.
				However, subclasses of class Random are permitted to use other
				algorithms, so long as they adhere to the general contracts for all
				the methods. The algorithms implemented by class Random use a
				protected utility method that on each invocation can supply up to 32
				pseudorandomly generated bits. Many applications will find the
				random method in class Math simpler to use. <br></br>
				An instance of this class is used to generate a stream of
				pseudorandom numbers. The class uses a 48-bit seed, which is
				modified using a linear congruential formula. (See Donald Knuth, The
				Art of Computer Programming, Volume 2, Section 3.2.1.) </font></td>
				<td>

				<div id="mydiv"></div>
				</td>
			</tr>
		</table>
		<table width="100%">
			<tr>
				<td width="20%">
				<% 	ArrayList<String> datasets = sessionDataManager.getDatasets();
					for (String dataset: datasets) {
				%>
				<ul id=<%=dataset %>><%=dataset%>
					<li><a href="" onclick="getReport(this)" id="1"><font class="font-text-style">Group Terms</font></a></li>
					<li><a href="" onclick="getReport(this)"id="2"><font class="font-text-style">Structure Hierarchy</font></a></li>
					<li><a href="" onclick="getReport(this)" id="3"><font class="font-text-style">Term Order</font></a></li>
				</ul>
				<%		
					}
				%>
				</td>
				<td width="80%" id="reportContent">
				test
				</td>
			</tr>
		</table>

		<table width="100%">
			<tr>
				<td>
				<table width="100%">
					<tr bgcolor="green">
						<th align="left" width="10%"><font class="font-text-style"
							color="white">Dataset</font></th>
						<th align="left" width="5%"><font class="font-text-style"
							color="white">#</font></th>
						<th align="left" width="23%"><font class="font-text-style"
							color="white">Term</font></th>
						<th align="left" width="43%"><font class="font-text-style"
							color="white">Your Decision</font></th>
						<th align="left" width="13%"><font class="font-text-style"
							color="white">Date</font></th>
						<th align="left" width="8%"><font class="font-text-style"
							color="white">Acceptance Status</font></th>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td>
				<div style="width: 100%; height: 475px; overflow: auto;"
					class="border">
				<%
						int count = 1;
						boolean flag = true;
						String tdClass = "";
						SimpleDateFormat format = new SimpleDateFormat(
								"MMMM dd, yyyy");
				%>
				<table width="100%">
					<%
						for (String key : keys) {
								ArrayList<DecisionBean> userDecisions = decisions.get(key);
					%>
					<tr>
						<td><font class="font-text-style"><b><%=key%></b></font></td>
						<td></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>
						<%
							for (DecisionBean dbean : userDecisions) {
										tdClass = (flag) ? "d0" : "d1";
										flag = (flag) ? false : true;
						%>
						<table width="100%">
							<tr class="<%=tdClass%>">
								<td width="5%"><font class="font-text-style"><%=count++%></font></td>
								<td width="25%"><font class="font-text-style"><%=dbean.getTerm().getTerm()%></font></td>
								<td width="25%"><font class="font-text-style"><%=dbean.getDecision()%></font></td>
								<td width="25%"><font class="font-text-style"><%=format.format(dbean.getDecisionDate())%></font></td>
								<td width="15%">
								<%
									if (dbean.isAccepted()) {
								%> <img src="images/up.jpg" width="30" height="auto" /> <%
 	} else {
 %> <font class="font-text-style">Pending... </font> <%
 	}
 %>
								</td>
							</tr>
							<%
								}
							%>
						</table>
						</td>
					</tr>
					<%
						}
					%>
				</table>

				</div>


				</td>
			</tr>

		</table>
		</td>
	</tr>
</table>
<%
	} else {
%>
<br><br></br>
</br>
<jsp:include page="loginHeader.jsp" /><font class="font-text-style"><b>
Your session has timed off. Please <a
	href="<%=request.getContextPath()%>">login</a></b></font>
<%
	}
%>
<jsp:include page="footer.jsp" />
</body>
<%@ page errorPage="error.jsp"%>
</html>