/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.engines;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import edu.stanford.nlp.ling.TaggedWord;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import hintsfromthecrowd.models.MongoDb;
import hintsfromthecrowd.models.Product;
import hintsfromthecrowd.models.ProductCollection;
import hintsfromthecrowd.models.StanfordParser;
import hintsfromthecrowd.models.StopWords;
import hintsfromthecrowd.models.WordNet;
import java.io.IOException;
import java.util.Collections;

/**
 *
 * @author david
 */
public class CrowdHintsEngine {

    List<Product> rankList = new ArrayList<>();

    public List<Product> run(String query) throws IOException {

        //lowercase all
        query = query.toLowerCase();
        
        //init data connection
        MongoDb mongo = new MongoDb();
        ProductCollection productCollection = new ProductCollection();
        productCollection.initFindAll();
        DBObject dbProduct;

        //Stanford Parser
        List<List<TaggedWord>> queryTermsets = new StanfordParser().posTagString(query);

        //Stopword
        StopWords stopWords = new StopWords();
        List<TaggedWord> queryTermset = stopWords.removeStopWords(queryTermsets);

        //remove duplication
        stopWords.removeDuplication2(queryTermset);

        //wordNet
        List<List<TaggedWord>> expandedTermset = new WordNet().expandQueryTermset(queryTermset);
        List<List<String>> DqList = new ArrayList();
        build_Dq(expandedTermset, DqList);

        //rank each product at a time
        List<String[]> rankedProducts = new ArrayList<>();

        while ((dbProduct = productCollection.getNextProduct()) != null) {
            //System.out.println(dbProduct.toString());
            List<List<Integer>> minWinss = new ArrayList<>();

            List<List<String>> rdStart = findRdStart(dbProduct, DqList, minWinss);
            if (rdStart != null) {
                //reviews size
                int noOfReviews = ((BasicDBList) dbProduct.get("reviews")).size();
                //get PRM 
                float productRank = findPRM(rdStart, expandedTermset, queryTermset.size(), minWinss, noOfReviews);
                //add list
                String productName = (String) dbProduct.get("pname");
                String productLink = (String) dbProduct.get("plink");
                Product myPro = new Product();
                myPro.productLink = productLink;
                myPro.productName = productName;
                myPro.productRank = productRank;
//            if(myPro.productRank!=0){
//            rankList.add(myPro);
//            }
                int listSize = rankList.size();
                sortProduct(rankList, myPro, listSize);
            }
            //rankedProducts.add(new String[]{productName, productLink, Float.toString(productRank)});
        }
        for (Product pro : rankList) {
            System.out.println("result: " + pro.productName);
            System.out.println(pro.productRank);
        }
        return rankList;
    }

    public void sortProduct(List<Product> rankList, Product myPro, int listSize) {
        if (rankList.isEmpty()) {
            rankList.add(myPro);
        } else {
            for (int i = 0; i < listSize; i++) {

                if (myPro.productRank < rankList.get(i).productRank) {
                    rankList.add(i, myPro);
                    return;
                } else if (myPro.productRank >= rankList.get(i).productRank && rankList.get(i + 1).productRank >= myPro.productRank) {
                    rankList.add(i, myPro);
                    return;
                }
            }
            rankList.add(myPro);
            return;
        }
    }

