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
