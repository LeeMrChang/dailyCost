package com.daily.cost.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daily.cost.controller.req.BudgetQueryReq;
import com.daily.cost.controller.resp.Result;
import com.daily.cost.dto.BudgetDto;
import com.daily.cost.entity.Budget;
import com.daily.cost.service.IBudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 预算管理控制器
 * </p>
 *
 * @author lichanghao
 * @since 2025-11-14
 */
@Tag(name = "预算管理")
@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Resource
    private IBudgetService budgetService;

    @Operation(summary = "根据ID查询预算")
    @GetMapping("/getById/{id}")
    public Result<BudgetDto> getById(@PathVariable Long id) {
        Budget budget = budgetService.getById(id);
        if (budget == null) {
            return Result.fail("预算不存在");
        }
        BudgetDto dto = BeanUtil.copyProperties(budget, BudgetDto.class);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询预算列表")
    @GetMapping("/page")
    public Result<Page<BudgetDto>> page(@Validated @RequestBody BudgetQueryReq req) {
        Page<Budget> page = Page.of(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<Budget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(req.getUserId()), Budget::getUserId, req.getUserId());
        wrapper.eq(Objects.nonNull(req.getLedgerId()), Budget::getLedgerId, req.getLedgerId());
        wrapper.eq(StringUtils.isNotBlank(req.getBudgetMonth()), Budget::getBudgetMonth, req.getBudgetMonth());
        wrapper.orderByDesc(Budget::getBudgetMonth);
        wrapper.orderByDesc(Budget::getCreateTime);
        Page<Budget> result = budgetService.page(page, wrapper);
        Page<BudgetDto> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(BeanUtil.copyToList(result.getRecords(), BudgetDto.class));
        return Result.success(dtoPage);
    }

    @Operation(summary = "新增预算")
    @PostMapping("/save")
    public Result<Void> save(@Validated @RequestBody BudgetDto dto) {
        Budget budget = BeanUtil.copyProperties(dto, Budget.class);
        boolean success = budgetService.save(budget);
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    @Operation(summary = "更新预算")
    @PutMapping("/update")
    public Result<Void> update(@Validated @RequestBody BudgetDto dto) {
        Long id = dto.getId();
        Budget existBudget = budgetService.getById(id);
        if (existBudget == null) {
            return Result.fail("预算不存在");
        }
        Budget budget = BeanUtil.copyProperties(dto, Budget.class);
        budget.setId(id);
        boolean success = budgetService.updateById(budget);
        return success ? Result.success("更新成功") : Result.fail("更新失败");
    }

    @Operation(summary = "删除预算")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = budgetService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }
}

