/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author david
 */
public class Term {
    public int noOfDocuments;
    public Map<String,Integer> noOfOccurences;
    public String name;
    public int id;
    
    public Term(int id, String name)
    {
        this.id = id;
        this.name = name;
        noOfDocuments = 0;
        noOfOccurences = new HashMap();
    }
    
    public void updateOccurences(String productId)
    {
        Integer occurences = noOfOccurences.get(productId);
        
        if(occurences == null)
        {
            occurences = 0;
            
            noOfDocuments++;
        }
        occurences++;
        
        noOfOccurences.put(productId, occurences);
    }
}