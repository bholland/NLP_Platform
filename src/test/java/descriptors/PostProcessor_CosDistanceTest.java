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
package descriptors;

import junit.framework.*;
import tf_idf.DocumentIteratorFromDatabase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNotNull;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

import database.DatabaseConnector;

public class PostProcessor_CosDistanceTest {
    
    private static String type;
    private static String database_server; 
    private static String port;
    private static String database;
    private static String user_name;
    private static String password;
    
    @BeforeClass
    public static void setUp() throws SQLException {
        type = "pgsql";
        database_server = "localhost";
        port = "5432";
        database = "nlp_testing";
        user_name = "ben";
        password = "password";
        
        Connection c;
        
        c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", user_name, password);
        Statement s = c.createStatement();
        
        String terminate = String.format("SELECT pg_terminate_backend(pid)" + 
                "            FROM pg_stat_activity " + 
                "            WHERE datname = '%s';", database);
        s.executeQuery(terminate);
        s = c.createStatement();
        s.executeUpdate(String.format("DROP DATABASE IF EXISTS %s", database));
        
        String statement_str = String.format("CREATE DATABASE %s" + 
                "    WITH " + 
                "    OWNER = ben " + 
                "    TEMPLATE = nlp_template " +
                "    ENCODING = 'UTF8'" + 
                "    LC_COLLATE = 'en_US.UTF-8'" + 
                "    LC_CTYPE = 'en_US.UTF-8'" + 
                "    TABLESPACE = pg_default" + 
                "    CONNECTION LIMIT = -1;", database);
        s.executeUpdate(statement_str);
    }
    
    @AfterClass
    public static void tearDown() throws SQLException {
        try {
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", user_name, password);
            Statement s = c.createStatement();
            s.executeUpdate(String.format("DROP DATABASE %s", database));
        } catch (SQLException e) {
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", user_name, password);
            Statement s = c.createStatement();
            String terminate = String.format("SELECT pg_terminate_backend(pid)" + 
                    "            FROM pg_stat_activity " + 
                    "            WHERE datname = '%s';", database);
            s.executeQuery(terminate);
            
            s.executeUpdate(String.format("DROP DATABASE %s", database));
        }
    }
    
    @Before
    public void setUpMethods() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/nlp_testing", user_name, password)) {
            Statement statement_clean = connection.createStatement();
            statement_clean.execute("TRUNCATE source_text CASCADE");
            statement_clean.execute("TRUNCATE category_text CASCADE");
            statement_clean.execute("ALTER SEQUENCE source_text_id_seq RESTART WITH 1");
            statement_clean.execute("ALTER SEQUENCE category_text_id_seq RESTART WITH 1");
            
            CallableStatement statement = connection.prepareCall("{call insert_source_text(?, ?)}");
            statement.setString(1, "1");
            statement.setString(2, "Testing source one.");
            statement.execute();
            
            statement = connection.prepareCall("{call insert_source_text(?, ?)}");
            statement.setString(1, "2");
            statement.setString(2, "Testing source two.");
            statement.execute();
            
            
            statement = connection.prepareCall("{call insert_category_text(?, ?)}");
            statement.setString(1, "1");
            statement.setString(2, "Testing category one.");
            statement.execute();
            
            statement = connection.prepareCall("{call insert_category_text(?, ?)}");
            statement.setString(1, "1");
            statement.setString(2, "Testing category two.");
            statement.execute();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @After
    public void tearDownMethods() throws SQLException {
        
    }
    
    @Test 
    public void connectionTriGuideme() throws SQLException{
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/tri_guideme", "ben", "password");
        connection.close();
    }
    
    @Test 
    public void connectionNlp() throws SQLException{
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/nlp_template", "ben", "password");
        connection.close();
    }
    
