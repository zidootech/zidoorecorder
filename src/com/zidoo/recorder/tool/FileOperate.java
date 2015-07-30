package com.zidoo.recorder.tool;

import android.os.StatFs;

/**
 * 文件大小处理类
 * 
 * @author jiangbo
 * 
 *         2014-2-19
 */
public class FileOperate {

	/**
	 * 得到特定目录下文件总大小
	 * 
	 * 
	 * @author jiangbo
	 * @param filePath
	 * @return if error return null
	 */
	public static String getFileSize(String filePath) {
		if (filePath == null || filePath.equals("")) {
			return null;
		}
		try {
			long totalSize = getTotalSize(filePath);
			if (totalSize <= 0) {
				return null;
			}
			String fileSize = toSize(totalSize);
			return fileSize;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 得到特定目录下文件总大小
	 * 
	 * 
	 * @author jiangbo
	 * @param filePath
	 * @return if error return null
	 */
	public static String getFileAvailableSize(String filePath) {
		if (filePath == null || filePath.equals("")) {
			return null;
		}
		try {
			long totalSize = getAvailableSize(filePath);
			if (totalSize <= 0) {
				return null;
			}
			String fileSize = toSize(totalSize);
			return fileSize;
		} catch (Exception e) {
			return null;
		}
	}

	private static long kb = 1024;
	public static long mb = 1024 * 1024;
	public static long gb = 1024 * 1024 * 1024;

	public static String toSize(long mbyte) {
		if (mbyte >= gb) {
			double gb_p = (double) mbyte / gb;
			if (gb_p >= 1024) {
				return String.format("%.2f T ", gb_p / 1024);
			} else {
				return String.format("%.2f G ", gb_p);
			}
		} else if (mbyte >= mb)
			return String.format("%.2f M ", (double) mbyte / mb);
		else if (mbyte >= kb)
			return String.format("%.2f K ", (double) mbyte / kb);
		else
			return String.format("%d b", mbyte);
	}

	/* 获取全部空间,..GB */
	public static long getTotalSize(String path) {
		try {
			StatFs statfs = new StatFs(path);
			long totalBlocks = statfs.getBlockCount();
			long blockSize = statfs.getBlockSize();
			long totalsize = blockSize * totalBlocks;
			return totalsize;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

	/* 获取可用空间 */
	public static long getAvailableSize(String path) {
		try {
			StatFs statfs = new StatFs(path);
			long blockSize = statfs.getBlockSize();
			long availBlocks = statfs.getAvailableBlocks();
			long availsize = blockSize * availBlocks;
			return availsize;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;

	}

}
