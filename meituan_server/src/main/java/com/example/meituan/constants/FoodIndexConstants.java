package com.example.meituan.constants;

/**
 * @author : [wangminan]
 * @description : [一句话描述该类的功能]
 */
public class FoodIndexConstants {
    public static final String MAPPING_TEMPLATE =
            "{\n" +
                    "  \"settings\": {\n" +
                    "    \"analysis\": {\n" +
                    "      \"analyzer\": {\n" +
                    "        \"text_anlyzer\": {\n" +
                    "          \"tokenizer\": \"ik_max_word\",\n" +
                    "          \"filter\": \"py\"\n" +
                    "        },\n" +
                    "        \"completion_analyzer\": {\n" +
                    "          \"tokenizer\": \"keyword\",\n" +
                    "          \"filter\": \"py\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"filter\": {\n" +
                    "        \"py\": {\n" +
                    "          \"type\": \"pinyin\",\n" +
                    "          \"keep_full_pinyin\": false,\n" +
                    "          \"keep_joined_full_pinyin\": true,\n" +
                    "          \"keep_original\": true,\n" +
                    "          \"limit_first_letter_length\": 16,\n" +
                    "          \"remove_duplicated_term\": true,\n" +
                    "          \"none_chinese_pinyin_tokenize\": false\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"mappings\": {\n" +
                    "    \"properties\": {\n" +
                    "      \"poiId\":{\n" +
                    "        \"type\": \"keyword\"\n" +
                    "      },\n" +
                    "      \"title\":{\n" +
                    "        \"type\": \"text\",\n" +
                    "        \"analyzer\": \"text_anlyzer\",\n" +
                    "        \"search_analyzer\": \"ik_smart\",\n" +
                    "        \"copy_to\": \"all\"\n" +
                    "      },\n" +
                    "      \"avgPrice\":{\n" +
                    "        \"type\": \"integer\"\n" +
                    "      },\n" +
                    "      \"avgScore\":{\n" +
                    "        \"type\": \"integer\"\n" +
                    "      },\n" +
                    "      \"avgCommentNum\":{\n" +
                    "        \"type\": \"integer\"\n" +
                    "      },\n" +
                    "      \"brand\":{\n" +
                    "        \"type\": \"keyword\",\n" +
                    "        \"copy_to\": \"all\"\n" +
                    "      },\n" +
                    "      \"category\":{\n" +
                    "        \"type\": \"keyword\",\n" +
                    "        \"copy_to\": \"all\"\n" +
                    "      },\n" +
                    "      \"district\":{\n" +
                    "        \"type\": \"keyword\"\n" +
                    "      },\n" +
                    "      \"businessDistrict\":{\n" +
                    "        \"type\": \"keyword\",\n" +
                    "        \"copy_to\": \"all\"\n" +
                    "      },\n" +
                    "      \"frontImg\":{\n" +
                    "        \"type\": \"keyword\",\n" +
                    "        \"index\": false\n" +
                    "      },\n" +
                    "      \"all\":{\n" +
                    "        \"type\": \"text\",\n" +
                    "        \"analyzer\": \"text_anlyzer\",\n" +
                    "        \"search_analyzer\": \"ik_smart\"\n" +
                    "      },\n" +
                    "      \"suggestion\":{\n" +
                    "          \"type\": \"completion\",\n" +
                    "          \"analyzer\": \"completion_analyzer\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
}
