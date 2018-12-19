

   
/* Apache UIMA v3 - First created by JCasGen Wed Oct 17 18:47:20 EDT 2018 */

package objects;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;


import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.cas.IntegerArray;


/** 
 * Updated by JCasGen Wed Oct 17 18:47:20 EDT 2018
 * XML source: /home/ben/workspace/opennlp_processing/desc/objects/ModelObject.xml
 * @generated */
public class ModelObject extends Annotation {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "objects.ModelObject";
  
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
 
 
  /* *******************
   *   Feature Offsets *
   * *******************/ 
   
  public final static String _FeatName_ModelFile = "ModelFile";
  public final static String _FeatName_IdArray = "IdArray";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_ModelFile = TypeSystemImpl.createCallSite(ModelObject.class, "ModelFile");
  private final static MethodHandle _FH_ModelFile = _FC_ModelFile.dynamicInvoker();
  private final static CallSite _FC_IdArray = TypeSystemImpl.createCallSite(ModelObject.class, "IdArray");
  private final static MethodHandle _FH_IdArray = _FC_IdArray.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected ModelObject() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public ModelObject(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
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
  //* Feature: ModelFile

  /** getter for ModelFile - gets Use this model file to process data.
   * @generated
   * @return value of the feature 
   */
  public String getModelFile() { return _getStringValueNc(wrapGetIntCatchException(_FH_ModelFile));}
    
  /** setter for ModelFile - sets Use this model file to process data. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setModelFile(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_ModelFile), v);
  }    
    
   
    
  //*--------------*
  //* Feature: IdArray

  /** getter for IdArray - gets This is a corresponding id array. This exists because only primitives (no Entry<> types) are allowed within the xml.
   * @generated
   * @return value of the feature 
   */
  public IntegerArray getIdArray() { return (IntegerArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IdArray)));}
    
  /** setter for IdArray - sets This is a corresponding id array. This exists because only primitives (no Entry<> types) are allowed within the xml. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setIdArray(IntegerArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_IdArray), v);
  }    
    
    
  /** indexed getter for IdArray - gets an indexed value - This is a corresponding id array. This exists because only primitives (no Entry<> types) are allowed within the xml.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public int getIdArray(int i) {
     return ((IntegerArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IdArray)))).get(i);} 

  /** indexed setter for IdArray - sets an indexed value - This is a corresponding id array. This exists because only primitives (no Entry<> types) are allowed within the xml.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setIdArray(int i, int v) {
    ((IntegerArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_IdArray)))).set(i, v);
  }  
  }

    