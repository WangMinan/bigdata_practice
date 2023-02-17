package com.example.meituan.service;

import com.example.meituan.pojo.R;

public interface DistrictService {
    R getMerchantNumberByDistrict();

    R getFlowByDistrict();

    R getMerchantTypeByDistrict();

    R getAvgPriceByDistrict();

    R getDistrictMap();
}
