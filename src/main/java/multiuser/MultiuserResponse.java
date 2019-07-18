package multiuser;

public class MultiuserResponse {
	
	private Integer mCheckoutDocumentId;
	private Integer mDocumentId;
	
	public MultiuserResponse(Integer checkout_document_id, Integer document_id) {
		mCheckoutDocumentId = checkout_document_id;
		mDocumentId = document_id;
	}
	
	public MultiuserResponse() {
		mCheckoutDocumentId = null;
		mDocumentId = null;
	}
	
	public Integer getCheckoutDocumentId() {
		return mCheckoutDocumentId;
	}

	public void setCheckoutDocumentId(Integer mCheckoutDocumentId) {
		this.mCheckoutDocumentId = mCheckoutDocumentId;
	}

	public Integer getDocumentId() {
		return mDocumentId;
	}

	public void setDocumentId(Integer mDocumentId) {
		this.mDocumentId = mDocumentId;
	}
	
	
}
