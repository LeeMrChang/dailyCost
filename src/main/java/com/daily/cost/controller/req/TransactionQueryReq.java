package com.daily.cost.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "账单查询req")
@EqualsAndHashCode(callSuper = true)
@Data
public class TransactionQueryReq extends PageParam {

    private Long userId;
    private Long ledgerId;
    private Short type;
}
