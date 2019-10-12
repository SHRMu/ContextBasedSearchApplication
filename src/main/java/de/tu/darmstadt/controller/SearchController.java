package de.tu.darmstadt.controller;

import de.tu.darmstadt.model.Search;
import de.tu.darmstadt.service.ModelPredService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class SearchController {

    @Autowired
    ModelPredService modelPredService;

    @RequestMapping(value = "/data", method = RequestMethod.POST)
    @ResponseBody
    public JSONArray searchData(@RequestBody Search search, HttpServletRequest request){
        String keyword = search.getKeyword();
        System.out.println("input keywords :" + keyword);
        ArrayList<String> predict= modelPredService.invokeModel(keyword);
        JSONArray result = new JSONArray();
        JSONObject data = null;
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
