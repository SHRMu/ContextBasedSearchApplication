package de.tu.darmstadt.service;

import org.tensorflow.Session;
import org.tensorflow.Tensor;

public class ModelPredService {

    public static Session charSess = null;
    public static Session wordSess = null;

    public static int[] getNextChar(String input){
        return null;
    }

    public static int [] getNextWord(int word){
        int[] inputs = new int[1];
        inputs[0] = word;
        Tensor<?> X_input = Tensor.create(inputs);
        Tensor indices = wordSess.runner().feed("X_input", X_input).fetch("top_k:1").run().get(0);
        int [][] words = new int[1][5];
        indices.copyTo(words);
        return words[0];
    }

}
