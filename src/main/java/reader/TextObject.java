package reader;

public class TextObject {
    
    private String mId;
    private String mText;
    private String mPath;
    private Boolean mIsModelData;
    private Integer mDatabaseId;
    private String mCategory;
    
    public TextObject() {
        mId = null;
        mText = null;
        mIsModelData = null;
    }
    
    public TextObject(String id, String text, String path, Boolean isModelData, Integer database_id, String category) {
        mId = id;
        mText = text;
        mPath = path;
        mIsModelData = isModelData;
        mDatabaseId = database_id;
        mCategory = category;
    }

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		this.mId = id;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		this.mText = text;
	}

	public String getPath() {
		return mPath;
	}

	public void setPath(String path) {
		this.mPath = path;
	}

	public Boolean getIsModelData() {
		return mIsModelData;
	}

	public void setIsModelData(Boolean is_model_data) {
		this.mIsModelData = is_model_data;
	}

	public Integer getDatabaseId() {
		return mDatabaseId;
	}

	public void setDatabaseId(Integer database_id) {
		this.mDatabaseId = database_id;
	}

	public String getCategory() {
		return mCategory;
	}

	public void setCategory(String category) {
		this.mCategory = category;
	}
    
}
