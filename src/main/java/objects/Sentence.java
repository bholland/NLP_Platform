/*******************************************************************************
 * Copyright (C) 2018 by Benedict M. Holland <benedict.m.holland@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/* First created by JCasGen Fri Sep 15 14:51:31 EDT 2017 */
package objects;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;


import org.apache.uima.jcas.cas.BooleanArray;


/** 
 * Updated by JCasGen Sun Oct 15 18:46:06 EDT 2017
 * XML source: /home/ben/workspace/opennlp_processing/desc/objects/Sentence.xml
 * @generated */
public class Sentence extends Annotation {
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
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Sentence() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Sentence(int addr, TOP_Type type) {
    super(addr, type);
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
  public StringArray getWords() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_words == null)
      jcasType.jcas.throwFeatMissing("words", "objects.Sentence");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_words)));}
    
  /** setter for words - sets An array of each word in the sentence. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setWords(StringArray v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_words == null)
      jcasType.jcas.throwFeatMissing("words", "objects.Sentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_words, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for words - gets an indexed value - An array of each word in the sentence. 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getWords(int i) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_words == null)
      jcasType.jcas.throwFeatMissing("words", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_words), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_words), i);}

  /** indexed setter for words - sets an indexed value - An array of each word in the sentence. 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setWords(int i, String v) { 
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_words == null)
      jcasType.jcas.throwFeatMissing("words", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_words), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_words), i, v);}
   
    
  //*--------------*
  //* Feature: pos_tags

  /** getter for pos_tags - gets POS tags for each associated word.
   * @generated
   * @return value of the feature 
   */
  public StringArray getPos_tags() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_pos_tags == null)
      jcasType.jcas.throwFeatMissing("pos_tags", "objects.Sentence");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_pos_tags)));}
    
  /** setter for pos_tags - sets POS tags for each associated word. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPos_tags(StringArray v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_pos_tags == null)
      jcasType.jcas.throwFeatMissing("pos_tags", "objects.Sentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_pos_tags, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for pos_tags - gets an indexed value - POS tags for each associated word. 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getPos_tags(int i) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_pos_tags == null)
      jcasType.jcas.throwFeatMissing("pos_tags", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_pos_tags), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_pos_tags), i);}

  /** indexed setter for pos_tags - sets an indexed value - POS tags for each associated word. 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setPos_tags(int i, String v) { 
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_pos_tags == null)
      jcasType.jcas.throwFeatMissing("pos_tags", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_pos_tags), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_pos_tags), i, v);}
   
    
  //*--------------*
  //* Feature: lemma_tags

  /** getter for lemma_tags - gets Lemmintized words
   * @generated
   * @return value of the feature 
   */
  public StringArray getLemma_tags() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_lemma_tags == null)
      jcasType.jcas.throwFeatMissing("lemma_tags", "objects.Sentence");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_lemma_tags)));}
    
  /** setter for lemma_tags - sets Lemmintized words 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLemma_tags(StringArray v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_lemma_tags == null)
      jcasType.jcas.throwFeatMissing("lemma_tags", "objects.Sentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_lemma_tags, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for lemma_tags - gets an indexed value - Lemmintized words
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getLemma_tags(int i) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_lemma_tags == null)
      jcasType.jcas.throwFeatMissing("lemma_tags", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_lemma_tags), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_lemma_tags), i);}

  /** indexed setter for lemma_tags - sets an indexed value - Lemmintized words
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setLemma_tags(int i, String v) { 
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_lemma_tags == null)
      jcasType.jcas.throwFeatMissing("lemma_tags", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_lemma_tags), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_lemma_tags), i, v);}
   
    
  //*--------------*
  //* Feature: text_string

  /** getter for text_string - gets 
   * @generated
   * @return value of the feature 
   */
  public String getText_string() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_text_string == null)
      jcasType.jcas.throwFeatMissing("text_string", "objects.Sentence");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Sentence_Type)jcasType).casFeatCode_text_string);}
    
  /** setter for text_string - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setText_string(String v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_text_string == null)
      jcasType.jcas.throwFeatMissing("text_string", "objects.Sentence");
    jcasType.ll_cas.ll_setStringValue(addr, ((Sentence_Type)jcasType).casFeatCode_text_string, v);}    
   
    
  //*--------------*
  //* Feature: chunks

  /** getter for chunks - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getChunks() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_chunks == null)
      jcasType.jcas.throwFeatMissing("chunks", "objects.Sentence");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_chunks)));}
    
  /** setter for chunks - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setChunks(StringArray v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_chunks == null)
      jcasType.jcas.throwFeatMissing("chunks", "objects.Sentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_chunks, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for chunks - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getChunks(int i) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_chunks == null)
      jcasType.jcas.throwFeatMissing("chunks", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_chunks), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_chunks), i);}

  /** indexed setter for chunks - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setChunks(int i, String v) { 
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_chunks == null)
      jcasType.jcas.throwFeatMissing("chunks", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_chunks), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_chunks), i, v);}
   
    
  //*--------------*
  //* Feature: names

  /** getter for names - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getNames() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_names == null)
      jcasType.jcas.throwFeatMissing("names", "objects.Sentence");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_names)));}
    
  /** setter for names - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNames(StringArray v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_names == null)
      jcasType.jcas.throwFeatMissing("names", "objects.Sentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_names, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for names - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getNames(int i) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_names == null)
      jcasType.jcas.throwFeatMissing("names", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_names), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_names), i);}

  /** indexed setter for names - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setNames(int i, String v) { 
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_names == null)
      jcasType.jcas.throwFeatMissing("names", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_names), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_names), i, v);}
   
    
  //*--------------*
  //* Feature: DocumentID

  /** getter for DocumentID - gets 
   * @generated
   * @return value of the feature 
   */
  public int getDocumentID() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_DocumentID == null)
      jcasType.jcas.throwFeatMissing("DocumentID", "objects.Sentence");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Sentence_Type)jcasType).casFeatCode_DocumentID);}
    
  /** setter for DocumentID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentID(int v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_DocumentID == null)
      jcasType.jcas.throwFeatMissing("DocumentID", "objects.Sentence");
    jcasType.ll_cas.ll_setIntValue(addr, ((Sentence_Type)jcasType).casFeatCode_DocumentID, v);}    
   
    
  //*--------------*
  //* Feature: IsName

  /** getter for IsName - gets An array of boolean values where true means this word is a name.
   * @generated
   * @return value of the feature 
   */
  public BooleanArray getIsName() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsName == null)
      jcasType.jcas.throwFeatMissing("IsName", "objects.Sentence");
    return (BooleanArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsName)));}
    
  /** setter for IsName - sets An array of boolean values where true means this word is a name. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsName(BooleanArray v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsName == null)
      jcasType.jcas.throwFeatMissing("IsName", "objects.Sentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsName, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for IsName - gets an indexed value - An array of boolean values where true means this word is a name.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public boolean getIsName(int i) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsName == null)
      jcasType.jcas.throwFeatMissing("IsName", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsName), i);
    return jcasType.ll_cas.ll_getBooleanArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsName), i);}

  /** indexed setter for IsName - sets an indexed value - An array of boolean values where true means this word is a name.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setIsName(int i, boolean v) { 
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsName == null)
      jcasType.jcas.throwFeatMissing("IsName", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsName), i);
    jcasType.ll_cas.ll_setBooleanArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsName), i, v);}
   
    
  //*--------------*
  //* Feature: SentenceNumber

  /** getter for SentenceNumber - gets The sentence number within the document.
   * @generated
   * @return value of the feature 
   */
  public int getSentenceNumber() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_SentenceNumber == null)
      jcasType.jcas.throwFeatMissing("SentenceNumber", "objects.Sentence");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Sentence_Type)jcasType).casFeatCode_SentenceNumber);}
    
  /** setter for SentenceNumber - sets The sentence number within the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSentenceNumber(int v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_SentenceNumber == null)
      jcasType.jcas.throwFeatMissing("SentenceNumber", "objects.Sentence");
    jcasType.ll_cas.ll_setIntValue(addr, ((Sentence_Type)jcasType).casFeatCode_SentenceNumber, v);}    
   
    
  //*--------------*
  //* Feature: sentence_id

  /** getter for sentence_id - gets Sentence ID from the database
   * @generated
   * @return value of the feature 
   */
  public int getSentence_id() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_sentence_id == null)
      jcasType.jcas.throwFeatMissing("sentence_id", "objects.Sentence");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Sentence_Type)jcasType).casFeatCode_sentence_id);}
    
  /** setter for sentence_id - sets Sentence ID from the database 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSentence_id(int v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_sentence_id == null)
      jcasType.jcas.throwFeatMissing("sentence_id", "objects.Sentence");
    jcasType.ll_cas.ll_setIntValue(addr, ((Sentence_Type)jcasType).casFeatCode_sentence_id, v);}    
   
    
  //*--------------*
  //* Feature: IsHospital

  /** getter for IsHospital - gets 
   * @generated
   * @return value of the feature 
   */
  public BooleanArray getIsHospital() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsHospital == null)
      jcasType.jcas.throwFeatMissing("IsHospital", "objects.Sentence");
    return (BooleanArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsHospital)));}
    
  /** setter for IsHospital - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsHospital(BooleanArray v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsHospital == null)
      jcasType.jcas.throwFeatMissing("IsHospital", "objects.Sentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsHospital, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for IsHospital - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public boolean getIsHospital(int i) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsHospital == null)
      jcasType.jcas.throwFeatMissing("IsHospital", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsHospital), i);
    return jcasType.ll_cas.ll_getBooleanArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsHospital), i);}

  /** indexed setter for IsHospital - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setIsHospital(int i, boolean v) { 
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsHospital == null)
      jcasType.jcas.throwFeatMissing("IsHospital", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsHospital), i);
    jcasType.ll_cas.ll_setBooleanArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsHospital), i, v);}
   
    
  //*--------------*
  //* Feature: IsPerson

  /** getter for IsPerson - gets 
   * @generated
   * @return value of the feature 
   */
  public BooleanArray getIsPerson() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsPerson == null)
      jcasType.jcas.throwFeatMissing("IsPerson", "objects.Sentence");
    return (BooleanArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsPerson)));}
    
  /** setter for IsPerson - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsPerson(BooleanArray v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsPerson == null)
      jcasType.jcas.throwFeatMissing("IsPerson", "objects.Sentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsPerson, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for IsPerson - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public boolean getIsPerson(int i) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsPerson == null)
      jcasType.jcas.throwFeatMissing("IsPerson", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsPerson), i);
    return jcasType.ll_cas.ll_getBooleanArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsPerson), i);}

  /** indexed setter for IsPerson - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setIsPerson(int i, boolean v) { 
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsPerson == null)
      jcasType.jcas.throwFeatMissing("IsPerson", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsPerson), i);
    jcasType.ll_cas.ll_setBooleanArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsPerson), i, v);}
   
    
  //*--------------*
  //* Feature: IsIllness

  /** getter for IsIllness - gets 
   * @generated
   * @return value of the feature 
   */
  public BooleanArray getIsIllness() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsIllness == null)
      jcasType.jcas.throwFeatMissing("IsIllness", "objects.Sentence");
    return (BooleanArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsIllness)));}
    
  /** setter for IsIllness - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsIllness(BooleanArray v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsIllness == null)
      jcasType.jcas.throwFeatMissing("IsIllness", "objects.Sentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsIllness, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for IsIllness - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public boolean getIsIllness(int i) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsIllness == null)
      jcasType.jcas.throwFeatMissing("IsIllness", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsIllness), i);
    return jcasType.ll_cas.ll_getBooleanArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsIllness), i);}

  /** indexed setter for IsIllness - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setIsIllness(int i, boolean v) { 
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_IsIllness == null)
      jcasType.jcas.throwFeatMissing("IsIllness", "objects.Sentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsIllness), i);
    jcasType.ll_cas.ll_setBooleanArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_IsIllness), i, v);}
  }

    
