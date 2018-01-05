/**
 * TuSDKVideoDemo
 * MovieEditorActivity.java
 *
 * @author  Yanlin
 * @Date  Feb 21, 2017 8:52:11 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo.component;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.audio.TuSDKAudioFileRecorder;
import org.lasque.tusdk.core.audio.TuSDKAudioFileRecorder.OutputFormat;
import org.lasque.tusdk.core.audio.TuSDKAudioFileRecorder.RecordError;
import org.lasque.tusdk.core.audio.TuSDKAudioFileRecorder.RecordState;
import org.lasque.tusdk.core.audio.TuSDKAudioFileRecorder.TuSDKRecordAudioDelegate;
import org.lasque.tusdk.core.seles.SelesParameters;
import org.lasque.tusdk.core.seles.SelesParameters.FilterArg;
import org.lasque.tusdk.core.seles.tusdk.FilterWrap;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.FileHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.TuSdkWaterMarkOption.WaterMarkPosition;
import org.lasque.tusdk.core.utils.image.BitmapHelper;
import org.lasque.tusdk.core.video.TuSDKVideoResult;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView.TuSdkTableViewItemClickDelegate;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import org.lasque.tusdk.video.editor.TuSDKMediaAudioEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaStickerAudioEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaStickerEffectData;
import org.lasque.tusdk.video.editor.TuSDKMovieEditor;
import org.lasque.tusdk.video.editor.TuSDKMovieEditor.TuSDKMovieEditorDelegate;
import org.lasque.tusdk.video.editor.TuSDKMovieEditor.TuSDKMovieEditorSoundStatus;
import org.lasque.tusdk.video.editor.TuSDKMovieEditor.TuSDKMovieEditorStatus;
import org.lasque.tusdk.video.editor.TuSDKMovieEditorOptions;
import org.lasque.tusdk.video.editor.TuSDKTimeRange;
import org.lasque.tusdk.video.editor.TuSDKVideoImageExtractor;
import org.lasque.tusdk.video.editor.TuSDKVideoImageExtractor.TuSDKVideoImageExtractorDelegate;
import org.lasque.tusdk.video.mixer.TuSDKMediaDataSource;
import org.lasque.tusdkvideodemo.R;
import org.lasque.tusdkvideodemo.SimpleCameraActivity;
import org.lasque.tusdkvideodemo.utils.AudioTimingRunnable;
import org.lasque.tusdkvideodemo.utils.Constants;
import org.lasque.tusdkvideodemo.views.CompoundConfigView;
import org.lasque.tusdkvideodemo.views.CompoundDrawableTextView;
import org.lasque.tusdkvideodemo.views.ConfigViewParams;
import org.lasque.tusdkvideodemo.views.ConfigViewParams.ConfigViewArg;
import org.lasque.tusdkvideodemo.views.ConfigViewSeekBar;
import org.lasque.tusdkvideodemo.views.FilterCellView;
import org.lasque.tusdkvideodemo.views.FilterConfigSeekbar;
import org.lasque.tusdkvideodemo.views.FilterConfigView;
import org.lasque.tusdkvideodemo.views.FilterConfigView.FilterConfigViewSeekBarDelegate;
import org.lasque.tusdkvideodemo.views.FilterListView;
import org.lasque.tusdkvideodemo.views.StickerAudioEffectCellView;
import org.lasque.tusdkvideodemo.views.StickerAudioEffectListView;
import org.lasque.tusdkvideodemo.views.AudioEffectCellView;
import org.lasque.tusdkvideodemo.views.AudioEffectListView;
import org.lasque.tusdkvideodemo.views.ScenceEffectLayout;
import org.lasque.tusdkvideodemo.views.MovieRangeSelectionBar;
import org.lasque.tusdkvideodemo.views.MovieRangeSelectionBar.OnCursorChangeListener;
import org.lasque.tusdkvideodemo.views.MovieRangeSelectionBar.Type;
import org.lasque.tusdkvideodemo.views.SceneEffectListView;
import org.lasque.tusdkvideodemo.views.SceneEffectsTimelineView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频编辑示例
 * 
 * 功能：
 * 1. 预览视频，添加滤镜查看效果
 * 2. 导出新的视频
 * 
 * @author Yanlin
 */
public class MovieEditorActivity extends SimpleCameraActivity 
{
	/** 视频路径 */
	protected String mVideoPath;
	/** 视频裁切区域 */
	private TuSDKTimeRange mCutTimeRange;
	/** 存储MV数据对象 */
	private TuSDKMediaEffectData mStickerAudioEffectData;

	private TuSDKMediaAudioEffectData	mAudioEffectData;
	/** MV特效时间 */
	private TuSDKTimeRange mMVStickerMusicTimeRange ;

	/** 音频文件录制实例 */
	private TuSDKAudioFileRecorder mAudioRecorder;


	/** 保存按钮 */
	protected TextView mSaveButton;
	/** 标题  TextView */
	private TextView mTitleTextView;
	/** 返回按钮 */
	protected TextView mBackTextView;	
	/** 编辑器 */
	private TuSDKMovieEditor mMovieEditor;
    /** 开始播放按钮 */
    protected Button mActionButton;
    /** 滤镜栏 */
	private FilterListView	mFilterListView;
	/** 滤镜参数调节栏 */
    private FilterConfigView  mConfigView;
	/** 贴纸栏视图 */
	protected StickerAudioEffectListView mMvListView;

    private TuSdkTextButton mFilterBtn;
	
	// 记录当前滤镜
    private FilterWrap mSelesOutInput;
    
	// 记录上一个选中的滤镜
	private FilterCellView lastSelectedCellView;

    private TuSdkTextButton mMvBtn;
    
    private LinearLayout mFilterWrap;


	/** 视频裁剪控件 */
	private MovieRangeSelectionBar mRangeSelectionBar;
	
	/** 记录缩略图列表容器 */
	private List<Bitmap> mVideoThumbList;
	/** 用于记录当前调节栏磨皮系数 */
	private float mSmoothingProgress = -1.0f;
	/** 用于记录当前调节栏效果系数 */
	private float mMixiedProgress = -1.0f;

	/** 记录是否是首次进入 */
	private boolean mIsFirstEntry = true;
	/** 用于记录焦点位置 */
	private int mFocusPostion = 1;
	


    /** 保存上一个MV特效*/	
	private StickerAudioEffectCellView mLastMvView;
	 /** 保存上一次点击MV列表的时间*/	
	private long lastClickTime;
	
	/** 配音按钮 */
	private CompoundDrawableTextView mDubbingButton;
	
	/**  配音功能布局 */
	private LinearLayout mDubbingLayoutWrap;
	
	/** 最小时间,单位s*/
	private int minAudioRecordTime = 1;
	
	/** 歌曲列表 */
	private AudioEffectListView mMixingListView;
	
	/** 录音界面 */
	private RelativeLayout mAudioRecordingLayout;
	
	/** 录音界面关闭按钮 */
	private TuSdkImageView mAudioRecordCloseButton;
	
	/** 录音录制按钮 */
	private TuSdkImageView mAudioRecordButton;
	
	/** 录音保存按钮 */
	private TuSdkImageView mAudioRecordSaveButton;
	
	/** 录音取消按钮 */
	private TuSdkImageView mAudioRecordCancelButton;
	
	/** 录音进度条 */
	private ProgressBar mAudioRecordProgressBar;
	
	/** 录音计时线程 */
	private AudioTimingRunnable mAudioTimingRunnable;
	
	/** 保存的录音文件 */
	private File mAudioFile;

	/** 录音开始时间 */
	private long mAudioRecordStartTime;
	
	/** 声音强度调节栏 */
	private CompoundConfigView mVoiceConfigView;
	
	/** 上一次选中的背景音效 */
	private AudioEffectCellView mLastMixingCellView;
	
	/** 录音界面剩余时间文本 */
	private TextView mAudioTimeRemainingText;
	
	/** 记录开启录音的次数 */
	private int mStartAudioTimes = 0;

