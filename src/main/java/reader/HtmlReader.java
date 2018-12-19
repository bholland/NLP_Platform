package reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlReader extends Reader_ImplBase {
    
    public HtmlReader() { }
    
    @Override
    public void ReadText() {
        // TODO Auto-generated method stub
        
    }
    
    public ArrayList<TextObject> ReadText(File file, boolean isModelData, boolean isSourceData) throws IOException {
        String text = "";
        Elements elms = Jsoup.parse(file, "UTF-8").select("*");
        for (Element e : elms) {
            String t = e.ownText().trim();
            if (t.length() > 0) {
                text = String.format("%s%s\n", text, t);
            }
        }
        System.out.println(text);
        ArrayList<TextObject> ret = new ArrayList<TextObject>();
        if (isModelData == true) {
            TextObject text_object = new TextObject();
            text_object.setId(file.getName());
            text_object.setText(text);
            text_object.setIsModelData(true);
            ret.add(text_object);
        }
        if (isSourceData == true) {
            TextObject text_object = new TextObject();
            text_object.setId(file.getName());
            text_object.setText(text);
            text_object.setIsModelData(false);
            ret.add(text_object);
        }
        return ret;
    }
}
