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
/* First created by JCasGen Fri Dec 22 15:33:48 EST 2017 */
package objects;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;


import org.apache.uima.jcas.cas.IntegerArray;


/** 
 * Updated by JCasGen Tue Feb 13 15:23:28 EST 2018
 * XML source: /home/ben/workspace/opennlp_processing/desc/objects/ModelObject.xml
 * @generated */
public class ModelObject extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ModelObject.class);
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
  protected ModelObject() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ModelObject(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ModelObject(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ModelObject(JCas jcas, int begin, int end) {
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
  //* Feature: TextArray

  /** getter for TextArray - gets This is an array of text.
   * @generated
   * @return value of the feature 
   */
  public StringArray getTextArray() {
    if (ModelObject_Type.featOkTst && ((ModelObject_Type)jcasType).casFeat_TextArray == null)
      jcasType.jcas.throwFeatMissing("TextArray", "objects.ModelObject");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ModelObject_Type)jcasType).casFeatCode_TextArray)));}
    
  /** setter for TextArray - sets This is an array of text. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTextArray(StringArray v) {
    if (ModelObject_Type.featOkTst && ((ModelObject_Type)jcasType).casFeat_TextArray == null)
      jcasType.jcas.throwFeatMissing("TextArray", "objects.ModelObject");
    jcasType.ll_cas.ll_setRefValue(addr, ((ModelObject_Type)jcasType).casFeatCode_TextArray, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for TextArray - gets an indexed value - This is an array of text. 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getTextArray(int i) {
    if (ModelObject_Type.featOkTst && ((ModelObject_Type)jcasType).casFeat_TextArray == null)
      jcasType.jcas.throwFeatMissing("TextArray", "objects.ModelObject");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ModelObject_Type)jcasType).casFeatCode_TextArray), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ModelObject_Type)jcasType).casFeatCode_TextArray), i);}

  /** indexed setter for TextArray - sets an indexed value - This is an array of text. 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setTextArray(int i, String v) { 
    if (ModelObject_Type.featOkTst && ((ModelObject_Type)jcasType).casFeat_TextArray == null)
      jcasType.jcas.throwFeatMissing("TextArray", "objects.ModelObject");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ModelObject_Type)jcasType).casFeatCode_TextArray), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ModelObject_Type)jcasType).casFeatCode_TextArray), i, v);}
   
    
  //*--------------*
  //* Feature: ModelFile

  /** getter for ModelFile - gets Use this model file to process data.
   * @generated
   * @return value of the feature 
   */
  public String getModelFile() {
    if (ModelObject_Type.featOkTst && ((ModelObject_Type)jcasType).casFeat_ModelFile == null)
      jcasType.jcas.throwFeatMissing("ModelFile", "objects.ModelObject");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ModelObject_Type)jcasType).casFeatCode_ModelFile);}
    
  /** setter for ModelFile - sets Use this model file to process data. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setModelFile(String v) {
    if (ModelObject_Type.featOkTst && ((ModelObject_Type)jcasType).casFeat_ModelFile == null)
      jcasType.jcas.throwFeatMissing("ModelFile", "objects.ModelObject");
    jcasType.ll_cas.ll_setStringValue(addr, ((ModelObject_Type)jcasType).casFeatCode_ModelFile, v);}    
   
    
  //*--------------*
  //* Feature: IdArray

  /** getter for IdArray - gets This is a corresponding id array. This exists because only primitives (no Entry<> types) are allowed within the xml.
   * @generated
   * @return value of the feature 
   */
  public IntegerArray getIdArray() {
    if (ModelObject_Type.featOkTst && ((ModelObject_Type)jcasType).casFeat_IdArray == null)
      jcasType.jcas.throwFeatMissing("IdArray", "objects.ModelObject");
    return (IntegerArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ModelObject_Type)jcasType).casFeatCode_IdArray)));}
    
  /** setter for IdArray - sets This is a corresponding id array. This exists because only primitives (no Entry<> types) are allowed within the xml. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setIdArray(IntegerArray v) {
    if (ModelObject_Type.featOkTst && ((ModelObject_Type)jcasType).casFeat_IdArray == null)
      jcasType.jcas.throwFeatMissing("IdArray", "objects.ModelObject");
    jcasType.ll_cas.ll_setRefValue(addr, ((ModelObject_Type)jcasType).casFeatCode_IdArray, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for IdArray - gets an indexed value - This is a corresponding id array. This exists because only primitives (no Entry<> types) are allowed within the xml. 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public int getIdArray(int i) {
    if (ModelObject_Type.featOkTst && ((ModelObject_Type)jcasType).casFeat_IdArray == null)
      jcasType.jcas.throwFeatMissing("IdArray", "objects.ModelObject");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ModelObject_Type)jcasType).casFeatCode_IdArray), i);
    return jcasType.ll_cas.ll_getIntArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ModelObject_Type)jcasType).casFeatCode_IdArray), i);}

  /** indexed setter for IdArray - sets an indexed value - This is a corresponding id array. This exists because only primitives (no Entry<> types) are allowed within the xml. 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setIdArray(int i, int v) { 
    if (ModelObject_Type.featOkTst && ((ModelObject_Type)jcasType).casFeat_IdArray == null)
      jcasType.jcas.throwFeatMissing("IdArray", "objects.ModelObject");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ModelObject_Type)jcasType).casFeatCode_IdArray), i);
    jcasType.ll_cas.ll_setIntArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ModelObject_Type)jcasType).casFeatCode_IdArray), i, v);}
  }

    
