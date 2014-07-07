package edu.arizona.biosemantics.oto.oto.db;

/**
 * @author Partha Pratim Sanyal
 */
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import edu.arizona.biosemantics.oto.oto.beans.CommentBean;
import edu.arizona.biosemantics.oto.oto.beans.DecisionBean;
import edu.arizona.biosemantics.oto.oto.beans.SpecificReportBean;
import edu.arizona.biosemantics.oto.oto.beans.SynBean;
import edu.arizona.biosemantics.oto.oto.beans.Term;
import edu.arizona.biosemantics.oto.oto.beans.User;

/**
 * This class will handle all the report specific database queries
 * 
 * @author Partha
 * 
 */
public class ReportingDBAccess extends DatabaseAccess {

	public ReportingDBAccess() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final Logger LOGGER = Logger
			.getLogger(ReportingDBAccess.class);

	/**
	 * This method create a report specific for the hierarchy tree page.
	 * 
	 * @param tagID
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public ArrayList<SpecificReportBean> getTagSpecificReport(String tagID,
			String dataset) throws Exception {
		ArrayList<SpecificReportBean> tagReports = new ArrayList<SpecificReportBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "select u.userid, u.firstname, u.lastname from users u where u.userid in ("
				+ "select distinct a.userid from "
				+ dataset
				+ "_user_tags_decisions a where "
				+ "a.tagID = ? union select distinct b.userid from "
				+ dataset
				+ "_comments b where b.tagID =?)" + " order by u.firstname";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, tagID);
			pstmt.setString(2, tagID);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				User user = new User();
				user.setUserId(rset.getInt("userid"));
				user.setFirstName(rset.getString("firstname"));
				user.setLastName(rset.getString("lastname"));
				tagReports.add(new SpecificReportBean(tagID, user, dataset, 2));
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("couldn't generate specific report for tag", exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return tagReports;
	}

	public ArrayList<SpecificReportBean> getOrderSpecificReport(String orderID,
			String dataset) throws Exception {
		ArrayList<SpecificReportBean> orderReports = new ArrayList<SpecificReportBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "select u.userid, u.firstname, u.lastname from users u where u.userid in ("
				+ "select distinct a.userid from "
				+ dataset
				+ "_user_orders_decisions a where "
				+ "a.orderID = ? and isTerm = ? union select distinct b.userid from "
				+ dataset
				+ "_comments b where b.orderID =?)"
				+ " order by u.firstname";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, orderID);
			pstmt.setBoolean(2, false);
			pstmt.setString(3, orderID);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				User user = new User();
				user.setUserId(rset.getInt("userid"));
				user.setFirstName(rset.getString("firstname"));
				user.setLastName(rset.getString("lastname"));
				orderReports.add(new SpecificReportBean(orderID, user, dataset,
						3));
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("couldnt generate termspecific report", exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return orderReports;
	}

	/**
	 * This method create a report specific for the categorizing page.
	 * 
	 * @param term
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public ArrayList<SpecificReportBean> getTermSpecificReport(String termName,
			String dataset) throws Exception {

		ArrayList<SpecificReportBean> termReports = new ArrayList<SpecificReportBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "select u.userid, u.firstname, u.lastname from users u where u.userid in ("
				+ "select distinct a.userid from "
				+ dataset
				+ "_user_terms_decisions a where "
				+ "a.term = ? union select distinct b.userid from "
				+ dataset
				+ "_comments b where b.term =? union select distinct c.userid from " + dataset + 
				"_review_history c where c.term = ?)" + " order by u.firstname";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, termName);
			pstmt.setString(2, termName);
			pstmt.setString(3, termName);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				User user = new User();
				user.setUserId(rset.getInt("userid"));
				user.setFirstName(rset.getString("firstname"));
				user.setLastName(rset.getString("lastname"));
				termReports.add(new SpecificReportBean(termName, user, dataset,
						1));
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("couldnt generate termspecific report", exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return termReports;
	}

	/**
	 * This method pulls all the user specific comments of a term
	 * 
	 * @param commentBean
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public ArrayList<CommentBean> getUserComments(CommentBean commentBean,
			String dataset, int type, String idOrName) throws Exception {

		ArrayList<CommentBean> comments = new ArrayList<CommentBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try {			
			conn = getConnection();
			//get comments
			String sql = "select comments, commentDate from " + dataset + "_comments "
					+ "where userid = ? and ";
			if (type == 1) {
				sql += "term = ?";
			} else if (type == 2) {
				sql += "tagID = ?";
			} else {
				sql += "orderID = ?";
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, commentBean.getUser().getUserId());
			pstmt.setString(2, idOrName);
			rset = pstmt.executeQuery();
			if (rset != null) {
				while (rset.next()) {
					CommentBean bean = new CommentBean(commentBean.getUser(),
							rset.getString("comments"));
					bean.setCommentDate(rset.getDate("commentDate"));
					comments.add(bean);
				}
			}
			
			//get reviewed history for categorizing page
			if (type == 1) {
				sql = "select term, reviewTime from " + dataset + "_review_history " +
						" where userid = ? and term = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, commentBean.getUser().getUserId());
				pstmt.setString(2, idOrName);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					CommentBean bean = new CommentBean(commentBean.getUser(), "Last reviewed on ");
					bean.setCommentDate(rset.getDate("reviewTime"));
					bean.setIsReviewComment(true);
					comments.add(bean);
				}
			}

		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Unable to retrieve comments ", exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return comments;
	}

	/**
	 * This method will get the synonyms list from fna_v19_user_terms_decisions
	 * 
	 * @param user
	 * @param termName
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public ArrayList<SynBean> getSynonyms(User user, String termName,
			String dataset) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		ArrayList<SynBean> syns = new ArrayList<SynBean>();
		try {
			conn = getConnection();
			String sql = "select decisiondate, relatedTerms from "
					+ dataset
					+ "_user_terms_decisions where userid = ? and term = ? and relatedTerms <> '' group by relatedTerms order by decisionid";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user.getUserId());
			pstmt.setString(2, termName);
			rset = pstmt.executeQuery();
			if (rset != null) {
				while (rset.next()) {
					SynBean syn = new SynBean(rset.getString("relatedTerms"));
					syn.setDecisionDate(new Date(rset.getDate("decisiondate")
							.getTime()));
					syns.add(syn);
				}
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Unable to retrieve synonyms from db: getSynonyms",
					exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return syns;
	}

	/**
	 * This method pull all the term decisions made by the user for a specific
	 * term
	 * 
	 * @param user
	 * @param term
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public ArrayList<DecisionBean> getUserDecisions(User user, String idOrName,
			String dataset, int type) throws Exception {
		ArrayList<DecisionBean> decisions = new ArrayList<DecisionBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			String sql;
			if (type == 1) {
				sql = "select decisiondate, decision, relatedTerms from "
						+ dataset
						+ "_user_terms_decisions where userid = ? and term = ? order by isActive, decisiondate";
			} else if (type == 2) {
				sql = "select decisionDate, pathWithName as decision from "
						+ dataset
						+ "_user_tags_decisions where userid = ? and tagID = ?";
			} else {
				sql = "select decisionDate, decision from "
						+ dataset
						+ "_user_orders_decisions where userid = ? and orderID = ? and isTerm = false";
			}
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, user.getUserId());
			pstmt.setString(2, idOrName);
			rset = pstmt.executeQuery();
			if (rset != null) {
				while (rset.next()) {
					DecisionBean dbean = new DecisionBean();
					dbean.setUser(user);
					dbean.setTerm(new Term(idOrName));
					dbean.setDecision(rset.getString(2));
					if (type == 1) {
						dbean.setSyns(rset.getString("relatedTerms"));
					}
					dbean.setDecisionDate(new Date(rset.getDate(1).getTime()));
					decisions.add(dbean);
				}
			}

		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Unable to retrieve decisions: ", exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return decisions;
	}
	
	/**
	 * get user's log regarding dataset operations
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public String getUserLog(User user) throws SQLException {
		String returnHTML = "";
		boolean hasRecord = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		String sql = "select * from users_log where userid = ? order by operateTime";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user.getUserId());
			rset = pstmt.executeQuery();
			
			int count = 1;
			boolean flag = true;
			String tdClass = "";
			returnHTML = "<table width='100%'><tr bgcolor='green'>"
					+ "<th color='white' width='2%'>#</th>"
					+ "<th width='30%'>Operation</th>"
					+ "<th width='38%'>Dataset</th>"
					+ "<th width='30%'>Date</th>"
					+ "</tr>";
			while (rset.next()) {
				hasRecord = true;
				tdClass = (flag) ? "d0" : "d1";
				flag = (flag) ? false : true;
				
				returnHTML += "<tr class='" + tdClass + "'>";
				returnHTML += "<td><font class='font-text-style'>" + count
						+ "</font></td>";
				returnHTML += "<td><font class='font-text-style'>" + rset.getString("operation")
						+ "</font></td>";
				returnHTML += "<td><font class='font-text-style'>" + rset.getString("dataset")
						+ "</font></td>";				
				returnHTML += "<td><font class='font-text-style'>"
						+ rset.getString("operateTime")
						+ "</font></td>";
				returnHTML += "</tr>";
				count++;
			}
			returnHTML += "</table>";

			if (!hasRecord) {
				returnHTML = "<font class='font-text-style'>No record from you. </font>";
			}

		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("unable to get report of term grouping", exe);

		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return returnHTML;
	}
 
	/**
	 * 
	 * @param user
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public String getOrderReport(User user, String dataset) throws SQLException {
		String returnHTML = "<table width='100%'>";
		String sql = "select d.orderID, d.decisionDate, w.name, w.base from "
				+ dataset
				+ "_user_orders_decisions d left join "
				+ dataset
				+ "_web_orders w on w.id = d.orderID where d.isTerm = false and d.userID = ? order by name";

		String sqlDetailOrder = "select * from "
				+ dataset
				+ "_user_orders_decisions "
				+ "where userID = ? and isTerm = true and orderID = ? order by distance";
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtOrder = null;
		ResultSet rset = null;
		ResultSet rsetOrder = null, rsetAccept = null;
		boolean hasRecord = false;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user.getUserId());
			rset = pstmt.executeQuery();
			SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
			while (rset.next()) {
				hasRecord = true;
				// make one order one box
				returnHTML += "<tr><td style='border: 1px solid grey'>" + //one order one line
						"<table width='100%'>";//one order one table
				returnHTML += "<tr><th align='left' style='background-color: #B0F1A0; width: 100%; padding: 0px'>"
						+ rset.getString("name")
						+ " <font style='font-weight: normal;'>( "
						+ format.format(rset.getDate("decisionDate"))
						+ " )</font></th></tr>"; // order title line
				// actual order part
				int orderID = rset.getInt("orderID");
				pstmtOrder = conn.prepareStatement(sqlDetailOrder);
				pstmtOrder.setInt(1, user.getUserId());
				pstmtOrder.setInt(2, orderID);
				rsetOrder = pstmtOrder.executeQuery();
				int lastPosition = -1000, currentPosition = -1000;
				boolean isFirst = true;
				returnHTML += "<tr><td style='padding: 0px'><table><tr>";
				while (rsetOrder.next()) {
					currentPosition = rsetOrder.getInt("distance");
					if (!isFirst && (lastPosition != currentPosition)) {
						returnHTML += "<td><div><font class='font-text-style'>-></font><div><div>&nbsp;<div></td>";
					}
					returnHTML += "<td><div style='border: 1px solid green' align='center'><font class='font-text-style'>"
							+ rsetOrder.getString("termName") + "</font></div>";
					if (rsetOrder.getBoolean("isBase")) {
						returnHTML += "<div><font class='font-text-style' style='color: grey'>Base</font></div>";
					} else {
						sql = "select * from " + dataset + "_confirmed_orders where orderID = ? and term = ? and accepted = ?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setInt(1, orderID);
						pstmt.setString(2, rsetOrder.getString("termName"));
						pstmt.setBoolean(3, true);
						rsetAccept = pstmt.executeQuery();
						boolean decisionMade = false, accepted = false;
						while (rsetAccept.next()) {
							decisionMade = true;
							int aceptedDistance = rsetAccept.getInt("distance");
							if ((currentPosition == 0 && aceptedDistance ==0) || (currentPosition * aceptedDistance > 0)) {
								accepted = true;
								returnHTML += "<div><font class='font-text-style' style='color: green'>Accepted</font></div>";
								break;
							}
						}
						
						if (!accepted) {
							if (decisionMade) {
								returnHTML += "<div><font class='font-text-style' style='color: purple;'>Good Suggestion</font></div>";
							} else {
								returnHTML += "<div><font class='font-text-style' style='color: black'>Pending</font></div>";	
							}
						}
					}
					returnHTML += "</td>";
					lastPosition = currentPosition;
					isFirst = false;
				}
				returnHTML += "</tr></table></td></tr></table></td></tr>";
			}
			returnHTML += "</table>";

			if (!hasRecord) {
				returnHTML = "<font class='font-text-style'>You have not made any decisions in ordering terms. </font>";
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("unable to get report of orders", exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return returnHTML;
	}

	/**
	 * get a String of HTML of hierarchy tree (set isForReport = true when call
	 * getHierarchyNode)
	 * 
	 * @param user
	 * @param dataset
	 * @return
	 * @throws Exception 
	 */
	public String getHierarchyTreeReport(User user, String dataset)
			throws Exception {
		return "<div class='dtree'>" + "<div class='clip'>"
				+ new CharacterDBAccess().getHierarchyNode(dataset, user, true)
				+ "</div></div>";
	}

