package org.lasque.tusdkvideodemo.views.editor.playview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdkvideodemo.views.editor.playview.rangeselect.TuSdkMovieColorGroupView;
import org.lasque.tusdkvideodemo.views.editor.playview.rangeselect.TuSdkMovieColorRectView;

/**
 * 视频播放滚动视图
 *
 * @author MirsFang
 */
@TargetApi(Build.VERSION_CODES.M)
public class TuSdkMovieScrollView extends HorizontalScrollView implements View.OnScrollChangeListener {
    private static final String TAG = "MoviePlayScrollView";
    private TuSdkMovieScrollContent mMovieScrollContentView;
    private OnProgressChangedListener mProgressChangedListener;
    /** 当前进度 **/
    private float mPercent;
    /** 0不带bar  1带bar */
    private int mType = 0;
    /** 时间特效的类型  0 正常   1 倒序 **/
    private int mTimeEffectType = 0;
    /** 是否正在添加一个颜色区间 **/
    private boolean isAddColorRect;
    /** 是否正在触摸中 **/
    private boolean isTouching;
    private boolean isSetLayoutWidth;


    /** 进度改变回调 */
    public static interface OnProgressChangedListener {
        void onProgressChanged(float progress, boolean isTouching);
    }

    public TuSdkMovieScrollView(Context context) {
        super(context);
        init(context);
    }

