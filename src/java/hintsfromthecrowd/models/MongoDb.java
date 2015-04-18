
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.models;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;

/**
 *
 * @author david
 */
public class MongoDb {

    private static MongoDb instance;
    public MongoClient mongoClient;
    public DB db;

    /**
     *
     * @throws UnknownHostException
     */
    public MongoDb() throws UnknownHostException {
        // To directly connect to a single MongoDB server (note that this will not auto-discover the primary even
// if it's a member of a replica set:
        mongoClient = new MongoClient("localhost", 27017);
// or, to connect to a replica set, with auto-discovery of the primary, supply a seed list of members
        db = mongoClient.getDB("advdbms");
    }

    public static MongoDb getInstance() throws UnknownHostException {
        if (instance == null) {
            instance = new MongoDb();
        }

        return instance;
    }
}
