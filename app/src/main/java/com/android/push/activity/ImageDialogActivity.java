/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： ImageDialogActivity.java
 * 内容摘要： Push的弹窗展示的图文形式实现
 * 当前版本： 1.0
 * 作 者： 	 李翊星
 * 完成日期： 2015-11-24
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 **************************************************************************/

package com.android.push.activity;

import com.android.push.R;
import com.android.push.bean.FeedbackBean;
import com.android.push.bean.PushBean;
import com.android.push.bean.PushInfoBean;
import com.android.push.db.PushDataBase;
import com.android.push.util.FeedbackUtil;
import com.android.push.util.ImageLoader;
import com.android.push.view.ImageViewFlipper;
import com.android.push.view.PointIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.android.push.constant.PushConstant.PUSH_PARCEL_KEY;

/**
 * 类描述 ：图文弹窗的Activity
 *
 * @author 李翊星
 * @version 1.0
 */
public class ImageDialogActivity extends Activity implements ImageViewFlipper.IImageFlipperIndicator {

    private static final String TAG = "push.ImageDialog";

    private TextView tvTitle, tvContent;
    private Button btnGo, btnOk;
    private ImageViewFlipper vfImages;
    private PointIndicatorView pivFlipper;

    private PushBean push;
    private int mImageCounts;

    private ImageLoader mImageLoader;

    private final String[] IMAGE_URLS = {
//            "http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg",
            "http://www.bz55.com/uploads/allimg/130304/1-1303040Z528.jpg",
            "http://img.my.csdn.net/uploads/201407/26/1406383291_6518.jpg",
            "http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image);

        Intent intent = getIntent();
        push = intent.getParcelableExtra(PUSH_PARCEL_KEY);
        if (push == null) {
            finish();
        }
        mImageLoader = ImageLoader.getInstance();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        feedBackInfo();
        initView(push);
        initImages();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化界面
     */
    private void initView(PushBean push) {
        if (push == null) {
            finish();
        }

        tvTitle = (TextView) findViewById(R.id.tv_dialog_image_title);
        tvContent = (TextView) findViewById(R.id.tv_dialog_image_content);
        btnOk = (Button) findViewById(R.id.btn_dialog_image_ok);
        vfImages = (ImageViewFlipper) findViewById(R.id.vf_dialog_image);
        pivFlipper = (PointIndicatorView) findViewById(R.id.piv_flipper);
        tvTitle.setText(push.title);
        tvContent.setText(push.content_text);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化ImageViewFlipper
     */
    private void initImages() {
        mImageCounts = push.picture_urls.length();
        if (mImageCounts <= 0) {
            finish();
        }
        pivFlipper.setPointCount(mImageCounts);
        pivFlipper.setPoint(0);
        vfImages.setImageFlipperIndicator(this);

        JSONArray pics = push.picture_urls;
        for (int i = 0; i < mImageCounts; i++) {
            try {
                ImageView iv = new ImageView(this);
                iv.setImageResource(R.mipmap.pictures_no);
                BitmapDrawable bd = (BitmapDrawable) getDrawable(R.mipmap.pictures_no);
                iv.setImageBitmap(bd.getBitmap());
                Log.d(TAG, "initImage()");
                mImageLoader.loadImage(pics.getString(i), iv, true);
                vfImages.addView(iv);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /*for (String url : push.picture_urls) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.mipmap.pictures_no);
            BitmapDrawable bd = (BitmapDrawable) getDrawable(R.mipmap.pictures_no);
            iv.setImageBitmap(bd.getBitmap());
            Log.d(TAG, "initImage()");
//            mImageLoader.loadImage(url, iv, true);
            vfImages.addView(iv);
        }*/

        if (mImageCounts == 1) {
            pivFlipper.setVisibility(View.INVISIBLE);
        } else {
            vfImages.setInAnimation(this, R.anim.right_in);
            vfImages.setOutAnimation(this, R.anim.left_out);
            vfImages.setFlipInterval(2000);
            vfImages.startFlipping();
        }
    }

    /**
     * 反馈信息给服务器
     */
    private void feedBackInfo() {

        PushInfoBean pushInfo = push.push_info;

        //  记录PushInfo
        PushDataBase pushDataBase = PushDataBase.getInstance(this);
        pushDataBase.open();
        pushDataBase.recordPushInfo(pushInfo);
        pushDataBase.close();

        //  反馈操作
        FeedbackBean feedback = new FeedbackBean();
        feedback.push_id = pushInfo.push_id;
        feedback.is_clicked = 1;
        FeedbackUtil.feedbackToServer(this, feedback);
    }

    @Override
    public PointIndicatorView getMarkView() {
        return pivFlipper;
    }
}
