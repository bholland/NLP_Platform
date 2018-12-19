package reader;

public class TextObject {
    
    private String mId;
    private String mText;
    private Boolean mIsModelData;
    
    public TextObject() {
        mId = null;
        mText = null;
        mIsModelData = null;
    }
    
    public TextObject(String id, String text, Boolean isModelData) {
        mId = id;
        mText = text;
        mIsModelData = isModelData;
    }
    
    public void setId(String id) {
        mId = id;
    }

    public void setText(String text) {
        mText = text;
    }
    
    public void setIsModelData(Boolean isModelData) {
        mIsModelData = isModelData;
    }
    
    public String getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }
    
    public Boolean getIsModelData() {
        return mIsModelData;
    }
}
