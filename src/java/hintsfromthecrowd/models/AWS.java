/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.models;

import hintsfromthecrowd.helpers.SignedRequestsHelper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Suja
 */
public class AWS {

    /*
     * Your AWS Access Key ID, as taken from the AWS Your Account page.
     */
    private final String AWS_ACCESS_KEY_ID = "AKIAI647G52U7UV5DFKA";

    /*
     * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
     * Your Account page.
     */
    private final String AWS_SECRET_KEY = "xsDsdCFRXVzyZ+jYFCHFchIg2AoXNQLRpUv0CHDU";
    private final String ASSOCIATE_KEY = "amazonmovie07-20";

    /*
     * Use one of the following end-points, according to the region you are
     * interested in:
     * 
     *      US: ecs.amazonaws.com 
     *      CA: ecs.amazonaws.ca 
     *      UK: ecs.amazonaws.co.uk 
     *      DE: ecs.amazonaws.de 
     *      FR: ecs.amazonaws.fr 
     *      JP: ecs.amazonaws.jp
     * 
     */
    private final String ENDPOINT = "ecs.amazonaws.com";

    public List<String> getProductInfos(String productId) throws InvalidKeyException, IllegalArgumentException, UnsupportedEncodingException, NoSuchAlgorithmException {
        /*
         * Set up the signed requests helper 
         */
        SignedRequestsHelper helper;
        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (IllegalArgumentException | UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            return null;
        }

        String requestUrl;
        String title;
        String url;

        /* The helper can sign requests in two forms - map form and string form */
        /*
         * Here is an example in map form, where the request parameters are stored in a map.
         */
        Map<String, String> params = new HashMap<>();
        params.put("Service", "AWSECommerceService");
        params.put("AssociateTag", ASSOCIATE_KEY);
        params.put("Operation", "ItemLookup");
        params.put("ItemId", productId);
        params.put("ResponseGroup", "Small");

        requestUrl = helper.sign(params);

        title = fetchTitle(requestUrl);
        url = fetchURL(requestUrl);

        //return var
        List<String> productInfos = new ArrayList<>();
        productInfos.add(title == null ? productId : title);
        productInfos.add(url);
        return productInfos;
    }

    /*
     * Utility function to fetch the response from the service and extract the
     * title from the XML.
     */
    private static String fetchTitle(String requestUrl) {
        String title = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);

            Node titleNode = doc.getElementsByTagName("Title").item(0);
            if (titleNode != null) {
                title = titleNode.getTextContent();
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            throw new RuntimeException(e);
        }
        return title;
    }

    private static String fetchURL(String requestUrl) {
        String url = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);

            Node titleNode = doc.getElementsByTagName("DetailPageURL").item(0);
            if (titleNode != null) {
                url = titleNode.getTextContent();
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            throw new RuntimeException(e);
        }
        return url;
    }
}
