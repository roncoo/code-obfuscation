package com.roncoo.utils;

import org.codehaus.plexus.util.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.*;

/**
 * 使用dom4j操作xml
 *
 */
public class DocumentUtil {

    public static void setConfigDirPath(String configPath, String dirPath) throws Exception {
        //读取XML文件，获得document对象
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(configPath));
        document.setXMLEncoding("UTF-8");
        // 获取根节点
        Element root = document.getRootElement();
        Element input = (Element) root.elements("input").get(0);
        Element jar = (Element) input.elements("dir").get(0);
        Attribute in = jar.attribute("in");
        in.setValue(dirPath);
        Attribute out = jar.attribute("out");
        out.setValue(dirPath);
        //格式化输出流，同时指定编码格式。也可以在FileOutputStream中指定。
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(configPath), format);
        writer.write(document);
        writer.close();
    }

    /**
     * 向配置文件中添加需要忽略的类
     *
     * @param configPath    源文件路径
     * @param ignoreClass 需要忽略的类
     * @throws Exception
     */
    public static void addIgnore(String configPath, String[] ignoreClass) throws Exception {
        if(ignoreClass == null || ignoreClass.length == 0){
            return;
        }
        //读取XML文件，获得document对象
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(configPath));
        document.setXMLEncoding("UTF-8");
        // 获取根节点
        Element root = document.getRootElement();
        Element keepNames = (Element) root.elements("keep-names").get(0);
        System.out.println("以下类将被忽略混淆：");
        for (int i = 0; i < ignoreClass.length; i++) {
            System.out.println(ignoreClass[i]);
            keepNames.addElement("class").addAttribute("template", "class " + ignoreClass[i]);
        }
        for (int i = 0; i < keepNames.elements("class").size(); i++) {
            Element ignore = (Element) keepNames.elements("class").get(i);
            ignore.addElement("field").addAttribute("access", "private+");
            ignore.addElement("method").addAttribute("access", "private+");
        }
        //格式化输出流，同时指定编码格式。也可以在FileOutputStream中指定。
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(configPath), format);
        writer.write(document);
        writer.close();
    }

    /**
     * 向配置文件中添加水印或到期时间
     *
     * @param configPath    源文件路径
     * @param watermark 水印
     * @param expiry 到期时间
     * @throws Exception
     */
    public static void addOtherConfig(String configPath, String watermark, String expiry) throws Exception {
        if(StringUtils.isEmpty(watermark) && StringUtils.isEmpty(expiry)){
            return;
        }
        boolean flag = true;
        if(StringUtils.isNotEmpty(expiry)){
            if(!TimeUtil.isValidDate(expiry, "yyyy/MM/dd")){
                System.out.println("---------------到期时间格式错误-------------");
                flag = false;
            }
        }
        if(StringUtils.isEmpty(watermark) && !flag){
            return;
        }
        //读取XML文件，获得document对象
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(configPath));
        document.setXMLEncoding("UTF-8");
        // 获取根节点
        Element root = document.getRootElement();
        if(StringUtils.isNotEmpty(watermark)){
            System.out.println("添加水印：" + watermark);
            root.addElement("watermark").addAttribute("key", "secure-key-to-extract-watermark").addAttribute("value", watermark);
        }
        if(flag && StringUtils.isNotEmpty(expiry)){
            System.out.println("添加到期时间：" + expiry);
            root.addElement("expiry").addAttribute("date", expiry).addAttribute("string", "EXPIRED!");
        }
        //格式化输出流，同时指定编码格式。也可以在FileOutputStream中指定。
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(configPath), format);
        writer.write(document);
        writer.close();
    }

}
