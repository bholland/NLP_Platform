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
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Apr 09 16:41:13 EDT 2018
 * @generated */
public class PdfObject_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = PdfObject.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("objects.PdfObject");
 
  /** @generated */
  final Feature casFeat_PdfFile;
  /** @generated */
  final int     casFeatCode_PdfFile;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPdfFile(int addr) {
        if (featOkTst && casFeat_PdfFile == null)
      jcas.throwFeatMissing("PdfFile", "objects.PdfObject");
    return ll_cas.ll_getStringValue(addr, casFeatCode_PdfFile);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPdfFile(int addr, String v) {
        if (featOkTst && casFeat_PdfFile == null)
      jcas.throwFeatMissing("PdfFile", "objects.PdfObject");
    ll_cas.ll_setStringValue(addr, casFeatCode_PdfFile, v);}
    
  
 
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
      jcas.throwFeatMissing("IsModelData", "objects.PdfObject");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_IsModelData);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsModelData(int addr, boolean v) {
        if (featOkTst && casFeat_IsModelData == null)
      jcas.throwFeatMissing("IsModelData", "objects.PdfObject");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_IsModelData, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public PdfObject_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_PdfFile = jcas.getRequiredFeatureDE(casType, "PdfFile", "uima.cas.String", featOkTst);
    casFeatCode_PdfFile  = (null == casFeat_PdfFile) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_PdfFile).getCode();

 
    casFeat_IsModelData = jcas.getRequiredFeatureDE(casType, "IsModelData", "uima.cas.Boolean", featOkTst);
    casFeatCode_IsModelData  = (null == casFeat_IsModelData) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_IsModelData).getCode();

  }
}



    
