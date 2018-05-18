package com.github.timoyung.scanner;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <head><Groovy Bean脚本扫描器/head>
 *  使用脚本扫描器可以省略在配置文件中配置大量的<lang:groovy></lang:groovy>标签
 */
public class GroovyScriptScanner implements ApplicationContextAware {

    /**
     * Groovy脚本基础包名，默认值为groovy
     */
    private String basePackge = "groovy";

    /**
     * 项目源码类路径，默认值：src/main/resources
     */
    private String projectClassPath = "src/main/resources";

    /**
     * 动态脚本代码的后缀名，默认值：.groovy
     */
    private String suffix = ".groovy";

    /**
     * 脚本刷新的频率，默认值：2000ms
     */
    private Long refreshCheckDelay = 2000L;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        // Bean工厂，用于将Bean注册到Spring容器中
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();

        final String refreshCheckDelayClass = "org.springframework.scripting.support.ScriptFactoryPostProcessor.refreshCheckDelay";
        final String languageClass = "org.springframework.scripting.support.ScriptFactoryPostProcessor.language";

        List<File> fileList = this.getScriptFiles();


        fileList.forEach(item ->{

            GenericBeanDefinition bd = new GenericBeanDefinition();
            bd.setBeanClassName("org.springframework.scripting.groovy.GroovyScriptFactory");
            // 刷新时间
            bd.setAttribute(refreshCheckDelayClass, refreshCheckDelay);
            // 语言脚本
            bd.setAttribute(languageClass, "groovy");
            // 文件目录
             bd.getConstructorArgumentValues().addIndexedArgumentValue(0, "file:" + item.getAbsoluteFile());

            //将类名首字母小写
            String fileName = item.getName();
            char s = fileName.charAt(0);
            fileName = fileName.replace(s, new String("" + s).toLowerCase().charAt(0));

            // 注册到spring容器
            beanFactory.registerBeanDefinition(fileName.replace(suffix, ""), bd);
        });

    }

    /**
     * 获取指定基础包下的所有脚本文件
     * @return 返回指定包及其子包下所有指定后缀的文件
     */
    private List<File> getScriptFiles() {
        //将包结构转换成目录结构
        String basePath = basePackge.replace(".", "/");
        String rootPath = this.getProjectPath();

        String scriptBasePath = rootPath + "/" + this.projectClassPath + "/" + basePath;

        File path = new File(scriptBasePath);

        return this.getFile(path);

    }

    /**
     * 获取当前目录下所有指定后缀的文件
     * @param path 当前路径
     * @return 所有指定后缀的文件
     */
    private List<File> getFile(File path){
        //过滤当前路径下虽有指定后缀文件
        Assert.notNull(path, "指定路径不能为空");
        File[] files = path.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(suffix) ? true : false;
            }
        });

        //过滤当前目录下所有直接子目录
        File[] paths = path.listFiles(new FileFilter() {
            @Override
            public boolean accept(File path) {
                return path.isDirectory();
            }
        });

        List<File> fileList = null;
        if(null != files) {
            fileList = new ArrayList<>(Arrays.asList(files));

        }else {
            fileList = new ArrayList<File>();
        }

        //循环递归遍历子目录下的虽有文件
        if(null != paths){
            for (File childPath : paths) {
                List<File> childPathFiles = this.getFile(childPath);
                fileList.addAll(new ArrayList<>(childPathFiles));
            }
        }

        return fileList;
    }

    /**
     * 获取项目的根路径
     * @return
     */
    private String getProjectPath() {
        String rootPath = null;
        try {
            File root = new File("");// 参数为空
            rootPath = root.getCanonicalPath();
        }catch (Exception e){
            e.printStackTrace();
        }

        return rootPath;
    }

    public String getBasePackge() {
        return basePackge;
    }

    public void setBasePackge(String basePackge) {
        this.basePackge = basePackge;
    }

    public String getProjectClassPath() {
        return projectClassPath;
    }

    public void setProjectClassPath(String projectClassPath) {
        this.projectClassPath = projectClassPath;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Long getRefreshCheckDelay() {
        return refreshCheckDelay;
    }

    public void setRefreshCheckDelay(Long refreshCheckDelay) {
        this.refreshCheckDelay = refreshCheckDelay;
    }
}
