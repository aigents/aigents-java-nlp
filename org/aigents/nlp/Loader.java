package org.aigents.nlp;

import java.util.*;
import java.util.logging.Logger;
import java.io.*;

public class Loader {
	public static boolean check_dir(String dir_path, boolean create, String verbose) throws Exception {
		if (verbose == null) verbose = "none";
		Logger logger = Logger.getLogger("Loader.check_dir");
		File f = new File(dir_path);
		if (f.exists()) {
			return true;
		} else {
			if (create) {
				f.mkdirs();
				return true;
			}
		}
		throw new FileNotFoundException("No directory " + dir_path);
	}
	
	public static ArrayList<String> check_dir_files(String dir_path, String verbose) throws Exception {
		if (verbose == null) verbose = "none";
		Logger logger = Logger.getLogger("Loader.check_dir_files");
		ArrayList<String> files = new ArrayList<>();
		String path;
		if (dir_path.charAt(dir_path.length()-1) != '/') {
			path = dir_path + "/";
		} else path = dir_path;
		File f = new File(dir_path);
		if (f.exists()) {
			logger.info("Directory " + path + " exists.");
			for (String filename : f.list()) {
				files.add(path + filename);
				logger.info(filename);
			}
		} else {
			throw new FileNotFoundException("No directory " + dir_path);
		}
		return files;
	}
	
	public static Object[] check_mst_files(String input_dir, String verbose) throws Exception {
		if (verbose == null) verbose = "none";
		Logger logger = Logger.getLogger("Loader.check_mst_files");
		if (check_dir(input_dir, false, verbose)) {
			ArrayList<String> files = check_dir_files(input_dir, verbose);
			if (files.size() > 0) {
				logger.info(files.toString());
				HashMap<String, String> response = new HashMap<>();
				response.put("input files", files.toString());
				for (int i = 0; i < files.size(); i++) {
					String file = files.get(i);
					if (new File(file).isFile()) {
						logger.info("File #" + i + " " + file + " checked");
					} else {
						logger.severe("File #" + i + " " + file + " check failed");
					}
				}
				return new Object[] {files, response};
			} else {
				logger.severe("Input directory " + input_dir + " is empty");
				HashMap<String, String> response = new HashMap<>();
				response.put("check_mst_file_error", "empty input directory");
				return new Object[] {new ArrayList<>(), response};
			}
		} else {
			logger.severe("No input directory");
			HashMap<String, String> response = new HashMap<>();
			response.put("check_mst_file_error", "no input directory");
			return new Object[] {new ArrayList<>(), response};
		}
	}
	
	public static boolean check_dict(String file_path) {
		return new File(file_path).isFile();
	}
	
	public static boolean check_ull	(String file_path) {
		return new File(file_path).isFile();
	}
	
	public static boolean check_corpus(String input_dir, String verbose) throws Exception {
		if (verbose == null) verbose = "none";
		if (check_dir(input_dir, false, verbose)) {
			ArrayList<String> files = check_dir_files(input_dir, verbose);
			if (files.size() > 0) {
				HashMap<String, String> response = new HashMap<>();
				response.put("input files", files.toString());
				ArrayList<String> parses = new ArrayList<>();
				for (int i = 0; i < files.size(); i++) {
					String file = files.get(i);
					if (check_ull(file)) {
						Scanner read = new Scanner(file);
						ArrayList<String> lines = new ArrayList<>();
						while (read.hasNextLine()) {
							lines.add(read.nextLine());
						}
						if (lines.size() > 0) {
							for (String line : lines) {
								parses.add(line);
							}
							parses.add("");
						}
					}
				}
				return parses.size() > 0;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public static String check_path(String par, String t, String... parameters) throws Exception {
		if (t == null) t = "else";
		String module_path, path;
		HashMap<String, String> map = new HashMap<>();
		for (String param : parameters) {
            String[] pair = parseKeyValue(param);
            map.put(pair[0], pair[1]);
        }
		if (map.keySet().contains("module_path")) {
			module_path = map.get("module_path");
		} else {
			return null;
		}
		if (map.keySet().contains(par)) {
			path = map.get(par);
			if (path.length() == 0) {
				path = module_path;
			} else if (!path.contains("home")) {
				if (path.toCharArray()[0] != '/') {
					path = '/' + path;
				}
				path = module_path + path;
			}
		} else {
			System.out.println("\""+ par + "\" not in kwargs:" + parameters);
			return null;
		}
		if (t.contains("dir")) {
			if (check_dir(path, true, "max")) {
				return path;
			} else return null;
		} else if (t.contains("fil")) {
			if (new File(path).isFile()) {
				return path;
			} else return null;
		} else if (t.contains("dic")) {
			if (check_dict(path)) {
				return path;
			} else return null;
		} else if (t.contains("cor")) {
			if (check_corpus(path, null)) {
				return path;
			} else return null;
		} else if (t.contains("ull")) {
			if (check_ull(path)) {
				return path;
			} else return null;
		} else {
			if (check_dir(path, false, "none") || new File(path).isFile()) {
				return path;
			} else return null;
		}
	}
	
	private static String[] parseKeyValue(String token) {
        if (token == null || token.equals("")) {
            return null;
        }

        token = token.trim();

        int index = token.indexOf("=");

        if (index == -1) {
            return new String[] { token.trim(), "" };
        } else {
            return new String[] { token.substring(0, index).trim(),
                    token.substring(index + 1).trim() };
        }

    }
}