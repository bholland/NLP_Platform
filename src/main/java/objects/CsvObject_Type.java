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
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Apr 09 16:41:13 EDT 2018
 * @generated */
public class CsvObject_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CsvObject.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("objects.CsvObject");
 
  /** @generated */
  final Feature casFeat_CsvFile;
  /** @generated */
  final int     casFeatCode_CsvFile;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCsvFile(int addr) {
        if (featOkTst && casFeat_CsvFile == null)
      jcas.throwFeatMissing("CsvFile", "objects.CsvObject");
    return ll_cas.ll_getStringValue(addr, casFeatCode_CsvFile);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCsvFile(int addr, String v) {
        if (featOkTst && casFeat_CsvFile == null)
      jcas.throwFeatMissing("CsvFile", "objects.CsvObject");
    ll_cas.ll_setStringValue(addr, casFeatCode_CsvFile, v);}
    
  
 
  /** @generated */
  final Feature casFeat_CsvIdColumn;
  /** @generated */
  final int     casFeatCode_CsvIdColumn;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCsvIdColumn(int addr) {
        if (featOkTst && casFeat_CsvIdColumn == null)
      jcas.throwFeatMissing("CsvIdColumn", "objects.CsvObject");
    return ll_cas.ll_getStringValue(addr, casFeatCode_CsvIdColumn);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCsvIdColumn(int addr, String v) {
        if (featOkTst && casFeat_CsvIdColumn == null)
      jcas.throwFeatMissing("CsvIdColumn", "objects.CsvObject");
    ll_cas.ll_setStringValue(addr, casFeatCode_CsvIdColumn, v);}
    
  
 
  /** @generated */
  final Feature casFeat_CsvTextColumn;
  /** @generated */
  final int     casFeatCode_CsvTextColumn;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCsvTextColumn(int addr) {
        if (featOkTst && casFeat_CsvTextColumn == null)
      jcas.throwFeatMissing("CsvTextColumn", "objects.CsvObject");
    return ll_cas.ll_getStringValue(addr, casFeatCode_CsvTextColumn);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCsvTextColumn(int addr, String v) {
        if (featOkTst && casFeat_CsvTextColumn == null)
      jcas.throwFeatMissing("CsvTextColumn", "objects.CsvObject");
    ll_cas.ll_setStringValue(addr, casFeatCode_CsvTextColumn, v);}
    
  
 
  /** @generated */
  final Feature casFeat_IsModelData;
  /** @generated */
  final int     casFeatCode_IsModelData;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIsModelData(int addr) {
        if (featOkTst && casFeat_IsModelData == null)
      jcas.throwFeatMissing("IsModelData", "objects.CsvObject");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_IsModelData);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsModelData(int addr, boolean v) {
        if (featOkTst && casFeat_IsModelData == null)
      jcas.throwFeatMissing("IsModelData", "objects.CsvObject");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_IsModelData, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CsvObject_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_CsvFile = jcas.getRequiredFeatureDE(casType, "CsvFile", "uima.cas.String", featOkTst);
    casFeatCode_CsvFile  = (null == casFeat_CsvFile) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_CsvFile).getCode();

 
    casFeat_CsvIdColumn = jcas.getRequiredFeatureDE(casType, "CsvIdColumn", "uima.cas.String", featOkTst);
    casFeatCode_CsvIdColumn  = (null == casFeat_CsvIdColumn) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_CsvIdColumn).getCode();

 
    casFeat_CsvTextColumn = jcas.getRequiredFeatureDE(casType, "CsvTextColumn", "uima.cas.String", featOkTst);
    casFeatCode_CsvTextColumn  = (null == casFeat_CsvTextColumn) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_CsvTextColumn).getCode();

 
    casFeat_IsModelData = jcas.getRequiredFeatureDE(casType, "IsModelData", "uima.cas.Boolean", featOkTst);
    casFeatCode_IsModelData  = (null == casFeat_IsModelData) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_IsModelData).getCode();

  }
}



    
