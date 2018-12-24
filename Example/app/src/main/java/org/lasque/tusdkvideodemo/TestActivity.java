package org.lasque.tusdkvideodemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine;
import org.lasque.tusdk.api.engine.TuSdkFilterEngine;
import org.lasque.tusdk.api.engine.TuSdkFilterEngineImpl;
import org.lasque.tusdk.api.video.retriever.TuSDKVideoImageExtractor;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.api.extend.TuSdkMediaPlayerListener;
import org.lasque.tusdk.core.api.extend.TuSdkMediaProgress;
import org.lasque.tusdk.core.api.extend.TuSdkSurfaceRender;
import org.lasque.tusdk.core.audio.TuSdkAudioRecorder;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;
import org.lasque.tusdk.core.media.camera.TuSdkCameraImpl;
import org.lasque.tusdk.core.media.camera.TuSdkCameraSizeImpl;
import org.lasque.tusdk.core.media.codec.audio.TuSdkAudioInfo;
import org.lasque.tusdk.core.media.codec.audio.TuSdkAudioTrack;
import org.lasque.tusdk.core.media.codec.audio.TuSdkAudioTrackImpl;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFileCuterTimeline;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaTimeSlice;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaTimeline;
import org.lasque.tusdk.core.media.codec.suit.TuSdkMediaFileDirectorPlayerImpl;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoInfo;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoQuality;
import org.lasque.tusdk.core.media.record.TuSdkMediaRecordHub;
import org.lasque.tusdk.core.media.suit.TuSdkCameraSuit;
import org.lasque.tusdk.core.media.suit.TuSdkMediaSuit;
import org.lasque.tusdk.core.seles.output.SelesSmartView;
import org.lasque.tusdk.core.seles.output.SelesView;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.hardware.CameraConfigs;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera;
import org.lasque.tusdk.core.utils.hardware.TuSdkRecorderCameraSetting;
import org.lasque.tusdk.core.utils.hardware.TuSdkRecorderVideoCamera;
import org.lasque.tusdk.core.utils.hardware.TuSdkRecorderVideoCameraImpl;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.utils.image.ImageOrientation;
import org.lasque.tusdk.core.video.TuSDKVideoResult;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.video.editor.TuSdkMediaFilterEffectData;
import org.lasque.tusdkvideodemo.views.editor.playview.TuSdkMovieScrollContent;

