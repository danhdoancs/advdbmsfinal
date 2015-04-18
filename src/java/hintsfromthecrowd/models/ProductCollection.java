/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.models;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import edu.stanford.nlp.ling.TaggedWord;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author david
 */
public class ProductCollection {

    public DBCollection collectionProducts;
    public DBCursor cursorProductions;
    public DB db;

    public ProductCollection() throws UnknownHostException {

        db = MongoDb.getInstance().db;
        collectionProducts = db.getCollection("Products");
    }

    public void initFindAll() {
        cursorProductions = collectionProducts.find();
    }

    public void clearProducts() {
        //drop collection
        collectionProducts.drop();
        collectionProducts = db.getCollection("Products");
    }

    public void insertProduct(String productId, String productTitle, String productUrl, List<List<TaggedWord>> reviewTermsets, int productLength) {

        //create attr reviews for product
        List<BasicDBObject> reviews = new ArrayList<>();

        Integer rId = 0;

        //loop each review termset
        for (Iterator<List<TaggedWord>> it1 = reviewTermsets.iterator(); it1.hasNext();) {
            List<TaggedWord> reviewTermset = it1.next();

            //reset tId;
            Integer tId = 0;

            //att terms of review Object
            List<BasicDBObject> terms = new ArrayList<>();

            //create DBOjbect for each term
            for (Iterator<TaggedWord> it = reviewTermset.iterator(); it.hasNext();) {
                TaggedWord term = it.next();

                BasicDBObject termObject = new BasicDBObject("tid", tId++)
                        .append("tname", term.word())
                        .append("tposition", term.beginPosition());

                //add into array
                terms.add(termObject);
            }

            //create new review object
            BasicDBObject reviewObject = new BasicDBObject("rid", rId++);
            reviewObject.put("terms", terms);

            //add into array reviews
            reviews.add(reviewObject);
        }

        BasicDBObject productObject = new BasicDBObject("pid", productId)
                .append("pname", productTitle == null ? productId : productTitle)
                .append("plength", productLength)
                .append("plink", productUrl);
        productObject.put("reviews", reviews);

        //insert
        collectionProducts.insert(productObject);
        
        System.out.println("Inserted product " + productId);
    }

    public DBObject getNextProduct() throws MongoException {

        if (cursorProductions.hasNext()) {
            return cursorProductions.next();
        } else {
            return null;
        }
    }

    public void printAllWithoutTerms() {
        System.out.println("Products collection:");
        initFindAll();
        while (cursorProductions.hasNext()) {
            DBObject pd = cursorProductions.next();
            System.out.format("Product: id: %s, name: %s, length: %d\n", (String)pd.get("pid"), (String)pd.get("pname"), (int)pd.get("plength"));
        }
    }
    
    public void printAll() {
        System.out.println("Products collection:");
        initFindAll();
        while (cursorProductions.hasNext()) {
            System.out.println(cursorProductions.next().toString());
        }
    }
    public int getDocLength(String pName) {
        int noOfDoc=0;
        BasicDBObject query = new BasicDBObject();
        query.put("pname", pName);
        cursorProductions = collectionProducts.find(query);
        while (cursorProductions.hasNext()) {
            noOfDoc = Integer.parseInt(cursorProductions.next().get("plength").toString());
        }
        return noOfDoc;
    }
}
