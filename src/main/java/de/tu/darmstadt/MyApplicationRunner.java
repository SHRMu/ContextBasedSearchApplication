package de.tu.darmstadt;

import de.tu.darmstadt.controller.IndexController;
import de.tu.darmstadt.dao.JDBCNews;
import de.tu.darmstadt.model.NewsDoc;
import de.tu.darmstadt.model.UserDoc;
import de.tu.darmstadt.service.EsRestService;
import de.tu.darmstadt.service.ModelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.tensorflow.SavedModelBundle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class MyApplicationRunner implements ApplicationRunner {
    private static Logger logger = Logger.getLogger(MyApplicationRunner.class.getClass());

    @Autowired
    EsRestService restService;

    @Override
    public void run(ApplicationArguments var1) throws Exception {


        if (restService.existIndex("userdoc"))
            restService.deleteIndex("userdoc");

        //设置mapping
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.startObject("properties");
                {
                    builder.startObject("title");
                    {
                        builder.field("type", "text");
                        builder.field("analyzer", "ik_max_word");
                    }
                    builder.endObject();
                    builder.startObject("filecontent");
                    {
                        builder.field("type", "text");
                        builder.field("analyzer", "ik_max_word");
                        builder.field("term_vector", "with_positions_offsets");
                    }
                    builder.endObject();
                }
                builder.endObject();
            }
            builder.endObject();

            //初始化索引
            Boolean isSuccess = restService.initIndex("userdoc",
                    "file", 3, 0, builder);

            if (isSuccess) {

                logger.info("init index susscess. index_name: userdoc, type_name: file, shardNum: 3, repliNum: 0");
                /**
                 * 批量导数据
                 */
                Resource resource = new ClassPathResource("files");
                ObjectMapper objMapper = new ObjectMapper();

                ArrayList<String> fileList = new ArrayList<>();
                ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
                JDBCNews JDBCNews = (JDBCNews) context.getBean("JDBCNews");

//                for (int id = 5914747; id <= 5915747; id++) {
//                    NewsDoc newsDoc = JDBCNews.getNewsDoc(id);
//                    UserDoc userDoc = new UserDoc();
//                    userDoc.setTitle(newsDoc.getNews_title());
//                    userDoc.setFilecontent(newsDoc.getNews_fulltext());
//                    String json = objMapper.writeValueAsString(userDoc);
//                    fileList.add(json);
//                }

                List<NewsDoc> allDoc = JDBCNews.getWorldNewsDoc();
                for (NewsDoc doc:
                     allDoc) {
                    UserDoc userDoc = new UserDoc();
                    userDoc.setTitle(doc.getNews_title());
                    userDoc.setFilecontent(doc.getNews_fulltext());
                    String json = objMapper.writeValueAsString(userDoc);
                    fileList.add(json);
                    System.out.println(doc.getNews_title());
                }
                System.out.println("selected news doc "+ fileList.size());
                restService.indexDoc("userdoc", "file", fileList);

                //建立映射
                IndexController.word2int = new HashMap<>();
                IndexController.int2word = new HashMap<>();
                BufferedReader br = new BufferedReader(new FileReader("D:\\Github\\esfilesearch\\src\\main\\resources\\mymodel\\allWords.txt"));
                int count = 0;
                String line = null;
                while ((line = br.readLine())!=null){
                    IndexController.word2int.put(line, count);
                    IndexController.int2word.put(count, line);
                    count ++;
                }

                //建立session
                SavedModelBundle savedModelBundle = SavedModelBundle.load("./src/main/resources/mymodel","myTag");
                ModelService.session = savedModelBundle.session();

            } else {
                logger.error("Failed to init the index...");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
