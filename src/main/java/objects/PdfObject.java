

   
/* Apache UIMA v3 - First created by JCasGen Wed Oct 17 18:47:05 EDT 2018 */

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
 * Updated by JCasGen Wed Oct 17 18:47:05 EDT 2018
 * XML source: /home/ben/workspace/opennlp_processing/desc/objects/PdfObject.xml
 * @generated */
public class PdfObject extends Annotation {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "objects.PdfObject";
  
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
 
 
  /* *******************
   *   Feature Offsets *
   * *******************/ 
   
  public final static String _FeatName_PdfFile = "PdfFile";
  public final static String _FeatName_IsModelData = "IsModelData";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_PdfFile = TypeSystemImpl.createCallSite(PdfObject.class, "PdfFile");
  private final static MethodHandle _FH_PdfFile = _FC_PdfFile.dynamicInvoker();
  private final static CallSite _FC_IsModelData = TypeSystemImpl.createCallSite(PdfObject.class, "IsModelData");
  private final static MethodHandle _FH_IsModelData = _FC_IsModelData.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected PdfObject() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public PdfObject(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
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
  public String getPdfFile() { return _getStringValueNc(wrapGetIntCatchException(_FH_PdfFile));}
    
  /** setter for PdfFile - sets The CSV file. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPdfFile(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_PdfFile), v);
  }    
    
   
    
  //*--------------*
  //* Feature: IsModelData

  /** getter for IsModelData - gets If this is csv file with the column contains model data.
   * @generated
   * @return value of the feature 
   */
  public boolean getIsModelData() { return _getBooleanValueNc(wrapGetIntCatchException(_FH_IsModelData));}
    
  /** setter for IsModelData - sets If this is csv file with the column contains model data. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsModelData(boolean v) {
    _setBooleanValueNfc(wrapGetIntCatchException(_FH_IsModelData), v);
  }    
    
  }

    