package com.example.meituan;

import com.alibaba.fastjson.JSON;
import com.example.meituan.domain.Food;
import com.example.meituan.domain.FoodDoc;
import com.example.meituan.service.FoodService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 文档操作测试
 */
@SpringBootTest
@Slf4j
class FoodDocumentTest {

    private RestHighLevelClient client;

    @Resource
    private FoodService foodService;

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
    void testAddDocument() throws IOException {
        // 1.查询数据库hotel数据
        Food food = foodService.getById("1000386622");
        // 2.转换为HotelDoc
        FoodDoc foodDoc = new FoodDoc(food);
        // 3.转JSON
        String json = JSON.toJSONString(foodDoc);

        // 1.准备Request
        IndexRequest request = new IndexRequest("meituan").id(foodDoc.getPoiId());
        // 2.准备请求参数DSL，其实就是文档的JSON字符串
        request.source(json, XContentType.JSON);
        // 3.发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetDocumentById() throws IOException {
        // 1.准备Request      // GET /hotel/_doc/{id}
        GetRequest request = new GetRequest("meituan", "95437482");
        // 2.发送请求
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        // 3.解析响应结果
        String json = response.getSourceAsString();

        FoodDoc foodDoc = JSON.parseObject(json, FoodDoc.class);
        System.out.println("foodDoc = " + foodDoc);
    }

    @Test
    void testDeleteDocumentById() throws IOException {
        // 1.准备Request      // DELETE /hotel/_doc/{id}
        DeleteRequest request = new DeleteRequest("meituan", "1000386622");
        // 2.发送请求
        client.delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void testUpdateById() throws IOException {
        // 1.准备Request
        UpdateRequest request = new UpdateRequest("hotel", "61083");
        // 2.准备参数
        request.doc(
                "price", "870"
        );
        // 3.发送请求
        client.update(request, RequestOptions.DEFAULT);
    }
    @Test
    void testBulkRequest() throws IOException {
        // 查询所有的酒店数据
        List<Food> list = foodService.list();

        // 1.准备Request
        BulkRequest request = new BulkRequest();
        // 2.准备参数
        for (Food food : list) {
            // 2.1.转为FoodDoc
            FoodDoc foodDoc = new FoodDoc(food);
            // 2.2.转json
            String json = JSON.toJSONString(foodDoc);
            // 2.3.添加请求
            request.add(new IndexRequest("meituan").id(food.getPoiId()).source(json, XContentType.JSON));
        }

        // 3.发送请求
        client.bulk(request, RequestOptions.DEFAULT);
    }
}
