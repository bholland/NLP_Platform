package tf_idf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.api.ops.impl.transforms.strict.ACos;

public class main_class {
    static Logger logger = Logger.getLogger(main_class.class.getName());

    
    public main_class(String[] args) throws Exception {
        
        DocumentIterator doc_iter = new DocumentIterator();
        TokenizerFactory tf = new DefaultTokenizerFactory();
        
        
        TfidfVectorizer vec = new TfidfVectorizer.Builder()
        .setIterator(doc_iter)
        .setMinWordFrequency(0)
        .setStopWords(new ArrayList<String>())
        .setTokenizerFactory(tf)
        .build();
        
        vec.fit();
        
        FileOutputStream f = new FileOutputStream(new File("tf_idf.bin"));
        try (ObjectOutputStream o = new ObjectOutputStream(f)) {
        	o.writeObject(vec);
        }
        
        
        DatabaseText campaign_text = new DatabaseText("select_mass_campaign_text");
        ArrayList<INDArray> text_mat_tmp = new ArrayList<INDArray>();
        
        while (campaign_text.hasNext()) {
            String text = campaign_text.next();
            Integer id = campaign_text.get_id();
            
            INDArray array = vec.transform(text);
            text_mat_tmp.add(array);
        }
        
        INDArray text_mat = Nd4j.vstack(text_mat_tmp);
        
        DatabaseText comment_text = new DatabaseText("select_mass_campaign_text");
        while (comment_text.hasNext()) {
            String text = comment_text.next();
            Integer id = comment_text.get_id();
            
            INDArray array = vec.transform(text);
            
            for (INDArray  x: text_mat_tmp) {
                double [] s1 = array.data().asDouble();
                double sum_1 = 0;
                for (double d: s1 ) {
                    sum_1 = sum_1 + d*d;
                }
                sum_1 = Math.sqrt(sum_1);
                
                double [] s2 = x.data().asDouble();
                double sum_2 = 0;
                for (double d: s2 ) {
                    sum_2 = sum_2 + d*d;
                }
                sum_2 = Math.sqrt(sum_2);
                
                double scaling = sum_1 * sum_2;
                
                INDArray dot_prod = array.mmul(x.transpose());
                INDArray cos_theta = dot_prod.div(scaling);
                
                
                INDArray out = Nd4j.create(1,1);
                Nd4j.getExecutioner().exec(new ACos(cos_theta, out));
                System.out.println(String.format("Dist1: %s cos_theta: %s, out: %s", array.distance2(x), cos_theta, Math.toDegrees(out.getDouble(0)) ));
            }
            /*
            INDArray cos_dist = array.mmul(text_mat.transpose());
            for (int x = 0; x < cos_dist.columns(); x++) {
                System.out.println(String.format("%s: %s", x, cos_dist.getDouble(x)));
            }
            
            
            System.out.println(scaling);
            
            System.out.println(array.div(scaling));
            */
            return;
        }
        
        System.out.println("It actually worked.");
        

    }
    
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("/home/ben/workspace/opennlp_processing/src/tf_idf/log.conf");
        new main_class(args);
    }
}
