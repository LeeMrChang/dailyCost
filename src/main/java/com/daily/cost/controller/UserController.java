package com.daily.cost.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daily.cost.controller.req.UserLoginReq;
import com.daily.cost.controller.req.UserQueryReq;
import com.daily.cost.controller.resp.Result;
import com.daily.cost.dto.UserDto;
import com.daily.cost.entity.User;
import com.daily.cost.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * <p>
 * 用户管理控制器
 * </p>
 *
 * @author lichanghao
 * @since 2025-11-14
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/getById/{id}")
    public Result<UserDto> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        UserDto dto = BeanUtil.copyProperties(user, UserDto.class);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public Result<Page<UserDto>> page(@RequestBody @Validated UserQueryReq req) {
        IPage<User> page = Page.of(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(req.getKeywords())) {
            wrapper.or();
            wrapper.like(User::getNickname, req.getKeywords());
            wrapper.like(User::getRealName, req.getKeywords());
            wrapper.like(User::getPhoneNumber, req.getKeywords());
        }
        wrapper.orderByDesc(User::getCreateTime);
        IPage<User> result = userService.page(page, wrapper);
        Page<UserDto> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(BeanUtil.copyToList(result.getRecords(), UserDto.class));
        return Result.success(dtoPage);
    }

    @Operation(summary = "用户登录（支持昵称或手机号）")
    @PostMapping("/login")
    public Result<UserDto> login(@Validated @RequestBody UserLoginReq req) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPassword, req.getPassword())
                .eq(User::getPhoneNumber, req.getPhoneNumber());
        User user = userService.getOne(wrapper, false);
        if (user == null) {
            return Result.fail("账号或密码错误");
        }
        return Result.success(BeanUtil.copyProperties(user, UserDto.class));
    }

    @Operation(summary = "注册用户")
    @PostMapping("/register")
    public Result<UserDto> register(@Validated @RequestBody UserDto dto) {
        User user = BeanUtil.copyProperties(dto, User.class);
        user.setTotalAssets(BigDecimal.TEN);//注册是默认给10资产
        boolean success = userService.save(user);
        return success ? Result.success(BeanUtil.copyProperties(user, UserDto.class)) : Result.fail("注册失败");
    }

    @Operation(summary = "更新用户")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody UserDto dto) {
        Long id = dto.getId();
        User existUser = userService.getById(id);
        if (existUser == null) {
            return Result.fail("用户不存在");
        }
        User user = BeanUtil.copyProperties(dto, User.class);
        user.setId(id);
        boolean success = userService.updateById(user);
        return success ? Result.success("更新成功") : Result.fail("更新失败");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }
}

