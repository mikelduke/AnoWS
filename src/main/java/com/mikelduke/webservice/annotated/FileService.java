package com.mikelduke.webservice.annotated;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mikelduke.webservice.annotations.AWS;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public class FileService {
	private static final String CLAZZ = FileService.class.getName();
	private static final Logger LOG = Logger.getLogger(CLAZZ);
	
	public static final String PATH = "/files";

	private Map<String, File> directoryMap = new HashMap<String, File>();
	private boolean showFolders = false;
	
	public FileService(File... directories) throws IOException {
		for (File f : directories) {
			addDirectoryDefaultName(f);
		}
	}

	public void addDirectoryDefaultName(File directory) throws IOException {
		if (!directory.isDirectory() || !directory.canRead()) {
			throw new IOException("Invalid Directory Path " 
					+ directory.getAbsolutePath());
		}
		
		String defaultName = directory.getName();

		addDirectory(defaultName, directory);
	}
	
	public void addDirectory(String name, File directory) throws IOException {
		if (!directory.isDirectory() || !directory.canRead()) {
			throw new IOException("Invalid Directory Path " 
					+ directory.getAbsolutePath());
		}
		
		if (!this.directoryMap.containsKey(name)) {
			this.directoryMap.put(name, directory);
		}
	}
	
	@AWS(path=PATH, startsWith=true, method="GET", description="File Service")
	public AWSResponse getFile(IHTTPSession session) {
		String fileName = session.getUri();
		fileName = fileName.trim().substring(PATH.length(), fileName.length());
		LOG.info("Requested File: " + fileName);
		
		String folderName = getRequestFolderFromFilePath(fileName);
		fileName = fileName.substring(folderName.length() + 1, fileName.length());
		File file = findFile(folderName, fileName);
		
		if (file != null) {
			try {
				if (!file.isDirectory()) {
					return new AWSResponse(200, new FileInputStream(file));
				} else {
					if (showFolders) {
						return new AWSResponse(200, buildDirListing(file));
					} else {
						return new AWSResponse(403);
					}
				}
			} catch (IOException e) {
				LOG.logp(Level.WARNING, CLAZZ, "getFile", "Error Opening File Stream", e);
			}
		}

		return new AWSResponse(404);
	}
	
	public void hideFolders() {
		showFolders = false;
	}
	
	public void showFolders() {
		showFolders = true;
	}
	
	private String getRequestFolderFromFilePath(String fileName) {
		String folderName = "";
		
		if (fileName.startsWith("/")) {
			fileName = fileName.substring(1, fileName.length());
		}
		
		int slashInd = fileName.indexOf("/");
		if (slashInd >= 0) {
			folderName = fileName.substring(0, slashInd);
		}
		
		return folderName;
	}
	
	private File findFile(String folderName, String fileName) {
		LOG.info("Finding Folder: " + folderName + " File: " + fileName);
		File file = null;
		
		File dir = this.directoryMap.get(folderName);
		
		if (dir == null) return null;
		
		String newPath = dir.getAbsolutePath() + fileName;
		LOG.info("New Path: " + newPath);
		
		File testFile = new File(newPath);
		if (testFile.exists() && testFile.canRead()) {
			file = testFile;
		}
		
		return file;
	}
	
	private String buildDirListing(File folder) {
		if (!folder.isDirectory()) return "";
		
		StringBuilder page = new StringBuilder();
		page.append("<html><body>\n");
		page.append("File Listing for ");
		page.append(folder.getName());
		page.append("<br /><br />");
		
		for (File f: folder.listFiles()) {
			page.append("<a href=\"" + f.getName() + "\">" + f.getName() + "</a><br />");
		}
		
		return page.toString();
	}
}
