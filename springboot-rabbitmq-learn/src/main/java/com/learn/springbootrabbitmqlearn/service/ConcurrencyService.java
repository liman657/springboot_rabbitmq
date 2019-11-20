package com.learn.springbootrabbitmqlearn.service;

import com.learn.springbootrabbitmqlearn.entity.Product;
import com.learn.springbootrabbitmqlearn.mapper.ProductMapper;
import com.learn.springbootrabbitmqlearn.mapper.ProductRobbingRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * autor:liman
 * createtime:2019/10/22
 * comment:
 */
@Service
public class ConcurrencyService {
    private static final Logger log= LoggerFactory.getLogger(ConcurrencyService.class);

    private static final String ProductNo="product_10010";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRobbingRecordMapper productRobbingRecordMapper;

    /**
     * 处理抢单
     * @param mobile
     */
    public void manageRobbing(String mobile){

        try{
            Product product = productMapper.selectByProductNo(ProductNo);
            if(product!=null && product.getTotal()>0){
                int count = productMapper.updateTotal(product);
                if(count > 0){//大于零表示更新成功，这里利用了MYSQL的机制
                    log.info("手机号:{},抢到单了",mobile);
                }
            }else{
                log.info("机会从你手中溜走了，别想了,就是你，手机号为:{}",mobile);
            }
        }catch (Exception e){
            log.error("{}",e.fillInStackTrace());
        }

        //V1.0
//        try {
//            Product product=productMapper.selectByProductNo(ProductNo);
//            if (product!=null && product.getTotal()>0){
//                log.info("当前手机号：{} 恭喜您抢到单了!",mobile);
//                productMapper.updateTotal(product);
//            }else{
//                log.error("当前手机号：{} 抢不到单!",mobile);
//
//            }
//        }catch (Exception e){
//            log.error("处理抢单发生异常：mobile={} ",mobile);
//        }
    }
}
