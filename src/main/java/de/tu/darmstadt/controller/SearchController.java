package de.tu.darmstadt.controller;

import de.tu.darmstadt.model.Search;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping
public class SearchController {

    @RequestMapping(value = "/data", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject TestAjax(@RequestBody Search search, HttpServletRequest request){
        String keyword = search.getKeyword();
        System.out.println(keyword);
        JSONObject json = new JSONObject();
        json.put("result","value");
        return json;
    }

}
