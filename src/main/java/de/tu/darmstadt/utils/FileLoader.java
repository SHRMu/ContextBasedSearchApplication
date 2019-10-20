package de.tu.darmstadt.utils;

import de.tu.darmstadt.domain.TrieTree;
import de.tu.darmstadt.service.ModelPredService;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;

public class FileLoader {

    public static void loadWords(){
        try {

            ModelPredService.entity2int = new HashMap<>();
            ModelPredService.int2entity = new HashMap<>();

//            BufferedReader br = new BufferedReader(new FileReader("D:\\Github\\ContextBasedSuggestionSearchEngine\\src\\main\\resources\\mymodel\\allWords.txt"));
            Resource resource = new ClassPathResource("mymodel/allWords.txt");
            String Path = resource.getFile().getPath();
            BufferedReader br = new BufferedReader(new FileReader(Path));
            int count = 0;
            String line = null;
            while ((line = br.readLine())!=null){
                ModelPredService.entity2int.put(line, count);
                ModelPredService.int2entity.put(count, line);
                count ++;

            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //build trie tree based on allWords
    public static void buildTrieTree(){
        //init trieTree
        ModelPredService.trieTree = new TrieTree();
        Collection<String> entities = ModelPredService.int2entity.values();
        for (String entity:
                entities) {
//            String[] words = entity.split("_");
//            for (int i = 0; i < words.length; i++) {
//                ModelPredService.trieTree.insert(words[i]);
//            }
            ModelPredService.trieTree.insert(entity);
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
