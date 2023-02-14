package com.example.meituan.service.impl;

import com.example.meituan.constants.CategoryConstants;
import com.example.meituan.dto.CategoryDto;
import com.example.meituan.pojo.R;
import com.example.meituan.service.FoodShopService;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsNodes;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FoodShopServiceImpl implements FoodShopService {

    private static final Map<String, Integer> map = CategoryConstants.initCategoryMap();
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public R getTotalShopByCategory() throws IOException {
        List<CategoryDto> CategoryMap = new ArrayList<>();
        for (Map.Entry<String, Integer> cateName : map.entrySet()) {
            List<CategoryDto> aggByCategory;
            SearchResponse response =
                    getSearchResponse(QueryBuilders.matchQuery("all", cateName.getKey()), "CategoryAggression", "category");
            // 解析聚合结果
            Aggregations aggregations = response.getAggregations();
            aggByCategory = getCategoryDtoCount(aggregations, "CategoryAggression");
            int cnt = 0;
            for (CategoryDto categoryDto : aggByCategory) {
                if (categoryDto.getCategory().equals("")) {
                    continue;
                }
                cnt += categoryDto.getCount();
            }

            CategoryMap.add(new CategoryDto(cateName.getKey(), cnt));
        }
        return R.ok().put("result", CategoryMap);
    }

    @Override
    public R getAvgPriceByCategory() throws IOException {
        Map<String, Long> avgPriceMap = new HashMap<>();
        for (Map.Entry<String, Integer> cate : map.entrySet()) {
            if(cate.getKey().startsWith("代金券")){
                int cnt=0;
            }
            List<CategoryDto> aggByCategory;
            SearchResponse response = getSearchResponse(
                            QueryBuilders.matchQuery("all", cate.getKey()), "CategoryAggression", "category");
            // 解析聚合结果
            Aggregations aggregations = response.getAggregations();
            aggByCategory = getCategoryDtoCount(aggregations, "CategoryAggression");

            //{result={自助餐=74, 蒙餐=389, 海鲜=292, 创意菜=151, 台湾_客家菜=43, 烧烤烤肉=234, 新疆菜=131, 中式烧烤_烤串=223, 其他美食=157, 川湘菜=215, 代金券=67, 聚餐宴请=68, 西餐=152, 云贵菜=131, 汤_粥_炖菜=1180, 咖啡酒吧=2639, 江浙菜=131, 日韩料理=427, 西北菜=136, 蛋糕甜点=677, 东北菜=87, 京菜鲁菜=94, 素食=791, 火锅=214, 香锅烤鱼=298, 粤菜=161, 小吃快餐=162, 东南亚菜=131}, code=200}
//            long TotalPrice = 0L;
//            int cnt=0;
//            for (CategoryDto categoryDto : aggByCategory) {
//                if (categoryDto.getCategory().equals("")) {
//                    continue;
//                }
//                String category = categoryDto.getCategory();
//                BoolQueryBuilder queryBuilder1 =
//                        QueryBuilders.boolQuery()
//                                .must(QueryBuilders.termQuery("category",category));
//                SearchRequest searchRequest = new SearchRequest("meituan");
//                searchRequest.source()
//                        .query(queryBuilder1)
//                        .aggregation(
//                                AggregationBuilders
//                                        .sum("totalPrice")
//                                        .field("avgPrice")
//                        );
//                SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//                Aggregations aggregations1 = searchResponse.getAggregations();
//                Sum totalPrice = aggregations1.get("totalPrice");
//                // 转换 double转int
//                TotalPrice+=(long)totalPrice.getValue();
//                cnt+=categoryDto.getCount();
//            }
//            avgPriceMap.put(cate.getKey(), (long) (TotalPrice/cnt));

            //{result={自助餐=74, 蒙餐=73, 海鲜=74, 创意菜=62, 台湾_客家菜=43, 烧烤烤肉=80, 新疆菜=59, 中式烧烤_烤串=70, 其他美食=64, 川湘菜=61, 代金券=72, 聚餐宴请=75, 西餐=74, 云贵菜=58, 汤_粥_炖菜=54, 咖啡酒吧=190, 江浙菜=58, 日韩料理=101, 西北菜=60, 蛋糕甜点=585, 东北菜=42, 京菜鲁菜=52, 素食=50, 火锅=106, 香锅烤鱼=78, 粤菜=99, 小吃快餐=33, 东南亚菜=58}, code=200}
            for (CategoryDto categoryDto : aggByCategory) {
                if (categoryDto.getCategory().equals("")) {
                    continue;
                }
                cate.setValue(categoryDto.getCount() + cate.getValue());
            }
            String category = cate.getKey();
            BoolQueryBuilder queryBuilder1 =
                    QueryBuilders.boolQuery()
                            .must(QueryBuilders.matchQuery("all", category));
            SearchRequest searchRequest = new SearchRequest("meituan");
            searchRequest.source()
                    .query(queryBuilder1)
                    .aggregation(
                            AggregationBuilders
                                    .count("totalPrice")
                                    .field("avgPrice")
                    );
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations1 = searchResponse.getAggregations();
            ParsedValueCount totalPrice = aggregations1.get("totalPrice");
            // 转换 double转int
            avgPriceMap.put(category, (long) (totalPrice.getValue() / cate.getValue()));
        }


        return R.ok().put("result", avgPriceMap);
    }

    @Override
    public R getTotalCommentByCategory() throws IOException {
        Map<String, Long> avgPriceMap = new HashMap<>();
        for (Map.Entry<String, Integer> cate : map.entrySet()) {
            List<CategoryDto> aggByCategory;
            SearchResponse response =
                    getSearchResponse(QueryBuilders.matchQuery("all", cate.getKey()), "CategoryAggression", "category");
            // 解析聚合结果
            Aggregations aggregations = response.getAggregations();
            aggByCategory = getCategoryDtoCount(aggregations, "CategoryAggression");

            for (CategoryDto categoryDto : aggByCategory) {
                if (categoryDto.getCategory().equals("")) {
                    continue;
                }
                cate.setValue(categoryDto.getCount() + cate.getValue());
            }
            String category = cate.getKey();
            BoolQueryBuilder queryBuilder1 =
                    QueryBuilders.boolQuery()
                            .must(QueryBuilders.matchQuery("all", category));
            SearchRequest searchRequest = new SearchRequest("meituan");
            searchRequest.source()
                    .query(queryBuilder1)
                    .aggregation(
                            AggregationBuilders
                                    .count("totalPrice")
                                    .field("avgPrice")
                    );
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations1 = searchResponse.getAggregations();
            ParsedValueCount totalPrice = aggregations1.get("totalPrice");
            // 转换 double转int
            avgPriceMap.put(category, (long) (totalPrice.getValue() / cate.getValue()));
        }
        return R.ok().put("result", avgPriceMap);
    }

    private SearchResponse getSearchResponse(QueryBuilder queryBuilder, String aggName, String fieldName) {
        SearchRequest searchRequest = new SearchRequest("meituan");
        searchRequest.source()
                .query(queryBuilder)
                .aggregation(
                        AggregationBuilders
                                .terms(aggName)
                                .field(fieldName)
                                .size(100)
                );
        // 发送请求
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<CategoryDto> getCategoryDtoCount(Aggregations aggregations, String aggName) {
        // 4.1.根据聚合名称获取聚合结果
        Terms brandTerms = aggregations.get(aggName);
        // 4.2.获取buckets
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        // 4.3.遍历
        List<CategoryDto> categoryDto = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            // 4.4.获取key
            String key = bucket.getKeyAsString();
            long number = bucket.getDocCount();
            categoryDto.add(new CategoryDto(key, Math.toIntExact(number)));
        }
        return categoryDto;
    }
}