    public List<List<String>> findRdStart(DBObject dbProduct, List<List<String>> DqList, List<List<Integer>> minWinss) {

        List<List<String>> rdStart = new ArrayList<>();

        //check if the product is relavant
        BasicDBList dbReviews = (BasicDBList) dbProduct.get("reviews");

        //each I* in Dq
        for (Iterator<List<String>> itDq = DqList.iterator(); itDq.hasNext();) {
            List<String> iStart = itDq.next();

            int iStartSize = iStart.size();
            List<Integer> minWins = new ArrayList<>();
            boolean flagAddedIntoRdStart = false;

            //each review
            for (Iterator<Object> itReview = dbReviews.iterator(); itReview.hasNext();) {
                BasicDBObject review = (BasicDBObject) itReview.next();

                //check each termList
                BasicDBList dbTerms = (BasicDBList) review.get("terms");

                //init check relevant
                int countRelevant = 0;
                List<Integer> positions = new ArrayList<>();

                //each term in I*
                for (Iterator<String> itIstart = iStart.iterator(); itIstart.hasNext();) {
                    String iStartTerm = itIstart.next();

                    //each term in review
                    for (Iterator<Object> itTerm = dbTerms.iterator(); itTerm.hasNext() && countRelevant < iStartSize;) {
                        BasicDBObject term = (BasicDBObject) itTerm.next();
                        if (iStartTerm.equalsIgnoreCase(term.get("tname").toString())) {
                            //increase count
                            countRelevant++;

                            //collect positions
                            positions.add((int) term.get("tposition"));

                            //next I* term
                            break;
                        }
                    }
                }

                //check relavant
                if (countRelevant >= iStartSize) {
                    //relavant
                    if (flagAddedIntoRdStart == false) {
                        rdStart.add(iStart);
                    }

                    //add into RDStart only one time
                    flagAddedIntoRdStart = true;

                    //get minWindow
                    minWins.add(Collections.max(positions) - Collections.min(positions));
                }
            }

            //add minWins if if empty
            if (minWins.size() > 0) {
                minWinss.add(minWins);
            }
        }
        return rdStart.size() > 0 ? rdStart : null;
    }

    public float findPRM2(float weight, float semanticCoefficence, float avgDensity) {
        return weight + semanticCoefficence + avgDensity;
    }
    public float findPRM(List<List<String>> RDStar, List<List<TaggedWord>> expandedTearmset, int queryLength, List<List<Integer>> minWins, int noOfReviews) {
        float pRM = 0;
        int count = 0;
        int sizeList = RDStar.size();
        for (List<String> iStar : RDStar) {
            double weight = termSetWeight(iStar.size(), queryLength);
            double scT = semanticCoefficient(expandedTearmset, iStar);
            System.out.println("Weight: " + weight + "\n ; " + "SCT: " + scT);
            pRM += weight * averageDensity(iStar.size(), noOfReviews, minWins.get(count)) * scT;
            count++;//increase iterator of minwin corresponding to RDStar
        }
        return pRM;
    }
//find semantic coefficient for each I*

    public double semanticCoefficient(List<List<TaggedWord>> expandedTermset, List<String> iStar) {
        double scT = 1;
        for (String term : iStar) {
            //in this case termSet should consist of original term
            for (List<TaggedWord> termSet : expandedTermset) {
                String firstTerm = termSet.get(0).word();
                for (TaggedWord taggedTerm : termSet) {
                    if (taggedTerm == termSet.get(0) && term.equals(termSet.get(0).word().toString())) {
                        scT = scT * (0.5 + 0.5 / termSet.size());
                    } else if (term.equals(taggedTerm.word().toString())) {
                        scT = scT * (0.5 / termSet.size());
                    }

                }
            }
        }

        return scT;
    }

    public int findQuery(List<String> expandedTermSet, List<String> query) {
        boolean found = false;
        int count = 0;
        int minPos = 9999999;
        int maxPos = 0;
        for (String term : expandedTermSet) {
            for (String queryTerm : query) {
                if (term == null ? queryTerm == null : term.equals(queryTerm)) {
                    found = true;
                    if (query.indexOf(term) > maxPos) {
                        maxPos = query.indexOf(term);
                    }
                    if (query.indexOf(term) < minPos) {
                        minPos = query.indexOf(term);
                    }
                }
            }
            if (found == true) {
                count++;
            }
        }
        if (count == expandedTermSet.size()) {
            return maxPos - minPos;
        } else {
            return 0;
        }
    }

    public double averageDensity(double iStarLength, double reviewNum, List<Integer> minWins) {
        double averageDensity = 0;
        for (double minWin : minWins) {
            if (minWin != 0) {
                //double density = expandedTermSet.size() / minWin;
                averageDensity = averageDensity + (iStarLength / minWin) / reviewNum;
            }

        }
        return averageDensity;
    }

