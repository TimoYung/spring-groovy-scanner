package com.github.timoyung.test.startup;

import com.github.timoyung.test.service.PrintService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("groovy-beans.xml");

        System.out.println(ctx.containsBean("printServiceImpl"));
        PrintService ps = ctx.getBean(PrintService.class);

        Scanner scan = new Scanner(System.in);
        int exit = 0;
        do{

            ps.print("答应PDF文档...");
            System.out.print("输入【0】结束程序：");
            exit = scan.nextInt();

        }while (exit != 0);
   }
}
