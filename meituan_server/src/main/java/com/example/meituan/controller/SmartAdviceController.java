package com.example.meituan.controller;

import com.example.meituan.dto.SmartSuggestionDto;
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
public class SmartAdviceController {

    @Resource
    private SearchService searchService;

    @PostMapping("/smartSuggestion")
    public R getSmartSuggestion(@RequestBody SmartSuggestionDto smartSuggestionDto) {
        return searchService.getSmartSuggestion(smartSuggestionDto);
    }
}
