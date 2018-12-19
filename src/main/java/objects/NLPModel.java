

   
/* Apache UIMA v3 - First created by JCasGen Tue Oct 23 14:43:15 EDT 2018 */

package objects;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;


import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Oct 23 14:43:59 EDT 2018
 * XML source: /home/ben/workspace/opennlp_processing/desc/objects/NLPModel.xml
 * @generated */
public class NLPModel extends Annotation {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "objects.NLPModel";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NLPModel.class);
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
   
  public final static String _FeatName_CategoryName = "CategoryName";
  public final static String _FeatName_BatchNumber = "BatchNumber";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_CategoryName = TypeSystemImpl.createCallSite(NLPModel.class, "CategoryName");
  private final static MethodHandle _FH_CategoryName = _FC_CategoryName.dynamicInvoker();
  private final static CallSite _FC_BatchNumber = TypeSystemImpl.createCallSite(NLPModel.class, "BatchNumber");
  private final static MethodHandle _FH_BatchNumber = _FC_BatchNumber.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected NLPModel() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public NLPModel(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public NLPModel(JCas jcas) {
    super(jcas);
    readObject();   
  } 


  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public NLPModel(JCas jcas, int begin, int end) {
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
  //* Feature: CategoryName

  /** getter for CategoryName - gets 
   * @generated
   * @return value of the feature 
   */
  public String getCategoryName() { return _getStringValueNc(wrapGetIntCatchException(_FH_CategoryName));}
    
  /** setter for CategoryName - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCategoryName(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_CategoryName), v);
  }    
    
   
    
  //*--------------*
  //* Feature: BatchNumber

  /** getter for BatchNumber - gets 
   * @generated
   * @return value of the feature 
   */
  public int getBatchNumber() { return _getIntValueNc(wrapGetIntCatchException(_FH_BatchNumber));}
    
  /** setter for BatchNumber - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBatchNumber(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_BatchNumber), v);
  }    
    
  }

    