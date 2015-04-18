/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.models;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author david
 */
public class Dataset {

    /**
     *
     */
    public enum Keys {

        pid, rid, rev
    };

    private String bufferedLine;
    private Integer ridIndex;
    public BufferedReader br;

    public Integer noOfProducts;
    public Integer avgProductLength;
    public Integer productLength;

    public Dataset(String path) throws FileNotFoundException {
        // Open the file
        FileInputStream fstream;
        fstream = new FileInputStream(path);
        br = new BufferedReader(new InputStreamReader(fstream));

        ridIndex = 0;
        bufferedLine = "";
        noOfProducts = 0;
        productLength = 0;
        avgProductLength = 0;
    }

    public List<String> load(List<String> record) {
        List<String> formatedRecord = new ArrayList<>();

        //add the product Id
        formatedRecord.add(Keys.pid.ordinal(), record.get(0));

        //add the review Id, auto increase
        formatedRecord.add(Keys.rid.ordinal(), ridIndex.toString());
        ridIndex++;

        //add the review
        formatedRecord.add(Keys.rev.ordinal(), record.get(7));

        return formatedRecord;
    }

    public List<String> read() throws IOException {
        List<String> record = new ArrayList<>();

        String line;

        try {

            //Read File Line By Line for one record
            while ((line = br.readLine()) != null) {

                //trim
                line = line.trim();

                //output each record
                if (line.isEmpty() || line.equals("\n")) //end of record
                {
                    return record;
                } else {
                    record.add(line);
                }
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    public String readProduct(List<String> reviews) throws IOException {

        String line;
        String productId = "";
        productLength = 0;

        try {

            //get product id
            String productLine;
            if (bufferedLine.isEmpty()) {
                productLine = br.readLine();
            } else {
                productLine = bufferedLine;
                bufferedLine = "";
            }

            //end of file
            if (productLine == null) {
                return "";
            }

            //update noOfProducts
            noOfProducts++;

            productId = productLine.replaceFirst("product/productId: ", "");

            //skip next 6 lines
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();

            //get review
            line = br.readLine();
            String reviewContent = line.replaceFirst("review/text: ", "");
            reviews.add(reviewContent);

            //skip the empty line
            br.readLine();

            //loop the next record that have reviews for the same product id above
            while ((line = br.readLine()) != null) {

                //return if the line is of another product
                if (!line.equals(productLine)) {
                    //buffer
                    bufferedLine = line;
                    return productId;
                }

                //skip next 6 lines
                br.readLine();
                br.readLine();
                br.readLine();
                br.readLine();
                br.readLine();
                br.readLine();

                //get review
                line = br.readLine();
                reviewContent = line.replaceFirst("review/text: ", "");
                reviews.add(reviewContent);

                //skip the empty line
                br.readLine();

                //add the review content into avgdl
                int wordCount = countWords(reviewContent);
                productLength += wordCount;
                avgProductLength += wordCount;
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        return productId;
    }

    public void close() throws IOException {
        br.close();
    }

    private int countWords(String s) {

        int wordCount = 0;

        boolean word = false;
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
    }
}
