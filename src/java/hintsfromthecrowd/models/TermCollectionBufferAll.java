/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.models;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
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
public class TermCollectionBufferAll {

    public DBCollection collectionTerms;
    public DB db;
    public DBCursor cursorTerms;
    public int termIndex;

    /**
     *
     */
    public static Map<String, Term> terms;

    public TermCollectionBufferAll() throws UnknownHostException {

        db = MongoDb.getInstance().db;
        collectionTerms = db.getCollection("Terms");
        termIndex = 0;
        terms = new HashMap();

    }

    public void updateTerms(String productId, List<List<TaggedWord>> reviewTermsets) {
        //loop each review termset
        for (Iterator<List<TaggedWord>> it1 = reviewTermsets.iterator(); it1.hasNext();) {
            List<TaggedWord> reviewTermset = it1.next();

            //loop each term
            for (Iterator<TaggedWord> it = reviewTermset.iterator(); it.hasNext();) {
                TaggedWord term = it.next();

                updateTerm(termIndex++, term.word(), productId);
            }
        }
    }

    public static void updateTerm(Integer termId, String termName, String productId) {
        Term term = terms.get(termName);
        if (term == null) {
            term = new Term(termId, termName);
        }

        term.updateOccurences(productId);

        //update term in set
        terms.put(termName, term);
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
            Term term = (Term) termPair.getValue();

            //create termProducts
            List<BasicDBObject> termProducts = new ArrayList();
            for (Iterator<Map.Entry<String, Integer>> it2 = term.noOfOccurences.entrySet().iterator(); it2.hasNext();) {
                Map.Entry<String, Integer> termProductPair = it2.next();

                termProducts.add(new BasicDBObject("pid", termProductPair.getKey())
                        .append("occur", termProductPair.getValue()));
            }

            BasicDBObject termDb = new BasicDBObject("tid", term.id)
                    .append("tname", term.name)
                    .append("tnodoc", term.noOfDocuments);
            termDb.put("products", termProducts);

            collectionTerms.insert(termDb);
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
                productOBJ = (BasicDBObject)cursorTerms.next();
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
