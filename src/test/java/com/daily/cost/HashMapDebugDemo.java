package com.daily.cost;


import java.util.HashMap;
import java.util.Map;

public class HashMapDebugDemo {

    static class BadKey {
        private final int value;
        public BadKey(int value) {
            this.value = value;
        }
        @Override
        public int hashCode() {
            return 1; // 固定hashCode，强制冲突
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BadKey that = (BadKey) obj;
            return value == that.value;
        }
        @Override
        public String toString() {
            return "Key" + value;
        }
    }

    public static void main(String[] args) {
        // 初始容量16，负载因子0.75，当size > 12时扩容
        Map<BadKey, String> map = new HashMap<>(16);

        // 添加12个元素，此时不会触发扩容（因为12 <= 12）
        for (int i = 1; i <= 12; i++) {
            map.put(new BadKey(i), "Value" + i);
        }
        System.out.println("After 12 entries, map size: " + map.size());

        // 第13个元素，触发扩容至32
        map.put(new BadKey(13), "Value13");
        System.out.println("After 13 entries (扩容至32), map size: " + map.size());

        // 继续添加直到达到某个阈值，观察链表转红黑树
        // 当桶内链表长度>=8且数组长度>=64时，链表转为红黑树
        // 目前容量32，需要继续添加直到容量变为64
        // 先添加元素到map，直到容量扩容到64
        // 我们需要计算：当size > 32*0.75 = 24时，会触发扩容至64
        for (int i = 14; i <= 25; i++) {
            map.put(new BadKey(i), "Value" + i);
        }
        System.out.println("After 25 entries (即将扩容至64), map size: " + map.size());

        // 第26个元素触发扩容至64
        map.put(new BadKey(26), "Value26");
        System.out.println("After 26 entries (扩容至64), map size: " + map.size());

        // 现在容量64，同一个桶内已经有26个元素（因为所有key都冲突），链表长度26
        // 当链表长度>=8时，会在put时尝试转为红黑树，但需要容量>=64，现在容量64满足，所以应该已经转成红黑树了
        // 实际上在put第9个元素（总size9）时，链表长度达到9，但当时容量可能小于64，所以不会转树，而是继续链表
        // 但我们可以在这里加断点查看内部结构

        // 打印map信息，或者添加断点
        System.out.println("Final map size: " + map.size());
        //map.forEach((badKey, s) -> );



        // 可以通过调试工具查看map的table数组，找到那个桶，观察节点类型是TreeNode还是Node
    }
}
