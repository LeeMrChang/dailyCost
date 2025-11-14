package com.daily.cost.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "账本查询req")
@EqualsAndHashCode(callSuper = true)
@Data
public class LedgerQueryReq extends PageParam{

    private Long userId;
    private String name;
}