    public double termSetWeight(int termLength, int queryLength) {
        if (queryLength == 2) {
            return 1;
        } else if (queryLength > 2 && termLength == 2) {
            return termSetWeight(3, queryLength) * 2 * factorial(queryLength - 2) / factorial(queryLength);
        } else if (queryLength > 2 && termLength == queryLength) {
            return 0.5;
        } else {
            return termSetWeight(termLength + 1, queryLength) * factorial(termLength) * factorial(queryLength - termLength) / factorial(queryLength);
        }
    }

    public int factorial(int n) {
        if (n == 0) {
            return 1;
        }
        return n * factorial(n - 1);
    }

    public void build_Dq(List<List<TaggedWord>> expandedTermset, List<List<String>> DqList) {
        List<List<TaggedWord>> resultList = new ArrayList();
        for (int i = 2; i <= expandedTermset.size(); i++) {
            combination(expandedTermset, i, resultList);
            int j = 0;
            List<List<TaggedWord>> inputList = new ArrayList();
            while (j < resultList.size()) {
                inputList = resultList.subList(j, j + i);
                DqList.addAll(find_Dq(inputList));
                j = j + i;
            }
            resultList.clear();
        }
    }

    public void combination(List<List<TaggedWord>> elements, int K, List<List<TaggedWord>> resultList) {

        // get the length of the array
        // e.g. for {'A','B','C','D'} => N = 4 
        int N = elements.size();
        if (K > N) {
            System.out.println("Invalid input, K > N");
            return;
        }
        // get the combination by index 
        // e.g. 01 --> AB , 23 --> CD
        int combination[] = new int[K];
        // position of current index
        //  if (r = 1)              r*
        //  index ==>        0   |   1   |   2
        //  element ==>      A   |   B   |   C
        int r = 0;
        int index = 0;
        while (r >= 0) {
            // possible indexes for 1st position "r=0" are "0,1,2" --> "A,B,C"
            // possible indexes for 2nd position "r=1" are "1,2,3" --> "B,C,D"             
            // for r = 0 ==> index < (4+ (0 - 2)) = 2
            if (index <= (N + (r - K))) {
                combination[r] = index;

                // if we are at the last position print and increase the index
                if (r == K - 1) {

                    //do something with the combination e.g. add to list or print
                    print(combination, elements, resultList);
                    index++;
                } else {
                    // select index for next position
                    index = combination[r] + 1;
                    r++;
                }
            } else {
                r--;
                if (r > 0) {
                    index = combination[r] + 1;
                } else {
                    index = combination[0] + 1;
                }
            }
        }
    }

    protected List<List<String>> find_Dq(List<List<TaggedWord>> lists) {
        List<List<String>> resultLists = new ArrayList<>();
        if (lists.isEmpty()) {
            resultLists.add(new ArrayList<>());
            return resultLists;
        } else {
            List<TaggedWord> firstList = lists.get(0);
            List<List<String>> remainingLists = find_Dq(lists.subList(1, lists.size()));
            for (TaggedWord condition : firstList) {
                for (List<String> remainingList : remainingLists) {
                    List<String> resultList = new ArrayList<>();
                    if (addToWordList((List<String>) resultList, condition.toString().substring(0, condition.toString().indexOf('/'))) == false) {
                        resultList.add((String) condition.toString().substring(0, condition.toString().indexOf('/')));
                        resultList.addAll(remainingList);
                        resultLists.add(resultList);
                    }
                }
            }
        }
        return resultLists;
    }

    public boolean addToWordList(List<String> wordList, String addedWord) {
        boolean found = false;
        if (wordList == null) {
            wordList.add(addedWord);
        } else {
            for (String word : wordList) {
                if (addedWord == null ? word == null : addedWord.equals(word)) {
                    found = true;
                }
            }
        }
        return found;
    }

    public void print(int[] combination, List<List<TaggedWord>> elements, List<List<TaggedWord>> resultList) {
        for (int i = 0; i < combination.length; i++) {
            resultList.add(elements.get(combination[i]));
        }
    }

}