	/**
	 * This method return a String of HTML, which will be used as responseText
	 * for the report page
	 * 
	 * @param user
	 * @param dataset
	 * @return
	 * @throws SQLException
	 * 
	 * 
	 */
	public String getUserGroupingReport(User user, String dataset)
			throws SQLException {
		String returnHTML = "";
		boolean hasRecord = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtTemp = null;
		ResultSet rset = null;
		ResultSet rset1 = null, rset2 = null;
		String sql = "select term, decision, decisiondate, relatedTerms from "
				+ dataset
				+ "_user_terms_decisions where userid=? and isActive = true order by term";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user.getUserId());
			rset = pstmt.executeQuery();
			int count = 1;
			boolean flag = true;
			String tdClass = "";
			SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
			returnHTML = "<table width='100%'><tr bgcolor='green'>"
					+ "<th color='white' width='2%'>#</th>"
					+ "<th width='20%'>Term</th>"
					+ "<th width='20%'>Category</th>"
					+ "<th width='25%'>Synonyms</th>"
					+ "<th width='15%'>Date</th>"
					+ "<th width='18%'>Acceptance Status</th></tr>";
			while (rset.next()) {
				hasRecord = true;
				tdClass = (flag) ? "d0" : "d1";
				flag = (flag) ? false : true;
				String term = rset.getString("term");
				String decision = rset.getString("decision");
				returnHTML += "<tr class='" + tdClass + "'>";
				returnHTML += "<td><font class='font-text-style'>" + count
						+ "</font></td>";
				returnHTML += "<td><font class='font-text-style'>" + term
						+ "</font></td>";
				returnHTML += "<td><font class='font-text-style'>" + decision
						+ "</font></td>";
				returnHTML += "<td><font class='font-text-style'>"
						+ rset.getString("relatedTerms") + "</font></td>";
				returnHTML += "<td><font class='font-text-style'>"
						+ format.format(rset.getDate("decisiondate"))
						+ "</font></td>";

				// get the Acceptance Status
				String sqlAcceptance = "select term, category from " + dataset + "_confirmed_category where term = ? "
						+ "and category = ? and categoryApproved = ?";
				pstmtTemp = conn.prepareStatement(sqlAcceptance);
				pstmtTemp.setString(1, term);
				pstmtTemp.setString(2, decision);
				pstmtTemp.setBoolean(3, true);
				rset1 = pstmtTemp.executeQuery();
				String acceptStatus = "";
				if (rset1.next()) {
					// Accepted
					acceptStatus = "<font class='font-text-style' color='green'>Accepted</font>";
					// returnHTML += "<td></td>";
				} else {
					// either pending or not Accepted
					sqlAcceptance = "select term, category from " + dataset + "_confirmed_category where term = ? and category <> ? and categoryApproved = ?";
					pstmt = conn.prepareStatement(sqlAcceptance);
					pstmt.setString(1, term);
					pstmt.setString(2, decision);
					pstmt.setBoolean(3, true);
					rset2 = pstmt.executeQuery();
					if (rset2.next()) {
						// has other decision
						acceptStatus = "<font class='font-text-style' color='purple'>Good suggestion.</font>";
						// returnHTML +=
						// "<td><img src='images/down.jpg' height='20px' /> <font class='font-text-style' color='red'>Declined</font></td>";
					} else {
						acceptStatus = "<font class='font-text-style'>Pending... </font>";
					}
				}
				returnHTML += "<td>" + acceptStatus + "</td>";
				returnHTML += "</tr>";
				count++;
			}
			returnHTML += "</table>";

