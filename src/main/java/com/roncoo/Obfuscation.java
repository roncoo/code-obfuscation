package com.roncoo;

import com.roncoo.utils.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.net.URL;

/**
 * 混淆插件主类
 *
 */
@Mojo(name = "obfuscation", defaultPhase = LifecyclePhase.PACKAGE)
public class Obfuscation extends AbstractMojo {

    /**
     * class文件所在目录
     */
    @Parameter(required = true)
    private String classPath = "";

    /**
     * 忽略混淆的包
     */
    @Parameter
    private String[] ignoreClass;

    /**
     * 水印
     */
    @Parameter
    private String watermark;

    /**
     * 到期时间， 格式： yyyy/MM/dd
     */
    @Parameter
    private String expiry;

    /**
     * 混淆配置文件xml的路径
     */
    @Parameter
    private String configFilePath;

    public void execute() {
        try {
            String configPath = classPath + "/config.xml";
            boolean copyConfigFile = true;
            if(StringUtils.isNotEmpty(configFilePath) && configFilePath.endsWith(".xml")){
                if(new File(configFilePath).exists()){
                    configPath = configFilePath;
                    copyConfigFile = false;
                }
            }

            //复制工具jar包、配置文件到目标目录
            URL url = this.getClass().getResource("");
            JarFileLoader.copyFile(url, classPath, copyConfigFile);

            if(copyConfigFile){
                //指定class类所在路径
                DocumentUtil.setConfigDirPath(configPath, classPath);

                //添加水印或到期时间
                DocumentUtil.addOtherConfig(configPath, watermark, expiry);

                //添加需要忽略的类
                DocumentUtil.addIgnore(configPath, ignoreClass);
            }

            // 创建并运行脚本文件
            ShellExcutor.createAndRunShell(classPath, configPath);

            //删除多余文件，避免项目污染
            FileUtil.delFile(classPath + "/allatori.jar");
            FileUtil.delFile(classPath + "/allatori-annotations.jar");
            if (OSUtil.isMac() || OSUtil.isLinux()) {
                FileUtil.delFile(classPath + "/run.sh");
            } else if (OSUtil.isWindows()) {
                FileUtil.delFile(classPath + "/run.bat");
            }
            if(copyConfigFile){
                FileUtil.delFile(configPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}