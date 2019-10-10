package de.tu.darmstadt.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.util.*;

@Service
public class ModelPredService {

    public static Map<String, Integer> char2int = null;
    public static Map<Integer, String> int2char = null;

    public static Map<String, Integer> word2int = null;
    public static Map<Integer, String> int2word = null;

    public static Session charSess = null;
    public static Session wordSess = null;


    @Autowired
    CharService charService;

    /**
     * @param inputStr : hello worl
     */
    public ArrayList invokeModel(String inputStr){
        String[] keywords = inputStr.split(" ");
        ArrayList<String> predicts = new ArrayList();
        //get last keyword
        String keyword = keywords[keywords.length - 1].toLowerCase();
        Integer word_index = word2int.get(keyword);
        if (word_index != null){
            //if last keyword is a complete word, then predict based on word model
            predicts = getNextWord(word_index);
            predicts.replaceAll(item -> item.replace(item,keyword +" "+item));
        }else {
            //predict the complete word first
            String word = wordCompletion(keyword);
            word_index = word2int.get(word);
            if (word_index != null){
                predicts = getNextWord(word_index);
            }
            predicts.replaceAll(item -> item.replace(item, word + " "+ item));
        }
        return predicts;
    }

    private String wordCompletion(String keyword){
        char[] chars = keyword.toCharArray();
        String word = "";
        ArrayList<Integer> chars_index = new ArrayList<>();
        for (char c:
                chars) {
            chars_index.add(char2int.get(String.valueOf(c)));
        }
        float[][] inputs = charService.getInputChar(chars_index);
        ArrayList<Integer> nextChar = charService.getNextChar(inputs);
        for (int i = 0; i < nextChar.size(); i++) {
            word = charService.guessWord(chars_index, nextChar.get(i));
            if (!word.equalsIgnoreCase(""))
                System.out.println("guess success : " + word);
        }
        return word;
    }

    private ArrayList getNextWord(int word_index){
        int[] inputs = new int[1];
        ArrayList<String> outputs = new ArrayList();
        inputs[0] = word_index;
        Tensor<?> X_input = Tensor.create(inputs);
        Tensor indices = wordSess.runner().feed("X_input", X_input).fetch("top_k:1").run().get(0);
        int [][] words_index = new int[1][8];
        indices.copyTo(words_index);
        for (int index:
             words_index[0]) {
             if (index != word_index){
                 outputs.add(int2word.get(index));
             }
        }
        return outputs;
    }

    private void getWordSimilarity(int word_index){
        int[] inputs = new int[1];
        inputs[0] = word_index;
        Tensor<?> X_input = Tensor.create(inputs);
        Tensor indices = wordSess.runner().feed("X_input", X_input).fetch("top_k:0").run().get(0);
        float[][] words_sim = new float[1][8];
        indices.copyTo(words_sim);

//        return words_sim[0];
    }

    @Test
    public void test(){

    }
}
