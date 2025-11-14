package com.daily.cost.controller.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

@Data
@Accessors(chain = true)
@Schema(description = "分页参数")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PageParam {

    @NotNull(message = "current不能为空")
    @Min(value = 1, message = "current>=1")
    @Schema(description = "当前页")
    private Integer current;

    @NotNull(message = "size不能为空")
    @Range(min = 1, max = 1000, message = "1<=size<=1000")
    @Schema(description = "每页显示条数")
    private Integer size;

    @Schema(description = "开始时间（yyyy-MM-dd，如果按月份筛选则yyyy-MM-01）")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    @Schema(description = "结束时间（yyyy-MM-dd，如果按月份筛选则yyyy-MM-01）")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;

    @Schema(description = "关键字")
    private String keywords;
}
