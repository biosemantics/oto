<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="edu.arizona.sirls.util.Utilities"%>
<%@ page import="edu.arizona.sirls.beans.TermsGroup"%>
<%@ page import="edu.arizona.sirls.beans.CharacterGroupBean"%>
<%@ page import="edu.arizona.sirls.beans.SessionDataManager"%>
<%@ page import="edu.arizona.sirls.beans.Term"%>
<%@ page import="edu.arizona.sirls.beans.User"%>
<%@ page import="edu.arizona.sirls.db.CharacterDBAccess"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/groupStyles.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
<script language="javascript" src="js/greyout.js"></script>
<script language="javascript" src="js/context.js"></script>
<script language="javascript" src="js/fader.js"></script>
<script language="javascript" src="js/categorize.js"></script>
<script language="javascript">
	function submitGroupForm() {
		var formName = document.getElementById('generalForm');
		formName.submit();
	}

	function showTermsReport(term) {
		window.open(
						'comment.do?term=' + term,
						fileToShow,
						'height=300,width=700, directories=no, toolbar=no, location=no, menubar=no,resizable=yes,scrollbars=yes, statusbar=no, left=0,top=0');
	}
	
	function newCategory() {
		var name = prompt("Please input category name: ", "");		
		if (name) {
			var newCategory = document.getElementById('newCategory');
			newCategory.parentNode.id = name;
			document.getElementById('newCategoryName').innerHTML = name;
			
			newCategory.style["visibility"] = "visible";
			var btn = document.getElementById('newCategoryBtn');
			btn.parentNode.removeChild(btn);
		}
	}
</script>
</head>

<body onload="drag_init()">
<div id="dragging_mask"></div>
<div id="glossaryTerm"></div>
<div id="sourceFiles"></div>

<!-- Session Validity check -->

