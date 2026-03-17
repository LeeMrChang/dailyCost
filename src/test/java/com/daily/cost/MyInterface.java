package com.daily.cost;

@FunctionalInterface
public interface MyInterface {

    void study();

    default void print() {
        System.out.println("MyInterface is studying");
    }
}
