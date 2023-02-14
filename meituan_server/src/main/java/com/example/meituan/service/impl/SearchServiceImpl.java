package com.example.meituan.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.meituan.domain.FoodDoc;
import com.example.meituan.dto.PageResult;
import com.example.meituan.dto.QueryDto;
import com.example.meituan.dto.SmartSuggestionDto;
import com.example.meituan.dto.SuggestionDto;
import com.example.meituan.exception.SearchException;
import com.example.meituan.pojo.R;
import com.example.meituan.service.SearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.meituan.constants.FoodIndexConstants.MEITUAN_INDEX_NAME;

/**
 * @author : [wangminan]
 * @description : [搜索服务实现类]
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    private static final String RESULT = "result";
    private static final String AVERAGE_SCORE_FIELD_NAME = "avgScore";
    private static final String ALL_COMMENT_NUM_FIELD_NAME = "allCommentNum";

    @Override
    public R getSuggestion(SuggestionDto suggestionDto) {
        String key = suggestionDto.getKeyword();
        SearchRequest request = new SearchRequest(MEITUAN_INDEX_NAME);
        request.source()
                .suggest(new SuggestBuilder().addSuggestion(
                                // suggestName
                                "suggestions",
                                // 指定建议的字段
                                SuggestBuilders.completionSuggestion("suggestion")
                                        // 指定建议的关键字
                                        .prefix(key)
                                        .skipDuplicates(true)
                                        .size(10)
                        )
                );
        // 发送请求
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            Suggest suggest = response.getSuggest();
            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
            // 获取options
            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
            List<String> result = new ArrayList<>();
            for (CompletionSuggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                result.add(text);
            }
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put(RESULT, result);
            return R.ok(resultMap);
        } catch (IOException e) {
            throw new SearchException("获取建议失败");
        }
    }

    @Override
    public R query(QueryDto queryDto) {
        try {
            // 1.准备Request
            SearchRequest request = new SearchRequest(MEITUAN_INDEX_NAME);
            // 2.准备请求参数
            // 2.1.query
            buildBasicQuery(queryDto, request);
            // 2.2.分页
            int page = queryDto.getPageNum();
            int size = queryDto.getPageSize();
            request.source().from((page - 1) * size).size(size);
            // 3.发送请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 4.解析响应
            PageResult pageResult = handlePageResponse(response);
            Map<String,Object> result = new HashMap<>();
            result.put(RESULT, pageResult);
            return R.ok(result);
        } catch (IOException e) {
            throw new SearchException("搜索失败");
        }
    }

    @Override
    public R getSmartSuggestion(SmartSuggestionDto smartSuggestionDto) {
        try{
            SearchRequest request = new SearchRequest(MEITUAN_INDEX_NAME);
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            // 1.关键字
            String keyword = smartSuggestionDto.getKeyword();
            if (StringUtils.isNotBlank(keyword)) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("all", keyword));
            } else {
                boolQueryBuilder.must(QueryBuilders.matchAllQuery());
            }
            int scoreAdd4 = RandomUtil.randomInt(10, 20);
            int scoreAdd3 = RandomUtil.randomInt(5, 10);
            int commentAdd1000 = RandomUtil.randomInt(20, 30);
            int commentAdd100 = RandomUtil.randomInt(5, 10);
            // 2.算分函数查询
            FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                boolQueryBuilder, // 原始查询，boolQuery
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                    // 根据avgScore和allCommentNum进行加权
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.rangeQuery(AVERAGE_SCORE_FIELD_NAME).gte(4),
                        ScoreFunctionBuilders.weightFactorFunction(scoreAdd4)
                    ),
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.rangeQuery(AVERAGE_SCORE_FIELD_NAME).gte(3),
                        ScoreFunctionBuilders.weightFactorFunction(scoreAdd3)
                    ),
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.rangeQuery(ALL_COMMENT_NUM_FIELD_NAME).gte(100),
                        ScoreFunctionBuilders.weightFactorFunction(commentAdd1000)
                    ),
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.rangeQuery(ALL_COMMENT_NUM_FIELD_NAME).gte(100),
                        ScoreFunctionBuilders.weightFactorFunction(commentAdd100)
                    )
                }
            );
            request.source().query(functionScoreQuery);
            // 2.3.距离排序
            Double latitude = smartSuggestionDto.getLatitude();
            Double longitude = smartSuggestionDto.getLongitude();
            if (latitude != null && longitude != null) {
                String location = latitude + "," + longitude;
                request.source().sort(SortBuilders
                        .geoDistanceSort("location", new GeoPoint(location))
                        .order(SortOrder.ASC)
                        .unit(DistanceUnit.KILOMETERS)
                );
            }
            // 3.发送请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 4.解析响应
            List<FoodDoc> foodDocs = handleSmartSuggestionResponse(response);
            // 截取前15个
            if (foodDocs.size() > 15) {
                foodDocs = foodDocs.subList(0, 15);
            }
            Map<String,Object> result = new HashMap<>();
            result.put(RESULT, foodDocs);
            return R.ok(result);
        } catch (IOException e) {
            throw new SearchException("获取智能建议失败");
        }
    }

    private void buildBasicQuery(QueryDto params, SearchRequest request){
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // 1.关键字
        String keyword = params.getQuery();
        if (StringUtils.isNotBlank(keyword)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("all", keyword));
        } else {
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        }
        // 2.分类
        String category = params.getType();
        if (StringUtils.isNotBlank(category)) {
            // 如果ES的category字段含有关键该type关键词 则筛选出
            boolQueryBuilder.filter(QueryBuilders
                    .wildcardQuery("category", '*' + category + '*'));
        }
        // 3.商圈
        String business = params.getBusinessDistrict();
        if (StringUtils.isNotBlank(business)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("businessDistrict", business));
        }
        // 4.价格
        Integer bottomPrice = params.getBottomPrice();
        Integer topPrice = params.getTopPrice();
        if (bottomPrice != null && topPrice != null) {
            topPrice = topPrice == 0 ? Integer.MAX_VALUE : topPrice;
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("avgPrice").gte(bottomPrice).lte(topPrice));
        }

        // 2.算分函数查询
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
            boolQueryBuilder, // 原始查询，boolQuery
            new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{ // function数组
                // 根据avgScore和allCommentNum进行加权
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.rangeQuery(AVERAGE_SCORE_FIELD_NAME).gte(4),
                        ScoreFunctionBuilders.weightFactorFunction(10)
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.rangeQuery(AVERAGE_SCORE_FIELD_NAME).gte(3),
                        ScoreFunctionBuilders.weightFactorFunction(5)
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.rangeQuery(ALL_COMMENT_NUM_FIELD_NAME).gte(100),
                        ScoreFunctionBuilders.weightFactorFunction(20)
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.rangeQuery(ALL_COMMENT_NUM_FIELD_NAME).gte(100),
                        ScoreFunctionBuilders.weightFactorFunction(5)
                )
            }
        );
        request.source().query(functionScoreQuery);
    }

    private PageResult handlePageResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 4.1.总条数
        long total = searchHits.getTotalHits().value;
        // 4.2.获取文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        List<FoodDoc> merchants = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            // 4.4.获取source
            String json = hit.getSourceAsString();
            // 4.5.反序列化，非高亮的
            FoodDoc foodDoc = JSON.parseObject(json, FoodDoc.class);
            // 4.6.处理高亮结果
            // 1)获取高亮map
            Map<String, HighlightField> map = hit.getHighlightFields();
            if (map != null && !map.isEmpty()) {
                // 2）根据字段名，获取高亮结果
                HighlightField highlightField = map.get("name");
                if (highlightField != null) {
                    // 3）获取高亮结果字符串数组中的第1个元素
                    String hName = highlightField.getFragments()[0].toString();
                    // 4）把高亮结果放到FoodDoc中
                    foodDoc.setTitle(hName);
                }
            }
            // 4.9.放入集合
            merchants.add(foodDoc);
        }
        return new PageResult(total, merchants);
    }

    private List<FoodDoc> handleSmartSuggestionResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 4.1.获取文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        List<FoodDoc> merchants = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            // 4.4.获取source
            String json = hit.getSourceAsString();
            // 4.5.反序列化，非高亮的
            FoodDoc foodDoc = JSON.parseObject(json, FoodDoc.class);
            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0) {
                foodDoc.setDistance(sortValues[0]);
            }
            // 4.9.放入集合
            merchants.add(foodDoc);
        }
        return merchants;
    }
}
