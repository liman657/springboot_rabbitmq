package com.learn.springbootrabbitmqlearn.service;

import com.learn.springbootrabbitmqlearn.producer.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;

/**
 * autor:liman
 * createtime:2019/10/22
 * comment:
 */
@Service
public class InitService {
    private static final Logger log= LoggerFactory.getLogger(InitService.class);

    public static final int ThreadNum = 1000;

    private static int mobile = 0;

    @Autowired
    private ConcurrencyService concurrencyService;

    @Autowired
    private ProducerService producerService;

    public void generateMultiThread(){
        log.info("开始初始化线程数----> ");
        try {
            for (int i=0;i<ThreadNum;i++){
                new Thread(new RunThread()).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class RunThread implements Runnable{
        public void run() {
            try {
                mobile += 1;
                log.info("生成手机号:{}",mobile);
//                concurrencyService.manageRobbing(String.valueOf(mobile));
                producerService.sendRobbingMessage(String.valueOf(mobile));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
