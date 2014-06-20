<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="edu.arizona.biosemantics.oto.oto.beans.DatasetBean"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.GlossaryGroupBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper" %>

<script language="javascript" src="js/managerSave.js"></script>
<%
	SessionDataManager sessionDataManager = (SessionDataManager) (session
			.getAttribute("sessionDataMgr"));
	String selectedDataset = "", manageType = "";
	if (session.getAttribute("selectedDataset") != null) {
		selectedDataset = session.getAttribute("selectedDataset")
				.toString(); //used to mark current menu
	}
	if (session.getAttribute("manageType") != null) {
		manageType = session.getAttribute("manageType").toString();//used to mark current menu
	}

	CharacterDBAccess cdba = new CharacterDBAccess();
	User user = sessionDataManager.getUser();
%>

<table style="border-right: 1px solid green">

	<!-- manage user link -->
	<%
		if (user.getRole().equals("S") || user.getRole().equals("A")) {
	%>
	<tr>
		<th bgcolor="green" align="left"><font color="white">Users
				Management</font></th>
	</tr>
	<tr>
		<td><a href="manageUsers.do"
			<%if (manageType.equals("0"))
					out.print("style='color: purple'");
				else
					out.print("style='color: black'");%>><font
				class="font-text-style">All Users</font></a></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<%
		}
	%>

	<!-- merge dataset link -->
	<%
		if (user.getRole().equals("S") || user.getRole().equals("A") || user.getRole().equals("O")) {
	%>
	<tr>
		<th bgcolor="green" align="left"><font color="white">Merge
				Datasets</font></th>
	</tr>
	<tr>
		<td><a href="mergeDatasets.do"
			<%if (manageType.equals("5"))
					out.print("style='color: purple'");
				else
					out.print("style='color: black'");%>><font
				class="font-text-style">Merge unfinalized datasets</font></a></td>
	</tr>
	<%
		if (user.getRole().equals("S")) {
	%>
	<tr>
		<td><a href="mergeIntoSystem.do"
			<%if (manageType.equals("6"))
						out.print("style='color: purple'");
					else
						out.print("style='color: black'");%>><font
				class="font-text-style">Merge into system datasets</font></a></td>
	</tr>
	<%
		}
	%>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<%
		}
	%>

	<!-- manage decisions links -->
	<%
		ArrayList<String> datasets = cdba.getManageableDatasets(user);
		if (datasets.size() > 0) {
	%>
	<tr>
		<th bgcolor="green" align="left"><font color="white">Decisions
				Management</font></th>
	</tr>
	<%
		}
	%>
	<tr>
		<td width="15%" valign="top"
			style="padding-top: 2px; padding-bottom: 5px;">
			<%
				for (String dataset : datasets) {
					boolean isCurrent = manageType.equals("4")
							&& selectedDataset.equals(dataset);
			%>
			<div class="dtree" style="height: 100px">
				<font class="font-text-style"
					style="font-size: 13px; font-weight: bolder; background-color: #B0F1A0;">
					<a href="manageDataset.do?s=<%=dataset%>"
					title="Click to view dataset information."
					<%=isCurrent ? "style='color: purple'" : ""%>><%=GlossaryNameMapper.getInstance().isGlossaryReservedDataset(dataset) ? dataset
						+ " [System Reserved]"
						: dataset%></a>
					<%
						if (!cdba.isConfirmed(dataset, 4)
									&& !GlossaryNameMapper.getInstance().isSystemReservedDataset(dataset)) {
					%> <a href="manageDataset.do?s=<%=dataset%>&action=delete"
					style="text-decoration: underline; margin-left: 2px; color: blue">x</a>
					<%
						}
					%>
				</font>
				<div class="clip" id=<%=dataset%> style="padding-top: 3px;">
					<div class="dtree" onclick="getReport(this)" id="1"
						style="cursor: pointer;">
						<img src="images/tree/join.gif"></img><a
							href="manageCategory.do?s=<%=dataset%>"
							<%boolean confirmed = cdba.isConfirmed(dataset, 1);
				isCurrent = manageType.equals("1")
						&& selectedDataset.equals(dataset);
				if (isCurrent)
					out.print("style='color: purple'");
				else if (confirmed)
					out.print("style='color: gray'");
				else
					out.print("style='color: black'");%>><font
							class="font-text-style" style="text-decoration: underline;"
							<%=(isCurrent ? " id='current'" : "")%>>Group Terms<%=(confirmed ? " <b>[Finalized]</b>" : "")%></font></a>
					</div>

					<div class="dtree" onclick="getReport(this)" id="2"
						style="cursor: pointer;">
						<img src="images/tree/join.gif"></img><a
							href="managPath.do?s=<%=dataset%>"
							<%confirmed = cdba.isConfirmed(dataset, 2);
				isCurrent = manageType.equals("2")
						&& selectedDataset.equals(dataset);
				if (isCurrent)
					out.print("style='color: purple'");
				else if (confirmed)
					out.print("style='color: gray'");
				else
					out.print("style='color: black'");%>><font
							class="font-text-style" style="text-decoration: underline;"
							<%=(isCurrent ? " id='current'" : "")%>>Structure
								Hierarchy<%=(confirmed ? " <b>[Finalized]</b>" : "")%></font></a>
					</div>
					<div class="dtree" onclick="getReport(this)" id="3"
						style="cursor: pointer;">
						<img src="images/tree/joinbottom.gif"></img><a
							href="manageOrder.do?s=<%=dataset%>"
							<%confirmed = cdba.isConfirmed(dataset, 3);
				isCurrent = manageType.equals("3")
						&& selectedDataset.equals(dataset);
				if (isCurrent)
					out.print("style='color: purple'");
				else if (confirmed)
					out.print("style='color: gray'");
				else
					out.print("style='color: black'");%>><font
							class="font-text-style" style="text-decoration: underline;"
							<%=(isCurrent ? " id='current'" : "")%>>Term Order<%=(confirmed ? " <b>[Finalized]</b>" : "")%></font></a>
					</div>
				</div>
			</div> <%
 	}
 %>
		</td>
	</tr>
</table>