package de.tu.darmstadt.controller;

import de.tu.darmstadt.domain.Search;
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

    @RequestMapping(value = "/data", method = RequestMethod.POST)
    @ResponseBody
    public JSONArray searchData(@RequestBody Search search, HttpServletRequest request){
        String keyword = search.getKeyword();
        System.out.println("input keywords :" + keyword);
        ArrayList<String> predict= modelService.invokeModel(keyword);
        JSONArray result = new JSONArray();
        JSONObject data ;
        for (String word:
                predict) {
            data = new JSONObject();
            data.put("word"," "+word);
            result.add(data);
        }
        System.out.println(request.toString());
        return result;
    }

}
