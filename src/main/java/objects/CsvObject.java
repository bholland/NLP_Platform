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

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Apr 09 16:41:13 EDT 2018
 * XML source: /home/ben/workspace/opennlp_processing/desc/ReaderObjects/ReaderAnnotator.xml
 * @generated */
public class CsvObject extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CsvObject.class);
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
  protected CsvObject() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CsvObject(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CsvObject(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public CsvObject(JCas jcas, int begin, int end) {
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
  //* Feature: CsvFile

  /** getter for CsvFile - gets The CSV file.
   * @generated
   * @return value of the feature 
   */
  public String getCsvFile() {
    if (CsvObject_Type.featOkTst && ((CsvObject_Type)jcasType).casFeat_CsvFile == null)
      jcasType.jcas.throwFeatMissing("CsvFile", "objects.CsvObject");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CsvObject_Type)jcasType).casFeatCode_CsvFile);}
    
  /** setter for CsvFile - sets The CSV file. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCsvFile(String v) {
    if (CsvObject_Type.featOkTst && ((CsvObject_Type)jcasType).casFeat_CsvFile == null)
      jcasType.jcas.throwFeatMissing("CsvFile", "objects.CsvObject");
    jcasType.ll_cas.ll_setStringValue(addr, ((CsvObject_Type)jcasType).casFeatCode_CsvFile, v);}    
   
    
  //*--------------*
  //* Feature: CsvIdColumn

  /** getter for CsvIdColumn - gets The ID column for the CSV.
   * @generated
   * @return value of the feature 
   */
  public String getCsvIdColumn() {
    if (CsvObject_Type.featOkTst && ((CsvObject_Type)jcasType).casFeat_CsvIdColumn == null)
      jcasType.jcas.throwFeatMissing("CsvIdColumn", "objects.CsvObject");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CsvObject_Type)jcasType).casFeatCode_CsvIdColumn);}
    
  /** setter for CsvIdColumn - sets The ID column for the CSV. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCsvIdColumn(String v) {
    if (CsvObject_Type.featOkTst && ((CsvObject_Type)jcasType).casFeat_CsvIdColumn == null)
      jcasType.jcas.throwFeatMissing("CsvIdColumn", "objects.CsvObject");
    jcasType.ll_cas.ll_setStringValue(addr, ((CsvObject_Type)jcasType).casFeatCode_CsvIdColumn, v);}    
   
    
  //*--------------*
  //* Feature: CsvTextColumn

  /** getter for CsvTextColumn - gets The text column for the CSV file.
   * @generated
   * @return value of the feature 
   */
  public String getCsvTextColumn() {
    if (CsvObject_Type.featOkTst && ((CsvObject_Type)jcasType).casFeat_CsvTextColumn == null)
      jcasType.jcas.throwFeatMissing("CsvTextColumn", "objects.CsvObject");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CsvObject_Type)jcasType).casFeatCode_CsvTextColumn);}
    
  /** setter for CsvTextColumn - sets The text column for the CSV file. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCsvTextColumn(String v) {
    if (CsvObject_Type.featOkTst && ((CsvObject_Type)jcasType).casFeat_CsvTextColumn == null)
      jcasType.jcas.throwFeatMissing("CsvTextColumn", "objects.CsvObject");
    jcasType.ll_cas.ll_setStringValue(addr, ((CsvObject_Type)jcasType).casFeatCode_CsvTextColumn, v);}    
   
    
  //*--------------*
  //* Feature: IsModelData

  /** getter for IsModelData - gets If this is csv file with the column contains model data.
   * @generated
   * @return value of the feature 
   */
  public boolean getIsModelData() {
    if (CsvObject_Type.featOkTst && ((CsvObject_Type)jcasType).casFeat_IsModelData == null)
      jcasType.jcas.throwFeatMissing("IsModelData", "objects.CsvObject");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((CsvObject_Type)jcasType).casFeatCode_IsModelData);}
    
  /** setter for IsModelData - sets If this is csv file with the column contains model data. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsModelData(boolean v) {
    if (CsvObject_Type.featOkTst && ((CsvObject_Type)jcasType).casFeat_IsModelData == null)
      jcasType.jcas.throwFeatMissing("IsModelData", "objects.CsvObject");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((CsvObject_Type)jcasType).casFeatCode_IsModelData, v);}    
  }

    
