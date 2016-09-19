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
			//����
			session.connect();
			// ����ͨ��
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
			throw new RuntimeException("��ȡԶ��Ŀ¼·������Ϊ��");
		}
		
		try {
			Vector<?> vector = sftp.ls(path);
			// ��ʱΪ�ļ�·��
			if(vector.size() == 1 && !cdDirectory(sftp, path)) {
				SFTPFile transFile = transFile((LsEntry)vector.get(0), path);
				transFile.setParentPath(TextUtil.replaceEndStr(path, transFile.getFileName(), ""));
				result.add(transFile);
			} else {// ��һ���ļ���·������ȡ�ļ������ļ�
				Iterator<?> iterator = vector.iterator();
				while(iterator.hasNext()) {
					Object obj = iterator.next();
					if(obj instanceof LsEntry) {
						SFTPFile transFile = transFile((LsEntry) obj, path);
						//�����ԡ�..���������ļ�
						if(!transFile.getFileName().matches("[\\.]*"))
							result.add(transFile);
					}
				}
			}
		} catch (SftpException e) {
			e.printStackTrace();
			System.err.println("��ȡĿ¼�쳣������·��===>" + path);
		}
		return result;
	}
	
	public static void downloadFile(ChannelSftp sftp, List<SFTPFile> fileList, String outPath) {
		for (SFTPFile file : fileList) {
			downloadFile(sftp, file, outPath);
		}
	}
	
	public static void downloadFile(ChannelSftp sftp, SFTPFile file, String outPath) {
		if(file.isDirectory()) {
			List<SFTPFile> fileList = getDirectory(sftp, file.getFilePath());
			outPath = outPath +"/"+ file.getFileName();
			if(fileList.isEmpty()) {//���ļ���
				File directoryFile = new File(outPath);
				if(!directoryFile.exists()) directoryFile.mkdirs();
			} else {
				downloadFile(sftp, fileList, outPath);
			}
			
		} else {
			downloadFile(sftp, file.getFilePath(), outPath);
		}
	}
	
	public static void downloadFile(ChannelSftp sftp, String filePath, String outPath) {
		File file = new File(outPath);
		if(file.isFile()) {
			System.err.println("�ļ��������·������Ϊ�ļ�·����ӦΪ�ļ���·����=>" + outPath);
			return ;
		} else if(!file.exists()) {
			file.mkdirs();
		}
		
		
		try {
			sftp.get(filePath, outPath, new ProgressMonitor());
		} catch (SftpException e) {
			e.printStackTrace();
			System.err.println("�ļ������쳣==> "+ e.getMessage() + "path=> "+ filePath);
		}
	}
	
	
	public static void uploadFile(ChannelSftp sftp, String uploadPath, String filePath) {
		File file = new File(filePath);
		if(!file.exists()) {
			System.out.println("δ�ҵ���Ҫ�ϴ����ļ�����·�� " + filePath);
			return ;
		}
		boolean isDirectoryExists = cdDirectory(sftp, uploadPath);
		if(!isDirectoryExists) { //Ŀ¼������
			if(!mkdirDirectory(sftp, uploadPath)) {
				System.out.println("Ŀ¼�����ڣ��Ҵ���Ŀ¼ʧ�ܣ�==> "+ uploadPath);
				return ;
			}
		}
		
		if(file.isDirectory()) {
			File[] listFiles = file.listFiles();
			String parentDirectory = uploadPath + "/" + file.getName();
			for (int i = 0; i < listFiles.length; i++) {
				uploadFile(sftp, parentDirectory, listFiles[i].getPath());
			}
		} else {
			try {
				sftp.put(filePath, uploadPath, new ProgressMonitor());
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
			System.err.println("Ŀ¼������ => "+ path);
		}
		
		return false;
	}
	
	public static boolean mkdirDirectory(ChannelSftp sftp, String path) {
		try {
			sftp.mkdir(path);
			System.out.println("����Ŀ¼�ɹ� => " + path);
			return true;
		} catch (SftpException e) {
			e.printStackTrace();
			System.err.println("����Ŀ¼ʧ�� => "+ path);
		}
		return false;
	}
	
	private static SFTPFile transFile(LsEntry ls, String parentPath) {
		SFTPFile file = new SFTPFile();
		if(ls.getLongname().startsWith("d")) {//Ŀ¼
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