	/** 场景特效底部按钮 */
	private TuSdkTextButton mScenceEffectBtn;

	// 场景特效布局文件
	private ScenceEffectLayout mScenceEffectLayout;

	
	/** MV音效资源  */
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Integer> mMusicMap = new HashMap<Integer, Integer>();

    protected int getLayoutId()
    {
    	return R.layout.movie_editor_activity;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(getLayoutId());
		
		mSaveButton = findViewById(R.id.lsq_next);
		mSaveButton.setText(R.string.lsq_save);
		mSaveButton.setOnClickListener(mButtonSafeClickListener);
		mSaveButton.setEnabled(false);
		
		mTitleTextView = this.findViewById(R.id.lsq_title);
		mTitleTextView.setText(R.string.lsq_add_filter);
        mActionButton = findViewById(R.id.lsq_actButton);
        mActionButton.setVisibility(View.INVISIBLE);
        mBackTextView = findViewById(R.id.lsq_back);
        mBackTextView.setOnClickListener(mButtonClickListener);
        
        mFilterBtn= findViewById(R.id.lsq_filterWrap);
        mFilterBtn.setOnClickListener(mButtonClickListener);


        mMvBtn= findViewById(R.id.lsq_mvWrap);
        mMvBtn.setOnClickListener(mButtonClickListener);

        mRangeSelectionBar =  this.findViewById(R.id.lsq_rangeseekbar);
        mRangeSelectionBar.setShowPlayCursor(false);
        mRangeSelectionBar.setType(Type.MV);
        mRangeSelectionBar.setLeftSelection(0);
        mRangeSelectionBar.setPlaySelection(0);
        mRangeSelectionBar.setRightSelection(100);
        mRangeSelectionBar.setOnCursorChangeListener(mOnCursorChangeListener);

		hideNavigationBar();


		mFilterWrap=(LinearLayout) findViewById(R.id.lsq_filter_layout);
        mFilterWrap.setVisibility(View.GONE);


        getFilterMVLayout().setBackgroundColor(TuSdkContext.getColor("lsq_color_white"));
        getBottomNavigationLayout().setBackgroundColor(TuSdkContext.getColor("lsq_color_white"));
        getMixingListViewWrap().setBackgroundColor(TuSdkContext.getColor("lsq_color_white"));
        initFilterListView();
        initMvListView();
        
        setCameraViewSize(TuSdkContext.getScreenSize().width,TuSdkContext.getScreenSize().width);

        Intent intent = getIntent();
		mVideoPath = intent.getStringExtra("videoPath");
		
		// 视频裁切区域时间
		mCutTimeRange = TuSDKTimeRange.makeRange(intent.getFloatExtra("startTime", 0) / (float)1000, intent.getFloatExtra("endTime", 0) / (float)1000);
		// 如果没有传递开始和结束时间，默认视频编辑时长为总时长
		if(mCutTimeRange.duration() == 0 && mVideoPath != null)
		{
			mCutTimeRange = TuSDKTimeRange.makeRange(0, getVideoDuration(mVideoPath));
		}
		
		// MV播放时间 
		mMVStickerMusicTimeRange = TuSDKTimeRange.makeRange(0.f, mCutTimeRange.duration() );

		// 加载场景特效视图
		loadScenceEffectLayout();

		// 加载视频缩略图 
        loadVideoThumbList();
         
        // 初始化编辑器
		initMovieEditor();
		// 设置弹窗提示是否在隐藏虚拟键的情况下使用
		TuSdk.messageHub().applyToViewWithNavigationBarHidden(true);
		
		initDubbingView();

	    toggleFilterMode();
	}

	/**
	 * 初始化 TuSDKMovieEditor
	 */
	protected void initMovieEditor()
	{
		mMovieEditor = new TuSDKMovieEditor(this.getBaseContext(), getCameraView(), getEditorOption());

		// 视频原音音量
		mMovieEditor.setVideoSoundVolume(1f);
		// 打开美颜
		mMovieEditor.setEnableBeauty(true);

		// 设置水印，默认为空
		mMovieEditor.setWaterMarkImage(BitmapHelper.getBitmapFormRaw(this, R.raw.sample_watermark));
		mMovieEditor.setWaterMarkPosition(WaterMarkPosition.TopRight);
		mMovieEditor.setDelegate(mEditorDelegate);

		mMovieEditor.loadVideo();

	}

	/**
	 * TuSDKMovieEditor 配置信息
	 *
	 * @return
	 */
	protected TuSDKMovieEditorOptions getEditorOption()
	{
		float movieLeft = getIntent().getFloatExtra("movieLeft", 0.0f);
		float movieTop = getIntent().getFloatExtra("movieTop", 0.0f);
		float movieRight = getIntent().getFloatExtra("movieRight", 1.0f);
		float movieBottom = getIntent().getFloatExtra("movieBottom", 1.0f);

		boolean isRatioAdaption = getIntent().getBooleanExtra("ratioAdaption", true);

		TuSDKMovieEditorOptions defaultOptions = TuSDKMovieEditorOptions.defaultOptions();
		defaultOptions.setMoviePath(mVideoPath)
				.setCutTimeRange(mCutTimeRange)
				// 是否需要按原视频比例显示
				.setOutputRegion(isRatioAdaption ? null : new RectF(movieLeft,movieTop, movieRight, movieBottom) )
				.setEnableBeauty(true) // 设置是否开启美颜
				.setIncludeAudioInVideo(true) // 设置是否保存或者播放原音
				.setLoopingPlay(true) // 设置是否循环播放视频
				.setAutoPlay(true) // 设置视频加载完成后是否自动播放
				.setClearAudioDecodeCacheInfoOnDestory(false); // 设置MovieEditor销毁时是否自动清除缓存音频解码信息

		return defaultOptions;
	}

	/**
	 * 启动预览
	 */
	private void startPreView()
	{
		if (mMovieEditor == null) return;

		// 移除之前设置的特效，并设置新的特效
		mMovieEditor.setSceneMediaEffectList(mScenceEffectLayout.getSceneEffectsTimelineView().getAllSceneEffectModelList());

		mMovieEditor.startPreview();
		updateActionButtonStatus(false);
	}

	/***
	 * 启动录制
	 */
	private void startRecording()
	{
		if (mMovieEditor == null || mMovieEditor.isRecording()) return;

		String msg = getStringFromResource("new_movie_saving");
		TuSdk.messageHub().setStatus(MovieEditorActivity.this, msg);

		// 设置场景特效
		mMovieEditor.setSceneMediaEffectList(mScenceEffectLayout.getSceneEffectsTimelineView().getAllSceneEffectModelList());

		// 生成视频文件
		mMovieEditor.startRecording();

		updateActionButtonStatus(false);

	}

	/**
	 * 切换滤镜
	 *
	 * @param code
	 * 			滤镜code
	 */
	protected void changeVideoFilterCode(String code)
	{
		mMovieEditor.switchFilter(code);
	}

	/**
	 * 添加特效
	 *
	 * @param effectData
	 */
	private void addMediaEffect(TuSDKMediaEffectData effectData)
	{
		if(effectData != null)
		{
			// 设置MV所在区域
			effectData.setAtTimeRange(mMVStickerMusicTimeRange);
		}

		mMovieEditor.addMediaEffectData(effectData);
	}

	/**
	 * 是否正在录制音效
	 *
	 * @return
	 */
	private boolean isAudioRecording()
	{
		return mAudioRecorder.isRecording();
	}

	/**
	 * 初始化 TuSDKAudioFileRecorder
	 */
	private void initAudioFileRecorder()
	{
		mAudioRecorder = new TuSDKAudioFileRecorder();
		mAudioRecorder.setOutputFormat(OutputFormat.AAC);
		mAudioRecorder.setAudioRecordDelegate(mRecordAudioDelegate);
	}


