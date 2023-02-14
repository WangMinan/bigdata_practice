package com.example.meituan.service.impl;

import com.example.meituan.dto.CategoryDto;
import com.example.meituan.exception.SearchException;
import com.example.meituan.pojo.R;
import com.example.meituan.service.CategoryService;
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
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.meituan.constants.CategoryConstants.initCategoryMap;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Map<String, Integer> map = initCategoryMap();
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public R getTotalShopByCategory(){
        List<CategoryDto> CategoryMap = new ArrayList<>();
        for (Map.Entry<String, Integer> cateName : map.entrySet()) {
            List<CategoryDto> aggByCategory;
            SearchResponse response =
                    getSearchResponse(
                            QueryBuilders.matchQuery("all", cateName.getKey()), "CategoryAggression", "category");
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
    public R getAvgPriceByCategory(){
        RangeQueryBuilder queryBuilder =
                QueryBuilders
                        .rangeQuery("avgPrice")
                        .gt(0);
        SearchResponse response =
                getSearchResponse(queryBuilder, "CategoryAggression", "category");
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        Map<String, Integer> categoryMap = initCategoryMap();
        List<CategoryDto> aggByCategory = getCategoryDtoCount(aggregations, "CategoryAggression");
        // 双重循环
        for (CategoryDto categoryDto : aggByCategory) {
            if (categoryDto.getCategory().equals("")) {
                continue;
            }
            for (Map.Entry<String, Integer> cate : categoryMap.entrySet()) {
                if (categoryDto.getCategory().contains(cate.getKey())) {
                    cate.setValue(categoryDto.getCount() + cate.getValue());
                }
            }
        }
        return R.ok().put("result", categoryMap);
    }

    @Override
    public R getTotalCommentByCategory(){
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
            SearchResponse searchResponse = null;
            try {
                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new SearchException("查询总评论数失败");
            }
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
            throw new SearchException("查询失败");
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
