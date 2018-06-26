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
/* First created by JCasGen Fri Apr 06 15:05:52 EDT 2018 */
package objects;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Apr 09 16:41:13 EDT 2018
 * XML source: /home/ben/workspace/opennlp_processing/desc/ReaderObjects/ReaderAnnotator.xml
 * @generated */
public class PdfObject extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PdfObject.class);
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
  protected PdfObject() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public PdfObject(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public PdfObject(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public PdfObject(JCas jcas, int begin, int end) {
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
  //* Feature: PdfFile

  /** getter for PdfFile - gets The CSV file.
   * @generated
   * @return value of the feature 
   */
  public String getPdfFile() {
    if (PdfObject_Type.featOkTst && ((PdfObject_Type)jcasType).casFeat_PdfFile == null)
      jcasType.jcas.throwFeatMissing("PdfFile", "objects.PdfObject");
    return jcasType.ll_cas.ll_getStringValue(addr, ((PdfObject_Type)jcasType).casFeatCode_PdfFile);}
    
  /** setter for PdfFile - sets The CSV file. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPdfFile(String v) {
    if (PdfObject_Type.featOkTst && ((PdfObject_Type)jcasType).casFeat_PdfFile == null)
      jcasType.jcas.throwFeatMissing("PdfFile", "objects.PdfObject");
    jcasType.ll_cas.ll_setStringValue(addr, ((PdfObject_Type)jcasType).casFeatCode_PdfFile, v);}    
   
    
  //*--------------*
  //* Feature: IsModelData

  /** getter for IsModelData - gets If this is csv file with the column contains model data.
   * @generated
   * @return value of the feature 
   */
  public boolean getIsModelData() {
    if (PdfObject_Type.featOkTst && ((PdfObject_Type)jcasType).casFeat_IsModelData == null)
      jcasType.jcas.throwFeatMissing("IsModelData", "objects.PdfObject");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((PdfObject_Type)jcasType).casFeatCode_IsModelData);}
    
  /** setter for IsModelData - sets If this is csv file with the column contains model data. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsModelData(boolean v) {
    if (PdfObject_Type.featOkTst && ((PdfObject_Type)jcasType).casFeat_IsModelData == null)
      jcasType.jcas.throwFeatMissing("IsModelData", "objects.PdfObject");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((PdfObject_Type)jcasType).casFeatCode_IsModelData, v);}    
  }

    
