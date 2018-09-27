package com.mediaserver.MediaServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	HashSet<String> allowedFormats;

	public HomeController() {
		allowedFormats = new HashSet<>();

		allowedFormats.add("mp4");
		allowedFormats.add("mkv");
		allowedFormats.add("mpe4");
		allowedFormats.add("dat");
		allowedFormats.add("mp3");
		allowedFormats.add("3gp");
		allowedFormats.add("mpeg");
		allowedFormats.add("mpg");
	}

	@RequestMapping("/files/{path}")
	public ArrayList<FileInfo> files(@PathVariable String path) {
		path = path.replace("@", "\\");
		System.err.println(path);
		File file = new File(path);
		ArrayList<FileInfo> files = new ArrayList<>();
		if (file.isDirectory() == false) {

			return files;
		}
		String[] list = file.list();
		FileInfo obj;
		for (String f : list) {
			obj = new FileInfo(f, path);

			files.add(obj);
		}
		return files;
	}

	@GetMapping("/folders/{path}")
	public ArrayList<FileInfo> getFolders(@PathVariable String path) {
		path = path.replace("@", "\\");

		File file = new File(path);
		ArrayList<FileInfo> files = new ArrayList<>();

		if (file.isDirectory() == false) {
			return files;
		}
		String[] list = file.list();
		FileInfo obj;

		for (String f : list) {
			obj = new FileInfo(f, path + "\\" + f);
			obj.isPlayable = IsFilePlayable(f);
			files.add(obj);
		}
		return files;
	}

	private boolean IsFilePlayable(String file) {
		if (file.length() < 4) {
			return false;
		}
		return allowedFormats.contains(file.toLowerCase().substring(file.length() - 3, file.length()))
				|| allowedFormats.contains(file.toLowerCase().substring(file.length() - 4, file.length()));
	}

	@GetMapping("/drives")
	public ArrayList<String> getDrives() {
		ArrayList<String> drive = new ArrayList<>();

		File[] drives = File.listRoots();
		if (drives != null && drives.length > 0) {
			for (File aDrive : drives) {
				drive.add(aDrive.toString());
			}
		}
		return drive;
	}

	@GetMapping("/ipaddress")
	public ArrayList<String> getIPAddress() {
		ArrayList<String> ipAddresses = new ArrayList<>();
		String address;
		try {
			address = InetAddress.getLocalHost().getHostAddress().toString();
		} catch (UnknownHostException e) {

			e.printStackTrace();

			return ipAddresses;
		}
		String parts[] = address.split("\\.");

		int last = Integer.valueOf(parts[3]);

		int startIndex = last - 10;
		int lastIndex = last + 10;
		if (startIndex < 100) {
			startIndex = 100;
		}
		if (lastIndex > 255) {
			lastIndex = 255;
		}
		String subnet = parts[0] + "." + parts[1] + "." + parts[2] + ".";
		for (int i = startIndex; i < lastIndex; i++) {
			String ipAddress = subnet + i;

			if (isIpReachable(ipAddress)) {
				ipAddresses.add(ipAddress);
			}
		}
		return ipAddresses;
	}

	private static boolean isIpReachable(String ipAddress) {
		try {
			InetAddress address = InetAddress.getByName(ipAddress);

			if (address.isReachable(20)) {
				return true;
			}
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return false;
	}

	@GetMapping("/video/{path}")
	public ResponseEntity<byte[]> usingResponseEntityBuilderAndHttpHeaders(@PathVariable String path) {
		path = path.replace("@", "\\");
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("content-type", "video/mp4");
		responseHeaders.set("Cache-Control", " max-age=2592000, public");

		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
		String strDate = format.format(date);
		System.out.println(strDate);
		responseHeaders.set("Date", strDate);
		responseHeaders.set("Cache-Control", "max-age=2592000, public");

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		date = new Date(timestamp.getTime() + 2592000);
		strDate = format.format(date);
		responseHeaders.set("Expires", strDate);
		File file;
		try {
			file = new File(path);
			if (!file.isFile()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		date = new Date(file.lastModified());
		strDate = format.format(date);
		responseHeaders.set("Last-Modified", strDate);

		byte[] data = fileToByte(file);
		int size = data.length - 1;
		responseHeaders.set("Content-Length", data.length + "");
		responseHeaders.set("Accept-Ranges", "0-" + size);
		responseHeaders.set("Content-Range", "bytes 0 - " + (size - 1) + "/" + size);
		return ResponseEntity.ok().headers(responseHeaders).body(data);
	}

	public byte[] fileToByte(File file) {
		byte[] bytesArray = new byte[(int) file.length()];
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			fis.read(bytesArray); // read file into bytes[]
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bytesArray;
	}
}
