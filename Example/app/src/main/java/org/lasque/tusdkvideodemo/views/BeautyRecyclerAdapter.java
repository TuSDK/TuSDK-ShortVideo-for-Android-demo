package org.lasque.tusdkvideodemo.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.lasque.tusdkvideodemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 美颜
 * @author xujie
 * @Date 2018/9/29
 */

public class BeautyRecyclerAdapter extends RecyclerView.Adapter<BeautyRecyclerAdapter.BeautyViewHolder>{

    private Context mContext;
    private List<String> mBeautyParams;
    private int mCurrentPos = 1;

    public OnBeautyItemClickListener listener;

    public interface OnBeautyItemClickListener{
        void onItemClick(View v,int position);
        void onClear();
    }

    public void setOnBeautyItemClickListener(OnBeautyItemClickListener onBeautyItemClickListener){
        this.listener = onBeautyItemClickListener;
    }


    public BeautyRecyclerAdapter(Context context) {
        super();
        mContext = context;
        mBeautyParams = new ArrayList<>();
    }

    /**
     * 设置美颜参数
     * @param beautyParams
     */
    public void setBeautyParams(List<String> beautyParams){
        mBeautyParams = beautyParams;
        notifyDataSetChanged();
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

        mBeautyParams.get(position);

        if(listener != null && mCurrentPos == 0) listener.onClear();

        if(position == 0){
            beautyViewHolder.mBeautyLevelText.setText("");
            beautyViewHolder.mBeautyLevelText.setBackgroundResource(R.drawable.tusdk_view_widget_beauty_unselect);
            beautyViewHolder.mBeautyLevelImage.setImageResource(R.drawable.ic_nix);
        }else if(mCurrentPos == position && position != 0){
            beautyViewHolder.mBeautyLevelText.setText("");
            beautyViewHolder.mBeautyLevelText.setBackgroundResource(R.drawable.tusdk_view_widget_beauty_select);
            beautyViewHolder.mBeautyLevelImage.setImageResource(R.drawable.lsq_ic_parameter);
        }else{
            beautyViewHolder.mBeautyLevelText.setText(String.valueOf(position));
            beautyViewHolder.mBeautyLevelText.setBackgroundResource(R.drawable.tusdk_view_widget_beauty_unselect);
            beautyViewHolder.mBeautyLevelImage.setImageBitmap(null);
        }
        beautyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(mCurrentPos);
                mCurrentPos = position;
                if(listener != null) listener.onItemClick(beautyViewHolder.itemView,position);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBeautyParams.size();
    }

    class BeautyViewHolder extends RecyclerView.ViewHolder{

        public TextView mBeautyLevelText;
        public ImageView mBeautyLevelImage;

        public BeautyViewHolder(View itemView) {
            super(itemView);
            mBeautyLevelText =  itemView.findViewById(R.id.lsq_beauty_level_text);
            mBeautyLevelImage =  itemView.findViewById(R.id.lsq_beauty_level_image);
        }
    }
}
