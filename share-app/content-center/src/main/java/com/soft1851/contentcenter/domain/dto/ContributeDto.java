package com.soft1851.contentcenter.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContributeDto {
    /**
     * 投稿人id
     */
    private Integer userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 是否原创
     */
    private Boolean isOriginal;

    /**
     * 作者
     */
    private String author;

    /**
     * 封面
     */
    private String cover;

    /**
     * 内容
     */
    private String summary;

    /**
     * 下载所需积分
     */
    private Integer price;

    /**
     * 下载地址
     */
    private String downloadUrl;
}