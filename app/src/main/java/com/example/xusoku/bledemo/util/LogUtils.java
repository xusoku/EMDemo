package com.example.xusoku.bledemo.util;

/**
 * the logger
 * 
 * @author MaTianyu 2014-1-1下午4:05:39
 */
public final class LogUtils {

	/**
	 * isPrint: print switch, true will print. false not print
	 */
	public static boolean isPrint = true;

	private LogUtils() {
	}

	/**
	 * ******************** Log **************************
	 */
	public static int v(String tag, String msg) {
		return isPrint && msg != null ? android.util.Log.v(tag, msg) : -1;
	}

	public static int d(String tag, String msg) {
		return isPrint && msg != null ? android.util.Log.d(tag, msg) : -1;
	}

	public static int i(String tag, String msg) {
		return isPrint && msg != null ? android.util.Log.i(tag, msg) : -1;
	}

	public static int w(String tag, String msg) {
		return isPrint && msg != null ? android.util.Log.w(tag, msg) : -1;
	}

	public static int e(String tag, String msg) {
		return isPrint && msg != null ? android.util.Log.e(tag, msg) : -1;
	}

	/**
	 * ******************** Log with Throwable **************************
	 */
	public static int v(String tag, String msg, Throwable tr) {
		return isPrint && msg != null ? android.util.Log.v(tag, msg, tr) : -1;
	}

	public static int d(String tag, String msg, Throwable tr) {
		return isPrint && msg != null ? android.util.Log.d(tag, msg, tr) : -1;
	}

	public static int i(String tag, String msg, Throwable tr) {
		return isPrint && msg != null ? android.util.Log.i(tag, msg, tr) : -1;
	}

	public static int w(String tag, String msg, Throwable tr) {
		return isPrint && msg != null ? android.util.Log.w(tag, msg, tr) : -1;
	}

	public static int e(String tag, String msg, Throwable tr) {
		return isPrint && msg != null ? android.util.Log.e(tag, msg, tr) : -1;
	}

	/**
	 * ******************** TAG use Object Tag **************************
	 */
	public static int v(Object tag, String msg) {
		return isPrint ? android.util.Log
				.v(tag.getClass().getSimpleName(), msg) : -1;
	}

	public static int d(Object tag, String msg) {
		return isPrint ? android.util.Log
				.d(tag.getClass().getSimpleName(), msg) : -1;
	}

	public static int i(Object tag, String msg) {
		return isPrint ? android.util.Log
				.i(tag.getClass().getSimpleName(), msg) : -1;
	}

	public static int w(Object tag, String msg) {
		return isPrint ? android.util.Log
				.w(tag.getClass().getSimpleName(), msg) : -1;
	}

	public static int e(Object tag, String msg) {
		return isPrint ? android.util.Log
				.e(tag.getClass().getSimpleName(), msg) : -1;
	}
}
