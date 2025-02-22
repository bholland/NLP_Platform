package descriptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.svenjacobs.loremipsum.LoremIpsum;
import helper.DatabaseHelper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class CSV_Insert_CPETest {
	private static String type;
    private static String database_server; 
    private static String port;
    private static String database;
    private static String database_connection;
    private static String user_name;
    private static String password;
    
    /**
     * document without category information
     * document with category information
     */
    private static String csv_filename_no_cat;
    private static String csv_filename_cat;
	
	@Before
    public void setUp() throws SQLException {
		type = "pgsql";
        database_server = "localhost";
        port = "5432";
        database = "db_csv_testing";
        user_name = "ben";
        password = "password";
        database_connection = String.format("jdbc:postgresql://localhost:5432/%s", database);
        
        File csv_filename_no_cat_fh = new File("test_csv_no_cat.csv");
        File csv_filename_cat_fh = new File("test_csv_cat.csv");
        
        csv_filename_no_cat = csv_filename_no_cat_fh.getAbsolutePath();
        csv_filename_cat = csv_filename_cat_fh.getAbsolutePath();
        
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
        
        LoremIpsum li = new LoremIpsum();
        try(BufferedWriter csv_file_output = new BufferedWriter(new FileWriter(csv_filename_no_cat_fh))) {
        	CSVPrinter csvPrinter = new CSVPrinter(csv_file_output, CSVFormat.DEFAULT
                    .withHeader("ID", "Text"));
        	csvPrinter.printRecord("1", li.getParagraphs(2));
            csvPrinter.printRecord("2", li.getParagraphs(1));
            csvPrinter.printRecord("3", li.getParagraphs(4));
            csvPrinter.flush();
            csvPrinter.close();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try(BufferedWriter csv_file_output = new BufferedWriter(new FileWriter(csv_filename_cat_fh))) {
        	CSVPrinter csvPrinter = new CSVPrinter(csv_file_output, CSVFormat.DEFAULT
                    .withHeader("ID", "Text", "category", "in_category"));
        	csvPrinter.printRecord("1", li.getParagraphs(2), "null", "false");
            csvPrinter.printRecord("2", li.getParagraphs(1), "cat1", "true");
            csvPrinter.printRecord("3", li.getParagraphs(4), "none", "false");
            csvPrinter.flush();
            csvPrinter.close();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        c.close();
        
        /**
         * setting up the nlp_stack user 
         */
        c = DriverManager.getConnection(database_connection, user_name, password);
        s = c.createStatement();
        String insert_str = String.format("insert into users" +
        		"	(id, username, email, first_name, last_name)" +
        		"	values" +
        		"	(14, 'nlp_stack', 'admin@nlp_stack.com', null, null);");
        
        s.execute(insert_str);
        c.close();
	}
	
	@After
    public void tearDown() throws SQLException {
		File f = new File(csv_filename_no_cat);
		f.delete();
		
		try {
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", user_name, password);
            Statement s = c.createStatement();
            s.executeUpdate(String.format("DROP DATABASE %s", database));
            c.close();
        } catch (SQLException e) {
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", user_name, password);
            Statement s = c.createStatement();
            String terminate = String.format("SELECT pg_terminate_backend(pid)" + 
                    "            FROM pg_stat_activity " + 
                    "            WHERE datname = '%s';", database);
            s.executeQuery(terminate);
            
            s.executeUpdate(String.format("DROP DATABASE %s", database));
            c.close();
        }
	}
	
	//insertCSVFile(Connection connection, String csv_file, String id_column, 
	//	String text_column, boolean is_model_text, String category, 
	//   String is_in_category_column) 
	
	@Test
    public void testinsertCSVFile_No_Category() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		Integer user_id = 14;
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			cpe.insertCSVFile(connection, user_id, csv_filename_no_cat, "ID", "Text", false, null, null);
        }
	}
	
	@Test
	public void testCSVInsertDriver() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		String[] file_names = {csv_filename_no_cat};
		String[] id_columns = {"ID"};
		String[] text_columns = {"Text"};
		Boolean[] is_model_data = {false};
		String[] csv_category_column = {"None"};
		String[] is_in_category_column = {"None"};
		Integer user_id = 14;
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			cpe.CSVInsertDriver(connection, user_id, file_names, id_columns, text_columns, is_model_data, csv_category_column, is_in_category_column);
		}
	}
	
	@Test(expected = SQLException.class)
    public void testinsertCSVFile_With_Category() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			cpe.insertCSVFile(connection, 14, csv_filename_no_cat, "ID", "Text", true, null, null);
        }
	}
	
	@Test(expected = IllegalArgumentException.class)
    public void testinsertCSVFile_Throws_Error() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			cpe.insertCSVFile(connection, 14, csv_filename_no_cat, "ID", "Text", true, null, "in_category");
        }
	}
	
	@Test(expected = SQLException.class)
    public void testinsertCSVFile_is_in_category_column_true_not_cat() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			cpe.insertCSVFile(connection, 14, csv_filename_cat, "ID", "Text", true, null, "in_category");
        }
	}

	@Test
    public void testinsertCSVFile_is_in_category_column_true_with_cat() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			cpe.insertCSVFile(connection, 14, csv_filename_cat, "ID", "Text", true, "category", "in_category");
        }
	}

	@Test(expected = SQLException.class)
    public void testinsertCSVFile_is_in_category_column_false() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			cpe.insertCSVFile(connection, 14, csv_filename_cat, "ID", "Text", true, null, "in_category");
        }
	}
	
	@Test
    public void testinsertCSVFile_is_in_category_column_1_with_cat() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			cpe.insertCSVFile(connection, 14, csv_filename_cat, "ID", "Text", true, "category", "in_category");
        }
	}
	
	@Test(expected = SQLException.class)
    public void testinsertCSVFile_bad_model_value() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			cpe.insertCSVFile(connection, 14, csv_filename_no_cat, "ID", "Text", true, null, null);
		}
	}
	
	@Test
    public void testinsertCSVFile_added_source() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			cpe.insertCSVFile(connection, 14, csv_filename_no_cat, "ID", "Text", false, null, null);
			
			PreparedStatement ps = connection.prepareStatement("SELECT count(*) from document_text");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int count = rs.getInt(1);
				assertEquals(count, 3);
			}
			
			ps = connection.prepareStatement("SELECT count(*) from category_text");
			rs = ps.executeQuery();
			while (rs.next()) {
				int count = rs.getInt(1);
				assertEquals(count, 0);
			}
        }
	}
	
	@Test
    public void testinsertCSVFileFilePathTest() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			cpe.insertCSVFile(connection, 14, csv_filename_cat, "ID", "Text", false, null, null);
			
			ArrayList<String> unprocessed = DatabaseHelper.selectUnprocessedFiles(connection, 14);
			assertEquals(1, unprocessed.size());
			assertEquals(csv_filename_cat, unprocessed.get(0));
			
			ArrayList<String> procssed = DatabaseHelper.selectProcessedFiles(connection, 14);
			assertEquals(0, procssed.size());
			
			
			
			/*PreparedStatement ps = connection.prepareStatement("SELECT count(*) from document_text");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int count = rs.getInt(1);
				assertEquals(count, 3);
			}
			
			ps = connection.prepareStatement("SELECT count(*) from category_text");
			rs = ps.executeQuery();
			while (rs.next()) {
				int count = rs.getInt(1);
				assertEquals(count, 0);
			}*/
        }
	}
	
	@Test
    public void testinsertCSVFileFilePathDoesNotExistTest() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		
		String[] file_names = {csv_filename_no_cat};
		String[] id_columns = {"ID"};
		String[] text_columns = {"Text"};
		Boolean[] is_model_data = {false};
		String[] csv_category_column = {"None"};
		String[] is_in_category_column = {"None"};
		Integer user_id = 14;
		
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			
			String file_path_insert = String.format("insert into completed_processed_files (file_path, is_complete) values ('%s', FALSE);", csv_filename_cat);
			PreparedStatement ps = connection.prepareStatement(file_path_insert);
			ps.execute();
			
			ArrayList<String> unprocssed = DatabaseHelper.selectUnprocessedFiles(connection, 14);
			assertEquals(1, unprocssed.size());
			assertEquals(csv_filename_cat, unprocssed.get(0));
			
			ArrayList<String> procssed = DatabaseHelper.selectProcessedFiles(connection, 14);
			assertEquals(0, procssed.size());
			
			cpe.CSVInsertDriver(connection, user_id, file_names, id_columns, text_columns, is_model_data, csv_category_column, is_in_category_column);
			assertEquals(0, cpe.getProcessedFiles().size());
			assertEquals(1, cpe.getFilesToProcess().size());
        }
	}
	
	@Test
    public void testinsertCSVFileFilePathExistsTest() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		String[] file_names = {csv_filename_cat};
		String[] id_columns = {"ID"};
		String[] text_columns = {"Text"};
		Boolean[] is_model_data = {false};
		String[] csv_category_column = {"None"};
		String[] is_in_category_column = {"None"};
		Integer user_id = 14;
		
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			
			String file_path_insert = String.format("insert into completed_processed_files (file_path, is_complete) values ('%s', TRUE);", csv_filename_cat);
			PreparedStatement ps = connection.prepareStatement(file_path_insert);
			ps.execute();
			
			ArrayList<String> unprocssed = DatabaseHelper.selectUnprocessedFiles(connection, 14);
			assertEquals(0, unprocssed.size());
			
			ArrayList<String> procssed = DatabaseHelper.selectProcessedFiles(connection, 14);
			assertEquals(1, procssed.size());
			assertEquals(csv_filename_cat, procssed.get(0));
			
			cpe.CSVInsertDriver(connection, user_id, file_names, id_columns, text_columns, is_model_data, csv_category_column, is_in_category_column);
			assertEquals(1, cpe.getFilesToProcess().size());
			
			Statement s = connection.createStatement();
			String select_statement = "SELECT id, file_path, is_complete from completed_processed_files;";
			s.execute(select_statement);
			ResultSet rs = s.getResultSet();
			int row_count = 0;
			while (rs.next()) {
				Integer id = rs.getInt(1);
				String file_path = rs.getString(2);
				Boolean is_complete = rs.getBoolean(3);
				assertEquals("filepath does not equal input.", file_path, csv_filename_cat);
				assertTrue("is_complete should be true here", is_complete == true);
				row_count++;
			}
			assertTrue("There should only be a single row.", row_count == 1);
        }
	}
	
	@Test
    public void testClose() throws FileNotFoundException, SQLException, IOException, CollectionException, ResourceInitializationException {
		CSV_Insert_CPE cpe = new CSV_Insert_CPE();
		String[] file_names = {csv_filename_cat};
		String[] id_columns = {"ID"};
		String[] text_columns = {"Text"};
		Boolean[] is_model_data = {false};
		String[] csv_category_column = {"None"};
		String[] is_in_category_column = {"None"};
		Integer user_id = 14;
		
		try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
			
			String file_path_insert = String.format("insert into completed_processed_files (file_path, is_complete) values ('%s', FALSE);", csv_filename_cat);
			PreparedStatement ps = connection.prepareStatement(file_path_insert);
			ps.execute();
			
			ArrayList<String> unprocssed = DatabaseHelper.selectUnprocessedFiles(connection, 14);
			assertEquals(1, unprocssed.size());
			assertEquals(csv_filename_cat, unprocssed.get(0));
			
			ArrayList<String> procssed = DatabaseHelper.selectProcessedFiles(connection, 14);
			assertEquals(0, procssed.size());
			
			cpe.CSVInsertDriver(connection, user_id, file_names, id_columns, text_columns, is_model_data, csv_category_column, is_in_category_column);
			assertEquals(0, cpe.getProcessedFiles().size());
			assertEquals(1, cpe.getFilesToProcess().size());
			
			//cpe.close() only calls updatePaths(connection);
			cpe.updatePaths(connection);
			
			unprocssed = DatabaseHelper.selectUnprocessedFiles(connection, 14);
			assertEquals(0, unprocssed.size());
			
			procssed = DatabaseHelper.selectProcessedFiles(connection, 14);
			assertEquals(1, procssed.size());
			assertEquals(csv_filename_cat, procssed.get(0));
        }
	}
	
}
