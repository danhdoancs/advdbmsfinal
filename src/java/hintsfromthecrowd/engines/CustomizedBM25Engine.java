/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.engines;

/**
 *
 * @author david
 */
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import edu.stanford.nlp.ling.TaggedWord;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import hintsfromthecrowd.models.MongoDb;
import hintsfromthecrowd.models.Product;
import hintsfromthecrowd.models.ProductCollection;
import hintsfromthecrowd.models.TermCollection;
import hintsfromthecrowd.models.StanfordParser;
import hintsfromthecrowd.models.StopWords;
import hintsfromthecrowd.models.SummaryCollection;
import hintsfromthecrowd.models.WordNet;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;

public class CustomizedBM25Engine {

    //helper function to calculate score

    public boolean updateList(List<Product> productList, String productNew, double scoreNew) {
        boolean found = false;
        for (Product product : productList) {
            if (product.productName.compareTo(productNew) == 0) {
                found = true;
                product.score = product.score + scoreNew;
                return found;
            }
        }
        if (found == false) {
            Product newProduct = new Product();
            newProduct.productName = productNew;
            newProduct.score = scoreNew;
            productList.add(newProduct);
        }
        return found;
    }

    public int partition(List<Product> productList, int left, int right) {
        int i = left, j = right;
        Product tmp;
        double pivot = (productList.get(left).score + productList.get(right).score) / 2;

        while (i <= j) {
            while (productList.get(i).score < pivot) {
                i++;
            }
            while (productList.get(j).score > pivot) {
                j--;
            }
            if (i <= j) {
                tmp = productList.get(i);
                productList.set(i, productList.get(j));
                productList.set(j, tmp);
                i++;
                j--;
            }
        }
        return i;
    }
    void quickSort(List<Product>productList, int left, int right) {
        int index = partition(productList, left, right);
        if (left < index - 1) {
            quickSort(productList, left, index - 1);
        }
        if (index < right) {
            quickSort(productList, index, right);
        }
    }
    public List<Product> run(String query) throws IOException {
        
        //lowercase all
        query = query.toLowerCase();
        
        //init data connection
        MongoDb mongo = new MongoDb();
        TermCollection productCollection = new TermCollection();
        productCollection.initTermCursor();
        ProductCollection productColl = new ProductCollection();
        productColl.initFindAll();
        DBObject dbProduct;
        //Stanford Parser
        List<List<TaggedWord>> queryTermsets = new StanfordParser().posTagString(query);
        //Stopword
        StopWords stopWords = new StopWords();
        List<TaggedWord> queryTermset = stopWords.removeStopWords(queryTermsets);
        
        List<Product> scoreProduct = new ArrayList<>();
        scoreProduct = (ArrayList)proccessData(productCollection,queryTermset, productColl);
        if(scoreProduct.size()>0){
            quickSort(scoreProduct, 0, scoreProduct.size() - 1);
            int i = 10;
            for (Product pro : scoreProduct) {
                System.out.println("score: " + pro.score + " productName: " + pro.productName);
            }
        }
        return scoreProduct;
    }
    //loop all term in query and calculate score 
    public List<Product> proccessData(TermCollection productCollection,List<TaggedWord> queryTermset,ProductCollection productColl ) throws UnknownHostException{
        SummaryCollection sumColl = new SummaryCollection();
        double noOfDoc =Double.parseDouble(sumColl.getData().get(0));
        double avdl = Double.parseDouble(sumColl.getData().get(1));
        int countProduct=0;//number of Doc contain term
        //double score =0;
        List<Product> productTermList = new ArrayList();
        for (TaggedWord term : queryTermset) {
            BasicDBObject productOBJ = productCollection.getProductInfo(term.word());
            if (productOBJ.size() != 0) {
                //String tname = productOBJ.get("tname").toString();
                //System.out.print(tname + ": ");
                // String tid= cursorTerms.curr().get("pid").toString();
                // System.out.println(tid);
                int docLength =0;
                Double ocurr=0.0;
                double k = 1.5;
                BasicDBList productList = (BasicDBList) productOBJ.get("products");
                countProduct = productList.size();
                for (int i = 0; i < productList.size(); i++) {
                    BasicDBObject product = (BasicDBObject) productList.get(i);
                    String pid = product.get("pid").toString();
                    docLength = productColl.getDocLength(pid);
                    //System.out.print(pid + " + ");
                    ocurr = Double.parseDouble(product.get("occur").toString());
                    //System.out.println(ocurr + " !");
                    double score =  k*ocurr/(ocurr+k*(1-0.75+0.75*(docLength/avdl)))*Math.log((noOfDoc+1)/countProduct);
                    updateList(productTermList, pid, score);
                    
                }
                
            }
        }
        return productTermList;
    }

    public double score(String queryTerm) {

        return 0;
    }

}
