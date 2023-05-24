package com.lalamove.huolala.dynamiccore.manager.apply;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.DynamicResType;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamiccore.DynamicResException;
import com.lalamove.huolala.dynamiccore.bean.LoadResInfo;
import com.lalamove.huolala.dynamiccore.listener.ILoadResListener;
import com.lalamove.huolala.dynamiccore.manager.DynamicResManager;

import java.io.File;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: FrameAnimApply
 * @author: huangyuchen
 * @date: 3/14/22
 * @description: 帧动画动态资源执行对象
 * @history:
 */
public class FrameAnimApply extends AbsResApply<View, Integer> {

    /**
     * 帧动画对象
     */
    private AnimationDrawable mAnimDrawable;
    /**
     * 持续时间
     */
    private int[] mDurations;
    /**
     * 动画是否只执行一次
     */
    private boolean mOneShot;
    /**
     * 执行动画的imageView
     */
    private ImageView mImageView;
    /**
     * 执行动画的view
     */
    private View mBackground;
    /**
     * 占位图id
     */
    private Integer mHolderId;
    /**
     * 资源信息
     */
    private DynamicPkgInfo mPkg;

    protected ILoadResListener mListener;

    /**
     * 从imageview创建
     *
     * @param iv
     * @return
     */
    public static FrameAnimApply createImageSrc(ImageView iv) {
        return new FrameAnimApply(iv, null);
    }

    /**
     * 从普通view创建
     *
     * @param v
     * @return
     */
    public static FrameAnimApply createBackground(View v) {
        return new FrameAnimApply(null, v);
    }

    private FrameAnimApply(ImageView iv, View v) {
        this.mImageView = iv;
        this.mBackground = v;
    }

    /**
     * 设置帧动画间隔
     *
     * @param durations
     * @return
     */
    public FrameAnimApply durations(int... durations) {
        if (durations != null) {
            mDurations = new int[durations.length];
            for (int i = 0; i < durations.length; i++) {
                mDurations[i] = durations[i];
            }
        }
        return this;
    }

    /**
     * 设置帧动画是否只执行一次
     *
     * @param oneShot
     * @return
     */
    public FrameAnimApply oneShot(boolean oneShot) {
        mOneShot = oneShot;
        return this;
    }

    /**
     * 设置占位图
     *
     * @param holderId
     * @return
     */
    public FrameAnimApply holder(int holderId) {
        mHolderId = holderId;
        return this;
    }

    /**
     * 启动帧动画
     *
     * @param pkg
     */
    public void startAnim(DynamicPkgInfo pkg) {
        if (pkg == null) {
            return;
        }
        if (pkg.getType() != DynamicResType.FRAME_ANIM) {
            return;
        }
        //r如果帧动画为空，则创建，并执行动态资源加载过程
        if (mAnimDrawable == null) {
            mPkg = pkg;
            View v = (mImageView != null) ? (mImageView) : (mBackground);
            mListener = apply(v, pkg, mHolderId);
        } else if (!mAnimDrawable.isRunning()) {
            //否则直接启动
            mAnimDrawable.start();
        }
    }

    public void stopAnim() {
        if (mPkg.getType() != DynamicResType.FRAME_ANIM) {
            return;
        }
        //如果帧动画不为空，则直接停止
        if (mAnimDrawable != null) {
            mAnimDrawable.stop();
        } else if (mListener != null) {
            //否则从加载监听器中移除
            DynamicResManager.getInstance().removeListener(mPkg, mListener);
            mListener = null;
        }
    }

    @Override
    protected void setDefaultRes(View view, DynamicPkgInfo pkg, Integer defaultObj) {
        super.setDefaultRes(view, pkg, defaultObj);
        if (defaultObj != 0) {
            //ImageView设置Src
            if (view instanceof ImageView) {
                ImageView iv = (ImageView) view;
                iv.setImageResource(defaultObj);
            } else {
                //普通View，设置background
                view.setBackgroundResource(defaultObj);
            }
        }
    }

    @Override
    protected void onLoadSucceed(View v, DynamicPkgInfo pkg, LoadResInfo info) {
        List<File> files = info.files;
        if (files == null || files.size() == 0) {
            reportFail(pkg, new DynamicResException(DynamicConst.Error.APPLY, " onLoadSucceed file null "));
            return;
        }
        //创建逐帧drawable
        mAnimDrawable = new AnimationDrawable();
        //将各个动画文件添加到该drawable中
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            Drawable d = Drawable.createFromPath(file.getAbsolutePath());
            mAnimDrawable.addFrame(d, getDuration(i));
        }
        //设置是否只播放一次
        mAnimDrawable.setOneShot(mOneShot);
        //将drawable设置到控件上
        if (v instanceof ImageView) {
            ImageView iv = (ImageView) v;
            iv.setImageDrawable(mAnimDrawable);
        } else {
            v.setBackground(mAnimDrawable);
        }
        //尝试设置自动停止动画监听器
        registerStopListener(v);
        mAnimDrawable.start();
        reportSucceed(pkg);
    }

    /**
     * 获取动画每帧执行时间
     *
     * @param index
     * @return
     */
    private int getDuration(int index) {
        if (mDurations == null || mDurations.length == 0) {
            return 200;
        }
        index = (index < mDurations.length) ? (index) : 0;
        return mDurations[index];
    }

    /**
     * 如果Activity实现LifecycleOwner接口，则其onDestroy时，自动停止动画
     *
     * @param v
     */
    private void registerStopListener(View v) {
        Context c = v.getContext();
        LifecycleOwner owner;
        if (c instanceof LifecycleOwner) {
            owner = (LifecycleOwner) c;
            owner.getLifecycle().removeObserver(new LifecycleObserver() {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                void onDestroy() {
                    stopAnim();
                }
            });
        }
    }
}
