package com.scannerpop.jieyaozu.scannerpicturepopupwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jieyaozu on 2016/6/6.
 */
public class ScannerPicturePopupWindow {
    private final int statusBarHeight;
    private Context mContext;
    private View parentView;
    public PopupWindow popupWindow;
    RelativeLayout relativeLayout;
    private DisplayMetrics disp;

    private int destWidth;
    private int destHeight;
    float scale = 0.0f;

    private RelativeLayout.LayoutParams originparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    float widthStep = 0, disw = 0;
    float heightStep = 0, dish = 0;
    float margintopStep = 0, dismts = 0;
    float marginleftStep = 0, dismls = 0;

    //为了让图片在屏幕中间展示，所以要计算一下
    int shoudTopMargin = 0;

    //图片数据源
    private List<Bitmap> imagesList = new ArrayList<Bitmap>();
    private Map<Integer, ImageView> clickImageViewList;
    private int startPosition;
    private int currentPosition = 0;

    private boolean isRunning = false;

    public ScannerPicturePopupWindow(Context context, View parent) {
        mContext = context;
        parentView = parent;
        disp = context.getResources().getDisplayMetrics();
        statusBarHeight = getStatusBarHeight();
    }

    public void showScannerPictureWindow(Map<Integer, ImageView> clickViews, Bitmap bitmap, List<Bitmap> imagesList, int currentItem) {
        if (isRunning) {
            return;
        }
        intiImageViews(imagesList);
        clickImageViewList = clickViews;
        RelativeLayout.LayoutParams params = computeClickView(clickViews.get(currentItem));
        startPosition = currentItem;
        currentPosition = currentItem;
        resetStepValue();
        resetDisValue();

        View view = View.inflate(mContext, R.layout.popupwindow, null);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.popupwindow_rl);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.popupwindow_scanner_viewpager);
        ScannerViewPagerAdapder viewPagerAdapder = new ScannerViewPagerAdapder();
        viewPager.setAdapter(viewPagerAdapder);
        viewPager.setCurrentItem(currentItem);
        viewPager.setOffscreenPageLimit(imagesList.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(parentView, Gravity.TOP | Gravity.LEFT, localtion[0], localtion[1]);

        //计算目标宽高
        computeScaleDestWidthAndHeight(bitmap);

        initOriginparams(params);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(params.width, params.height);
        relativeParams.leftMargin = params.leftMargin;
        relativeParams.topMargin = params.topMargin;
        relativeLayout.setLayoutParams(relativeParams);
        relativeLayout.post(showRunnable);
    }

    /**
     * 计算图片所要放大到的目标宽度和高度
     *
     * @param bitmap
     */
    private void computeScaleDestWidthAndHeight(Bitmap bitmap) {
        scale = (float) bitmap.getWidth() / (float) bitmap.getHeight();
        destWidth = disp.widthPixels;//目标宽度
        destHeight = (int) (disp.widthPixels / scale);//目标高
        //为了使图片居中显示计算实际目标的topmargin值
        shoudTopMargin = (disp.heightPixels - statusBarHeight - destHeight) / 2;
    }

    /**
     * 初始化ImageView
     */
    private void intiImageViews(List<Bitmap> imagesList) {
        views.clear();
        this.imagesList.clear();
        this.imagesList.addAll(imagesList);
        for (int i = 0; i < imagesList.size(); i++) {
            View imageView = View.inflate(mContext, R.layout.popupwindow_viewpager_item, null);
            views.add(imageView);
        }
    }

    private void changeImageViewScaleType(ImageView.ScaleType scaleType) {
        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i);
            ImageView imageView = (ImageView) view.findViewById(R.id.popupwindow_viewpager_item_image);
            imageView.setScaleType(scaleType);
        }
    }

    private List<View> views = new ArrayList<View>();

    class ScannerViewPagerAdapder extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = views.get(position);
            ImageView imageView = (ImageView) view.findViewById(R.id.popupwindow_viewpager_item_image);
            final Bitmap bitmap = imagesList.get(position);
            //System.out.println("========bitmap=========>" + (bitmap != null) + "   ====position======>" + position);
            imageView.setImageBitmap(bitmap);
            container.addView(view);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopupWindow();
                }
            });
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = views.get(position);
            container.removeView(view);
        }

        @Override
        public int getCount() {
            return imagesList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    /**
     * 关闭popupwindow
     */
    public void dismissPopupWindow() {
        if (isRunning) {
            return;
        }
        if (currentPosition != startPosition) {
            RelativeLayout.LayoutParams params = computeClickView(clickImageViewList.get(currentPosition));
            final Bitmap bitmap = imagesList.get(currentPosition);
            computeScaleDestWidthAndHeight(bitmap);
            initOriginparams(params);
            computeSteps(originparams, millis);
        }

        resetDisValue();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
        params.height = destHeight;
        params.topMargin = shoudTopMargin;
        relativeLayout.setLayoutParams(params);
        changeImageViewScaleType(ImageView.ScaleType.CENTER_CROP);
        relativeLayout.post(reverRunnable);
    }

    public boolean isShowing() {
        if (popupWindow != null) {
            return popupWindow.isShowing();
        }
        return false;
    }

    private int[] localtion = new int[2];

    /**
     * 计算所点击视图的位置、LayouParams
     *
     * @param clickView
     * @return
     */
    private RelativeLayout.LayoutParams computeClickView(ImageView clickView) {
        clickView.getLocationInWindow(localtion);
        RelativeLayout.LayoutParams clickparams = (RelativeLayout.LayoutParams) clickView.getLayoutParams();
        if (clickparams.width <= 0 || clickparams.height <= 0) {
            int width = clickView.getWidth();
            int height = clickView.getHeight();
            clickparams.width = width;
            clickparams.height = height;
        }
        return clickparams;
    }

    private void computeSteps(RelativeLayout.LayoutParams originparams, float millis) {
        widthStep = (float) (destWidth - originparams.width) / millis;
        heightStep = (float) (destHeight - originparams.height) / millis;
        margintopStep = (float) (originparams.topMargin - shoudTopMargin) / millis;
        marginleftStep = (float) originparams.leftMargin / millis;
    }

    /**
     * 初始化最初的布局参数，也就是点击时候的
     */
    private void initOriginparams(RelativeLayout.LayoutParams clickparams) {
        clickparams.leftMargin = localtion[0];
        clickparams.topMargin = localtion[1] - statusBarHeight;

        originparams.width = clickparams.width;
        originparams.height = clickparams.height;
        originparams.topMargin = clickparams.topMargin;
        originparams.leftMargin = clickparams.leftMargin;
    }

    /**
     * 把用来计算int值加减时丢失的小数点的值重置
     */
    private void resetDisValue() {
        disw = dish = dismts = dismls = 0;
    }

    private void resetStepValue() {
        widthStep = 0;
        heightStep = 0;
        margintopStep = 0;
        marginleftStep = 0;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获得加速值
     * 返回的值不变时 或为 1时为均速运动
     * <p/>
     * 不能返回0
     *
     * @param count
     * @return
     */
    private float getAccelerate(int count) {
        float value = (float) Math.sin((count / 12.0f) + 3.14f / 2);
        if (value < 0) {
            value = 0.1f;
        }
        return Math.abs(value);
    }

    ShowRunnable showRunnable = new ShowRunnable(new CallBack() {
        @Override
        public void excuteCompelet() {
            isRunning = false;
            count = 0;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
            params.height = disp.heightPixels - statusBarHeight;
            params.topMargin = 0;
            params.leftMargin = 0;
            changeImageViewScaleType(ImageView.ScaleType.FIT_CENTER);
            relativeLayout.setLayoutParams(params);
        }
    });

    float millis = 12.0f;
    private int count = 0;

    public class ShowRunnable implements Runnable {
        private CallBack callBack;

        public ShowRunnable(CallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        public void run() {
            isRunning = true;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
            if (widthStep == 0 || heightStep == 0) {
                widthStep = (float) (destWidth - params.width) / millis;
                heightStep = (float) (destHeight - params.height) / millis;
                margintopStep = (float) (params.topMargin - shoudTopMargin) / millis;
                marginleftStep = (float) params.leftMargin / millis;
                //System.out.println("=========heightStep==========>" + heightStep + " params.topMargin====>" + params.topMargin);
            }
            count++;
            if (params.width < destWidth) {
                //宽度
                float ws = widthStep * getAccelerate(count) + disw;
                disw = ws - (int) ws;//丢弃的值
                params.width += ws;

                //高度
                if (heightStep > 0) {
                    float hs = heightStep * getAccelerate(count) + dish;
                    dish = hs - (int) hs;
                    params.height += hs;
                } else {
                    float hs = -heightStep * getAccelerate(count) - dish;
                    float realHvalue = params.height - hs;
                    dish = realHvalue - (int) realHvalue;
                    params.height = (int) realHvalue;
                }

                //顶部边缘距离
                float tm = margintopStep * getAccelerate(count) - dismts;
                float realtValue = params.topMargin - tm;
                dismts = realtValue - (int) realtValue;
                params.topMargin = (int) realtValue;

                //左边边缘距离
                float lm = marginleftStep * getAccelerate(count) - dismls;
                float reallValue = params.leftMargin - lm;
                dismls = reallValue - (int) reallValue;
                params.leftMargin = (int) reallValue;
            } else {
                params.width = destWidth;
                params.height = destHeight;
                params.topMargin = shoudTopMargin;
                params.leftMargin = 0;
                relativeLayout.setLayoutParams(params);
                if (callBack != null) {
                    callBack.excuteCompelet();
                }
                return;
            }
            relativeLayout.setLayoutParams(params);
            relativeLayout.post(showRunnable);
        }
    }


    ReverRunnable reverRunnable = new ReverRunnable(new CallBack() {
        @Override
        public void excuteCompelet() {
            isRunning = false;
            count = 0;
            popupWindow.dismiss();
        }
    });

    public class ReverRunnable implements Runnable {
        private CallBack callBack;

        public ReverRunnable(CallBack callBack) {
            this.callBack = callBack;
        }

        boolean iscomplete = false, iscomplete2 = false, iscomplete3 = false, iscomplete4 = false;

        @Override
        public void run() {
            isRunning = true;
            count++;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
            //宽度
            float ws = widthStep * getAccelerate(count) - disw;
            float realvalue = params.width - ws;
            disw = realvalue - (int) realvalue;//多减的值
            params.width = (int) realvalue;

            //高度
            float hs = heightStep * getAccelerate(count) - dish;
            float realHvalue = params.height - hs;
            dish = realHvalue - (int) realHvalue;
            params.height = (int) realHvalue;

            //顶部边缘距离
            if (margintopStep > 0) {
                float tm = margintopStep * getAccelerate(count) + dismts;
                dismts = tm - (int) tm;
                params.topMargin += tm;
            } else {
                float tm = -margintopStep * getAccelerate(count) - dismts;
                float realtValue = params.topMargin - tm;
                dismts = realtValue - (int) realtValue;
                params.topMargin = (int) realtValue;
            }

            //左边边缘距离
            float lm = marginleftStep * getAccelerate(count) + dismls;
            dismls = lm - (int) lm;
            params.leftMargin += lm;
            relativeLayout.setLayoutParams(params);

            if (Math.abs(params.width - originparams.width) < Math.abs(widthStep * getAccelerate(count))) {
                params.width = originparams.width;
                iscomplete = true;
            }
            if (Math.abs(params.height - originparams.height) < Math.abs(heightStep * getAccelerate(count))) {
                params.height = originparams.height;
                iscomplete2 = true;
            }
            if (Math.abs(params.topMargin - originparams.topMargin) <= Math.abs(margintopStep * getAccelerate(count))) {
                params.topMargin = originparams.topMargin;
                iscomplete3 = true;
            }
            if (Math.abs(params.leftMargin - originparams.leftMargin) < Math.abs(marginleftStep * getAccelerate(count))) {
                params.leftMargin = originparams.leftMargin;
                iscomplete4 = true;
            }
            relativeLayout.setLayoutParams(params);
            if (iscomplete && iscomplete2 && iscomplete3 && iscomplete4) {
                iscomplete = iscomplete2 = iscomplete3 = iscomplete4 = false;
                callBack.excuteCompelet();
                return;
            }

            relativeLayout.post(reverRunnable);
        }
    }

    public interface CallBack {
        public void excuteCompelet();
    }
}
