package com.daily.cost.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "分类查询req")
@EqualsAndHashCode(callSuper = true)
@Data
public class CategorizeQueryReq extends PageParam{

    private Long userId;
    private Short type;
    private Long parentId;
}
