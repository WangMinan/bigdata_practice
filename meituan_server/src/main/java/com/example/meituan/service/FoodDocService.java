package com.example.meituan.service;

import com.example.meituan.pojo.R;

import java.io.IOException;

public interface FoodDocService {
    R getMerchantNumberByDistrict() throws IOException;

    R getFlowByDistrict() throws IOException;

    R getMerchantTypeByDistrict() throws IOException;

    R getAvgPriceByDistrict() throws IOException;
}
