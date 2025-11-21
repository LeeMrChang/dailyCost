package com.daily.cost.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daily.cost.controller.req.LedgerQueryReq;
import com.daily.cost.controller.resp.Result;
import com.daily.cost.dto.LedgerDto;
import com.daily.cost.entity.Ledger;
import com.daily.cost.service.ILedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 账本管理控制器
 * </p>
 *
 * @author lichanghao
 * @since 2025-11-14
 */
@Tag(name = "账本管理")
@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    @Resource
    private ILedgerService ledgerService;

    @Operation(summary = "根据ID查询账本")
    @GetMapping("/getById/{id}")
    public Result<LedgerDto> getById(@PathVariable Long id) {
        Ledger ledger = ledgerService.getById(id);
        if (ledger == null) {
            return Result.fail("账本不存在");
        }
        LedgerDto dto = BeanUtil.copyProperties(ledger, LedgerDto.class);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询账本列表")
    @PostMapping("/page")
    public Result<Page<LedgerDto>> page(@Validated  @RequestBody LedgerQueryReq req) {
        Page<Ledger> page = Page.of(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<Ledger> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(req.getUserId()), Ledger::getUserId, req.getUserId());
        wrapper.eq(StringUtils.isNotBlank(req.getName()), Ledger::getName, req.getName());
        wrapper.orderByDesc(Ledger::getCreateTime);
        Page<Ledger> result = ledgerService.page(page, wrapper);
        Page<LedgerDto> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(BeanUtil.copyToList(result.getRecords(), LedgerDto.class));
        return Result.success(dtoPage);
    }

    @Operation(summary = "根据用户ID查询账本列表")
    @GetMapping("/user/{userId}")
    public Result<Page<LedgerDto>> listByUserId(@PathVariable Long userId) {
        LambdaQueryWrapper<Ledger> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ledger::getUserId, userId);
        wrapper.orderByDesc(Ledger::getIsDefault);
        wrapper.orderByDesc(Ledger::getCreateTime);
        Page<Ledger> result = ledgerService.page(new Page<>(1, 100), wrapper);
        Page<LedgerDto> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(BeanUtil.copyToList(result.getRecords(), LedgerDto.class));
        return Result.success(dtoPage);
    }

    @Operation(summary = "新增账本")
    @PostMapping("/save")
    public Result<Void> save(@Validated @RequestBody LedgerDto dto) {
        Ledger ledger = BeanUtil.copyProperties(dto, Ledger.class);
        boolean success = ledgerService.save(ledger);
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    @Operation(summary = "更新账本")
    @PutMapping("/update")
    public Result<Void> update(@Validated @RequestBody LedgerDto dto) {
        Long id = dto.getId();
        Ledger existLedger = ledgerService.getById(id);
        if (existLedger == null) {
            return Result.fail("账本不存在");
        }
        Ledger ledger = BeanUtil.copyProperties(dto, Ledger.class);
        ledger.setId(id);
        boolean success = ledgerService.updateById(ledger);
        return success ? Result.success("更新成功") : Result.fail("更新失败");
    }

    @Operation(summary = "删除账本")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = ledgerService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }
}

