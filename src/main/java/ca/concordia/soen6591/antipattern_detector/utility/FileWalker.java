package ca.concordia.soen6591.antipattern_detector.utility;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FileWalker {
	private String dirPath;
	
	
	public FileWalker(String dirPath) {
		this.dirPath = dirPath;
	}

	/**
	 @return A list of Path objects for all the .java files under the directory specified by dirPath.
	 @throws IOException If an I/O error occurs while accessing the file system.
	 */
	public List<Path> filewalk() throws IOException {
		Path path = Paths.get(dirPath);
		List<Path> paths = findByFileExtension(path, ".java");
		return paths;
		
	}

	/**
	 Finds all files with the given file extension in the provided directory path recursively.
	 @param path the directory path to search in
	 @param fileExtension the file extension to filter by
	 @return a List of Paths to files that have the given file extension
	 @throws IOException if there is an error accessing the file system
	 @throws IllegalArgumentException if the provided path is not a directory
	 */
	private List<Path> findByFileExtension(Path path, String fileExtension) throws IOException {

		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("The provided path is not a directory. Please provide a valid directory path.");
		}

		List<Path> result;
		try (Stream<Path> walk = Files.walk(path)) {
			result = walk.filter(Files::isRegularFile)
					.filter(p -> p.getFileName().toString().endsWith(fileExtension)).collect(Collectors.toList());
		}
		return result;

	}

}
