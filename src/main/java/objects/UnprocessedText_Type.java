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
 * Updated by JCasGen Tue Apr 10 14:52:37 EDT 2018
 * @generated */
public class UnprocessedText_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = UnprocessedText.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("objects.UnprocessedText");
 
  /** @generated */
  final Feature casFeat_RawTextString;
  /** @generated */
  final int     casFeatCode_RawTextString;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getRawTextString(int addr) {
        if (featOkTst && casFeat_RawTextString == null)
      jcas.throwFeatMissing("RawTextString", "objects.UnprocessedText");
    return ll_cas.ll_getStringValue(addr, casFeatCode_RawTextString);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRawTextString(int addr, String v) {
        if (featOkTst && casFeat_RawTextString == null)
      jcas.throwFeatMissing("RawTextString", "objects.UnprocessedText");
    ll_cas.ll_setStringValue(addr, casFeatCode_RawTextString, v);}
    
  
 
  /** @generated */
  final Feature casFeat_TextId;
  /** @generated */
  final int     casFeatCode_TextId;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getTextId(int addr) {
        if (featOkTst && casFeat_TextId == null)
      jcas.throwFeatMissing("TextId", "objects.UnprocessedText");
    return ll_cas.ll_getIntValue(addr, casFeatCode_TextId);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTextId(int addr, int v) {
        if (featOkTst && casFeat_TextId == null)
      jcas.throwFeatMissing("TextId", "objects.UnprocessedText");
    ll_cas.ll_setIntValue(addr, casFeatCode_TextId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_NumTokens;
  /** @generated */
  final int     casFeatCode_NumTokens;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getNumTokens(int addr) {
        if (featOkTst && casFeat_NumTokens == null)
      jcas.throwFeatMissing("NumTokens", "objects.UnprocessedText");
    return ll_cas.ll_getIntValue(addr, casFeatCode_NumTokens);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNumTokens(int addr, int v) {
        if (featOkTst && casFeat_NumTokens == null)
      jcas.throwFeatMissing("NumTokens", "objects.UnprocessedText");
    ll_cas.ll_setIntValue(addr, casFeatCode_NumTokens, v);}
    
  
 
  /** @generated */
  final Feature casFeat_IsSource;
  /** @generated */
  final int     casFeatCode_IsSource;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIsSource(int addr) {
        if (featOkTst && casFeat_IsSource == null)
      jcas.throwFeatMissing("IsSource", "objects.UnprocessedText");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_IsSource);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsSource(int addr, boolean v) {
        if (featOkTst && casFeat_IsSource == null)
      jcas.throwFeatMissing("IsSource", "objects.UnprocessedText");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_IsSource, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public UnprocessedText_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_RawTextString = jcas.getRequiredFeatureDE(casType, "RawTextString", "uima.cas.String", featOkTst);
    casFeatCode_RawTextString  = (null == casFeat_RawTextString) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_RawTextString).getCode();

 
    casFeat_TextId = jcas.getRequiredFeatureDE(casType, "TextId", "uima.cas.Integer", featOkTst);
    casFeatCode_TextId  = (null == casFeat_TextId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TextId).getCode();

 
    casFeat_NumTokens = jcas.getRequiredFeatureDE(casType, "NumTokens", "uima.cas.Integer", featOkTst);
    casFeatCode_NumTokens  = (null == casFeat_NumTokens) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_NumTokens).getCode();

 
    casFeat_IsSource = jcas.getRequiredFeatureDE(casType, "IsSource", "uima.cas.Boolean", featOkTst);
    casFeatCode_IsSource  = (null == casFeat_IsSource) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_IsSource).getCode();

  }
}



    
