package com.daily.cost.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daily.cost.controller.req.TransactionQueryReq;
import com.daily.cost.controller.resp.Result;
import com.daily.cost.dto.TransactionDto;
import com.daily.cost.entity.Transaction;
import com.daily.cost.service.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 账单管理控制器
 * </p>
 *
 * @author lichanghao
 * @since 2025-11-14
 */
@Tag(name = "账单管理")
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Resource
    private ITransactionService transactionService;

    @Operation(summary = "根据ID查询账单")
    @GetMapping("/getById/{id}")
    public Result<TransactionDto> getById(@PathVariable Long id) {
        Transaction transaction = transactionService.getById(id);
        if (transaction == null) {
            return Result.fail("账单不存在");
        }
        TransactionDto dto = BeanUtil.copyProperties(transaction, TransactionDto.class);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询账单列表")
    @GetMapping("/page")
    public Result<Page<TransactionDto>> page(@Validated  @RequestBody TransactionQueryReq req) {
        Page<Transaction> page = Page.of(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(req.getUserId()), Transaction::getUserId, req.getUserId());
        wrapper.eq(Objects.nonNull(req.getLedgerId()), Transaction::getLedgerId, req.getLedgerId());
        wrapper.eq(Objects.nonNull(req.getType()), Transaction::getType, req.getType());
        wrapper.ge(Objects.nonNull(req.getStartTime()), Transaction::getTransactionDate, req.getStartTime());
        wrapper.lt(Objects.nonNull(req.getEndTime()), Transaction::getTransactionDate, req.getEndTime());
        wrapper.orderByDesc(Transaction::getTransactionDate);
        Page<Transaction> result = transactionService.page(page, wrapper);
        Page<TransactionDto> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(BeanUtil.copyToList(result.getRecords(), TransactionDto.class));
        return Result.success(dtoPage);
    }

    @Operation(summary = "新增账单")
    @PostMapping("/save")
    public Result<Void> save(@Validated @RequestBody TransactionDto dto) {
        Transaction transaction = BeanUtil.copyProperties(dto, Transaction.class);
        boolean success = transactionService.save(transaction);
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    @Operation(summary = "更新账单")
    @PutMapping("/update")
    public Result<Void> update(@Validated @RequestBody TransactionDto dto) {
        Long id = dto.getId();
        Transaction existTransaction = transactionService.getById(id);
        if (existTransaction == null) {
            return Result.fail("账单不存在");
        }
        Transaction transaction = BeanUtil.copyProperties(dto, Transaction.class);
        transaction.setId(id);
        boolean success = transactionService.updateById(transaction);
        return success ? Result.success("更新成功") : Result.fail("更新失败");
    }

    @Operation(summary = "删除账单")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = transactionService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }
}

