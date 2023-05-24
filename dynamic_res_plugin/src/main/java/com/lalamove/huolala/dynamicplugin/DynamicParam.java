package com.lalamove.huolala.dynamicplugin;

import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicbase.util.TextUtil;
import com.lalamove.huolala.dynamicplugin.create.IDynamicFileCreate;
import com.lalamove.huolala.dynamicplugin.create.JavaFileCreate;

import org.gradle.api.plugins.ExtraPropertiesExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import groovy.lang.Closure;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicParam
 * @author: huangyuchen
 * @date: 4/14/22
 * @description:
 * @history:
 */
public class DynamicParam {
    /**
     * 是否执行替换System.loadlibrary操作
     */
    private boolean mIsReplaceLoadLibrary;
    /**
     * 是否执行替换System.load操作
     */
    private boolean mIsReplaceLoad;
    /**
     * 是否执行删除so文件操作
     */
    private boolean mIsDeleteSo;
    /**
     * 是否执行将so文件拷贝到其他目录操作
     */
    private boolean mIsCopySo;
    /**
     * 是否执行将动态资源打包，并生成java文件操作
     */
    private boolean mIsZipRes;
    /**
     * 是否执行将so文件打包，并生成java文件操作
     */
    private boolean mIsZipSo;
    /**
     * 是否自动上传所有资源
     */
    private boolean mIsUploadRes;
    /**
     * 是否工作在release模式下
     */
    private boolean mIsReleaseType;
    private boolean mIsDebugLog;
    /**
     * 生成java文件时的包名
     */
    private String mCreateJavaPkgName;

    /**
     * 扫描so文件map
     */
    private Map<String, List<String>> mScanSoFileMap;
    /**
     * 忽略so文件列表
     */
    private List<String> mIgnoreSoFiles;
    /**
     * 扫描system.loadLibrary和System.load方法的包列表
     */
    private List<String> mScanLoadLibraryPkgs;
    /**
     * 忽略load方法的包列表
     */
    private List<String> mIgnoreLoadLibraryPkgs;
    /**
     * 扫描so文件时的abi列表
     */
    private List<String> mScanSoAbis;
    /**
     * 忽略so文件列表起作用时，so压缩包的前缀
     */
    private String mInputSoPrefix;

    /**
     * 系统的第一个so任务
     */
    private String mSoFirstTask;
    /**
     * 系统的第二个so任务
     * 我们的DeleteAndCopySoTask就插入到这2个系统任务之间
     */
    private String mSoSecondTask;
    /**
     * 系统so任务的输出目录
     */
    private String mSoTaskOutputPath;

    /**
     * 插件输出目录
     */
    private String mOutputPath;
    /**
     * 插件so文件输入目录
     */
    private String mInputSo;
    /**
     * 插件字体文件存放目录
     */
    private String mInputTypeface;
    /**
     * 插件帧动画文件存放目录
     */
    private String mInputFrameAnim;
    /**
     * 插件zip包存放目录
     */
    private String mInputZip;
    /**
     * 插件自定义单个文件输入目录
     */
    private String mInputSingle;
    /**
     * 资源动态上传时的方法，在配置文件中定义
     */
    private Closure<String> mUploadClosure;

    /*================以下为本地使用数据==============*/
    /**
     * java文件生成期
     */
    private IDynamicFileCreate mFileCreate;
    /**
     * 该列表存储了所有要生成java文件中常量的动态资源
     */
    private List<DynamicPkgInfo> mPkgs;

    public DynamicParam() {
        mFileCreate = new JavaFileCreate();
        mPkgs = new ArrayList<>();
    }


    public void parse(ExtraPropertiesExtension ext) {
        parseConfig(ext);
        parseSoConfig(ext);
        parseScanSoMap(ext);
        parseLibList(ext);
        parsePath(ext);
        parseTask(ext);
        mUploadClosure = (Closure) ext.get("dynamic_upload");
    }

    private void parseScanSoMap(ExtraPropertiesExtension ext) {
        Object obj = ext.get("dynamic_scan_so_map");
        if (obj instanceof Map) {
            mScanSoFileMap = (Map<String, List<String>>) obj;
        }
    }

    private void parseConfig(ExtraPropertiesExtension ext) {
        Map<String, Object> map = (Map<String, Object>) ext.get("dynamic_config");
        mIsReplaceLoadLibrary = (boolean) map.get("is_replace_load_library");
        mIsReplaceLoad = (boolean) map.get("is_replace_load");
        mIsDeleteSo = (boolean) map.get("is_delete_so");
        mIsCopySo = (boolean) map.get("is_copy_so");
        mIsZipRes = (boolean) map.get("is_zip_res");
        mIsZipSo = (boolean) map.get("is_zip_so");
        mIsUploadRes = (boolean) map.get("is_upload_res");
        mCreateJavaPkgName = (String) map.get("create_java_pkg_name");
        mIsReleaseType = (boolean) map.get("is_release_type");
        mIsDebugLog = (boolean) map.get("is_debug_log");
    }

    private void parseLibList(ExtraPropertiesExtension ext) {
        Map<String, List<String>> map = (Map<String, List<String>>) ext.get("dynamic_lib_list");
        mScanLoadLibraryPkgs = map.get("scan_load_library_pkgs");
        mIgnoreLoadLibraryPkgs = map.get("ignore_load_library_pkgs");
        addIgnoreLoadLibraryPkgs();
    }

