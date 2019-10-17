package de.tu.darmstadt.service;

import de.tu.darmstadt.domain.EntityTrie;
import de.tu.darmstadt.domain.TrieTree;
import de.tu.darmstadt.utils.FileLoader;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import javax.swing.text.html.parser.Entity;
import java.util.*;

@Service
public class ModelPredService {

//    public static Map<String, Integer> word2int = null;
//    public static Map<Integer, String> int2word = null;

    public static Map<String, Integer> entity2int = null;
    public static Map<Integer, String> int2entity = null;

    public static Session sess = null;

    public static TrieTree trieTree = null;
    public static EntityTrie entityTrie = null;

    @Autowired
    CharService charService;

    /**
     * @param inputStr : dona
     */
    public ArrayList invokeModel(String inputStr){
        String[] keywords = inputStr.split(" ");


        return null;
    }

    private ArrayList<String> getNextWord(int word_index){
        int[] inputs = new int[1];
        ArrayList<String> outputs = new ArrayList();
        inputs[0] = word_index;
        Tensor<?> X_input = Tensor.create(inputs);
        Tensor indices = sess.runner().feed("X_input", X_input).fetch("top_k:1").run().get(0);
        int [][] words_index = new int[1][8];
        indices.copyTo(words_index);
        for (int index:
             words_index[0]) {
             if (index != word_index){
                 outputs.add(int2entity.get(index));
             }
        }
        return outputs;
    }

    private void getWordSimilarity(int word_index){
        int[] inputs = new int[1];
        inputs[0] = word_index;
        Tensor<?> X_input = Tensor.create(inputs);
        Tensor indices = sess.runner().feed("X_input", X_input).fetch("top_k:0").run().get(0);
        float[][] words_sim = new float[1][8];
        indices.copyTo(words_sim);

//        return words_sim[0];
    }

    @Test
    public void test(){

        //init char and word dictionary
        FileLoader.loadWords();
        SavedModelBundle savedModelBundle = SavedModelBundle.load("./src/main/resources/mymodel","myTag");
        sess= savedModelBundle.session();

        //init trieTree
        trieTree = new TrieTree();
        Collection<String> entities = ModelPredService.int2entity.values();
        for (String entity:
                entities) {
            String[] words = entity.split("_");
            for (int i = 0; i < words.length; i++) {
                trieTree.insert(words[i]);
            }
        }
        //init entityTree
        entityTrie = new EntityTrie();
        for (String entity:
                entities ) {
            entityTrie.insert(entity);
        }


        HashMap<String,Integer> wordsMap= new HashMap<>();
        HashMap<String,Integer> entityMap= new HashMap<>();

        wordsMap=trieTree.getWordsForPrefix("university_of_nor");

        System.out.println("\n包含chin（包括本身）前缀的单词及出现次数：");
        for(String word:wordsMap.keySet()){
            System.out.println(word+" 出现: "+ wordsMap.get(word)+"次");
            entityMap = entityTrie.getWordsForPrefix(word);
            if (entityMap != null && entityMap.size()>0){
                for (String entity: entityMap.keySet()){
                    System.out.println("#####"+entity+":"+entityMap.get(entity));
                }
            }
            System.out.println("----------------------------------------------");
        }





    }
}
