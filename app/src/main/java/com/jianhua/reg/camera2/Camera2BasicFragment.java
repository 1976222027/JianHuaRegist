package com.jianhua.reg.camera2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.jianhua.reg.R;
import com.jianhua.reg.RegistActivity;
import com.jianhua.reg.base.BaseFragment;
import com.jianhua.reg.utils.MLog;
import com.jianhua.reg.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


/**
 * 相机
 * 1、CameraManager
 * Context.getSystemService(CameraManager.class ) 或者Context.getSystemService(Context.CAMERA_SERVICE) 来完成初始化
 * <p>
 * 2、CameraDevice
 * 直接与系统硬件摄像头相联系，
 * 管理（1）摄像头的状态（2）管理CameraCaptureSession（3）管理CaptureRequest
 * <p>
 * 3、CameraCaptureSession
 * 流程控制类 预览 拍照 再预览,等的操作，都是该类控制
 * <p>
 * 4、CameraCharacteristics
 * 描述Cameradevice属性 使用CameraManager通过getCameraCharacteristics（String cameraId）进行查询
 * <p>
 * 5、CameraRequest和CameraRequest.Builder
 * 用于描述捕获图片的各种参数设置，包含捕获硬件（传感器，镜头，闪存），对焦模式、曝光模式，处理流水线，控制算法和输出缓冲区的配置
 * <p>
 * <p>
 * 相机流程图如下：
 * <p>
 * |                             ---------------------                                       ---------------
 * |                            丨 onResume之前的操作 丨                                     丨onResume中操作 丨
 * |                             --------------------                                       ---------------
 * |                                      丨                                                      丨
 * |                                      丨                                                      丨
 * |                        ------------------------------                         -------------------------------
 * |                       丨 onViewCreated中初始化控件    丨                       丨   TextureView.isAvailable()  丨
 * |                       丨onActivityCreated中初始化路径 丨                       丨   判断是初始界面还是锁屏再进入  丨
 * |                       丨  onResume中初始化线程        丨                        -------------------------------
 * |                       ------------------------------                         丨                          丨
 * |                                                                              丨                          丨
 * |                                                                      false初始进入界面                 true 锁屏进入 后台进入
 * |                  调用TextureView.setSurfaceTextureListener(SurfaceTextureListener)监听                调用openCamera()方法
 * |                                                                              丨                          丨
 * |                                                                              丨                          丨
 * |                                 -------------------------------------------------------------------      丨
 * |                                丨   SurfaceTextureListener重写onSurfaceTextureAvailable()方法中    丨      丨
 * |                                丨   调用openCemera()                                              丨      丨
 * |                                 ------------------------------------------------------------------       丨
 * |                                                                              丨                          丨
 * |                                                                              丨                          丨
 * |                                                                              丨                          丨
 * |                                                                              丨                          丨
 * |                                                                              丨                          丨
 * |                          ---------------------------------------------------------------------------------------------------------------
 * |                         丨                                        1.选择前置 后置摄像头操作                                               丨
 * |                         丨setUpCameraOutputs(width, height)方法：2.实例化ImageReader对象，关联监听和线程handler                            丨
 * |                         丨                                       3.根据系统rotation,调整角度，设置isSwapDimension，再用该变量，调整预览宽高值丨
 * |                         丨                                       4.获取摄像头支持的Size,再根据该Size等变量，选择最佳Size                    丨
 * |                         丨 configureTransform() 根据角度 设置正确旋转                                                                     丨
 * |                         丨                                                                                                              丨
 * |                         丨 CameraManager.openCamera(cameraId,CameraDevice.StateCallback,handler);  触发StateCallback回调                 丨                    |
 * |                          ----------------------------------------------------------------------------------------------------------------
 * |                                                                              丨
 * |                                                                              丨
 * |                                                                     StateCallback回调
 * |                                                                              丨
 * |                                                                              丨
 * |                       ---------------------------------------------------------------
 * |                      丨 onOpened() 相机打开成功,设置预览：createCameraPreviewSession(); 丨               -----------------------------------
 * |                      丨 onDisconnected() 未连接相机                                    丨------------->丨 createCameraPreviewSession()方法 丨
 * |                      丨 onError() 相机打开失败                                         丨               -----------------------------------
 * |                       ----------------------------------------------------------------                      丨
 * |                                                                                                             丨
 * |                                                                                                             丨
 * |                            ----------------------------------------------------------------------------------------------------------
 * |                           丨    1CameraDevice管理CamereRequest 或CameraRequestBuilder,获取CameraRequestBuilder实例，绑定Surface        丨
 * |                           丨                                                                                                         丨
 * |                           丨   2 CameraDevice管理CameraCaptureSession，获取CameraCaptureSession实例，                                  丨
 * |                           丨    并在CameraCaptureSession.StateCallback()中的重写方法onConfigured()中，                                 丨
 * |                           丨    使用CameraRequestBuilder设置 flash 曝光率等参数                                                        丨
 * |                           丨                                                                                                         丨
 * |                           丨  3 最后，CameraCaptureSession.setRepeatingRequest(CaptureRequest,CameraCaptureSession.CaptureCallback    丨
 * |                           丨             ,Handler)启动预览。调用CameraCaptureSession.CaptureCallback 回调，处理拍照等操作                丨
 * |                            -----------------------------------------------------------------------------------------------------------
 * |                                                                        丨
 * |                                                            CameraCaptureSession.CaptureCallback 回调
 * |                                                                        丨
 * |                                 ---------------------------------------------------------------------------------------
 * |                                 丨  拍照CameraCaptureSession.capture()还是预览CameraCaptureSession.setRepeatingRequest(),丨
 * |                                 丨 参数中都要处理 CaptureCallback回调，如下，7个重写方法：                                  丨
 * |                                 丨               1：onCaptureProgressed --> process()统一处理                            丨
 * |                                 丨               2：onCaptureCompleted -->  process()统一处理                            丨
 * |                                 丨               3：onCaptureStarted                                                    丨
 * |                                 丨               4：onCaptureFailed                                                     丨
 * |                                 丨               5：onCaptureSequenceCompleted                                          丨
 * |                                 丨               6：onCaptureSequenceAborted                                            丨
 * |                                 丨               7：onCaptureBufferLost                                                 丨
 * |                                 ---------------------------------------------------------------------------------------
 */

