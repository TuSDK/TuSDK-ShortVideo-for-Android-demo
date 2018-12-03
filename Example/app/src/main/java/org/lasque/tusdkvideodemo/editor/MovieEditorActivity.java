package org.lasque.tusdkvideodemo.editor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.utils.TuSdkWaterMarkOption;
import org.lasque.tusdk.core.utils.image.BitmapHelper;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdkvideodemo.R;
import org.lasque.tusdkvideodemo.views.VideoContent;

import static org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor.TuSdkMovieEditorOptions.TuSdkMediaPictureEffectReferTimelineType.TuSdkMediaEffectReferInputTimelineType;

/**
 * 编辑页面
 *
 * @since v3.0.0
 */
public class MovieEditorActivity extends FragmentActivity {
    /** 顶部ViewContent **/
    private FrameLayout mHeaderView;
    /** 底部ViewContent**/
    private FrameLayout mBottomView;
    /** 播放器Content **/
    private VideoContent mVideoContent;
    /** 编辑控制器 **/
    private MovieEditorController mEditorController;
    /** 文字贴纸操作视图 **/
    private StickerView mStickerView;
    /** 魔法效果操作视图 **/
    private RelativeLayout mMagicContent;
    /** 视频路径 **/
    private String mVideoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_editor_full_screen);
        initView();
    }

    /**
     * 初始化View
     */
    private void initView(){
        mVideoPath = getIntent().getStringExtra("videoPath");

        mHeaderView = findViewById(R.id.lsq_editor_header);
        mBottomView = findViewById(R.id.lsq_editor_bottom);
        mVideoContent = findViewById(R.id.lsq_editor_content);
        mStickerView = findViewById(R.id.lsq_stickerView);
        mMagicContent = findViewById(R.id.lsq_magic_content);

        //初始化视频编辑器
        initEditorController();
    }

    private void initEditorController() {
        TuSdkMovieEditor.TuSdkMovieEditorOptions defaultOptions = TuSdkMovieEditor.TuSdkMovieEditorOptions.defaultOptions();
        defaultOptions.setVideoDataSource(new TuSdkMediaDataSource(mVideoPath))
                .setIncludeAudioInVideo(true) // 设置是否保存或者播放原音
                .setClearAudioDecodeCacheInfoOnDestory(false)// 设置MovieEditor销毁时是否自动清除缓存音频解码信息
                .setPictureEffectReferTimelineType(TuSdkMediaEffectReferInputTimelineType)//设置时间线模式
                //设置水印
                .setWaterImage(BitmapHelper.getBitmapFormRaw(this, R.raw.sample_watermark), TuSdkWaterMarkOption.WaterMarkPosition.TopRight, true);
            mEditorController = new MovieEditorController(this,mVideoPath,mVideoContent,defaultOptions);
    }

    /**
     * 获取HeadView
     * @return
     */
    public ViewGroup getHeaderView() {
        return mHeaderView;
    }

    /**
     * 获取BottomView
     * @return
     */
    public ViewGroup getBottomView() {
        return mBottomView;
    }

    /**
     * 获取文字控件
     * @return
     */
    public StickerView getStickerView() {
        return mStickerView;
    }

    /**
     * 获取魔法效果
     * @return
     */
    public RelativeLayout getMagicContent() {
        return mMagicContent;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mEditorController != null)
            mEditorController.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mEditorController != null)
            mEditorController.onDestroy();

        if (mEditorController != null) {
            for (Bitmap bitmap : mEditorController.getThumbBitmapList())
                BitmapHelper.recycled(bitmap);

            mEditorController.getThumbBitmapList().clear();
        }
    }
}
