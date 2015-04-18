/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd;

import java.io.IOException;
import hintsfromthecrowd.procedures.Backend;
import hintsfromthecrowd.procedures.Frontend;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author danh
 */
public class HintsFromTheCrowd {

    public static enum Procedure {

        frontend, backend
    };

    public static enum Engine {

        crowdhints, bm25
    }

    public static enum Task {

        run, print
    }

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, NullPointerException, InvalidKeyException, IllegalArgumentException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // TODO code application logic here

        //procedure selection
        Procedure procedure = Procedure.frontend;
        Engine engine = Engine.bm25;
        String query = "funny racing";
        String dataset = "dataset3.txt";
        Task task = Task.print;

        //get procedure from comman line
        if (args.length > 0 && args[0] != null) {
            switch (args[0]) {
                case "frontend":
                    procedure = Procedure.frontend;

                    //get engine from comman line
                    if (args.length > 1 && args[1] != null) {
                        switch (args[1]) {
                            case "crowdhints":
                                engine = Engine.crowdhints;
                                break;
                            case "bm25":
                                engine = Engine.bm25;
                                break;
                        }
                    }

                    //get query from command line
                    if (args.length > 2 && args[2] != null) {
                        query = args[2];
                    }

                    break;
                case "backend":
                    procedure = Procedure.backend;

                    //get task from comman line
                    if (args.length > 1 && args[1] != null) {
                        switch (args[1]) {
                            case "run":
                                task = Task.run;
                                break;
                            case "print":
                                task = Task.print;
                                break;
                        }
                    }

                    //get dataset from command line
                    if (args.length > 2 && args[2] != null) {
                        dataset = args[2];
                    }

                    break;
            }
        }

        //run procedure
        switch (procedure) {
            case frontend:
                new Frontend().run(query, engine);
                break;
            case backend:
                switch (task) {
                    case run:
                        new Backend().run(dataset);
                        break;
                    case print:
                        new Backend().printCollections(true);
                        break;
                }
                break;
        }
    }
}
