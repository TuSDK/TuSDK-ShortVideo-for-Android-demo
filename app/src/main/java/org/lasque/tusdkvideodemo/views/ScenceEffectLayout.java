package org.lasque.tusdkvideodemo.views;

import android.content.Context;
import android.util.AttributeSet;

import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import org.lasque.tusdkvideodemo.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sprint on 26/12/2017.
 */

public class ScenceEffectLayout extends TuSdkRelativeLayout
{
    /** 场景特效时间轴视图 */
    private SceneEffectsTimelineView mSceneEffectsTimelineView;

    /** 场景特效列表视图 */
    private SceneEffectListView mListView;

    public ScenceEffectLayout(Context context, AttributeSet attrs)
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

        /** 场景特效列表视图 */
        mListView = getViewById(R.id.lsq_scene_effect_list_view);
        mListView.loadView();
        mListView.setModeList(createEffectDataList());

    }

    /**
     * 场景特效时间轴视图
     *
     * @return
     */
    public SceneEffectsTimelineView getSceneEffectsTimelineView()
    {
        return mSceneEffectsTimelineView;
    }

    /**
     * 场景特效code
     *
     * @return
     */
    private List<SceneEffectListView.SceneEffectData> createEffectDataList()
    {
        return Arrays.asList(
                new SceneEffectListView.SceneEffectData("None"),
                new SceneEffectListView.SceneEffectData("LiveShake01"),
                new SceneEffectListView.SceneEffectData("LiveMegrim01"),
                new SceneEffectListView.SceneEffectData("EdgeMagic01"),
                new SceneEffectListView.SceneEffectData("LiveFancy01_1"),
                new SceneEffectListView.SceneEffectData("LiveSoulOut01"),
                new SceneEffectListView.SceneEffectData("LiveSignal01")
        );
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
     * 设置场景特效时间轴动画委托
     *
     * @param delegate
     */
    public void setDelegate(SceneEffectsTimelineView.SceneEffectsTimelineViewDelegate delegate)
    {
        mSceneEffectsTimelineView.setDelegate(delegate);
    }
}
