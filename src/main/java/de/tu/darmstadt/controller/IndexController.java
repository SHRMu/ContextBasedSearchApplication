package de.tu.darmstadt.controller;

import de.tu.darmstadt.domain.SearchData;
import de.tu.darmstadt.service.EsRestService;
import de.tu.darmstadt.service.ModelPredService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping
public class IndexController {

    @Autowired
    EsRestService restService;
    @Autowired
    ModelPredService modelService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    /**
     * search new fulltext
     * @param model
     * @param input
     * @return
     */
    @RequestMapping("/search")
    public String search(Model model, @RequestParam("keyword") String input) {
        String[] searchFields = {"news_title", "news_fulltext"};
        Pattern p = Pattern.compile("\\_");
        Matcher m = p.matcher(input);
        String keywords = "";
        while (m.find()){
            keywords = m.replaceAll(" ");
        }
        ArrayList<Map<String, Object>> fileList = restService.searchDocs("newsdoc",
                keywords, searchFields, 1, 10);
        model.addAttribute("flist", fileList);
        model.addAttribute("keyword", input.toLowerCase());
        return "result";
    }


    @RequestMapping(value = "/entity", method = RequestMethod.POST)
    @ResponseBody
    public JSONArray getEntity(@RequestBody SearchData searchData, HttpServletRequest request){
        String keyword = searchData.getKeyword();
        System.out.println("\n### user input keywords :" + keyword);
        System.out.println("---------------- predict start ------------------");
        ArrayList<String> predict= modelService.invokeModel(keyword);
        JSONArray result = new JSONArray();
        JSONObject data ;
        for (String word:
                predict) {
            data = new JSONObject();
            data.put("word", word);
            result.add(data);
        }
        System.out.println(request.toString());
        return result;
    }

}
