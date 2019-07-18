

   
/* Apache UIMA v3 - First created by JCasGen Mon Jun 24 16:57:54 EDT 2019 */

package objects;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;


import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.cas.BooleanArray;


/** 
 * Updated by JCasGen Mon Jun 24 16:57:54 EDT 2019
 * XML source: /home/ben/workspace/NLP_Stack/desc/annotators/SentenceSplittingAnnotator.xml
 * @generated */
public class Sentence extends Annotation {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "objects.Sentence";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Sentence.class);
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
   
  public final static String _FeatName_words = "words";
  public final static String _FeatName_pos_tags = "pos_tags";
  public final static String _FeatName_lemma_tags = "lemma_tags";
  public final static String _FeatName_text_string = "text_string";
  public final static String _FeatName_chunks = "chunks";
  public final static String _FeatName_names = "names";
  public final static String _FeatName_DocumentID = "DocumentID";
  public final static String _FeatName_IsName = "IsName";
  public final static String _FeatName_SentenceNumber = "SentenceNumber";
  public final static String _FeatName_sentence_id = "sentence_id";
  public final static String _FeatName_IsHospital = "IsHospital";
  public final static String _FeatName_IsPerson = "IsPerson";
  public final static String _FeatName_IsIllness = "IsIllness";
  public final static String _FeatName_IsRawSentence = "IsRawSentence";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_words = TypeSystemImpl.createCallSite(Sentence.class, "words");
  private final static MethodHandle _FH_words = _FC_words.dynamicInvoker();
  private final static CallSite _FC_pos_tags = TypeSystemImpl.createCallSite(Sentence.class, "pos_tags");
  private final static MethodHandle _FH_pos_tags = _FC_pos_tags.dynamicInvoker();
  private final static CallSite _FC_lemma_tags = TypeSystemImpl.createCallSite(Sentence.class, "lemma_tags");
  private final static MethodHandle _FH_lemma_tags = _FC_lemma_tags.dynamicInvoker();
  private final static CallSite _FC_text_string = TypeSystemImpl.createCallSite(Sentence.class, "text_string");
  private final static MethodHandle _FH_text_string = _FC_text_string.dynamicInvoker();
  private final static CallSite _FC_chunks = TypeSystemImpl.createCallSite(Sentence.class, "chunks");
  private final static MethodHandle _FH_chunks = _FC_chunks.dynamicInvoker();
  private final static CallSite _FC_names = TypeSystemImpl.createCallSite(Sentence.class, "names");
  private final static MethodHandle _FH_names = _FC_names.dynamicInvoker();
  private final static CallSite _FC_DocumentID = TypeSystemImpl.createCallSite(Sentence.class, "DocumentID");
  private final static MethodHandle _FH_DocumentID = _FC_DocumentID.dynamicInvoker();
  private final static CallSite _FC_IsName = TypeSystemImpl.createCallSite(Sentence.class, "IsName");
  private final static MethodHandle _FH_IsName = _FC_IsName.dynamicInvoker();
  private final static CallSite _FC_SentenceNumber = TypeSystemImpl.createCallSite(Sentence.class, "SentenceNumber");
  private final static MethodHandle _FH_SentenceNumber = _FC_SentenceNumber.dynamicInvoker();
  private final static CallSite _FC_sentence_id = TypeSystemImpl.createCallSite(Sentence.class, "sentence_id");
  private final static MethodHandle _FH_sentence_id = _FC_sentence_id.dynamicInvoker();
  private final static CallSite _FC_IsHospital = TypeSystemImpl.createCallSite(Sentence.class, "IsHospital");
  private final static MethodHandle _FH_IsHospital = _FC_IsHospital.dynamicInvoker();
  private final static CallSite _FC_IsPerson = TypeSystemImpl.createCallSite(Sentence.class, "IsPerson");
  private final static MethodHandle _FH_IsPerson = _FC_IsPerson.dynamicInvoker();
  private final static CallSite _FC_IsIllness = TypeSystemImpl.createCallSite(Sentence.class, "IsIllness");
  private final static MethodHandle _FH_IsIllness = _FC_IsIllness.dynamicInvoker();
  private final static CallSite _FC_IsRawSentence = TypeSystemImpl.createCallSite(Sentence.class, "IsRawSentence");
  private final static MethodHandle _FH_IsRawSentence = _FC_IsRawSentence.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected Sentence() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public Sentence(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Sentence(JCas jcas) {
    super(jcas);
    readObject();   
  } 


  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Sentence(JCas jcas, int begin, int end) {
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
  //* Feature: words

  /** getter for words - gets An array of each word in the sentence.
   * @generated
   * @return value of the feature 
   */
  public StringArray getWords() { return (StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_words)));}
    
  /** setter for words - sets An array of each word in the sentence. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setWords(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_words), v);
  }    
    
    
  /** indexed getter for words - gets an indexed value - An array of each word in the sentence.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getWords(int i) {
     return ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_words)))).get(i);} 

  /** indexed setter for words - sets an indexed value - An array of each word in the sentence.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setWords(int i, String v) {
    ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_words)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: pos_tags

  /** getter for pos_tags - gets POS tags for each associated word.
   * @generated
   * @return value of the feature 
   */
  public StringArray getPos_tags() { return (StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_pos_tags)));}
    
  /** setter for pos_tags - sets POS tags for each associated word. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPos_tags(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_pos_tags), v);
  }    
    
    
  /** indexed getter for pos_tags - gets an indexed value - POS tags for each associated word.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getPos_tags(int i) {
     return ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_pos_tags)))).get(i);} 

  /** indexed setter for pos_tags - sets an indexed value - POS tags for each associated word.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setPos_tags(int i, String v) {
    ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_pos_tags)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: lemma_tags

  /** getter for lemma_tags - gets Lemmintized words
   * @generated
   * @return value of the feature 
   */
  public StringArray getLemma_tags() { return (StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_lemma_tags)));}
    
  /** setter for lemma_tags - sets Lemmintized words 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLemma_tags(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_lemma_tags), v);
  }    
    
    
  /** indexed getter for lemma_tags - gets an indexed value - Lemmintized words
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getLemma_tags(int i) {
     return ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_lemma_tags)))).get(i);} 

  /** indexed setter for lemma_tags - sets an indexed value - Lemmintized words
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setLemma_tags(int i, String v) {
    ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_lemma_tags)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: text_string

  /** getter for text_string - gets 
   * @generated
   * @return value of the feature 
   */
  public String getText_string() { return _getStringValueNc(wrapGetIntCatchException(_FH_text_string));}
    
  /** setter for text_string - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setText_string(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_text_string), v);
  }    
    
   
    
  //*--------------*
  //* Feature: chunks

  /** getter for chunks - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getChunks() { return (StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_chunks)));}
    
  /** setter for chunks - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setChunks(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_chunks), v);
  }    
    
    
  /** indexed getter for chunks - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getChunks(int i) {
     return ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_chunks)))).get(i);} 

  /** indexed setter for chunks - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setChunks(int i, String v) {
    ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_chunks)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: names

  /** getter for names - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getNames() { return (StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_names)));}
    
  /** setter for names - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNames(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_names), v);
  }    
    
    
  /** indexed getter for names - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getNames(int i) {
     return ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_names)))).get(i);} 

  /** indexed setter for names - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setNames(int i, String v) {
    ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_names)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: DocumentID

  /** getter for DocumentID - gets 
   * @generated
   * @return value of the feature 
   */
  public int getDocumentID() { return _getIntValueNc(wrapGetIntCatchException(_FH_DocumentID));}
    
  /** setter for DocumentID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentID(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_DocumentID), v);
  }    
    
   
    
  //*--------------*
  //* Feature: IsName

  /** getter for IsName - gets An array of boolean values where true means this word is a name.
   * @generated
   * @return value of the feature 
   */
  public BooleanArray getIsName() { return (BooleanArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IsName)));}
    
  /** setter for IsName - sets An array of boolean values where true means this word is a name. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsName(BooleanArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_IsName), v);
  }    
    
    
  /** indexed getter for IsName - gets an indexed value - An array of boolean values where true means this word is a name.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public boolean getIsName(int i) {
     return ((BooleanArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IsName)))).get(i);} 

  /** indexed setter for IsName - sets an indexed value - An array of boolean values where true means this word is a name.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setIsName(int i, boolean v) {
    ((BooleanArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IsName)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: SentenceNumber

  /** getter for SentenceNumber - gets The sentence number within the document.
   * @generated
   * @return value of the feature 
   */
  public int getSentenceNumber() { return _getIntValueNc(wrapGetIntCatchException(_FH_SentenceNumber));}
    
  /** setter for SentenceNumber - sets The sentence number within the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSentenceNumber(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_SentenceNumber), v);
  }    
    
   
    
  //*--------------*
  //* Feature: sentence_id

  /** getter for sentence_id - gets Sentence ID from the database
   * @generated
   * @return value of the feature 
   */
  public int getSentence_id() { return _getIntValueNc(wrapGetIntCatchException(_FH_sentence_id));}
    
  /** setter for sentence_id - sets Sentence ID from the database 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSentence_id(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_sentence_id), v);
  }    
    
   
    
  //*--------------*
  //* Feature: IsHospital

  /** getter for IsHospital - gets 
   * @generated
   * @return value of the feature 
   */
  public BooleanArray getIsHospital() { return (BooleanArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IsHospital)));}
    
  /** setter for IsHospital - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsHospital(BooleanArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_IsHospital), v);
  }    
    
    
  /** indexed getter for IsHospital - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public boolean getIsHospital(int i) {
     return ((BooleanArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IsHospital)))).get(i);} 

  /** indexed setter for IsHospital - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setIsHospital(int i, boolean v) {
    ((BooleanArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IsHospital)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: IsPerson

  /** getter for IsPerson - gets 
   * @generated
   * @return value of the feature 
   */
  public BooleanArray getIsPerson() { return (BooleanArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IsPerson)));}
    
  /** setter for IsPerson - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsPerson(BooleanArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_IsPerson), v);
  }    
    
    
  /** indexed getter for IsPerson - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public boolean getIsPerson(int i) {
     return ((BooleanArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IsPerson)))).get(i);} 

  /** indexed setter for IsPerson - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setIsPerson(int i, boolean v) {
    ((BooleanArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IsPerson)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: IsIllness

  /** getter for IsIllness - gets 
   * @generated
   * @return value of the feature 
   */
  public BooleanArray getIsIllness() { return (BooleanArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IsIllness)));}
    
  /** setter for IsIllness - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsIllness(BooleanArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_IsIllness), v);
  }    
    
    
  /** indexed getter for IsIllness - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public boolean getIsIllness(int i) {
     return ((BooleanArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IsIllness)))).get(i);} 

  /** indexed setter for IsIllness - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setIsIllness(int i, boolean v) {
    ((BooleanArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IsIllness)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: IsRawSentence

  /** getter for IsRawSentence - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getIsRawSentence() { return _getBooleanValueNc(wrapGetIntCatchException(_FH_IsRawSentence));}
    
  /** setter for IsRawSentence - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsRawSentence(boolean v) {
    _setBooleanValueNfc(wrapGetIntCatchException(_FH_IsRawSentence), v);
  }    
    
  }

    