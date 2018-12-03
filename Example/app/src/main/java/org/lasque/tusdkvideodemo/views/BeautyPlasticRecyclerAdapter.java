package org.lasque.tusdkvideodemo.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdkvideodemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 微整形
 * @author xujie
 * @Date 2018/9/29
 */

public class BeautyPlasticRecyclerAdapter extends RecyclerView.Adapter<BeautyPlasticRecyclerAdapter.BeautyViewHolder>{

    private Context mContext;
    private List<String> mBeautyParams;
    private int mCurrentPos = -1;
    private float[] mPercentParams = new float[]{0f,0.3f,0.2f,0.2f,0.5f,0.5f,0.5f,0.5f,0.5f};

    public BeautyPlasticRecyclerAdapter.OnBeautyPlasticItemClickListener listener;

    public interface OnBeautyPlasticItemClickListener{
        void onItemClick(View v,int position);
        void onClear();
    }

    public void setOnBeautyPlasticItemClickListener(BeautyPlasticRecyclerAdapter.OnBeautyPlasticItemClickListener onBeautyPlasticItemClickListener){
        this.listener = onBeautyPlasticItemClickListener;
    }

    public BeautyPlasticRecyclerAdapter(Context context) {
        super();
        mContext = context;
        mBeautyParams = new ArrayList<>();
    }

    /**
     * 重置微整形值
     * @return
     */
    public float[] resetPercentParams(){
        mPercentParams = new float[]{0f,0.3f,0.2f,0.2f,0.5f,0.5f,0.5f,0.5f,0.5f};
        return mPercentParams;
    }

    /**
     * 改变默认值
     * @param pos position
     * @param percentParam 百分比
     */
    public void setPercentParamByPos(int pos,float percentParam){
        mPercentParams[pos] = percentParam;
    }

    /**
     * 获取当前选中的百分比值
     * @return
     */
    public float getPercentParams(){
        return mPercentParams[mCurrentPos < 0 ? 0 : mCurrentPos];
    }

    /**
     * 设置参数
     * @param beautyParams
     */
    public void setBeautyParams(List<String> beautyParams){
        mBeautyParams = beautyParams;
        notifyDataSetChanged();
    }

    /**
     * 获取当前参数列表
     * @return
     */
    public List<String> getBeautyParams(){
        return mBeautyParams;
    }

    /**
     * 获取当前选中position
     * @return
     */
    public int getCurrentPos(){
        return mCurrentPos;
    }

    @Override
    public BeautyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lsq_recycler_beauty_item_layout,null);
        BeautyViewHolder viewHolder = new BeautyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BeautyViewHolder beautyViewHolder,final int position) {
        String code = mBeautyParams.get(position);
        code = code.toLowerCase();
        String codeName = getPrefix() + code;
        beautyViewHolder.mBeautyLevelImage.setSelected(false);
        beautyViewHolder.mBeautyName.setChecked(false);

        if(position == 0){
            beautyViewHolder.mBeautyLevelImage.setImageResource(R.drawable.lsq_ic_reset_normall);
            beautyViewHolder.mBeautyName.setText(R.string.lsq_reset);
        }else if(mCurrentPos == position && position != 0){
            beautyViewHolder.mBeautyLevelImage.setSelected(true);
            beautyViewHolder.mBeautyName.setChecked(true);
            beautyViewHolder.mBeautyLevelImage.setImageResource(TuSdkContext.getDrawableResId(codeName));
            beautyViewHolder.mBeautyName.setText(TuSdkContext.getString(codeName));
        }else{
            beautyViewHolder.mBeautyLevelImage.setImageResource(TuSdkContext.getDrawableResId(codeName));
            beautyViewHolder.mBeautyName.setText(TuSdkContext.getString(codeName));
        }
        beautyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position == 0 && listener != null) {
                    mCurrentPos = 0;
                    notifyDataSetChanged();
                    listener.onClear();
                }else {
                    notifyItemChanged(mCurrentPos);
                    mCurrentPos = position;
                    notifyItemChanged(position);

                    if(listener != null) listener.onItemClick(beautyViewHolder.itemView,position);
                }
            }
        });
    }

    /**
     * 获取前缀
     *
     * @return
     */
    protected String getPrefix()
    {
        return "lsq_ic_";
    }

    @Override
    public int getItemCount() {
        return mBeautyParams.size();
    }

    class BeautyViewHolder extends RecyclerView.ViewHolder{

        public TextView mBeautyLevelText;
        public ImageView mBeautyLevelImage;
        public CheckedTextView mBeautyName;

        public BeautyViewHolder(View itemView) {
            super(itemView);
            mBeautyLevelText =  itemView.findViewById(R.id.lsq_beauty_level_text);
            mBeautyLevelImage =  itemView.findViewById(R.id.lsq_beauty_level_image);
            mBeautyLevelText.setVisibility(View.GONE);
            mBeautyName = itemView.findViewById(R.id.lsq_beauty_name);
            mBeautyName.setVisibility(View.VISIBLE);
        }
    }
}