public class Camera2BasicFragment extends BaseFragment implements View.OnClickListener {

    //将activity设置成全局
    private Activity activity;
    private ImageView btn;

    //==========================================常量===================================================

    /**
     * 图片旋转方向数组
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final String TAG = "Camera2BasicFragment";

    /**
     * 相机状态：展示相机预览
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * 相机状态： 等待焦点被锁定.
     */
    private static final int STATE_WAITING_LOCK = 1;

    /**
     * 相机状态: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * 相机状态: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * 相机状态:Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1080;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;//1080 / 1920


    //==========================================变量===================================================

    private File mFile;
    /**
     * 相机id
     */
    private String currentCameraId;

    /**
     * 初始化 进入界面固定为前置摄像头
     */
    private int cameraFacing = CameraCharacteristics.LENS_FACING_FRONT;//LENS_FACING_FRONT / LENS_FACING_BACK

    /**
     * 相机预览使用的布局
     */
    private AutoFitTextureView mTextureView;

    //    private SurfaceView mSurfaceView;

    /**
     * 相机流程控制类
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * 相机类
     * 通过CameraDevice.StateCallback监听摄像头的状态
     * 管理CameraCaptureSession：通过方法createCaptureSession(3个参数)或者createReprocessableCaptureSession()
     * 管理CaptureRequest
     */
    private CameraDevice mCameraDevice;
    /**
     * 相机参数类
     */
    private CaptureRequest mPreviewRequest;
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * 相机的当前状态
     */
    private int mState = STATE_PREVIEW;

    /**
     * 相机预览尺寸
     */
    private Size mPreviewSize;


    /**
     * 处理静态图片拍照
     */

    private ImageReader mImageReader;

    /**
     * 并发控制
     * 通常用于限制线程的数量
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * 相机是否支持 flash
     */
    private boolean mFlashSupported;

    /**
     * 相机的方向
     */
    private int mSensorOrientation;

