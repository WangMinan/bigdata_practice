package com.example.meituan.service.impl;

import com.example.meituan.dto.DistrictDto;
import com.example.meituan.pojo.R;
import com.example.meituan.service.FoodDocService;
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

/**
 * @author : [wangminan]
 * @description : [一句话描述该类的功能]
 */
@Service
public class FoodDocServiceImpl implements FoodDocService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public R getMerchantNumberByDistrict(){
        SearchResponse response =
                getSearchResponse(QueryBuilders.matchAllQuery(),
                        "DistrictAggression", "district");
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        List<DistrictDto> aggByDistrict = getDistrictDtoCount(aggregations, "DistrictAggression");
        return R.ok().put("result",aggByDistrict);
    }

    /**
     * 西安各行政区划餐饮历史评价数量对比
     * @return 西安各行政区划餐饮历史评价数量对比
     */
    @Override
    public R getFlowByDistrict(){
        SearchResponse response =
                getSearchResponse(QueryBuilders.matchAllQuery(),
                        "DistrictAggression", "district");
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        List<DistrictDto> aggByDistrict = getDistrictDtoCount(aggregations, "DistrictAggression");
        List<DistrictDto> result = new ArrayList<>();
        for (DistrictDto districtDto : aggByDistrict) {
            String district = districtDto.getDistrict();
            BoolQueryBuilder queryBuilder =
                    QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("district", district));
            SearchRequest searchRequest = new SearchRequest("meituan");
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
            result.add(new DistrictDto(district, (int)flow.getValue()));
        }
        return R.ok().put("result",result);
    }

    @Override
    public R getMerchantTypeByDistrict() {
        SearchResponse response =
                getSearchResponse(QueryBuilders.matchAllQuery(),
                        "DistrictAggression", "district");
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        List<DistrictDto> aggByDistrict = getDistrictDtoCount(aggregations, "DistrictAggression");
        Map<String, Object> result = new HashMap<>();
        for (DistrictDto districtDto : aggByDistrict) {
            String district = districtDto.getDistrict();
            BoolQueryBuilder queryBuilder =
                    QueryBuilders.boolQuery()
                            .must(QueryBuilders.termQuery("district", district));
            SearchResponse searchResponse =
                    getSearchResponse(queryBuilder,
                            "MerchantTypeAggression", "category");
            result.put(district,
                    getCategoryCount(searchResponse.getAggregations(), "MerchantTypeAggression"));
        }
        return R.ok().put("result",result);
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
                getSearchResponse(queryBuilder,"DistrictAggression", "district");
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        List<DistrictDto> aggByDistrict = getDistrictDtoCount(aggregations, "DistrictAggression");
        Map<String, Long> avgPriceMap = new HashMap<>();
        for (DistrictDto districtDto : aggByDistrict) {
            String district = districtDto.getDistrict();
            BoolQueryBuilder queryBuilder1 =
                    QueryBuilders.boolQuery()
                            .must(QueryBuilders.termQuery("district", district));
            SearchRequest searchRequest = new SearchRequest("meituan");
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
            avgPriceMap.put(district, (long) (totalPrice.getValue() / districtDto.getCount()));
        }
        return R.ok().put("result",avgPriceMap);
    }

    private SearchResponse getSearchResponse(QueryBuilder queryBuilder, String aggName, String fieldName){
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

    private List<DistrictDto> getDistrictDtoCount(Aggregations aggregations, String aggName) {
        // 4.1.根据聚合名称获取聚合结果
        Terms brandTerms = aggregations.get(aggName);
        // 4.2.获取buckets
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        // 4.3.遍历
        List<DistrictDto> districtDto = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            // 4.4.获取key
            String key = bucket.getKeyAsString();
            long number = bucket.getDocCount();
            districtDto.add(new DistrictDto(key, Math.toIntExact(number)));
        }
        return districtDto;
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
