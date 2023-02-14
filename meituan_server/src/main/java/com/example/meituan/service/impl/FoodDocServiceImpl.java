package com.example.meituan.service.impl;

import com.example.meituan.dto.DistrictDto;
import com.example.meituan.pojo.R;
import com.example.meituan.service.FoodDocService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
                getSearchResponse("DistrictAggression", "district");
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        List<DistrictDto> aggByDistrict = getAggCntByName(aggregations, "DistrictAggression");
        return R.ok().put("result",aggByDistrict);
    }

    /**
     * 西安各行政区划餐饮历史评价数量对比
     * @return 西安各行政区划餐饮历史评价数量对比
     */
    @Override
    public R getFlowByDistrict() throws IOException{
        SearchResponse response =
                getSearchResponse("DistrictAggression", "district");
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        List<DistrictDto> aggByDistrict = getAggCntByName(aggregations, "DistrictAggression");
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
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
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
                getSearchResponse("DistrictAggression", "district");
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        List<DistrictDto> aggByDistrict = getAggCntByName(aggregations, "DistrictAggression");
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
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations1 = searchResponse.getAggregations();
            Sum flow = aggregations1.get("flow");
            // 转换 double转int
            result.add(new DistrictDto(district, (int)flow.getValue()));
        }
        return R.ok().put("result",result);
    }

    private SearchResponse getSearchResponse(String aggName, String fieldName){
        SearchRequest searchRequest = new SearchRequest("meituan");
        searchRequest.source().aggregation(
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

    private List<DistrictDto> getAggCntByName(Aggregations aggregations, String aggName) {
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
}
