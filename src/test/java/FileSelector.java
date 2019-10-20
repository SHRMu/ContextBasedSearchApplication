package de.tu.darmstadt.utils;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * File processing for large file
 */
public class FileSelector {

    public static final String URL = "www.washingtonpost.com";

    public static final String FULL_NEWS_ALL = "D:\\TUDa\\DMPR\\Data\\full-news-all.txt";
    public static final String FULL_NEWS_SELECTED = "D:\\TUDa\\DMPR\\Data\\full-news-selected.txt";

    public static final String FULLTEXT_FOLDER = "D:\\TUDa\\DMPR\\Data\\text";

    public static final String DOC_ENTITY_ALL = "D:\\TUDa\\DMPR\\Data\\doc_entity_value-all.txt";
    public static final String DOC_ENTITY_SELECTED = "D:\\TUDa\\DMPR\\Data\\doc_entity_value-selected.txt";

    private static BufferedReader getInputStream(String inputFile) throws Exception{
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputFile)));
        return new BufferedReader(new InputStreamReader(bis, "utf-8"), 10*1024*1024);
    }

    /**
     * splitter for full-news-all.txt by selecting specific url
     *
     * @param inputFile full-news-all.txt
     * @param outputFile full-news-selected.txt
     */
    public static void full_news_splitter(String inputFile, String outputFile){
        try {
            BufferedReader in = getInputStream(inputFile);
            FileWriter fw = new FileWriter(outputFile);
            int count = 0;
            String url;
            while (in.ready()){
                String line = in.readLine();
                try {
                    url = line.split("://")[1].split("/")[0];
                    if (url.equalsIgnoreCase(URL)){
                        fw.append(line + "\n");
//                    count ++;
//                    if (count%1000 == 0){
//                        System.out.println(count);
//                    }
                    }
                } catch (Exception e){
                    System.out.println(line);
                }
            }
            in.close();
            fw.flush();
            fw.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * get selected text id with full-news-selected.txt
     * @param inputFile  full-news-selected.txt
     * @return id list
     */
    public static ArrayList getTextID(String inputFile){
        ArrayList<Integer> idList = null;
        try {
            BufferedReader in = getInputStream(inputFile);
            idList = new ArrayList<>();
            int id;
            while (in.ready()){
                String line = in.readLine();
                try{
                    id = Integer.valueOf(line.split(",")[0]);
                    idList.add(id);
                }catch (Exception e){
                    System.out.println(line);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return idList;
    }

    /**
     * splitter fulltext folder by selected id list
     *
     * @param folder text folder
     * @param idList selected id list
     */
    public static void text_filter(String folder, ArrayList<Integer> idList){
        File file = new File(folder);
        File[] files = file.listFiles();
        System.out.println(files.length);
        for (File f: files){
            if (f.isFile()){
                String name = f.getName();
                try {
                    Integer id = Integer.valueOf(name.split(".txt")[0]);
                    if (!idList.contains(id))
                        f.delete(); // delete fulltext if not selected
                }catch (ArrayIndexOutOfBoundsException e){
                    System.out.println(name);
                }
            }
        }
    }

    public static void doc_entity_splitter(String inputFile, ArrayList idList){
        try {
            BufferedReader in = getInputStream(DOC_ENTITY_ALL);
            FileWriter fw = new FileWriter(DOC_ENTITY_SELECTED);
            int count = 0;
            while (in.ready()){
                String line = in.readLine();
                Integer newsID;
                try {
                    newsID = Integer.valueOf(line.split(",")[0]);
                    if (idList.contains(newsID)){
                        fw.append(line + "\n");
                        count ++;
                        if (count%1000 == 0){
                            System.out.println(count);
                        }
                    }
                } catch (Exception e){
                    System.out.println(line);
                }
            }
            System.out.println(count);
            in.close();
            fw.flush();
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void doc_entity_cleaner(){
        try {
            BufferedReader in = getInputStream("D:\\TUDa\\DMPR\\Data\\data-21855\\doc_entity_value_21855.txt");
            FileWriter fw = new FileWriter("D:\\TUDa\\DMPR\\Data\\data-21855\\news_id_entity_21855.txt");
            int news_id;
            String pre_entity = "";
            String news_entity;
            Map<Integer, String> map = new HashMap<>();
            Pattern p0 = Pattern.compile("\\<(.*?)\\>");
            Pattern p1 = Pattern.compile("\\_\\((.*?)\\)"); //remove ()
            Matcher m = null;
            while (in.ready()){
                String line = in.readLine();
                String[] split = line.split(",");
                news_id = Integer.valueOf(split[0]);
                news_entity = split[1].split(":")[1];
                m = p0.matcher(news_entity);
                while(m.find()) {
                    news_entity = m.group(1);
                }
                m = p1.matcher(news_entity);
                while (m.find()){
                    news_entity = m.replaceAll("");
                }
                news_entity = news_entity.replaceAll("[^a-zA-Z0-9\\_]", "");
                if (!map.keySet().contains(news_id)){
                    map.put(news_id, news_entity);
                    pre_entity = news_entity;
                }else if(!news_entity.equals(pre_entity)){
                    String s = map.get(news_id);
                    StringBuilder sb = new StringBuilder();
                    sb.append(s);
                    sb.append(" ");
                    sb.append(news_entity);
                    map.put(news_id, sb.toString());
                    pre_entity = news_entity;
                }
            }
            Set<Integer> keySet = map.keySet();
            for (Integer id:
                    keySet) {
                fw.write(id.toString()+","+map.get(id)+"\n");
            }
            in.close();
            fw.flush();
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void largeFileIOTest(){
        full_news_splitter(FULL_NEWS_ALL,FULL_NEWS_SELECTED);
    }

    @Test
    public void textFilterTest(){
        ArrayList idList = getTextID(FULL_NEWS_SELECTED);
        System.out.println("selected news data size : " + idList.size());


    }

    @Test
    public void entity_file() throws Exception{

    }

}