import java.io.File;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TestActivity extends Activity {
    private RelativeLayout mContent;
    private TuSdkCameraImpl mTuSdkCamera;
    private SelesSmartView mVideoView;
    private TuSdkMediaRecordHub mRecordHub;
    private Button mPlayBtn, mPauseBtn, mSlowBtn, mFastBtn, mReverseBtn, mAddFilterBtn,mSwitchCamera,mTakePhoto,mFlashMode,mTranscode;
    private TuSdkFilterEngine mFilterEngine;
    private TuSdkRecorderVideoCameraImpl  mRecorderVideoCamera;
    private TuSdkAudioTrack mAudioTrack;

//     /storage/emulated/0/outputTranscode.mp4
    private TuSdkMediaFileDirectorPlayerImpl mVideoPlayer;

    private TuSdkMediaPlayerListener mediaPlayerListener = new TuSdkMediaPlayerListener() {
        @Override
        public void onStateChanged(int state) {

        }

        @Override
        public void onFrameAvailable() {
//            mVideoPlayer.newFrameReadyInGLThread();
            mVideoView.requestRender();
        }

        @Override
        public void onProgress(long playbackTimeUs, TuSdkMediaDataSource mediaDataSource, long totalTimeUs) {
            TLog.e("playbackTimeUs : %s  totalTimeUs : %s",playbackTimeUs,totalTimeUs);
        }

        @Override
        public void onCompleted(Exception e, TuSdkMediaDataSource mediaDataSource) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mContent = findViewById(R.id.lsq_content);

        mVideoPlayer = new TuSdkMediaFileDirectorPlayerImpl();
        /*** Step 1-2: 设置播放文件路径 */
        mVideoPlayer.setMediaDataSource(new TuSdkMediaDataSource("/storage/emulated/0/outputTranscode.mp4"));
        /*** Step 1-3: 设置媒体处理进度接口 */
        mVideoPlayer.setListener(mediaPlayerListener);
        /*** Step 1-4: 加载媒体文件 */
        mVideoPlayer.load(false);
        mVideoPlayer.setProgressOutputMode(1);


        TuSdkMediaTimeline timeline = new TuSdkMediaFileCuterTimeline();
        timeline.append(0,3000000);
        timeline.append(6000000,8000000);
        mVideoPlayer.preview(timeline);


        /** Step 3: 创建预览视图 */
        mVideoView = new SelesSmartView(this);
        /** Step 4 - 1: 内部调用方法 */
        mVideoView.setRenderer(mVideoPlayer.getExtenalRenderer());
        mVideoPlayer.getFilterBridge().addTarget(mVideoView, 0);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mContent.addView(mVideoView, 0, params);

//
        mPlayBtn = this.findViewById(R.id.lsq_record_btn);
        mPauseBtn = this.findViewById(R.id.lsq_pause_btn);
        mSlowBtn = this.findViewById(R.id.lsq_slow_btn);
        mFastBtn = this.findViewById(R.id.lsq_fast_btn);
        mReverseBtn = this.findViewById(R.id.lsq_reverse_btn);
        mAddFilterBtn = this.findViewById(R.id.lsq_addFilter_btn);
        mSwitchCamera = this.findViewById(R.id.lsq_switch_camera);
        mTakePhoto = this.findViewById(R.id.lsq_take_photo);
        mFlashMode = this.findViewById(R.id.lsq_flash_mode);

        View.OnClickListener mClickListener = new TuSdkViewHelper.OnSafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_record_btn:
                        mVideoPlayer.resume();
                        break;
                    case R.id.lsq_pause_btn:
                        mVideoPlayer.pause();
                        break;
                    case R.id.lsq_slow_btn:
    //                        mRecordHub.resume();
                            mRecorderVideoCamera.resumeRecording();
                            break;
                    case R.id.lsq_fast_btn:
    //                        mRecordHub.changePitch(1.2f);

    //                        mRecorderVideoCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Girl);
                            mRecorderVideoCamera.setSpeedMode(TuSdkRecorderVideoCamera.SpeedMode.FAST1);
                            break;
                        case R.id.lsq_reverse_btn:
    //                        mRecordHub.stop();
                            mRecorderVideoCamera.stopRecording();
                            break;
                        case R.id.lsq_addFilter_btn:
    //                        TuSdkMediaFilterEffectData filterEffectData = new TuSdkMediaFilterEffectData("Gold_1");
    //                        mFilterEngine.addMediaEffectData(filterEffectData);
//                            mRecorderVideoCamera.switchFilter("Gold_1");
                            break;
                        case R.id.lsq_switch_camera:
                            //切换摄像头
                            mRecorderVideoCamera.rotateCamera();
                            break;
                        case R.id.lsq_take_photo:
                            mRecorderVideoCamera.captureImage();
    //                        mRecorderVideoCamera.resumeCameraCapture();
                            break;
                        case R.id.lsq_flash_mode:
    //                        TLog.e("canSupportFlash() : %s",mRecorderVideoCamera.canSupportFlash());
    //                        mRecorderVideoCamera.setFlashMode(CameraConfigs.CameraFlash.Always);
                            mRecorderVideoCamera.changeRegionRatio(3f/4f);
                            mRecorderVideoCamera.setRegionRatio(3f/4f);
                            break;
                        default:
                            break;
                    }
                }
            };

            mPlayBtn.setOnClickListener(mClickListener);
            mPauseBtn.setOnClickListener(mClickListener);
            mSlowBtn.setOnClickListener(mClickListener);
            mFastBtn.setOnClickListener(mClickListener);
            mReverseBtn.setOnClickListener(mClickListener);
            mAddFilterBtn.setOnClickListener(mClickListener);
            mSwitchCamera.setOnClickListener(mClickListener);
            mTakePhoto.setOnClickListener(mClickListener);
            mFlashMode.setOnClickListener(mClickListener);



