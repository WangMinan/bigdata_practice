package com.example.meituan.service.impl;

import com.example.meituan.constants.CategoryConstants;
import com.example.meituan.exception.SearchException;
import com.example.meituan.pojo.R;
import com.example.meituan.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.meituan.constants.FoodIndexConstants.MEITUAN_INDEX_NAME;

/**
 * @author 林昊辰 wangminan
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private static Map<String, Integer> categoryMap = CategoryConstants.initCategoryMap();

    private static final String CATEGORY_AGG_NAME = "CategoryAggression";
    private static final String CATEGORY_FIELD_NAME = "category";
    private static final String RESULT = "result";


    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public R getTotalShopByCategory(){
        putMerchantNumberToMap(QueryBuilders.matchAllQuery());
        return R.ok().put(RESULT, categoryMap);
    }

    @Override
    public R getAvgPriceByCategory(){
        // 重置 categoryMap
        resetCategoryMap();
        Map<String, Integer> avgPriceMap = new HashMap<>();
        RangeQueryBuilder queryBuilder =
                QueryBuilders
                        .rangeQuery("avgPrice")
                        .gt(0);
        putMerchantNumberToMap(queryBuilder);

        for (Map.Entry<String, Integer> cate : categoryMap.entrySet()) {
            String category = cate.getKey();
            SearchResponse searchResponse =
                    getSumAggSearchResponse(category, "totalPrice", "avgPrice");
            Aggregations aggregations = searchResponse.getAggregations();
            Sum totalPrice = aggregations.get("totalPrice");
            // 转换 double转int
            avgPriceMap.put(category,
                    totalPrice.getValue() == 0 ? 0: (int) totalPrice.getValue() / cate.getValue());
        }
        return R.ok().put(RESULT, avgPriceMap);
    }

    @Override
    public R getTotalCommentByCategory(){
        // 重置 categoryMap
        resetCategoryMap();
        Map<String, Integer> totalCommentMap = new HashMap<>();
        putMerchantNumberToMap(QueryBuilders.matchAllQuery());

        for (Map.Entry<String, Integer> cate : categoryMap.entrySet()) {
            String category = cate.getKey();
            SearchResponse searchResponse =
                    getSumAggSearchResponse(category, "totalComment", "allCommentNum");
            Aggregations aggregations = searchResponse.getAggregations();
            Sum totalComment = aggregations.get("totalComment");
            // 转换 double转int
            totalCommentMap.put(category, (int) totalComment.getValue());
        }
        return R.ok().put(RESULT, totalCommentMap);
    }

    private void putMerchantNumberToMap(QueryBuilder queryBuilder){
        SearchResponse response =
                getAggSearchResponse(queryBuilder);
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        getNumberOfMerchantsByAgg(aggregations);
    }

    private SearchResponse getAggSearchResponse(QueryBuilder queryBuilder) {
        SearchRequest searchRequest = new SearchRequest(MEITUAN_INDEX_NAME);
        searchRequest.source()
                .query(queryBuilder)
                .aggregation(
                        AggregationBuilders
                                .terms(CategoryServiceImpl.CATEGORY_AGG_NAME)
                                .field(CategoryServiceImpl.CATEGORY_FIELD_NAME)
                                .size(600)
                );
        // 发送请求
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new SearchException("查询失败");
        }
    }

    private SearchResponse getSumAggSearchResponse(String category, String sumName, String sumField) {
        BoolQueryBuilder queryBuilder =
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.wildcardQuery(CATEGORY_FIELD_NAME, "*" + category + "*"));
        SearchRequest searchRequest = new SearchRequest(MEITUAN_INDEX_NAME);
        searchRequest.source()
                .query(queryBuilder)
                .aggregation(
                        AggregationBuilders
                                .sum(sumName)
                                .field(sumField)
                );
        // 发送请求
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new SearchException("求和失败");
        }
    }

    private void getNumberOfMerchantsByAgg(Aggregations aggregations) {
        Map<String, Integer> aggByCategory;
        aggByCategory = getCategoryDtoCount(aggregations);

        // 双层循环 获取某一类型店铺总数
        for (Map.Entry<String, Integer> categoryDto : aggByCategory.entrySet()) {
            if (categoryDto.getKey().equals("")) {
                continue;
            }
            for (Map.Entry<String, Integer> cate : categoryMap.entrySet()) {
                if (categoryDto.getKey().contains(cate.getKey())) {
                    categoryMap.put(cate.getKey(), cate.getValue() + categoryDto.getValue());
                }
            }
        }
    }

    private Map<String, Integer> getCategoryDtoCount(Aggregations aggregations) {
        // 4.1.根据聚合名称获取聚合结果
        Terms brandTerms = aggregations.get(CategoryServiceImpl.CATEGORY_AGG_NAME);
        // 4.2.获取buckets
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        // 4.3.遍历
        Map<String, Integer> categoryDto = new HashMap<>();
        for (Terms.Bucket bucket : buckets) {
            // 4.4.获取key
            String key = bucket.getKeyAsString();
            long number = bucket.getDocCount();
            categoryDto.put(key, (int) number);
        }
        return categoryDto;
    }

    private static void resetCategoryMap() {
        categoryMap = CategoryConstants.initCategoryMap();
    }
}
