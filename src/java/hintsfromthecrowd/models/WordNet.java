/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.models;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.stanford.nlp.ling.TaggedWord;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author david
 */
public class WordNet {

    private IDictionary initiateWordNet() throws MalformedURLException, IOException {
        URL url = new URL("file", null, "database/dict");
        IDictionary dict = new Dictionary(url);
        dict.open();
        return dict;
    }

    public List<List<TaggedWord>> expandQueryTermset(List<TaggedWord> queryTermset) throws IOException {
        IDictionary dict = initiateWordNet();
        List<List<TaggedWord>> expandedTermset;
        expandedTermset = new ArrayList();

        //add expanded terms into expandedTermset
        queryTermset.stream().map((tWord) -> {
            System.out.println(tWord.toString());
            return tWord;
        }).map((tWord) -> expandWord(tWord, dict)).filter((expandedTerms) -> (expandedTerms != null)).forEach((expandedTerms) -> {
            expandedTermset.add(expandedTerms);
//                    System.out.println("++" + expandedTerms.toString());
        });

        return expandedTermset;
    }

    private List<TaggedWord> expandWord(TaggedWord tWord, IDictionary dict) {
        //return new arraylist
        List<TaggedWord> expandedSet = new ArrayList<>();
        TaggedWord expandedTaggegWord;

        // look up first sense of the word
        IIndexWord idxWord = dict.getIndexWord(tWord.value(), findPOS(tWord.tag()));
     
        //check null
        if (idxWord == null) {
            return null;
        }

        IWordID wordID = idxWord.getWordIDs().get(0); // 1st meaning 
        IWord word = dict.getWord(wordID);

        //get synonyms
        ISynset synset = word.getSynset();

        // iterate over words associated with the synset 
        for (IWord w : synset.getWords()) {
            System.out.println(w.getLemma());
            expandedTaggegWord = new TaggedWord(w.getLemma(), tWord.tag());
            expandedSet.add(expandedTaggegWord);
        }

        //get hypernyms
        List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);

        //print out each hypernyms id and sysnonyms
        List<IWord> words;
        for (ISynsetID sid : hypernyms) {
            words = dict.getSynset(sid).getWords();
            System.out.print(sid + " {");
            for (Iterator<IWord> i = words.iterator(); i.hasNext();) {
                expandedTaggegWord = new TaggedWord(i.next().getLemma(), tWord.tag());
                System.out.print(expandedTaggegWord.toString());
                expandedSet.add(expandedTaggegWord);
                if (i.hasNext()) {
                    System.out.print(", ");
                }
            }
            System.out.println("}");
        }
        return expandedSet;
    }

    private POS findPOS(String tag) {
        switch (tag) {
            case "DT":
            case "JJ":
            case "VBG":
                return POS.ADJECTIVE;
            case "VBZ":
            case "VBN":
                return POS.VERB;
            case "NN":
            case "NNS":
            case "NNP":
                return POS.NOUN;
            default:
                return POS.ADVERB;
        }
    }
}
