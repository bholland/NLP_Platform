

   
/* Apache UIMA v3 - First created by JCasGen Mon Jul 08 13:18:30 EDT 2019 */

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
 * Updated by JCasGen Mon Jul 08 13:18:32 EDT 2019
 * XML source: /home/ben/workspace/NLP_Stack/desc/objects/ProjectObject.xml
 * @generated */
public class ProjectObject extends Annotation {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "objects.ProjectObject";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ProjectObject.class);
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
   
  public final static String _FeatName_Name = "Name";
  public final static String _FeatName_ProjectType = "ProjectType";
  public final static String _FeatName_CheckoutTimeout = "CheckoutTimeout";
  public final static String _FeatName_ProjectOwner = "ProjectOwner";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_Name = TypeSystemImpl.createCallSite(ProjectObject.class, "Name");
  private final static MethodHandle _FH_Name = _FC_Name.dynamicInvoker();
  private final static CallSite _FC_ProjectType = TypeSystemImpl.createCallSite(ProjectObject.class, "ProjectType");
  private final static MethodHandle _FH_ProjectType = _FC_ProjectType.dynamicInvoker();
  private final static CallSite _FC_CheckoutTimeout = TypeSystemImpl.createCallSite(ProjectObject.class, "CheckoutTimeout");
  private final static MethodHandle _FH_CheckoutTimeout = _FC_CheckoutTimeout.dynamicInvoker();
  private final static CallSite _FC_ProjectOwner = TypeSystemImpl.createCallSite(ProjectObject.class, "ProjectOwner");
  private final static MethodHandle _FH_ProjectOwner = _FC_ProjectOwner.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected ProjectObject() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public ProjectObject(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ProjectObject(JCas jcas) {
    super(jcas);
    readObject();   
  } 


  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ProjectObject(JCas jcas, int begin, int end) {
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
  //* Feature: Name

  /** getter for Name - gets The project name.
   * @generated
   * @return value of the feature 
   */
  public String getName() { return _getStringValueNc(wrapGetIntCatchException(_FH_Name));}
    
  /** setter for Name - sets The project name. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setName(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_Name), v);
  }    
    
   
    
  //*--------------*
  //* Feature: ProjectType

  /** getter for ProjectType - gets The project type. Currently, the only supported type is dual_validation. 
   * @generated
   * @return value of the feature 
   */
  public String getProjectType() { return _getStringValueNc(wrapGetIntCatchException(_FH_ProjectType));}
    
  /** setter for ProjectType - sets The project type. Currently, the only supported type is dual_validation.  
   * @generated
   * @param v value to set into the feature 
   */
  public void setProjectType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_ProjectType), v);
  }    
    
   
    
  //*--------------*
  //* Feature: CheckoutTimeout

  /** getter for CheckoutTimeout - gets The value of the checkout timeout. 
   * @generated
   * @return value of the feature 
   */
  public int getCheckoutTimeout() { return _getIntValueNc(wrapGetIntCatchException(_FH_CheckoutTimeout));}
    
  /** setter for CheckoutTimeout - sets The value of the checkout timeout.  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCheckoutTimeout(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_CheckoutTimeout), v);
  }    
    
   
    
  //*--------------*
  //* Feature: ProjectOwner

  /** getter for ProjectOwner - gets This will specify the user name of the project owner. If this user does not exist (case senseative), it will be created and assigned as the owner. 
   * @generated
   * @return value of the feature 
   */
  public String getProjectOwner() { return _getStringValueNc(wrapGetIntCatchException(_FH_ProjectOwner));}
    
  /** setter for ProjectOwner - sets This will specify the user name of the project owner. If this user does not exist (case senseative), it will be created and assigned as the owner.  
   * @generated
   * @param v value to set into the feature 
   */
  public void setProjectOwner(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_ProjectOwner), v);
  }    
    
  }

    