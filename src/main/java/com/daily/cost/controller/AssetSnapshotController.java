package com.daily.cost.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daily.cost.controller.req.AssetSnapshotQueryReq;
import com.daily.cost.controller.resp.Result;
import com.daily.cost.dto.AssetSnapshotDto;
import com.daily.cost.entity.AssetSnapshot;
import com.daily.cost.service.IAssetSnapshotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 资产快照管理控制器
 * </p>
 *
 * @author lichanghao
 * @since 2025-11-14
 */
@Tag(name = "资产快照管理")
@RestController
@RequestMapping("/api/asset-snapshot")
public class AssetSnapshotController {

    @Resource
    private IAssetSnapshotService assetSnapshotService;

    @Operation(summary = "根据ID查询资产快照")
    @GetMapping("/getById/{id}")
    public Result<AssetSnapshotDto> getById(@PathVariable Long id) {
        AssetSnapshot assetSnapshot = assetSnapshotService.getById(id);
        if (assetSnapshot == null) {
            return Result.fail("资产快照不存在");
        }
        AssetSnapshotDto dto = BeanUtil.copyProperties(assetSnapshot, AssetSnapshotDto.class);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询资产快照列表")
    @GetMapping("/page")
    public Result<Page<AssetSnapshotDto>> page(@Validated @RequestBody AssetSnapshotQueryReq req) {
        Page<AssetSnapshot> page = Page.of(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<AssetSnapshot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(req.getUserId()), AssetSnapshot::getUserId, req.getUserId());
        wrapper.eq(Objects.nonNull(req.getType()), AssetSnapshot::getType, req.getType());
        wrapper.eq(Objects.nonNull(req.getIsHidden()), AssetSnapshot::getIsHidden, req.getIsHidden());
        wrapper.orderByDesc(AssetSnapshot::getCreateTime);
        Page<AssetSnapshot> result = assetSnapshotService.page(page, wrapper);
        Page<AssetSnapshotDto> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(BeanUtil.copyToList(result.getRecords(), AssetSnapshotDto.class));
        return Result.success(dtoPage);
    }

    @Operation(summary = "新增资产快照")
    @PostMapping("/save")
    public Result<Void> save(@Validated @RequestBody AssetSnapshotDto dto) {
        AssetSnapshot assetSnapshot = BeanUtil.copyProperties(dto, AssetSnapshot.class);
        boolean success = assetSnapshotService.save(assetSnapshot);
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    @Operation(summary = "更新资产快照")
    @PutMapping("/update")
    public Result<Void> update(@Validated @RequestBody AssetSnapshotDto dto) {
        Long id = dto.getId();
        AssetSnapshot existAssetSnapshot = assetSnapshotService.getById(id);
        if (existAssetSnapshot == null) {
            return Result.fail("资产快照不存在");
        }
        AssetSnapshot assetSnapshot = BeanUtil.copyProperties(dto, AssetSnapshot.class);
        assetSnapshot.setId(id);
        boolean success = assetSnapshotService.updateById(assetSnapshot);
        return success ? Result.success("更新成功") : Result.fail("更新失败");
    }

    @Operation(summary = "删除资产快照")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = assetSnapshotService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }
}

