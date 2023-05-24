package com.lalamove.huolala.lib_dynamic_plugin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lalamove.huolala.client.nativelib.DynamicLib;
import com.lalamove.huolala.client.nativelib.NativeLib;
import com.lalamove.huolala.dynamicbase.so.ILoadSoListener;
import com.lalamove.huolala.dynamiccore.manager.DynamicResManager;
import com.lalamove.huolala.dynamiccore.manager.apply.FrameAnimApply;
import com.lalamove.huolala.lib_dynamic_plugin.job.PreDynamicLoadJob;


public class MainActivity extends AppCompatActivity {

    private TextView mFontTv;
    private ImageView mIvCar;
    private TextView mSoTv;
    private TextView mAnimTv;
    private TextView mContentTv;
    private FrameAnimApply mCarAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAnimTv = findViewById(R.id.anim_tv);
        mSoTv = findViewById(R.id.so_tv);
        mFontTv = findViewById(R.id.font_tv);
        mIvCar = findViewById(R.id.ivCar);
        mContentTv = findViewById(R.id.content);

        //预加载 so
        new PreDynamicLoadJob().init(this);

        mFontTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicResManager.getInstance().setTypeface(mFontTv, DynamicResConst.TypeFace.TG_TYPE_REGULAR);
            }
        });
        mAnimTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCarAnim == null) {
                    mCarAnim = DynamicResManager.getInstance().createFrameAnim(mIvCar).durations(100).oneShot(false);
                }
                mCarAnim.startAnim(DynamicResConst.FrameAnim.ANIM_CAR);
            }
        });
        mSoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicResManager.getInstance().getLoadSoManager().loadSo(DynamicResConst.DEMO_SO, new ILoadSoListener() {
                    @Override
                    public void onSucceed(String path) {
                        mContentTv.append(new NativeLib().stringFromJNI());
                        mContentTv.append("-");
                        mContentTv.append(new DynamicLib().stringFromJNI());
                    }

                    @Override
                    public void onError(Throwable t) {
                        mContentTv.append(t.getMessage());
                    }
                });

            }
        });
    }
}