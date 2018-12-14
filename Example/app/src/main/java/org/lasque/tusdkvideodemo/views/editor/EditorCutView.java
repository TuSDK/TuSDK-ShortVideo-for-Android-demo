package org.lasque.tusdkvideodemo.views.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdkvideodemo.R;
import org.lasque.tusdkvideodemo.views.editor.ruler.RulerView;

import java.util.ArrayList;
import java.util.List;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/21 15:21
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 编辑里选取裁剪的View
 */
public class EditorCutView extends FrameLayout {
    private View mContentView;
    //时间选择
    private LineView mRangeView;
    //刻度
    private RulerView mRulerView;
    private TextView mTimeRangView;
    //最小裁剪时间
    private long mMinCutTimeUs =  3 * 1000000;
    private boolean isEnable = true;


    public EditorCutView(@NonNull Context context) {
        super(context);
        init();
    }

    public EditorCutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /** 获取布局Id */
    protected int getLayoutId() {
        return R.layout.editor_cut_view;
    }

    /** 初始化View */
    private void init() {
        mContentView = LayoutInflater.from(getContext()).inflate(getLayoutId(), null);
        mRangeView = mContentView.findViewById(R.id.lsq_range_line);
        mRangeView.setInitType(LineView.LineViewType.DrawPointer,getResources().getColor(R.color.lsq_color_white));
        mRulerView = mContentView.findViewById(R.id.lsq_rule_view);
        mTimeRangView = mContentView.findViewById(R.id.lsq_range_time);
        addView(mContentView);
    }

    public void loadView(){
        mRangeView.loadView();
    }

    private int mTwoBarsDistance = 0;
    public void setTwoBarsMinDistance(int twoBarsDistance){
        this.mTwoBarsDistance = twoBarsDistance;
        mRangeView.setTwoBarsMinDistance(twoBarsDistance);
    }

    public void setMinCutTimeUs(long timeUs){
        mRangeView.setMinSelectTimeUs(timeUs);
    }

    /**
     * 设置时间区间
     * @param times
     */
    public void setRangTime(float times){
        String rangeTime = String.format("%s %.1f %s",getResources().getString(R.id.lsq_movie_cut_selecttime),times,"s");
        mTimeRangView.setText(rangeTime);
    }

    /**
     * 设置封面图
     * @param coverList 封面图列表
     */
    public void setCoverList(List<Bitmap> coverList){
        if(coverList == null){
            TLog.e(" bitmap list of cover is null !!!");
            return;
        }
        mRangeView.setBitmapList(coverList);
        mRangeView.setMinSelectTimeUs(mMinCutTimeUs);
    }

    /**
     * 设置视频的总时长
     * @param totalTimeUs
     */
    public void setTotalTime(long totalTimeUs){
        if(totalTimeUs <= 0)
        {
            TLog.e(" video time length mast > 0  !!!");
            return;
        }
        mRangeView.setTotalTimeUs(totalTimeUs);
        mRulerView.setMaxValueAndPaintColor(totalTimeUs, getResources().getColor(R.color.lsq_color_white));
    }

    /**
     * 设置选择区间回调
     * @param onSelectTimeChangeListener
     */
    public void setOnSelectCeoverTimeListener(LineView.OnSelectTimeChangeListener onSelectTimeChangeListener){
        if(onSelectTimeChangeListener == null)
        {
            TLog.e("setSelectCoverTimeListener is null !!!");
            return;
        }
        mRangeView.setOnSelectTimeChangeListener(onSelectTimeChangeListener);
    }

    /**
     * 播放指针 位置改变监听
     * @param onPlayPointerChangeListener
     */
    public void setOnPlayPointerChangeListener(LineView.OnPlayPointerChangeListener onPlayPointerChangeListener){
        if(onPlayPointerChangeListener == null){
            TLog.e("setSelectCoverTimeListener is null !!!");
            return;
        }
        mRangeView.setOnPlayPointerChangeListener(onPlayPointerChangeListener);
    }


    /**
     * 设置播放进度
     * @param percent 播放进度的百分比
     */
    public void setVideoPlayPercent(float percent){
        if(mRangeView == null) return;;
        if(percent < 0){
            TLog.e("setSelectCoverTimeListener is null !!!");
            return;
        }
        mRangeView.pointerMoveToPercent(percent);
    }

    public LineView getLineView(){
        return mRangeView;
    }

    public void addBitmap(Bitmap bitmap) {
        mRangeView.addBitmap(bitmap);
    }

    /** 设置是否启用 **/
    public void setEnable(boolean isEnable){
        this.isEnable = isEnable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !isEnable;
    }
}
