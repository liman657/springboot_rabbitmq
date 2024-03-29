package com.learn.springbootrabbitmqlearn.controller;

import com.learn.springbootrabbitmqlearn.listener.InsertOrderRecordEvent;
import com.learn.springbootrabbitmqlearn.response.BaseResponse;
import com.learn.springbootrabbitmqlearn.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by steadyjack on 2018/8/21.
 */
@RestController
public class OrderRecordController {

    private static final Logger log= LoggerFactory.getLogger(OrderRecordController.class);

    private static final String Prefix="order";

    //TODO：类似于RabbitTemplate
    @Autowired
    private ApplicationEventPublisher publisher;


    /**
     * 下单
     * @return
     */
    @RequestMapping(value = Prefix+"/push",method = RequestMethod.GET)
    public BaseResponse pushOrder(){
        BaseResponse response=new BaseResponse(StatusCode.Success);

        try{
            InsertOrderRecordEvent event = new InsertOrderRecordEvent(this,"testInsertOrderInsert_001","测试数据");
            publisher.publishEvent(event);
        }catch (Exception e){
            log.error("订单出现异常:{}",e.fillInStackTrace());
        }


//        try {
//            PushOrderRecordEvent event=new PushOrderRecordEvent(this,"orderNo_20180821001","物流12");
//            publisher.publishEvent(event);
//        }catch (Exception e){
//            log.error("下单发生异常：",e.fillInStackTrace());
//        }
        log.info("数据保存完成");
        return response;
    }

}