    /**
     * 子线程
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;


    //==========================================含重写的变量（监听等内部类变量）===================================================

    /**
     * This a callback object for the ImageReader. "onImageAvailable" will be called when a still image is ready to be saved.
     * ImageReader类的回调，当拍照完成时，会生成ImageReader类，就会回调此类，一般用于保存操作
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            //异步保存图片
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
        }

    };

    /**
     * 布局视图的监听，用户处理视图生命周期的事件
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    /**
     * mCameraDevice通过CameraDevice.StateCallback监听摄像头的状态
     * 连接摄像头设备时不同状态的操作
     * <p>
     * 摄像头切换的回调
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        //相机打开就执行该方法，用于预览
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            //释放并发
            mCameraOpenCloseLock.release();

            mCameraDevice = cameraDevice;
            //设置预览参数
            createCameraPreviewSession();
        }

        //未连接上硬件相机
        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            //释放并发
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        //相机开启失败
        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            //释放并发
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;

            if (null != activity) {
                activity.finish();
            }
        }
    };


    /**
     * 拍照回调
     * 有多个回调重写，这里只用了2个
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            MLog.d("CameraCaptureSession.CaptureCallback", "onCaptureProgressed");
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            //相机开启后，会一直循环回调该方法，直到有外部给响应
            process(result);
        }

        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
            //相机开启后，会一直循环回调该方法，直到有外部给响应
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            MLog.d("CameraCaptureSession.CaptureCallback", "onCaptureFailed");
        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
            MLog.d("CameraCaptureSession.CaptureCallback", "onCaptureSequenceCompleted");
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
            MLog.d("CameraCaptureSession.CaptureCallback", "onCaptureSequenceAborted");
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
        }

        /**
         * 根据回调 设置result
         *
         * CONTROL_AF_STATE 自动对焦算法的当前状态
         * CONTROL_AE_STATE 自动曝光算法的当前状态
         * CONTROL_AF_STATE_FOCUSED_LOCKED  AF认为它是正确的并且锁定了焦点
         * CONTROL_AF_STATE_NOT_FOCUSED_LOCKED AF未能成功地集中注意力，并且锁定了焦点。
         * CONTROL_AE_STATE_CONVERGED AE对当前场景有很好的控制值。
         * CONTROL_AE_STATE_PRECAPTURE AE被要求做一个预捕获序列，目前正在执行它。
         * CONTROL_AE_STATE_FLASH_REQUIRED AE有一组很好的控制值，但是flash需要被触发，因为它的质量仍然很好。
         *
         * @param result
         */
        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW:
                    break;
                case STATE_WAITING_LOCK: {
                    //某些设备上CONTROL_AF_STATE不存在 通常是前置摄像头不支持
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null || CaptureResult.CONTROL_AF_STATE_INACTIVE == afState) {
                        MLog.d("STATE_WAITING_LOCK", "afState==null");
                        captureStillPicture();

                        //测试 后置相机走该方法
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState || CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        MLog.d("STATE_WAITING_LOCK", "afState(4/5)==" + afState);
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            MLog.d("STATE_WAITING_LOCK--captureStillPicture", "aeState == null|| aeState ==2");
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            MLog.d("STATE_WAITING_LOCK--runPrecaptureSequence", "aeState =" + aeState);
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    MLog.d("CameraCaptureSession.CaptureCallback--STATE_WAITING_PRECAPTURE");

                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE || aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {

                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    MLog.d("CameraCaptureSession.CaptureCallback--STATE_WAITING_NON_PRECAPTURE");

                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }
    };


    //==========================================surface渲染层变量===================================================

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;


