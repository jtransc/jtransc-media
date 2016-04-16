import java.io.File;

public class LwjglFiles {
	static public File getResource(String path) {
		return new File(LwjglFiles.class.getClassLoader().getResource(path).getPath());
		//String basePath = (new File(LwjglFiles.class.getClassLoader().getResource(".").getPath())).getAbsolutePath();
		//return new File(basePath + "/" + path);
	}
}
