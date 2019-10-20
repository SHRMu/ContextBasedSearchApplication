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

    private Node root;///树根

    public TrieTree() {
        this.root = new Node();
    }

    public void insert(String word){
        insert(this.root, word);
    }
    /**
     * 插入字串，用循环代替迭代实现
     * @param root
     * @param word
     */
    private void insert(Node root,String word){
        word=word.toLowerCase();////转化为小写
        char[] chrs=word.toCharArray();

        for(int i=0,length=chrs.length; i<length; i++){
            ///用相对于a字母的值作为下标索引，也隐式地记录了该字母的值
            int index=chrs[i]-'_';
            try {
                if(root.childs[index]!=null){
                    ////已经存在了，该子节点prefix_num++
                    root.childs[index].prefix_num++;
                }else{
                    ///如果不存在
                    root.childs[index]=new Node();
                    root.childs[index].prefix_num++;
                }
                ///如果到了字串结尾，则做标记
                if(i==length-1){
                    root.childs[index].isLeaf=true;
                    root.childs[index].dumpli_num++;
                }
                ///root指向子节点，继续处理
                root=root.childs[index];
            }catch (ArrayIndexOutOfBoundsException e){
//                System.out.println(chrs[i]);
//                System.out.println(index);
            }

        }

    }

    /**
     * 遍历Trie树，查找所有的words以及出现次数
     * @return HashMap<String, Integer> map
     */
    public HashMap<String,Integer> getAllWords(){
//		HashMap<String, Integer> map=new HashMap<String, Integer>();

        return preTraversal(this.root, "");
    }

    /**
     * 前序遍历。。。
     * @param root		子树根节点
     * @param prefixs	查询到该节点前所遍历过的前缀
     * @return
     */
    private  HashMap<String,Integer> preTraversal(Node root,String prefixs){
        HashMap<String, Integer> map=new HashMap<String, Integer>();

        if(root!=null){

            if(root.isLeaf==true){
                ////当前即为一个单词
                map.put(prefixs, root.dumpli_num);
            }

            for(int i=0,length=root.childs.length; i<length;i++){
                if(root.childs[i]!=null){
                    char ch=(char) (i+'_');
                    ////递归调用前序遍历
                    String tempStr=prefixs+ch;
                    map.putAll(preTraversal(root.childs[i], tempStr));
                }
            }
        }

        return map;
    }


    /**
     * 判断某字串是否在字典树中
     * @param word
     * @return true if exists ,otherwise  false
     */
    public boolean isExist(String word){
        return search(this.root, word);
    }
    /**
     * 查询某字串是否在字典树中
     * @param word
     * @return true if exists ,otherwise  false
     */
    private boolean search(Node root,String word){
        char[] chs=word.toLowerCase().toCharArray();
        for(int i=0,length=chs.length; i<length;i++){
            int index=chs[i]-'_';
            if(root.childs[index]==null){
                ///如果不存在，则查找失败
                return false;
            }
            root=root.childs[index];
        }

        return true;
    }

    /**
     * 得到以某字串为前缀的字串集，包括字串本身！ 类似单词输入法的联想功能
     * @param prefix 字串前缀
     * @return 字串集以及出现次数，如果不存在则返回null
     */
    public HashMap<String, Integer> getWordsForPrefix(String prefix){
        return getWordsForPrefix(this.root, prefix);
    }
    /**
     * 得到以某字串为前缀的字串集，包括字串本身！
     * @param root
     * @param prefix
     * @return 字串集以及出现次数
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
        ///结果包括该前缀本身
        ///此处利用之前的前序搜索方法进行搜索
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
