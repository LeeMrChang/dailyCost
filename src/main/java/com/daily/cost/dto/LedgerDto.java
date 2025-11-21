package com.daily.cost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 账本表 DTO
 * </p>
 *
 * @author lichanghao
 * @since 2025-11-14
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "账本表 DTO")
public class LedgerDto {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "用户id(t_user.id)")
    private Long userId;

    @Schema(description = "账本名称")
    private String name;

    @Schema(description = "预算")
    private BigDecimal budget;

    @Schema(description = "分类(1-个人，2-家庭，3-旅行，4-商务)")
    private Short category;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否默认账本：0-否；1-是")
    private Boolean isDefault;
}