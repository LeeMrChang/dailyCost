package com.daily.cost.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daily.cost.controller.req.CategorizeQueryReq;
import com.daily.cost.controller.resp.Result;
import com.daily.cost.dto.CategorizeDto;
import com.daily.cost.entity.Categorize;
import com.daily.cost.service.ICategorizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 分类管理控制器
 * </p>
 *
 * @author lichanghao
 * @since 2025-11-14
 */
@Tag(name = "分类管理")
@RestController
@RequestMapping("/api/categorize")
public class CategorizeController {

    @Resource
    private ICategorizeService categorizeService;

    @Operation(summary = "根据ID查询分类")
    @GetMapping("/getById/{id}")
    public Result<CategorizeDto> getById(@PathVariable Long id) {
        Categorize categorize = categorizeService.getById(id);
        if (categorize == null) {
            return Result.fail("分类不存在");
        }
        CategorizeDto dto = BeanUtil.copyProperties(categorize, CategorizeDto.class);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询分类列表")
    @GetMapping("/page")
    public Result<Page<CategorizeDto>> page(@Validated @RequestBody CategorizeQueryReq req) {
        Page<Categorize> page = Page.of(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<Categorize> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(req.getUserId()), Categorize::getUserId, req.getUserId());
        wrapper.eq(Objects.nonNull(req.getType()), Categorize::getType, req.getType());
        wrapper.eq(Objects.nonNull(req.getParentId()), Categorize::getParentId, req.getParentId());
        wrapper.orderByAsc(Categorize::getSortOrder);
        wrapper.orderByDesc(Categorize::getCreateTime);
        Page<Categorize> result = categorizeService.page(page, wrapper);
        Page<CategorizeDto> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(BeanUtil.copyToList(result.getRecords(), CategorizeDto.class));
        return Result.success(dtoPage);
    }

    @Operation(summary = "根据用户ID和类型查询分类列表")
    @GetMapping("/list")
    public Result<List<CategorizeDto>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Short type,
            @RequestParam(required = false) Long parentId) {
        LambdaQueryWrapper<Categorize> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(userId), Categorize::getUserId, userId);
        wrapper.eq(Objects.nonNull(type), Categorize::getType, type);
        wrapper.eq(Objects.nonNull(parentId), Categorize::getParentId, parentId);
        wrapper.orderByAsc(Categorize::getSortOrder);
        List<Categorize> list = categorizeService.list(wrapper);
        List<CategorizeDto> dtoList = BeanUtil.copyToList(list, CategorizeDto.class);
        return Result.success(dtoList);
    }

    @Operation(summary = "新增分类")
    @PostMapping("/save")
    public Result<Void> save(@Validated @RequestBody CategorizeDto dto) {
        Categorize categorize = BeanUtil.copyProperties(dto, Categorize.class);
        boolean success = categorizeService.save(categorize);
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    @Operation(summary = "更新分类")
    @PutMapping("/update")
    public Result<Void> update(@Validated @RequestBody CategorizeDto dto) {
        Long id = dto.getId();
        Categorize existCategorize = categorizeService.getById(id);
        if (existCategorize == null) {
            return Result.fail("分类不存在");
        }
        Categorize categorize = BeanUtil.copyProperties(dto, Categorize.class);
        categorize.setId(id);
        boolean success = categorizeService.updateById(categorize);
        return success ? Result.success("更新成功") : Result.fail("更新失败");
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = categorizeService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }
}

