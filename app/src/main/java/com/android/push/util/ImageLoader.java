package com.android.push.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.android.push.util.ImageSizeUtil.ImageSize;

/**
 * class description:
 *
 * @author liyixing
 * @version 1.0
 */
public class ImageLoader {
    private static ImageLoader mInstance;

    /**
     * 图片缓存的核心对象
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    private static final int DEAFULT_THREAD_COUNT = 1;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;
    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    /**
     * UI线程中的Handler
     */
    private Handler mUIHandler;

    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    private Semaphore mSemaphoreThreadPool;

    private boolean isDiskCacheEnable = true;

    private static final int MB = 1024 * 1024;
    private static final String TAG = "lyx.ImageLoader";

    public enum Type {
        FIFO, LIFO;
    }

    private ImageLoader(int threadCount, Type type) {
        init(threadCount, type);
    }

    /**
     * 初始化
     */
    private void init(int threadCount, Type type) {
        initBackThread();

        // 获取我们应用的最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

        };

        // 创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;
        mSemaphoreThreadPool = new Semaphore(threadCount);
    }

    /**
     * 初始化后台轮询线程
     */
    private void initBackThread() {
        // 后台轮询线程
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        // 线程池去取出一个任务进行执行
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                        }
                    }
                };
                // 释放一个信号量
                mSemaphorePoolThreadHandler.release();
                Looper.loop();
            }

            ;
        };

        mPoolThread.start();
    }

    public static ImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(DEAFULT_THREAD_COUNT, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    public static ImageLoader getInstance(int threadCount, Type type) {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    /**
     * 根据path为imageview设置图片
     */
    public void loadImage(final String path, final ImageView imageView,
                          final boolean isFromNet) {

        imageView.setTag(path);

        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                public void handleMessage(Message msg) {
                    Log.d(TAG, "loadImage, handleMsg");
                    // 获取得到图片，为imageview回调设置图片
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView imageview = holder.imageView;
                    String path = holder.path;

                    Log.d(TAG, "loadImage, handleMsg-->path: " + path
                            + "\ttag: " + imageview.getTag());

                    // 将path与getTag存储路径进行比较
                    if (imageview.getTag().toString().equals(path)) {
                        imageview.setImageBitmap(bm);
                    }
                }
            };
        }

        // 根据path在缓存中获取bitmap
        Bitmap bm = getBitmapFromLruCache(path);

        if (bm != null) {
            Log.d(TAG, "bm isn't null, refresh Bitmap");
            refreshBitmap(path, imageView, bm);
        } else {
            Log.d(TAG, "bm is null, build and add task");
            addTask(buildTask(path, imageView, isFromNet));
        }

    }

    /**
     * 根据传入的参数，新建一个任务
     */
    private Runnable buildTask(final String path, final ImageView imageView,
                               final boolean isFromNet) {
        return new Runnable() {
            @Override
            public void run() {
                Bitmap bm = null;
                if (isFromNet) {
                    String md5 = MD5Util.getMD5(path);
                    File file = getDiskCacheFile(imageView.getContext(), md5);

                    Log.d(TAG, "file path: " + file.getAbsolutePath());

                    // 如果在缓存文件中发现
                    if (file.exists()) {
                        Log.e(TAG, "find image :" + path + " in disk cache .");
                        bm = loadImageFromLocal(file.getAbsolutePath(),
                                imageView);
                    } else {
                        // 检测是否开启硬盘缓存
                        int freeSdSpace = freeSpaceOnSdDir(getDiskCacheDir(imageView
                                .getContext()));
                        if (isDiskCacheEnable || freeSdSpace > 10) {
                            boolean downloadState = DownloadImgUtils
                                    .downloadImgByUrl(path, file);
                            // 如果下载成功
                            if (downloadState) {
                                Log.e(TAG,
                                        "download image :" + path
                                                + " to disk cache . path is "
                                                + file.getAbsolutePath());
                                bm = loadImageFromLocal(file.getAbsolutePath(),
                                        imageView);
                            }
                        }
                        // 直接从网络加载
                        else {
                            Log.e(TAG, "load image :" + path + " to memory.");
                            bm = DownloadImgUtils.downloadImgByUrl(path,
                                    imageView);
                        }
                    }
                } else {
                    bm = loadImageFromLocal(path, imageView);
                }
                // 把图片加入到缓存
                addBitmapToLruCache(path, bm);
                refreshBitmap(path, imageView, bm);
                mSemaphoreThreadPool.release();
            }
        };
    }

    private Bitmap loadImageFromLocal(final String path,
                                      final ImageView imageView) {
        Bitmap bm;
        // 加载图片
        // 图片的压缩
        // 1、获得图片需要显示的大小
        ImageSize imageSize = ImageSizeUtil.getImageViewSize(imageView);
        // 2、压缩图片
        bm = decodeSampledBitmapFromPath(path, imageSize.width,
                imageSize.height);
        return bm;
    }

    /**
     * 从任务队列取出一个方法
     */
    private Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTaskQueue.removeLast();
        }
        return null;
    }


    private void refreshBitmap(final String path, final ImageView imageView,
                               Bitmap bm) {
        Log.d(TAG, "refreshBitmap");
        Message message = Message.obtain();
        ImgBeanHolder holder = new ImgBeanHolder();
        holder.bitmap = bm;
        holder.path = path;
        holder.imageView = imageView;
        message.obj = holder;
        mUIHandler.sendMessage(message);
    }

    private void clearLruCache() {
        if (mLruCache != null && mLruCache.size() > 0) {
            mLruCache.evictAll();
        }
    }

    /**
     * 将图片加入LruCache
     */
    protected void addBitmapToLruCache(String path, Bitmap bm) {
        Log.d(TAG, "addBitmapToLruCache(..)");
        if (getBitmapFromLruCache(path) == null) {
            if (bm != null)
                mLruCache.put(path, bm);
        }
    }

    /**
     * 根据图片需要显示的宽和高对图片进行压缩
     */
    protected Bitmap decodeSampledBitmapFromPath(String path, int width,
                                                 int height) {
        // 获得图片的宽和高，并不把图片加载到内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options,
                width, height);

        // 使用获得到的InSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        // if(mPoolThreadHandler==null)wait();
        try {
            if (mPoolThreadHandler == null)
                mSemaphorePoolThreadHandler.acquire();
        } catch (InterruptedException e) {
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    /**
     * 获得缓存图片的地址
     */
    public File getDiskCacheFile(Context context, String uniqueName) {
        String cachePath = getDiskCacheDir(context);
        freeSpaceOnSdDir(cachePath);
        return new File(cachePath + File.separator + uniqueName);
    }

    public String getDiskCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            cachePath = context.getExternalCacheDir().getPath();
            Log.d(TAG, "External Storage is mounted, cachePath: " + cachePath);
        } else {
            cachePath = context.getCacheDir().getPath();
            Log.d(TAG, "External Storage isn't mounted, cachePath: " + cachePath);
        }
        return cachePath;
    }

    /**
     * 计算sdcard上的剩余空间
     */
    private int freeSpaceOnSdDir(String path) {
        StatFs stat = new StatFs(path);
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
        Log.d(TAG, "cache dir free space: " + sdFreeMB);
        return (int) sdFreeMB;
    }

    private int usedSpaceOnCacheDir(String path) {
        StatFs stat = new StatFs(path);
        double usedBlocks = stat.getTotalBytes() - stat.getAvailableBytes();
        double sdUsedMB = usedBlocks / MB;
        Log.d(TAG, "cache dir used space: " + usedBlocks);
        return (int) sdUsedMB;
    }

    /**
     * 根据path在缓存中获取bitmap
     */
    private Bitmap getBitmapFromLruCache(String key) {
        Log.d(TAG, "getBitmapFromLruCache()");
        return mLruCache.get(key);
    }

    private class ImgBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }
}