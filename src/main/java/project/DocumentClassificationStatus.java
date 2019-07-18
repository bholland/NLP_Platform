package project;

public class DocumentClassificationStatus {
	private Integer mStatusId;
	private String mClassifcationText;
	private Integer mNextId;
	
	public DocumentClassificationStatus(Integer status_id, String classification_text, Integer next_id) {
		mStatusId = status_id;
		mClassifcationText = classification_text;
		mNextId = next_id;
	}
	
	public DocumentClassificationStatus() {
		mStatusId = null;
		mClassifcationText = null;
		mNextId = null;
	}

	public Integer getStatusId() {
		return mStatusId;
	}

	public void setStatusId(Integer status_id) {
		this.mStatusId = status_id;
	}

	public String getClassifcationText() {
		return mClassifcationText;
	}

	public void setClassifcationText(String classification_text) {
		this.mClassifcationText = classification_text;
	}

	public Integer getNextId() {
		return mNextId;
	}

	public void setNextId(Integer next_id) {
		this.mNextId = next_id;
	}
	
}
