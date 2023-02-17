package com.example.meituan.service.impl;

import com.example.meituan.exception.SearchException;
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
import java.util.*;

import static com.example.meituan.constants.CategoryConstants.initCategoryMap;
import static com.example.meituan.constants.FoodIndexConstants.MEITUAN_INDEX_NAME;

/**
 * @author : [wangminan]
 * @description : [区划角度的service]
 */
@Service
public class DistrictServiceImpl implements DistrictService {

    private static final String DISTRICT_AGGREGATION_NAME = "DistrictAggression";
    private static final String DISTRICT_FIELD_NAME = "district";
    private static final String RESULT = "result";
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public R getMerchantNumberByDistrict() {
        SearchResponse response =
                getSearchResponse(QueryBuilders.matchAllQuery(),
                        DISTRICT_AGGREGATION_NAME, DISTRICT_FIELD_NAME);
        // 解析聚合结果
        Aggregations aggregations = response.getAggregations();
        Map<String, Integer> aggByDistrict = getDistrictDtoCount(aggregations, DISTRICT_AGGREGATION_NAME);
        return R.ok().put(RESULT, aggByDistrict);
    }

    /**
     * 西安各行政区划餐饮历史评价数量对比
     *
     * @return 西安各行政区划餐饮历史评价数量对比
     */
    @Override
    public R getFlowByDistrict() {
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
                throw new SearchException("查询评价对比失败");
            }
            Aggregations aggregations1 = searchResponse.getAggregations();
            Sum flow = aggregations1.get("flow");
            // 转换 double转int
            result.put(district, (int) flow.getValue());
        }
        return R.ok().put(RESULT, result);
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
        return R.ok().put(RESULT, result);
    }

    /**
     * 获取各行政区划平均价格
     *
     * @return 各行政区划餐饮平均价格
     */
    @Override
    public R getAvgPriceByDistrict() {
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
            SearchResponse searchResponse;
            try {
                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new SearchException("查询价格对比失败");
            }
            Aggregations aggregations1 = searchResponse.getAggregations();
            Sum totalPrice = aggregations1.get("totalPrice");
            avgPriceMap.put(district, (long) (totalPrice.getValue() / entry.getValue()));
        }
        return R.ok().put(RESULT, avgPriceMap);
    }

    @Override
    public R getDistrictMap() {
        Map<String, List<String>> result = new HashMap<>();
        String BusinessDis
                = """                
                东三岔,人民路_文化路,兵马俑,华清宫,华清池,芷阳湖:
                周至县中心城区,周至汽车站,武商购物广场,沙河村:
                万寿路,幸福路沿线,康复路,新城广场,民乐园,立丰国际,胡家庙,解放路_火车站,韩森寨:
                三桥,二府庄,六号大院,北大学城,和平村,城市运动公园,大明宫,大明宫万达,太华路沿线,张家堡,文景路,明光路,未央路沿线,朱宏路,矿山路,红旗厂,西安北站,赛高街区,辛家庙,金花路沿线,雅荷花园,龙首:
                十里铺,城东客运站,御锦城,浐灞半岛_世园会,浐灞生态区,灞业大境,灞桥火车站,田洪正街,田王洪庆,白鹿原,纺织城,长乐坡:
                东关正街,东大街,东门外,互助立交,互助路立交,交大东校区_理工大,何家村_黄雁村,南大街,南院门,和平门_建国门,太乙路,小雁塔_南稍门,广济街,建工路,怡丰城_西荷花园,文艺路,朱雀大街北段,朱雀门,李家村,省体育场,西北大_西工大,钟楼_鼓楼,长乐坊,长安立交:
                丰庆公园,劳动公园,劳动南路,北关,北大街,唐延路北段,土门,大兴新区,大皮院_西羊市,安定门,桃园路,桥梓口,汉城路沿线,玉祥门,甜水井,红庙坡,莲湖公园,西大街,西稍门:
                北环路,向阳路,汤峪镇,蓝田县中心城区:
                人民路,余下镇,娄敬路,草堂路:
                南大学城,秦岭沿线,紫薇田园都市,航天城,郭杜,长安广场,韦曲南站,韦曲西街:
                凤凰广场,前进路,千禧广场,润天大道,蓝天广场:
                三森,南二环东段,南二环西段,吉祥村,含光路南段,大寨路,大雁塔,太白立交,小寨,徐家庄,明德门,曲江新区,朱雀大街南段,杨家村,电视台_会展中心,翠华路,西影路,长安路,雁翔路:
                丈八,光华路,唐延路南段,唐延路沿线,太白南路沿线,枫林绿洲,玫瑰大楼,电子城,科技路沿线,科技路西口,绿地世纪城,西万路口,高新路沿线,高新软件园:
                车城花园,马家湾,高陵县城         
                """;
        String Dis = "临潼区,周至县,新城区,未央区,灞桥区,碑林区,莲湖区,蓝田县,鄠邑区,长安区,阎良区,雁塔区,高新区,高陵区";
        String[] districts = Dis.split(",");
        String[] BusinessDistricts = BusinessDis.split(":");
        for (int i=0;i<districts.length-1;i++) {
            List<String> list = Arrays.asList(BusinessDistricts[i].replaceAll("\n","").split(","));
            result.put(districts[i],list);
        }
        return R.ok().put(RESULT,result);
    }

    private SearchResponse getSearchResponse(QueryBuilder queryBuilder, String aggName, String fieldName) {
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
            throw new SearchException("查询失败");
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
                if (key.contains(key1)) {
                    result.put(key1, result.get(key1) + value);
                }
            }
        }
        return result;
    }
}
