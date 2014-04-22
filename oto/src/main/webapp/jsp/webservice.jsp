<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.form.RegistrationForm"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<script language="javascript" src="js/jquery-1.8.3.js"></script>
<script language="javascript" src="js/fader.js"></script>
<script language="javascript" src="js/group.js"></script>
<script language="javascript" src="js/download.js"></script>
</head>
<body>
	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));
		if (sessionDataManager == null) {
	%>
	<jsp:include page="GotologinHeader.jsp" />
	<%
		} else {
	%>
	<jsp:include page="header.jsp" />
	<%
		}
	%>
	<div>
		<font class="font-text-style" style="font-weight: bold;">The
			following functions are supported by OTO Web Service:</font>
	</div>
	<div>
		<ul>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#glossaryTypes">Get the glossaryTypes </a></li>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#glossary">Download glossary </a></li>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#categoryAndDef">Get glossary categories and their
					definitions </a></li>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#categoryListOfTerm">Get term entries of a glossary type in OTO </a></li>
				
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#getTermEntry">Get the term category entry of a glossary type in OTO</a></li>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#addTermEntry">Add a term category entry of a glossary type in OTO </a></li>
		</ul>
	</div>

	<a name="glossaryTypes"></a>
	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">* Get the available
				glossary types in OTO</th>
		</tr>
		<tr>
			<td>
				<ul>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b>Access</b>: <br />GET <a
							href="http://biosemantics.arizona.edu/OTO/rest/glossaryTypes">
								http://biosemantics.arizona.edu/OTO/rest/glossaryTypes</a><br />
							Parameters: No parameters <br />Return: The available glossary
							types in OTO.</font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b>Example</b>: <br />GET <a
							href="http://biosemantics.arizona.edu/OTO/rest/glossaryTypes">
								http://biosemantics.arizona.edu/OTO/rest/glossaryTypes</a><br />Return:
							<br /> ["Plant", "Hymenoptera", "Algea", "Porifera", "Fossil"]</font></li>
				</ul>
			</td>
		</tr>
	</table>

	<a name="glossary"></a>

	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">* Get the most
				recent version of a glossary of a certain glossaryType</th>
		</tr>
		<tr>
			<td>
				<ul>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style">A glossary consists of terms and
							their categories, as well as synonyms.</font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b>Access</b>: <br />GET <a
							href="http://biosemantics.arizona.edu/OTO/rest/glossaries/Plant">
								http://biosemantics.arizona.edu/OTO/rest/glossaries/</a>{glossaryType}<br />
							Parameters: glossaryType <br />Return: The glossary of the
							specified glossaryType.</font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b>Example</b>: <br />GET Plant
							glossary: replace {GlossryType} with 'Plant' in the URL<br />&nbsp;&nbsp;<a
							href="http://biosemantics.arizona.edu/OTO/rest/glossaries/Plant">http://biosemantics.arizona.edu/OTO/rest/glossaries/Plant</a><br />Return:
							<br /> {<br /> &nbsp;&nbsp;&nbsp;&nbsp;"version": "0.7",<br />
							&nbsp;&nbsp;&nbsp;&nbsp;"termCategories": [{<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"term":
							"lineolate",<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"category":
							"external texture",<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"hasSyn": false,<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"sourceDataset":
							"fna_gloss_final_20130517",<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"termID":
							"6f55220d-7825-4e6b-9d4e-860abdb4ef3e"<br />
							&nbsp;&nbsp;&nbsp;&nbsp;},<br /> &nbsp;&nbsp;&nbsp;&nbsp;{<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"term": "green",<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"category":
							"color",<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"hasSyn": true,<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"sourceDataset":
							"fna_gloss_final_20130517",<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"termID":
							"fccefe3f-8664-4fd6-a828-807750a658f4"<br />
							&nbsp;&nbsp;&nbsp;&nbsp;}],<br />
							&nbsp;&nbsp;&nbsp;&nbsp;"termSynonyms": [{<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"term":"green",<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"category":"color",<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"synonym":"greenish",<br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"termID":"fccefe3f-8664-4fd6-a828-807750a658f4"<br />
							&nbsp;&nbsp;&nbsp;&nbsp;}]<br /> }<br /></font></li>
				</ul>
			</td>
		</tr>
	</table>

	<a name="categoryAndDef"></a>
	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">* Get the glossary
				categories and their definitions</th>
		</tr>
		<tr>
			<td>
				<ul>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b>Access</b>: <br />GET <a
							href="http://biosemantics.arizona.edu/OTO/rest/categories">
								http://biosemantics.arizona.edu/OTO/rest/categories</a><br />
							Parameters: No parameters <br />Return: The glossary categories
							and their definitions.</font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b>Example</b>: <br />GET <a
							href="http://biosemantics.arizona.edu/OTO/rest/categories">
								http://biosemantics.arizona.edu/OTO/rest/categories</a><br />Return:
							<br /> [{<br />&nbsp;&nbsp;&nbsp;&nbsp;"category":"internal
							texture",<br />&nbsp;&nbsp;&nbsp;&nbsp;"definition":"Internal
							elements."<br />},<br /> {<br />&nbsp;&nbsp;&nbsp;&nbsp;"category":"arrangement",<br />&nbsp;&nbsp;&nbsp;&nbsp;"definition":"Disposition
							of structures with respect to one another within some explicit or
							implicit standard context."<br />},<br /> {<br />&nbsp;&nbsp;&nbsp;&nbsp;"category":"behaviour",<br />&nbsp;&nbsp;&nbsp;&nbsp;"definition":"'action'
							terms such as 'collapsing' or 'disintegrating' that are unrelated
							to plant age or maturity."<br />}]</font></li>
				</ul>
			</td>
		</tr>
	</table>
	
	<a name="categoryListOfTerm"></a>
	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">* Get term entries of a glossary type in OTO</th>
		</tr>
		<tr>
			<td>
				<ul>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style">
						<b>Access</b>: <br />
						GET <a href="#categoryListOfTerm">
								http://biosemantics.arizona.edu/OTO/rest/termCategories/{glossaryType}/{term}</a><br />
						Parameters: glossaryType, term <br />
						Return: The list of term category entries associated with the term in the glossaryType</font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style">
						<b>Example</b>: <br />
						GET <a
							href="http://biosemantics.arizona.edu/OTO/rest/termCategories/Hymenoptera/abundance">
								http://biosemantics.arizona.edu/OTO/rest/termCategories/Hymenoptera/abundance</a><br />
						Return:<br /> 
						[{<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"termID":"149af0ee-21de-11e3-a402-0026b9326338",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"term":"abundance",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"category":"density",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"glossaryType":"Hymenoptera",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"definition":null<br />
						},<br /> 
						{<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"termID":"14a46778-21de-11e3-a402-0026b9326338",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"term":"abundance",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"category":"count",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"glossaryType":"Hymenoptera",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"definition":null<br />
						}]</font></li>
				</ul>
			</td>
		</tr>
	</table>
	
	<a name="getTermEntry"></a>
	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">* Get the term category entry of a glossary type in OTO</th>
		</tr>
		<tr>
			<td>
				<ul>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style">
						<b>Access</b>: <br />
						GET <a href="#getTermEntry">
								http://biosemantics.arizona.edu/OTO/rest/termCategories/{glossaryType}/{term}/{category}</a><br />
						Parameters: glossaryType, term, category <br />
						Return: The term category entry associated with the term and category in the glossaryType or null if none was found</font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style">
						<b>Example</b>: <br />
						GET <a
							href="http://biosemantics.arizona.edu/OTO/rest/termCategories/Plant/round/shape">
								http://biosemantics.arizona.edu/OTO/rest/termCategories/Plant/round/shape</a><br />
						Return:<br /> 
						[{<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"termID":"0012424f-dc82-4749-88b0-d8758b4b7d3b",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"term":"round",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"category":"shape",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"glossaryType":"Plant",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"definition":"Circular."<br />
						}]</font></li>
				</ul>
			</td>
		</tr>
	</table>
	
	<a name="addTermEntry"></a>
	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">* Add a term category entry of a glossary type in OTO</th>
		</tr>
		<tr>
			<td>
				<ul>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style">
						<b>Access</b>: <br />
						PUT <a href="#addTermEntry">
								http://biosemantics.arizona.edu/OTO/rest/termCategories/{glossaryType}/{term}/{category}</a><br />
						Parameters:  glossaryType, term, category, and definition as request entity <br />
						Return: The term category entry associated with the term and category in the glossaryType</font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style">
						<b>Example</b>: <br />
						PUT <a
							href="http://biosemantics.arizona.edu/OTO/rest/termCategories/Plant/square/shape">
								http://biosemantics.arizona.edu/OTO/rest/termCategories/Plant/square/shape</a> with request entity "Circularish."<br />
						Return:<br /> 
						[{<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"termID":"0012424f-dc82-4749-88b0-d8758b4b7d3b",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"term":"roundish",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"category":"shape",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;glossaryType":"Plant",<br />
						&nbsp;&nbsp;&nbsp;&nbsp;"definition":"Circularish."<br />
						}]</font></li>
				</ul>
			</td>
		</tr>
	</table>

	<jsp:include page="footer.jsp" />

</body>
<%@ page errorPage="error.jsp"%>
</html>