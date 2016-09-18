package com.polysoft.policy;

import com.jcraft.jsch.ChannelSftp;
import com.polysoft.policy.sftp.SFTPManager;

public class Main {

	
	public static void main(String[] args) {
		String downFilePath = "/-20160721164512-1.tif";
		String outPath = "C:\\Users\\Thinkpad\\Desktop\\sftp\\";
		ChannelSftp sftp = SFTPManager.connect(SFTPManager.getConnectParamter17());
		
		
		
		SFTPManager.downloadFile(sftp, downFilePath, outPath);
		System.out.println("===========>");
	}
	
}
