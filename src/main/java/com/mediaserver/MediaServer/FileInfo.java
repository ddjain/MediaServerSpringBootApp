package com.mediaserver.MediaServer;

public class FileInfo {
	String filename;
	String path;
	boolean isPlayable = false;

	public boolean isPlayable() {
		return isPlayable;
	}

	public void setPlayable(boolean isPlayable) {
		this.isPlayable = isPlayable;
	}

	public FileInfo(String filename, String path, boolean isPlayable) {
		super();
		this.filename = filename;
		this.path = path;
		this.isPlayable = isPlayable;
	}

	public FileInfo(String filename, String path) {
		super();
		this.filename = filename;
		this.path = path;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	public String toString() {
		return "FileInfo [filename=" + filename + ", path=" + path + ", isPlayable=" + isPlayable + "]";
	}
}
