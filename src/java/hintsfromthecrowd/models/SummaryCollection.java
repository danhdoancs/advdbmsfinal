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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author david
 */
public class SummaryCollection {

    public DB db;

    public DBCollection collectionSummary;

    public DBCursor cursorSummary;

    public SummaryCollection() throws UnknownHostException {
        db = MongoDb.getInstance().db;
        collectionSummary = db.getCollection("Summary");
    }

    public void insertSummary(int avgdl, int noOfProducts) {
        BasicDBObject summary = new BasicDBObject("avgDocumentLength", avgdl)
                .append("noOfDocuments", noOfProducts);

        //clear
        collectionSummary.drop();
        collectionSummary = db.getCollection("Summary");
        //insert
        collectionSummary.insert(summary);
        
        System.out.format("Updated summary: avgDocumentLength: %d, noOfProduct: %d\n", avgdl, noOfProducts);
    }

    public void printSummary() {
        cursorSummary = collectionSummary.find();        
        System.out.println("Summary collection:");
        while (cursorSummary.hasNext()) {
            System.out.println(cursorSummary.next().toString());
        }
    }
    public List<String> getData (){
        cursorSummary = collectionSummary.find(); 
        List<String> result = new ArrayList();
        //System.out.println("Summary collection:");
        while (cursorSummary.hasNext()) {
            String noOfDoc = cursorSummary.next().get("noOfDocuments").toString();
            result.add(noOfDoc);
            String avdl = cursorSummary.curr().get("avgDocumentLength").toString();
            result.add(avdl);
        }
        return result;
    }
}