    @Test
    public void testDatabasePassAll() throws SQLException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password);
        assertEquals(annotator.getType(), type);
        assertEquals(annotator.getPort(), port);
        
    }
    
    @Test
    public void testDatabasePassPort() throws SQLException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        annotator.getDatabaseConnector(null, database_server, 
                port, database, user_name, password);
        assertEquals(annotator.getType(), type);
        assertEquals(annotator.getPort(), port);
    }
    
    @Test
    public void testDatabasePassType() throws SQLException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        annotator.getDatabaseConnector(type, database_server, 
                null, database, user_name, password);
        assertEquals(annotator.getType(), type);
        assertEquals(annotator.getPort(), port);
    }
    
    @Test
    public void testDatabasePassNeither() throws ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        try {
            annotator.getDatabaseConnector(null, database_server, 
                    null, database, user_name, password);
            fail("A user did not pass the port or the type. It should fail here.");
        } catch (SQLException e) { 
        }
    }
    
    @Test
    public void testDatabaseConnection() throws SQLException, IOException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password);
        connector.connect();
        Connection connection = connector.getConnection();
        connector.close();
    }
    
    
    @Test
    public void testInsertSource() throws SQLException, IOException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        try (DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                    port, database, user_name, password)) {
            connector.connect();
            Connection connection = connector.getConnection();
            CallableStatement statement = connection.prepareCall("{call insert_source_text(?, ?)}");
            statement.setString(1, "1");
            statement.setString(2, "This is a test of the tf-idf (source).");
            statement.executeQuery();
            ResultSet rs = statement.getResultSet();
            
            int test = 0;
            while (rs.next()) {
                int id = rs.getInt(1);
                String delete = String.format("delete from source_text where id=%s", id); 
                Statement s = connection.createStatement();
                s.executeUpdate(delete);
                test++;
            }
            
            assertTrue("Insert category text should return the id.", test == 1);
            
            connector.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Test
    public void testInsertCategory() throws SQLException, IOException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        try (DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                    port, database, user_name, password)) {
            connector.connect();
            Connection connection = connector.getConnection();
            CallableStatement statement = connection.prepareCall("{call insert_category_text(?, ?)}");
            statement.setString(1, "1");
            statement.setString(2, "This is a test of the tf-idf (category).");
            statement.executeQuery();
            
            ResultSet rs = statement.getResultSet();
            
            int test = 0;
            while (rs.next()) {
                int id = rs.getInt(1);
                String delete = String.format("delete from category_text where id=%s", id); 
                Statement s = connection.createStatement();
                s.executeUpdate(delete);
                test++;
            }
            
            assertTrue("Insert category text should return the id.", test == 1);
    
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Test
    public void testDatabaseSortedClean() throws SQLException, IOException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        try (DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                    port, database, user_name, password)) {
            connector.connect();
            Connection connection = connector.getConnection();
            annotator.CleanSortedIndex(connection);
            connector.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private VocabCache<VocabWord> makeVecCategory() throws IOException, ResourceInitializationException, SQLException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        try (DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                    port, database, user_name, password)) {
            connector.connect();
            Connection connection = connector.getConnection();
            
            DocumentIteratorFromDatabase doc_iter = new DocumentIteratorFromDatabase(connection, true);
            TfidfVectorizer vec = annotator.fitVectorized(connection, doc_iter);
            
            VocabCache<VocabWord> cache = vec.getVocabCache();
            return cache;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private VocabCache<VocabWord> makeVecSource() throws IOException, ResourceInitializationException, SQLException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        try (DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                    port, database, user_name, password)) {
            connector.connect();
            Connection connection = connector.getConnection();
            
            DocumentIteratorFromDatabase doc_iter = new DocumentIteratorFromDatabase(connection, false);
            TfidfVectorizer vec = annotator.fitVectorized(connection, doc_iter);
            
            VocabCache<VocabWord> cache = vec.getVocabCache();
            return cache;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private INDArray makeINDArraySource() throws IOException, ResourceInitializationException, SQLException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        
        try (DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password)) {
            connector.connect();
            Connection connection = connector.getConnection();
            
            DocumentIteratorFromDatabase doc_iter = new DocumentIteratorFromDatabase(connection, false);
            TfidfVectorizer vec = annotator.fitVectorized(connection, doc_iter);
            return annotator.makeINDArray(vec, doc_iter);
                    
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private INDArray makeINDArrayCategory() throws IOException, ResourceInitializationException, SQLException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        
        try (DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password)) {
            connector.connect();
            Connection connection = connector.getConnection();
            
            DocumentIteratorFromDatabase doc_iter = new DocumentIteratorFromDatabase(connection, true);
            TfidfVectorizer vec = annotator.fitVectorized(connection, doc_iter);
            return annotator.makeINDArray(vec, doc_iter);
                    
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Test
    public void testINDArraySource() throws IOException, ResourceInitializationException, SQLException, ClassNotFoundException {
        INDArray array = makeINDArraySource();
        //There should only be a single value that is non-zero somewhere in this array. 
        for (int x = 0; x < array.rows(); x++) {
            boolean one = false;
            INDArray row = array.getRow(x);
            for (int y = 0; y < row.columns(); y++) {
                if ( row.getFloat(y) > 0.000001) {
                    assertEquals(1, row.getFloat(y), 0.000001);
                    one = true;
                }
            }
            assertTrue("A row in the test dataset did not have a 1 value.", one);
        }
    }
    
    @Test
    public void testINDArrayCategory() throws IOException, ResourceInitializationException, SQLException, ClassNotFoundException {
        INDArray array = makeINDArrayCategory();
        //There should only be a single value that is non-zero somewhere in this array. 
        for (int x = 0; x < array.rows(); x++) {
            boolean one = false;
            INDArray row = array.getRow(x);
            for (int y = 0; y < row.columns(); y++) {
                if ( row.getFloat(y) > 0.000001) {
                    assertEquals(1, row.getFloat(y), 0.000001);
                    one = true;
                }
            }
            assertTrue("A row in the test dataset did not have a 1 value.", one);
        }
    }
 
    @Test
    public void testCreateVecSourceTesting() throws SQLException, IOException, ResourceInitializationException, ClassNotFoundException {
        VocabCache<VocabWord> cache = makeVecSource();
        VocabWord word = cache.tokenFor("testing");
        assertNull(word);
    }
    
    @Test
    public void testCreateVecSourceSource() throws SQLException, IOException, ResourceInitializationException, ClassNotFoundException {
        VocabCache<VocabWord> cache = makeVecSource();
        VocabWord word = cache.tokenFor("source");
        assertNull(word);
    }
    
    @Test
    public void testCreateVecSourceSource_Lemma() throws SQLException, IOException, ResourceInitializationException, ClassNotFoundException {
        VocabCache<VocabWord> cache = makeVecSource();
        VocabWord word = cache.tokenFor("sourc");
        assertNotNull(word);
    }
    
    @Test
    public void testCreateVecSourceOne() throws SQLException, IOException, ResourceInitializationException, ClassNotFoundException {
        VocabCache<VocabWord> cache = makeVecSource();
        VocabWord word = cache.tokenFor("one");
        assertNotNull(word);
    }
    
    @Test
    public void testCreateVecSourceTwo() throws SQLException, IOException, ResourceInitializationException, ClassNotFoundException {
        VocabCache<VocabWord> cache = makeVecSource();
        VocabWord word = cache.tokenFor("two");
        assertNotNull(word);
    }
    
    @Test
    public void testCreateVecSourcePeriod() throws SQLException, IOException, ResourceInitializationException, ClassNotFoundException {
        VocabCache<VocabWord> cache = makeVecSource();
        VocabWord word = cache.tokenFor(".");
        assertNotNull(word);
    }
    
    @Test
    public void testGetCategoryIDs() throws SQLException, IOException, ResourceInitializationException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        
        try (DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password)) {
            connector.connect();
            Connection connection = connector.getConnection();
            int[] category_text_ids = annotator.getCategoryTextIDs(connection); 
            assertNotNull(category_text_ids);
            assertEquals(2, category_text_ids.length);
        }
    }
    
    @Test
    public void testGetSourceIDs() throws SQLException, IOException, ResourceInitializationException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        
        try (DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password)) {
            connector.connect();
            Connection connection = connector.getConnection();
            ArrayList<Integer> category_text_ids = annotator.getSourceTextIDs(connection); 
            assertNotNull(category_text_ids);
            assertEquals(2, category_text_ids.size());
        }
    }
    
    @Test
    public void testGetSourceTextOne() throws IOException, SQLException, CollectionException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        try (DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password)) {
            connector.connect();
            Connection connection = connector.getConnection();
            String text = annotator.getSourceTextFromID(connection, 1);
            assertEquals("Testing source one.", text);
        }
    }
    
    @Test
    public void testGetSourceTextTwo() throws IOException, SQLException, CollectionException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        try (DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password)) {
            connector.connect();
            Connection connection = connector.getConnection();
            String text = annotator.getSourceTextFromID(connection, 2);
            assertEquals("Testing source two.", text);
        }
    }
    
    @Test
    public void testGetSourceTextAssert() throws IOException, SQLException, ClassNotFoundException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        try (DatabaseConnector connector = annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password)) {
            connector.connect();
            Connection connection = connector.getConnection();
            String text = annotator.getSourceTextFromID(connection, 3);
            assertTrue(false);
        } catch (CollectionException e) {
            assertTrue(true);
        }
    }
}