			if (!hasRecord) {
				returnHTML = "<font class='font-text-style'>You have not made any decisions in grouping terms. </font>";
			}

		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("unable to get report of term grouping", exe);

		} finally {
			closeConnection(pstmt, rset, conn);
			rset1 = null;
			rset2 = null;
		}

		return returnHTML;
	}

	/**
	 * This method creates a user specific report. It describes all the
	 * decisions made by the current user.
	 * 
	 * @param user
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<DecisionBean> getUserSpecificReport(User user,
			String dataset) throws SQLException {
		ArrayList<DecisionBean> decisions = new ArrayList<DecisionBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtTemp = null;
		ResultSet rset = null;
		ResultSet rsetTemp = null;
		String sql = "select term, decision, decisiondate from "
				+ dataset
				+ "_user_terms_decisions where userid=? order by decisiondate desc";
		String sqlTemp = "select count(*) as _count from fnaglossary where term = ? and category = ? ";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user.getUserId());
			rset = pstmt.executeQuery();
			pstmtTemp = conn.prepareStatement(sqlTemp);

			while (rset.next()) {
				DecisionBean dbean = new DecisionBean();
				dbean.setTerm(new Term(rset.getString("term")));
				dbean.setDecision(rset.getString("decision"));
				dbean.setDecisionDate(new Date(rset.getDate("decisiondate")
						.getTime()));

				pstmtTemp.setString(1, dbean.getTerm().getTerm());
				pstmtTemp.setString(2, dbean.getDecision());

				rsetTemp = pstmtTemp.executeQuery();
				if (rsetTemp.next()) {
					if (rsetTemp.getInt("_count") > 0) {
						dbean.setAccepted(true);
					}
				}
				decisions.add(dbean);
			}

		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("unable to get decisions", exe);

		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return decisions;
	}

	/**
	 * This method pushes the user comments made on a specific term
	 * 
	 * @param cbean
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public boolean insertComment(CommentBean cbean, String dataset, String type)
			throws Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean returnValue = false;
		String sql = "insert into " + dataset + "_comments(comments, ";
		if (type.equals("1")) {
			sql += "term, ";
		} else if (type.equals("2")) {
			sql += "tagID, ";
		} else
			sql += "orderID, ";

		sql += "userid, commentdate) values(?,?,?,sysdate())";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cbean.getComments());
			pstmt.setString(2, cbean.getIdOrName());
			pstmt.setInt(3, cbean.getUser().getUserId());
			returnValue = pstmt.execute();
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("unable to save comment", exe);
		} finally {
			closeConnection(pstmt, conn);
		}
		return returnValue;
	}
}
