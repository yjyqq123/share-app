package com.soft1851.contentcenter.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Table(name = "share")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Share {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC") // strategy 设置使用数据库主键自增策略；generator 设置插入完成后，查询最后生成的 ID 填充到该属性中。
    @ApiModelProperty(name = "id",value = "分享id")
    private Integer id;

    @Column(name = "user_id")
    @ApiModelProperty(name = "userId",value = "分享分id")
    private Integer userId;

    @Column(name = "title")
    @ApiModelProperty(name = "title",value = "分享内容标题")
    private String title;

    @Column(name = "create_time")
    @ApiModelProperty(name = "createTime",value = "创建时间")
    @JsonFormat(locale = "zh",timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column(name = "update_time")
    @ApiModelProperty(name = "updateTime",value = "更新时间")
    @JsonFormat(locale = "zh",timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Column(name = "is_original")
    @ApiModelProperty(name = "isOriginal",value = "是否原创 0：否   1：是")
    private Boolean isOriginal;

    @Column(name = "author")
    @ApiModelProperty(name = "author",value = "资源作者")
    private String author;

    @Column(name = "cover")
    @ApiModelProperty(name = "cover",value = "资源封面URL")
    private String cover;

    @Column(name = "summary")
    @ApiModelProperty(name = "summary",value = "资源摘要")
    private String summary;

    @Column(name = "price")
    @ApiModelProperty(name = "price",value = "下载需要的积分")
    private Integer price;

    @Column(name = "download_url")
    @ApiModelProperty(name = "downloadUrl",value = "下载地址")
    private String downloadUrl;

    @Column(name = "buy_count")
    @ApiModelProperty(name = "buyCount",value = "下载次数")
    private Integer buyCount;

    @Column(name = "show_flag")
    @ApiModelProperty(name = "showFlag",value = "是否显示 0：否 1：是")
    private Boolean showFlag;

    @Column(name = "audit_status")
    @ApiModelProperty(name = "auditStatus",value = "审核状态")
    private String auditStatus;

    @Column(name = "reason")
    @ApiModelProperty(name = "reason",value = "审核不通过原因")
    private String reason;

}
