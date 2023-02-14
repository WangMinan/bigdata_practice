package com.example.meituan.service.impl;

import com.example.meituan.pojo.R;
import com.example.meituan.service.DistrictService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.meituan.constants.CategoryConstants.initCategoryMap;
import static com.example.meituan.constants.FoodIndexConstants.MEITUAN_INDEX_NAME;

/**
 * @author : [wangminan]
 * @description : [区划角度的service]
 */
@Service
public class DistrictServiceImpl implements DistrictService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    private static final String DISTRICT_AGGREGATION_NAME = "DistrictAggression";
    private static final String DISTRICT_FIELD_NAME = "district";
    private static final String RESULT = "result";

    @Override
    public R getMerchantNumberByDistrict(){
        SearchResponse response =
                getSearchResponse(QueryBuilders.matchAllQuery(),
                        DISTRICT_AGGREGATION_NAME, DISTRICT_FIELD_NAME);
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        Map<String, Integer> aggByDistrict = getDistrictDtoCount(aggregations, DISTRICT_AGGREGATION_NAME);
        return R.ok().put(RESULT,aggByDistrict);
    }

    /**
     * 西安各行政区划餐饮历史评价数量对比
     * @return 西安各行政区划餐饮历史评价数量对比
     */
    @Override
    public R getFlowByDistrict(){
        SearchResponse response =
                getSearchResponse(QueryBuilders.matchAllQuery(),
                        DISTRICT_AGGREGATION_NAME, DISTRICT_FIELD_NAME);
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        Map<String, Integer> aggByDistrict = getDistrictDtoCount(aggregations, DISTRICT_AGGREGATION_NAME);
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : aggByDistrict.entrySet()) {
            String district = entry.getKey();
            BoolQueryBuilder queryBuilder =
                    QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery(DISTRICT_FIELD_NAME, district));
            SearchRequest searchRequest = new SearchRequest(MEITUAN_INDEX_NAME);
            searchRequest.source()
                    .query(queryBuilder)
                    .aggregation(
                        AggregationBuilders
                                .sum("flow")
                                .field("allCommentNum")
            );
            SearchResponse searchResponse = null;
            try {
                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Aggregations aggregations1 = searchResponse.getAggregations();
            Sum flow = aggregations1.get("flow");
            // 转换 double转int
            result.put(district, (int) flow.getValue());
        }
        return R.ok().put(RESULT,result);
    }

    @Override
    public R getMerchantTypeByDistrict() {
        SearchResponse response =
                getSearchResponse(QueryBuilders.matchAllQuery(),
                        DISTRICT_AGGREGATION_NAME, DISTRICT_FIELD_NAME);
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        Map<String, Integer> aggByDistrict = getDistrictDtoCount(aggregations, DISTRICT_AGGREGATION_NAME);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : aggByDistrict.entrySet()) {
            String district = entry.getKey();
            BoolQueryBuilder queryBuilder =
                    QueryBuilders.boolQuery()
                            .must(QueryBuilders.termQuery(DISTRICT_FIELD_NAME, district));
            SearchResponse searchResponse =
                    getSearchResponse(queryBuilder,
                            "MerchantTypeAggression", "category");
            Map<String, Object> map = new HashMap<>();
            map.put(district,
                    getCategoryCount(searchResponse.getAggregations(), "MerchantTypeAggression"));
            result.add(map);
        }
        return R.ok().put(RESULT,result);
    }

    /**
     * 获取各行政区划平均价格
     * @return 各行政区划餐饮平均价格
     */
    @Override
    public R getAvgPriceByDistrict(){
        RangeQueryBuilder queryBuilder =
                QueryBuilders
                        .rangeQuery("avgPrice")
                        .gt(0);
        SearchResponse response =
                getSearchResponse(queryBuilder, DISTRICT_AGGREGATION_NAME, DISTRICT_FIELD_NAME);
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        Map<String, Integer> aggByDistrict = getDistrictDtoCount(aggregations, DISTRICT_AGGREGATION_NAME);
        Map<String, Long> avgPriceMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : aggByDistrict.entrySet()) {
            String district = entry.getKey();
            BoolQueryBuilder queryBuilder1 =
                    QueryBuilders.boolQuery()
                            .must(QueryBuilders.termQuery(DISTRICT_FIELD_NAME, district));
            SearchRequest searchRequest = new SearchRequest(MEITUAN_INDEX_NAME);
            searchRequest.source()
                    .query(queryBuilder1)
                    .aggregation(
                            AggregationBuilders
                                    .sum("totalPrice")
                                    .field("avgPrice")
                    );
            SearchResponse searchResponse = null;
            try {
                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Aggregations aggregations1 = searchResponse.getAggregations();
            Sum totalPrice = aggregations1.get("totalPrice");
            avgPriceMap.put(district, (long) (totalPrice.getValue() / entry.getValue()));
        }
        return R.ok().put(RESULT,avgPriceMap);
    }

    private SearchResponse getSearchResponse(QueryBuilder queryBuilder, String aggName, String fieldName){
        SearchRequest searchRequest = new SearchRequest(MEITUAN_INDEX_NAME);
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

    private Map<String, Integer> getDistrictDtoCount(Aggregations aggregations, String aggName) {
        // 4.1.根据聚合名称获取聚合结果
        Terms brandTerms = aggregations.get(aggName);
        // 4.2.获取buckets
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        // 4.3.遍历
        Map<String, Integer> map = new HashMap<>();
        for (Terms.Bucket bucket : buckets) {
            // 4.4.获取key
            String key = bucket.getKeyAsString();
            int number = Math.toIntExact(bucket.getDocCount());
            map.put(key, number);
        }
        return map;
    }

    private Map<String, Integer> getCategoryCount(Aggregations aggregations, String aggName) {
        // 4.1.根据聚合名称获取聚合结果
        Terms brandTerms = aggregations.get(aggName);
        // 4.2.获取buckets
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        // 4.3.遍历
        Map<String, Integer> tmpMap = new HashMap<>();
        for (Terms.Bucket bucket : buckets) {
            // 4.4.获取key
            String key = bucket.getKeyAsString();
            long number = bucket.getDocCount();
            tmpMap.put(key, Math.toIntExact(number));
        }
        // 清洗数据 遍历tmpMap
        Map<String, Integer> result = initCategoryMap();
        // 双层循环
        for (Map.Entry<String, Integer> entry : tmpMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            for (Map.Entry<String, Integer> entry1 : result.entrySet()) {
                String key1 = entry1.getKey();
                if (key.contains(key1)){
                    result.put(key1, result.get(key1) + value);
                }
            }
        }
        return result;
    }
}
