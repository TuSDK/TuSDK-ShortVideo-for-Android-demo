package org.lasque.tusdkvideodemo.views.editor.playview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.lasque.tusdk.core.utils.TLog;


/**
 * 视频封面列表控件
 * @author MirsFang
 */
public class TuSdkMovieCoverListView extends LinearLayout {

    private static final String TAG = "MovieCoverList";
    /** 图片的张数 (默认20张) **/
    private int mImageCount = 20;
    /** 当前图片的数量 **/
    private int mCurrentImageCount = 0;
    /** 每张图片的宽度 **/
    private int mImageWidth;
    /** 每张图片的高度 **/
    private int mImageHeight;
    /** 视图的宽度 **/
    private int mViewWidth;

    public TuSdkMovieCoverListView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
    }

    public TuSdkMovieCoverListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);


        //计算图片显示的宽高
        mViewWidth = viewWidth - getPaddingLeft() - getPaddingRight();
        mImageWidth = mViewWidth/mImageCount ;
        mImageHeight = viewHeight;

        for (int i = 0; i < getChildCount(); i++) {
            ImageView imageView = (ImageView) getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) imageView.getLayoutParams();
            if(i == getChildCount() - 1){
                int diffWidth = mViewWidth - mImageCount * mImageWidth;
                if( diffWidth > 0){
                    layoutParams.width = mImageWidth + diffWidth;
                }
            }else {
              layoutParams.width = mImageWidth;
            }
            layoutParams.height = mImageHeight;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    /** 设置图片数量  **/
    public void setBitmapCount(int imageCount){
        this.mImageCount = imageCount;
    }

    /** 添加图片 **/
    public synchronized void addBitmap(Bitmap bitmap){
        if(mCurrentImageCount >= mImageCount )return;
        mCurrentImageCount++;
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.addView(imageView,mImageWidth,mImageHeight);
    }

    /**
     * 获取图片的总长度
     * @return
     */
    public int getTotalWidth(){
        return mViewWidth;
    }

}

