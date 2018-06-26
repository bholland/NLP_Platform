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
/* First created by JCasGen Fri Apr 06 14:47:16 EDT 2018 */
package objects;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Apr 10 14:52:37 EDT 2018
 * XML source: /home/ben/workspace/opennlp_processing/desc/objects/UnprocessedText.xml
 * @generated */
public class UnprocessedText extends Annotation {
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
 
  /** Never called.  Disable default constructor
   * @generated */
  protected UnprocessedText() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public UnprocessedText(int addr, TOP_Type type) {
    super(addr, type);
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
  public String getRawTextString() {
    if (UnprocessedText_Type.featOkTst && ((UnprocessedText_Type)jcasType).casFeat_RawTextString == null)
      jcasType.jcas.throwFeatMissing("RawTextString", "objects.UnprocessedText");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UnprocessedText_Type)jcasType).casFeatCode_RawTextString);}
    
  /** setter for RawTextString - sets This is the raw text of the input document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRawTextString(String v) {
    if (UnprocessedText_Type.featOkTst && ((UnprocessedText_Type)jcasType).casFeat_RawTextString == null)
      jcasType.jcas.throwFeatMissing("RawTextString", "objects.UnprocessedText");
    jcasType.ll_cas.ll_setStringValue(addr, ((UnprocessedText_Type)jcasType).casFeatCode_RawTextString, v);}    
   
    
  //*--------------*
  //* Feature: TextId

  /** getter for TextId - gets Text id from the database.
   * @generated
   * @return value of the feature 
   */
  public int getTextId() {
    if (UnprocessedText_Type.featOkTst && ((UnprocessedText_Type)jcasType).casFeat_TextId == null)
      jcasType.jcas.throwFeatMissing("TextId", "objects.UnprocessedText");
    return jcasType.ll_cas.ll_getIntValue(addr, ((UnprocessedText_Type)jcasType).casFeatCode_TextId);}
    
  /** setter for TextId - sets Text id from the database. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTextId(int v) {
    if (UnprocessedText_Type.featOkTst && ((UnprocessedText_Type)jcasType).casFeat_TextId == null)
      jcasType.jcas.throwFeatMissing("TextId", "objects.UnprocessedText");
    jcasType.ll_cas.ll_setIntValue(addr, ((UnprocessedText_Type)jcasType).casFeatCode_TextId, v);}    
   
    
  //*--------------*
  //* Feature: NumTokens

  /** getter for NumTokens - gets 
   * @generated
   * @return value of the feature 
   */
  public int getNumTokens() {
    if (UnprocessedText_Type.featOkTst && ((UnprocessedText_Type)jcasType).casFeat_NumTokens == null)
      jcasType.jcas.throwFeatMissing("NumTokens", "objects.UnprocessedText");
    return jcasType.ll_cas.ll_getIntValue(addr, ((UnprocessedText_Type)jcasType).casFeatCode_NumTokens);}
    
  /** setter for NumTokens - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNumTokens(int v) {
    if (UnprocessedText_Type.featOkTst && ((UnprocessedText_Type)jcasType).casFeat_NumTokens == null)
      jcasType.jcas.throwFeatMissing("NumTokens", "objects.UnprocessedText");
    jcasType.ll_cas.ll_setIntValue(addr, ((UnprocessedText_Type)jcasType).casFeatCode_NumTokens, v);}    
   
    
  //*--------------*
  //* Feature: IsSource

  /** getter for IsSource - gets Is this is a source or a category document?
   * @generated
   * @return value of the feature 
   */
  public boolean getIsSource() {
    if (UnprocessedText_Type.featOkTst && ((UnprocessedText_Type)jcasType).casFeat_IsSource == null)
      jcasType.jcas.throwFeatMissing("IsSource", "objects.UnprocessedText");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((UnprocessedText_Type)jcasType).casFeatCode_IsSource);}
    
  /** setter for IsSource - sets Is this is a source or a category document? 
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsSource(boolean v) {
    if (UnprocessedText_Type.featOkTst && ((UnprocessedText_Type)jcasType).casFeat_IsSource == null)
      jcasType.jcas.throwFeatMissing("IsSource", "objects.UnprocessedText");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((UnprocessedText_Type)jcasType).casFeatCode_IsSource, v);}    
  }

    
