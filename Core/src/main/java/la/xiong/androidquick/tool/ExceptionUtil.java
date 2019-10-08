package la.xiong.androidquick.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常处理工具类
 * 
 * @author yanliang
 * 
 */
public class ExceptionUtil {

	public static final String TAG = "Exception";
	/**
	 * 异常信息
	 */
	private static String result = null;
	/**
	 * 用来存储设备信息
	 */
	private static Map<String, String> infos = new HashMap<String, String>();
	/**
	 * 用于格式化日期,作为日志文件名的一部分
	 */
	@SuppressLint("SimpleDateFormat")
	private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	public static void handle(Exception e, Context context) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		// 异常信息转为字符串
		e.printStackTrace(printWriter);
		result = stringWriter.toString();
		Log.d(TAG, result);
//		// 收集设备参数信息
//		collectDeviceInfo(MyApplication.getInstance());
//		// 处理错误信息
//		saveException(context);
	}
	public static void handle(Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		// 异常信息转为字符串
		e.printStackTrace(printWriter);
		result = stringWriter.toString();
		Log.i(TAG, result);
//		// 收集设备参数信息
//		collectDeviceInfo(MyApplication.getInstance());
//		// 处理错误信息
//		saveException();
	}
//
//	/**
//	 * 收集设备参数信息
//	 *
//	 * @param ctx
//	 */
//	public static void collectDeviceInfo(Context ctx) {
//		try {
//			PackageManager pm = ctx.getPackageManager();
//			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
//			StatusRecordBiz biz = new StatusRecordBiz(ctx);
//			if (pi != null) {
//				String versionName = pi.versionName == null ? "null" : pi.versionName;
//				String versionCode = pi.versionCode + "";
//				infos.put("versionName", versionName);
//				infos.put("versionCode", versionCode);
////				infos.put("cityName", biz.getCityname());
////				infos.put("lat", biz.getLat());
////				infos.put("lng", biz.getLng());
//
//			}
//		} catch (NameNotFoundException e) {
//			Log.d(TAG, e.toString());
//		}
//		Field[] fields = Build.class.getDeclaredFields();
//		for (Field field : fields) {
//			try {
//				field.setAccessible(true);
//				infos.put(field.getName(), field.get(null).toString());
//				Log.d(TAG, field.getName() + " : " + field.get(null));
//			} catch (Exception e) {
//				Log.d(TAG, e.toString());
//			}
//		}
//		infos.put("sdk", android.os.Build.VERSION.SDK_INT + "");
//		// 当前发生错误时的网络状态
//		infos.put("netType", NetTypeUtil.getNetworkType(ctx) + "");
//	}

//	/**
//	 * 处理错误信息
//	 */
//	private static void saveException(Context context) {
//		final StringBuffer sb = new StringBuffer();
//		for (Map.Entry<String, String> entry : infos.entrySet()) {
//			String key = entry.getKey();
//			String value = entry.getValue();
//			sb.append(key + "=" + value + "\n");
//		}
//		// 异常信息
//		if (result != null){
//			sb.append(result);
//			MobclickAgent.reportError(context,sb.toString());
//		}}
/**
	 * 处理错误信息
	 */
	private static void saveException() {
		final StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		// 异常信息
		if (result != null){
			sb.append(result);

		}


//
	}

}
