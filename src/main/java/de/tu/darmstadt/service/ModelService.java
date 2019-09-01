package de.tu.darmstadt.service;

import org.tensorflow.Session;
import org.tensorflow.Tensor;

public class ModelService {

    public static Session session = null;

    public static int [] getMaxWords(int input){
        int[] inputs = new int[1];
        inputs[0] = input;
        Tensor<?> X_input = Tensor.create(inputs);
        Tensor indices = session.runner().feed("X_input", X_input).fetch("top_k:1").run().get(0);
        int [][] words = new int[1][5];
        indices.copyTo(words);
        return words[0];

    }

}