//        findViewById(R.id.lsq_transcode).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                File outputFile = AlbumHelper.getAlbumVideoFile("LSQ_20181217_184236276.mp4");
//                String outputPath = Environment.getExternalStorageDirectory().getPath()+"/outputTranscode.mp4";
//
//                TuSdkMediaDataSource dataSource = new TuSdkMediaDataSource("/storage/emulated/0/DCIM/Camera/LSQ_20181221_143543430.mp4");
//
//
//                TuSdkMediaTimeSlice timeSlice = new TuSdkMediaTimeSlice(0,15*1000000);
//                final long startTimeMs = System.currentTimeMillis();
//                TuSdkMediaSuit.cuter(dataSource,outputPath,
//                        getOutputVideoFormat(),getOutputAudioFormat(),
//                        ImageOrientation.Up,
//                        new RectF(0f, 0f, 1f, 1f), new RectF(0f, 0f, 1f, 1f), timeSlice,
//                        new TuSdkMediaProgress(){
//
//                            @Override
//                            public void onProgress(float progress, TuSdkMediaDataSource mediaDataSource, int index, int total) {
//
//                            }
//
//                            @Override
//                            public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total) {
//                                TLog.e("耗费时间 : %s   %s",(System.currentTimeMillis() - startTimeMs),outputFile);
//                            }
//                        });
//            }
//        });

//        initCamera();
//        initAudioRecord();

    }

