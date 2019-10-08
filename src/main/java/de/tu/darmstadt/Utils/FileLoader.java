package de.tu.darmstadt.Utils;

import de.tu.darmstadt.service.ModelPredService;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;

public class FileLoader {

    public static void loadChars() {
        try {
            ModelPredService.char2int = new HashMap<>();
            ModelPredService.int2char = new HashMap<>();
            BufferedReader br = new BufferedReader(new FileReader("D:\\Github\\ContextBasedSuggestionSearchEngine\\src\\main\\resources\\charmodel\\allChars.txt"));
            int count = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] items = line.split(":");
                ModelPredService.char2int.put(items[0],count);
                ModelPredService.int2char.put(count,items[0]);
                count ++ ;

            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(ModelPredService.char2int);
    }

    public static void loadWords(){
        try {
            //建立映射
            ModelPredService.word2int = new HashMap<>();
            ModelPredService.int2word = new HashMap<>();
            BufferedReader br = new BufferedReader(new FileReader("D:\\Github\\ContextBasedSuggestionSearchEngine\\src\\main\\resources\\mymodel\\allWords.txt"));
            int count = 0;
            String line = null;
            while ((line = br.readLine())!=null){
                ModelPredService.word2int.put(line, count);
                ModelPredService.int2word.put(count, line);
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
//        System.out.println(ModelPredService.char2int.get("a"));
//        System.out.println(ModelPredService.int2char.get(1));
//        loadWords();
//        System.out.println(ModelPredService.word2int.get("may"));
//        System.out.println(ModelPredService.int2word.get(1));
    }
}
