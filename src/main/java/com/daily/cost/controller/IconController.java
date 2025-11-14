package com.daily.cost.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daily.cost.controller.req.IconQueryReq;
import com.daily.cost.controller.resp.Result;
import com.daily.cost.dto.IconDto;
import com.daily.cost.entity.Icon;
import com.daily.cost.service.IIconService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 图标管理控制器
 * </p>
 *
 * @author lichanghao
 * @since 2025-11-14
 */
@Tag(name = "图标管理")
@RestController
@RequestMapping("/api/icon")
public class IconController {

    @Resource
    private IIconService iconService;

    @Operation(summary = "根据ID查询图标")
    @GetMapping("/getById/{id}")
    public Result<IconDto> getById(@PathVariable Long id) {
        Icon icon = iconService.getById(id);
        if (icon == null) {
            return Result.fail("图标不存在");
        }
        IconDto dto = BeanUtil.copyProperties(icon, IconDto.class);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询图标列表")
    @GetMapping("/page")
    public Result<Page<IconDto>> page(@Validated @RequestBody IconQueryReq req) {
        IPage<Icon> page = Page.of(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<Icon> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Icon::getSortOrder);
        IPage<Icon> result = iconService.page(page, wrapper);
        Page<IconDto> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(BeanUtil.copyToList(result.getRecords(), IconDto.class));
        return Result.success(dtoPage);
    }

    @Operation(summary = "查询图标列表")
    @GetMapping("/list")
    public Result<List<IconDto>> list(
            @RequestParam(required = false) Short type,
            @RequestParam(required = false) Long parentId) {
        LambdaQueryWrapper<Icon> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(Icon::getType, type);
        }
        if (parentId != null) {
            wrapper.eq(Icon::getParentId, parentId);
        }
        wrapper.orderByAsc(Icon::getSortOrder);
        List<Icon> list = iconService.list(wrapper);
        List<IconDto> dtoList = BeanUtil.copyToList(list, IconDto.class);
        return Result.success(dtoList);
    }

    @Operation(summary = "新增图标")
    @PostMapping("/save")
    public Result<Void> save(@Validated @RequestBody IconDto dto) {
        Icon icon = BeanUtil.copyProperties(dto, Icon.class);
        boolean success = iconService.save(icon);
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    @Operation(summary = "更新用户")
    @PutMapping("/update")
    public Result<Void> update(@Validated @RequestBody IconDto dto) {
        Long id = dto.getId();
        Icon existIcon = iconService.getById(id);
        if (existIcon == null) {
            return Result.fail("图标不存在");
        }
        Icon icon = BeanUtil.copyProperties(dto, Icon.class);
        icon.setId(id);
        boolean success = iconService.updateById(icon);
        return success ? Result.success("更新成功") : Result.fail("更新失败");
    }
}

