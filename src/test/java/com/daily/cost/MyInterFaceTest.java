package com.daily.cost;

import com.daily.cost.entity.Icon;
import com.daily.cost.entity.User;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MyInterFaceTest {


    public static void main(String[] args) {
        //自定义
        MyInterface myInterface  = ()-> System.out.println("测试函数式表达式");
        myInterface.study();
        myInterface.print();
        //消费型接口
        Consumer<User> consumer = (u) -> System.out.println("Hello," + u.getNickname());
        consumer.accept(new User().setNickname("lucky"));
        //供给型接口
        Supplier<Icon> supplier = Icon::new;
        Icon icon = supplier.get();
        icon.setId(656L);
        System.out.println(icon.getId());
        //断定型接口
        Predicate<String> str = (s)-> !s.isEmpty();
        System.out.println(str.test("hello"));
        System.out.println(str.negate().test("hello"));
        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;
        System.out.println(nonNull.test(null));
        System.out.println(isNull.test(null));
        //函数型接口
        Function<String, Integer> toInteger = Integer::valueOf;
        System.out.println(toInteger.apply("123"));
        //Optional对象
        Predicate<String> stringPredicate = Optional.ofNullable(str).get();
        System.out.println(stringPredicate);
    }
}
