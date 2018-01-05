package org.lasque.tusdkvideodemo.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import org.lasque.tusdkvideodemo.R;


/**
 * Created by sprint on 26/12/2017.
 */

/**
 * 场景特效列表
 */
public class SceneEffectListView extends TuSdkTableView<SceneEffectListView.SceneEffectData, SceneEffectCellView> implements TuSdkTableView.TuSdkTableViewItemClickDelegate<SceneEffectListView.SceneEffectData, SceneEffectCellView>
{
    private static final int MIN_PRESS_DURATION_MILLIS = 200;
    private Handler mHandler = new Handler();

    /** 委托对象 */
    private SceneEffectListViewDelegate mDelegate;


    public SceneEffectListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setDelegate(SceneEffectListViewDelegate delegate)
    {
        this.mDelegate = delegate;
    }

    @Override
    public void loadView()
    {
        super.loadView();

        this.setCellLayoutId(R.layout.movie_editor_scence_effect_cell_view);

        this.setItemClickDelegate(this);
        this.reloadData();
    }

    /**
     * 视图创建
     *
     * @param view
     *            创建的视图
     * @param parent
     *            父对象
     * @param viewType
     *            视图类型
     */
    @Override
    protected void onViewCreated(SceneEffectCellView view, ViewGroup parent, int viewType)
    {

    }

    /**
     * 绑定视图数据
     *
     * @param view
     *            创建的视图
     * @param position
     *            索引位置
     */
    @Override
    protected void onViewBinded(SceneEffectCellView view, final int position)
    {
        view.setTag(position);

        if (position == 0) return;


        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:

                        // 处理按下
                        handlePressEvent(position);

                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_POINTER_UP:

                        // 处理释放
                        handleReleaseEvent(position);

                        break;

                }

                return true;
            }
        });
    }

    /**
     * 处理按下场景特效事件
     *
     * @param position
     */
    long mDuration = 0;
    private void handlePressEvent(final  int position)
    {
        mHandler.removeCallbacksAndMessages(null);

        mDuration =  System.currentTimeMillis();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if (mDelegate != null)
                    mDelegate.onPressSceneEffect(getModeList().get(position));
            }
        },MIN_PRESS_DURATION_MILLIS);
    }

    /**
     * 处理释放场景特效事件
     *
     * @param position
     */
    private void handleReleaseEvent(int position)
    {
        if (System.currentTimeMillis() - mDuration  < MIN_PRESS_DURATION_MILLIS)
        {
            mHandler.removeCallbacksAndMessages(null);
            return;
        }

        if (mDelegate != null)
            mDelegate.onReleaseSceneEffect(getModeList().get(position));
    }

    @Override
    public void onTableViewItemClick(SceneEffectData effectData, SceneEffectCellView itemView, int position)
    {
        if (position == 0 && mDelegate != null)
                mDelegate.onUndo();
    }



    /**  SceneEffectListViewDelegate 委托 */
    public static interface SceneEffectListViewDelegate
    {
        /**
         * 撤销事件
         */
        void onUndo();

        /**
         * 按下特效
         *
         * @param effectData
         *          特效数据
         */
        void onPressSceneEffect(SceneEffectData effectData);

        /**
         * 释放特效
         *
         * @param effectData
         *          特效数据
         */
        void onReleaseSceneEffect(SceneEffectData effectData);

    }

    /**
     * SceneEffectData
     */
    public static class SceneEffectData
    {
        private int mColor;
        private String mEffectCode;

        public SceneEffectData(int color, String effectCode)
        {
            this.mColor = color;
            this.mEffectCode = effectCode;
        }

        public SceneEffectData(String effectCode)
        {
            this.mEffectCode = effectCode;
            this.mColor =  TuSdkContext.getColor(TuSdkContext.getColorResId("lsq_scence_effect_color_"+effectCode));
        }

        public String getTitle()
        {
            return TuSdkContext.getString("lsq_filter_"+mEffectCode);
        }

        public String getEffectCode()
        {
            return mEffectCode;
        }

        public int getColor()
        {
            return mColor;
        }

        public int getImageResourceId()
        {
            return TuSdkContext.getDrawableResId("lsq_scence_effect_"+mEffectCode.toLowerCase());
        }

    }

}
