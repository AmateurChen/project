package com.polysoft.policy.sftp;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.polysoft.policy.utils.DataUtil;
import com.polysoft.policy.utils.TextUtil;

public class SFTPManager {

	public static SFTPConnectParamter getConnectParamter17(){
		SFTPConnectParamter paramter = new SFTPConnectParamter();
		paramter.setHost("192.168.180.17");
		paramter.setPort(22);
		paramter.setUsername("root");
		paramter.setPassword("ncl@blrj6");
		return paramter;
	}
	
	public static ChannelSftp connect(SFTPConnectParamter paramter) {
		ChannelSftp result = null;
		JSch jsch = new JSch();
		try {
			Session session = jsch.getSession(paramter.getUsername(), paramter.getHost(), paramter.getPort());
			if(null == session) {
				System.err.println("connected result session is null");
				return result;
			}
			session.setPassword(paramter.getPassword());
			Properties properties = new Properties();
			properties.put("StrictHostKeyChecking", "no");
			session.setConfig(properties);
			//连接
			session.connect();
			// 设置通道
			Channel channel = session.openChannel("sftp");
			channel.connect();
			result = (ChannelSftp) channel;
			
		} catch (JSchException e) {
			e.printStackTrace();
			System.err.println("connect exception; msg =>" + e.getMessage());
		}
		
		return result;
	}
	
	
	public static List<SFTPFile> getDirectory(ChannelSftp sftp, String path) {
		List<SFTPFile> result = new ArrayList<SFTPFile>();
		
		if(TextUtil.isEmpty(path)) {
			path = "/";
		}
		try {
			Vector<?> vector = sftp.ls(path);
			Iterator<?> iterator = vector.iterator();
			while(iterator.hasNext()) {
				Object obj = iterator.next();
				if(obj instanceof LsEntry) {
					LsEntry ls = (LsEntry) obj;
					result.add(transFile(ls, path));
				}
			}
		} catch (SftpException e) {
			e.printStackTrace();
			System.err.println("获取目录异常，请检查路径===>" + path);
		}
		return result;
	}
	
	public static void downloadFile(ChannelSftp sftp, List<SFTPFile> fileList, String outPath) {
		for (SFTPFile file : fileList) {
			downloadFile(sftp, file, outPath);
		}
	}
	
	public static void downloadFile(ChannelSftp sftp, SFTPFile file, String outPath) {
		downloadFile(sftp, file.getFilePath(), outPath);
	}
	
	public static void downloadFile(ChannelSftp sftp, String filePath, String outPath) {
		try {
			sftp.get(filePath, outPath, new ProgressMonitor());
		} catch (SftpException e) {
			e.printStackTrace();
			System.err.println("文件下载异常==> "+ e.getMessage());
		}
	}
	
	
	public static void uploadFile(ChannelSftp sftp, String uploadPath, String filePath) {
		File file = new File(filePath);
		if(!file.exists()) {
			System.out.println("未找到需要上传的文件及其路径 " + filePath);
			return ;
		}
		
		if(file.isDirectory()) {
			String directoryName = file.getName();
			//检查sftp是否存在此目录
			if(cdDirectory(sftp, uploadPath)) {
				
			} else {
				mkdirDirectory(sftp, directoryName);
			}
			
		} else {
			try {
				sftp.put(uploadPath, filePath, new ProgressMonitor());
			} catch (SftpException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static boolean cdDirectory(ChannelSftp sftp, String path) {
		try {
			sftp.cd(path);
			return true;
		} catch (SftpException e) {
			System.err.println("目录不存在 => "+ path);
		}
		
		return false;
	}
	
	public static boolean mkdirDirectory(ChannelSftp sftp, String path) {
		try {
			sftp.mkdir(path);
			return true;
		} catch (SftpException e) {
			e.printStackTrace();
			System.err.println("目录创建失败 => "+ path);
		}
		return false;
	}
	
	private static SFTPFile transFile(LsEntry ls, String parentPath) {
		SFTPFile file = new SFTPFile();
		if(ls.getLongname().startsWith("d")) {//目录
			file.setDirectory(true);
		} else {
			file.setDirectory(false);
		}
		
		file.setParentPath(parentPath);
		file.setFileName(ls.getFilename());
		
		SftpATTRS attrs = ls.getAttrs();
		file.setFileSize(attrs.getSize());
		
		int mTime = attrs.getMTime();
		String modifytime = DataUtil.transForm(mTime, DataUtil.PATTERN_1);
		file.setModifyTime(modifytime);
		
		return file;
	}

	
}
