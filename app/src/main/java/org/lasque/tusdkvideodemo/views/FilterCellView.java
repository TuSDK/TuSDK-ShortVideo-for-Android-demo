/**
 * TuSDKVideoDemo
 * FilterCellView.java
 *
 * @author  Yanlin
 * @Date  Jan 5, 2017 6:06:07 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo.views;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.tusdk.FilterLocalPackage;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.tusdk.core.view.listview.TuSdkCellRelativeLayout;
import org.lasque.tusdkvideodemo.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Yanlin
 *
 */
public class FilterCellView extends TuSdkCellRelativeLayout<String>
{
	/** 缩略图 */
	private TuSdkImageView mThumbView;

	/** 滤镜名称*/
	private TextView mTitlebView;
	
	/** 滤镜边框*/
	private View mFilterBorderView ;
	
	// 标记该滤镜项是否被选中
	private int flag = -1;

	public FilterCellView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public FilterCellView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public FilterCellView(Context context)
	{
		super(context);
	}
	
	@Override
	protected void bindModel() 
	{
		String filterCode = this.getModel();
		
		if (filterCode == null) return;
		
		filterCode = filterCode.toLowerCase();
		
		String filterImageName = "lsq_filter_thumb_" + filterCode;
		
		Bitmap filterImage = TuSdkContext.getRawBitmap(filterImageName);
		
		if (this.getImageView() != null && filterImage != null)
		{
			getImageView().setImageBitmap(filterImage);
		}
		
		if (this.getTitleView() != null)
		{
			getTitleView().setText(TuSdkContext.getString("lsq_filter_" + filterCode));
		}
		
		RelativeLayout layout =  this.findViewById(R.id.lsq_none_layout);
		ImageView imageView = this.findViewById(R.id.lsq_item_none);
		if (layout != null)
		{
			layout.setVisibility(((Integer)getTag() == 0)?View.VISIBLE:View.GONE);
			imageView.setVisibility(((Integer)getTag() == 0)?View.VISIBLE:View.GONE);
		}
	}
	
	public TuSdkImageView getImageView()
	{
		if (mThumbView == null)
		{
			mThumbView = findViewById(R.id.lsq_item_image);
		}
		return mThumbView;
	}
	
	public View getBorderView()
	{
		if (mFilterBorderView == null)
		{
			 mFilterBorderView = findViewById(R.id.lsq_item_border);
		}
		return mFilterBorderView;
	}
	
	public TextView getTitleView()
	{
		if (mTitlebView == null)
		{
			mTitlebView = findViewById(R.id.lsq_item_title);
		}
		return mTitlebView;
	}

	public void viewNeedRest()
	{
		super.viewNeedRest();
		
		if (this.getImageView() != null)
		{
			this.getImageView().setImageBitmap(null);

			FilterLocalPackage.shared().cancelLoadImage(getImageView());
		}
	}
	
	@Override
	protected void onLayouted()
	{
		super.onLayouted();
		if (this.getImageView() != null)
		{
	
			this.getImageView().setCornerRadiusDP(4);
		}
	}
	
	/**
	 * 标记该滤镜项是否被选中
	 * 
	 * @param flag
	 */
	public void setFlag(int flag)
	{
		this.flag = flag;
	}
	
	/**
	 * 标记该滤镜项是否被选中
	 * 
	 * @return
	 */
	public int getFlag()
	{
		return this.flag;
	}
}