	@SuppressWarnings("deprecation")
	private GestureDetector detector = new GestureDetector(new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) 
        {
        	// 开始播放视频
        	handleActionButton();
            return true;
        }
    });

	public RelativeLayout getFilterMVLayout()
	{
		RelativeLayout filterMVLayoutWrap = (RelativeLayout) findViewById(R.id.filter_mv_wrap_layout);
		return filterMVLayoutWrap;
	}
	
	public RelativeLayout getBottomNavigationLayout()
	{
		RelativeLayout bottomNavigationLayout = (RelativeLayout) findViewById(R.id.lsq_bottom_navigator);
		return bottomNavigationLayout;
	}
	
	public RelativeLayout getMixingListViewWrap()
	{
		RelativeLayout mixingLayoutWrap = (RelativeLayout) findViewById(R.id.lsq_mixing_list_view_wrap);
		return mixingLayoutWrap;
	}

	/**
	 * 加载场景特效视图
	 */
	private void loadScenceEffectLayout()
	{

		mScenceEffectBtn = findViewById(R.id.lsq_effectWrap);
		mScenceEffectBtn.setOnClickListener(mButtonClickListener);

		mScenceEffectLayout = findViewById(R.id.lsq_scence_effect_layout);
		mScenceEffectLayout.loadView();

		mScenceEffectLayout.getSceneEffectsTimelineView().setDuration(mCutTimeRange.duration());

		mScenceEffectLayout.setDelegate(mSceneEffectListViewDelegate);
		mScenceEffectLayout.setDelegate(mSceneEffectsTimelineViewDelegate);
	}

	/**
	 * 初始化音效视图
	 */
	private void initDubbingView()
	{
        mDubbingLayoutWrap = findViewById(R.id.lsq_dubbing_wrap);
		mDubbingButton = findViewById(R.id.lsq_dubbingBtn);
		mDubbingButton.setOnClickListener(mButtonClickListener);
		mAudioRecordingLayout =  findViewById(R.id.lsq_editor_voiceRecording_layout);

		updateVoiceRecordLayout(true);

		getMixingListView();
		getVoiceConfigView();

		initAudioRecordingView();
		mAudioTimingRunnable = new AudioTimingRunnable();
		mAudioTimingRunnable.setDelegate(mAudioRecordProgressDelegate);
	}

	private CompoundConfigView getVoiceConfigView()
	{
		if (mVoiceConfigView == null)
		{
			mVoiceConfigView = (CompoundConfigView) findViewById(R.id.lsq_voice_config_view);
			mVoiceConfigView.setBackgroundResource(R.color.lsq_alpha_white_99);
			mVoiceConfigView.setDelegate(mFilterConfigSeekbarDelegate);

			ConfigViewParams params = new ConfigViewParams();
			params.appendFloatArg(TuSdkContext.getString("originIntensity"), 1.0f);
			params.appendFloatArg(TuSdkContext.getString("dubbingIntensity"), 1.0f);
			mVoiceConfigView.setCompoundConfigView(params);
		}

		return mVoiceConfigView;
	}


	private AudioTimingRunnable.AudioRecordProgressDelegate mAudioRecordProgressDelegate = new AudioTimingRunnable.AudioRecordProgressDelegate()
	{

		@Override
		public void onAudioRecordPogressChanged(final float duration) 
		{
			ThreadHelper.post(new Runnable()
			{
				@Override
				public void run()
				{
                	if (duration > mCutTimeRange.duration())
					{
                		stopAudio();
						updateVoiceRecordButton(false);
					}
					updateAudioProgressBar(duration);
					updateAudioTimeRemaining(duration);  
				}
			});
		}
	};
	
	/** 关闭录音 */
	private void stopAudio()
	{
		// 关闭录音放在子线程,避免录音时更新进度延迟严重(30ms)
		ThreadHelper.runThread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				mAudioRecorder.stop();
			}
		});
	}
	
	@SuppressLint("ClickableViewAccessibility") 
	private void initAudioRecordingView()
	{
		mAudioRecordCloseButton = (TuSdkImageView) findViewById(R.id.lsq_voice_close_button);
		mAudioRecordButton = (TuSdkImageView) findViewById(R.id.lsq_voice_record_button);
		mAudioRecordCancelButton = (TuSdkImageView) findViewById(R.id.lsq_voice_cancel_button);
		mAudioRecordCancelButton.setOnClickListener(mButtonClickListener);
		mAudioRecordSaveButton = (TuSdkImageView) findViewById(R.id.lsq_voice_record_save_button);
		mAudioRecordCloseButton.setOnClickListener(mButtonClickListener);
		mAudioRecordButton.setOnTouchListener(mAudioRecordButtonOnTouchListener);
		mAudioRecordSaveButton.setOnClickListener(mButtonClickListener);
		
		Button minTimeButton = (Button) findViewById(R.id.lsq_minTimeBtn);
		LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
		minTimeLayoutParams.leftMargin =(int)(((float) minAudioRecordTime * TuSdkContext.getScreenSize().width) / mCutTimeRange.duration())
				 -TuSdkContext.dip2px(minTimeButton.getWidth());
		
		mAudioTimeRemainingText = (TextView) findViewById(R.id.lsq_voiceRrecord_timeRemaining_text);
		updateAudioTimeRemaining(0.0f);
		mAudioRecordProgressBar = (ProgressBar) findViewById(R.id.lsq_record_progressbar);
		updateAudioProgressBar(0.0f);
		initAudioFileRecorder();
	}
	
	/**   
	 * 更新录音界面剩余时间文本
	 * 
	 * @param duration
	 */
	private void updateAudioTimeRemaining(float duration)
	{
		int timeRemaining = (int) Math.abs(mCutTimeRange.duration()- duration);
		mAudioTimeRemainingText.setText("剩余"+timeRemaining+"秒");
	}
	
	/**
	 * 更新录音进度条
	 * 
	 * @param audioRecordDuration
	 */
	private void updateAudioProgressBar(float audioRecordDuration)
	{
		float videoDuration = mCutTimeRange.duration();
		
		if(videoDuration == 0) return;
		
		int progress = (int) (audioRecordDuration *100 / videoDuration);
		if (progress > 100) progress = 100;
		mAudioRecordProgressBar.setProgress(progress);
	}

	/**
	 * 原音配音调节栏委托事件
	 */
	private ConfigViewSeekBar.ConfigSeekbarDelegate mFilterConfigSeekbarDelegate = new ConfigViewSeekBar.ConfigSeekbarDelegate() 
	{
		
		@Override
		public void onSeekbarDataChanged(ConfigViewSeekBar seekbar, ConfigViewArg arg) 
		{
			if(arg.getKey().equals("originIntensity"))
			{
				mMovieEditor.setVideoSoundVolume(arg.getPercentValue());
			}
			else if(arg.getKey().equals("dubbingIntensity"))
			{
				if (mAudioEffectData == null) return;
				mAudioEffectData.setVolume(arg.getPercentValue());
			}
		}
	};
	

	/** 
	 * 加载视频缩略图
	 */
	public void loadVideoThumbList()
	{

		if ( (mRangeSelectionBar != null || mRangeSelectionBar.getVideoThumbList().size() == 0)
				&& (mScenceEffectLayout != null || mRangeSelectionBar.getVideoThumbList().size() == 0))
		{

			TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56), TuSdkContext.dip2px(56));
			TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();
			extractor.setOutputImageSize(tuSdkSize);
			extractor.setVideoDataSource(TuSDKMediaDataSource.create(mVideoPath));
			extractor.setExtractFrameCount(6);
			extractor.setTimeRange(mCutTimeRange);

			extractor.asyncExtractImageList(new TuSDKVideoImageExtractorDelegate()
			{
				@Override
				public void onVideoImageListDidLoaded(List<Bitmap> images) {
				}

				@Override
				public void onVideoNewImageLoaded(Bitmap bitmap)
				{
					mRangeSelectionBar.drawVideoThumb(bitmap);
					mScenceEffectLayout.getSceneEffectsTimelineView().drawVideoThumb(bitmap);
				}
			});

		}

	}
	
	@Override
    protected void onResume()
    {
	    hideNavigationBar();
        super.onResume();

		mMovieEditor.startPreview();
    }
	
    @Override
    protected void onPause()
    {
        super.onPause();
        TuSdk.messageHub().dismissRightNow();

        if (mMovieEditor.isPreviewing())
			mMovieEditor.stopPreview();
        else
			mMovieEditor.cancelRecording();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mMovieEditor != null)
        	mMovieEditor.destroy();

		if (mRangeSelectionBar != null)
			mRangeSelectionBar.clearVideoThumbList();

		if (mScenceEffectLayout != null)
			mScenceEffectLayout.getSceneEffectsTimelineView().clearVideoThumbList();

		if(mVideoThumbList != null)
		{
			for (Bitmap bitmap : mVideoThumbList)
				BitmapHelper.recycled(bitmap);

			mVideoThumbList.clear();
			mVideoThumbList = null;
		}

    }
	
	@SuppressLint("ClickableViewAccessibility") 
	protected OnTouchListener mCameraViewOnTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			detector.onTouchEvent(event);
			return true;
		}
	};
	
	protected RelativeLayout getCameraView()
	{
		RelativeLayout cameraView =  (RelativeLayout)findViewById(R.id.lsq_cameraView);
		
		// 播放视图设置触摸事件
		cameraView.setOnTouchListener(mCameraViewOnTouchListener);
		return cameraView;
	}
	
	/**
	 * 设置视频播放区域大小
	 */
	protected void setCameraViewSize(int width,int height)
	{
		LayoutParams lp = (LayoutParams) getCameraView().getLayoutParams();
		lp.width = width;
		lp.height = height;
	}
	

	private List<AudioEffectCellView.AudioEffectEntity> getMixingListData()
	{
		List<AudioEffectCellView.AudioEffectEntity> groups = new ArrayList<AudioEffectCellView.AudioEffectEntity>();
		
		String[][] mixingStrings = new String[][]{{"0","nosound"},{"0","soundrecording"},{"1","lively"},{"1","oldmovie"},{"1","relieve"}};
		
		for(int i = 0; i < mixingStrings.length; i++)
		{
			int type = Integer.parseInt(mixingStrings[i][0]);
			String name = mixingStrings[i][1];
			AudioEffectCellView.AudioEffectEntity audioEffectEntity = new AudioEffectCellView.AudioEffectEntity(type,name);
			groups.add(audioEffectEntity);
		}
		return groups;
	}
	
	/**
	 * 贴纸组视图
	 */
	private AudioEffectListView getMixingListView()
	{
		if (mMixingListView == null)
		{
			mMixingListView = findViewById(R.id.lsq_mixing_list_view);

			mMixingListView.loadView();
			mMixingListView.setCellLayoutId(R.layout.movie_editor_audio_effect_cell_view);
			mMixingListView.setItemClickDelegate(mMixingTableItemClickDelegate);

			mMixingListView.setModeList(getMixingListData());
			mMixingListView.reloadData();

		}
		
		return mMixingListView;
	}
	
	/**
	 * 初始化MV贴纸视图
	 */
	protected void initMvListView()
	{
		getMvListView();

		if (mMvListView == null) return;
		
		mMusicMap.put(1420, R.raw.lsq_audio_cat);
		mMusicMap.put(1427, R.raw.lsq_audio_crow);
		mMusicMap.put(1432, R.raw.lsq_audio_tangyuan);
		mMusicMap.put(1446, R.raw.lsq_audio_children);
		List<StickerGroup> groups = new ArrayList<StickerGroup>();
		List<StickerGroup> smartStickerGroups = StickerLocalPackage.shared().getSmartStickerGroups(false);
		groups.addAll(smartStickerGroups);
		groups.add(0,new StickerGroup());
		this.mMvListView.setModeList(groups);
	}
	
	/**
	 * 贴纸组视图
	 */
	private StickerAudioEffectListView getMvListView()
	{
		if (mMvListView == null)
		{
			mMvListView = findViewById(R.id.lsq_mv_list_view);
			mMvListView.loadView();
			mMvListView.setCellLayoutId(R.layout.movie_editor_sticker_audio_effect_cell_view);
			mMvListView.setItemClickDelegate(mMvTableItemClickDelegate);
			mMvListView.reloadData();
		}
		
		return mMvListView;
	}

	/**
	 *  混音列表点击事件
	 */
	private TuSdkTableViewItemClickDelegate<AudioEffectCellView.AudioEffectEntity, AudioEffectCellView> mMixingTableItemClickDelegate = new TuSdkTableView.TuSdkTableViewItemClickDelegate<AudioEffectCellView.AudioEffectEntity, AudioEffectCellView>()
	{
		@Override
		public void onTableViewItemClick(AudioEffectCellView.AudioEffectEntity itemData, AudioEffectCellView itemView, int position)
		{
			onMixingGroupSelected(itemData, itemView, position);
		}
	};
	
	/**
	 * MV 列表点击事件
	 */
	private TuSdkTableViewItemClickDelegate<StickerGroup, StickerAudioEffectCellView> mMvTableItemClickDelegate = new TuSdkTableView.TuSdkTableViewItemClickDelegate<StickerGroup, StickerAudioEffectCellView>()
	{
		@Override
		public void onTableViewItemClick(StickerGroup itemData, StickerAudioEffectCellView itemView, int position)
		{
			onMvGroupSelected(itemData, itemView, position);
		}
	};
	


    /**
     * MV选择事件
     *
     * @param itemData
     * @param itemView
     * @param position
     */
    protected void onMixingGroupSelected(AudioEffectCellView.AudioEffectEntity itemData, AudioEffectCellView itemView, int position)
    {
    	if (mLastMixingCellView == itemView) return;
		// 取消上一个 MV 的选中状态
    	selectMixing(mLastMixingCellView, false, -1);
		
		// 选中当前 MV
    	selectMixing(itemView, true, position);
	}
    
    
    /**
     * 配音界面歌曲选中事件
     */
 	@SuppressLint("DefaultLocale") 
 	private void selectMixing(AudioEffectCellView itemView, boolean isEnable, final int position)
 	{
 		if(itemView == null) return;
 		
 		// 显示或隐藏边框
 		RelativeLayout filterBorderView = itemView.getBorderView();
 		filterBorderView.setVisibility(isEnable? View.VISIBLE: View.GONE);
 		
 		AudioEffectCellView.AudioEffectEntity group = itemView.getModel();
 		TuSdkImageView mixingImageView = itemView.getImageView();
 		String drawableId = isEnable ? ("lsq_mixing_thumb_"+group.mName.toLowerCase()+"_selected") : ("lsq_mixing_thumb_"+group.mName.toLowerCase());
 		Drawable drawable = TuSdkContext.getDrawable(drawableId);
 		if (drawable == null)
 			drawable = TuSdkContext.getDrawable("lsq_mixing_thumb_"+group.mName.toLowerCase());
 		
 		mixingImageView.setImageDrawable(drawable);
 		
 		// 设置字体颜色
 		int textColorId = isEnable? TuSdkContext.getColor("lsq_filter_title_color"): TuSdkContext.getColor("lsq_filter_title_default_color");
 		itemView.getTitleView().setTextColor(textColorId);
 		
 		if(!isEnable) 
 		{
 			itemView.getImageView().invalidate();
 			return;
 		}
 		else 
 		{
 			selectMV(mLastMvView, false, -1, null);
 			mLastMvView = null;
 			
 			changeAudioEffect(position);
		}
        mLastMixingCellView = itemView;
	}
 	
 	/**
 	 * 应用背景音乐特效
 	 * 
 	 * @param position
 	 */
    protected void changeAudioEffect(int position)
    {
    	int[] resId = new int[]{R.raw.lsq_audio_lively, R.raw.lsq_audio_oldmovie, R.raw.lsq_audio_relieve};
    	
    	if (position == 0)
    	{
    		mMovieEditor.removeAllMediaEffects();
    		initDubbingSeekBar(0.0f);
    	}
    	else if (position == 1)
    	{
    		handleAudioRecordBtn();
    		return;
    	}
    	else if (position > 1)
    	{
    		Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + resId[position-2]);

    		setAudioEffect(uri);
    	}
    	
	  	//循环播放视频
    	startPreView();
    }
    
    /** 处理自己录音按钮操作 */
    private void handleAudioRecordBtn()
    {
		updateVoiceRecordLayout(false);
    	toggleCloseCancleButton(true);
    	updateAudioTimeRemaining(0.0f);
    	deleteFile(mAudioFile);
    	mAudioFile = null;
    	
    	// 录音时清除配音效果
    	mMovieEditor.removeAllMediaEffects();
    	// 将原音强度设为0
    	mMovieEditor.setVideoSoundVolume(0.0f);
    	getConfigViewSeekBar(0).setProgress(0.0f);
    	// 清除配音选中框
    	selectMixing(mLastMixingCellView, false, -1);
    	mLastMixingCellView = null;
    }
    
    /**
     * 设置配音音效
     * 
     * @param audioPathUri
     */
    private void setAudioEffect(Uri audioPathUri)
    {
    	if (audioPathUri == null) return;
		mAudioEffectData = new TuSDKMediaAudioEffectData(TuSDKMediaDataSource.create(audioPathUri));
		
		addMediaEffect(mAudioEffectData);
		initDubbingSeekBar(1.0f);
    }
    
    /**
     * 初始化配音强度SeekBar
     * 
     * @param progress
     */
    public void initDubbingSeekBar(float progress)
    {
		getConfigViewSeekBar(1).setProgress(progress);
    }
    
    /**
     * MV选择事件
     *
     * @param itemData
     * @param itemView
     * @param position
     */
	
    protected void onMvGroupSelected(StickerGroup itemData, StickerAudioEffectCellView itemView, int position)
    {
    	if(mLastMvView==itemView)return;
    	long currentClickTime=System.currentTimeMillis();
    	if(currentClickTime-lastClickTime<=500)return;
    	lastClickTime=currentClickTime;
		// 取消上一个 MV 的选中状态
		selectMV(mLastMvView, false, -1, null);
		
		// 选中当前 MV
		selectMV(itemView, true, position, itemData);
	}

	/**
	 *
	 * @param itemView
	 * @param isEnable
	 * @param position
	 * @param itemData
	 */
 	private void selectMV(StickerAudioEffectCellView itemView, boolean isEnable, final int position, final StickerGroup itemData)
 	{
 		if(itemView == null) return;
 		
 		// 显示或隐藏边框
 		RelativeLayout filterBorderView = itemView.getBorderView();
 		filterBorderView.setVisibility(isEnable? View.VISIBLE: View.GONE);
 		
 		if(!isEnable) 
 		{
 			itemView.getImageView().invalidate();

 			mMovieEditor.stopPreview();
 			return;
 		}
 		// 选中MV就取消背景音效
 		selectMixing(mLastMixingCellView, false, -1);
 		mLastMixingCellView = null;
 		
        mLastMvView = itemView;

    	ThreadHelper.post(new Runnable()
    	{
			@Override
			public void run() 
			{
			   // 应用 MV 特效
		       changeMvEffect(position, itemData);
			}
		});
	}
 	
	/**
	 * 初始化滤镜栏视图
	 */
	protected void initFilterListView()
	{
		getFilterConfigView().setVisibility(View.INVISIBLE);
		getFilterListView();

		if (mFilterListView == null) return;

		this.mFilterListView.setModeList(Arrays.asList(Constants.EDITORFILTERS));
	}

	/**
	 * 滤镜栏视图
	 * 
	 * @return
	 */
	private FilterListView getFilterListView()
	{
		if (mFilterListView == null)
		{
			mFilterListView = (FilterListView) findViewById(R.id.lsq_filter_list_view);
			
			if (mFilterListView == null) return null;

			mFilterListView.loadView();
			mFilterListView.setCellLayoutId(R.layout.filter_list_cell_view);
			mFilterListView.setCellWidth(TuSdkContext.dip2px(62));
			mFilterListView.setItemClickDelegate(mFilterTableItemClickDelegate);
			mFilterListView.reloadData();
			mFilterListView.selectPosition(mFocusPostion);
		}
		return mFilterListView;
	}

	private FilterConfigView getFilterConfigView()
	{
		if (mConfigView == null)
		{
			mConfigView = (FilterConfigView)findViewById(R.id.lsq_filter_config_view);
			mConfigView.setBackgroundResource(R.color.lsq_alpha_white_99);
		}
		
		return mConfigView;
	}

	/** 滤镜组列表点击事件 */
	private TuSdkTableViewItemClickDelegate<String, FilterCellView> mFilterTableItemClickDelegate = new TuSdkTableViewItemClickDelegate<String, FilterCellView>()
	{
		@Override
		public void onTableViewItemClick(String itemData,
				FilterCellView itemView, int position)
		{
			onFilterGroupSelected(itemData, itemView, position);
		}
	};

    /**
 	 * 滤镜组选择事件
 	 * 
  	 * @param itemView
 	 * @param position
 	 */
 	protected void onFilterGroupSelected(String itemData, FilterCellView itemView, int position)
	{
 		startPreView();
 		
		mFocusPostion = position;
		mFilterListView.selectPosition(mFocusPostion);
		
 		changeVideoFilterCode(itemData);
 		deSelectLastFilter(lastSelectedCellView, position);
 		selectFilter(itemView, position);
		getFilterConfigView().setVisibility((position == 0)?View.INVISIBLE:View.VISIBLE);
 	}

 	/**
 	 * 滤镜选中状态
 	 * 
 	 * @param itemView
 	 * @param position
 	 */
 	private void selectFilter(FilterCellView itemView, int position)
 	{
 		updateFilterBorderView(itemView, false);
 		itemView.setFlag(position);
 		
		TextView titleView = itemView.getTitleView();
		titleView.setBackground(TuSdkContext.getDrawable("tusdk_view_filter_selected_text_roundcorner"));
        // 记录本次选中的滤镜
        lastSelectedCellView = itemView;
 	}
 	
	/**
	 * 设置滤镜单元边框是否可见
	 * @param lastFilter
	 * @param isHidden
	 */
	private void updateFilterBorderView(FilterCellView lastFilter,boolean isHidden)
	{
		View filterBorderView = lastFilter.getBorderView();
		filterBorderView.setVisibility(isHidden ? View.GONE : View.VISIBLE);
	}
 	
 	/**
 	 * 取消上一个滤镜的选中状态
 	 * 
 	 * @param lastFilter
 	 * @param position
 	 */
 	private void deSelectLastFilter(FilterCellView lastFilter, int position)
 	{
 		if(lastFilter == null) return;

 		lastFilter.setFlag(-1);
 		updateFilterBorderView(lastFilter, true);
 		lastFilter.getTitleView().setBackground(TuSdkContext.getDrawable("tusdk_view_filter_unselected_text_roundcorner"));
		lastFilter.getImageView().invalidate();
 	}
 	
 	/**
 	 * 应用MV特效
 	 * @param position
 	 * @param itemData
 	 */
    protected void changeMvEffect(int position, StickerGroup itemData)
    {
    	if (position < 0) return;
    	
    	if (position == 0 )
    	{
			mStickerAudioEffectData = null;
			mMovieEditor.removeAllMediaEffects();
    	}
    	else
    	{
        	int groupId = (int) itemData.groupId;
        	if (mMusicMap!=null && mMusicMap.containsKey(groupId))
        	{
    			Uri	uri = Uri.parse("android.resource://" + getPackageName() + "/" + mMusicMap.get(groupId));
        		mStickerAudioEffectData = new TuSDKMediaStickerAudioEffectData(TuSDKMediaDataSource.create(uri), itemData);
        	}
        	else
        	{
        		mStickerAudioEffectData = new TuSDKMediaStickerEffectData(itemData);
        	}
        	
			// 设置MV特效的音乐路径和贴纸对象
			addMediaEffect(mStickerAudioEffectData);
    	}
    	
	  	//循环播放视频
    	startPreView();
    }
    
    /**
     * 更新操作按钮
     *
     * @param isRunning 是否直播中
     */
	protected void updateActionButtonStatus(Boolean isRunning)
    {
        mActionButton.setVisibility(isRunning? View.VISIBLE: View.INVISIBLE);
    }
	
	/**
	 * 选择滤镜
	 */
	private void toggleFilterMode()
	{
		if (mIsFirstEntry)
		{
			changeVideoFilterCode(Arrays.asList(Constants.VIDEOFILTERS).get(mFocusPostion));
		}

		mFilterWrap.setVisibility(View.VISIBLE);

		getFilterMVLayout().setVisibility(View.GONE);
		getVoiceConfigView().setVisibility(View.INVISIBLE);
		mScenceEffectLayout.setVisibility(View.GONE);
		mRangeSelectionBar.setVisibility(View.GONE);
		mDubbingLayoutWrap.setVisibility(View.GONE);
		getFilterConfigView().setVisibility(View.VISIBLE);

		updateButtonStatus(mFilterBtn, true);
		updateButtonStatus(mMvBtn, false);
		updateButtonStatus(mDubbingButton, false);
		updateButtonStatus(mScenceEffectBtn,false);


		if (mSelesOutInput == null) return;
		
		getFilterConfigView().post(new Runnable()
		{

			@Override
			public void run() {
				getFilterConfigView().setSelesFilter(mSelesOutInput.getFilter());
				getFilterConfigView().setVisibility(View.VISIBLE);
			}});
		
		getFilterConfigView().setSeekBarDelegate(mConfigSeekBarDelegate);
		getFilterConfigView().invalidate();
	}
	
	/**
	 * 选择MV
	 */
	private void toggleMVMode()
	{
		getFilterMVLayout().setVisibility(View.VISIBLE);
		mRangeSelectionBar.setVisibility(View.VISIBLE);

		mFilterWrap.setVisibility(View.GONE);
		mDubbingLayoutWrap.setVisibility(View.GONE);
		getVoiceConfigView().setVisibility(View.VISIBLE);
		getFilterConfigView().setVisibility(View.GONE);
		mScenceEffectLayout.setVisibility(View.GONE);


		updateButtonStatus(mMvBtn, true);
		updateButtonStatus(mFilterBtn, false);
		updateButtonStatus(mDubbingButton, false);
		updateButtonStatus(mScenceEffectBtn,false);

	}

	/**
	 * 点击配音按钮实现的功能
	 */
	private void toggleDubbingMode()
	{
		mDubbingLayoutWrap.setVisibility(View.VISIBLE);
		getVoiceConfigView().setVisibility(View.VISIBLE);
		mRangeSelectionBar.setVisibility(View.VISIBLE);

		mFilterWrap.setVisibility(View.GONE);
		mScenceEffectLayout.setVisibility(View.GONE);
		getFilterMVLayout().setVisibility(View.GONE);
		getFilterConfigView().setVisibility(View.GONE);

		updateButtonStatus(mFilterBtn, false);
		updateButtonStatus(mMvBtn, false);
		updateButtonStatus(mDubbingButton, true);
		updateButtonStatus(mScenceEffectBtn,false);

	}

	/**
	 * 切换场景特效模式
	 */
	private void toggleScenceEffectMode()
	{
		/**  设置场景特效 */
		mMovieEditor.setSceneMediaEffectList(mScenceEffectLayout.getSceneEffectsTimelineView().getAllSceneEffectModelList());

		mScenceEffectLayout.setVisibility(View.VISIBLE);

		mRangeSelectionBar.setVisibility(View.GONE);
		mFilterWrap.setVisibility(View.GONE);
		mDubbingLayoutWrap.setVisibility(View.GONE);
		getVoiceConfigView().setVisibility(View.GONE);
		getFilterMVLayout().setVisibility(View.GONE);
		getFilterConfigView().setVisibility(View.GONE);

		updateButtonStatus(mFilterBtn, false);
		updateButtonStatus(mMvBtn, false);
		updateButtonStatus(mDubbingButton, false);
		updateButtonStatus(mScenceEffectBtn,true);

	}

	/** 按钮点击事件处理 */
    private View.OnClickListener mButtonSafeClickListener = new TuSdkViewHelper.OnSafeClickListener() 
    {
        public void onSafeClick(View v)
        {
            if (v == mSaveButton)
                startRecording();
        }
    };
    
    private View.OnClickListener mButtonClickListener=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
		  if(v == mBackTextView)
	        {
	        	finish();
	        }
	        else if (v==mFilterBtn) 
	        {
	        	toggleFilterMode();
	        }
	        else if(v==mMvBtn)
	        {
	        	toggleMVMode();
	        }
	        else if(v == mDubbingButton)
	        {
	        	toggleDubbingMode();
	        }
	        else if(v ==  mScenceEffectBtn)
		   {
			  toggleScenceEffectMode();
		   }
	        else if (v == mAudioRecordCloseButton) 
	        {
	        	updateVoiceRecordLayout(true);
	        	mMovieEditor.setVideoSoundVolume(1.0f);
	        	getConfigViewSeekBar(0).setProgress(1.0f);
	        	selectMixing(mLastMixingCellView, false, -1);
	        	mLastMixingCellView = null;
	        }
		  else if (v == mAudioRecordSaveButton) 
		  {
			  if (mAudioRecordProgressBar.getProgress() == 0) return;
			  updateVoiceRecordLayout(true);
			  updateAudioProgressBar(0);
			  mMovieEditor.setVideoSoundVolume(1.0f);
			  getConfigViewSeekBar(0).setProgress(1.0f);
			  if (mAudioFile != null)
			  {
				  setAudioEffect(Uri.fromFile(mAudioFile));
				  startPreView();
			  }
		  }
		  else if (v == mAudioRecordCancelButton)
		  {
			  // 清空录音临时文件
			  deleteFile(mAudioFile);
			  mAudioFile = null;
			  updateAudioProgressBar(0);
			  
			 toggleCloseCancleButton(true);
			 
			 updateAudioTimeRemaining(0.0f);
		  }
		}
	};
	
	private void deleteFile(File file)
	{
		if (file == null) return;
		
		FileHelper.delete(file);
		refreshFile(file);
	}
	
	 public void refreshFile(File file) 
	 {
		if (file == null) {
			TLog.e("refreshFile file == null");
			return;
		}
		
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		this.sendBroadcast(intent);
	 }
	 
	 private ConfigViewSeekBar getConfigViewSeekBar(int index)
	 {
		 ConfigViewSeekBar configViewSeekBar = getVoiceConfigView().getSeekBarList().get(index);
		 return configViewSeekBar;
	 }
	
	/** 录音按钮触摸事件处理 */
	private OnTouchListener mAudioRecordButtonOnTouchListener = new OnTouchListener()
	{
		long startTime;
		long recordTime;
		
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					startTime = System.currentTimeMillis();
					if (mAudioFile == null && mStartAudioTimes < 1)
					{
						mAudioRecorder.start();
					}
					if (!isAudioRecording())
					{
						String hintMsg = getStringFromResource("lsq_audio_delete_hint");
						TuSdk.messageHub().showToast(MovieEditorActivity.this, hintMsg);
					}
					break;
					
				case MotionEvent.ACTION_UP:
			
				case MotionEvent.ACTION_CANCEL:
					if (mStartAudioTimes >1) break;
					recordTime = System.currentTimeMillis()-startTime; 
					// 避免录音时间过短导致奔溃
					if (recordTime < 500)
					{
						ThreadHelper.postDelayed(new Runnable() 
						{
							
							@Override
							public void run()
							{
								// 调用stop前先判断
								// 避免录音状态不对造成异常
								if (!isAudioRecording()) return;
								mAudioRecorder.stop();
							
							}
						}, 500-recordTime);
					}
					else 
					{
						stopAudio();
					}
					
	
					break;   
	        }
			return true;
		}
	};
	
	/**
	 * 录音界面关闭和撤销按钮切换
	 */
	private void toggleCloseCancleButton(boolean isCloseButton)
	{
		mAudioRecordCloseButton.setVisibility(isCloseButton ? View.VISIBLE : View.INVISIBLE);
		mAudioRecordCancelButton.setVisibility(isCloseButton ? View.INVISIBLE : View.VISIBLE);
	}
	
	private void updateVoiceRecordButton(boolean isRecording)
	{
		int imgId = 0;
		imgId = isRecording ? R.drawable.tusdk_view_dubbing_record_selected_button : R.drawable.tusdk_view_dubbing_record_unselected_button;
		mAudioRecordButton.setImageResource(imgId);
	}
	

	/**
	 * 录音委托事件
	 */
	private TuSDKRecordAudioDelegate mRecordAudioDelegate = new TuSDKRecordAudioDelegate(){

		@Override
		public void onAudioRecordComplete(File file)
		{
			mAudioFile = file;
		}

		@Override
		public void onAudioRecordStateChanged(RecordState state)
		{
			if (state == RecordState.Recording)
			{
				mAudioRecordStartTime = System.currentTimeMillis();
				mAudioTimingRunnable.setAudioRecordStartTime(mAudioRecordStartTime);
				mAudioTimingRunnable.start();
				ThreadHelper.runThread(mAudioTimingRunnable);
				
				updateVoiceRecordButton(true);
				mMovieEditor.stopPreview();
				startPreView();
				mStartAudioTimes++;
	                
			}
			else if (state == RecordState.Stoped)
			{
				mStartAudioTimes = 0;
				mAudioTimingRunnable.stop();
				mMovieEditor.stopPreview();
				updateVoiceRecordButton(false);
				toggleCloseCancleButton(false);
				
				// 录音时间小于最小时间不保留录音
				long audioRecordStopTime = System.currentTimeMillis();
				if((audioRecordStopTime - mAudioRecordStartTime)/(float)1000 < (float)minAudioRecordTime)
				{
					deleteFile(mAudioFile);
					mAudioFile = null;
					ThreadHelper.post(new Runnable() 
					{
						
						@Override
						public void run() 
						{
							updateAudioProgressBar(0.0f);
							toggleCloseCancleButton(true);
						}
					});
					
					String messageId = getStringFromResource("lsq_audio_record_mintime");
					TuSdk.messageHub().showToast(MovieEditorActivity.this, messageId);
				}
			}
		}

		@Override
		public void onAudioRecordError(RecordError error)
		{
			if(error == RecordError.InitializationFailed)
			{
				String messageId = getStringFromResource("lsq_audio_initialization_failed_hint");
				TuSdk.messageHub().showError(MovieEditorActivity.this,messageId);
			}
		}
		
	};
	
	/**
	 * 更新录音界面的显示状态
	 * 
	 * @param isHidden
	 */
	private void updateVoiceRecordLayout(boolean isHidden)
	{
		mAudioRecordingLayout.setVisibility(isHidden ? View.INVISIBLE : View.VISIBLE);
	}
	
    /** 滤镜拖动条监听事件 */
    private FilterConfigViewSeekBarDelegate mConfigSeekBarDelegate = new FilterConfigViewSeekBarDelegate()
    {

		@Override
		public void onSeekbarDataChanged(FilterConfigSeekbar seekbar,FilterArg arg)
		{
			if (arg == null) return;
			
    		if (arg.equalsKey("mixied"))
    			mMixiedProgress = arg.getPrecentValue();
		}

    };

	
	/**
	 * 更新按钮显示状态
	 * 
	 * @param button
	 * @param clickable
	 */
	private void updateButtonStatus(TuSdkTextButton button, boolean clickable)
	{
		int imgId = 0, colorId = 0;
		
		switch (button.getId())
		{
		case R.id.lsq_filterWrap:
			imgId = clickable? R.drawable.lsq_style_default_btn_filter_selected 
					: R.drawable.lsq_style_default_btn_filter_unselected;
			colorId = clickable? R.color.lsq_filter_title_color : R.color.lsq_filter_title_default_color;
			break;
			
		case R.id.lsq_mvWrap:
			imgId = clickable? R.drawable.lsq_style_default_btn_mv_selected 
					: R.drawable.lsq_style_default_btn_mv_unselected;
			colorId = clickable? R.color.lsq_filter_title_color : R.color.lsq_filter_title_default_color;
			break;
		case R.id.lsq_dubbing_wrap:
			imgId = clickable? R.drawable.lsq_style_default_btn_beauty_selected 
					: R.drawable.lsq_style_default_btn_beauty_unselected;
			colorId = clickable? R.color.lsq_filter_title_color : R.color.lsq_filter_title_default_color;
			break;
			case R.id.lsq_effectWrap:
				imgId = clickable? R.drawable.lsq_tab_ic_special_selected
						: R.drawable.lsq_tab_ic_special_normal;
				colorId = clickable? R.color.lsq_filter_title_color : R.color.lsq_filter_title_default_color;
				break;
		}
		
		button.setCompoundDrawables(null, TuSdkContext.getDrawable(imgId), null, null);
		button.setTextColor(TuSdkContext.getColor(colorId));
	}
	
	
	/**
	 * 更新按钮显示状态
	 * 
	 * @param button
	 * @param clickable
	 */
	private void updateButtonStatus(CompoundDrawableTextView button, boolean clickable)
	{
		int imgId = 0, colorId = 0;
		
		switch (button.getId())
		{
		case R.id.lsq_dubbingBtn:
			imgId = clickable? R.drawable.lsq_dubbing_selected
					: R.drawable.lsq_dubbing_default;
			colorId = clickable? R.color.lsq_filter_title_color : R.color.lsq_filter_title_default_color;
			break;
		}
		Drawable dubbingDrawable = TuSdkContext.getDrawable(imgId);
		dubbingDrawable.setBounds(0, 0, TuSdkContext.dip2px(28), TuSdkContext.dip2px(28));
		button.setCompoundDrawables(null, dubbingDrawable, null, null);
		button.setTextColor(TuSdkContext.getColor(colorId));
	}
	
    /**
     * 处理开始、暂停事件
     */
    private void handleActionButton()
    {
		if (mMovieEditor.isPreviewing())
		{
			updateActionButtonStatus(true);
			mMovieEditor.pausePreview();
		}
		else
		{
			startPreView();
		}
    }
    
    /**
     * 第一次进入MV页面默认选中无效果MV
     */
    private void selectNormalFilterAndNormalMv()
    {
    	// 选中默认 MV
		StickerAudioEffectCellView firstMVItem = (StickerAudioEffectCellView) mMvListView.getChildAt(0);
		if(firstMVItem == null) return;
		
		// 选中默认滤镜
		FilterCellView firstFilterItem = (FilterCellView) mFilterListView.getChildAt(0);
		if(firstFilterItem == null) return;

		selectFilter(firstFilterItem, 0);
		
		// 更新播放按钮为播放状态
		updateActionButtonStatus(false);
    }
    
    private TuSDKMovieEditorDelegate mEditorDelegate = new TuSDKMovieEditorDelegate()
    {
		/**
		 * 视频处理完成
		 * 
		 * @param result
		 *            生成的新视频信息，预览时该对象为 null 
		 */
		@Override
		public void onMovieEditComplete(TuSDKVideoResult result) 
		{
			String msg = result == null ? getStringFromResource("new_movie_error_saving")
					: getStringFromResource("new_movie_saved");
			TuSdk.messageHub().showError(MovieEditorActivity.this, msg);
			
			setResult(RESULT_OK);
			finish();
		}
	
		@Override
		public void onMovieEditProgressChanged(float durationTimes, float progress)
		{
			if(!mRangeSelectionBar.isShowPlayCursor())
				mRangeSelectionBar.setShowPlayCursor(true);

			mRangeSelectionBar.setPlaySelection((int)(progress * 100));

			mScenceEffectLayout.getSceneEffectsTimelineView().setProgress(progress);
			mScenceEffectLayout.getSceneEffectsTimelineView().updateLastSceneEffectEndTime(durationTimes);
		}
		
		@Override
		public void onMovieEditorStatusChanged(TuSDKMovieEditorStatus status) 
		{
			TuSdk.messageHub().dismissRightNow();
			
			if(status==TuSDKMovieEditorStatus.Loaded)
			{
		        // 首次进入时，选中 MV 和滤镜默认效果
				selectNormalFilterAndNormalMv();

				mSaveButton.setEnabled(true);
				
			}else if(status==TuSDKMovieEditorStatus.Recording)
			{
				String msg = getStringFromResource("new_movie_saving");
				TuSdk.messageHub().setStatus(MovieEditorActivity.this,msg);
				
			}else if(status == TuSDKMovieEditorStatus.LoadVideoFailed)
			{
                String msg = getStringFromResource("lsq_loadvideo_failed");
                TuSdk.messageHub().showError(MovieEditorActivity.this, msg);				
			}
			else if(status == TuSDKMovieEditorStatus.RecordingFailed)
			{
				updateActionButtonStatus(true);
				mMovieEditor.stopPreview();
				
                String msg = getStringFromResource("new_movie_error_saving");
                TuSdk.messageHub().showError(MovieEditorActivity.this, msg);
			}
			else if(status == TuSDKMovieEditorStatus.PreviewingCompleted)
			{

			}
		}

		/**
		 * 视频原音和音效状态
		 */
		@Override
		public void onMovieEditorSoundStatusChanged(TuSDKMovieEditorSoundStatus status) 
		{
			TuSdk.messageHub().dismissRightNow();
			
			if(status == TuSDKMovieEditorSoundStatus.Loading)
			{
				String msg = getStringFromResource("new_movie_audio_effect_loading");
				TuSdk.messageHub().setStatus(MovieEditorActivity.this, msg);
			}
		}
		
		@Override
		public void onFilterChanged(FilterWrap selesOutInput)
		{
        	if (selesOutInput == null) return;
        	
        	SelesParameters params = selesOutInput.getFilterParameter();
        	List<FilterArg> list = params.getArgs();
        	for (FilterArg arg : list)
        	{
        		if (arg.equalsKey("smoothing") && mSmoothingProgress !=  -1.0f)
        			arg.setPrecentValue(mSmoothingProgress);
        		else if (arg.equalsKey("smoothing") && mSmoothingProgress == -1.0f)
        			mSmoothingProgress = arg.getPrecentValue();
        		else if (arg.equalsKey("mixied") && mMixiedProgress !=  -1.0f)
        			arg.setPrecentValue(mMixiedProgress);
        		else if (arg.equalsKey("mixied") && mMixiedProgress == -1.0f)
        			mMixiedProgress = arg.getPrecentValue();        		
        		
        	}
        	selesOutInput.setFilterParameter(params);
        	
            mSelesOutInput = selesOutInput;
            
			if (getFilterConfigView() != null) 
			{
                getFilterConfigView().setSelesFilter(selesOutInput.getFilter());

            }

            if (mIsFirstEntry)
			{
				mIsFirstEntry = false;
				toggleFilterMode();
			}
		}
    };

	/**
	 * 场景特效列表委托
	 */
	private SceneEffectListView.SceneEffectListViewDelegate mSceneEffectListViewDelegate = new SceneEffectListView.SceneEffectListViewDelegate()
	{

		@Override
		public void onUndo()
		{
			mScenceEffectLayout.getSceneEffectsTimelineView().removeLastSceneEffect();
		}

		@Override
		public void onPressSceneEffect(SceneEffectListView.SceneEffectData effectData)
		{
			// 移除 mMovieEditor 中设置的特效，防止冲突
			mMovieEditor.setSceneMediaEffectList(null);

			float startTime = mMovieEditor.getCurrentSampleTimeUs() / 1000000f;

			SceneEffectsTimelineView.SceneEffectModel sceneEffectModel = new SceneEffectsTimelineView.SceneEffectModel(effectData.getEffectCode(),effectData.getColor(), TuSDKTimeRange.makeRange(startTime,0));

			// 按下时启用场景特效编辑功能
			mScenceEffectLayout.setEditable(true);
			mScenceEffectLayout.getSceneEffectsTimelineView().addSceneEffect(sceneEffectModel);

            mMovieEditor.setLooping(false);
			mMovieEditor.switchSceneEffect(effectData.getEffectCode());
			mMovieEditor.startPreview();
		}

		@Override
		public void onReleaseSceneEffect(SceneEffectListView.SceneEffectData effectData)
		{
			// 松手时场景特效禁用编辑功能
			mScenceEffectLayout.setEditable(false);

			mMovieEditor.setLooping(true);
			mMovieEditor.pausePreview();

			mMovieEditor.switchSceneEffect("");
		}
	};

	/**
	 * 动画时间轴委托
	 */
	private SceneEffectsTimelineView.SceneEffectsTimelineViewDelegate mSceneEffectsTimelineViewDelegate = new SceneEffectsTimelineView.SceneEffectsTimelineViewDelegate()
	{
		public void onCursorWillMove()
		{
			mMovieEditor.stopPreview();
		}

		@Override
		public void onCursorMoved(float progress)
		{
			if (mMovieEditor.getTimeRange() != null && mMovieEditor.getTimeRange().isValid())
				mMovieEditor.seekTimeUs((long)(mMovieEditor.getTimeRange().durationTimeUS() * progress));

		}
	};

	/** 用于监听裁剪控件  */
	private OnCursorChangeListener mOnCursorChangeListener = new OnCursorChangeListener()
	{

		@Override
		public void onSeeekBarChanged(int width, int height)
		{
			setBarSpace();
		}

		@Override
		public void onLeftCursorChanged(final int percent)
		{
			if (mMovieEditor != null)
			{
				mMVStickerMusicTimeRange.start = percent * mCutTimeRange.duration() / 100;
				mMovieEditor.seekTimeUs((long)mMVStickerMusicTimeRange.start * 1000000l);

				if(mMovieEditor.isPreviewing())
				{
					mMovieEditor.stopPreview();
					updateActionButtonStatus(true);
				}
			}

			hidePlayCursor();
		}

		@Override
		public void onPlayCursorChanged(int percent)
		{
		}

		@Override
		public void onRightCursorChanged(final int percent)
		{
			if (mMovieEditor != null)
			{
				mMVStickerMusicTimeRange.end =  percent * mCutTimeRange.duration() / 100;
				mMovieEditor.seekTimeUs((long)mMVStickerMusicTimeRange.start * 1000000l);


				if(mMovieEditor.isPreviewing())
				{
					mMovieEditor.stopPreview();
					updateActionButtonStatus(true);
				}
			}

			hidePlayCursor();
		}

		@Override
		public void onLeftCursorUp()
		{
		}

		@Override
		public void onRightCursorUp()
		{
		}
	};

	/** 设置裁剪控件开始与结束的最小间隔距离 */
	public void setBarSpace()
	{
		if(mCutTimeRange.duration() == 0 ) return;
		if(mRangeSelectionBar!=null)
		{
			/**
			 * 需求需要，需设定最小间隔为1秒的
			 *
			 */
			double percent = (1/mCutTimeRange.duration());
			int space = (int) (percent*mRangeSelectionBar.getWidth());
			mRangeSelectionBar.setCursorSpace(space);
		}
	}

	/**隐藏裁剪控件播放指针  */
	public void hidePlayCursor()
	{
		if(mRangeSelectionBar!=null)
		{
			mRangeSelectionBar.setPlaySelection(-1);
			mRangeSelectionBar.setShowPlayCursor(false);
		}
	}
}