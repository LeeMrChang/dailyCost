package com.daily.cost.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户查询req")
@EqualsAndHashCode(callSuper = true)
@Data
public class IconQueryReq extends PageParam{
}
