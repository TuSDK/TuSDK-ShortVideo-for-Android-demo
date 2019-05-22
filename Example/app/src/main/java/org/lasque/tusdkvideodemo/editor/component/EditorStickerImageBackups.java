package org.lasque.tusdkvideodemo.editor.component;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;

import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffector;
import org.lasque.tusdk.core.utils.ColorUtils;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.impl.components.widget.sticker.StickerImageItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerTextItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerData;
import org.lasque.tusdk.video.editor.TuSdkMediaStickerImageEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaTextEffectData;
import org.lasque.tusdkvideodemo.editor.component.helper.EditorTextAndStickerRankHelper;
import org.lasque.tusdkvideodemo.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.tusdkvideodemo.views.editor.playview.rangeselect.TuSdkMovieColorRectView;

import java.util.ArrayList;
import java.util.List;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/12/19 10:14
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 贴纸特效备份管理
 */
public class EditorStickerImageBackups {
    /** 贴纸父视图 **/
    private StickerView mStickerView;
    /** LineView **/
    private TuSdkMovieScrollPlayLineView mLineView;
    private TuSdkEditorEffector mEditorEffector;

    private EditorTextAndStickerRankHelper mRankHelper;


    public EditorStickerImageBackups(StickerView mStickerView,TuSdkEditorEffector editorEffector,EditorTextAndStickerRankHelper rankHelper) {
        this.mStickerView = mStickerView;
        this.mEditorEffector = editorEffector;
        this.mRankHelper = rankHelper;
    }

    /** 设置LineView **/
    public void setLineView(TuSdkMovieScrollPlayLineView lineView){
        this.mLineView = lineView;
    }

