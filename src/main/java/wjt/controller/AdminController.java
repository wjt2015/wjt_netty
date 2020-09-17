package wjt.controller;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wjt.web.HttpResult;

@Slf4j
@RequestMapping(value = {"/admin"})
@RestController
public class AdminController {

    @RequestMapping(value = {"/admins.json"}, method = {RequestMethod.GET})
    public HttpResult admins() {

        HttpResult httpResult = new HttpResult(0, "success", Lists.newArrayList(1L, 2L, 3L));

        return httpResult;
    }
}
