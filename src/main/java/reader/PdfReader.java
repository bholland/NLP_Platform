package reader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.ExtractText;
import org.apache.uima.jcas.JCas;

import objects.PdfObject;

public class PdfReader extends Reader_ImplBase {
    
    private PdfObject mPdfObject = null;
    
    public PdfReader(PdfObject pdfObject) {
        mPdfObject = pdfObject;
    }
        
    public PdfReader() { }

    public ArrayList<TextObject> ReadText(File file, boolean isModelData, boolean isSourceData) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            
            boolean toConsole = false;
            boolean toHTML = false;
            boolean sort = false;
            boolean separateBeads = true;
            String password = "";
            String encoding = "UTF-8";
            String pdfFile = null;
            String outputFile = null;
            
            String ext = ".txt";
            int startPage = 1;
            int endPage = Integer.MAX_VALUE;
            
            stripper.setSortByPosition( sort );
            stripper.setShouldSeparateByBeads( separateBeads );
            stripper.setStartPage( startPage );
            stripper.setEndPage( endPage );
            
            System.out.println(String.format("Working on: %s", file.getAbsolutePath()));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(output, Charset.forName("UTF-8").newEncoder());
            stripper.writeText( document, writer );
            writer.flush();
            String document_text = output.toString("UTF-8");
            
            CharSequence target_regs_1 = " regs";
            CharSequence replacement_regs_1 = " regulations";
            document_text = document_text.replace(target_regs_1, replacement_regs_1);
            
            CharSequence target_regs_2 = "regs ";
            CharSequence replacement_regs_2 = "regulations ";
            document_text = document_text.replace(target_regs_2, replacement_regs_2);
            
            ArrayList<TextObject> ret = new ArrayList<TextObject>();
            if (isModelData == true) {
                TextObject text_object = new TextObject();
                text_object.setId(file.getName());
                text_object.setText(document_text);
                text_object.setIsModelData(true);
                ret.add(text_object);
            }
            if (isSourceData == true) {
                TextObject text_object = new TextObject();
                text_object.setId(file.getName());
                text_object.setText(document_text);
                text_object.setIsModelData(false);
                ret.add(text_object);
            }
            return ret;
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void ReadText() {
        // TODO Auto-generated method stub
        
    }

}
