package com.example.meituan.service;

import com.example.meituan.dto.QueryDto;
import com.example.meituan.dto.SmartSuggestionDto;
import com.example.meituan.dto.SuggestionDto;
import com.example.meituan.pojo.R;

public interface SearchService {
    R getSuggestion(SuggestionDto suggestionDto);

    R query(QueryDto queryDto);

    R getSmartSuggestion(SmartSuggestionDto smartSuggestionDto);
}
