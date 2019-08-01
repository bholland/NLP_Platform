

   
/* Apache UIMA v3 - First created by JCasGen Mon Jul 08 13:22:10 EDT 2019 */

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
 * Updated by JCasGen Mon Jul 08 13:22:10 EDT 2019
 * XML source: /home/ben/workspace/NLP_Stack/desc/ContentProcessingEngine/FJSP/Setup_CPE.xml
 * @generated */
public class DatabaseConnection extends Annotation {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "objects.DatabaseConnection";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DatabaseConnection.class);
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
   
  public final static String _FeatName_DatabaseServer = "DatabaseServer";
  public final static String _FeatName_Database = "Database";
  public final static String _FeatName_UserName = "UserName";
  public final static String _FeatName_Password = "Password";
  public final static String _FeatName_Port = "Port";
  public final static String _FeatName_DatabaseType = "DatabaseType";
  public final static String _FeatName_LoggingUserId = "LoggingUserId";
  public final static String _FeatName_UseJobQueue = "UseJobQueue";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_DatabaseServer = TypeSystemImpl.createCallSite(DatabaseConnection.class, "DatabaseServer");
  private final static MethodHandle _FH_DatabaseServer = _FC_DatabaseServer.dynamicInvoker();
  private final static CallSite _FC_Database = TypeSystemImpl.createCallSite(DatabaseConnection.class, "Database");
  private final static MethodHandle _FH_Database = _FC_Database.dynamicInvoker();
  private final static CallSite _FC_UserName = TypeSystemImpl.createCallSite(DatabaseConnection.class, "UserName");
  private final static MethodHandle _FH_UserName = _FC_UserName.dynamicInvoker();
  private final static CallSite _FC_Password = TypeSystemImpl.createCallSite(DatabaseConnection.class, "Password");
  private final static MethodHandle _FH_Password = _FC_Password.dynamicInvoker();
  private final static CallSite _FC_Port = TypeSystemImpl.createCallSite(DatabaseConnection.class, "Port");
  private final static MethodHandle _FH_Port = _FC_Port.dynamicInvoker();
  private final static CallSite _FC_DatabaseType = TypeSystemImpl.createCallSite(DatabaseConnection.class, "DatabaseType");
  private final static MethodHandle _FH_DatabaseType = _FC_DatabaseType.dynamicInvoker();
  private final static CallSite _FC_LoggingUserId = TypeSystemImpl.createCallSite(DatabaseConnection.class, "LoggingUserId");
  private final static MethodHandle _FH_LoggingUserId = _FC_LoggingUserId.dynamicInvoker();
  private final static CallSite _FC_UseJobQueue = TypeSystemImpl.createCallSite(DatabaseConnection.class, "UseJobQueue");
  private final static MethodHandle _FH_UseJobQueue = _FC_UseJobQueue.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected DatabaseConnection() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public DatabaseConnection(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public DatabaseConnection(JCas jcas) {
    super(jcas);
    readObject();   
  } 


  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public DatabaseConnection(JCas jcas, int begin, int end) {
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
  //* Feature: DatabaseServer

  /** getter for DatabaseServer - gets 
   * @generated
   * @return value of the feature 
   */
  public String getDatabaseServer() { return _getStringValueNc(wrapGetIntCatchException(_FH_DatabaseServer));}
    
  /** setter for DatabaseServer - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDatabaseServer(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_DatabaseServer), v);
  }    
    
   
    
  //*--------------*
  //* Feature: Database

  /** getter for Database - gets 
   * @generated
   * @return value of the feature 
   */
  public String getDatabase() { return _getStringValueNc(wrapGetIntCatchException(_FH_Database));}
    
  /** setter for Database - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDatabase(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_Database), v);
  }    
    
   
    
  //*--------------*
  //* Feature: UserName

  /** getter for UserName - gets 
   * @generated
   * @return value of the feature 
   */
  public String getUserName() { return _getStringValueNc(wrapGetIntCatchException(_FH_UserName));}
    
  /** setter for UserName - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUserName(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_UserName), v);
  }    
    
   
    
  //*--------------*
  //* Feature: Password

  /** getter for Password - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPassword() { return _getStringValueNc(wrapGetIntCatchException(_FH_Password));}
    
  /** setter for Password - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPassword(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_Password), v);
  }    
    
   
    
  //*--------------*
  //* Feature: Port

  /** getter for Port - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPort() { return _getStringValueNc(wrapGetIntCatchException(_FH_Port));}
    
  /** setter for Port - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPort(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_Port), v);
  }    
    
   
    
  //*--------------*
  //* Feature: DatabaseType

  /** getter for DatabaseType - gets can either be mysql or pgsql
   * @generated
   * @return value of the feature 
   */
  public String getDatabaseType() { return _getStringValueNc(wrapGetIntCatchException(_FH_DatabaseType));}
    
  /** setter for DatabaseType - sets can either be mysql or pgsql 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDatabaseType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_DatabaseType), v);
  }    
    
   
    
  //*--------------*
  //* Feature: LoggingUserId

  /** getter for LoggingUserId - gets 
   * @generated
   * @return value of the feature 
   */
  public int getLoggingUserId() { return _getIntValueNc(wrapGetIntCatchException(_FH_LoggingUserId));}
    
  /** setter for LoggingUserId - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLoggingUserId(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_LoggingUserId), v);
  }    
    
   
    
  //*--------------*
  //* Feature: UseJobQueue

  /** getter for UseJobQueue - gets Use Job Queue can be 1 of 3 valid values. 
0: Do not use the job queue.
1: Run this as an insert job
2: Run this as a processing job
   * @generated
   * @return value of the feature 
   */
  public int getUseJobQueue() { return _getIntValueNc(wrapGetIntCatchException(_FH_UseJobQueue));}
    
  /** setter for UseJobQueue - sets Use Job Queue can be 1 of 3 valid values. 
0: Do not use the job queue.
1: Run this as an insert job
2: Run this as a processing job 
   * @generated
   * @param v value to set into the feature 
   */
  public void setUseJobQueue(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_UseJobQueue), v);
  }    
    
  }

    