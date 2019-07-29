package com.rocket.test.web.controller;


import com.rocket.summer.framework.stereotype.Controller;
import com.rocket.summer.framework.web.bind.annotation.RequestMapping;
import com.rocket.summer.framework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {

    @RequestMapping("/index")
    @ResponseBody
    public Map<String, String> index(){
        Map<String, String> result = new HashMap<>();
        result.put("name", "jack");
        result.put("address", "test");
        return result;
    }
}
