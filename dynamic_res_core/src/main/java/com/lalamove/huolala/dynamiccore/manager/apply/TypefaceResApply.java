package com.lalamove.huolala.dynamiccore.manager.apply;

import android.graphics.Typeface;
import android.widget.TextView;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamiccore.DynamicResException;
import com.lalamove.huolala.dynamiccore.bean.LoadResInfo;

import java.io.File;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: TypefaceResApply
 * @author: huangyuchen
 * @date: 3/14/22
 * @description:
 * @history:
 */
public class TypefaceResApply extends AbsResApply<TextView, Typeface> {
    /**
     * Typeface对象缓存
     */
    private Typeface mTypeface;

    public void setTypeface(TextView textView, DynamicPkgInfo pkg) {
        apply(textView, pkg, null);
    }

    @Override
    protected boolean loadResFromCache(TextView textView, DynamicPkgInfo pkg, Typeface defaultObj) {
        //如果存在缓存，则直接使用
        if (mTypeface != null) {
            textView.setTypeface(mTypeface);
            return true;
        }
        return false;
    }

    @Override
    protected void setDefaultRes(TextView tv, DynamicPkgInfo pkg, Typeface defaultObj) {
        tv.setTypeface(defaultObj);
    }

    @Override
    protected void onLoadSucceed(TextView tv, DynamicPkgInfo pkg, LoadResInfo info) {
        //如果加载成功，但是其他地方已经设置了缓存，则直接使用缓存结果
        if (mTypeface != null) {
            tv.setTypeface(mTypeface);
            return;
        }
        List<File> files = info.files;
        if (files == null || files.size() == 0) {
            reportFail(pkg, new DynamicResException(DynamicConst.Error.APPLY, " onLoadSucceed file null "));
            return;
        }
        //将动态资源文件解析成Typeface对象
        Typeface tf = Typeface.createFromFile(files.get(0));
        tv.setTypeface(tf);
        //缓存Typeface,供其他类使用
        mTypeface = tf;
        reportSucceed(pkg);
    }
}
