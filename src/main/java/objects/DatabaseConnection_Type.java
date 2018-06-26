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
/* First created by JCasGen Wed Sep 13 17:01:54 EDT 2017 */
package objects;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue Apr 10 14:33:25 EDT 2018
 * @generated */
public class DatabaseConnection_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DatabaseConnection.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("objects.DatabaseConnection");
 
  /** @generated */
  final Feature casFeat_DatabaseServer;
  /** @generated */
  final int     casFeatCode_DatabaseServer;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDatabaseServer(int addr) {
        if (featOkTst && casFeat_DatabaseServer == null)
      jcas.throwFeatMissing("DatabaseServer", "objects.DatabaseConnection");
    return ll_cas.ll_getStringValue(addr, casFeatCode_DatabaseServer);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDatabaseServer(int addr, String v) {
        if (featOkTst && casFeat_DatabaseServer == null)
      jcas.throwFeatMissing("DatabaseServer", "objects.DatabaseConnection");
    ll_cas.ll_setStringValue(addr, casFeatCode_DatabaseServer, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Database;
  /** @generated */
  final int     casFeatCode_Database;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDatabase(int addr) {
        if (featOkTst && casFeat_Database == null)
      jcas.throwFeatMissing("Database", "objects.DatabaseConnection");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Database);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDatabase(int addr, String v) {
        if (featOkTst && casFeat_Database == null)
      jcas.throwFeatMissing("Database", "objects.DatabaseConnection");
    ll_cas.ll_setStringValue(addr, casFeatCode_Database, v);}
    
  
 
  /** @generated */
  final Feature casFeat_UserName;
  /** @generated */
  final int     casFeatCode_UserName;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getUserName(int addr) {
        if (featOkTst && casFeat_UserName == null)
      jcas.throwFeatMissing("UserName", "objects.DatabaseConnection");
    return ll_cas.ll_getStringValue(addr, casFeatCode_UserName);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUserName(int addr, String v) {
        if (featOkTst && casFeat_UserName == null)
      jcas.throwFeatMissing("UserName", "objects.DatabaseConnection");
    ll_cas.ll_setStringValue(addr, casFeatCode_UserName, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Password;
  /** @generated */
  final int     casFeatCode_Password;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPassword(int addr) {
        if (featOkTst && casFeat_Password == null)
      jcas.throwFeatMissing("Password", "objects.DatabaseConnection");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Password);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPassword(int addr, String v) {
        if (featOkTst && casFeat_Password == null)
      jcas.throwFeatMissing("Password", "objects.DatabaseConnection");
    ll_cas.ll_setStringValue(addr, casFeatCode_Password, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Port;
  /** @generated */
  final int     casFeatCode_Port;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPort(int addr) {
        if (featOkTst && casFeat_Port == null)
      jcas.throwFeatMissing("Port", "objects.DatabaseConnection");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Port);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPort(int addr, String v) {
        if (featOkTst && casFeat_Port == null)
      jcas.throwFeatMissing("Port", "objects.DatabaseConnection");
    ll_cas.ll_setStringValue(addr, casFeatCode_Port, v);}
    
  
 
  /** @generated */
  final Feature casFeat_DatabaseType;
  /** @generated */
  final int     casFeatCode_DatabaseType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDatabaseType(int addr) {
        if (featOkTst && casFeat_DatabaseType == null)
      jcas.throwFeatMissing("DatabaseType", "objects.DatabaseConnection");
    return ll_cas.ll_getStringValue(addr, casFeatCode_DatabaseType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDatabaseType(int addr, String v) {
        if (featOkTst && casFeat_DatabaseType == null)
      jcas.throwFeatMissing("DatabaseType", "objects.DatabaseConnection");
    ll_cas.ll_setStringValue(addr, casFeatCode_DatabaseType, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public DatabaseConnection_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_DatabaseServer = jcas.getRequiredFeatureDE(casType, "DatabaseServer", "uima.cas.String", featOkTst);
    casFeatCode_DatabaseServer  = (null == casFeat_DatabaseServer) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_DatabaseServer).getCode();

 
    casFeat_Database = jcas.getRequiredFeatureDE(casType, "Database", "uima.cas.String", featOkTst);
    casFeatCode_Database  = (null == casFeat_Database) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Database).getCode();

 
    casFeat_UserName = jcas.getRequiredFeatureDE(casType, "UserName", "uima.cas.String", featOkTst);
    casFeatCode_UserName  = (null == casFeat_UserName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_UserName).getCode();

 
    casFeat_Password = jcas.getRequiredFeatureDE(casType, "Password", "uima.cas.String", featOkTst);
    casFeatCode_Password  = (null == casFeat_Password) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Password).getCode();

 
    casFeat_Port = jcas.getRequiredFeatureDE(casType, "Port", "uima.cas.String", featOkTst);
    casFeatCode_Port  = (null == casFeat_Port) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Port).getCode();

 
    casFeat_DatabaseType = jcas.getRequiredFeatureDE(casType, "DatabaseType", "uima.cas.String", featOkTst);
    casFeatCode_DatabaseType  = (null == casFeat_DatabaseType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_DatabaseType).getCode();

  }
}



    
