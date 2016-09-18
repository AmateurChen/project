package com.polysoft.policy.sftp;

import java.text.DecimalFormat;

import com.jcraft.jsch.SftpProgressMonitor;

public class ProgressMonitor implements SftpProgressMonitor{

	private long fileSize = 0;
	private String title ;
	private long downloadSize = 0;
	
	@Override
	public void init(int paramInt, String paramString1, String paramString2,
			long paramLong) {
		// TODO Auto-generated method stub
		this.fileSize = paramLong;
		
		if(paramInt == PUT) {
			title = "�ϴ��ļ� " + paramString2;
		} else if(paramInt == GET) {
			title ="�����ļ�" + paramString1 ;
		}
	}

	@Override
	public boolean count(long paramLong) {
		// TODO Auto-generated method stub
		if(0 != fileSize) {
			this.downloadSize += paramLong;
			double d = ((double)downloadSize * 100)/(double)fileSize;
			DecimalFormat df = new DecimalFormat( "#.##"); 
			System.out.println(this.title + " ����===>" + df.format(d) + "%");
		}
		
		return true;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		System.out.println(this.title + " ���");
	}

}
