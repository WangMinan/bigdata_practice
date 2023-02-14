package com.example.coverage;

import com.alibaba.fastjson.JSON;
import com.example.meituan.domain.FoodDoc;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

import static com.example.meituan.constants.FoodIndexConstants.MEITUAN_INDEX_NAME;

/**
 * @author : [wangminan]
 * @description : [覆盖率测试]
 */
@SpringBootTest
public class DocumentTest {

    private RestHighLevelClient client;

    private static final String EXPECTED_FOOD_JSON = "FoodDoc(poiId=95437482, frontImg=http://p0.meituan.net/600.600/merchant/dceadfad98ccf41044622e4041e090bf36163.jpg, title=大龙燚火锅（凤城二路店）, category=, district=未央区, businessDistrict=未央路沿线, avgScore=3, allCommentNum=3305, avgPrice=89, suggestion=[未央路沿线, 大龙燚火锅（凤城二路店）], location=34.31851,108.946603, distance=null)";

    @Resource
    private Environment config;

    @BeforeEach
    void setUp() {
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create(Objects.requireNonNull(config.getProperty("var.elasticsearch.host"))))
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        client.close();
    }

    @Test
    void testGetDocumentById() throws IOException {
        // 1.准备Request      // GET /hotel/_doc/{id}
        GetRequest request = new GetRequest(MEITUAN_INDEX_NAME, "95437482");
        // 2.发送请求
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        // 3.解析响应结果
        String json = response.getSourceAsString();

        FoodDoc foodDoc = JSON.parseObject(json, FoodDoc.class);
        Assertions.assertEquals(EXPECTED_FOOD_JSON, foodDoc.toString());
    }
}
