package com.daily.cost;

import java.util.Arrays;

public class SortTest {

    public static void main(String[] args) {
        int[] array = {5, 2, 4, 6, 1, 3, 7, 2};
        int[] insertSortArray = insertSort(array);
        System.out.println("插入排序：" + Arrays.toString(insertSortArray));
        int[] selectArray = {64, 25, 12, 22, 100, 111, 200, 300, 11, 22, 222};
        int[] selectSortArray = selectSort(selectArray);
        System.out.println("选择排序：" + Arrays.toString(selectSortArray));
        int[] bubblingArray = {46, 52, 22, 22, 1, 11, 20, 31, 111};
        int[] bubblingSortArray = bubblingSort(bubblingArray);
        System.out.println("冒泡排序：" + Arrays.toString(bubblingSortArray));
    }

    /**
     * 插入排序
     * 将数组分为已排序和未排序两部分，每次从未排序部分取出第一个元素，插入到已排序部分的正确位置，就像打扑克牌时整理手牌一样。
     */
    private static int[] insertSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int c = arr[i];
            int n = i - 1;
            while (n >= 0 && arr[n] > c) {
                arr[n + 1] = arr[n];
                n--;
            }
            arr[n + 1] = c;
        }
        return arr;
    }

    /**
     * 选择排序
     * 每次从未排序部分选出最小（或最大）元素，放到已排序部分的末尾。
     */
    private static int[] selectSort(int[] arr) {
        int l = arr.length;
        for (int i = 0; i < l - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < l; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                int temp = arr[minIndex];
                arr[minIndex] = arr[i];
                arr[i] = temp;
            }
        }
        return arr;
    }

    /**
     * 交换排序，冒泡排序
     * 通过相邻元素之间的比较和交换，使较大（或较小）的元素逐渐“浮”到数组的顶端。
     * {46, 52, 22, 22, 1, 11, 20, 31, 111};
     */
    private static int[] bubblingSort(int[] arr) {
        int l = arr.length;
        boolean swap = false;
        for (int i = 0; i < l - 1; i++) {
            for (int j = 0; j < l - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swap = true;
                }
            }
            if (!swap) break;
        }
        return arr;
    }
}
