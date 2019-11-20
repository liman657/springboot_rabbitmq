package com.learn.springbootrabbitmqlearn.listener;

import com.learn.springbootrabbitmqlearn.entity.OrderRecord;
import com.learn.springbootrabbitmqlearn.mapper.OrderRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * autor:liman
 * createtime:2019/10/20
 * comment:
 */
@Component

public class InsertOrderRecordListener implements ApplicationListener<InsertOrderRecordEvent> {
    private static final Logger log = LoggerFactory.getLogger(InsertOrderRecordListener.class);

    @Autowired
    private OrderRecordMapper orderRecordMapper;

    @Override
    public void onApplicationEvent(InsertOrderRecordEvent insertOrderRecordEvent) {
        log.info("监听到下单记录");
        try{
            if(insertOrderRecordEvent!=null){
                OrderRecord orderRecord = new OrderRecord();
                BeanUtils.copyProperties(insertOrderRecordEvent,orderRecord);
                try {
                    log.info("我看看事件处理是否是异步的");
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                orderRecordMapper.insert(orderRecord);
                log.info("数据保存完成");
            }
        }catch (Exception e){
            log.error("监听下单事件异常，异常信息为:{}",e.fillInStackTrace());
        }
    }
}
