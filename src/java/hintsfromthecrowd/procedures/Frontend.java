/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.procedures;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import hintsfromthecrowd.HintsFromTheCrowd.Engine;
import hintsfromthecrowd.engines.CustomizedBM25Engine;
import hintsfromthecrowd.engines.CrowdHintsEngine;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author david
 */
public class Frontend {

    public void run(String query, Engine engine) throws IOException {
        
        //run query engine
        switch (engine) {
            case crowdhints:
                new CrowdHintsEngine().run(query);
                break;
            case bm25:
                new CustomizedBM25Engine().run(query);
                break;
        }
    }

    public void getSub(List<List<TaggedWord>> resultList, int k) {

    }

    public JSONArray readData(String filePath) {
        JSONParser parser = new JSONParser();
        JSONArray dataArray;
        try {
            Object obj = parser.parse(new FileReader(filePath));
            dataArray = (JSONArray) obj;
            //show test
            Iterator<JSONObject> iterator;
            iterator = dataArray.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
            return dataArray;
        } catch (IOException | ParseException e) {
        }
        return null;
    }

    public void postTag() throws FileNotFoundException {

        MaxentTagger tagger = new MaxentTagger("database/english-left3words-distsim.tagger");
        List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader("E:/Projects/Java/JavaApplication2/database/sample-input.txt")));
        for (List<HasWord> sentence : sentences) {
            List<TaggedWord> tSentence = tagger.tagSentence(sentence);
            System.out.println(Sentence.listToString(tSentence, false));
        }
    }
}
