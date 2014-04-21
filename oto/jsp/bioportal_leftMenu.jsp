<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page import="java.util.ArrayList"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.TermForBioportalBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.TermsForBioportalBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.DatasetBean"%>

<%
	SessionDataManager sessionDataManager = (SessionDataManager) (session
			.getAttribute("sessionDataMgr"));
	String dataset = sessionDataManager.getDataset();
	CharacterDBAccess cdba = new CharacterDBAccess();

	int expand = 0;
	if (request.getParameter("expand") != null) {
		expand = Integer.parseInt(request.getParameter("expand")
				.toString());
	}

	String termToDisplay = "";
	if (request.getParameter("term") != null) {
		termToDisplay = request.getParameter("term").toString();
	}

	//get the terms list that to list on the left side
	TermsForBioportalBean terms = cdba
			.getAvailableTermsForSubmission(dataset);
	DatasetBean ds = cdba.getDataset(dataset);
%>
<div style="height: 500px; overflow: scroll;" id="DIV_TERMLIST">
	<table width="100%">

		<%
			for (int i = 0; i < 2; i++) {
		%>

		<tr>
			<td width="100%">
				<div style="border-collapse: collapse; border: 1px solid green;">
					<table width="100%">
						<tr>
							<th bgcolor="#B0F1A0" align="left" height="17px" width="100%"><font
								size="2"><%=i == 0 ? "Terms for Submission" : "Removed Terms"%></font></th>
						</tr>
						<tr style="display: block;">
							<td width="100%"><table width="100%">
									<%
										for (int j = 0; j < 2; j++) {
									%>
									<%
										ArrayList<String> termList = null;
												String tableID = "";
												int location = 0;
												boolean display = false;//to display the block or not
												if (i == 0 && j == 0) {//regular structure
													termList = terms.getRegStructures();
													tableID = "regStructuresTable";
													display = (expand == 1 ? true : false);
													location = 1;
												} else if (i == 0 && j == 1) {
													termList = terms.getRegCharacters();
													tableID = "regCharactersTable";
													display = (expand == 2 ? true : false);
													location = 2;
												} else if (i == 1 && j == 0) {
													termList = terms.getRemovedStructures();
													tableID = "RemovedStructuresTable";
													display = (expand == 3 ? true : false);
													location = 3;
												} else {
													termList = terms.getRemovedCharacters();
													tableID = "RemovedCharactersTable";
													display = (expand == 4 ? true : false);
													location = 4;
												}
									%>
									<tr>
										<th bgcolor="#B0F1A0" align="left" height="17px" width="100%"><font
											style="text-align: left; width: 60%" size="2"><%=j == 0 ? "Structures" : "Characters"%></font><font
											style="text-align: left; width: 28%; font-weight: normal;"
											color="green"> (<span id="SIZE_OF_LIST_<%=location%>"><%=termList.size()%></span>)
										</font><img onclick="expandTerms(this)" border="0px"
											style="width: 12%"
											src="images/icon_<%=display ? "collapse" : "expand"%>.gif"
											height="16px" align="right"></img></th>
									</tr>
									<tr style="display: <%=display ? "block" : "none"%>">
										<td width="100%">
											<table id=<%=tableID%> width="100%">
												<%
													if (termList != null && termList.size() > 0) {
																for (String term : termList) {
												%>
												<tr>
													<td style="padding: 0px 0px 0px 10px;"><a
														id="LEFTLIST_a_<%=location%>_<%=term%>"
														href="ontologyTerm.do?term=<%=term%>&expand=<%=location%>"
														onclick="updateURL('<%=term%>', '<%=location%>')"
														style="color: black; text-decoration: <%=location == expand
									&& term.equals(termToDisplay) ? "" : "none"%>;  
														vertical-align: middle"><font
															class="font-text-style"><%=term%></font></a> <label
														title="Remove term from this list"
														style="color: blue; vertical-align: middle;"
														onclick="removeTerm(this, '<%=term%>', <%=i%>, <%=j%>, <%=ds.getGlossaryID()%>)">x</label>
													</td>
												</tr>
												<%
													}
												%>
												<%
													}
												%>
											</table>
										</td>
									</tr>
									<%
										}
									%>


								</table></td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
		<%
			}
		%>
	</table>
</div>