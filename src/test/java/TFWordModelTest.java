import de.tu.darmstadt.service.ModelPredService;
import de.tu.darmstadt.utils.FileLoader;
import org.junit.Test;
import org.tensorflow.*;

/**
 * @author huanyingcool
 *
 * @description invoke trainied SkipGram_word_model from java code
 *
 */
public class TFWordModelTest {

    // top_k param in training model, which fixed before model training
    public static final int TOP_SIZE = 8;

    public static void getSimilarityWord(String str) {
        FileLoader.loadWords();
        String[] inputStr = str.split(" ");
        int input_size = inputStr.length;
        int[] input = new int[input_size];
        for (int i = 0; i < input_size; i++) {
            input[i] = ModelPredService.word2int.get(inputStr[i].toLowerCase());
        }
        SavedModelBundle savedModelBundle = SavedModelBundle.load("./src/main/resources/mymodel","myTag");
//        Graph graph = savedModelBundle.graph();
//        Iterator<Operation> opIterator = graph.operations();
//        System.out.println("operations --- ");
//        while (opIterator.hasNext()){
//            System.out.println(opIterator.next());
//        }
        Session sess = savedModelBundle.session();
        Tensor<?> X_input = Tensor.create(input);
        //top_k:0 values top_k:1 indices
        Tensor tensor = sess.runner().feed("X_input", X_input).fetch("top_k:1").run().get(0);
        int [][] ans = new int[input_size][TOP_SIZE];
        tensor.copyTo(ans);

        Tensor similarity = sess.runner().feed("X_input", X_input).fetch("top_k:0").run().get(0);
        float[][] sim = new float[input_size][TOP_SIZE];
        similarity.copyTo(sim);

        for (int i = 0; i < input_size; i++) {
            System.out.println("————————————————————————————————————————————————");
            System.out.println("the similarity result for input ##"+ inputStr[i]+"## as following :");
            for (int j = 0; j < TOP_SIZE; j++) {
                System.out.println("word :"+ModelPredService.int2word.get(ans[i][j])+" with similarity value :"+sim[i][j]);
            }
            System.out.println("————————————————————————————————————————————————");
        }
    }

    @Test
    public void test(){
        String str = "china huawei";
        getSimilarityWord(str);
    }

}
