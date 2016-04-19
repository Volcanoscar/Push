/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： PointIndicatorView.java
 * 内容摘要： 自定义的View，实现圆点指示器
 * 当前版本： 1.0
 * 作 者： 	 李翊星
 * 完成日期： 2015-11-30
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 **************************************************************************/

package com.android.push.view;

import com.android.push.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 类描述 ：自定义的View，实现圆点指示器
 *
 * @author 李翊星
 * @version 1.0
 */
public class PointIndicatorView extends LinearLayout {

    private ImageView[] mImageViews;
    private Context mContext;

    public PointIndicatorView(Context context) {
        super(context);
        mContext = context;
    }

    public PointIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    /**
     * 设置圆点指示器的圆点个数
     *
     * @param count 圆点个数
     */
    public void setPointCount(int count) {

        mImageViews = new ImageView[count];
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < count; i++) {
            ImageView iv = new ImageView(mContext);
            iv.setImageResource(R.drawable.shape_indicator_point2);
            iv.setLayoutParams(lp);
            mImageViews[i] = iv;
            iv.setId(i);
            addView(iv);
        }
    }

    /**
     * 设置圆点
     *
     * @param position 圆点的位置
     */
    public void setPoint(int position) {

        for (int i = 0; i < mImageViews.length; i++) {
            if (i == position) {
                mImageViews[i].setImageResource(R.drawable.shape_indicator_point1);
            } else {
                mImageViews[i].setImageResource(R.drawable.shape_indicator_point2);
            }
        }
    }
}
