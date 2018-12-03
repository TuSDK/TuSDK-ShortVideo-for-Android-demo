package org.lasque.tusdkvideodemo.editor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lasque.tusdk.api.video.retriever.TuSDKVideoImageExtractor;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.api.extend.TuSdkMediaPlayerListener;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;
import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.media.codec.suit.TuSdkMediaFilePlayer;
import org.lasque.tusdk.core.media.suit.TuSdkMediaSuit;
import org.lasque.tusdk.core.seles.output.SelesView;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorTranscoder;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorTranscoderImpl;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.JVMUtils;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.tusdkvideodemo.R;
import org.lasque.tusdkvideodemo.ScreenAdapterActivity;
import org.lasque.tusdkvideodemo.album.MovieInfo;
import org.lasque.tusdkvideodemo.views.editor.EditorCutView;
import org.lasque.tusdkvideodemo.views.editor.LineView;

import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/21 14:42
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 编辑-裁剪页面
 * @since v3.0.0
 */
public class MovieEditorCutActivity extends ScreenAdapterActivity {
    private static final String TAG = "MovieEditorCutActivity";
    //底部裁剪的控件
    private EditorCutView mEditorCutView;
    //返回按钮
    private TextView mBackBtn;
    //下一步按钮
    private TextView mNextBtn;
    //播放按钮
    private ImageView mPlayBtn;
    //播放器父视图
    private RelativeLayout mContent;
    //转码进度视图
    private FrameLayout mLoadContent;
    private CircleProgressView mLoadProgress;
    //播放器
    private TuSdkMediaFilePlayer mVideoPlayer;
    //播放视图
    private SelesView mVideoView;
    //视频路径
    private List<MovieInfo> mVideoPaths;
    //编辑转码类
    private TuSdkEditorTranscoder mTransCoder;

    /** 当前剪裁后的持续时间   微秒 **/
    private long mDurationTimeUs;
    /** 左边控件选择的时间     微秒 **/
    private long mLeftTimeRangUs;
    /** 右边控件选择的时间     微秒**/
    private long mRightTimeRangUs;
    /** 最小裁切时间 */
    private long mMinCutTimeUs = 3 * 1000000;

