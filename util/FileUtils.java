package assignment.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
	public static byte[] readFileBytes(String filePath) throws IOException{
		Path path = Paths.get(filePath);
		return Files.readAllBytes(path);
	}
	
	public static void saveFileBytes(byte[] bytes, String fullPathTargetLocation, boolean force) throws IOException{
		File file = new File(fullPathTargetLocation);
		if(file.exists()){
			if(force){
				file.delete();
			}else{
				System.err.println("File - " + fullPathTargetLocation + " already exists!!");
				throw new RuntimeException("File - " + fullPathTargetLocation + " already exists!!");
			}
		}
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(fullPathTargetLocation);
			fos.write(bytes);
		}finally{
			if(fos!=null){
				fos.close();
			}
		}
	}
}
