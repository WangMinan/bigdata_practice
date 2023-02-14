package com.example.meituan.controller;

import com.example.meituan.dto.QueryDto;
import com.example.meituan.dto.SuggestionDto;
import com.example.meituan.pojo.R;
import com.example.meituan.service.SearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : [wangminan]
 * @description : [一句话描述该类的功能]
 */
@RestController
public class SearchController {

    @Resource
    private SearchService searchService;

    /**
     * 用户输入时实时获取输入建议
     * @return 用户输入时实时获取输入建议
     */
    @PostMapping("/suggestion")
    public R getSuggestion(@RequestBody SuggestionDto suggestionDto) {
        return searchService.getSuggestion(suggestionDto);
    }

    @PostMapping("/query")
    public R query(@RequestBody QueryDto queryDto) {
        return searchService.query(queryDto);
    }
}
