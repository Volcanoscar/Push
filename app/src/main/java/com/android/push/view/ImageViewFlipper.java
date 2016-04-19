/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： ImageViewFlipper.java
 * 内容摘要： 自定义的ViewFlipper，实现图文弹窗的图片轮播功能，重写回调方法实现圆点指示器
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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * 类描述 ：自定义的ViewFlipper
 *
 * @author 李翊星
 * @version 1.0
 */
public class ImageViewFlipper extends ViewFlipper {

    private static final String TAG = "push.imgFlipper";

    /**
     * 回调接口
     */
    private IImageFlipperIndicator mFlipperIndicator;

    public ImageViewFlipper(Context context) {
        super(context);
    }

    public ImageViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageFlipperIndicator(IImageFlipperIndicator flipperIndicator) {
        this.mFlipperIndicator = flipperIndicator;
    }

    /**
     * 重写showNext()方法，用于实现图片自动切换时，图片的指示标也跟着切换
     */
    @Override
    public void showNext() {
        super.showNext();

        if (null != mFlipperIndicator.getMarkView())
            mFlipperIndicator.getMarkView().setPoint(getDisplayedChild());
    }

    /**
     * 图片浏览指示标的回调接口
     */
    public interface IImageFlipperIndicator {
        PointIndicatorView getMarkView();
    }

}

