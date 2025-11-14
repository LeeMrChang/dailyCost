package com.daily.cost.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "记录查询req")
@EqualsAndHashCode(callSuper = true)
@Data
public class AssetSnapshotQueryReq extends PageParam{

    private Long userId;
    private Short type;
    private Short isHidden;
}
