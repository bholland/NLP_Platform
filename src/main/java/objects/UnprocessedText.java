

   
/* Apache UIMA v3 - First created by JCasGen Tue Dec 18 13:10:52 EST 2018 */

package objects;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;


import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Dec 18 13:10:52 EST 2018
 * XML source: /home/ben/workspace/NLP_Stack/desc/objects/UnprocessedText.xml
 * @generated */
public class UnprocessedText extends Annotation {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "objects.UnprocessedText";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(UnprocessedText.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
 
  /* *******************
   *   Feature Offsets *
   * *******************/ 
   
  public final static String _FeatName_RawTextString = "RawTextString";
  public final static String _FeatName_TextId = "TextId";
  public final static String _FeatName_NumTokens = "NumTokens";
  public final static String _FeatName_IsDocument = "IsDocument";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_RawTextString = TypeSystemImpl.createCallSite(UnprocessedText.class, "RawTextString");
  private final static MethodHandle _FH_RawTextString = _FC_RawTextString.dynamicInvoker();
  private final static CallSite _FC_TextId = TypeSystemImpl.createCallSite(UnprocessedText.class, "TextId");
  private final static MethodHandle _FH_TextId = _FC_TextId.dynamicInvoker();
  private final static CallSite _FC_NumTokens = TypeSystemImpl.createCallSite(UnprocessedText.class, "NumTokens");
  private final static MethodHandle _FH_NumTokens = _FC_NumTokens.dynamicInvoker();
  private final static CallSite _FC_IsDocument = TypeSystemImpl.createCallSite(UnprocessedText.class, "IsDocument");
  private final static MethodHandle _FH_IsDocument = _FC_IsDocument.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected UnprocessedText() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public UnprocessedText(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public UnprocessedText(JCas jcas) {
    super(jcas);
    readObject();   
  } 


  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public UnprocessedText(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: RawTextString

  /** getter for RawTextString - gets This is the raw text of the input document.
   * @generated
   * @return value of the feature 
   */
  public String getRawTextString() { return _getStringValueNc(wrapGetIntCatchException(_FH_RawTextString));}
    
  /** setter for RawTextString - sets This is the raw text of the input document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRawTextString(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_RawTextString), v);
  }    
    
   
    
  //*--------------*
  //* Feature: TextId

  /** getter for TextId - gets Text id from the database.
   * @generated
   * @return value of the feature 
   */
  public int getTextId() { return _getIntValueNc(wrapGetIntCatchException(_FH_TextId));}
    
  /** setter for TextId - sets Text id from the database. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTextId(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_TextId), v);
  }    
    
   
    
  //*--------------*
  //* Feature: NumTokens

  /** getter for NumTokens - gets 
   * @generated
   * @return value of the feature 
   */
  public int getNumTokens() { return _getIntValueNc(wrapGetIntCatchException(_FH_NumTokens));}
    
  /** setter for NumTokens - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNumTokens(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_NumTokens), v);
  }    
    
   
    
  //*--------------*
  //* Feature: IsDocument

  /** getter for IsDocument - gets Is this is a document to categorize or is this training material?
   * @generated
   * @return value of the feature 
   */
  public boolean getIsDocument() { return _getBooleanValueNc(wrapGetIntCatchException(_FH_IsDocument));}
    
  /** setter for IsDocument - sets Is this is a document to categorize or is this training material? 
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsDocument(boolean v) {
    _setBooleanValueNfc(wrapGetIntCatchException(_FH_IsDocument), v);
  }    
    
  }

    