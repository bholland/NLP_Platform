package multiuser;

public class MultiuserReviewDocument {
	
	private Integer mDocumentId;
	private Integer mUserId;
	private Integer mCategoryId;
	
	public MultiuserReviewDocument(Integer document_id, Integer user_id, Integer category_id) {
		mDocumentId = document_id;
		mUserId = user_id;
		mCategoryId = category_id;
	}
	
	public MultiuserReviewDocument() {
		mDocumentId = null;
		mUserId = null;
		mCategoryId = null;
	}
	
	public Integer getDocumentId() {
		return mDocumentId;
	}

	public void setDocumentId(Integer document_id) {
		this.mDocumentId = document_id;
	}

	public Integer getUserId() {
		return mUserId;
	}

	public void setUserId(Integer user_id) {
		this.mUserId = user_id;
	}

	public Integer getCategoryId() {
		return mCategoryId;
	}

	public void setCategoryId(Integer category_id) {
		this.mCategoryId = category_id;
	}	
	
}
