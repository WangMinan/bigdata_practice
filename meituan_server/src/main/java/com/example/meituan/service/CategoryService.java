package com.example.meituan.service;

import com.example.meituan.pojo.R;

import java.io.IOException;

public interface CategoryService {
    R getTotalShopByCategory();

    R getAvgPriceByCategory();

    R getTotalCommentByCategory();
}