    public TuSdkMovieScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mMovieScrollContentView = new TuSdkMovieScrollContent(context);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setScrollBarSize(0);
        setHorizontalScrollBarEnabled(false);
        setOnScrollChangeListener(this);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!isSetLayoutWidth) {
                    ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(getWidth(),getHeight());
                    addView(mMovieScrollContentView,layoutParams1);
                    isSetLayoutWidth = true;
                }
                if (mType == 1) {
                    TuSdkRangeSelectionBar rangeSelectionBar = (TuSdkRangeSelectionBar) mMovieScrollContentView.getChildAt(2);
                    int barWidth = TuSdkContext.dip2px(15);
                    if (rangeSelectionBar != null) barWidth = rangeSelectionBar.getBarWidth();
                    mMovieScrollContentView.setPadding(getWidth() / 2 - getPaddingLeft() - barWidth, 0, getWidth() / 2 - getPaddingRight() - barWidth, 0);
                } else {
                    mMovieScrollContentView.setPadding(getWidth() / 2 - getPaddingLeft(), 0, getWidth() / 2 - getPaddingRight(), 0);
                }
            }
        });
    }


    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        mPercent = scrollX / (float) mMovieScrollContentView.getTotalWidth();
        if (mProgressChangedListener != null)
            mProgressChangedListener.onProgressChanged(mPercent, isTouching);
        if (mMovieScrollContentView != null && isAddColorRect)
            mMovieScrollContentView.updateScrollPercent(mPercent);
    }

    /**
     * 添加一张图片
     *
     * @param bitmap bitmap图片
     */
    public void addBitmap(Bitmap bitmap) {
        mMovieScrollContentView.addBitmap(bitmap);
    }

    /**
     * 设置进度改变回调
     *
     * @param progressChangedListener
     */
    public void setProgressChangedListener(OnProgressChangedListener progressChangedListener) {
        this.mProgressChangedListener = progressChangedListener;
    }

    /**
     * 跳转到指定进度
     *
     * @param percent 进度
     */
    public void seekTo(float percent) {
        smoothScrollTo((int) (mMovieScrollContentView.getTotalWidth() * percent), 0);
    }

    /**
     * 获取当前进度
     *
     * @return 当前进度百分比
     */
    public float getCurrentPercent() {
        return mPercent;
    }


    /**
     * 设置类型
     *
     * @param type 0 不带Bar  1带bar
     */
    public void setType(int type) {
        this.mType = type;
        mMovieScrollContentView.setType(type);
    }

    /** 添加一个颜色色块 **/
    public void startAddColorRect(@ColorRes int colorId) {
        TuSdkMovieColorRectView rectView = new TuSdkMovieColorRectView(getContext());
        rectView.setDrawDirection(mTimeEffectType);
        rectView.setColorId(colorId);
        rectView.setStartPercent(mPercent);
        isAddColorRect = true;
        mMovieScrollContentView.addColorRect(rectView);
    }

    /**
     * 回复一个色块
     *
     * @param colorId
     * @param startPercent
     * @param endPercent
     */
    public TuSdkMovieColorRectView recoverColorRect(int colorId, float startPercent, float endPercent) {
        TuSdkMovieColorRectView rectView = new TuSdkMovieColorRectView(getContext());
        rectView.setDrawDirection(mTimeEffectType);
        rectView.setColorId(colorId);
        rectView.setStartPercent(startPercent);
        int width = (int) ((endPercent - startPercent) * mMovieScrollContentView.getTotalWidth());
        rectView.setWidth(width);
        mMovieScrollContentView.addColorRect(rectView);
        return rectView;
    }

    /**
     * 改变一个色块的位置
     * @param rectView 当前色块的实体
     * @param startPercent  开始进度
     * @param endPercent    结束进度
     */
    public void changeColorRect(TuSdkMovieColorRectView rectView,float startPercent, float endPercent){
        if(!mMovieScrollContentView.isContain(rectView)){
            TLog.e("%s this rect is not contain : start : %s  end : %s",TAG,startPercent,endPercent);
            return;
        }
        rectView.setDrawDirection(mTimeEffectType);
        rectView.setStartPercent(startPercent);
        int width = (int) ((endPercent - startPercent) * mMovieScrollContentView.getTotalWidth());
        rectView.setWidth(width);
        rectView.postInvalidate();
    }


    /** 取消添加一个颜色色块 **/
    public void endAddColorRect() {
        isAddColorRect = false;
    }

    /** 删除一个颜色色块 **/
    public void deletedColorRect() {
        TuSdkMovieColorRectView rectView = mMovieScrollContentView.deletedColorRect();
        if (rectView == null) return;
        seekTo(rectView.getStartPercent());
    }

    public void deletedColorRect(TuSdkMovieColorRectView rectView) {
        mMovieScrollContentView.deletedColorRect(rectView);
    }

    /** 设置区间选择回调 **/
    public void setSelectRangeChangedListener(TuSdkRangeSelectionBar.OnSelectRangeChangedListener changedListener) {
        mMovieScrollContentView.setSelectRangeChangedListener(changedListener);
    }

    /** 设置区间临界值回调 **/
    public void setExceedCriticalValueListener(TuSdkRangeSelectionBar.OnExceedCriticalValueListener exceedValueListener) {
        mMovieScrollContentView.setExceedCriticalValueListener(exceedValueListener);
    }

    public void setOnTouchSelectBarListener(TuSdkRangeSelectionBar.OnTouchSelectBarListener onTouchSelectBarListener){
        mMovieScrollContentView.setOnTouchSelectBarListener(onTouchSelectBarListener);
    }

    /** 设置颜色选择监听 **/
    public void setOnSelectColorRectListener(TuSdkMovieColorGroupView.OnSelectColorRectListener onSelectColorRectListener){
        mMovieScrollContentView.setOnSelectColorRectListener(onSelectColorRectListener);
    }
    /**
     * 设置时间特效类型
     *
     * @param timeEffectType 0 正常   1 倒序
     */
    public void setTimeEffectType(int timeEffectType) {
        this.mTimeEffectType = timeEffectType;
    }

    /**
     * 最小区间占比
     **/
    public void setMinWidth(float minPercent) {
        mMovieScrollContentView.setMinWidth(minPercent);
    }

    /**
     * 最大区间占比
     **/
    public void setMaxWidth(float maxPercent) {
        mMovieScrollContentView.setMaxWidth(maxPercent);
    }

    /**
     * 移动左边Bar的位置
     **/
    public void setLeftBarPosition(float percent) {
        mMovieScrollContentView.setLeftBarPosition(percent);
    }

    /**
     * 移动右边Bar的位置
     **/
    public void setRightBarPosition(float percent) {
        mMovieScrollContentView.setRightBarPosition(percent);
    }

    /** 获取左边Bar的进度 **/
    public float getLeftBarPercent() {
        return mMovieScrollContentView.getLeftBarPercent();
    }

    /** 获取右边Bar的进度 **/
    public float getRightBarPercent() {
        return mMovieScrollContentView.getRightBarPercent();
    }

    public boolean isShowSelectBar() {
        return mMovieScrollContentView.isShowSelectBar();
    }

    public void setShowSelectBar(boolean showSelectBar) {
        mMovieScrollContentView.setShowSelectBar(showSelectBar);
    }


    public void clearAllColorRect() {
        mMovieScrollContentView.clearAllColorRect();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isTouching = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isTouching = false;
                break;
        }
        return super.onTouchEvent(ev);
    }
}