<%
	SessionDataManager sessionDataManager = (SessionDataManager) (session
	.getAttribute("sessionDataMgr"));

	if (sessionDataManager != null) {
		User user = sessionDataManager.getUser();
		String dataset = sessionDataManager.getDataset();
		String groupName = (String) request.getAttribute("groupName");
		ArrayList<String> groupNames = sessionDataManager
		.getGroupNames(); //groupNames is all the groups, name as 'Group_' + id
		ArrayList<String> decisions = sessionDataManager.getDecisions(); //decisions is all the categories to list on the right

		//get all group and put them into groups -- by f.huang
		CharacterDBAccess cdba = new CharacterDBAccess();
		HashMap<String, CharacterGroupBean> group = sessionDataManager.getGroups();
		for (int i = groupNames.size() - 1; i >= 0; i--) {
	String eachGroup = groupNames.get(i);
	group.remove(eachGroup);
	CharacterGroupBean cgroupbean = cdba.getAvailabelTermsForGroup(user, eachGroup,
				sessionDataManager.getDataset());
	if (cgroupbean.getCooccurrences() != null) {
		if (cgroupbean.getCooccurrences().size() < 1) {
			groupNames.remove(eachGroup);
		} else {
			group.put(eachGroup, cgroupbean);	
			groupName = eachGroup;
		}
	} else {
		groupNames.remove(eachGroup);
	}					
		}
		sessionDataManager.setGroups(group);//end of getting groups

		HashMap<String, CharacterGroupBean> groups = sessionDataManager
		.getGroups();
		CharacterGroupBean cbean = groups.get(groupName);

		// get all entrophyScores, not just the selected group
		HashMap<String, Double> entropyScores = cbean
		.getEntropyScores();

		String savedDecision = cbean.getDecision();

//		ArrayList<TermsGroup> groupTerms = cbean.getCooccurrences();

		

		//we don't need savedGroups rather than savedTerms
		//hashmap<decision, termslist>
//		ArrayList<String> savedGroups = new Utilities()
//			.getProcessedGroups(user, dataset);

		ArrayList<String> savedDecisions = new Utilities()
		.getProceccedCategories(user, dataset);

		//get all decided terms and put them into sessionDataManager --by f.huang
		HashMap<String, ArrayList<Term>> decisionTerms = new HashMap<String, ArrayList<Term>>();
		for (String eachDecision : savedDecisions) {
	ArrayList<Term> termsOfDecision = cdba.getTermsDecidedInCategory(
			eachDecision, dataset, user);
	decisionTerms.put(eachDecision, termsOfDecision);
		}
		//end of getting decided terms

//		sessionDataManager.setDecisionTerms(decisionTerms);
	
//		HashMap<String, ArrayList<Term>> processedDecisionTerms = sessionDataManager
//				.getDecisionTerms();
		
		int count = 1;
		boolean flag = true;
		String tdClass = "";
%>
<jsp:include page="header.jsp" />
<table border="1" width="100%">
	<tr>
		<td><font class="font-text-style"> A software application
		for automated semantic annotation of taxonomic, especially
		morphological, descriptions is reported in this paper. The tool is
		based on unsupervised machine learning methods. It is designed to
		annotate descriptions in a deviated syntax that is not normal English
		but often used in morphological descriptions. <br></br>
		</font></td>
	</tr>

	<tr class="dragging_part">
		<td><!-- below is the biggest dragging part table -->
		<table>
			<tr bgcolor="green">
				<th width="2.5%"></th>
				<th align="left" width="10%"><font color="white">Terms:</font></th>
				<td width="2.5%"></td>
				<th width="85%"><font color="white">Categories:</font></th>
			</tr>
			<tr>
				<td></td>
				<td>
				<div id="availableTerms">

				<%
					for (String grpName : groupNames) {
						boolean hasTerm = false;
							if (grpName != null) {
								CharacterGroupBean cgbean = groups.get(grpName);
								if (cgbean != null) {
									
									HashMap<String, Double> entrophy_scores = cgbean
									.getEntropyScores();

									ArrayList<TermsGroup> gTerms = cgbean
											.getCooccurrences();
									flag = true;
									tdClass = (flag) ? "d0" : "d1";
									flag = (flag) ? false : true;
				%>

				<div class="dragGroupTable" id="<%=grpName %>">
				<table>
					<tr>
						<td>
						<table class="termsTable">
							<%
								int i = 0;
								hasTerm = false;
								for (TermsGroup tGroup : gTerms) {
									if (tGroup.getTerm() != null) {
										hasTerm = true;
							%>
							<tr class="term_row" id="<%=tGroup.getTerm().getTerm()%>">
								<td class="term"><input type="checkbox"
									id="<%=tGroup.getFrequency()%>"/><label id="<%=tGroup.getSourceFiles()%>"
									 style="cursor: pointer; color: black" onclick="setTerm('<%=tGroup.getTerm().getTerm() %>')"><%=tGroup.getTerm().getTerm() %></label>
							<%
							 	if (entrophy_scores.get(tGroup.getTerm().getTerm()) != null) {
 							%> 
 							(<label><a href="javascript:void(0)" title="View term specific report for <%=tGroup.getTerm().getTerm()%>"
										onclick="showTermsReport('<%=tGroup.getTerm().getTerm()%>')"><%=entrophy_scores.get(tGroup.getTerm().getTerm())%></a></label>) <%
								}
							%></td>
							</tr>
							<%
								
													}
												}
											}

										}
							%>
						</table>
						</td>
						
						<%
							if (hasTerm == true) {
						%>
						<td><img class="dragme" src="images/drag.jpg" width="20px;"></img></td>
						<%		
							}
						%>
						
					</tr>
				</table>
				</div>
				<%
					}
				%>
				</div>
				</td>
				<td></td>
				<td><!-- below is the table of categories  -->
				<div id="categories_div" style="height: 300px; overflow: auto;">
				<table id="categories_table">
					<%
						int cellsinrow = 5;
							count = 1;
							for (String decision : decisions) {
								if (count % cellsinrow == 1)
									out.print("<tr class='category_row'>");
					%>
					<td class="categoryTable" id="<%=decision%>" style="vertical-align: top">
					<table style="border-collapse: collapse; border: 1px solid #B0F1A0;">
						<tr bgcolor="#B0F1A0">
							<th><%=decision%></th>
						</tr>
						<tr>
							<td><!-- decided terms -->
							<div class="categoryTerms">
							<table class="savedTerms">
								<%
									ArrayList<Term> terms = decisionTerms.get(decision);
											if (terms != null) {
								%>
								<!-- <tr>
									<td><img src="images/view.gif" width="15px" border="0px"></img><a href="javascript:void(0)" onclick="showSavedTerms('')">Saved Terms</a></td>
								</tr> -->
								<%
									for (Term et : terms) {
										if (!et.isAdditional()) {
											if (et.getRelatedTerms() != null && !et.getRelatedTerms().equals("") && !et.getRelatedTerms().equals(null) && !et.getRelatedTerms().equals("null")) {
								%>
								<tr class="term_row_saved">
									<td><table id=<%=et.getTerm() %>><tr><td><%=et.getTerm()%><label> ( <%=et.getRelatedTerms() %> )</label></td></tr></table></td>
								</tr>
								<%				
											} else {
								%>
								<tr class="term_row_saved">
									<td><table id=<%=et.getTerm() %>><tr><td><%=et.getTerm()%></td></tr></table></td>
								</tr>
								<%				
																
											}
										}
									}
									}
								%>
							</table>
							<!-- here suppose to list the terms to be saved -->
							<div class="termsToBeSaved"></div>
							</div>
							</td>
						</tr>
					</table>
					</td>
					<%
						if (count % cellsinrow == 0)
									out.print("</tr>");
								count++;
							}
							if (count % cellsinrow == 1)
								out.print("<tr>");
					%>
					<td class="categoryTable" align="center">
					<input type="button" value="New Category" onclick="newCategory()" id="newCategoryBtn"
						class="uiButton uiButtonSpecial uiButtonMedium"></input>
					<div id="newCategory">
					<table style="border-collapse: collapse; border: 1px solid #B0F1A0;">
						<tr bgcolor="#B0F1A0">
							<th id="newCategoryName" style="color: maroon;"></th>
						</tr>
						<tr>
							<td>
							<div class="categoryTerms">
							<div class="termsToBeSaved"></div>
							</div>
							</td>
						</tr>
					</table>
					</div>
					</td>
					<!-- <input type="button" value="New" onclick="mouse_down_handler"></input></th> -->
					<%
						if (count % cellsinrow == 0)
								out.print("</tr>");
					%>
				</table>
				</div>
				</td>
			</tr>
			<tr bgcolor="green">
				<th></th>
				<th></th>
				<th></th>
				<th>
				<table>
					<tr>
						<td width="400"></td>
						<td>
						<form id="submitForm" name="generalForm" action="saveGroup.do"
									method="post"><input type="button" name="button"
									value="Save Decisions" class="uiButton uiButtonSpecial uiButtonMedium"
									onclick="save_categories('submit')"
									onmouseover="document.getElementById('serverMessage').innerHTML='&nbsp;'" />
								<input type="hidden" id="hiddenvalue" name="value" />&nbsp;&nbsp;&nbsp;<img
								 src="images/green_rot.gif" id="processingSaveImage" style="visibility: hidden" width="15px;"/>
						</form></td>
						<td><div id="serverMessage" class="success">&nbsp; <%String message = (String) request.getAttribute("message");if (message != null) {%><%=message%><%}%></div></td>
					</tr>
				</table>
				</th>
			</tr>
		</table>
		</td>
	</tr>
	
	<tr>
	<td>
	<!-- the context part -->
	<div>
		<table width="100%" cellspacing="0px">
			<tr>
				<th width="15%" class="currentContext" id="context" onclick="showContext()"><font>Context</font></th>
				<th width="15%" class="backContext" id="glossary" onclick="showGlossary()"><font>Glossaries</font></th>
				<th width="70%" align="left" bgcolor="white"><font color="green">&nbsp;</font></th>
			</tr>
			<tr>
				<td width="85%" colspan="3" style="border: 2px solid green">

				<table width="100%">
					<tr>
						<th width="15%" align="left" id="th_context_1">Source</th>
						<th width="85%" align="left" id="th_context_2">Sentence</th>
					</tr>
				</table>
				<div style="width: 100%; height: 80px; overflow: auto;"
					class="border" id="contextSentences">
				<table width="100%" id="contextTable">
					<tr>
						<td width="15%">&nbsp;</td>
						<td width="85%">&nbsp;</td>
					</tr>

					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>

					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>

					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
				</table>
				</div>
				</td>
			</tr>
		</table>
		</div>
	</td>
	</tr>
</table>
<%
	} else {
%>

<jsp:include page="loginHeader.jsp" /><font class="font-text-style">
Your session has timed off. Please <a
	href="<%=request.getContextPath()%>">login</a></font>
<%
	}
%>
<jsp:include page="footer.jsp" />
</body>
<%@ page errorPage="error.jsp"%>
</html>