

   
/* Apache UIMA v3 - First created by JCasGen Thu May 23 15:02:13 EDT 2019 */

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
 * Updated by JCasGen Thu May 23 15:02:13 EDT 2019
 * XML source: /home/ben/workspace/NLP_Stack/desc/objects/CsvObject.xml
 * @generated */
public class CsvObject extends Annotation {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "objects.CsvObject";
  
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
 
 
  /* *******************
   *   Feature Offsets *
   * *******************/ 
   
  public final static String _FeatName_CsvFile = "CsvFile";
  public final static String _FeatName_CsvIdColumn = "CsvIdColumn";
  public final static String _FeatName_CsvTextColumn = "CsvTextColumn";
  public final static String _FeatName_CsvCategoryColumn = "CsvCategoryColumn";
  public final static String _FeatName_IsModelData = "IsModelData";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_CsvFile = TypeSystemImpl.createCallSite(CsvObject.class, "CsvFile");
  private final static MethodHandle _FH_CsvFile = _FC_CsvFile.dynamicInvoker();
  private final static CallSite _FC_CsvIdColumn = TypeSystemImpl.createCallSite(CsvObject.class, "CsvIdColumn");
  private final static MethodHandle _FH_CsvIdColumn = _FC_CsvIdColumn.dynamicInvoker();
  private final static CallSite _FC_CsvTextColumn = TypeSystemImpl.createCallSite(CsvObject.class, "CsvTextColumn");
  private final static MethodHandle _FH_CsvTextColumn = _FC_CsvTextColumn.dynamicInvoker();
  private final static CallSite _FC_CsvCategoryColumn = TypeSystemImpl.createCallSite(CsvObject.class, "CsvCategoryColumn");
  private final static MethodHandle _FH_CsvCategoryColumn = _FC_CsvCategoryColumn.dynamicInvoker();
  private final static CallSite _FC_IsModelData = TypeSystemImpl.createCallSite(CsvObject.class, "IsModelData");
  private final static MethodHandle _FH_IsModelData = _FC_IsModelData.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected CsvObject() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public CsvObject(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
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
  public String getCsvFile() { return _getStringValueNc(wrapGetIntCatchException(_FH_CsvFile));}
    
  /** setter for CsvFile - sets The CSV file. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCsvFile(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_CsvFile), v);
  }    
    
   
    
  //*--------------*
  //* Feature: CsvIdColumn

  /** getter for CsvIdColumn - gets The ID column for the CSV.
   * @generated
   * @return value of the feature 
   */
  public String getCsvIdColumn() { return _getStringValueNc(wrapGetIntCatchException(_FH_CsvIdColumn));}
    
  /** setter for CsvIdColumn - sets The ID column for the CSV. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCsvIdColumn(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_CsvIdColumn), v);
  }    
    
   
    
  //*--------------*
  //* Feature: CsvTextColumn

  /** getter for CsvTextColumn - gets The text column for the CSV file.
   * @generated
   * @return value of the feature 
   */
  public String getCsvTextColumn() { return _getStringValueNc(wrapGetIntCatchException(_FH_CsvTextColumn));}
    
  /** setter for CsvTextColumn - sets The text column for the CSV file. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCsvTextColumn(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_CsvTextColumn), v);
  }    
    
   
    
  //*--------------*
  //* Feature: CsvCategoryColumn

  /** getter for CsvCategoryColumn - gets The category column for the CSV file.
   * @generated
   * @return value of the feature 
   */
  public String getCsvCategoryColumn() { return _getStringValueNc(wrapGetIntCatchException(_FH_CsvCategoryColumn));}
    
  /** setter for CsvCategoryColumn - sets The category column for the CSV file. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCsvCategoryColumn(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_CsvCategoryColumn), v);
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

    