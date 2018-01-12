package com.jianhua.reg.base.app;

/**
 * 全局常量管理
 * 这里的存储路径，都存在私有目录的根目录下：
 * context.getExternalFilesDir(null);
 */

public class Constants {

    public static final String APP_NAME = "mops_fci";//文件名

    public static final String LOG_CACHE_NAME = "log_cache";//缓存文件目录
    public static final String LOG_FILE_NAME = "log";//缓存文件名


    public static final String PIC_FILE = "picCacheFile";//采集图片文件
    public static final String VOICE_FILE = "voiceCacheFile";//采集声音文件

    public static final String REG_PIC_FILE = "regist_pic";//注册图片文件
    public static final String REG_VOICE_FILE = "regist_voice";//注册声音文件

    public static final String RESIST_FILE = "no_delect_file";//注册文件
    public static final String TOKEN = "token";
    public static final int ANDROID = 2;//设备id,android=2,IOS=3
    public static final int PREVIEW_WIDTH = 1080;
    public static final int PREVIEW_HEIGHT = 1920;

    /**
     * 相机焦距设置
     */
    public static int maxZoom = 40;
    public static int sCameraZoom = 0;

    public static final String CAMERA_EXPOSURE = "camera_exposure";//相机曝光度
    /**
     * ==================================================================
     * ===============================注册的文本，sp暂存===================================
     * ==================================================================
     */
    //注册成功 需要清除
    public static final String CACHE_IDTYPE = "mops_idtype";//填写的证件类型
    public static final String CACHE_ID = "mops_identify";//填写的证件号
    public static final String CACHE_PS = "mops_password";//填写的密码


}
