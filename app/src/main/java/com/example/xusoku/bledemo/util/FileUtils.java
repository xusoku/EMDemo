package com.example.xusoku.bledemo.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;

/**
 * 文件与流处理工具�?<br>
 * 
 * <b>创建时间</b> 2014-8-14
 * 
 * @author kymjs (https://github.com/kymjs)
 * @version 1.1
 */

public final class FileUtils {
	/**
	 * �?测SD卡是否存�?,包括外部sd卡和虚拟的sd�?
	 */
	public static boolean isSDCardEnable() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable();
	}

	/**
	 * 得到SD卡根目录
	 */
	public static String getSDCardPath() {

		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * 从指定文件夹获取文件
	 * 
	 * @return 如果文件不存在则创建,如果无法创建文件或文件名为空则返回null
	 */
	public static File getFile(String folderName, String fileName) {

		File file = new File(getFolder(folderName), fileName);
		try {
			file.createNewFile();
			if (!file.isFile()) {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
		return file;
	}

	/**
	 * 从指定文件夹获取文件
	 * 
	 * @return 如果文件不存在则创建,如果如果无法创建文件或文件名为空则返回null
	 */
	public static String getFilePath(String folderName, String fileName) {
		File file = getFile(folderName, fileName);
		if (file != null) {
			return file.getAbsolutePath();
		}
		return null;
	}

	/**
	 * 获取文件夹对�?
	 * 
	 * @return 返回SD卡下的指定文件夹对象，若文件夹不存在则创�?
	 */
	public static File getFolder(String folderName) {
		File file = null;
		if (folderName.startsWith(File.separator)) {
			file = new File(getSDCardPath() + folderName);
		} else {
			file = new File(getSDCardPath() + File.separator + folderName);
		}
		file.mkdirs();
		return file;
	}

	/**
	 * 获取SD卡下指定文件夹的绝对路径 getFolderPath("home/admin") = "sd/home/admin"
	 * 
	 * @return 返回SD卡下的指定文件夹的绝对路�?
	 */
	public static String getFolderPath(String folderName) {
		File fileFolder = getFolder(folderName);
		if (fileFolder != null) {
			return fileFolder.getAbsolutePath();
		}
		return null;
	}

	/**
	 * 得到SD卡缓存根目录，�?�常是在 /sdcard/Android/data/<application package>/cache 这个路径下面
	 * 
	 * isExternalStorageRemovable()设备的外存是否是可以拆卸的，比如SD卡，是则返回true
	 * isExternalStorageEmulated()设备的外存是否是用内存模拟的，是则返回true�? getExternalCacheDir
	 * �?getExternalFilesDir getFilesDir() getCacheDir()
	 * 这些目录都是属于应用的，当应用被卸载的时候，里面的内容都会被移除
	 */
	public static File getCacheDirectory(Context context, String folderName) {

		File appCacheDir = null;
		if (isSDCardEnable()) {
			/*
			 * /sdcard/Android/data/<application package>/cache
			 */
			appCacheDir = context.getExternalCacheDir();
		}
		if (appCacheDir == null) {
			/*
			 * /data/data/<application package>/cache
			 */
			appCacheDir = context.getCacheDir();
		}
		if (!TextUtils.isEmpty(folderName)) {
			appCacheDir = new File(appCacheDir, folderName);
		}

		if (!appCacheDir.exists()) {
			appCacheDir.mkdirs();
		}

		return appCacheDir;
	}

	public static boolean deleteCacheDir(Context context) {
		File cacheFile;
		if (isSDCardEnable()) {
			cacheFile = context.getExternalCacheDir();
		} else {
			cacheFile = context.getCacheDir();
		}
		// 目录此时为空，可以删除

		return deleteFile(cacheFile);
	}

	/**
	 * 
	 * @return 是否删除成功
	 */
	public static boolean deleteFile(File file) {

		if (!file.exists()) {
			return true;
		}
		if (file.isFile()) {
			return file.delete();
		}
		if (!file.isDirectory()) {
			return false;
		}
		for (File f : file.listFiles()) {
			deleteFile(f);
		}
		return file.delete();// 目录此时为空，可以删除
	}

	public static long getCacheFileSize(Context context) {
		long s = 0;
		File cacheFile;
		if (isSDCardEnable()) {
			cacheFile = context.getExternalCacheDir();
		} else {
			cacheFile = context.getCacheDir();
		}

		try {
			s = getFileSize(cacheFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return s;
	}

	/* 递归 ：取得文件或文件夹大小 */
	public static long getFileSize(File f) {
		long size = 0;
		if (f.isDirectory()) {// 说明是文件夹
			File flist[] = f.listFiles();
			for (int i = 0; i < flist.length; i++) {
				size = size + getFileSize(flist[i]);
			}
		} else {
			if (f.exists() && f.isFile()) {
				size = f.length();
			}
		}

		return size;
	}

	public static String FormatFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "0.00B";
		if (fileS < 1024) {
			if (fileS != 0) {
				fileSizeString = df.format((double) fileS) + "B";
			}

		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static long getFileCount(File f) {// 递归求取目录文件个数
		long size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileCount(flist[i]);
				size--;
			}
		}
		return size;
	}

	/**
     * get suffix of file from path
     *
     * <pre>
     *      getFileExtension(null)               =   ""
     *      getFileExtension("")                 =   ""
     *      getFileExtension("   ")              =   "   "
     *      getFileExtension("a.mp3")            =   "mp3"
     *      getFileExtension("a.b.rmvb")         =   "rmvb"
     *      getFileExtension("abc")              =   ""
     *      getFileExtension("c:\\")              =   ""
     *      getFileExtension("c:\\a")             =   ""
     *      getFileExtension("c:\\a.b")           =   "b"
     *      getFileExtension("c:a.txt\\a")        =   ""
     *      getFileExtension("/home/admin")      =   ""
     *      getFileExtension("/home/admin/a.txt/b")  =   ""
     *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
     * </pre>
     * @param filePath   路径
     * @return  信息
     */
    public static String getFileExtension(String filePath) {

        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        int extenPosi = filePath.lastIndexOf(".");
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

	/**
	 * 将content://形式的uri转为实际文件路径
	 * 
	 * @param context
	 *            上下文
	 * @param uri
	 *            地址
	 * @return uri转为实际文件路径
	 */
	public static String uriToPath(Context context, Uri uri) {

		Cursor cursor = null;
		try {
			if (uri.getScheme().equalsIgnoreCase("file")) {
				return uri.getPath();
			}
			cursor = context.getContentResolver().query(uri, null, null, null,
					null);
			if (cursor.moveToFirst()) {
				return cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA)); // 图片文件路径
			}
		} catch (Exception e) {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
			return null;
		}
		return null;
	}

	

	/**
	 * 从文件中读取文本
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readTextFile(String folderName, String fileNmae) {
		InputStream is = getFileInputStream(folderName, fileNmae);
		return input2String(is);
	}

	/**
	 * 从文件中读取文本
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readTextFile(File file) {
		InputStream is = getFileInputStream(file);
		return input2String(is);
	}

	/**
	 * 从文件中读取图片
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap readBitmapFile(String folderName, String fileNmae) {
		InputStream is = getFileInputStream(folderName, fileNmae);
		if (is==null) {
			return null;
		}
		return BitmapFactory.decodeStream(is);
	}

	/**
	 * 从文件中读取图片
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap readBitmapFile(File file) {
		InputStream is = getFileInputStream(file);
		if (is==null) {
			return null;
		}
		return BitmapFactory.decodeStream(is);
	}

	/**
	 * 从文件中读取
	 * 
	 * @param filePath
	 * @return
	 */
	public static byte[] readBytesFile(String folderName, String fileNmae) {
		InputStream is = getFileInputStream(folderName, fileNmae);
		return input2Bytes(is);
	}

	/**
	 * 从文件中读取
	 * 
	 * @param filePath
	 * @return
	 */
	public static byte[] readBytesFile(File file) {
		InputStream is = getFileInputStream(file);
		return input2Bytes(is);
	}

	/**
	 * 从assets中读取文�?
	 * 
	 * @param name
	 * @return
	 */
	public static String readAssetsFile(Context context, String fileName) {
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open(fileName);
		} catch (Exception e) {
			throw new RuntimeException(FileUtils.class.getName()
					+ ".readFileFromAssets---->" + fileName + " not found");
		}
		return input2String(is);
	}

	/**
	 * 将字符串保存到本地
	 */
	public static boolean saveTextToFile(String content, String folderName,
			String fileName) {
		byte[] data = content.getBytes();
		File file = getFile(folderName, fileName);
		return saveBytesToFile(data, file);
	}

	/**
	 * 将字符串保存到本地
	 */
	public static boolean saveTextToFile(String content, File file) {
		byte[] data = content.getBytes();
		return saveBytesToFile(data, file);
	}

	/**
	 * 将文件保存到本地
	 */
	public static boolean saveBytesToFile(byte[] bytes, String folderName,
			String fileName) {
		File file = getFile(folderName, fileName);
		return saveBytesToFile(bytes, file);
	}

	/**
	 * 将文件保存到本地
	 */
	public static boolean saveBytesToFile(byte[] bytes, File file) {

		if (file==null||bytes==null) {
			return false;
		}
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while (-1 != (len = is.read(buffer))) {
				os.write(buffer, 0, len);
			}
			os.flush();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			closeIO(is, os);
		}

	}

	/**
	 * 图片写入文件
	 * 
	 * @param bitmap
	 *            图片
	 * @param filePath
	 *            文件路径
	 * @return 是否写入成功
	 */
	public static boolean saveBitmapToFile(Bitmap bitmap, File file) {
		if (file==null||bitmap==null) {
			return false;
		}
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		OutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(file), 8 * 1024);
			return bitmap.compress(CompressFormat.JPEG, 100, os);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			closeIO(os);

		}
		return false;
	}
	public static boolean saveBitmapToFile(Bitmap bitmap, String folderName,
			String fileName) {
		File file = getFile(folderName, fileName);
		return saveBitmapToFile(bitmap,file);
	}

	public static boolean saveStreamToFile(InputStream inStream, File file) {
		if (file==null||inStream==null) {
			return false;
		}
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			byte data[] = new byte[1024];
			int length = -1;
			while ((length = inStream.read(data)) != -1) {
				os.write(data, 0, length);
			}
			os.flush();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			closeIO(inStream, os);
		}

	}

	public static boolean saveStreamToFile(InputStream inStream,
			String folderName, String fileName) {

		File file = getFile(folderName, fileName);
		return saveStreamToFile(inStream, file);

	}

	
	/**
	 * 关闭
	 * 
	 * @param closeables
	 */
	public static void closeIO(Closeable... closeables) {
		if (null == closeables || closeables.length <= 0) {
			return;
		}
		for (Closeable cb : closeables) {
			try {
				if (null == cb) {
					continue;
				}
				cb.close();
			} catch (IOException e) {
				throw new RuntimeException(
						FileUtils.class.getClass().getName(), e);
			}
		}
	}

	/**
	 * 从文件中读取文本
	 * 
	 * @param filePath
	 * @return
	 */
	public static InputStream getFileInputStream(String folderName,
			String fileNmae) {
		File file=new File(getFolder(folderName),fileNmae);
		return getFileInputStream(file);
	}

	public static InputStream getFileInputStream(File file) {
		if (!file.exists()) {
            return null;
        }
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (Exception e) {
			 return null;
		}

		return is;
	}
	/**
	 * 输入流转byte[]<br>
	 */
	public static final byte[] input2Bytes(InputStream is) {
		if (is == null) {
			return null;
		}
		byte[] in2b = null;
		BufferedInputStream inStream = new BufferedInputStream(is);

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int len = 0;
			while (-1 != (len = inStream.read(buffer))) {
				outStream.write(buffer, 0, len);
			}

			in2b = outStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeIO(inStream, outStream);
		}

		return in2b;
	}

	/**
	 * 输入流转字符�?
	 * 
	 * @param is
	 * @return �?个流中的字符�?
	 */
	public static String input2String(InputStream is) {
		if (null == is) {
			return "";
		}
		StringBuilder resultSb = new StringBuilder("");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			String data;
			while (null != (data = br.readLine())) {
				if (!resultSb.toString().equals("")) {
					resultSb.append("\r\n");
				}
				resultSb.append(data);
			}
		} catch (Exception ex) {
		} finally {
			closeIO(is);
		}
		return resultSb.toString();
	}

}