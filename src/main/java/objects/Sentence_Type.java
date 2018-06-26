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
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Sun Oct 15 18:46:06 EDT 2017
 * @generated */
public class Sentence_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Sentence.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("objects.Sentence");
 
  /** @generated */
  final Feature casFeat_words;
  /** @generated */
  final int     casFeatCode_words;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getWords(int addr) {
        if (featOkTst && casFeat_words == null)
      jcas.throwFeatMissing("words", "objects.Sentence");
    return ll_cas.ll_getRefValue(addr, casFeatCode_words);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setWords(int addr, int v) {
        if (featOkTst && casFeat_words == null)
      jcas.throwFeatMissing("words", "objects.Sentence");
    ll_cas.ll_setRefValue(addr, casFeatCode_words, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getWords(int addr, int i) {
        if (featOkTst && casFeat_words == null)
      jcas.throwFeatMissing("words", "objects.Sentence");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_words), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_words), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_words), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setWords(int addr, int i, String v) {
        if (featOkTst && casFeat_words == null)
      jcas.throwFeatMissing("words", "objects.Sentence");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_words), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_words), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_words), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_pos_tags;
  /** @generated */
  final int     casFeatCode_pos_tags;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getPos_tags(int addr) {
        if (featOkTst && casFeat_pos_tags == null)
      jcas.throwFeatMissing("pos_tags", "objects.Sentence");
    return ll_cas.ll_getRefValue(addr, casFeatCode_pos_tags);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPos_tags(int addr, int v) {
        if (featOkTst && casFeat_pos_tags == null)
      jcas.throwFeatMissing("pos_tags", "objects.Sentence");
    ll_cas.ll_setRefValue(addr, casFeatCode_pos_tags, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getPos_tags(int addr, int i) {
        if (featOkTst && casFeat_pos_tags == null)
      jcas.throwFeatMissing("pos_tags", "objects.Sentence");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_pos_tags), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_pos_tags), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_pos_tags), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setPos_tags(int addr, int i, String v) {
        if (featOkTst && casFeat_pos_tags == null)
      jcas.throwFeatMissing("pos_tags", "objects.Sentence");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_pos_tags), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_pos_tags), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_pos_tags), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_lemma_tags;
  /** @generated */
  final int     casFeatCode_lemma_tags;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getLemma_tags(int addr) {
        if (featOkTst && casFeat_lemma_tags == null)
      jcas.throwFeatMissing("lemma_tags", "objects.Sentence");
    return ll_cas.ll_getRefValue(addr, casFeatCode_lemma_tags);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLemma_tags(int addr, int v) {
        if (featOkTst && casFeat_lemma_tags == null)
      jcas.throwFeatMissing("lemma_tags", "objects.Sentence");
    ll_cas.ll_setRefValue(addr, casFeatCode_lemma_tags, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getLemma_tags(int addr, int i) {
        if (featOkTst && casFeat_lemma_tags == null)
      jcas.throwFeatMissing("lemma_tags", "objects.Sentence");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_lemma_tags), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_lemma_tags), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_lemma_tags), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setLemma_tags(int addr, int i, String v) {
        if (featOkTst && casFeat_lemma_tags == null)
      jcas.throwFeatMissing("lemma_tags", "objects.Sentence");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_lemma_tags), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_lemma_tags), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_lemma_tags), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_text_string;
  /** @generated */
  final int     casFeatCode_text_string;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getText_string(int addr) {
        if (featOkTst && casFeat_text_string == null)
      jcas.throwFeatMissing("text_string", "objects.Sentence");
    return ll_cas.ll_getStringValue(addr, casFeatCode_text_string);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setText_string(int addr, String v) {
        if (featOkTst && casFeat_text_string == null)
      jcas.throwFeatMissing("text_string", "objects.Sentence");
    ll_cas.ll_setStringValue(addr, casFeatCode_text_string, v);}
    
  
 
  /** @generated */
  final Feature casFeat_chunks;
  /** @generated */
  final int     casFeatCode_chunks;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getChunks(int addr) {
        if (featOkTst && casFeat_chunks == null)
      jcas.throwFeatMissing("chunks", "objects.Sentence");
    return ll_cas.ll_getRefValue(addr, casFeatCode_chunks);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setChunks(int addr, int v) {
        if (featOkTst && casFeat_chunks == null)
      jcas.throwFeatMissing("chunks", "objects.Sentence");
    ll_cas.ll_setRefValue(addr, casFeatCode_chunks, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getChunks(int addr, int i) {
        if (featOkTst && casFeat_chunks == null)
      jcas.throwFeatMissing("chunks", "objects.Sentence");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_chunks), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_chunks), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_chunks), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setChunks(int addr, int i, String v) {
        if (featOkTst && casFeat_chunks == null)
      jcas.throwFeatMissing("chunks", "objects.Sentence");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_chunks), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_chunks), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_chunks), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_names;
  /** @generated */
  final int     casFeatCode_names;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getNames(int addr) {
        if (featOkTst && casFeat_names == null)
      jcas.throwFeatMissing("names", "objects.Sentence");
    return ll_cas.ll_getRefValue(addr, casFeatCode_names);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNames(int addr, int v) {
        if (featOkTst && casFeat_names == null)
      jcas.throwFeatMissing("names", "objects.Sentence");
    ll_cas.ll_setRefValue(addr, casFeatCode_names, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getNames(int addr, int i) {
        if (featOkTst && casFeat_names == null)
      jcas.throwFeatMissing("names", "objects.Sentence");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_names), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_names), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_names), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setNames(int addr, int i, String v) {
        if (featOkTst && casFeat_names == null)
      jcas.throwFeatMissing("names", "objects.Sentence");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_names), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_names), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_names), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_DocumentID;
  /** @generated */
  final int     casFeatCode_DocumentID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getDocumentID(int addr) {
        if (featOkTst && casFeat_DocumentID == null)
      jcas.throwFeatMissing("DocumentID", "objects.Sentence");
    return ll_cas.ll_getIntValue(addr, casFeatCode_DocumentID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentID(int addr, int v) {
        if (featOkTst && casFeat_DocumentID == null)
      jcas.throwFeatMissing("DocumentID", "objects.Sentence");
    ll_cas.ll_setIntValue(addr, casFeatCode_DocumentID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_IsName;
  /** @generated */
  final int     casFeatCode_IsName;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getIsName(int addr) {
        if (featOkTst && casFeat_IsName == null)
      jcas.throwFeatMissing("IsName", "objects.Sentence");
    return ll_cas.ll_getRefValue(addr, casFeatCode_IsName);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsName(int addr, int v) {
        if (featOkTst && casFeat_IsName == null)
      jcas.throwFeatMissing("IsName", "objects.Sentence");
    ll_cas.ll_setRefValue(addr, casFeatCode_IsName, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public boolean getIsName(int addr, int i) {
        if (featOkTst && casFeat_IsName == null)
      jcas.throwFeatMissing("IsName", "objects.Sentence");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsName), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_IsName), i);
  return ll_cas.ll_getBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsName), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setIsName(int addr, int i, boolean v) {
        if (featOkTst && casFeat_IsName == null)
      jcas.throwFeatMissing("IsName", "objects.Sentence");
    if (lowLevelTypeChecks)
      ll_cas.ll_setBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsName), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_IsName), i);
    ll_cas.ll_setBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsName), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_SentenceNumber;
  /** @generated */
  final int     casFeatCode_SentenceNumber;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSentenceNumber(int addr) {
        if (featOkTst && casFeat_SentenceNumber == null)
      jcas.throwFeatMissing("SentenceNumber", "objects.Sentence");
    return ll_cas.ll_getIntValue(addr, casFeatCode_SentenceNumber);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSentenceNumber(int addr, int v) {
        if (featOkTst && casFeat_SentenceNumber == null)
      jcas.throwFeatMissing("SentenceNumber", "objects.Sentence");
    ll_cas.ll_setIntValue(addr, casFeatCode_SentenceNumber, v);}
    
  
 
  /** @generated */
  final Feature casFeat_sentence_id;
  /** @generated */
  final int     casFeatCode_sentence_id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSentence_id(int addr) {
        if (featOkTst && casFeat_sentence_id == null)
      jcas.throwFeatMissing("sentence_id", "objects.Sentence");
    return ll_cas.ll_getIntValue(addr, casFeatCode_sentence_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSentence_id(int addr, int v) {
        if (featOkTst && casFeat_sentence_id == null)
      jcas.throwFeatMissing("sentence_id", "objects.Sentence");
    ll_cas.ll_setIntValue(addr, casFeatCode_sentence_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_IsHospital;
  /** @generated */
  final int     casFeatCode_IsHospital;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getIsHospital(int addr) {
        if (featOkTst && casFeat_IsHospital == null)
      jcas.throwFeatMissing("IsHospital", "objects.Sentence");
    return ll_cas.ll_getRefValue(addr, casFeatCode_IsHospital);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsHospital(int addr, int v) {
        if (featOkTst && casFeat_IsHospital == null)
      jcas.throwFeatMissing("IsHospital", "objects.Sentence");
    ll_cas.ll_setRefValue(addr, casFeatCode_IsHospital, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public boolean getIsHospital(int addr, int i) {
        if (featOkTst && casFeat_IsHospital == null)
      jcas.throwFeatMissing("IsHospital", "objects.Sentence");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsHospital), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_IsHospital), i);
  return ll_cas.ll_getBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsHospital), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setIsHospital(int addr, int i, boolean v) {
        if (featOkTst && casFeat_IsHospital == null)
      jcas.throwFeatMissing("IsHospital", "objects.Sentence");
    if (lowLevelTypeChecks)
      ll_cas.ll_setBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsHospital), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_IsHospital), i);
    ll_cas.ll_setBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsHospital), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_IsPerson;
  /** @generated */
  final int     casFeatCode_IsPerson;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getIsPerson(int addr) {
        if (featOkTst && casFeat_IsPerson == null)
      jcas.throwFeatMissing("IsPerson", "objects.Sentence");
    return ll_cas.ll_getRefValue(addr, casFeatCode_IsPerson);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsPerson(int addr, int v) {
        if (featOkTst && casFeat_IsPerson == null)
      jcas.throwFeatMissing("IsPerson", "objects.Sentence");
    ll_cas.ll_setRefValue(addr, casFeatCode_IsPerson, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public boolean getIsPerson(int addr, int i) {
        if (featOkTst && casFeat_IsPerson == null)
      jcas.throwFeatMissing("IsPerson", "objects.Sentence");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsPerson), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_IsPerson), i);
  return ll_cas.ll_getBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsPerson), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setIsPerson(int addr, int i, boolean v) {
        if (featOkTst && casFeat_IsPerson == null)
      jcas.throwFeatMissing("IsPerson", "objects.Sentence");
    if (lowLevelTypeChecks)
      ll_cas.ll_setBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsPerson), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_IsPerson), i);
    ll_cas.ll_setBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsPerson), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_IsIllness;
  /** @generated */
  final int     casFeatCode_IsIllness;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getIsIllness(int addr) {
        if (featOkTst && casFeat_IsIllness == null)
      jcas.throwFeatMissing("IsIllness", "objects.Sentence");
    return ll_cas.ll_getRefValue(addr, casFeatCode_IsIllness);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsIllness(int addr, int v) {
        if (featOkTst && casFeat_IsIllness == null)
      jcas.throwFeatMissing("IsIllness", "objects.Sentence");
    ll_cas.ll_setRefValue(addr, casFeatCode_IsIllness, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public boolean getIsIllness(int addr, int i) {
        if (featOkTst && casFeat_IsIllness == null)
      jcas.throwFeatMissing("IsIllness", "objects.Sentence");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsIllness), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_IsIllness), i);
  return ll_cas.ll_getBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsIllness), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setIsIllness(int addr, int i, boolean v) {
        if (featOkTst && casFeat_IsIllness == null)
      jcas.throwFeatMissing("IsIllness", "objects.Sentence");
    if (lowLevelTypeChecks)
      ll_cas.ll_setBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsIllness), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_IsIllness), i);
    ll_cas.ll_setBooleanArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_IsIllness), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Sentence_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_words = jcas.getRequiredFeatureDE(casType, "words", "uima.cas.StringArray", featOkTst);
    casFeatCode_words  = (null == casFeat_words) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_words).getCode();

 
    casFeat_pos_tags = jcas.getRequiredFeatureDE(casType, "pos_tags", "uima.cas.StringArray", featOkTst);
    casFeatCode_pos_tags  = (null == casFeat_pos_tags) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pos_tags).getCode();

 
    casFeat_lemma_tags = jcas.getRequiredFeatureDE(casType, "lemma_tags", "uima.cas.StringArray", featOkTst);
    casFeatCode_lemma_tags  = (null == casFeat_lemma_tags) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lemma_tags).getCode();

 
    casFeat_text_string = jcas.getRequiredFeatureDE(casType, "text_string", "uima.cas.String", featOkTst);
    casFeatCode_text_string  = (null == casFeat_text_string) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_text_string).getCode();

 
    casFeat_chunks = jcas.getRequiredFeatureDE(casType, "chunks", "uima.cas.StringArray", featOkTst);
    casFeatCode_chunks  = (null == casFeat_chunks) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_chunks).getCode();

 
    casFeat_names = jcas.getRequiredFeatureDE(casType, "names", "uima.cas.StringArray", featOkTst);
    casFeatCode_names  = (null == casFeat_names) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_names).getCode();

 
    casFeat_DocumentID = jcas.getRequiredFeatureDE(casType, "DocumentID", "uima.cas.Integer", featOkTst);
    casFeatCode_DocumentID  = (null == casFeat_DocumentID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_DocumentID).getCode();

 
    casFeat_IsName = jcas.getRequiredFeatureDE(casType, "IsName", "uima.cas.BooleanArray", featOkTst);
    casFeatCode_IsName  = (null == casFeat_IsName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_IsName).getCode();

 
    casFeat_SentenceNumber = jcas.getRequiredFeatureDE(casType, "SentenceNumber", "uima.cas.Integer", featOkTst);
    casFeatCode_SentenceNumber  = (null == casFeat_SentenceNumber) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SentenceNumber).getCode();

 
    casFeat_sentence_id = jcas.getRequiredFeatureDE(casType, "sentence_id", "uima.cas.Integer", featOkTst);
    casFeatCode_sentence_id  = (null == casFeat_sentence_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sentence_id).getCode();

 
    casFeat_IsHospital = jcas.getRequiredFeatureDE(casType, "IsHospital", "uima.cas.BooleanArray", featOkTst);
    casFeatCode_IsHospital  = (null == casFeat_IsHospital) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_IsHospital).getCode();

 
    casFeat_IsPerson = jcas.getRequiredFeatureDE(casType, "IsPerson", "uima.cas.BooleanArray", featOkTst);
    casFeatCode_IsPerson  = (null == casFeat_IsPerson) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_IsPerson).getCode();

 
    casFeat_IsIllness = jcas.getRequiredFeatureDE(casType, "IsIllness", "uima.cas.BooleanArray", featOkTst);
    casFeatCode_IsIllness  = (null == casFeat_IsIllness) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_IsIllness).getCode();

  }
}



    
