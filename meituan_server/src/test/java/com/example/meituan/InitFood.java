package com.example.meituan;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.meituan.domain.BusinessPosition;
import com.example.meituan.domain.Food;
import com.example.meituan.service.BusinessPositionService;
import com.example.meituan.service.FoodService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : [wangminan]
 * @description : [一句话描述该类的功能]
 */
@SpringBootTest
@Slf4j
public class InitFood {
    @Resource
    private FoodService foodService;

    @Resource
    private BusinessPositionService businessPositionService;

    @Test
    void initFood(){
        List<Food> foods = foodService.list();
        for (Food food : foods) {
            // 加入经纬度
            if(food.getDistrict() != null && food.getBusinessDistrict() != null){
                String business = food.getDistrict()+food.getBusinessDistrict();
                QueryWrapper<BusinessPosition> wrapper = new QueryWrapper<>();
                wrapper.eq("address", business);
                BusinessPosition position = businessPositionService.getOne(wrapper);
                food.setLatitude(position.getLatitude());
                food.setLongitude(position.getLongitude());
            } else {
                food.setLatitude((double) 0);
                food.setLongitude((double) 0);
            }
            foodService.updateById(food);
        }
    }
}
