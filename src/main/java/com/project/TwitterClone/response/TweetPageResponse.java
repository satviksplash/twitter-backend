package com.project.TwitterClone.response;

import com.project.TwitterClone.dto.TweetDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TweetPageResponse {
    private List<TweetDto> content;
    private long totalElements;
    private int totalPages;
}