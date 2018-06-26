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
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Apr 10 14:33:25 EDT 2018
 * XML source: /home/ben/workspace/opennlp_processing/desc/CollectionReaders/FolderReader.xml
 * @generated */
public class DatabaseConnection extends Annotation {
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
 
  /** Never called.  Disable default constructor
   * @generated */
  protected DatabaseConnection() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public DatabaseConnection(int addr, TOP_Type type) {
    super(addr, type);
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
  public String getDatabaseServer() {
    if (DatabaseConnection_Type.featOkTst && ((DatabaseConnection_Type)jcasType).casFeat_DatabaseServer == null)
      jcasType.jcas.throwFeatMissing("DatabaseServer", "objects.DatabaseConnection");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DatabaseConnection_Type)jcasType).casFeatCode_DatabaseServer);}
    
  /** setter for DatabaseServer - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDatabaseServer(String v) {
    if (DatabaseConnection_Type.featOkTst && ((DatabaseConnection_Type)jcasType).casFeat_DatabaseServer == null)
      jcasType.jcas.throwFeatMissing("DatabaseServer", "objects.DatabaseConnection");
    jcasType.ll_cas.ll_setStringValue(addr, ((DatabaseConnection_Type)jcasType).casFeatCode_DatabaseServer, v);}    
   
    
  //*--------------*
  //* Feature: Database

  /** getter for Database - gets 
   * @generated
   * @return value of the feature 
   */
  public String getDatabase() {
    if (DatabaseConnection_Type.featOkTst && ((DatabaseConnection_Type)jcasType).casFeat_Database == null)
      jcasType.jcas.throwFeatMissing("Database", "objects.DatabaseConnection");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DatabaseConnection_Type)jcasType).casFeatCode_Database);}
    
  /** setter for Database - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDatabase(String v) {
    if (DatabaseConnection_Type.featOkTst && ((DatabaseConnection_Type)jcasType).casFeat_Database == null)
      jcasType.jcas.throwFeatMissing("Database", "objects.DatabaseConnection");
    jcasType.ll_cas.ll_setStringValue(addr, ((DatabaseConnection_Type)jcasType).casFeatCode_Database, v);}    
   
    
  //*--------------*
  //* Feature: UserName

  /** getter for UserName - gets 
   * @generated
   * @return value of the feature 
   */
  public String getUserName() {
    if (DatabaseConnection_Type.featOkTst && ((DatabaseConnection_Type)jcasType).casFeat_UserName == null)
      jcasType.jcas.throwFeatMissing("UserName", "objects.DatabaseConnection");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DatabaseConnection_Type)jcasType).casFeatCode_UserName);}
    
  /** setter for UserName - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUserName(String v) {
    if (DatabaseConnection_Type.featOkTst && ((DatabaseConnection_Type)jcasType).casFeat_UserName == null)
      jcasType.jcas.throwFeatMissing("UserName", "objects.DatabaseConnection");
    jcasType.ll_cas.ll_setStringValue(addr, ((DatabaseConnection_Type)jcasType).casFeatCode_UserName, v);}    
   
    
  //*--------------*
  //* Feature: Password

  /** getter for Password - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPassword() {
    if (DatabaseConnection_Type.featOkTst && ((DatabaseConnection_Type)jcasType).casFeat_Password == null)
      jcasType.jcas.throwFeatMissing("Password", "objects.DatabaseConnection");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DatabaseConnection_Type)jcasType).casFeatCode_Password);}
    
  /** setter for Password - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPassword(String v) {
    if (DatabaseConnection_Type.featOkTst && ((DatabaseConnection_Type)jcasType).casFeat_Password == null)
      jcasType.jcas.throwFeatMissing("Password", "objects.DatabaseConnection");
    jcasType.ll_cas.ll_setStringValue(addr, ((DatabaseConnection_Type)jcasType).casFeatCode_Password, v);}    
   
    
  //*--------------*
  //* Feature: Port

  /** getter for Port - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPort() {
    if (DatabaseConnection_Type.featOkTst && ((DatabaseConnection_Type)jcasType).casFeat_Port == null)
      jcasType.jcas.throwFeatMissing("Port", "objects.DatabaseConnection");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DatabaseConnection_Type)jcasType).casFeatCode_Port);}
    
  /** setter for Port - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPort(String v) {
    if (DatabaseConnection_Type.featOkTst && ((DatabaseConnection_Type)jcasType).casFeat_Port == null)
      jcasType.jcas.throwFeatMissing("Port", "objects.DatabaseConnection");
    jcasType.ll_cas.ll_setStringValue(addr, ((DatabaseConnection_Type)jcasType).casFeatCode_Port, v);}    
   
    
  //*--------------*
  //* Feature: DatabaseType

  /** getter for DatabaseType - gets can either be mysql or pgsql
   * @generated
   * @return value of the feature 
   */
  public String getDatabaseType() {
    if (DatabaseConnection_Type.featOkTst && ((DatabaseConnection_Type)jcasType).casFeat_DatabaseType == null)
      jcasType.jcas.throwFeatMissing("DatabaseType", "objects.DatabaseConnection");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DatabaseConnection_Type)jcasType).casFeatCode_DatabaseType);}
    
  /** setter for DatabaseType - sets can either be mysql or pgsql 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDatabaseType(String v) {
    if (DatabaseConnection_Type.featOkTst && ((DatabaseConnection_Type)jcasType).casFeat_DatabaseType == null)
      jcasType.jcas.throwFeatMissing("DatabaseType", "objects.DatabaseConnection");
    jcasType.ll_cas.ll_setStringValue(addr, ((DatabaseConnection_Type)jcasType).casFeatCode_DatabaseType, v);}    
  }

    
