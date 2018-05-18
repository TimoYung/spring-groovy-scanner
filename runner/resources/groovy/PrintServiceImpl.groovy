package com.github.timoyung.test.service.impl

import com.github.timoyung.test.service.PrintService;

public class PrintServiceImpl implements PrintService {

    @Override
    void print(String msg) {
        System.out.println("正在打印：" + msg);
        //System.out.println("注释块已经被打开。。。。。")
    }
}