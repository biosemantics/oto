package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * This will store the comments from the user
 * @author Partha
 *
 */
public class CommentBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3615461122515809841L;
	private int commentId;
	private User user;
	private String idOrName;
	private String comments;
	private Date commentDate;
	private boolean isReviewComment = false;
	
	public CommentBean(User user, String comments) {
		super();
		this.user = user;
		this.comments = comments;
	}
	
	public CommentBean(int commentId, User user, String idOrName, String comments) {
		super();
		this.commentId = commentId;
		this.user = user;
		this.idOrName = idOrName;
		this.comments = comments;
	}
	
	/**
	 * @return the commentId
	 */
	public int getCommentId() {
		return commentId;
	}

	public CommentBean(User user, String idOrName, String comments) {
		super();
		this.user = user;
		this.idOrName = idOrName;
		this.comments = comments;
	}

	public CommentBean(User user) {
		super();
		this.user = user;
	}

	/**
	 * @param commentId the commentId to set
	 */
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	/**
	 * @return the userId
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * @return the term
	 */
	public String getIdOrName() {
		return idOrName;
	}
	/**
	 * @param term the term to set
	 */
	public void setIdOrName(String idOrName) {
		this.idOrName = idOrName;
	}
	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + commentId;
		result = prime * result
				+ ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((idOrName == null) ? 0 : idOrName.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CommentBean))
			return false;
		CommentBean other = (CommentBean) obj;
		if (commentId != other.commentId)
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (idOrName == null) {
			if (other.idOrName != null)
				return false;
		} else if (!idOrName.equals(other.idOrName))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	public Date getCommentDate() {
		return commentDate;
	}
	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}

	public boolean isReviewComment() {
		return isReviewComment;
	}

	public void setIsReviewComment(boolean isReviewComment) {
		this.isReviewComment = isReviewComment;
	}


}
