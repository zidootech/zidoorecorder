package com.zidoo.recorder.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;

import com.zidoo.recorder.tool.FileManagerTool.ScanUSBOnListener;

/**
 * 文件管理界面
 * 
 * @author jiangbo
 * 
 *         2014-3-5
 */
public class DeviceManager {
	private Context					mContext;
	public String					externalStorageDirectory	= "";
	public static ArrayList<String>	deviceFileInfo_list			= new ArrayList<String>();
	public ArrayList<String>		mount_list					= new ArrayList<String>();
	public ScanUSBOnListener		mScanUSBOnListener			= null;
	public boolean					isScan						= true;

	public DeviceManager(Context mContext, ScanUSBOnListener mScanUSBOnListener) {
		this.mContext = mContext;
		this.mScanUSBOnListener = mScanUSBOnListener;
		isScan = true;
		do_exec("mount");
		inItMountData();
	}

	private String do_exec(String cmd) {
		String s = "";
		mount_list.clear();
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				String split[] = line.split(" ");
				if (split != null && split.length >= 3) {
					if (split[2].equals("vfat") || split[2].equals("ntfs") || split[2].equals("ext4") || split[2].equals("extFat") || split[2].equals("ntfs3g")
							|| split[2].equals("fuseblk")) {
						System.out.println("split[1]==" + split[1]);
						mount_list.add(split[1]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public void inItMountData() {
		new Thread(new Runnable() {
			public void run() {
				try {
					deviceFileInfo_list.clear();
					externalStorageDirectory = Environment.getExternalStorageDirectory().getPath();
					deviceFileInfo_list.add(externalStorageDirectory);
					int mount_size = mount_list.size();
					for (int i = 0; i < mount_size; i++) {
						if (!isScan) {
							break;
						}
						String mount_str = mount_list.get(i);
						addMountDevices(mount_str, true);
					}

					String sambaS = samba();
					// if (sambaS != null ) {
					// deviceFileInfo_list.add(sambaS);
					// }

				} catch (Exception e) {
					e.printStackTrace();
				}
				if (mScanUSBOnListener != null) {
					mScanUSBOnListener.onEndScan();
				}
			}
		}).start();
	}

	private String samba() {
		try {
			File file = new File("/mnt/samba");
			if (file != null) {
				File[] file_c = file.listFiles();
				if (file_c != null && file_c.length > 0) {
					for (int i = 0; i < file_c.length; i++) {
						File pathFile = file_c[i];
						if (pathFile != null && pathFile.canRead() && pathFile.canWrite()) {
							// File rFile = new
							// File(pathFile.getAbsolutePath()+"/HdmiRecorder");
							// if
							// (rFile!=null&&rFile.canRead()&&rFile.canWrite())
							// {
							// }
							try {
								File textfile = new File(pathFile.getAbsolutePath()+"/opss258sss369sdfdsfljlsfs0dsf");
								if (textfile.mkdirs()) {
									if (textfile != null && textfile.canRead() && textfile.canWrite()) {
										System.out.println("bob  add == " + pathFile.getAbsolutePath());
										deviceFileInfo_list.add(pathFile.getAbsolutePath());
									}
									textfile.delete();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						
						}
					}

					return "/mnt/samba";
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public ArrayList<String> addMountDevices(String path, boolean isStart) {
		add_deviceFileInfo_list.clear();
		if (externalStorageDirectory.equals(path)) {
			return add_deviceFileInfo_list;
		}
		if (path.contains("usb") || path.contains("uhost")) {
			File usbFile = new File(path);
			if (usbFile != null && usbFile.canRead()) {
				String totalSize = FileOperate.getFileSize(path);
				if (totalSize != null) {
					deviceFileInfo_list.add(path);
					add_deviceFileInfo_list.add(path);
				} else {
					if (!isStart) {
						if (usbFile.isDirectory()) {
							File[] usb_file = usbFile.listFiles();
							if (usb_file != null) {
								for (int i = 0; i < usb_file.length; i++) {
									String totalSize_f = FileOperate.getFileSize(usb_file[i].getAbsolutePath());
									if (totalSize_f != null) {
										deviceFileInfo_list.add(usb_file[i].getAbsolutePath());
										add_deviceFileInfo_list.add(usb_file[i].getAbsolutePath());
									}
								}
							}
						}
					}
				}
			}
		} else if (path.contains("sd")) {
			File sdFile = new File(path);
			if (sdFile != null && sdFile.canRead()) {
				/* 获取SD卡设备路径列表 */
				String totalSize = FileOperate.getFileSize(path);
				if (totalSize != null) {
					deviceFileInfo_list.add(path);
					add_deviceFileInfo_list.add(path);
				}
			}

		} else if (path.contains("samba")) {
			/* 获取sata设备路径列表 */

		}
		return add_deviceFileInfo_list;
	}

	ArrayList<String>	add_deviceFileInfo_list	= new ArrayList<String>();

	public ArrayList<String> addDevices(String path) {
		add_deviceFileInfo_list.clear();
		if (externalStorageDirectory.equals(path)) {
			return add_deviceFileInfo_list;
		}
		if (path.contains("usb") || path.contains("uhost")) {
			// getUSBPath(path);
			setUSBPath(path);
		} else if (path.contains("sd")) {
			File sdFile = new File(path);
			if (sdFile != null && sdFile.isDirectory() && sdFile.canRead()) {
				/* 获取SD卡设备路径列表 */
				String totalSize = FileOperate.getFileSize(path);
				if (totalSize != null) {
					deviceFileInfo_list.add(path);
					add_deviceFileInfo_list.add(path);
				}
			}

		} else if (path.contains("samba")) {
			/* 获取sata设备路径列表 */

		}
		return add_deviceFileInfo_list;
	}

	public void setUSBPath(String path) {
		File usbFile = new File(path);
		if (usbFile != null && usbFile.isDirectory() && usbFile.canRead()) {
			String totalSize = FileOperate.getFileSize(path);
			if (totalSize != null) {
				deviceFileInfo_list.add(path);
				add_deviceFileInfo_list.add(path);
			}
		}
	}

	// public void getUSBPath(String path) {
	// File usbFile = new File(path);
	// if (usbFile != null && usbFile.isDirectory() && usbFile.canRead()) {
	// String totalSize = FileOperate.getFileSize(path);
	// if (totalSize == null) {
	// File path_file = new File(path);
	// if (path_file.isDirectory()) {
	// File path_f[] = path_file.listFiles();
	// for (int i = 0; i < path_f.length; i++) {
	// if (path_f[i].isDirectory()) {
	// getUSBPath(path_f[i].getAbsolutePath());
	// }
	// }
	// }
	// } else {
	// deviceFileInfo_list.add(path);
	// deviceFileInfo_map.put(path, path);
	// add_deviceFileInfo_list.add(path);
	// }
	// }
	// }

	public String remountDevice(String path) {
		int size = deviceFileInfo_list.size();
		for (int i = 0; i < size; i++) {
			if (deviceFileInfo_list.get(i).contains(path)) {
				String deviceFileInfo = deviceFileInfo_list.get(i);
				deviceFileInfo_list.remove(i);
				return deviceFileInfo;
			}
		}
		return null;
	}

}
