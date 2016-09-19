package com.polysoft.policy.sftp;

import java.io.File;

import com.polysoft.policy.utils.TextUtil;

public class SFTPFile {
	private String parentPath;
	
	private String fileName;
	
	private boolean isDirectory;
	
	private long fileSize;
	
	private String modifyTime;

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
		
		if(!TextUtil.isEndSeparator(parentPath)){
			this.parentPath = parentPath + "/";
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	public String getFilePath() {
		return this.parentPath + this.fileName;
	}
}
