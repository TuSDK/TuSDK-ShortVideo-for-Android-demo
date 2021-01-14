package org.lasque.tusdkvideodemo.editor.component;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.sticker.StickerPositionInfo;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.impl.components.widget.sticker.StickerDynamicItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerImageItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerTextItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerDynamicData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerImageData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerItemViewInterface;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import org.lasque.tusdk.modules.view.widget.sticker.StickerTextData;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaLiveStickerEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.tusdkvideodemo.R;
import org.lasque.tusdkvideodemo.editor.MovieEditorController;
import org.lasque.tusdkvideodemo.views.MvRecyclerAdapter;
import org.lasque.tusdkvideodemo.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.tusdkvideodemo.views.editor.playview.TuSdkMovieScrollView;
import org.lasque.tusdkvideodemo.views.editor.playview.TuSdkRangeSelectionBar;
import org.lasque.tusdkvideodemo.views.editor.playview.rangeselect.TuSdkMovieColorGroupView;
import org.lasque.tusdkvideodemo.views.editor.playview.rangeselect.TuSdkMovieColorRectView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

/**
 * TuSDK
 * droid-sdk-video$
 *
 * @author H.ys
 * @Date 2020/03/13$ 10:25$
 * @Copyright (c) 2019 tusdk.com. All rights reserved.
 */
public class EditorDynamicStickerComponent extends EditorComponent {

    private View mBottomView;

    private StickerView mStickerView;

    private ImageButton mBackBtn;

    private ImageButton mNextBtn;

    private TuSdkMovieScrollPlayLineView mLineView;

    private ImageView mPlayBtn;

    /**
     * Mv
     */
    private RecyclerView mStickerRecyclerView;

    /**
     * Mv适配器
     */
    private MvRecyclerAdapter mStickerRecyclerAdapter;

    /** 默认持续时间 **/
    private long defaultDurationUs =  4 * 1000000;
    /** 最小持续时间 **/
    private int minSelectTimeUs =  1 * 1000000;

    private StickerDynamicData mCurrentSticker;

    private TuSdkMovieColorRectView mCurrentColorRectView;

    private EditorStickerImageBackups mStickerImageBackups;

    /**
     * MV音效资源
     */
    @SuppressLint("UseSparseArrays")
    private Map<Integer, Integer> mMusicMap = new HashMap<Integer, Integer>();

    private TuSdkSize mCurrentPreviewSize = null;

