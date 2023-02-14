package com.example.meituan.constants;

/**
 * @author : [wangminan]
 * @description : [索引建立语句]
 */
public class FoodIndexConstants {

    private FoodIndexConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String MEITUAN_INDEX_NAME = "meituan";

    public static final String MAPPING_TEMPLATE = """
            {
                      "settings": {
                        "analysis": {
                          "analyzer": {
                            "text_anlyzer": {
                              "tokenizer": "ik_max_word",
                              "filter": "py"
                            },
                            "completion_analyzer": {
                              "tokenizer": "keyword",
                              "filter": "py"
                            }
                          },
                          "filter": {
                            "py": {
                              "type": "pinyin",
                              "keep_full_pinyin": false,
                              "keep_joined_full_pinyin": true,
                              "keep_original": true,
                              "limit_first_letter_length": 16,
                              "remove_duplicated_term": true,
                              "none_chinese_pinyin_tokenize": false
                            }
                          }
                        }
                      },
                      "mappings" : {
                        "properties": {
                          "poiId": {
                            "type": "keyword"
                          },
                          "title": {
                            "type": "text",
                            "analyzer": "text_anlyzer",
                            "search_analyzer": "ik_smart",
                            "copy_to": "all"
                          },
                          "avgPrice": {
                            "type": "integer"
                          },
                          "avgScore": {
                            "type": "long"
                          },
                          "allCommentNum": {
                            "type": "integer"
                          },
                          "category": {
                            "type": "keyword",
                            "copy_to": "all"
                          },
                          "district": {
                            "type": "keyword",
                            "copy_to": "all"
                          },
                          "businessDistrict": {
                            "type": "keyword",
                            "copy_to": "all"
                          },
                          "frontImg": {
                            "type": "keyword",
                            "index": false
                          },
                          "location":{
                            "type": "geo_point"
                          },
                          "all":{
                            "type": "text",
                            "analyzer": "text_anlyzer",
                            "search_analyzer": "ik_smart"
                          },
                          "suggestion":{
                              "type": "completion",
                              "analyzer": "completion_analyzer"
                          }
                        }
                      }
                    }
            """;

}
