package de.tu.darmstadt.domain;

import de.tu.darmstadt.service.ModelPredService;
import de.tu.darmstadt.utils.FileLoader;
import org.apache.uima.internal.util.StringUtils;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class EntityTrie {

    private class Node{
        private int dumpli_num;
        private HashMap<String, Node> children;
        private boolean isLeaf;
        public Node(){
            dumpli_num = 0;
            children = new HashMap<>();
            isLeaf = false;
        }
    }

    private Node root;

    public EntityTrie(){
        this.root = new Node();
    }

    public void insert(String entity){
        this.insert(this.root, entity);

    }

    private void insert(Node root, String entity){
        String[] words = entity.split("_");
        for (int i = 0; i < words.length; i++) {
            if (!root.children.containsKey(words[i])){
                root.children.put(words[i],new Node());
            }
            if (i == words.length -1){
                root.children.get(words[i]).isLeaf = true;
                root.children.get(words[i]).dumpli_num++;
            }
            root = root.children.get(words[i]);
        }

    }

    public HashMap<String,Integer> getAllWords(){
//		HashMap<String, Integer> map=new HashMap<String, Integer>();

        return preTraversal(this.root, "");
    }

    private HashMap<String, Integer> preTraversal(Node root, String prefixs){
        HashMap<String, Integer> map = new HashMap<>();
        if (root != null){
            if (root.isLeaf == true){
                map.put(prefixs, root.dumpli_num);
            }

            Set<String> childStr = root.children.keySet();
            for (String child:
                 childStr) {
                if (root.children.get(child) != null){
                    String tempStr;
                    tempStr = prefixs + "_"+ child;
                    tempStr = StringUtils.replaceAll(tempStr, "__", "_");
                    map.putAll(preTraversal(root.children.get(child),tempStr));
                }
            }
        }
        return map;
    }

    public HashMap<String, Integer> getWordsForPrefix(String prefix){
        return getWordsForPrefix(this.root,prefix);
    }

    private HashMap<String, Integer> getWordsForPrefix(Node root, String prefix){
        HashMap<String, Integer> map = new HashMap<>();

        String[] words = prefix.split("_");

        if (root.isLeaf == true){
            map.put(prefix, root.dumpli_num);
        }

        for (int i = 0; i < words.length; i++) {
            if (root.children.get(words[i])==null) return null;
            root = root.children.get(words[i]);
        }
        return preTraversal(root, prefix);
    }

    @Test
    public void test(){
        EntityTrie trie = new EntityTrie();
        FileLoader.loadWords();
        Collection<String> entities = ModelPredService.int2entity.values();
        for (String entity:
            entities ) {
            trie.insert(entity);
        }

        HashMap<String, Integer> map = trie.getWordsForPrefix("doris");
        for (String key: map.keySet()){
            System.out.println(key+":"+map.get(key));
        }
    }

}
