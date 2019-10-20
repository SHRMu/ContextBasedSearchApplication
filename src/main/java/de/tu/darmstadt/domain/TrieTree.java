package de.tu.darmstadt.domain;

import de.tu.darmstadt.service.ModelPredService;
import de.tu.darmstadt.utils.FileLoader;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;

/**
 * Word Autocompletion
 */
public class TrieTree {

    private class Node{
        private int dumpli_num;
        private int prefix_num;
        private Node childs[];
        private boolean isLeaf;
        public Node(){
            dumpli_num = 0;
            prefix_num = 0;
            isLeaf = false;
            childs = new Node[28];
        }
    }

    private Node root; //root point

    public TrieTree() {
        this.root = new Node();
    }

    public void insert(String word){
        insert(this.root, word);
    }
    /**
     * insert character
     * @param root
     * @param word
     */
    private void insert(Node root,String word){
        word=word.toLowerCase(); // to lowerCase
        char[] chrs=word.toCharArray();

        for(int i=0,length=chrs.length; i<length; i++){
            ///use the relative value as index
            int index=chrs[i]-'_';
            try {
                if(root.childs[index]!=null){
                    ////if exist, the prefix num increase
                    root.childs[index].prefix_num++;
                }else{
                    ///if not exist, create node first
                    root.childs[index]=new Node();
                    root.childs[index].prefix_num++;
                }
                ///remark when it is leaf node
                if(i==length-1){
                    root.childs[index].isLeaf=true;
                    root.childs[index].dumpli_num++;
                }
                ///root points to child
                root=root.childs[index];
            }catch (ArrayIndexOutOfBoundsException e){
//                System.out.println(chrs[i]);
//                System.out.println(index);
            }

        }

    }

    /**
     *
     * @return HashMap<String, Integer> map
     */
    public HashMap<String,Integer> getAllWords(){
//		HashMap<String, Integer> map=new HashMap<String, Integer>();
        return preTraversal(this.root, "");
    }

    /**
     *
     * @param root
     * @param prefixs
     * @return
     */
    private  HashMap<String,Integer> preTraversal(Node root,String prefixs){
        HashMap<String, Integer> map=new HashMap<String, Integer>();

        if(root!=null){

            if(root.isLeaf==true){
                ////
                map.put(prefixs, root.dumpli_num);
            }

            for(int i=0,length=root.childs.length; i<length;i++){
                if(root.childs[i]!=null){
                    char ch=(char) (i+'_');
                    ////
                    String tempStr=prefixs+ch;
                    map.putAll(preTraversal(root.childs[i], tempStr));
                }
            }
        }

        return map;
    }


    /**
     *
     * @param word
     * @return true if exists ,otherwise  false
     */
    public boolean isExist(String word){
        return search(this.root, word);
    }
    /**
     *
     * @param word
     * @return true if exists ,otherwise  false
     */
    private boolean search(Node root,String word){
        char[] chs=word.toLowerCase().toCharArray();
        for(int i=0,length=chs.length; i<length;i++){
            int index=chs[i]-'_';
            if(root.childs[index]==null){
                ///
                return false;
            }
            root=root.childs[index];
        }

        return true;
    }

    /**
     *
     * @param prefix
     * @return
     */
    public HashMap<String, Integer> getWordsForPrefix(String prefix){
        return getWordsForPrefix(this.root, prefix);
    }
    /**
     *
     * @param root
     * @param prefix
     * @return
     */
    private HashMap<String, Integer> getWordsForPrefix(Node root,String prefix){
        HashMap<String, Integer> map=new HashMap<String, Integer>();
        char[] chrs=prefix.toLowerCase().toCharArray();
        ////
        for(int i=0, length=chrs.length; i<length; i++){

            int index=chrs[i]-'_';
            if(root.childs[index]==null){
                return null;
            }

            root=root.childs[index];

        }

        return preTraversal(root, prefix);
    }

    @Test
    public void test()  //Just used for test
    {
        TrieTree trie = new TrieTree();
        FileLoader.loadWords();
        Collection<String> values = ModelPredService.int2entity.values();
        for (String entity:
                values) {
//            String[] words = entity.split("_");
//            for (int i = 0; i < words.length; i++) {
//                trie.insert(words[i]);
//            }
            trie.insert(entity);
        }

        HashMap<String,Integer> map= new HashMap<>();

        map=trie.getWordsForPrefix("alibaba_g");

        System.out.println("words start with alibaba_g : ");

        for(String key:map.keySet()){
            System.out.println(key+" occours: "+ map.get(key)+" times");
        }

    }

}