    //播放器回调
    private TuSdkMediaPlayerListener mMediaPlayerListener = new TuSdkMediaPlayerListener() {
        @Override
        public void onStateChanged(int state) {
            if(getTransCoder().getStatus() != TuSdkEditorTranscoder.TuSdkTranscoderStatus.Loading)
                mPlayBtn.setVisibility(state == 1 ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onFrameAvailable() {
            mVideoView.requestRender();
        }

        @Override
        public void onProgress(long playbackTimeUs, TuSdkMediaDataSource mediaDataSource, long totalTimeUs) {
            if(mEditorCutView == null)return;
            float playPercent = (float)playbackTimeUs/(float) totalTimeUs;
            mEditorCutView.setVideoPlayPercent(playPercent);
        }

        @Override
        public void onCompleted(Exception e, TuSdkMediaDataSource mediaDataSource) {
            if(e != null) TLog.e(e);
        }
    };


    /** 转码回调 **/
    private TuSdkEditorTranscoder.TuSdkTranscoderProgressListener mTranscoderListener = new TuSdkEditorTranscoder.TuSdkTranscoderProgressListener() {
        @Override
        public void onProgressChanged(float percentage) {
            mLoadProgress.setValue(percentage * 100);
        }

        @Override
        public void onLoadComplete(TuSDKVideoInfo outputVideoInfo, TuSdkMediaDataSource outputVideoSource) {
            setEnable(true);
            mLoadContent.setVisibility(View.GONE);
            mLoadProgress.setValue(0);
            mPlayBtn.setVisibility(mVideoPlayer.isPause()?View.VISIBLE:View.GONE);
            Intent intent = new Intent(MovieEditorCutActivity.this,MovieEditorActivity.class);
            intent.putExtra("videoPath", outputVideoSource.getPath());
            startActivity(intent);

        }

        @Override
        public void onError(Exception e) {
            mPlayBtn.setVisibility(mVideoPlayer.isPause()?View.VISIBLE:View.GONE);
            TLog.e(e);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_editor_cut);
        initView();
        initPlayer();
    }

    /** 初始化 **/
    private void initView() {
        mVideoPaths = (List<MovieInfo>) getIntent().getSerializableExtra("videoPaths");

        mEditorCutView = findViewById(R.id.lsq_editor_cut);
        mBackBtn = findViewById(R.id.lsq_back);
        mBackBtn.setOnClickListener(mOnClickListener);
        mNextBtn = findViewById(R.id.lsq_next);
        mNextBtn.setOnClickListener(mOnClickListener);
        mPlayBtn = findViewById(R.id.lsq_play_btn);
        mLoadContent = findViewById(R.id.lsq_editor_cut_load);
        mLoadProgress = findViewById(R.id.lsq_editor_cut_load_parogress);

        mEditorCutView.loadView();

        mEditorCutView.setOnPlayPointerChangeListener(new LineView.OnPlayPointerChangeListener() {
            @Override
            public void onPlayPointerPosition(long playPointerPositionTime, float playPointerPositionTimePercent) {
                if(mVideoPlayer != null && mEditorCutView.getLineView().getTouchingState()){
                    mVideoPlayer.seekTo(playPointerPositionTime);
                }
            }
        });
        mEditorCutView.setOnSelectCeoverTimeListener(new LineView.OnSelectTimeChangeListener() {
            @Override
            public void onTimeChange(long startTime, long endTime, long selectTime, float startTimePercent, float endTimePercent, float selectTimePercent) {

            }

            @Override
            public void onLeftTimeChange(long startTime, float startTimePercent) {
                mLeftTimeRangUs = startTime;
                float selectTime = (mRightTimeRangUs - mLeftTimeRangUs) / 1000000.0f;
                mEditorCutView.setRangTime(selectTime);
                if(!mVideoPlayer.isPause())mVideoPlayer.pause();
                mEditorCutView.setVideoPlayPercent(startTimePercent);
                mVideoPlayer.seekTo(startTime);
            }

            @Override
            public void onRightTimeChange(long endTime, float endTimePercent) {
                mRightTimeRangUs = endTime;
                float selectTime = (mRightTimeRangUs - mLeftTimeRangUs) / 1000000.0f;
                mEditorCutView.setRangTime(selectTime);
                if(!mVideoPlayer.isPause())mVideoPlayer.pause();
                mEditorCutView.setVideoPlayPercent(endTimePercent);
                mVideoPlayer.seekTo(endTime);
            }

            @Override
            public void onMaxValue() {

            }

            @Override
            public void onMinValue() {
                Integer minTime = (int) (mMinCutTimeUs / 1000000);
                String tips = String.format(getString(R.string.lsq_min_time_effect_tips), minTime);
                TuSdk.messageHub().showToast(MovieEditorCutActivity.this, tips);
            }
        });

        mContent = findViewById(R.id.lsq_content);
        mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVideoPlayer == null)return;
                if (mVideoPlayer.isPause()) {
                    mVideoPlayer.resume();
                } else {
                    mVideoPlayer.pause();
                }
            }
        });

        mDurationTimeUs = TuSDKMediaUtils.getVideoInfo(mVideoPaths.get(0).getPath()).durationTimeUs;
        float duration = mDurationTimeUs / 1000000.0f;
        mRightTimeRangUs = mDurationTimeUs;
        mEditorCutView.setRangTime(duration);
        mEditorCutView.setTotalTime(mDurationTimeUs);
        mEditorCutView.getLineView().setLeftBarPosition(0f);
        mEditorCutView.getLineView().setRightBarPosition(1f);

        //加载封面图
        loadVideoThumbList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mVideoPlayer == null || mVideoPlayer.isPause()) return;
        mVideoPlayer.pause();

    }

    private void setEnable(boolean enable){
        mEditorCutView.setEnabled(enable);
        mBackBtn.setEnabled(enable);
        mNextBtn.setEnabled(enable);
        mPlayBtn.setEnabled(enable);
        mContent.setEnabled(enable);
    }

    /**
     * 点击事件监听
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.lsq_back:
                    //返回按钮
                    finish();
                    break;
                case R.id.lsq_next:
                    setEnable(false);
                    mVideoPlayer.pause();
                    mPlayBtn.setVisibility(View.GONE);
                    //设置媒体数据源
                    getTransCoder().setVideoDataSource(new TuSdkMediaDataSource(mVideoPaths.get(0).getPath()));
                    //设置裁剪范围
                    getTransCoder().setTimeRange(TuSdkTimeRange.makeTimeUsRange(mLeftTimeRangUs,mRightTimeRangUs));
                    getTransCoder().startTransCoder();
                    mLoadContent.setVisibility(View.VISIBLE);
                    mEditorCutView.setEnable(false);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(mEditorCutView != null)
        mEditorCutView.setEnable(true);
    }

    /** 获取编辑转码器 **/
    private TuSdkEditorTranscoder getTransCoder(){
        if(mTransCoder == null){
            mTransCoder = new TuSdkEditorTranscoderImpl();
            //设置画布裁剪区域()
            mTransCoder.setCanvasRect(new RectF(0,0,1,1));
            //设置图像裁剪区域
            mTransCoder.setCropRect(new RectF(0,0,1,1));
            mTransCoder.addTransCoderProgressListener(mTranscoderListener);
        }
        return mTransCoder;
    }

    /** 初始化播放器 **/
    public void initPlayer(){
        mVideoPlayer = TuSdkMediaSuit.playMedia(new TuSdkMediaDataSource(mVideoPaths.get(0).getPath()),true,mMediaPlayerListener);
        if (mVideoPlayer == null) {
            TLog.e("%s directorPlayer create failed.", TAG);
            return;
        }
        /** 创建预览视图 */
        mVideoView = new SelesView(this);
        mVideoView.setFillMode(SelesView.SelesFillModeType.PreserveAspectRatio);
        mVideoView.setRenderer(mVideoPlayer.getExtenalRenderer());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mContent.addView(mVideoView, 0, params);
        /** Step 3: 连接视图对象 */
        mVideoPlayer.getFilterBridge().addTarget(mVideoView, 0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoPlayer.release();
        JVMUtils.runGC();
    }

    /** 加载视频缩略图 */
    public void loadVideoThumbList() {
        if (mEditorCutView != null) {
            TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56),
                    TuSdkContext.dip2px(56));
            TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();

            extractor.setOutputImageSize(tuSdkSize)
                    .setVideoDataSource(TuSDKMediaDataSource.create(mVideoPaths.get(0).getPath()))
                    .setExtractFrameCount(20);

            extractor.asyncExtractImageList(new TuSDKVideoImageExtractor.TuSDKVideoImageExtractorDelegate() {
                @Override
                public void onVideoImageListDidLoaded(List<Bitmap> images) {
                }

                @Override
                public void onVideoNewImageLoaded(Bitmap bitmap) {
                    mEditorCutView.addBitmap(bitmap);
                    mEditorCutView.setTotalTime(mDurationTimeUs);
                    mEditorCutView.setMinCutTimeUs(mMinCutTimeUs);
                }
            });
        }
    }
}
