/**
 * TuSDKVideoDemo
 * TuApplication.java
 *
 * @author		Yanlin
 * @Date		4:54:37 PM
 * @Copright	(c) 2015 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tencent.bugly.crashreport.CrashReport;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkApplication;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.monitor.TuSdkMonitor;

/**
 * TuApplication.java
 *
 * @author Yanlin
 *
 */
public class TuApplication extends TuSdkApplication
{
	/** 应用程序创建 */
	@Override
	public void onCreate()
	{
		super.onCreate();

		/**
		 ************************* TuSDK 集成三部曲 *************************
		 * 
		 * 1. 在官网注册开发者账户
		 * 
		 * 2. 下载SDK和示例代码
		 * 
		 * 3. 创建应用，获取appkey，导出资源包
		 * 
		 ************************* TuSDK 集成三部曲 ************************* 
		 * 
		 * 关于TuSDK体积 (约2M大小)
		 * 
		 * Android 编译知识：
		 * APK文件包含了Java代码，JNI库和资源文件；
		 * JNI库包含arm64-v8a,armeabi等不同CPU的编译结果的集合，这些都会编译进 APK 文件；
		 * 在安装应用时，系统会自动选择最合适的JNI版本，其他版本不会占用空间；
		 * 参考TuSDK Demo的APK 大小，除去资源和JNI库，SDK本身的大小约2M；
		 * 
		 * 开发文档:http://tusdk.com/doc
		 */

		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

		// 初始化 Bugly SDK
		CrashReport.initCrashReport(getApplicationContext(), "09e008786d", true);

		// 设置资源类，当 Application id 与 Package Name 不相同时，必须手动调用该方法, 且在 init 之前执行。
		 TuSdk.setResourcePackageClazz(org.lasque.tusdkvideodemo.R.class);
		
		// 自定义 .so 文件路径，在 init 之前调用
		// NativeLibraryHelper.shared().mapLibrary(NativeLibType.LIB_CORE, "libtusdk-library.so 文件路径");
		// NativeLibraryHelper.shared().mapLibrary(NativeLibType.LIB_IMAGE, "libtusdk-image.so 文件路径");

		// 设置输出状态，建议在接入阶段开启该选项，以便定位问题。
		this.setEnableLog(true);
		// 设置Log输出为问题  建议在接入阶段或者测试阶段打开，用于测试问题。
		TLog.enableLog2File(false);

		// 在调试集成测试阶段，可能会出现一些很难定位的问题，
		// 开发人员可以打开如下配置，尝试在真机运行，复现该类问题 ，
		// 然后将 sdcard 根目录下的 log/tusdk 目录日志上报给涂图开发者，以便我们更好的定位问题。
		TuSdkMonitor.setEnableCheckGLError(false) // 开启 GL 日志检测
				.setEnableCheckFrameImage(false); // 开启 GL 图像帧检测

		/**
	     *  初始化SDK，应用密钥是您的应用在 TuSDK 的唯一标识符。每个应用的包名(Bundle Identifier)、密钥、资源包(滤镜、贴纸等)三者需要匹配，否则将会报错。
	     *
	     *  @param appkey 应用秘钥 (请前往 http://tusdk.com 申请秘钥)
	     */
		this.initPreLoader(this.getApplicationContext(), "c863a73a6e0294bc-04-ewdjn1");


		/**
	     *  指定开发模式,需要与lsq_tusdk_configs.json中masters.key匹配， 如果找不到devType将默认读取master字段
	     *  如果一个应用对应多个包名，则可以使用这种方式来进行集成调试。
	     */
		// this.initPreLoader(this.getApplicationContext(), "12aa4847a3a9ce68-04-ewdjn1", "debug");

		// 如果不想继承TuSdkApplication，直接在自定义Application.onCreate()方法中调用以下方法
		// 初始化全局变量
		// TuSdk.enableDebugLog(true);
		// 开发ID (请前往 http://tusdk.com 获取您的APP 开发秘钥)
		// TuSdk.init(this.getApplicationContext(), "12aa4847a3a9ce68-04-ewdjn1");
		// 需要指定开发模式 需要与lsq_tusdk_configs.json中masters.key匹配， 如果找不到devType将默认读取master字段
		// TuSdk.init(this.getApplicationContext(), "12aa4847a3a9ce68-04-ewdjn1", "debug");



	}
}