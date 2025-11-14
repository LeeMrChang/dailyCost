package com.daily.cost.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daily.cost.controller.req.LoanRecordQueryReq;
import com.daily.cost.controller.resp.Result;
import com.daily.cost.dto.LoanRecordDto;
import com.daily.cost.entity.LoanRecord;
import com.daily.cost.service.ILoanRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 借贷记录管理控制器
 * </p>
 *
 * @author lichanghao
 * @since 2025-11-14
 */
@Tag(name = "借贷记录管理")
@RestController
@RequestMapping("/api/loan-record")
public class LoanRecordController {

    @Resource
    private ILoanRecordService loanRecordService;

    @Operation(summary = "根据ID查询借贷记录")
    @GetMapping("/getById/{id}")
    public Result<LoanRecordDto> getById(@PathVariable Long id) {
        LoanRecord loanRecord = loanRecordService.getById(id);
        if (loanRecord == null) {
            return Result.fail("借贷记录不存在");
        }
        LoanRecordDto dto = BeanUtil.copyProperties(loanRecord, LoanRecordDto.class);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询借贷记录列表")
    @GetMapping("/page")
    public Result<Page<LoanRecordDto>> page(@Validated  @RequestBody LoanRecordQueryReq req) {
        Page<LoanRecord> page = Page.of(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<LoanRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(req.getUserId()), LoanRecord::getUserId, req.getUserId());
        wrapper.eq(Objects.nonNull(req.getType()), LoanRecord::getType, req.getType());
        wrapper.eq(Objects.nonNull(req.getStatus()), LoanRecord::getStatus, req.getStatus());
        wrapper.orderByDesc(LoanRecord::getCreateTime);
        Page<LoanRecord> result = loanRecordService.page(page, wrapper);
        Page<LoanRecordDto> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(BeanUtil.copyToList(result.getRecords(), LoanRecordDto.class));
        return Result.success(dtoPage);
    }

    @Operation(summary = "新增借贷记录")
    @PostMapping("/save")
    public Result<Void> save(@Validated @RequestBody LoanRecordDto dto) {
        LoanRecord loanRecord = BeanUtil.copyProperties(dto, LoanRecord.class);
        boolean success = loanRecordService.save(loanRecord);
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    @Operation(summary = "更新借贷记录")
    @PutMapping("/update")
    public Result<Void> update(@Validated @RequestBody LoanRecordDto dto) {
        Long id = dto.getId();
        LoanRecord existLoanRecord = loanRecordService.getById(id);
        if (existLoanRecord == null) {
            return Result.fail("借贷记录不存在");
        }
        LoanRecord loanRecord = BeanUtil.copyProperties(dto, LoanRecord.class);
        loanRecord.setId(id);
        boolean success = loanRecordService.updateById(loanRecord);
        return success ? Result.success("更新成功") : Result.fail("更新失败");
    }

    @Operation(summary = "删除借贷记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = loanRecordService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }
}

