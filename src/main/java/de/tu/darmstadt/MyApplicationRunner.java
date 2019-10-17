package de.tu.darmstadt;

import de.tu.darmstadt.dao.NewsDao;
import de.tu.darmstadt.utils.FileLoader;
import de.tu.darmstadt.domain.NewsDoc;
import de.tu.darmstadt.domain.UserDoc;
import de.tu.darmstadt.service.EsRestService;
import de.tu.darmstadt.service.ModelPredService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.tensorflow.Graph;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import ucar.ma2.ArrayDouble;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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


        if (restService.existIndex("userdoc"))
            restService.deleteIndex("userdoc");

        //mapper
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
//                        builder.field("analyzer", "ik_max_word");
                    }
                    builder.endObject();
                    builder.startObject("filecontent");
                    {
                        builder.field("type", "text");
//                        builder.field("analyzer", "ik_max_word");
                        builder.field("term_vector", "with_positions_offsets");
                    }
                    builder.endObject();
                }
                builder.endObject();
            }
            builder.endObject();

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

                List<NewsDoc> allDoc = newsDao.selectAll();
//                List<NewsDoc> allDoc = newsDao.selectByType();
                System.out.println("washingtonpost_21855 allDoc Size : "+ String.valueOf(allDoc.size()) );
                for (NewsDoc doc:
                     allDoc) {
                    if (doc.getNews_title()!=null && doc.getNews_fulltext()!=null){
                        UserDoc userDoc = new UserDoc();
                        userDoc.setTitle(doc.getNews_title());
                        userDoc.setFilecontent(doc.getNews_fulltext());
                        String json = objMapper.writeValueAsString(userDoc);
                        fileList.add(json);
                        System.out.println(doc.getNews_title());
                    }
                }
                System.out.println("selected news doc "+ fileList.size());
                restService.indexDoc("userdoc", "file", fileList);

                //init char and word dictionary
                FileLoader.loadWords();

                SavedModelBundle savedModelBundle = SavedModelBundle.load("./src/main/resources/mymodel","myTag");
//                Resource wordModelPath = new ClassPathResource("mymodel");
//                Path = wordModelPath.getFile().getPath();
//                SavedModelBundle savedModelBundle = SavedModelBundle.load(Path,"myTag");
                ModelPredService.sess = savedModelBundle.session();

            } else {
                logger.error("Failed to init the index...");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
