package org.lasque.tusdkvideodemo.views;

import android.content.Context;
import android.util.AttributeSet;

import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import org.lasque.tusdk.video.editor.TuSDKMovieEditor;
import org.lasque.tusdkvideodemo.R;
import org.lasque.tusdkvideodemo.utils.Constants;

import java.util.Arrays;

/**
 * Created by sprint on 26/12/2017.
 */

public class SceneEffectLayout extends TuSdkRelativeLayout
{
    private TuSDKMovieEditor mMovieEditor;

    /** 场景特效时间轴视图 */
    private EffectsTimelineView mSceneEffectsTimelineView;

    /** 场景特效列表视图 */
    private SceneEffectListView mListView;

    public SceneEffectLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

    }

    /**
     * 加载视图
     */
    @Override
    public void loadView()
    {
        super.loadView();

        // 动画时间轴视图
        mSceneEffectsTimelineView = getViewById(R.id.lsq_scence_effect_timelineView);
        mSceneEffectsTimelineView.setDelegate(mEffectsTimelineViewDelegate);

        /** 场景特效列表视图 */
        mListView = getViewById(R.id.lsq_scene_effect_list_view);
        mListView.loadView();
        mListView.setModeList(Arrays.asList(Constants.SCENE_EFFECT_CODES));
    }

    public SceneEffectListView getSceneEffectListView()
    {
        return mListView;
    }

    /**
     * 场景特效时间轴视图
     *
     * @return
     */
    public EffectsTimelineView getSceneEffectsTimelineView()
    {
        return mSceneEffectsTimelineView;
    }

    /**
     * 设置当前是否可以编辑
     *
     * @param editable
     */
    public void setEditable(boolean editable)
    {
        mSceneEffectsTimelineView.setEditable(editable);
    }

    /**
     * 设置场景特效列表委托对象
     *
     * @param delegate
     */
    public void setDelegate(SceneEffectListView.SceneEffectListViewDelegate delegate)
    {
        mListView.setDelegate(delegate);
    }

    /**
     * 设置MovieEditor
     * @param movieEditor
     */
    public void setMovieEditor(TuSDKMovieEditor movieEditor)
    {
        this.mMovieEditor = movieEditor;
    }

    /** 场景特效时间轴变化 */
    protected EffectsTimelineView.EffectsTimelineViewDelegate mEffectsTimelineViewDelegate = new EffectsTimelineView.EffectsTimelineViewDelegate()
    {
        @Override
        public void onProgressCursorWillChaned()
        {
            mMovieEditor.pausePreview();
        }

        @Override
        public void onProgressChaned(final float progress)
        {
            mMovieEditor.seekTimeUs((long)(mMovieEditor.getVideoInfo().durationTimeUs * progress));
        }

        @Override
        public void onEffectNumChanged(int effectNum)
        {
            mListView.updateUndoButtonState(effectNum == 0 ? false :true);
        }
    };
}