    //==========================================frg生命周期 重要方法===================================================

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        final Activity activity = this.activity;
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * @return
     */
    public static Camera2BasicFragment newInstance() {
        return new Camera2BasicFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /**
         * 将getActivity()转成全局变量
         */
        activity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera2, container, false);
    }

    //初始化控件
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        btn = view.findViewById(R.id.img_pic);
        btn.setOnClickListener(this);
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
        surfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);
        //
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFile = new File(activity.getExternalFilesDir(null), "pic.jpg");
        initShadowParams();
    }

    /**
     * 基类方法
     */
    @Override
    protected void lazyLoad() {

    }

    /**
     * 启动camrea的方法在该处触发
     */
    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        /**
         *当屏幕关闭再打开，SurfaceTexture已经存在并可用，onSurfaceTextureAvailable不会被调用。
         * 这时，我们可以打开相机从这里预览，否则，我们需要在监听mSurfaceTextureListener中一直等待surface准备好
         */
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_pic:
                //拍照数据
                //                takePicture();
                //获取数据跳转
                Intent intent = new Intent();
                intent.setClass(getActivity(), RegistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }

    }

    /**
     * 宿主act的控件调用 切换相机
     */
    public void changeFacing() {
        //关闭必要操作
        closeCamera();

        //清空图片缓存

        //操作线程更新
        updataBackgroundThread();

        //切换摄像头
        if (cameraFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            //如果当前为前置，则改为后置
            cameraFacing = CameraCharacteristics.LENS_FACING_BACK;
        } else if (cameraFacing == CameraCharacteristics.LENS_FACING_BACK) {
            //如果当前为后置，则改为前置
            cameraFacing = CameraCharacteristics.LENS_FACING_FRONT;
        } else {
            ToastUtil.ToastShort(activity, "后置多个摄像头，无法选择");
        }

        //打开相机
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    //==========================================初始化===================================================

    /**
     * 打开相机
     */
    private void openCamera(int width, int height) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            MLog.d("之前一个界面已经处理权限，此处不会出现");
            return;
        }
        //打开cameraManger控制类，
        setUpCameraOutputs(width, height);

        //上边完成后，设置textureView对应的旋转操作，必不可少
        configureTransform(width, height);

        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            //如果在给定的等待时间内2.5s，此信号量有可用的许可并且当前线程未被中断，则从此信号量获取一个许可。
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }

            //参数 设置完成后，可正常打开 相机
            manager.openCamera(currentCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * 切换相机的开始操作
     * <p>
     * 设置相机的成员变量
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private void setUpCameraOutputs(int width, int height) {
        //获取相机管理
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            //遍历摄像头列表中的每一个摄像头
            for (String cameraId : manager.getCameraIdList()) {
                //通过相机管理 获取 相机属性、参数
                CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
                //获取摄像头列表的前置 后置id
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);

                /**
                 * 满足前置 或后置，才继续
                 *
                 */
                if (facing != null && facing == cameraFacing) {

                    //获取配置属性
                    StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (map == null) {
                        continue;
                    }

                    /**
                     * 相机拍照，选择最大的Size,同时包含了宽高比 测试结果：宽高比是3:4
                     *  这一部分可以修改，重点修改宽高比
                     */
                    Size largestSize = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());

                    //初始化指定的ImageReader
                    mImageReader = ImageReader.newInstance(largestSize.getWidth(),
                            largestSize.getHeight(),
                            ImageFormat.JPEG,
                            2);

                    //设置回调，拍照的图片用于在回调中保存
                    mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

                    //找出我们是否需要交换尺寸来获得相对于传感器的预览尺寸
                    int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                    //                    MLog.d("根据手机方向获取的角度displayRotation：", displayRotation);

                    mSensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    //                    MLog.d("默认的角度mSensorOrientation：", mSensorOrientation);

                    boolean isSwapDimension = false;//交换尺寸标记
                    //调整角度
                    switch (displayRotation) {
                        case Surface.ROTATION_0:
                        case Surface.ROTATION_180:
                            if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                                isSwapDimension = true;
                            }
                            break;
                        case Surface.ROTATION_90:
                        case Surface.ROTATION_270:
                            if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                                isSwapDimension = true;
                            }
                            break;
                        default:
                            MLog.d("代码无法解决的角度displayRotation=" + displayRotation);
                            break;
                    }

                    //根据旋转角度 做修正
                    Point displaySize = new Point();
                    activity.getWindowManager().getDefaultDisplay().getSize(displaySize);

                    int rotatedPreviewWidth = width;
                    int rotatedPreviewHeight = height;
                    int maxPreviewWidth = displaySize.x;//最大预览尺寸
                    int maxPreviewHeight = displaySize.y;

                    if (isSwapDimension) {
                        rotatedPreviewWidth = height;
                        rotatedPreviewHeight = width;
                        maxPreviewWidth = displaySize.y;//最大预览尺寸
                        maxPreviewHeight = displaySize.x;
                    }

                    //选默认设置的尺寸
                    if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                        maxPreviewWidth = MAX_PREVIEW_WIDTH;
                    }

                    if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                        maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                    }

                    /**
                     *
                     * 尝试使用太大的预览尺寸可能会超过摄像头总线的带宽限制
                     * 测试获取的宽高:600 800
                     *
                     */
                    //                    MLog.d("选择最佳尺寸--", "rotatedPreviewWidth =" + rotatedPreviewWidth,
                    //                            "rotatedPreviewHeight =" + rotatedPreviewHeight,
                    //                            "maxPreviewWidth =" + maxPreviewWidth,
                    //                            "maxPreviewHeight =" + maxPreviewHeight
                    //                            , "largestSize=" + largestSize.getWidth() + "--" + largestSize.getHeight());

                    mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                            rotatedPreviewWidth,
                            rotatedPreviewHeight,
                            maxPreviewWidth,
                            maxPreviewHeight,
                            largestSize);


                    //将选择的预览的Size和视图的比率相适应
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        //横屏
                        mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                    } else {

                        //竖屏
                        mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                    }

                    //是否支持flash
                    Boolean isFlashSupport = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    mFlashSupported = isFlashSupport == null ? false : isFlashSupport;

                    //获取当前的相机id
                    currentCameraId = cameraId;
                    return;
                }
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            showToast(e.toString());
        }
    }

    /**
     * 为TextureView配置必要的旋转
     * <p>
     * 这个方法应该在摄像机预览size确定且TextureView的尺寸修复后被调用
     *
     * @param viewWidth  布局中TextureView的宽`
     * @param viewHeight 布局中TextureView的高
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        if (mTextureView == null || mPreviewSize == null || activity == null) {
            return;
        }
        //获取系统方向
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        //根据最佳筛选，获取的最佳尺寸，创建RectF
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {

            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) viewHeight / mPreviewSize.getHeight(), (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);

        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    /**
     * 关闭相机
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            //释放并发
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * 初始化surafceView
     */
    private void initShadowParams() {
        surfaceView.setZOrderOnTop(true);//顶层
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setKeepScreenOn(true);//常亮
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);//背景设置透明
    }

    /**
     * 创建相机流程控制类
     * <p>
     * 主要通过CameraDevice控制
     */
    private void createCameraPreviewSession() {
        try {

            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;//断言
            //设置图片流的尺寸
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            //输出使用的Surface
            Surface surface = new Surface(texture);

            /**
             * CameraDevice管理参数类 CaptureRequest
             *
             */
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            //此处可以添加ImageReader的surface/TextureView的surface
            mPreviewRequestBuilder.addTarget(surface);
            //            mPreviewRequestBuilder.addTarget(mImageReader.getSurface());

            /**
             * CameraDevice管理的流程类
             * 具体的操作在 CameraCaptureSession.CaputreCallback的回调中处理
             *
             * 第一个参数传入了我们想要绘制的视图列表
             *，第二个参数传入的是建立摄像会话的状态回调函数
             * 第三个参数传入相应的handler处理器
             *  功能：
             *  预览 拍照 再预览，功能流程都需要该类执行
             */

            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        //当摄像机设备完成配置时，这个方法就会被调用，session可以开始处理捕获请求。
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            // 关闭
                            if (null == mCameraDevice) {
                                return;
                            }
                            /** 获取session对象，已经准备好，可以开始预览
                             *  该对象调用capture(...)方法 触发拍照
                             */
                            mCaptureSession = session;

                            //参数类设置特定属性
                            try {
                                //设置自动对焦
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE
                                        , CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                //设置flash
                                if (mFlashSupported) {
                                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                                }

                                mPreviewRequest = mPreviewRequestBuilder.build();

                                //CaptureSession开启预览
                                mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        //如果session不能按照请求配置，则调用此方法，结束onConfigured()的活动状态。
                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            showToast("启动相机失败，CameraCaptureSession的配置失败");
                        }
                    },
                    null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }


    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     *
     */
    private void updataBackgroundThread() {
        stopBackgroundThread();
        startBackgroundThread();

    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    /**
     * 选择相机最佳Size
     * <p>
     * 如果有一个摄像头所支持Size的选择，那么选择最小的一个
     * 至少和纹理视图的Size一样大，最多也就是这个Size
     * 各自的最大Size，以及其方面的比率与指定的值相匹配。如果这样的Size
     * 不存在，选择最大的最大值和最大的最大值，
     * 它的纵横比与指定的值相匹配。
     *
     * @param choices           The list of sizes that the camera supports for the intended output class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();

        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        //筛选符合的Size集合
        for (Size option : choices) {
            //从集合中选择：宽高不超过最大值，且宽高比是3：4
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {

                //宽高都大于布局尺寸，放入bigEnough集合
                if (option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        //选择最大中的最小的Size,如果没有，就选择不是最大中的最大Size
        if (bigEnough.size() > 0) {
            for (int i = 0; i < bigEnough.size(); i++) {
                MLog.d("bigEnough=" + bigEnough.get(i).getWidth() + "--" + bigEnough.get(i).getHeight());
            }
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    /**
     * 启动一个静态图像捕获。
     */
    private void takePicture() {
        lockFocus();
    }

    /**
     * 将焦点锁定为静态图像捕获的第一步。
     */
    private void lockFocus() {
        try {
            //设置自动对焦
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            //告诉回调mCaptureCallback，等待锁定
            mState = STATE_WAITING_LOCK;

            /**
             * 触发拍照功能
             * 每一次请求 产生一个CaptureResult,可以为一个(多个)Surface生成新的帧
             * 之后，CaptureCallback处理拍照
             */

            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取静态照片，当从Capturecallback或者lockFucs中获取响应时，需要调用这个方法
     * TEMPLATE_STILL_CAPTURE 创建一个适合于静态图像捕获的请求。
     */
    private void captureStillPicture() {
        try {
            if (null == activity || mCameraDevice == null) {
                return;
            }

            final CaptureRequest.Builder captrueRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            /**
             * 添加Surface
             *
             * 拍照方法CameraCaptureSession.capture(CaptureRequest request
             * , CameraCaptureSession.CaptureCallback listener
             * , Handler handler）的参数request就与该方法绑定,生成的帧就附到Surface上了
             *
             *  所以此处可以添加多个surface
             */
            captrueRequestBuilder.addTarget(mImageReader.getSurface());//侧了半天bug 少这一行

            //设置自动对焦
            captrueRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE
                    , CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captrueRequestBuilder);

            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

            //设置图片方向
            captrueRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            //相机回调监听
            CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {

                //拍照完成操作
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session
                        , @NonNull CaptureRequest request
                        , @NonNull TotalCaptureResult result) {
                    MLog.d("CameraCaptureSession.CaptureCallback+++++", "onCaptureCompleted=" + mFile.getAbsoluteFile().toString());
                    ToastUtil.ToastShort(activity, "保存路径：" + mFile.getAbsoluteFile().toString());
                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();//取消继续捕捉
            mCaptureSession.abortCaptures();//丢弃当前正在等待和正在进行中的所有捕获，并尽可能快地完成。

            //拍照
            mCaptureSession.capture(captrueRequestBuilder.build(), captureCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * 解锁焦距，拍照完成后执行该方法
     */

    private void unlockFocus() {
        try {
            MLog.d("unlockFocus");
            //重新设置自动对焦的触发
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);

            //设置自动对焦
            setAutoFlash(mPreviewRequestBuilder);

            //拍照
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);

            // 拍照后，重新设置成预览状态
            mState = STATE_PREVIEW;

            //实现预览
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    /**
     * 从指定的屏幕旋转中检索JPEG朝向。
     *
     * @param rotation
     */
    private int getOrientation(int rotation) {
        /**
         *
         *   Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
         We have to take that into account and rotate JPEG properly.
         For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
         For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
         对于大多数设备来说，传感器的定位是90度，对于某些设备来说是270度(例如。Nexus5 x)
         我们必须把它考虑进去，并正确地旋转JPEG。
         对于有90度的设备，我们只是简单地从方向返回我们的映射。
         对于270度的设备，我们需要旋转JPEG 180度。
         */
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;

    }

    /**
     * 运行预捕捉序列 抓捕静态图片，当从lockFoucs()方法获取在mCaptureCallback的响应，应该调用该方法
     */
    private void runPrecaptureSequence() {
        try {
            //重新设置自动对焦的触发
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            //            //设置flash
            //            if (mFlashSupported) {
            //                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
            //                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            //            }
            mState = STATE_WAITING_PRECAPTURE;

            //拍照
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }


    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