//    TuSdkAudioRecorder mAudioRecorder;
//    private void initAudioRecord() {
//
//
//        TuSdkAudioRecorder.TuSdkAudioRecorderSetting setting = new TuSdkAudioRecorder.TuSdkAudioRecorderSetting();
//
//        mAudioRecorder = new TuSdkAudioRecorder(setting, new TuSdkAudioRecorder.TuSdkAudioRecorderListener() {
//            @Override
//            public void onRecordProgress(long durationTimeUS, float percent) {
//                TLog.e("onRecordProgress : %s"+percent);
//            }
//
//            @Override
//            public void onStateChanged(int state) {
//                TLog.e("onStateChanged : %s"+state);
//            }
//
//            @Override
//            public void onRecordError(int code) {
//                TLog.e("onRecordError : %s"+code);
//            }
//        });
//        mAudioRecorder.setMaxRecordTime(10 * 1000000);
//        mAudioRecorder.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Girl);
//    }
//
    private MediaFormat getOutputVideoFormat() {
        MediaFormat videoFormat = TuSdkMediaFormat.buildSafeVideoSurfaceEncodecFormat(
                TuSdkSize.create(1080,1920),
                TuSdkVideoQuality.RECORD_HIGH2, true
        );
        return videoFormat;
    }

    private MediaFormat getOutputAudioFormat() {
        MediaFormat audioFormat = TuSdkMediaFormat.buildSafeAudioEncodecFormat(44100, 2, 96000, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        return audioFormat;
    }

//    private void initCamera() {
//
//        mRecorderVideoCamera = new TuSdkRecorderVideoCameraImpl(this,mContent,new TuSdkRecorderCameraSetting());
//        mRecorderVideoCamera.setRecorderVideoCameraCallback(new TuSdkRecorderVideoCamera.TuSdkRecorderVideoCameraCallback() {
//            @Override
//            public void onMovieRecordComplete(TuSDKVideoResult result) {
//
//            }
//
//            @Override
//            public void onMovieRecordProgressChanged(float progress, float durationTime) {
//                TLog.e("onMovieRecordProgressChanged : %s    ,     %s"  ,progress,durationTime);
//            }
//
//            @Override
//            public void onMovieRecordStateChanged(TuSdkRecorderVideoCamera.RecordState state) {
//                TLog.e("onMovieRecordStateChanged : %s  "  , state);
//            }
//
//            @Override
//            public void onMovieRecordFailed(TuSdkRecorderVideoCamera.RecordError error) {
//
//            }
//        });

//        /** Step 1: 创建相机对象 */
//        mTuSdkCamera = (TuSdkCameraImpl) TuSdkCameraSuit.createCamera();
//
//        // 设置Surface空闲监听
//        mTuSdkCamera.setSurfaceListener(new SurfaceTexture.OnFrameAvailableListener() {
//            @Override
//            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//                mVideoView.requestRender();
//            }
//        });
//
//        mTuSdkCamera.prepare();
//
//        /** Step 2: 创建相机录制 */
//        String saveFile = String.format("atestout_%d_v.mp4", System.currentTimeMillis() / 1000);
//        final File outputFile = new File(Environment.getExternalStorageDirectory(), saveFile);
//        MediaFormat videoFormat = TuSdkMediaFormat.buildSafeVideoSurfaceEncodecFormat(
//                TuSdkSize.create(1080, 1080),
//                TuSdkVideoQuality.RECORD_HIGH2, false
//        );
//
//        MediaFormat audioFormat = TuSdkMediaFormat.buildSafeAudioEncodecFormat(44100, 1, 96000, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
//
//        /** Step 2: 配置相机录制 */
//        mRecordHub = TuSdkCameraSuit.cameraRecorder(videoFormat, audioFormat, mTuSdkCamera,
//                new TuSdkMediaRecordHub.TuSdkMediaRecordHubListener() {
//                    @Override
//                    public void onStatusChanged(TuSdkMediaRecordHub.TuSdkMediaRecordHubStatus status, TuSdkMediaRecordHub hub) {
//                        TLog.d("--onStatusChanged: %s", status);
//                    }
//
//                    @Override
//                    public void onProgress(long elapsedUs, TuSdkMediaDataSource mediaDataSource) {
//                        TLog.d("--onProgress: %d", elapsedUs);
//                    }
//
//                    @Override
//                    public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, TuSdkMediaTimeline timeline) {
//                        TLog.d("--onCompleted: %s, %s   %s", timeline, e ,outputFile);
//                    }
//                });
//
//        /** Step 3: 创建预览视图 */
//        mVideoView = new SelesSmartView(this);
////        mVideoView.setFillMode(SelesView.SelesFillModeType.PreserveAspectRatio);
//        /** Step 4 - 1: 内部调用方法 */
//        mVideoView.setRenderer(new GLSurfaceView.Renderer() {
//            @Override
//            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//                GLES20.glDisable(GLES20.GL_DEPTH_TEST);
//                mRecordHub.initInGLThread();
//                mFilterEngine.onSurfaceCreated();
//            }
//
//            @Override
//            public void onSurfaceChanged(GL10 gl, int width, int height) {
//                GLES20.glViewport(0, 0, width, height);
//                mFilterEngine.onSurfaceChanged(width,height);
//            }
//
//            @Override
//            public void onDrawFrame(GL10 gl) {
//                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//                mRecordHub.newFrameReadyInGLThread();
//            }
//        });
//
//        mRecordHub.setSurfaceRender(new TuSdkSurfaceRender() {
//            @Override
//            public void onSurfaceCreated() {
//                TLog.e("onSurfaceCreated()");
//
//            }
//
//            @Override
//            public void onSurfaceChanged(int width, int height) {
//
//            }
//
//            @Override
//            public void onSurfaceDestory() {
//                mFilterEngine.release();
//            }
//
//            @Override
//            public int onDrawFrame(int textureId, int width, int height, long frameTimeNs) {
//                int texureId =mFilterEngine.processFrame(textureId, width, height, frameTimeNs);
//                return texureId;
//            }
//
//            @Override
//            public void onDrawFrameCompleted() {
//
//            }
//        });
//
//        mVideoView.setDisplayRect(new RectF(0.25f,0.55f,0.75f,0.75f));
//
//
//        mFilterEngine = new TuSdkFilterEngineImpl(false, true);
//        mFilterEngine.setInputImageOrientation(ImageOrientation.Down);
//        mFilterEngine.setOutputImageOrientation(ImageOrientation.Down);
////        mFilterEngine.setCameraFacing(mTuSdkCamera);
//        mFilterEngine.setEnableLiveSticker(true);
//
//
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        mContent.addView(mVideoView, 0, params);
//        mRecordHub.addTarget(mVideoView, 0);
//        mTuSdkCamera.startPreview();

//
//        mPlayBtn = this.findViewById(R.id.lsq_record_btn);
//        mPauseBtn = this.findViewById(R.id.lsq_pause_btn);
//        mSlowBtn = this.findViewById(R.id.lsq_slow_btn);
//        mFastBtn = this.findViewById(R.id.lsq_fast_btn);
//        mReverseBtn = this.findViewById(R.id.lsq_reverse_btn);
//        mAddFilterBtn = this.findViewById(R.id.lsq_addFilter_btn);
//        mSwitchCamera = this.findViewById(R.id.lsq_switch_camera);
//        mTakePhoto = this.findViewById(R.id.lsq_take_photo);
//        mFlashMode = this.findViewById(R.id.lsq_flash_mode);
//
//        View.OnClickListener mClickListener = new TuSdkViewHelper.OnSafeClickListener() {
//            @Override
//            public void onSafeClick(View v) {
//                switch (v.getId()) {
//                    case R.id.lsq_record_btn:
////                        mRecordHub.start(outputFile);
////                        mRecorderVideoCamera.startRecording();
//                        mAudioRecorder.start();
//                        break;
//                    case R.id.lsq_pause_btn:
////                        mRecordHub.pause();
//    //                        mRecorderVideoCamera.pauseRecording();
//                            mAudioRecorder.stop();
//
//                            TLog.e("Output : %s",mAudioRecorder.getOutputFileTemp());
//
//                            break;
//                        case R.id.lsq_slow_btn:
//    //                        mRecordHub.resume();
//                            mRecorderVideoCamera.resumeRecording();
//                            break;
//                        case R.id.lsq_fast_btn:
//    //                        mRecordHub.changePitch(1.2f);
//
//    //                        mRecorderVideoCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Girl);
//                            mRecorderVideoCamera.setSpeedMode(TuSdkRecorderVideoCamera.SpeedMode.FAST1);
//                            break;
//                        case R.id.lsq_reverse_btn:
//    //                        mRecordHub.stop();
//                            mRecorderVideoCamera.stopRecording();
//                            break;
//                        case R.id.lsq_addFilter_btn:
//    //                        TuSdkMediaFilterEffectData filterEffectData = new TuSdkMediaFilterEffectData("Gold_1");
//    //                        mFilterEngine.addMediaEffectData(filterEffectData);
//                            mRecorderVideoCamera.switchFilter("Gold_1");
//                            break;
//                        case R.id.lsq_switch_camera:
//                            //切换摄像头
//                            mRecorderVideoCamera.rotateCamera();
//                            break;
//                        case R.id.lsq_take_photo:
//                            mRecorderVideoCamera.captureImage();
//    //                        mRecorderVideoCamera.resumeCameraCapture();
//                            break;
//                        case R.id.lsq_flash_mode:
//    //                        TLog.e("canSupportFlash() : %s",mRecorderVideoCamera.canSupportFlash());
//    //                        mRecorderVideoCamera.setFlashMode(CameraConfigs.CameraFlash.Always);
//                            mRecorderVideoCamera.changeRegionRatio(3f/4f);
//                            mRecorderVideoCamera.setRegionRatio(3f/4f);
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            };
//
//            mPlayBtn.setOnClickListener(mClickListener);
//            mPauseBtn.setOnClickListener(mClickListener);
//            mSlowBtn.setOnClickListener(mClickListener);
//            mFastBtn.setOnClickListener(mClickListener);
//            mReverseBtn.setOnClickListener(mClickListener);
//            mAddFilterBtn.setOnClickListener(mClickListener);
//            mSwitchCamera.setOnClickListener(mClickListener);
//            mTakePhoto.setOnClickListener(mClickListener);
//            mFlashMode.setOnClickListener(mClickListener);
//        }
}
