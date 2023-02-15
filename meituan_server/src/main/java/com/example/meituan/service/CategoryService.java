package com.example.meituan.service;

import com.example.meituan.pojo.R;

public interface CategoryService {
    R getTotalShopByCategory();

    R getAvgPriceByCategory();

    R getTotalCommentByCategory();
}
