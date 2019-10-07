package de.tu.darmstadt.Utils;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;

public class FileSelector {

    public static void largeFileIO(String inputFile, String outputFile){
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputFile)));
            BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10*1024*1024);
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
                } catch (ArrayIndexOutOfBoundsException e){
                    System.out.println(line);
                }
//                System.out.println(url);
            }
            in.close();
            fw.flush();
            fw.close();
        } catch (IOException e){
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
    public void textFilterTest(){
        ArrayList textID = getTextID("D:\\TUDa\\DMPR\\Data\\full-news-out.txt");
        System.out.println(textID.size());
//        textFilter("D:\\TUDa\\DMPR\\Data\\text", textID);
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
}

