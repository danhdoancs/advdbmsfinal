/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.procedures;

import edu.stanford.nlp.ling.TaggedWord;
import hintsfromthecrowd.models.AWS;
import hintsfromthecrowd.models.Dataset;
import hintsfromthecrowd.models.ProductCollection;
import hintsfromthecrowd.models.StanfordParser;
import hintsfromthecrowd.models.StopWords;
import hintsfromthecrowd.models.SummaryCollection;
import hintsfromthecrowd.models.TermCollection;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author david
 */
public class Backend {

    public void run(String datasetUrl) throws IOException, InvalidKeyException, IllegalArgumentException, UnsupportedEncodingException, NoSuchAlgorithmException {

        Dataset dataset = new Dataset("database/" + datasetUrl);
        List<String> reviews = new ArrayList<>();
        String productId;
        StanfordParser parser = new StanfordParser();
        StopWords stopWords = new StopWords();

        ProductCollection productCollection = new ProductCollection();
        productCollection.clearProducts();

        //init term collection
        TermCollection termCollection = new TermCollection();
        termCollection.clearTerms();

        SummaryCollection summary = new SummaryCollection();

        //init the Amazon Web Service
        AWS aws = new AWS();

        //read each record a time
        while (!(productId = dataset.readProduct(reviews)).isEmpty()) {

            //post tag and stopword the reivews
            List<List<TaggedWord>> reviewTermsets = new ArrayList<>();

            for (Iterator<String> it = reviews.iterator(); it.hasNext();) {
                String reviewContent = it.next();

                //lowercase all
                reviewContent = reviewContent.toLowerCase();

                //post tag
                List<List<TaggedWord>> taggedReivew = parser.posTagString(reviewContent);

                //remove stopwords
                List<TaggedWord> reviewTermset = stopWords.removeStopWords(taggedReivew);

                //remove duplication
                stopWords.removeDuplication2(reviewTermset);

                //add into revivew Termsets
                reviewTermsets.add(reviewTermset);
            }

            //get product infos
            List<String> productInfos = aws.getProductInfos(productId);
           
            //update the terms
            termCollection.updateTerms(productId, reviewTermsets);
            termCollection.insertTerms();

            //load into database
            productCollection.insertProduct(productId, productInfos.get(0), productInfos.get(1), reviewTermsets, dataset.productLength);

            //insert the Summary collection
            summary.insertSummary(dataset.avgProductLength / dataset.noOfProducts, dataset.noOfProducts);

            //reset the reviews
            reviews = new ArrayList<>();

            //delete all unused objects
            System.gc();
        }

        //print out
        printCollections(false);

        //close connection
        dataset.close();
    }

    public void printCollections(boolean flagTerms) throws UnknownHostException {
        if (flagTerms) {
            new TermCollection().printTerms();
            new ProductCollection().printAll();
            new SummaryCollection().printSummary();
        } else {
            new TermCollection().printTerms();
            new ProductCollection().printAllWithoutTerms();
            new SummaryCollection().printSummary();

        }
    }
}
