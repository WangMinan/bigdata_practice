package com.example.meituan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.meituan.domain.Food;
import com.example.meituan.mapper.FoodMapper;
import com.example.meituan.service.FoodService;
import org.springframework.stereotype.Service;

/**
* @author wangminan
* @description 针对表【food】的数据库操作Service实现
* @createDate 2023-02-09 10:55:12
*/
@Service
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food>
    implements FoodService{

}




