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
<title>Help Information</title>
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

<body>
	<%



	%>

	<jsp:include page="header.jsp" />


	<!-- upload info part -->
	<div>
		<ul>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#groupterms">Instructions for <b>"Group Terms"</b> page
			</a></li>
		</ul>
	</div>

	<a name="groupterms"></a>
	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">Instructions of <font
				color="purple">Group Terms</font> page
			</th>
		</tr>
		<tr>
			<td>
				<ul>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Categorize
								terms</b><br/>To categorize terms into the right category,
								select terms and drag the arrow that is associated with them
								onto the category box. <br/>Please notice that only the
									arrow can be dragged. <br/>Click the 'Save Decisions' button on top-right to save your decisions.
										You can save multiple times. <br/></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View/Change
								your decisions</b><br/>Click the category name, you will be able
								to view all the terms that have been categorized. <br/>You
									may change the terms by dragging the arrow in front of the term
									into other category that you think is more suitable. <br/>You
										can also drag the term out of the category panel to
										uncategorize the term if you think there is no suitable
										category. The removed term will automatically go back to the
										left 'Terms' panel.<br/></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Create
								a new category</b><br/>By clicking the 'New Category' button on
								the top-right of category panel, you can create a new category
								which is not in existent category list. You will need the
								specify the definition of the category you created. <br/></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Make a
								copy of a term</b><br/>If a term belongs to two or more
								categories, you can Ctrl+dragging (or Command+dragging on MAC)
								the term to another category to make a copy of the term. The
								term will be renamed with additional index such as 'term_1',
								'term_2'. <br/>Please notice that once you saved the copied
									terms, they cannot be deleted. <br/></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								term's locations</b><br/>Since there may be multiple copies of
								each term, you can view the locations of each copy at the
								'Locations' tab.<br/>The list contains both the saved copies
									and the newest unsaved copies on the page. We recommend users
									to check existing copies of a term before you create a new copy
									of that term since saved copies cannot be deleted once you
									submit them to the database. <br/></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Make
								synonyms</b><br/>After categorizing the terms, if two terms are
								synonyms or the same, you can make them synonyms or merge them
								together by dragging one term onto a second term. <br/>The
									term you are dragging must be single (has no synonyms). To drag
									terms with existing synonyms, click on the '<font
									style="color: blue; text-decoration: underline;">x</font>' sign
									behind the synonym to break them to single terms, then drag
									them to the target term one by one. <br/>If there are too
										many terms in one category box, you will need to use the
										auto-scroll function to look for the target term during
										dragging. To use auto-scroll, first drag a term and hold on to
										it (Don't release the mouse). Move the cursor up until the
										position of the cursor is outside of the category box or move
										the cursor down until it is outside of the category box to
										make the scroll bar go down. Once you find the target term,
										move the cursor back to the category box. The scroll bar will
										freeze if the cursor is inside the category box. Now you can
										drop the term to make synonyms. <br/>Please notice only
											the arrow can be dragged. 
						<br/></font></li>

					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								context/glossary</b><br/>To view context or glossary, just click
								the term name, and the context/glossary will show up in the
								Locations/Context/Glossaries panel at the bottom of page. Click
								the 'Context' and 'Glossaries' tab to view the content you like.
								<br/></font></li>
				</ul>
			</td>
		</tr>
	</table>
	<jsp:include page="footer.jsp" />


</body>
<%@ page errorPage="error.jsp"%>
</html>









