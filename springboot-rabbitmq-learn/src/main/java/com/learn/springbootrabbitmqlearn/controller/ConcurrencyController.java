package com.learn.springbootrabbitmqlearn.controller;

import com.learn.springbootrabbitmqlearn.response.BaseResponse;
import com.learn.springbootrabbitmqlearn.response.StatusCode;
import com.learn.springbootrabbitmqlearn.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * autor:liman
 * createtime:2019/10/22
 * comment:
 */
@RestController
public class ConcurrencyController {
    private static final Logger log= LoggerFactory.getLogger(ConcurrencyController.class);

    private static final String Prefix="concurrency";

    @Autowired
    private InitService initService;

    @RequestMapping(value = Prefix+"/robbing/thread",method = RequestMethod.GET)
    public BaseResponse robbingThread(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        initService.generateMultiThread();
        return response;
    }
}
