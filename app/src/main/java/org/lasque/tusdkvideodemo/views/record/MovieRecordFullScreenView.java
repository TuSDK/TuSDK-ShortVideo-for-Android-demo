/**
 * TuSDKVideoDemo 
 * MovieRecordView.java
 * 
 * @author Bonan
 * @Date: 2017-5-8 上午10:42:48
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 * 
 */
package org.lasque.tusdkvideodemo.views.record;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import org.lasque.tusdkvideodemo.R;

/**
 * 断点续拍全屏录制视图
 */
public class MovieRecordFullScreenView extends MovieRecordView
{
	/** 不透明度为1 */
	private final int MAX_ALPHA = 255;

	/** 透明度比例 */
	private final float ALPHA_RATIO = 0.4f;

	public MovieRecordFullScreenView(Context context)
	{
		super(context);
	}

	public MovieRecordFullScreenView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.movie_record_full_screen_view;
	}

	@Override
	protected void init(Context context)
	{
		super.init(context);

		updateButtonStatus(mRollBackButton, false);
		updateButtonStatus(mConfirmButton, false);
	}

	/** ----------------------- 修改按钮的透明度 ------------------------------------------------**/

	@Override
	protected void updateButtonStyle(TuSdkTextButton button, int imgId, int colorId, boolean clickable)
	{
		Drawable drawable = TuSdkContext.getDrawable(imgId);
		switch (button.getId())
		{
			case R.id.lsq_confirmWrap:
			case R.id.lsq_backWrap:
				if (!clickable)
					drawable.setAlpha((int) (MAX_ALPHA * ALPHA_RATIO));
				else
					drawable.setAlpha(MAX_ALPHA);
				break;
			default:
				break;
		}

		button.setCompoundDrawables(null, drawable, null, null);
		button.setTextColor(TuSdkContext.getColor(colorId));
	}

	/** ----------------------- 替换资源文件 ------------------------------------------------**/

	@Override
	protected int getFilterSelectedDrawable()
	{
		return R.drawable.lsq_filter_full;
	}

	@Override
	protected int getFilterUnselectedDrawable()
	{
		return R.drawable.lsq_filter_full;
	}

	@Override
	protected int getStickerSelectedDrawable()
	{
		return R.drawable.lsq_sticker_full;
	}

	@Override
	protected int getStickerUnselectedDrawable()
	{
		return R.drawable.lsq_sticker_full;
	}

	@Override
	protected int getConfirmSelectedDrawable()
	{
		return R.drawable.lsq_finish_full;
	}

	@Override
	protected int getConfirmUnselectedDrawable()
	{
		return R.drawable.lsq_finish_full;
	}

	@Override
	protected int getCancelSelectedDrawable()
	{
		return R.drawable.lsq_cancel_full;
	}

	@Override
	protected int getCancelUnselectedDrawable()
	{
		return R.drawable.lsq_cancel_full;
	}

	@Override
	protected int getRecordSelectedDrawable()
	{
		return R.drawable.lsq_recording_full;
	}

	@Override
	protected int getRecordUnselectedDrawable()
	{
		return R.drawable.lsq_record_pause_full;
	}

	@Override
	protected int getFlashSelectedDrawable()
	{
		return R.drawable.lsq_lamp_on_full;
	}

	@Override
	protected int getFlashUnselectedDrawable()
	{
		return R.drawable.lsq_lamp_off_full;
	}
}