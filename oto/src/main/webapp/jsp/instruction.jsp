<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@page import="edu.arizona.biosemantics.oto.oto.beans.CategoryBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
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
	%>
	<!-- Session Validity check header End -->
	<jsp:include page="header.jsp" />
	<div>
		<ul>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#groupterms">Instructions for <b>"Group Terms"</b> page
			</a></li>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#hierarchy">Instructions for <b>"Structure Hierarchy"</b>
					page
			</a></li>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#termorder">Instructions for <b>"Term Order"</b> page
			</a></li>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#admin">Instructions for <b>"Admin"</b> page
			</a></li>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#manageDatasets">Instructions for <b>"Manage Datasets"</b>
					page
			</a></li>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#catDef">Definitions of Categories in <b>"Group Terms"</b>
					page
			</a></li>
			<li
				style="padding-bottom: 10px; font-family: lucida grande; font-size: 13px"><a
				href="#updates">Updates </a></li>

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
						class="font-text-style"><b style="color: green">Select
								dataset prefix</b><br />After you log into our system, in the home
							page, you can select the dataset prefix you would like to work
							on. <br />Dataset information, such as dataset's name, total
							number of terms in this dataset and number of terms you have
							reviewed in this dataset, will be displayed at the top of the
							categorizing page. <a
							href="jsp/video.jsp?v=selectDataset&h=Select dataset prefix">watch
								video</a> <br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Categorize
								terms</b><br />To categorize terms into the right category, select
							terms and drag the arrow that is associated with them onto the
							category box. <br />Please notice that only the arrow can be
							dragged. <br />Click the 'Save Decisoin/Submit Review History'
							button on top-right to save your decisions. You can save multiple
							times. <a href="jsp/video.jsp?v=categorize&h=Categorize terms">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View/Change
								your decisions</b><br />Click the category name, you will be able
							to view all the terms that have been categorized. <br />You may
							change the terms by dragging the arrow in front of the term into
							other category that you think is more suitable. <br />You can
							also drag the term out of the category panel to uncategorize the
							term if you think there is no suitable category. The removed term
							will automatically go back to the left 'Terms' panel. When you
							uncategorize a term, you will be asked to explain why you need to
							uncategorize it. The reason you input will be saved as a comment
							to the term when you save your decisions and review history,
							which can be seen in term's report. <a
							href="jsp/video.jsp?v=change&h=View/Change your decisions">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Create
								a new category</b><br />By clicking the 'New Category' button on
							the top-right of category panel, you can create a new category
							which is not in existent category list. You will need the specify
							the definition of the category you created. <a
							href="jsp/video.jsp?v=new&h=Create a new category">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Make a
								copy of a term</b><br />If a term belongs to two or more
							categories, you can Ctrl+dragging (or Command+dragging on MAC)
							the term to another category to make a copy of the term. The term
							will be renamed with additional index such as 'term_1', 'term_2'.
							<br />Please notice that once you saved the copied terms, they
							cannot be deleted. <a
							href="jsp/video.jsp?v=copy&h=Make a copy of a term">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								term's locations</b><br />Since there may be multiple copies of
							each term, you can view the locations of each copy at the
							'Locations' tab.<br />The list contains both the saved copies
							and the newest unsaved copies on the page. We recommend users to
							check existing copies of a term before you create a new copy of
							that term since saved copies cannot be deleted once you submit
							them to the database. <a
							href="jsp/video.jsp?v=copy&h=View term's locations">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Make
								synonyms</b><br />After categorizing the terms, if two terms are
							synonyms or the same, you can make them synonyms or merge them
							together by dragging one term onto a second term. <br />The term
							you are dragging must be single (has no synonyms). To drag terms
							with existing synonyms, click on the '<font
							style="color: blue; text-decoration: underline;">x</font>' sign
							behind the synonym to break them to single terms, then drag them
							to the target term one by one. <br />If there are too many terms
							in one category box, you will need to use the auto-scroll
							function to look for the target term during dragging. To use
							auto-scroll, first drag a term and hold on to it (Don't release
							the mouse). Move the cursor up until the position of the cursor
							is outside of the category box or move the cursor down until it
							is outside of the category box to make the scroll bar go down.
							Once you find the target term, move the cursor back to the
							category box. The scroll bar will freeze if the cursor is inside
							the category box. Now you can drop the term to make synonyms. <br />Please
							notice only the arrow can be dragged. <a
							href="jsp/video.jsp?v=synonym&h=Make synonyms">watch video</a><br /></font></li>

					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								context/glossary</b><br />To view context or glossary, just click
							the term name, and the context/glossary will show up in the
							Locations/Context/Glossaries panel at the bottom of page. Click
							the 'Context' and 'Glossaries' tab to view the content you like.
							<a href="jsp/video.jsp?v=context&h=View context/glossary">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Review
								History</b><br />Actions on a term, such as categorizing, clicking
							the term name, clicking to view term's report, making synonyms,
							breaking synonyms, will be considered as reviewing the term. You
							have to click the 'Save Decisions/Submit Review History' button
							before you leave the page to save your reviewing actions. Terms
							which have been reviewed will be displayed in grey color. Terms
							you haven't touched before will be displayed in black color. It
							is the same with the category. If the category name is grey, it
							means there is no term in that category box that you haven't
							looked. Otherwise, the color of the category name is black. <a
							href="jsp/video.jsp?v=reviewHistory&h=Review History">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								term's report/Comment</b><br />For each term, there is a <img
							width="14px" src='images/view.gif'></img> (meaning all users
							agree with current decision) or a <img src="images/down.jpg"
							width="14px"></img> (meaning some users disagree with current
							decision) button. Click the button, you will see a full report of
							a term. A term's report contains all categorizing decisions on
							this term, all comments and last reviewing action on this term. <br />This
							page is also a place you can make comment on a specific term.<a
							href="jsp/video.jsp?v=comment&h=View term's report/Comment">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								your report</b><br />In the 'Reports' page, you can view the status
							of all decisions you've made. If the acceptance status is
							pending, it means this term is still in collecting opinions
							phase. <a href="jsp/video.jsp?v=report&h=View your report">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Copy
								System Decisions</b><br />When you start to work on the
							categorization of thousands of terms, you may find it
							overwhelming. The button 'Copy System Decisions' may save you
							some time by copying all the approved categorization and synonyms
							decisions from OTO system. <br />We strongly suggest you to do
							this right after you create a dataset so that all the users can
							focus on those 'new' terms to OTO. </font></li>
				</ul>
			</td>
		</tr>
	</table>

	<a name="hierarchy"></a>
	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">Instructions of <font
				color="purple">Structure Hierarchy</font> page
			</th>
		</tr>
		<tr>
			<td>
				<ul>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Select
								dataset prefix</b><br />After you log into our system, in the home
							page, you can select the dataset prefix you would like to work
							on. <br />Then you will see "Structure Hierarchy" in the main
							menu, which will direct you to the "Structure Hierarchy" page. <a
							href="jsp/video.jsp?v=selectdataset_tree&h=Select dataset prefix">watch
								video</a> <br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Build a
								hierarchical tree</b><br />Your task in this page is to build a
							hierarchical tree with giving structures. To add a term into the
							tree, simply drag the arrow in front of the term and drop it onto
							any node (node P) you want. The term will become a child of node
							P. <br />Notice that a term cannot be a child or grandchild of
							itself. <br />The term is still draggable before you the tree by
							hitting button 'Save Tree'. You can change your decision by drag
							the term to another node or hit the little cross attached to the
							term to remove it from the tree. <br />If you remove it from the
							tree, the term will automatically go back to the structures list
							in the 'Structures' panel. <a
							href="jsp/video.jsp?v=buildtree&h=Build a hierarchical tree">watch
								video</a> <br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Save
								the tree</b><br />When you finished constructing the tree, you must
							save the tree to the database by hitting button 'Save Tree' which
							located on the top right cornor of the 'Hierarchy' panel.<br />Notice
							that after you save the tree, you may add term to the tree, but
							you cannot change the position of saved terms any more.
							Therefore, saved decisions cannot be changed, but you can save
							the tree multiple times. <a
							href="jsp/video.jsp?v=savetree&h=Save the tree">watch video</a> <br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Copy a
								term</b><br />In case a term can be placed in more than one
							positions, you can drag a term to the tree without removing it
							from the structures list so that it is available for later
							dragging. To do this, hit 'Ctrl' or 'command' key when dropping
							the term onto a node. <br />You can do the same to drag a node
							on the tree to make a copy of the node. <a
							href="jsp/video.jsp?v=copystructure&h=Copy a term">watch
								video</a> <br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								context/glossary</b><br />To view context or glossary, just click
							the term name, and the context/glossary will show up in the
							context/glossary panel at the bottom of page. Click the 'Context'
							and 'Glossary' tab to view the content you like. <a
							href="jsp/video.jsp?v=context_tree&h=View context/glossary">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								term's report/Comment</b><br />For each order, there is a <img
							width="14px" src='images/view.gif'></img> (meaning all users
							agree with your order) or a <img src="images/down.jpg"
							width="14px"></img> (meaning decisions made by other uses are not
							exactly the same with your order) button. Click the button, you
							will see a full report of that term. <br />You can also make
							comment on this term in the report page. <a
							href="jsp/video.jsp?v=termreport_tree&h=View term's report/Comment">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								your report</b><br />In the 'Reports' page, you can view the status
							of all decisions you've made. <a
							href="jsp/video.jsp?v=report_tree&h=View your report">watch
								video</a><br /></font></li>
				</ul>
			</td>
		</tr>
	</table>

	<a name="termorder"></a>
	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">Instructions of <font
				color="purple">Term Order</font> page
			</th>
		</tr>
		<tr>
			<td>
				<ul>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Select
								dataset prefix</b><br />After you log into our system, in the home
							page, you can select the dataset prefix you would like to work
							on. <br />Then you will see "Term Order" in the main menu, which
							will direct you to the "Term Order" page. <a
							href="jsp/video.jsp?v=selectDataset_order&h=Select dataset prefix">watch
								video</a> <br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Basic
								concepts in this page</b><br />In each group, there are a number of
							terms to be ordered (displayed in green background in the first
							row of each group). There could be more than one order for these
							terms at different dimension. <br />You can drag available terms
							into the order boxes to organize the order.<br />You may create
							new terms or a new order on different dimension for each group. </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Order
								the terms</b><br />To order the term, simply drag a term into the
							box you want to put it in. You can only drag a term from the base
							row (terms in light green background) to an order, or adjust the
							position of a term inside an order. You can also remove a term
							from an organized order by dragging it out of the order's range.
							<br />Notice that selected terms will be highlighted by grey
							color. <br />After you finish ordering, click "save Orders"
							button to save your decision in that group. <a
							href="jsp/video.jsp?v=order&h=View/Order the terms">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								your decisions</b><br />After you saved your decision, the order
							will display your latest saved decision. If you never saved a
							decision before, the system will give you the latest decision
							made by other users for reference. <br />By clicking the order
							name, all terms in this order will be highlighted. <a
							href="jsp/video.jsp?v=vieworder&h=View your decision">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Create
								a new term</b><br />By clicking the 'New Term' link in the base row
							of each group, you can create a new term for that group. Terms
							must be unique in one group. <a
							href="jsp/video.jsp?v=newterm_order&h=Create a new term">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Create
								a new order</b><br />By clicking the 'New Order' link in the base
							row of each group, you can create a new order for that group.
							Order name must be unique in one group. <a
							href="jsp/video.jsp?v=neworder&h=Create a new order">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								context/glossary</b><br />To view context or glossary, just click
							the term name, and the context/glossary will show up in the
							context/glossary panel at the bottom of page. Click the 'Context'
							and 'Glossary' tab to view the content you like. <a
							href="jsp/video.jsp?v=context_order&h=View context/glossary">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								order's report/Comment</b><br />For each order, there is a <img
							width="14px" src='images/view.gif'></img> (meaning all users
							agree with your order) or a <img src="images/down.jpg"
							width="14px"></img> (meaning decisions made by other uses are not
							exactly the same with your order) button. Click the button, you
							will see a full report of that order. <br />You can also make
							comment on this order in the report page. <a
							href="jsp/video.jsp?v=termreport_order&h=View order's report/Comment">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">View
								your report</b><br />In the 'Reports' page, you can view the status
							of all decisions you've made. <a
							href="jsp/video.jsp?v=report_order&h=View your report">watch
								video</a><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Change
								order name</b><br />Mouse over the order name, you should be able
							to see the edit sign <img src="images/edit.png" height="13px" />
							next to the order name. Click on the edit sign, you can change
							the order name as needed. <br />Note: Order name should be
							unique in each order group.<br /></font></li>
				</ul>
			</td>
		</tr>
	</table>

	<a name="admin"></a>
	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">Instructions of <font
				color="purple">Admin</font> page
			</th>
		</tr>
		<tr>
			<td>
				<ul>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">What
								can be done in Admin page? </b><br /> User (must be administrator
							of OTO or owner of a dataset) to be able to access admin page. <br />
							<br />1. To approve a user's decision<br />2. To revoke a
							user's decision<br />3. To finalize a dataset<br />4. To reopen
							a dataset<br />5. To approve all system decisions <br />6. To
							merge datasets</font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Approve
								a decision</b><br /> All candicate decisions are listed in 'Other
							decisions' column. You can click <img src="images/accept.jpg"
							height="12px"></img> to approve it. After approve it, the
							decision will be moved to 'Accepted Decisions' column. <br />
							You can also mouse over the decision to see who made the
							decision. </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Revoke
								a decision</b><br />Decisions in 'Accepted Decisions' column can be
							revoked. You can click <img src="images/revoke.jpg" height="12px"></img>
							to revoke the approval of the decision. </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Finalize
								a dataset</b><br /> Finalize a dataset will freeze the dataset and
							generate downloadable files for the public to download. You can
							finalize a dataset by clicking 'Finalize this dataset' button on
							the top right of the page. </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Reopen
								a dataset</b><br /> After a dataset is finalized, the dataset is
							frozen at that time stamp and no one can change it. In order to
							make further changes to that dataset, you need to reopen the
							dataset. You can click the button 'Reopen this dataset' to reopen
							it. </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Approve
								all system categories</b><br /> To save your time of approving the
							decisions one by one, you may click the 'Approve all System
							Categories' button to copy those already approved categorization
							and synonyms decisions in OTO system. You can change them as you
							want after copied from the system. </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Merge
								Datasets</b><br /> <b style="color: black;">1. Merge
								unfinalized datasets</b><br /> Smaller unfinalized datasets can be
							merged together for better management. The operation is easy.
							Just select the datasets you want to merge, and type in a new
							name of the merged dataset, then click 'Merge Datasets' button.
							If the name you typed in already exists in OTO, you will be
							provided an option to merge selected datasets into this existing
							dataset. <br /> <b style="color: black;">2. Merge into
								system datasets</b><br /> Finalized datasets can be merged into OTO
							system in order to build a more comprehensive glossary. This can
							only be done by the administrators of OTO. <br></br>Notice:
							Curently, OTO only merge data of 'Group Terms' page. If the
							source dataset only has data in 'Group Terms' page, it will be
							removed after deleted from OTO permanently after being merged
							into a bigger dataset. </font></li>
				</ul>
			</td>
		</tr>
	</table>


	<a name="manageDatasets"></a>
	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">Instructions of <font
				color="purple">Manage Datasets</font> page
			</th>
		</tr>
		<tr>
			<td>
				<ul>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">What
								can be done in Manage Datasets page? </b><br /> <br />1. Create
							dataset<br />2. Set the privacy level of a dataset <br /> 3.
							Delete your dataset <br /> 4. Import your own
							terms/structures/orders tasks </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Create
								Datasets </b><br />You can create your own dataset by giving a
							dataset name prefix and selecting a glossary to which your
							dataset belongs. Curently OTO only support 5 glossaries: Plant,
							Hymenoptera, Porifera, Algea and Fossil. <br /> The name of
							newly created dataset will be a combination of your given name
							prefix, your first name, your last name and the timestamp when
							this dataset is created, all parts connected by underscore (_). <br />The
							newly created dataset is empty with no data in it. You can see
							the dataset listed on the right side of this page after
							successful creation and you may import your own data there. <br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Set
								privacy level of a dataset </b><br /> There are two privacy level
							in OTO: public and private. Public datasets can be selected and
							viewed by all registered users of OTO and any registered user of
							OTO can express their opinions in those datasets. Private
							datasets can only be selected and viewed by the owner/creater and
							only the owner/creater can express his/her opinions in them. <br />
							<br />The public datasets are indicated with sign <img
							src="images/public.png" height="13px"
							style="vertical-align: middle;" /> and private datasets have
							sign <img src="images/private.png" height="13px"
							style="vertical-align: middle;" /> before it. <br /> To change
							the privacy level of a dataset, click on the dataset name and you
							should be able to see the buttons next to the dataset name. Click
							on button <input type="button" value="Make Public"
							style="vertical-align: middle;"
							class="uiButton uiButtonSpecial uiButtonMedium actionbuttons" />
							or <input type="button" value="Make Private"
							style="vertical-align: middle;"
							class="uiButton uiButtonSpecial uiButtonMedium actionbuttons" />
							to change the privacy level.<br /></font></li>
					<li style="padding-bottom: 10px; vertical-align: bottom;"><font
						class="font-text-style"><b style="color: green">Delete
								your dataset </b><br />Click on the dataset name and you should be
							able to see the buttons next to the dataset name. Click on button
							<input type="button" value="Delete dataset"
							style="vertical-align: middle;"
							class="uiButton uiButtonSpecial uiButtonMedium actionbuttons" />
							to delete the dataset. <br /> Note: this action can not be
							recovered. Be sure you are deleting the correct dataset before
							you confirm the deleting. System reserved datasets can not be
							deleted. <br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Import
								terms for categorization </b><br /> Click on the dataset name, you
							should be able to see three tasks listed below the dataset:
							Categorization, Hierarchy and Orders. Click on the <input
							type="button" value='Import' class="uiButton uiButtonMedium"
							style="vertical-align: middle;" /> button next to
							Categorization, or <input type="button" value='Re-import'
							class="uiButton uiButtonMedium" style="vertical-align: middle;" />
							button if there are existing term in this dataset, to import
							terms for categorization. Currently OTO only support importing
							data from .CSV files.<br /> <br />Note:<br /> 1. Re-importing
							terms will remove all existing terms first. <br />2. If
							decisions have been made on existing terms, import function will
							be disabled. <br /> <br /> <a name="csvFormat_Terms" />Importing
							Format of 'Group Terms': <br /> <br />1. Only .CSV files can be
							imported. <br />2. The first column of the .CSV file is term
							name. The second column is the context sentence of the term. The
							sentence column can be empty. <br />3. The .CSV file should have
							no column name row. If you have a column name row, delete it
							before importing or the column name will be imported as a term. <br />4.
							No comma allowed in term's name. If comma exists, only the part
							before the comma will be imported as a term. The rest will be
							imported as part of the context sentence. <br />5. The .CSV file
							should look like this if opened as text. <br /> <img
							style="padding-left: 18px"
							src="images/screenshots/termsInText.png" /><br />6. The .CSV
							file should look like this if opened as spreadsheet. <br /> <img
							style="padding-left: 18px"
							src="images/screenshots/termsInExcel.png" /><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Import
								structures for hierarchical relations </b><br /> Click on the
							dataset name, you should be able to see three tasks listed below
							the dataset: Categorization, Hierarchy and Orders. Click on the <input
							type="button" value='Import' class="uiButton uiButtonMedium"
							style="vertical-align: middle;" /> button next to Hierarchy, or
							<input type="button" value='Re-import'
							class="uiButton uiButtonMedium" style="vertical-align: middle;" />
							button if there are existing structures in this dataset, to
							import structures for hierarchical relations. Currently OTO only
							support importing data from .CSV files.<br /> <br />Note:<br />
							1. Re-importing structures will remove all existing structures
							first. <br />2. If decisions have been made on existing
							structures, import function will be disabled. <br /> <br /> <a
							name="csvFormat_Structures" />Importing Format of 'Structure
							Hierarchy': <br /> <br />1. Only .CSV files can be imported. <br />2.
							The first column of the .CSV file is structure name. The second
							column is the context sentence of the structure. The sentence
							column can be empty. <br />3. The .CSV file should have no
							column name row. If you have a column name row, delete it before
							importing or the column name will be imported as a structure. <br />4.
							No comma allowed in structure's name. If comma exists in
							structure's name, only the part before the comma will be imported
							as a structure. The rest will be imported as part of the context
							sentence. <br />5. The .CSV file should look like this if opened
							as text. <br /> <img style="padding-left: 18px"
							src="images/screenshots/structuresInText.png" /><br />6. The
							.CSV file should look like this if opened as spreadsheet. <br />
							<img style="padding-left: 18px"
							src="images/screenshots/structuresInExcel.png" /><br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Import
								orders </b><br /> Click on the dataset name, you should be able to
							see three tasks listed below the dataset: Categorization,
							Hierarchy and Orders. Click on the <input type="button"
							value='Import' class="uiButton uiButtonMedium"
							style="vertical-align: middle;" /> button next to Orders, or <input
							type="button" value='Re-import' class="uiButton uiButtonMedium"
							style="vertical-align: middle;" /> button if there are existing
							orders in this dataset, to import orders tasks. Currently OTO
							only support importing data from .CSV files.<br /> <br />Note:<br />
							1. Re-importing orders will remove all existing orders first. <br />2.
							If decisions have been made on existing orders, import function
							will be disabled. <br /> <br /> <a name="csvFormat_Orders" />Importing
							Format of 'Term Order': <br /> <br />1. Only .CSV files can be
							imported. <br />2. Each row in the .CSV file will be imported as
							one order group. The first column will be imported as the order
							group name and all the other columns will be imported as terms in
							this order group. You are able to add orders or terms under this
							order group in Orders page after importing.<br />3. The .CSV
							file should have no column name row. If you have a column name
							row, delete it before importing or the column name will be
							imported as a order group. <br />4. No comma allowed in order
							group name and in term name. If comma exists, the column with
							comma in it will be imported as two separate columns. <br />5.
							The .CSV file should look like this if opened as text. <br /> <img
							style="padding-left: 18px"
							src="images/screenshots/ordersInText.png" /><br />6. The .CSV
							file should look like this if opened as spreadsheet. <br /> <img
							style="padding-left: 18px"
							src="images/screenshots/ordersInExcel.png" /><br /></font></li>
				</ul>
			</td>
		</tr>
	</table>



	<a name="catDef"></a>
	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">Definitions of
				categories in <font color="purple">Group Terms</font> page
			</th>
		</tr>
		<tr>
			<td style="padding-left: 40px;">
				<table>
					<tr>
						<th width="15%" align="left"
							style="border-bottom: 1px solid green">Category Name</th>
						<th width="85%" align="left"
							style="border-bottom: 1px solid green">Definition</th>
					</tr>
					<%
						CharacterDBAccess cdba = new CharacterDBAccess();
							ArrayList<CategoryBean> allCategories = cdba
									.getCategoryDefinitions();
							for (CategoryBean cb : allCategories) {
					%>
					<tr>
						<td width="15%" style="border-bottom: 1px dotted gray"><font
							class="font-text-style"><%=cb.getName()%></font></td>
						<td width="85%" style="border-bottom: 1px dotted gray"><font
							class="font-text-style"><%=cb.getDef()%></font></td>
					</tr>
					<%
						}
					%>
				</table>
			</td>
		</tr>
	</table>

	<a name="updates"></a>
	<table style="border-top: 1px solid green" width="100%">
		<tr>
			<th align="left" style="padding-left: 5px;">Updates</th>
		</tr>
		<tr>
			<td>
				<ul>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on June 30, 2014</b><br /> 1. Make order name editable<br /> 2.
							Allow user to create their own datasets and import terms and
							context sentences. <br /> 3. Dataset can be private or public.<br />4.
							Remove dragging arrow in categorization page. <br /> 5. Allow
							users to reset OTO_Demo to initial status. </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on September 24, 2013</b><br /> 1. Auto-scroll in categories<br />
							2. Only super user can modify system-reserved datasets. <br />
							3. Show "discarded" for uncategorization decisions in admin page</font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on July 29, 2013</b><br /> 1. Download page replaced by Web Service<br />
							2. Commit glossaries files to Git: <a
							href="https://github.com/biosemantics/glossaries" target="_blank">https://github.com/biosemantics/glossaries</a>
							<br /> 3. Add meta data in glossary files</font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on July 16, 2013</b><br /> 1. Merge unfinalized dataset / Merge
							into System datasets<br /> 2. Copy system decisions in 'Group
							Terms' page<br /> 3. Approve all system categories in
							categorization management page<br /> 4. Update instructions<br />
							5. Approve synonyms <br /> </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on June 18, 2013</b><br /> 1. Merge into an existing dataset<br />
							2. Add source dataset and term ID for glossary downloading<br />
							3. Add user's log in user's report page<br /> 4. Add glossary
							versions<br /> 5. Copy accepted decisions from system<br /> 6.
							Use URL biosemantics.arizona.edu/OTO and redirect /ONTNEW to it.
							<br /> 7. Get category definitions through
							http://biosemantics.arizona.edu/OTO/rest/glossary/categories. <br />
					</font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on June 9, 2013</b><br /> 1. Delete source datasets after merge
							according to certain rules<br /> 2. Only datasets under the same
							glossary can be merged.<br /> </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on May 31, 2013</b><br /> 1. Ontology Lookup<br /> </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on Apr 29, 2013</b><br /> 1. fix bug: zip file<br /> 2. fix bug:
							missing some context<br /> </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on Apr 23, 2013</b><br /> 1. Password encryption updates<br /> 2.
							Structures displayed in uploaded datasets<br /> </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on Apr 7, 2013</b><br /> 1. Forgot your password?<br /> </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on Mar 31, 2013</b><br /> 1. Users have access to finalized
							datasets when merging datasets.<br /> 2. Fix bug: note too long
							when merging datasets.<br /> </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on Mar 14, 2013</b><br /> 1. Set owner for each dataset<br /> 2.
							Allow admin or dataset owner to delete dataset<br /> 3. Allow
							admin or dataset owner to reopen finalized dataset<br /> 4.
							Allow admin or dataset owner to merge datasets to a new dataset<br />
							5. Update user file when registering a new user<br /> 6. Split
							download to a new page<br /> </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on Nov 22, 2012</b><br /> 1. add downloading finalized term sets<br />
							2. add demo video to log in page<br /> </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on Nov 8, 2012</b><br /> 1. fix bug: special character in comments<br />
							2. fix bug: term's name with space or starts with underscore<br />
							3. sort saved terms alphabetically<br /> 4. separate confirmed
							decisions for each dataset<br /> </font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on Sep 14, 2012</b><br /> 1. Allow user to specify a term to
							locate: click the <img src="images/locator.png" height="14px">
								button to input term's name.<br /> 2. Session timeout alert: at
								50 minutes (10 minutes before timeout). <br /></font></li>
					<li style="padding-bottom: 10px;"><font
						class="font-text-style"><b style="color: green">Updates
								on Jul 10, 2012</b><br /> 1. Added auto-scroll for making synonyms<br />
							2. To force user to make comment when uncategorize a term<br />
							3. Added dataset information on each functional page<br /> 4.
							Added term's location tab at the bottom of 'Group Terms' page to
							list all copies of a term<br /> 5. To record reviewing actions
							and use grey color to mark reviewed terms and categories<br />
							6. Updated instructions for all updates </font></li>
				</ul>
			</td>
		</tr>
	</table>

	<%
		} else {
	%>

	<jsp:include page="loginHeader.jsp" />
	Your session has timed off. Please
	<a href="<%=request.getContextPath()%>">login</a>
	<%
		}
	%>
	<jsp:include page="footer.jsp" />
</body>
</html>