    private void addIgnoreLoadLibraryPkgs() {
        if (mIgnoreLoadLibraryPkgs == null) {
            return;
        }
        //将relinker和我们动态资源加载库的包名，放入忽略列表中
        mIgnoreLoadLibraryPkgs.add("com.getkeepsafe.relinker");
        mIgnoreLoadLibraryPkgs.add("com.lalamove.huolala.dynamicbase");
        mIgnoreLoadLibraryPkgs.add("com.lalamove.huolala.dynamicres");
    }


    private void parseSoConfig(ExtraPropertiesExtension ext) {
        Map<String, Object> map = (Map<String, Object>) ext.get("dynamic_so_config");
        mIgnoreSoFiles = (List<String>) map.get("ignore_so_files");
        mScanSoAbis = (List<String>) map.get("scan_so_abis");
        mInputSoPrefix = map.get("so_input_prefix").toString();
    }

    private void parsePath(ExtraPropertiesExtension ext) {
        Map<String, Object> map = (Map<String, Object>) ext.get("dynamic_dir");
        mOutputPath = map.get("output").toString();
        mInputTypeface = map.get("typeface_input").toString();
        mInputFrameAnim = map.get("frame_anim_input").toString();
        mInputSo = map.get("so_input").toString();
        mInputZip = map.get("zip_input").toString();
        mInputSingle = map.get("single_input").toString();
    }

    private void parseTask(ExtraPropertiesExtension ext) {
        Map<String, Object> map = (Map<String, Object>) ext.get("dynamic_task");
        boolean runAfterMerge = (boolean) map.get("isTaskRunAfterMerge");
        String mergeTaskKey = mIsReleaseType ? ("releaseMergeNativeLibs") : ("debugMergeNativeLibs");
        String stripKey = mIsReleaseType ? ("releaseStripDebugSymbols") : ("debugStripDebugSymbols");
        String packageKey = mIsReleaseType ? ("releasePackage") : ("debugPackage");
        String nativeOutputKey = mIsReleaseType ? ("releaseNativeOutputPath") : ("debugNativeOutputPath");
        String stripOutputKey = mIsReleaseType ? ("releaseStripOutputPath") : ("debugStripOutputPath");
        if (runAfterMerge) {
            mSoFirstTask = (String) map.get(mergeTaskKey).toString();
            mSoSecondTask = (String) map.get(stripKey).toString();
            mSoTaskOutputPath = map.get(nativeOutputKey).toString();
        } else {
            mSoFirstTask = (String) map.get(stripKey).toString();
            mSoSecondTask = (String) map.get(packageKey).toString();
            mSoTaskOutputPath = map.get(stripOutputKey).toString();
        }
    }

    public boolean isReplaceLoadLibrary() {
        return mIsReplaceLoadLibrary;
    }

    public boolean isReplaceLoad() {
        return mIsReplaceLoad;
    }

    public boolean isDeleteSo() {
        return mIsDeleteSo;
    }

    public boolean isCopySo() {
        return mIsCopySo;
    }

    public boolean isZipRes() {
        return mIsZipRes;
    }

    public boolean isZipSo() {
        return mIsZipSo;
    }

    public boolean isDebugLog() {
        return mIsDebugLog;
    }

    public List<String> getIgnoreSoFiles() {
        return mIgnoreSoFiles;
    }

    public List<String> getScanLoadLibraryPkgs() {
        return mScanLoadLibraryPkgs;
    }

    public List<String> getIgnoreLoadLibraryPkgs() {
        return mIgnoreLoadLibraryPkgs;
    }

    public List<String> getScanSoAbis() {
        return mScanSoAbis;
    }

    public String getOutputPath() {
        return mOutputPath;
    }

    public String getInputSo() {
        return mInputSo;
    }

    public String getInputTypeface() {
        return mInputTypeface;
    }

    public String getInputFrameAnim() {
        return mInputFrameAnim;
    }

    public String getInputZip() {
        return mInputZip;
    }

    public String getInputSingle() {
        return mInputSingle;
    }

    public String getInputSoPrefix() {
        return mInputSoPrefix;
    }

    public IDynamicFileCreate getFileCreate() {
        return mFileCreate;
    }

    public List<DynamicPkgInfo> getPkgs() {
        return mPkgs;
    }

    public String getSoFirstTask() {
        return mSoFirstTask;
    }

    public String getSoSecondTask() {
        return mSoSecondTask;
    }

    public String getSoTaskOutputPath() {
        return mSoTaskOutputPath;
    }

    public String uploadDynamicPkg(String id, String path) {
        if (mIsUploadRes) {
            return mUploadClosure.call(id, path);
        }
        return "http://url";
    }

    public String getCreateJavaPkgName() {
        return mCreateJavaPkgName;
    }

    public boolean isScanSoMapEmpty() {
        return mScanSoFileMap == null || mScanSoFileMap.isEmpty();
    }

    /**
     *  dynamic_scan_so_map = [
     *             demoSo : ['libnativelib.so'],
     *  ]
     *
     * @param fileName
     * @return
     */
    public String getScanSoPkgName(String fileName) {
        if (TextUtil.isEmpty(fileName) || isScanSoMapEmpty()) {
            return "";
        }
        if (mScanSoFileMap.containsKey(PluginConst.DEBUG_ALL_TEST)) {
            return PluginConst.DEBUG_ALL_TEST;
        }
        for (Map.Entry<String, List<String>> entry : mScanSoFileMap.entrySet()) {
            List<String> list = entry.getValue();
            if (list.contains(fileName)) {
                return entry.getKey();
            }
        }
        return "";
    }


}
