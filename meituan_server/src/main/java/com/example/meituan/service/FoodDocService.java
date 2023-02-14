package com.example.meituan.service;

import com.example.meituan.pojo.R;

import java.io.IOException;

public interface FoodDocService {
    R getMerchantNumberByDistrict();

    R getFlowByDistrict();

    R getMerchantTypeByDistrict();

    R getAvgPriceByDistrict();
}
