/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.models;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;
import edu.stanford.nlp.ling.TaggedWord;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author david
 */
public class TermCollection {

    public DBCollection collectionTerms;
    public DB db;
    public DBCursor cursorTerms;
    public int termIndex;

    /**
     *
     */
    public Map<String, BasicDBObject> terms;
    public ArrayList updateList;

    public TermCollection() throws UnknownHostException {

        db = MongoDb.getInstance().db;
        collectionTerms = db.getCollection("Terms");

    }

    public void updateTerms(String productId, List<List<TaggedWord>> reviewTermsets) {
        terms = new HashMap();
        updateList = new ArrayList();

        //loop each review termset
        for (Iterator<List<TaggedWord>> it1 = reviewTermsets.iterator(); it1.hasNext();) {
            List<TaggedWord> reviewTermset = it1.next();

            //loop each term
            for (Iterator<TaggedWord> it = reviewTermset.iterator(); it.hasNext();) {
                TaggedWord term = it.next();

                updateTerm(term.word(), productId);
            }
        }
    }

    public void updateTerm(String termName, String productId) {

        BasicDBObject termObj = getTerm(termName);

        //not found in database
        if (termObj == null) {
//            termObj = new BasicDBObject("tid", termName)
//                    .append("tname", termName);
            termObj = new BasicDBObject("tname", termName)
                    .append("tnodoc", 1);
            BasicDBList products = new BasicDBList();
            products.add(new BasicDBObject("pid", productId).append("occur", 1));
            termObj.put("products", products);

        } else {

            //update Occurences
            BasicDBList occurences = (BasicDBList) termObj.get("products");

            boolean flagFound = false;
            for (Iterator<Object> it = occurences.iterator(); it.hasNext();) {
                BasicDBObject product = (BasicDBObject) it.next();

                if (product.get("pid").equals(productId)) {
                    product.put("occur", (int) product.get("occur") + 1);
                    flagFound = true;
                    break;
                }
            }

            //product not found
            if (!flagFound) {
                occurences.add(new BasicDBObject("pid", productId).append("occur", 1));
            }

            //add in updateList
            updateList.add(termObj);
        }

        //update term in set
        terms.put(termName, termObj);
    }

    public BasicDBObject getTerm(String termName) {

        //check buffer terms
        BasicDBObject termObj = terms.get(termName);
        if (termObj == null) {

            BasicDBObject query = new BasicDBObject();

            query.put("tname", termName);
            cursorTerms = collectionTerms.find(query);
            try {
                while (cursorTerms.hasNext()) {
                    termObj = (BasicDBObject) cursorTerms.next();
                }

            } finally {
                cursorTerms.close();
            }

            if (termObj != null) {
                //update number of docs
                termObj.put("tnodoc", termObj.getInt("tnodoc") + 1);
            }
        }

        return termObj;
    }

    public void initTermCursor() {
        cursorTerms = collectionTerms.find();
    }

    public void clearTerms() {
        //drop collection
        collectionTerms.drop();
        collectionTerms = db.getCollection("Terms");
    }

    public void insertTerms() {
        for (Iterator it = terms.entrySet().iterator(); it.hasNext();) {
            Map.Entry termPair = (Map.Entry) it.next();
            BasicDBObject term = (BasicDBObject) termPair.getValue();
            String tname = (String) termPair.getKey();
            
            if (updateList.contains(term)) {
                collectionTerms.update(new BasicDBObject("tname", tname ), term);
                System.out.println("Updated term " + tname);
            } else {
                collectionTerms.insert(term);
                System.out.println("Inserted term " + tname);
            }

            
        }
    }

    public void getDocument() throws MongoException {

    }

    public BasicDBObject getProductInfo(String queryTerm) throws MongoException {
        BasicDBObject productOBJ = new BasicDBObject();
        //List<String> results = new ArrayList();
        BasicDBObject query = new BasicDBObject();
        //BasicDBObject fields = new BasicDBObject();

        //List<String> products = new ArrayList();
        query.put("tname", queryTerm);
        cursorTerms = collectionTerms.find(query);
        try {
            while (cursorTerms.hasNext()) {
                productOBJ = (BasicDBObject) cursorTerms.next();
                //System.out.print(productOBJ.get(query));
                //String tid= cursorTerms.next().toString();
                //System.out.println(tid);
            }

        } finally {
            cursorTerms.close();
        }
        //fields.put("reviews",1);

        // List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
//        for(TaggedWord term:queryTerm){
//            obj.add(new BasicDBObject("tname",term.word().toString()));
//        }
        //obj.add(new BasicDBObject("reviews.terms.tname","story"));
        //query.put("$and", obj);
        return productOBJ;
    }

    public void printTerms() {
        cursorTerms = collectionTerms.find();

        System.out.println("Terms collection:");
        while (cursorTerms.hasNext()) {
            System.out.println(cursorTerms.next().toString());
        }
    }

}
