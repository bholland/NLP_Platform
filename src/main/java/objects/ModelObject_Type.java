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
/* First created by JCasGen Fri Dec 22 15:33:49 EST 2017 */
package objects;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue Feb 13 15:23:28 EST 2018
 * @generated */
public class ModelObject_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ModelObject.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("objects.ModelObject");
 
  /** @generated */
  final Feature casFeat_TextArray;
  /** @generated */
  final int     casFeatCode_TextArray;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getTextArray(int addr) {
        if (featOkTst && casFeat_TextArray == null)
      jcas.throwFeatMissing("TextArray", "objects.ModelObject");
    return ll_cas.ll_getRefValue(addr, casFeatCode_TextArray);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTextArray(int addr, int v) {
        if (featOkTst && casFeat_TextArray == null)
      jcas.throwFeatMissing("TextArray", "objects.ModelObject");
    ll_cas.ll_setRefValue(addr, casFeatCode_TextArray, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getTextArray(int addr, int i) {
        if (featOkTst && casFeat_TextArray == null)
      jcas.throwFeatMissing("TextArray", "objects.ModelObject");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_TextArray), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_TextArray), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_TextArray), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setTextArray(int addr, int i, String v) {
        if (featOkTst && casFeat_TextArray == null)
      jcas.throwFeatMissing("TextArray", "objects.ModelObject");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_TextArray), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_TextArray), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_TextArray), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_ModelFile;
  /** @generated */
  final int     casFeatCode_ModelFile;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getModelFile(int addr) {
        if (featOkTst && casFeat_ModelFile == null)
      jcas.throwFeatMissing("ModelFile", "objects.ModelObject");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ModelFile);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setModelFile(int addr, String v) {
        if (featOkTst && casFeat_ModelFile == null)
      jcas.throwFeatMissing("ModelFile", "objects.ModelObject");
    ll_cas.ll_setStringValue(addr, casFeatCode_ModelFile, v);}
    
  
 
  /** @generated */
  final Feature casFeat_IdArray;
  /** @generated */
  final int     casFeatCode_IdArray;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getIdArray(int addr) {
        if (featOkTst && casFeat_IdArray == null)
      jcas.throwFeatMissing("IdArray", "objects.ModelObject");
    return ll_cas.ll_getRefValue(addr, casFeatCode_IdArray);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIdArray(int addr, int v) {
        if (featOkTst && casFeat_IdArray == null)
      jcas.throwFeatMissing("IdArray", "objects.ModelObject");
    ll_cas.ll_setRefValue(addr, casFeatCode_IdArray, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getIdArray(int addr, int i) {
        if (featOkTst && casFeat_IdArray == null)
      jcas.throwFeatMissing("IdArray", "objects.ModelObject");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getIntArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IdArray), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_IdArray), i);
  return ll_cas.ll_getIntArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IdArray), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setIdArray(int addr, int i, int v) {
        if (featOkTst && casFeat_IdArray == null)
      jcas.throwFeatMissing("IdArray", "objects.ModelObject");
    if (lowLevelTypeChecks)
      ll_cas.ll_setIntArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IdArray), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_IdArray), i);
    ll_cas.ll_setIntArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IdArray), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ModelObject_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_TextArray = jcas.getRequiredFeatureDE(casType, "TextArray", "uima.cas.StringArray", featOkTst);
    casFeatCode_TextArray  = (null == casFeat_TextArray) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TextArray).getCode();

 
    casFeat_ModelFile = jcas.getRequiredFeatureDE(casType, "ModelFile", "uima.cas.String", featOkTst);
    casFeatCode_ModelFile  = (null == casFeat_ModelFile) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ModelFile).getCode();

 
    casFeat_IdArray = jcas.getRequiredFeatureDE(casType, "IdArray", "uima.cas.IntegerArray", featOkTst);
    casFeatCode_IdArray  = (null == casFeat_IdArray) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_IdArray).getCode();

  }
}



    