    /**
     * 显示区域改变回调
     *
     * @since V3.0.0
     */
    private TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener mOnDisplayChangeListener = new TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener() {
        @Override
        public void onPreviewSizeChanged(final TuSdkSize previewSize) {
            if (getEditorController().getActivity().getImageStickerView() == null) return;
            mCurrentPreviewSize = TuSdkSize.create(previewSize.width,previewSize.height);
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    getEditorController().getActivity().getImageStickerView().resize(previewSize, getEditorController().getVideoContentView());
                }
            });

        }
    };


    private StickerView.StickerViewDelegate mStickerDelegate = new StickerView.StickerViewDelegate() {
        @Override
        public boolean canAppendSticker(StickerView view, StickerData sticker) {
            return false;
        }

        @Override
        public boolean canAppendSticker(StickerView view, StickerDynamicData sticker) {
            return true;
        }

        @Override
        public void onStickerItemViewSelected(StickerData stickerData, String text, boolean needReverse) {

        }

        @Override
        public void onStickerItemViewSelected(StickerDynamicData stickerData, String text, boolean needReverse) {
            if (stickerData != null){
                mLineView.setShowSelectBar(true);
                mCurrentSticker = stickerData;
                mLineView.setLeftBarPosition(((StickerDynamicData) stickerData).starTimeUs / (float)getEditorPlayer().getTotalTimeUs());
                mLineView.setRightBarPosition(((StickerDynamicData) stickerData).stopTimeUs / (float)getEditorPlayer().getTotalTimeUs());
                mCurrentColorRectView = mStickerImageBackups.findColorRect(stickerData);
            }
        }

        @Override
        public void onStickerItemViewReleased() {

        }

        @Override
        public void onCancelAllStickerSelected() {
            mLineView.setShowSelectBar(false);
            mCurrentColorRectView = null;
        }

        @Override
        public void onStickerCountChanged(StickerData stickerData, StickerItemViewInterface stickerItemViewInterface, int operation, int count) {

        }

        @Override
        public void onStickerCountChanged(StickerDynamicData stickerData, StickerItemViewInterface stickerItemViewInterface, int operation, int count) {
            if (stickerItemViewInterface.getStickerType() != StickerView.StickerType.Dynamic) return;
            if (operation == 0){
                mStickerImageBackups.removeBackupEntityWithSticker(((StickerDynamicItemView) stickerItemViewInterface));
                mLineView.setShowSelectBar(false);
            } else {
                mLineView.setShowSelectBar(true);
                float startPercent = mLineView.getCurrentPercent();
                float endPercent = ((StickerDynamicData) stickerData).stopTimeUs / (float) getEditorPlayer().getTotalTimeUs();
                TuSdkMovieColorRectView rectView = mLineView.recoverColorRect(R.color.lsq_scence_effect_color_EdgeMagic01,startPercent,endPercent);
                mCurrentColorRectView = rectView;
                mStickerImageBackups.addBackupEntity(EditorStickerImageBackups.createBackUpEntity(stickerData, ((StickerDynamicItemView) stickerItemViewInterface),rectView));
            }
        }
    };

    private TuSdkMovieColorGroupView.OnSelectColorRectListener mSelectColorListener = new TuSdkMovieColorGroupView.OnSelectColorRectListener() {
        @Override
        public void onSelectColorRect(final TuSdkMovieColorRectView rectView) {
            if (rectView == null){
                mLineView.setShowSelectBar(false);
                mStickerView.cancelAllStickerSelected();
            }
            if (mStickerView.getDynamicStickerItems().size() == 0) return;
            final StickerDynamicData stickerDynamicData = (StickerDynamicData) mStickerImageBackups.findStickerGroup(rectView);

            if (stickerDynamicData != null && stickerDynamicData instanceof StickerDynamicData){
                mLineView.setShowSelectBar(true);
                mCurrentSticker = stickerDynamicData;
                final float leftPercent = stickerDynamicData.starTimeUs / (float)getEditorPlayer().getTotalTimeUs();
                float rightPercent = stickerDynamicData.stopTimeUs / (float)getEditorPlayer().getTotalTimeUs();
                mLineView.setLeftBarPosition(leftPercent);
                mLineView.setRightBarPosition(rightPercent);

                if (mCurrentColorRectView == rectView) return;
                mCurrentColorRectView = rectView;
                ThreadHelper.post(new Runnable() {
                    @Override
                    public void run() {
                        mLineView.seekTo(rectView.getStartPercent() + 0/002f);
                    }
                });

                if (mStickerImageBackups.findStickerGroup(rectView) != null){
                    mStickerView.onStickerItemViewSelected(mStickerImageBackups.findDynamicStickerItem(rectView));
                    mStickerImageBackups.findDynamicStickerItem(rectView).setSelected(true);
                }

            }
        }
    };

    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
            if (mBottomView == null) return;
            setPlayState(state);
        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if (mBottomView == null || isAnimationStaring) return;
            mLineView.seekTo(percentage);
        }
    };

    private TuSdkMovieScrollView.OnProgressChangedListener mOnScrollingPlayPositionListener = new TuSdkMovieScrollView.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(float progress, boolean isTouching) {
            long playPositionTime = (long) (progress * getEditorPlayer().getTotalTimeUs());
            for (StickerItemViewInterface itemViewInterface : mStickerView.getStickerItems()) {
                if (itemViewInterface instanceof StickerImageItemView) {
                    StickerImageItemView itemView = (StickerImageItemView) itemViewInterface;
                    StickerImageData textData = (StickerImageData) itemView.getSticker();
                    if (textData.isContains(playPositionTime)) {
                        itemView.setVisibility(View.VISIBLE);
                    } else {
                        itemView.setVisibility(View.GONE);
                    }
                }else if (itemViewInterface instanceof StickerTextItemView){
                    StickerTextItemView itemView = (StickerTextItemView) itemViewInterface;
                    StickerTextData imageData = (StickerTextData) itemView.getSticker();
                    if(imageData.isContains(playPositionTime)){
                        itemView.setVisibility(View.VISIBLE);
                    }else {
                        itemView.setVisibility(GONE);
                    }
                } else if (itemViewInterface instanceof StickerDynamicItemView){
                    StickerDynamicItemView itemView = ((StickerDynamicItemView) itemViewInterface);
                    StickerDynamicData dynamicData = itemView.getCurrentStickerGroup();
                    itemView.updateStickers(System.currentTimeMillis());
                    if (dynamicData.isContains(playPositionTime)){
                        itemView.setVisibility(View.VISIBLE);
                    } else {
                        itemView.setVisibility(GONE);
                    }
                }
            }
            if(!isTouching)return;
            if(isTouching){
                getEditorPlayer().pausePreview();
            }

            if (getEditorPlayer().isPause())
                getEditorPlayer().seekOutputTimeUs(playPositionTime);
        }

        @Override
        public void onCancelSeek() {

        }
    };

    private TuSdkRangeSelectionBar.OnSelectRangeChangedListener mOnSelectTimeChangeListener = new TuSdkRangeSelectionBar.OnSelectRangeChangedListener() {
        @Override
        public void onSelectRangeChanged(float leftPercent, float rightPerchent, int type) {
            if (mCurrentSticker == null) return;
            if (type == 0){
                mCurrentSticker.starTimeUs = (long) (leftPercent * getEditorPlayer().getTotalTimeUs());
            } else if(type == 1) {
                mCurrentSticker.stopTimeUs = (long)(rightPerchent * getEditorPlayer().getTotalTimeUs());
            }
            mLineView.changeColorRect(mCurrentColorRectView,leftPercent,rightPerchent);
        }
    };

    private TuSdkRangeSelectionBar.OnTouchSelectBarListener mOnTouchSelectBarListener = new TuSdkRangeSelectionBar.OnTouchSelectBarListener() {
        @Override
        public void onTouchBar(float leftPercent, float rightPerchent, int type) {
            if(type == 0){
                mLineView.seekTo(leftPercent);
            }else if(type == 1) {
                mLineView.seekTo(rightPerchent);
            }
        }
    };


    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorDynamicStickerComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.DynamicSticker;
        mStickerView = getEditorController().getActivity().getImageStickerView();
        getEditorPlayer().addPreviewSizeChangeListener(mOnDisplayChangeListener);
        getEditorPlayer().addProgressListener(mPlayProgressListener);
        mStickerImageBackups = new EditorStickerImageBackups(mStickerView,getEditorEffector(),editorController.getImageTextRankHelper());

        mStickerView.setDelegate(mStickerDelegate);
    }

    @Override
    public void attach() {
        getEditorController().getActivity().getTextStickerView().setVisibility(View.VISIBLE);
        getEditorController().getBottomView().addView(getBottomView());
        getEditorController().getVideoContentView().setClickable(false);
        getEditorController().getPlayBtn().setVisibility(GONE);
        mStickerView.setDelegate(mStickerDelegate);
        mStickerView.changeOrUpdateStickerType(StickerView.StickerType.Dynamic);
    }

    @Override
    public void detach() {
        getEditorPlayer().seekOutputTimeUs(0);
        getEditorController().getPlayBtn().setVisibility(View.VISIBLE);
        getEditorController().getVideoContentView().setClickable(true);
        getEditorController().getActivity().getTextStickerView().setVisibility(GONE);

        mStickerImageBackups.onComponentDetach();
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        getEditorPlayer().seekOutputTimeUs(0);
        if(getEditorPlayer().isReversing()) {
            mLineView.seekTo(1f);
        }else {
            mLineView.seekTo(0f);
        }
    }

    @Override
    public View getHeaderView() {
        return null;
    }

    public EditorStickerImageBackups getStickerImageBackups(){
        return mStickerImageBackups;
    }

    private View initBottomView() {
        if (mBottomView == null){
            View bottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_dynamic_sticker_bottom,null);
            mBottomView = bottomView;
            mLineView = bottomView.findViewById(R.id.lsq_editor_sticker_play_range);
            mLineView.setOnSelectColorRectListener(mSelectColorListener);
            mLineView.setSelectRangeChangedListener(mOnSelectTimeChangeListener);
            mLineView.setOnTouchSelectBarListener(mOnTouchSelectBarListener);
            mLineView.setOnProgressChangedListener(mOnScrollingPlayPositionListener);
            float minPercent = minSelectTimeUs / (float) getEditorPlayer().getTotalTimeUs();
            mLineView.setMinWidth(minPercent);

            mLineView.setShowSelectBar(false);
            mLineView.setType(1);
            mStickerImageBackups.setLineView(mLineView);

            mPlayBtn = bottomView.findViewById(R.id.lsq_editor_sticker_play);
            mPlayBtn.setOnClickListener(mOnClickListener);

            mBackBtn = bottomView.findViewById(R.id.lsq_sticker_close);
            mBackBtn.setOnClickListener(mOnClickListener);
            mNextBtn = bottomView.findViewById(R.id.lsq_sticker_sure);
            mNextBtn.setOnClickListener(mOnClickListener);

            initStickerRecyclerView();

        }
        return mBottomView;
    }

    /**
     * 初始化Mv
     */
    private void initStickerRecyclerView() {
        mStickerRecyclerView = mBottomView.findViewById(R.id.lsq_sticker_recyclerView);
        mStickerRecyclerView.setLayoutManager(new LinearLayoutManager(getEditorController().getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mStickerRecyclerAdapter = new MvRecyclerAdapter(false);
        mStickerRecyclerAdapter.setItemClickListener(mvItemClickListener);
        mStickerRecyclerView.setAdapter(mStickerRecyclerAdapter);
        mStickerRecyclerAdapter.setMvModeList(getMvModeList());
    }

    /**
     * MV列表Item点击
     */
    private MvRecyclerAdapter.ItemClickListener mvItemClickListener = new MvRecyclerAdapter.ItemClickListener() {
        @Override
        public void onItemClick(final int position) {
            getEditorPlayer().pausePreview();
            StickerGroup stickerGroup = mStickerRecyclerAdapter.getMvModeList().get(position);
            StickerData data = stickerGroup.copy().stickers.get(0);
            StickerDynamicData dynamicData = new StickerDynamicData(data);

            //时间间隔为2s
            dynamicData.starTimeUs = (long) (mLineView.getCurrentPercent() * getEditorPlayer().getInputTotalTimeUs());
            if(dynamicData.starTimeUs + defaultDurationUs > getEditorPlayer().getInputTotalTimeUs() ){
                dynamicData.stopTimeUs = getEditorPlayer().getOutputTotalTimeUS();
            }else {
                dynamicData.stopTimeUs = dynamicData.starTimeUs + defaultDurationUs;
            }
            getEditorController().getActivity().getImageStickerView().appendSticker(dynamicData);
            getEditorPlayer().startPreview();
        }
    };

    /**
     * 获取Mv列表
     *
     * @return
     */
    private List<StickerGroup> getMvModeList() {
        /** 当前资源内的Id **/
        mMusicMap.put(1420, R.raw.lsq_audio_cat);
        mMusicMap.put(1427, R.raw.lsq_audio_crow);
        mMusicMap.put(1432, R.raw.lsq_audio_tangyuan);
        mMusicMap.put(1446, R.raw.lsq_audio_children);
        mMusicMap.put(1470, R.raw.lsq_audio_oldmovie);
        mMusicMap.put(1469, R.raw.lsq_audio_relieve);

        List<StickerGroup> groups = new ArrayList<StickerGroup>();
        List<StickerGroup> smartStickerGroups = StickerLocalPackage.shared().getSmartStickerGroups(false);

        for (StickerGroup smartStickerGroup : smartStickerGroups) {
            if (mMusicMap.containsKey((int) smartStickerGroup.groupId))
                groups.add(smartStickerGroup);
        }
        return groups;
    }

    public void backUpDatas(){
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediEffectDataTypeStickerImage);
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeText);
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeDynamicSticker);
        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mStickerImageBackups == null)return;
                mStickerImageBackups.onDynamicComponentAttach();
            }
        },50);

    }

    @Override
    public View getBottomView() {
        if (mBottomView == null){
            mBottomView = initBottomView();
        }
        return mBottomView;
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {
        getBottomView();
        mLineView.addBitmap(bitmap);
    }

    /**
     * 设置播放状态
     *
     * @param state 0 播放  1 暂停
     * @since V3.0.0
     */
    public void setPlayState(int state) {
        if(state == 1){
            getEditorPlayer().pausePreview();
        }
        else {
            mStickerView.cancelAllStickerSelected();
            getEditorPlayer().startPreview();
        }

        mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_sticker_close:
                    mStickerImageBackups.onBackEffect();
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_sticker_sure:
                    mStickerImageBackups.onApplyEffect();
                    handleCompleted();
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_editor_sticker_play:
                    if (getEditorPlayer().isPause())
                        getEditorPlayer().startPreview();
                    else
                        getEditorPlayer().pausePreview();
                    break;
                default:
                    break;
            }
        }
    };

    private void handleCompleted() {
        for (StickerItemViewInterface stickerItem : getEditorController().getActivity().getImageStickerView().getStickerItems()){
            if (stickerItem instanceof StickerDynamicItemView){
                StickerDynamicItemView stickerItemView = ((StickerDynamicItemView) stickerItem);
                float scale = stickerItemView.getCurrentScale();
                TuSdkSize scaleSize = stickerItemView.getRenderScaledSize();
                float degree = stickerItemView.getCurrentDegree();
                stickerItemView.resetRotation();
                StickerView stickerView = getEditorController().getActivity().getImageStickerView();
                float renderWidth = mCurrentPreviewSize.width;
                float renderHeight = mCurrentPreviewSize.height;
                int[] locaiont = new int[2];
                /** 当SDKVersion >= 27 需要使用 getLocationInWindow() 方法 不然会产生极大的误差 小于27时 getLocationInWindow() 与 getLocationOnScreen()方法返回值相同*/
                stickerItemView.getRenderView().getLocationInWindow(locaiont);
                int[] parentLocaiont = new int[2];
                stickerView.getLocationInWindow(parentLocaiont);
                float pointX = locaiont[0] - parentLocaiont[0];
                float pointY = (locaiont[1] - parentLocaiont[1]);
                StickerDynamicData dynamicData = stickerItemView.getCurrentStickerGroup();
                //进行归一化操作
                StickerPositionInfo info = dynamicData.getStickerData().positionInfo;
                info.offsetX = pointX / (float)renderWidth;
                info.offsetY = pointY / (float)renderHeight;
                info.stickerWidth = scaleSize.width / (float)renderWidth;
                info.stickerHeight = scaleSize.height / (float)renderHeight;
                info.scale = scale;
                info.rotation = degree;
                info.posType = StickerPositionInfo.StickerPositionType.StickerPosDynamic.getValue();
                dynamicData.getStickerData().positionInfo = info;
                long starTimeUs = dynamicData.starTimeUs;
                long stopTimeUs = dynamicData.stopTimeUs;
                TuSdkMediaLiveStickerEffectData effectData = new TuSdkMediaLiveStickerEffectData(dynamicData);
                effectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(starTimeUs,stopTimeUs));
                getEditorEffector().addMediaEffectData(effectData);
                EditorStickerImageBackups.DynamicStickerBackupEntity backupEntity = mStickerImageBackups.findDynamicStickerBackupEntityByMemo(stickerItemView);
                if (backupEntity != null){
                    backupEntity.effectData = effectData;
                }
                stickerItemView.restoreRotation();
                stickerItemView.setVisibility(GONE);
            } else if (stickerItem instanceof StickerTextItemView){
                StickerTextItemView stickerItemView = ((StickerTextItemView) stickerItem);
                EditorTextBackups.TextBackupEntity backupEntity = getEditorController().getTextComponent().getTextBackups().findTextBackupEntityToMemo(stickerItemView);
                if(backupEntity != null)
                    getEditorEffector().addMediaEffectData(backupEntity.textMediaEffectData);
                stickerItemView.setVisibility(GONE);
            }else if(stickerItem instanceof StickerImageItemView){
                StickerImageItemView imageItemView = (StickerImageItemView) stickerItem;
                EditorStickerImageBackups.StickerImageBackupEntity stickerImageBackupEntity = getEditorController().getStickerComponent().getStickerImageBackups().findTextBackupEntityByMemo(imageItemView);
                getEditorEffector().addMediaEffectData(stickerImageBackupEntity.stickerImageMediaEffectData);
                imageItemView.setVisibility(GONE);
            }
        }

        //清空重置相关数据
        getEditorController().getActivity().getImageStickerView().cancelAllStickerSelected();
        getEditorController().getActivity().getImageStickerView().removeAllSticker();
    }
}
