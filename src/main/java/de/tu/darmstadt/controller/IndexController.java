package de.tu.darmstadt.controller;

import de.tu.darmstadt.service.ModelPredService;
import de.tu.darmstadt.service.EsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping
public class IndexController {

    @Autowired
    EsRestService restService;

    @RequestMapping("/")
    public String index() {
        return "test";
    }

    @RequestMapping("/search")
    public String search(Model model,
                         @RequestParam("keyword") String keyword) {
        String[] searchFields = {"title", "filecontent"};
        ArrayList<Map<String, Object>> fileList = restService.searchDocs("userdoc",
                keyword, searchFields, 1, 10);
        model.addAttribute("flist", fileList);
        model.addAttribute("keyword", keyword.toLowerCase());
        return "result";
    }

//    @RequestMapping("/suggest")
//    public String suggest(Model model,
//                         @RequestParam("keyword") String keyword) {
//        long start = System.currentTimeMillis();
//        int int_keyword = word2int.get(keyword.split(" ")[0].toLowerCase());
//        System.out.println("time for word2int : "+ Long.toString(System.currentTimeMillis() - start));
//        int[] maxWords = ModelPredService.getNextWord(int_keyword);
//        System.out.println("time to getMaxWords : "+ Long.toString(System.currentTimeMillis() - start));
//        String suggest = keyword + " " + int2word.get(maxWords[1]);
//        System.out.println(keyword);
//        String[] searchFields = {"title", "filecontent"};
//        ArrayList<Map<String, Object>> fileList = restService.searchDocs("userdoc",
//                keyword, searchFields, 1, 10);
//        ArrayList<Map<String, Object>> suggestFileList = restService.searchDocs("userdoc",
//                suggest, searchFields, 1, 10);
//        model.addAttribute("flist", fileList);
//        model.addAttribute("keyword", keyword);
//        model.addAttribute("suggestflist", suggestFileList);
//        model.addAttribute("suggest",suggest);
//        System.out.println(suggest);
//        return "suggest.html.bak";
//    }

}
