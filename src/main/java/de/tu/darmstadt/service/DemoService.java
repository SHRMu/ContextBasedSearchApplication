package de.tu.darmstadt.service;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DemoService {

    public static void main(String[] args) throws Exception {
        try(Graph graph = new Graph()){
            graph.importGraphDef(Files.readAllBytes(Paths.get("./src/main/resources/charmodel/char_model.pb")));
            try (Session sess = new Session(graph)){
                float[][] input = {{9, 2, 6, 1, 4}};
                Tensor tensor = Tensor.create(input);
                Tensor tensor1 = sess.runner().feed("embedding_1_input_1", tensor).fetch("dense_1_1/Softmax").run().get(0);
                float [][] ans = new float[1][37];
                tensor1.copyTo(ans);
                System.out.println(ans[0][0]);
            }
        }
    }
}
