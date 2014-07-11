<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="edu.arizona.biosemantics.oto.lite.db.GeneralDBAccess"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="edu.arizona.biosemantics.oto.lite.db.CategorizationDBAccess"%>
<%@ page import="edu.arizona.biosemantics.oto.lite.beans.Upload"%>
<%@ page import="edu.arizona.biosemantics.oto.lite.beans.Group"%>
<%@ page import="edu.arizona.biosemantics.oto.lite.beans.SavedTerm"%>
<%@ page import="edu.arizona.biosemantics.oto.lite.beans.Category"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en"
	style="height:100%;">
<head>
<title>Term Categorization</title>
<!-- <title>OTO</title> -->
<link rel="stylesheet" media="screen" type="text/css"
	href="css/groupStyles.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />

<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
<script language="javascript" src="js/jquery-1.8.3.js"></script>
<script language="javascript" src="js/context.js"></script>
<script language="javascript" src="js/categorize.js"></script>
</head>

<body onload="drag_init()"
	style="height: 100%; width: 100%; margin: 0px;">
	<div id="dragging_mask"></div>
	<div id="glossaryTerm"></div>
	<div id="sourceFiles"></div>

	<!-- This is a sample copy of new category box, will be used when creating a new category -->
	<div id="new_category_hidden" style="width: 100%">
		<table
			style="border-collapse: collapse; border: 1px solid #B0F1A0; width: 100%">
			<tr bgcolor="#B0F1A0">
				<th class="new_cat_th" style="color: red;"></th>
			</tr>
			<tr>
				<td width="100%">
					<div class="categoryTerms" style="width: 100%">
						<table class="changedDecision" width="100%">
						</table>
						<div class="newDecisions" style="width: 100%"></div>
					</div>
				</td>
			</tr>
		</table>
	</div>

	<%
		//get uploadID from URL
		String uploadIDString = "";
		boolean isEmbed = false;
		boolean isValidURL = true;//default url is valid
		boolean isIPlant = false;
		String secret = "";
		if (request.getParameter("origin") != null) {
			if (request.getParameter("origin").toString().equals("iplant")) {
				isIPlant = true;
			}
		}
		
		if (request.getParameter("embed") != null) {
			if (request.getParameter("embed").toString().equals("true")) {
				isEmbed = true;
			}
		}
		if (request.getParameter("uploadID") == null) {
			isValidURL = false;
		} else {
			uploadIDString = request.getParameter("uploadID").toString();
		}

		if (isValidURL) {
			if (request.getParameter("secret") == null) {
				isValidURL = false;
			} else {
				secret = request.getParameter("secret").toString();
			}
		}

		int uploadID = -1;
		if (isValidURL) {
			uploadID = GeneralDBAccess.getInstance().validateURL(
					uploadIDString, secret);
			if (uploadID <= 0) {
				isValidURL = false;
			}
		}

		if (isValidURL) {
			//get upload info to display on the top
			Upload upload = CategorizationDBAccess.getInstance()
					.getUploadInfo(uploadID);

			//get terms to list on the left side
			ArrayList<Group> groups = CategorizationDBAccess.getInstance()
					.getGroups(uploadID);

			//get all categories to display
			ArrayList<Category> categories = CategorizationDBAccess
					.getInstance().getCategories(uploadID);

			//get saved decisions to display
			HashMap<String, ArrayList<SavedTerm>> catTerms = CategorizationDBAccess
					.getInstance().getSavedTerms(uploadID);

			//get type names
			HashMap<Integer, String> termTypes = CategorizationDBAccess
					.getInstance().getTermTypes();
	%>
	<div id="UPLOADID" style="display: none;"><%=uploadID%></div>
	<%
		if (!isEmbed) {
	%>
	<jsp:include page="header.jsp" />
	<%
		}
	%>

	<!-- upload info part -->
	<div style="width: 100%; height: 25px; border: 1px solid #279446;">
		<table style="width: 100%">
			<tr style="width: 100%">
				<td width="100%"><table width="100%">
						<tr>
							<td width="90%">
								<font class="font-text-style" id="dataset_info">
								<!-- uploadID: <b><%=uploadID%></b> ( <%=upload.getNumberTerms()%> terms, upload time: <%=upload.getUploadTime()%> ) -->
								For help, see
								<a href="instruction.do" target="_blank" title="Basic instructions: Expand each term block (left side), view and select (check) the terms, and  drag the green arrow button to move terms to a category. Make as many categorizations as you can with certainty and leave terms if you can't categorize them.">Instructions</a>
								and 
								<a href="https://sites.google.com/site/biosemanticsproject/character-annotation-discussions/term-categorization-instruction" title="Learn Term Categorization" target="_blank">Learn Term Categorization</a></font>
								</font>
								
								<!-- old header
								<font class="font-text-style" id="dataset_info">uploadID: <b><%=uploadID%></b> ( <%=upload.getNumberTerms()%> terms, upload time: <%=upload.getUploadTime()%> )
								<br><b>Basic instruction: </b>Expand each term block (left side), view and select (check) the terms, and  drag the green arrow button to move terms to a category.
								Make as many categorizations as you can with certainty and leave terms if you can't categorize them. Click "Done" to finish.
								<br><b>More instruction information</b> can be found in <a href="instruction.do" title="Instructions of completing your tasks" target="_blank">Help</a>
								and <a href="https://sites.google.com/site/biosemanticsproject/character-annotation-discussions/term-categorization-instruction" title="Learn Term Categorization" target="_blank">Learn Term Categorization</a></font>
								-->
								
							</td>
							<!-- Done button <%
								if (!upload.isFinalized()) {
							%>
							<td width="10%" align="right"><input type="button"
								style="padding: 3px 10px 3px 10px" value="Done"
								onclick="finishCategorizing()" id="btn_done"
								class="uiButton uiButtonSpecial uiButtonMedium"></input></td>
							<%
								}
							%>
							-->
						</tr>
					</table></td>
			</tr>
		</table>
	</div>

	<!-- main part -->
	<%
		if (!upload.isFinalized()) {
	%>
	<div id="dragging_part"
		style="width: 100%; height: 70%; border: 1px solid #279446; overflow: auto">
		<table style="width: 100%; height: 100%;">
			<tr bgcolor="#279446" style="height: 28px; width: 100%">
				<th align="left" width="15%"><font color="white">Terms:</font></th>
				<th width="85%">
					<table width="100%">
						<tr>
							<td width="37.5%" align="left"><font color="white">Categories:</font></td>

							<td width="25%" align="right">
								<form id="submitForm" name="generalForm" action="saveGroup.do"
									method="post">
									<img src="images/green_rot.gif" id="processingSaveImage"
										style="visibility: hidden" width="15px;" /><input
										type="button" name="button" value="Save Decisions"
										class="uiButton uiButtonSpecial uiButtonMedium"
										style="padding: 0px 1px 1px 1px"
										onclick="save_categories(<%=uploadID%>)"
										onmouseover="document.getElementById('serverMessage').innerHTML='&nbsp;'" />
									<input type="hidden" id="hiddenvalue" name="value" />
								</form>
							</td>
							<td width="7.5%"><input type="button"
								style="padding: 0px 1px 1px 1px" value="New Category"
								onclick="newCategory()" id="newCategoryBtn"
								class="uiButton uiButtonSpecial uiButtonMedium"></input></td>
						</tr>
					</table>
				</th>
			</tr>
			<tr style="height: 100%">
				<td height="100%" style="overflow: auto">
					<div id="availableTerms" style="height: 100%;">
						<%
							if (groups.size() > 0) {
										for (Group group : groups) {
											ArrayList<String> terms = group.getTerms();
											if (terms.size() > 0) {
						%>
						<div class="dragGroupTable" id="<%=group.getGroupID()%>">
							<table width="100%">
								<%
									String typeName = termTypes.get(group
																.getGroupID());
														if (typeName == null) {
															typeName = "Unknown Types";
														}
								%>
								<tr>
									<th bgcolor="#B0F1A0" width="100%" colspan="2"><input
										type="checkbox" onclick="checkAll(this)" /><font size="2"><%=typeName.toUpperCase()%></font><img
										onclick="expandTerms(this)" border="0px"
										src="images/icon_expand.gif" height="16px" align="right"></img></th>
								</tr>
								<tr style="display: none;">
									<td>
										<table class="termsTable dragme">

											<%
												for (String term : terms) {
											%>
											<tr class="term_row" id="<%=term%>" termName="<%=term%>"
												onmouseover="displayFixTypoIcon(this)"
												onmouseout="hideFixTypoIcon(this)">
												<td class="term"><input class="checkbox" type="checkbox" align="bottom" /><label
													class="termLabel"
													style="cursor: pointer; color: black; vertical-align: baseline;"
													onclick="setTerm_categorizing('<%=term%>')"><%=term%></label><img
													align="bottom" class="fixTypoIcon" src="images/edit.png"
													height="14px" title="Fix Typo" style="display: none;"
													onclick="fixTypo('<%=term%>')" /></td>
											</tr>
											<%
												}
											%>
										</table>
									</td>
									<!-- <td><img class="dragme" src="images/drag.jpg"
										width="20px;" height="<%=(21 * terms.size())%>px"></img></td> -->
								</tr>
							</table>
						</div>
						<%
							}
										}
									} else {
									}
						%>
					</div>
				</td>

				<td
					style="border-left-color: #279446; border-left-style: solid; border-left-width: 1px; vertical-align: top; height: 100%">
					<!-- below is the table of categories  -->
					<div id="categories_div" style="overflow: auto; height: 100%">
						<table id="categories_table" width="100%">
							<%
								int cellsinrow = 6;
										int count = 1;
										for (Category category : categories) {
											if (count % cellsinrow == 1) {
							%><tr class='category_row'>
								<%
									}
												ArrayList<SavedTerm> savedTerms = catTerms.get(category
														.getName());

												boolean isStructure = category.getName().equals(
														"structure");
								%>
								<td class="categoryTable" id="<%=category.getName()%>"
									style="vertical-align: top" width="16.67%">
									<table width="100%"
										style="border-collapse: collapse; border: 1px solid #B0F1A0;">
										<tr bgcolor="<%=(isStructure ? "#66CC33" : "#B0F1A0")%>">
											<th title="<%=category.getDef()%>"
												style="color: <%=(savedTerms == null ? "grey" : "black")%>"><%=(isStructure ? "STRUCTURE" : category
								.getName())%> <%
 	if (category.isUserCreated() && savedTerms == null) {
 %> (<a href="javascript:void(0)"
												onclick="deleteCategory('<%=category.getName()%>', '<%=uploadID%>')">x</a>)
												<%
 	}
 %></th>
										</tr>
										<tr>
											<td width="100%">
												<!-- decided terms -->
												<div class="categoryTerms" style="width: 100%">
													<table class="savedTerms" width="100%">
														<%
															if (savedTerms != null) {
																			if (savedTerms != null) {
																				for (SavedTerm eachTerm : savedTerms) {
																					if (!eachTerm.isAdditional()) {
														%>
														<tr class="term_row_saved"
															termName="<%=eachTerm.getTermName()%>"
															id="<%=eachTerm.getTermName()%>" valign="middle">
															<td valign="middle">
																<div onmouseover="displayFixTypoIcon(this)"
																	class="mainTerm" onmouseout="hideFixTypoIcon(this)">
																	<!-- <img class="dragAfterSave" src="images/drag.jpg"
																		width="10px;"></img>--> <a class="termLabel"
																		style="text-decoration: none; color: black;"
																		href="javascript:void(0)"
																		onclick="setTerm_categorizing('<%=eachTerm.getTermName()%>')"><%=eachTerm.getTermName()%></a>
																	<img align="bottom" class="fixTypoIcon"
																		src="images/edit.png" height="14px" title="Fix Typo"
																		style="display: none;"
																		onclick="fixTypo('<%=eachTerm.getTermName()%>')" />
																</div> <%
 	if (eachTerm.isHasSyns()) {
 									ArrayList<String> syns = eachTerm
 											.getSyns();
 									for (String eachSyn : syns) {
 %><div onmouseover="displayFixTypoIcon(this)"
																	onmouseout="hideFixTypoIcon(this)">
																	<label class="syn" id="<%=eachSyn%>"
																		termName="<%=eachSyn%>"
																		style="vertical-align: middle;">
																		&nbsp;&nbsp;&nbsp;&nbsp;<a class="termLabel"
																		style="text-decoration: none; color: black;"
																		href="javascript:void(0)"
																		onclick="setTerm_categorizing('<%=eachSyn%>')"><%=eachSyn%></a><img
																		align="bottom" class="fixTypoIcon"
																		src="images/edit.png" height="14px" title="Fix Typo"
																		style="display: none;"
																		onclick="fixTypo('<%=eachSyn%>')" /> <label
																		onclick="removeTerm(this)"
																		style="vertical-align: middle;" class="delete_cross"><font
																			color='blue'>x</font></label>
																	</label>
																</div> <%
 	}
 								}
 %>
															</td>
														</tr>
														<%
															}
																				}
																			}
																		}
														%>
													</table>
													<!-- changed decisions will be listed here -->
													<table class="changedDecision" width="100%">
													</table>
													<!-- here suppose to list the terms to be saved -->
													<div class="newDecisions" style="width: 100%"></div>
												</div>
											</td>
										</tr>
									</table>
								</td>
								<%
									if (count % cellsinrow == 0) {
								%>
							</tr>
							<%
								}
											count++;
										}
							%>

						</table>
					</div>
				</td>
			</tr>
		</table>
	</div>
	<%
		} else {
	%>
	<div style="height: 75%; min-height: 200px;" align="center">
		<font class="font-text-style" color="#279446" style="font-size: 15px">
			The categorization of this upload <b>#<%=uploadID%></b> has been
			finalized! <br></br>
			<%
				if ( isIPlant == true ) {
			%>
			Please go back to iPlant Analysis CharaParser Mockup Tool to complete the process. <br></br> 			
			<%		
				}
			%>
		</font><br></br> <label id="serverMsg">&nbsp;</label>
	</div>
	<%
		}
	%>

	<!-- context part -->
	<div
		style="width: 100%; height: 30%; overflow: auto; border: 1px solid #279446;">
		<table width="100%" cellspacing="0px">
			<tr>
				<th width="10%" class="currentContext" id="termLocations"
					onclick="showTermLocations()"><font>Locations</font></th>
				<th width="10%" class="backContext" id="context"
					onclick="showContext()"><font>Context</font></th>
				<th width="10%" class="backContext" id="glossary"
					onclick="showGlossary()"><font>Ontology</font></th>
				<th width="70%" align="left" bgcolor="white"
					style="text-align: right"><font color="#279446">&nbsp;</font>
					<input
										type="button" name="button" value="Search"
										class="uiButton uiButtonSpecial uiButtonMedium"
										style="padding: 0px 1px 1px 1px"
										onclick="openTermLocator()"/>
					<img
					src="images/locator.png" onclick="openTermLocator()" height="14px"></img></th>
			</tr>
			<tr>
				<td width="85%" colspan="4" style="border: 2px solid #279446">

					<table width="100%">
						<tr>
							<th width="15%" align="left" id="th_context_1"></th>
							<th width="85%" align="left" id="th_context_2"></th>
						</tr>
					</table>
					<div style="width: 100%; height: 80px; overflow: auto;"
						class="border" id="contextSentences">
						<table width="100%" id="contextTable">
							<tr>
								<td width="2%">&nbsp;</td>
								<td width="98%"><font class="font-text-style">Click
										on term name to view locations of all copies, context and
										glossary.</font></td>
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
	<%
		} else {
	%>

	<jsp:include page="header.jsp" />
	<div style="height: 500px">
		<font class="font-text-style"> The provided URL contains invalid parameters. Please verify the URL and try again. </font>
	</div>
	<%
		}
	%>
	<%
		if (!isEmbed) {
	%>
	<jsp:include page="footer.jsp" />
	<%
		}
	%>
</body>
<%@ page errorPage="error.jsp"%>
</html>
