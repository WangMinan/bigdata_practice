package com.example.meituan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.meituan.domain.BusinessPosition;
import com.example.meituan.service.BusinessPositionService;
import com.example.meituan.mapper.BusinessPositionMapper;
import org.springframework.stereotype.Service;

/**
* @author wangminan
* @description 针对表【business_position】的数据库操作Service实现
* @createDate 2023-02-09 13:30:32
*/
@Service
public class BusinessPositionServiceImpl extends ServiceImpl<BusinessPositionMapper, BusinessPosition>
    implements BusinessPositionService{

}




