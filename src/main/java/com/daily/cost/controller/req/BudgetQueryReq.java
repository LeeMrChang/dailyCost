package com.daily.cost.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "预算查询req")
@EqualsAndHashCode(callSuper = true)
@Data
public class BudgetQueryReq extends PageParam{

    private Long userId;
    private Long ledgerId;
    private String budgetMonth;
}
