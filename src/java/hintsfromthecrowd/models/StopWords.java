/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.models;

import edu.stanford.nlp.ling.TaggedWord;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author david
 */
public class StopWords {

    public List<TaggedWord> removeStopWords(List<List<TaggedWord>> queryTermset) {
        List<TaggedWord> termset = new ArrayList<>();
        int k = 0;
        String sCurrentLine;

        List<String> stopwords = new ArrayList();
        String termPattern = "";
        try {
            FileReader fr = new FileReader("/prj/java/advdbmsfinal/database/stopwordslist.txt");
            BufferedReader br = new BufferedReader(fr);
            while ((sCurrentLine = br.readLine()) != null) {
                stopwords.add(sCurrentLine);
            }

            for (Iterator<List<TaggedWord>> sentenceIt = queryTermset.iterator(); sentenceIt.hasNext();) {
                List<TaggedWord> sentence = sentenceIt.next();

                for (int ii = 0; ii < sentence.size(); ii++) {
                    String term = sentence.get(ii).value();
                    
                    //check symbol contain only character
                    if (!term.matches("^[a-z]{1,10}$")) {
                        continue;
                    }

                    //check stopword
                    if (!stopwords.contains(term)) {
                        termset.add(sentence.get(ii));
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return termset;
    }

    public void removeDuplication(List<TaggedWord> queryTermset, List position, int pos) {
        if (pos >= queryTermset.size() - 1) {
            for (Object position1 : position) {
                queryTermset.remove((int) position1);
            }
        } else {
            for (int i = pos + 1; i < queryTermset.size(); i++) {
                if (queryTermset.get(i).toString().equals(queryTermset.get(pos).toString())) {
                    position.add(i);
                }
            }
            removeDuplication(queryTermset, position, pos + 1);
        }
    }

    public void removeDuplication2(List<TaggedWord> queryTermset) {
        int size = queryTermset.size();

        for (int i = 0; i < size - 1; i++) {
            TaggedWord iWord = queryTermset.get(i);
            for (int j = i + 1; j < size; j++) {
                if (iWord.equals(queryTermset.get(j))) {
                    queryTermset.remove(j--);
                    size--;
                }
            }
        }
    }
}