    /** 同步组件Attach */
    public void onComponentAttach() {
        mRankHelper.getBackupLinkedList().clear();
        for (Object entity : mRankHelper.getMemeoBackupLinkedList()) {
            if(entity instanceof StickerImageBackupEntity) {
                StickerImageBackupEntity item = (StickerImageBackupEntity) entity;
                mRankHelper.getBackupLinkedList().add(item);
                if (item.stickerImageItemView != null) {
                    item.stickerImageItemView.setVisibility(View.VISIBLE);
                    item.stickerImageItemView.setStickerViewType(StickerView.StickerType.Image);
                    item.stickerImageItemView.setStickerType(StickerView.StickerType.Image);
                    mStickerView.addView(item.stickerImageItemView, -1);
                    mStickerView.addSticker(item.stickerImageItemView);
                    item.stickerImageItemView.setTranslation(item.stickerImageItemView.getTranslation().x, item.stickerImageItemView.getTranslation().y);
                }

                if (item.colorRectView != null && mLineView != null) {
                    item.colorRectView.setVisibility(View.VISIBLE);
                }
            }else if(entity instanceof EditorTextBackups.TextBackupEntity){
                EditorTextBackups.TextBackupEntity item = (EditorTextBackups.TextBackupEntity) entity;
                mRankHelper.getBackupLinkedList().add(item);
                if (item.textItemView != null) {
                    item.textItemView.setVisibility(View.VISIBLE);
                    if(item.textItemView.getParent() != null){
                        ((ViewGroup)item.textItemView.getParent()).removeAllViews();
                    }
                    mStickerView.addView(item.textItemView);
                    mStickerView.addSticker(item.textItemView);
                    item.textItemView.reRotate();
                    item.textItemView.setTranslation(item.textItemView.getTranslation().x, item.textItemView.getTranslation().y);

                    Typeface typeface = Typeface.DEFAULT;
                    if (item.mTextFont == 2) typeface = Typeface.SERIF;
                    item.textItemView.setTextFont(typeface);

                    item.textItemView.setTextStrokeWidth((int) item.strokeWidth);
                    item.textItemView.setTextStrokeColor(item.strokeColor);
                    item.textItemView.onSelectedColorChanged(0, item.color);
                    item.textItemView.setStickerViewType(StickerView.StickerType.Text);
                    item.textItemView.setStickerType(StickerView.StickerType.Text);

                    //背景颜色
                    item.textItemView.onSelectedColorChanged(1, ColorUtils.alphaEvaluator(item.bgAlpha, item.bgColor));

                    //字间距
                    item.textItemView.setLetterSpacing(0.07f * item.wordWidth);
                    //行间距
                    item.textItemView.setLineSpacing(0, 0.5f + item.rowWidth);

                    int style = item.isBold ? Typeface.BOLD : Typeface.NORMAL;
                    if (item.isBold && item.isItalics) style = Typeface.BOLD_ITALIC;
                    else if (!item.isBold && item.isItalics) style = Typeface.ITALIC;
                    item.textItemView.getTextView().setTypeface(Typeface.defaultFromStyle(style));
                    item.textItemView.getTextView().setUnderlineText(item.isUnderline);
                    item.textItemView.getTextView().setAlpha(item.alpha);

                }

                if (item.colorRectView != null && mLineView != null) {
                    item.colorRectView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /** 同步组件Detach */
    public void onComponentDetach() {
    }


    /** 添加一个备份实体 **/
    public void addBackupEntity(StickerImageBackupEntity entity){
        if(mRankHelper.getBackupLinkedList().contains(entity))return;
        mRankHelper.getBackupLinkedList().addLast(entity);
    }

    public StickerImageBackupEntity findTextBackupEntity( StickerImageItemView stickerItemView){
        for (Object entity : mRankHelper.getBackupLinkedList()) {
            if (entity instanceof StickerImageBackupEntity) {
                StickerImageBackupEntity item = (StickerImageBackupEntity) entity;
                if (item.stickerImageItemView == stickerItemView) {
                    return item;
                }
            }
        }
        return null;
    }

    public StickerImageBackupEntity findTextBackupEntityByMemo( StickerImageItemView stickerItemView){
        for (Object entity : mRankHelper.getMemeoBackupLinkedList()) {
            if (entity instanceof StickerImageBackupEntity) {
                StickerImageBackupEntity item = (StickerImageBackupEntity) entity;
                if (item.stickerImageItemView == stickerItemView) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 通过贴纸移除一个备份实体 **/
    public void removeBackupEntityWithSticker(StickerImageItemView textItemView){
        if(textItemView == null)return;
        int index = -1;
        for (int i = 0; i < mRankHelper.getBackupLinkedList().size(); i++) {
            if (mRankHelper.getBackupLinkedList().get(i) instanceof StickerImageBackupEntity) {
                StickerImageBackupEntity item = (StickerImageBackupEntity) mRankHelper.getBackupLinkedList().get(i);
                if (textItemView.equals(item.stickerImageItemView)) {
                    index = i;
                }
            }
        }
        if(index == -1)return;

        StickerImageBackupEntity entity = (StickerImageBackupEntity) mRankHelper.getBackupLinkedList().get(index);
        if(entity.stickerImageItemView != null) mStickerView.removeView(entity.stickerImageItemView);
        if(entity.colorRectView != null && mLineView != null)mLineView.deletedColorRect(entity.colorRectView);
        if(entity.stickerImageMediaEffectData != null && mEditorEffector != null) mEditorEffector.removeMediaEffectData(entity.stickerImageMediaEffectData);

        mRankHelper.getBackupLinkedList().remove(index);
    }

//    /** 移除一个备份实体 **/
//    public void removeBackupEntity(StickerImageBackupEntity entity){
//       boolean result =  mBackupEntityList.remove(entity);
//
//       if(!result)return;
//       if(entity.stickerImageItemView != null) mStickerView.removeView(entity.stickerImageItemView);
//       if(entity.colorRectView != null && mLineView != null)mLineView.deletedColorRect(entity.colorRectView);
//    }

    /** 应用特效 **/
    public void onApplyEffect() {
        mRankHelper.getMemeoBackupLinkedList().clear();
        mRankHelper.getMemeoBackupLinkedList().addAll(mRankHelper.getBackupLinkedList());
        mRankHelper.getBackupLinkedList().clear();
    }

    /** 返回事件 **/
    public void onBackEffect() {
        //移除所有备忘数据
        mStickerView.cancelAllStickerSelected();
        mStickerView.getStickerItems().clear();
        mStickerView.removeAllViews();

        for (Object entity : mRankHelper.getBackupLinkedList()) {
            if(entity instanceof StickerImageBackupEntity){
                StickerImageBackupEntity item = (StickerImageBackupEntity) entity;
                if(item.colorRectView != null){
                    item.colorRectView.setVisibility(View.GONE);
                }
            }
        }
        mRankHelper.getBackupLinkedList().clear();

        //应用数据
        if(mRankHelper.getMemeoBackupLinkedList().size() > 0 && mEditorEffector != null){
            for (Object entity : mRankHelper.getMemeoBackupLinkedList()) {
                if(entity instanceof StickerImageBackupEntity) {
                    StickerImageBackupEntity item = (StickerImageBackupEntity) entity;
                    if (item.stickerImageMediaEffectData != null)
                        mEditorEffector.addMediaEffectData(item.stickerImageMediaEffectData);
                }else if(entity instanceof EditorTextBackups.TextBackupEntity){
                    EditorTextBackups.TextBackupEntity textBackupEntity = (EditorTextBackups.TextBackupEntity) entity;
                    if (textBackupEntity.textMediaEffectData != null)
                        mEditorEffector.addMediaEffectData(textBackupEntity.textMediaEffectData);
                }
            }
        }
    }

    /** 创建备份实体类 **/
    public static StickerImageBackupEntity createBackUpEntity(StickerData stickerData, StickerImageItemView stickerImageItemView, TuSdkMovieColorRectView colorRectView){
        StickerImageBackupEntity entity = new StickerImageBackupEntity();
        entity.stickerData = stickerData;
        entity.stickerImageItemView = stickerImageItemView;
        entity.colorRectView = colorRectView;
        return entity;
    }

    /** 寻找颜色区间 **/
    public TuSdkMovieColorRectView findColorRect(StickerData stickerData) {
        if(mRankHelper.getBackupLinkedList().size() == 0 || stickerData == null)return null;
        for (Object entity : mRankHelper.getBackupLinkedList()) {
            if(entity instanceof StickerImageBackupEntity) {
                StickerImageBackupEntity item = (StickerImageBackupEntity) entity;
                if (stickerData.equals(item.stickerData)) {
                    return item.colorRectView;
                }
            }
        }
        return null;
    }

    /** 寻找贴纸数据 **/
    public StickerData findStickerData(TuSdkMovieColorRectView rectView) {
        if(mRankHelper.getBackupLinkedList().size() == 0 || rectView == null)return null;
        for (Object entity : mRankHelper.getBackupLinkedList()) {
            if(entity instanceof StickerImageBackupEntity) {
                StickerImageBackupEntity item  = (StickerImageBackupEntity) entity;
                if (rectView.equals(item.colorRectView)) {
                    return item.stickerData;
                }
            }
        }
        return null;
    }

    /** 寻找贴纸视图 **/
    public StickerImageItemView findStickerItem(TuSdkMovieColorRectView rectView) {
        if(mRankHelper.getBackupLinkedList().size() == 0 || rectView == null)return null;
        for (Object entity : mRankHelper.getBackupLinkedList()) {
            if(entity instanceof StickerImageBackupEntity) {
                StickerImageBackupEntity item = (StickerImageBackupEntity) entity;
                if (rectView.equals(item.colorRectView)) {
                    return item.stickerImageItemView;
                }
            }
        }
        return null;
    }

    /** 实体备份包装类 **/
    public static class StickerImageBackupEntity{
        StickerData stickerData;
        StickerImageItemView stickerImageItemView;
        TuSdkMovieColorRectView colorRectView;
        TuSdkMediaStickerImageEffectData stickerImageMediaEffectData;
    }

}
