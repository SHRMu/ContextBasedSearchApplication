package de.tu.darmstadt.Utils;

import de.tu.darmstadt.controller.IndexController;
import org.apache.lucene.search.IndexSearcher;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;

public class FileLoader {

    public static void loadChars() {
        try {
            IndexController.char2int = new HashMap<>();
            IndexController.int2char = new HashMap<>();
            BufferedReader br = new BufferedReader(new FileReader("D:\\Github\\ContextBasedSuggestionSearchEngine\\src\\main\\resources\\charmodel\\allChars.txt"));
            int count = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] items = line.split(":");
                IndexController.char2int.put(items[0],count);
                IndexController.int2char.put(count,items[0]);
                count ++ ;

            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(IndexController.char2int);
    }

    public static void loadWords(){
        try {
            //建立映射
            IndexController.word2int = new HashMap<>();
            IndexController.int2word = new HashMap<>();
            BufferedReader br = new BufferedReader(new FileReader("D:\\Github\\ContextBasedSuggestionSearchEngine\\src\\main\\resources\\mymodel\\allWords.txt"));
            int count = 0;
            String line = null;
            while ((line = br.readLine())!=null){
                IndexController.word2int.put(line, count);
                IndexController.int2word.put(count, line);
                count ++;
            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Test
    public void Test(){
//        loadChars();
//        System.out.println(IndexController.char2int.get("a"));
//        System.out.println(IndexController.int2char.get(1));
//        loadWords();
//        System.out.println(IndexController.word2int.get("may"));
//        System.out.println(IndexController.int2word.get(1));
    }
}
