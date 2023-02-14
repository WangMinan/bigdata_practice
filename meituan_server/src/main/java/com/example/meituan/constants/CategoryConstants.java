package com.example.meituan.constants;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : [wangminan]
 * @description : [类别]
 */
public class CategoryConstants {

    private CategoryConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, Integer> initCategoryMap(){
        Map<String, Integer> result = new HashMap<>();
        result.put("代金券",0);
        result.put("蛋糕甜点",0);
        result.put("火锅",0);
        result.put("自助餐",0);
        result.put("小吃快餐",0);
        result.put("日韩料理",0);
        result.put("西餐",0);
        result.put("聚餐宴请",0);
        result.put("烧烤烤肉",0);
        result.put("东北菜",0);
        result.put("川湘菜",0);
        result.put("江浙菜",0);
        result.put("香锅烤鱼",0);
        result.put("粤菜",0);
        result.put("中式烧烤_烤串",0);
        result.put("西北菜",0);
        result.put("咖啡酒吧",0);
        result.put("京菜鲁菜",0);
        result.put("云贵菜",0);
        result.put("东南亚菜",0);
        result.put("海鲜",0);
        result.put("素食",0);
        result.put("台湾_客家菜",0);
        result.put("创意菜",0);
        result.put("汤_粥_炖菜",0);
        result.put("蒙餐",0);
        result.put("新疆菜",0);
        result.put("其他美食",0);
        return result;
    }
}
