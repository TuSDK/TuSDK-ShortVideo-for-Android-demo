package org.lasque.tusdkvideodemo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.lasque.tusdk.core.view.listview.TuSdkCellRelativeLayout;
import org.lasque.tusdk.core.view.listview.TuSdkListSelectableCellViewInterface;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import org.lasque.tusdkvideodemo.R;

/**
 * Created by sprint on 26/12/2017.
 */

public class SceneEffectCellView extends TuSdkCellRelativeLayout<SceneEffectListView.SceneEffectData>
{

    /** 缩略图 */
    private ImageView mThumbView;
    /** 场景特效名称 */
    private TextView mTitleView;
    /** 撤销按钮 */
    private TuSdkTextButton mUndoButton;

    public SceneEffectCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void bindModel()
    {

        SceneEffectListView.SceneEffectData scenceEffectData = getModel();

        getImageView().setImageResource(scenceEffectData.getImageResourceId());
        getTitleView().setText(scenceEffectData.getTitle());

        // 当前是否为撤销 Cell
        boolean isUndoCell = (Integer) getTag() == 0;

        findViewById(R.id.lsq_scence_effect_cell_layout).setVisibility( isUndoCell ? View.GONE : View.VISIBLE);
        getUndoButton().setVisibility(isUndoCell ? View.VISIBLE : View.GONE);
    }

    public TuSdkTextButton getUndoButton()
    {
        if ( mUndoButton == null)
        {
            mUndoButton = (TuSdkTextButton) findViewById(R.id.lsq_scence_effect_cell_undo_btn);
        }
        return mUndoButton;
    }


    public ImageView getImageView()
    {
        if ( mThumbView == null )
        {
            mThumbView = (ImageView)findViewById(R.id.lsq_item_image);
        }
        return mThumbView;
    }

    public TextView getTitleView()
    {
        if ( mTitleView == null )
        {
            mTitleView = (TextView)findViewById(R.id.lsq_item_title);
        }
        return mTitleView;
    }
}
