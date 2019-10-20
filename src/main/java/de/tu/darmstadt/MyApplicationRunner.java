package de.tu.darmstadt;

import de.tu.darmstadt.dao.NewsDao;
import de.tu.darmstadt.utils.FileLoader;
import de.tu.darmstadt.domain.NewsDoc;
import de.tu.darmstadt.service.EsRestService;
import de.tu.darmstadt.service.ModelPredService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.tensorflow.SavedModelBundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    private static Logger logger = Logger.getLogger(MyApplicationRunner.class.getClass());

    @Autowired
    EsRestService restService;
    @Autowired
    NewsDao newsDao;

    @Override
    public void run(ApplicationArguments var1) throws Exception {


        if (restService.existIndex("newsdoc"))
            restService.deleteIndex("newsdoc");

        //mapper
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.startObject("properties");
                {
                    builder.startObject("news_title");
                    {
                        builder.field("type", "text");
                    }
                    builder.endObject();
                    builder.startObject("news_fulltext");
                    {
                        builder.field("type", "text");
                        builder.field("term_vector", "with_positions_offsets");
                    }
                    builder.endObject();
                }
                builder.endObject();
            }
            builder.endObject();

            Boolean isSuccess = restService.initIndex("newsdoc",
                    "file", 3, 0, builder);

            if (isSuccess) {

                logger.info("init index susscess. index_name: newsdoc, type_name: file, shardNum: 3, repliNum: 0");

                /**
                 * import data from mysql database
                 */
                ObjectMapper objMapper = new ObjectMapper();

                ArrayList<String> fileList = new ArrayList<>();

                List<NewsDoc> allDoc = newsDao.selectAll();
//                List<NewsDoc> allDoc = newsDao.selectByType();
                for (NewsDoc doc:
                     allDoc) {
                    if (doc.getNews_title()!=null && doc.getNews_fulltext()!=null){
                        String json = objMapper.writeValueAsString(doc);
                        fileList.add(json);
//                        System.out.println(doc.getNews_title());
                    }
                }

                logger.info("selected news doc "+ fileList.size());

                restService.indexDoc("newsdoc", "file", fileList);

                //init char and word dictionary
                FileLoader.loadWords();
                FileLoader.buildTrieTree();

//                SavedModelBundle savedModelBundle = SavedModelBundle.load("./src/main/resources/mymodel","myTag");

                //use resource path
                String modelPath = this.getClass().getClassLoader().getResource("mymodel").getPath();
                ModelPredService.sess = SavedModelBundle.load(modelPath, "myTag").session();


            } else {
                logger.error("Failed to init the index...");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
