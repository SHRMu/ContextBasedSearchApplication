package de.tu.darmstadt.utils;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSelector {

    public static BufferedReader getInputStream(String inputFile) throws Exception{
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputFile)));
        return new BufferedReader(new InputStreamReader(bis, "utf-8"), 10*1024*1024);
    }

    public static void largeFileIO(String inputFile, String outputFile){
        try {
            BufferedReader in = getInputStream(inputFile);
            FileWriter fw = new FileWriter(outputFile);
            int count = 0;
            while (in.ready()){
                String line = in.readLine();
                String url;
                try {
                    url = line.split("://")[1].split("/")[0];
                    if (url.equalsIgnoreCase("www.washingtonpost.com")){
                        fw.append(line + "\n");
//                    count ++;
//                    if (count%100 == 0){
//                        System.out.println(count);
//                    }
                    }
                } catch (Exception e){
                    System.out.println(line);
                }
//                System.out.println(url);
            }
            in.close();
            fw.flush();
            fw.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ArrayList getTextID(String inputFile){
        ArrayList<Integer> idList = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputFile)));
            BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10*1024*1024);
            idList = new ArrayList<>();
            while (in.ready()){
                String line = in.readLine();
                try{
                    int id = Integer.valueOf(line.split(",")[0]);
                    idList.add(id);
                }catch (Exception e){
                    System.out.println(line);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return idList;
    }

    public static void textFilter(String folder, ArrayList<Integer> idList){
        File file = new File(folder);
        File[] files = file.listFiles();
        System.out.println(files.length);
        for (File f: files){
            if (f.isFile()){
                String name = f.getName();
                try {
                    Integer id = Integer.valueOf(name.split(".txt")[0]);
                    if (!idList.contains(id))
                        f.delete();
                }catch (ArrayIndexOutOfBoundsException e){
                    System.out.println(name);
                }
            }
        }
    }

    @Test
    public void largeFileIOTest(){
//        largeFileIO("D:\\TUDa\\DMPR\\Data\\extract-100000\\full-news-10000.txt","D:\\\\TUDa\\\\DMPR\\\\Data\\\\full-news-out.txt");
        largeFileIO("D:\\TUDa\\DMPR\\Data\\full-news-all.txt","D:\\TUDa\\DMPR\\Data\\full-news-out.txt");
    }

    @Test
    public void textFilterTest() throws Exception{
        ArrayList textID = getTextID("D:\\TUDa\\DMPR\\Data\\full-news-21855.txt");
        System.out.println(textID.size());
        BufferedReader in = getInputStream("D:\\TUDa\\DMPR\\Data\\doc_entity_value.txt");
        FileWriter fw = new FileWriter("D:\\TUDa\\DMPR\\Data\\doc_entity_value_21855.txt");
        int count = 0;
        while (in.ready()){
            String line = in.readLine();
            Integer newsID;
            try {
                newsID = Integer.valueOf(line.split(",")[0]);
                if (textID.contains(newsID)){
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

    }

    @Test
    public void test(){
        File file = new File("D:\\TUDa\\DMPR\\Data\\text");
        File[] files = file.listFiles();
        ArrayList<Integer> fileID = new ArrayList();
        for (File f:files){
            if (f.isFile()){
                String name= f.getName().split(".txt")[0];
                fileID.add(Integer.valueOf(name));
            }
        }
        System.out.println(fileID.size());
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File("D:\\TUDa\\DMPR\\Data\\full-news-out.txt")));
            BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10 * 1024 * 1024);
            FileWriter fw = new FileWriter(new File("D:\\TUDa\\DMPR\\Data\\full-news-final.txt"));
            while(in.ready()){
                String line = in.readLine();
                Integer id = Integer.valueOf(line.split(",")[0]);
                int i = fileID.indexOf(id);
                if (i > -1){
                    fw.append(line + '\n');
                }
            }
            in.close();
            fw.flush();
            fw.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void entity_file() throws Exception{
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
    }

    @Test
    public void test01(){
        Pattern p = Pattern.compile("\\_\\((.*?)\\)");
        String input = "Democratic_Party_(United_States)";
        Matcher m = p.matcher(input);
        while (m.find()){
            System.out.println(m.group(0));
            System.out.println(m.group(1));
            String s = m.replaceAll("");
            System.out.println(s);
        }
    }
}

