package de.tu.darmstadt.service;

import de.tu.darmstadt.domain.EntityTrie;
import de.tu.darmstadt.domain.TrieTree;
import de.tu.darmstadt.utils.FileLoader;
import de.tu.darmstadt.utils.MapUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.util.*;

@Service
public class ModelPredService {

    public static Map<String, Integer> entity2int = null;
    public static Map<Integer, String> int2entity = null;

    public static Session sess = null;

    public static TrieTree trieTree = null;
    public static EntityTrie entityTrie = null;

    public static final int LEN = 10;
    public static final int TOP_K = 17315;

    @Autowired
    CharService charService;

    public ArrayList<String> invokeModel(String inputStr){
        //get sperate chars input
        ArrayList<String> result = new ArrayList<>();
        String[] keywords = inputStr.trim().split(" ");
        int len = keywords.length;
        int count = 0;
        if (keywords.length == 1){
            ArrayList<String> entities = entityCompletion(null, inputStr);
            for (String str:
                    entities) {
                result.add(str);
                count++;
                if (count>10) break;
            }
        }else if (len >1){
            int index = entity2int.get(keywords[len-2]);
            Map<String, Float> nextEntity = getNextEntity(index);
            result = entityCompletion(nextEntity, keywords[keywords.length - 1]);
            //combine all
            result.replaceAll(item->item.replace(item, keywords[len-2]+" "+item));
        }
        return result;
    }

    /**
     *
     * @param nextEntity
     * @param chs
     * @return
     */
    private ArrayList<String> entityCompletion(Map<String, Float> nextEntity, String chs){
        HashMap<String,Integer> wordsMap ;
        HashMap<String,Integer> entityMap ;

        Map<String, Float> sorted = new HashMap<>();
        ArrayList<String> result = new ArrayList<>();

        if (entity2int.get(chs)!= null)
            return result;

        if (chs.endsWith("_")){
            entityMap = entityTrie.getWordsForPrefix(chs);
            if (entityMap != null && entityMap.size()>0){
                for (String entity: entityMap.keySet()){
                    System.out.println("#####"+entity+":"+entityMap.get(entity));
                    if (nextEntity==null){
                        result.add(entity);
                    }else {
                        sorted.put(entity,nextEntity.get(entity));
                    }
                }
            }
        }else {
            wordsMap=trieTree.getWordsForPrefix(chs);
            for(String word:wordsMap.keySet()){
//            System.out.println(word+" shows: "+ wordsMap.get(word)+" times");
                entityMap = entityTrie.getWordsForPrefix(word);
                if (entityMap != null && entityMap.size()>0){
                    for (String entity: entityMap.keySet()){
                    System.out.println("#####"+entity+":"+entityMap.get(entity));
                        if (nextEntity==null){
                            result.add(entity);
                        }else {
                            sorted.put(entity,nextEntity.get(entity));
                        }
                    }
                }
            }
        }
        sorted = MapUtil.sortByValue(sorted);
        int count = 0;
        for (String str:
             sorted.keySet()) {
            result.add(str);
            count++;
            if (count>LEN) break;
            System.out.println(str+" similarity : "+ sorted.get(str));
        }
        return result;
    }

    private Map<String,Float> getNextEntity(int word_index){
        int[] inputs = new int[1];
        Map<String,Float> outputs = new HashMap<>();
        inputs[0] = word_index;
        Tensor<?> X_input = Tensor.create(inputs);
        Tensor words = sess.runner().feed("X_input", X_input).fetch("top_k:1").run().get(0);
        Tensor simi = sess.runner().feed("X_input", X_input).fetch("top_k:0").run().get(0);
        int [][] words_index = new int[1][TOP_K];
        float[][] simi_index = new float[1][TOP_K];
        words.copyTo(words_index);
        simi.copyTo(simi_index);
        for (int i = 0; i < TOP_K; i++) {
            outputs.put(int2entity.get(words_index[0][i]),simi_index[0][i]);
        }
        outputs = MapUtil.sortByValue(outputs);
        return outputs;
    }


    @Test
    public void test(){

        //init char and word dictionary
        FileLoader.loadWords();
        SavedModelBundle savedModelBundle = SavedModelBundle.load("./src/main/resources/mymodel","myTag");
        sess= savedModelBundle.session();

        String keyword = "barack_obama";
        Map<String, Float> nextEntity = getNextEntity(entity2int.get(keyword));

//        for (String str:
//             nextEntity.keySet()) {
//            System.out.println(str+" with similarity :"+nextEntity.get(str));
//        }

        System.out.println("=================== model prediction end ===============================");

        FileLoader.buildTrieTree();

        ArrayList<String> result = entityCompletion(nextEntity, "do");

        System.out.println("=================== entity completion end ===============================");

        for (String str:
             result) {
            System.out.println(str);
        }

    }

}
