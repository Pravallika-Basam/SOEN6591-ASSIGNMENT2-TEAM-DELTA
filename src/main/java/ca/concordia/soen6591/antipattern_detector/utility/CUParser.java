package ca.concordia.soen6591.antipattern_detector.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import ca.concordia.soen6591.antipattern_detector.detecters.StaticAnalyzer;

public class CUParser {

    /**
     * Parses the Java file located at the specified file path and returns its corresponding CompilationUnit object.
     * @param filePath the path of the Java file to be parsed.
     * @return the CompilationUnit object representing the parsed Java file.
     */
    public CompilationUnit parseCU(String filePath) {
        String fileContent;
        CompilationUnit parsedUnit = null;
        try {
            fileContent = readFileToString(filePath);
            parsedUnit = parse(fileContent);
            return parsedUnit;
        } catch (Exception e) {
            StaticAnalyzer.LOG.error("Abstract class " + filePath.toString() + " cannot be analyzed. Exception message: " + e.getCause());
        }
        return parsedUnit;
    }

    /**
     This function takes a string representation of a Java file and uses an ASTParser to
     create a CompilationUnit object representing the file's abstract syntax tree.
     @param str a string containing the content of a Java file
     @return a CompilationUnit object representing the file's abstract syntax tree
     */
    private CompilationUnit parse(String str) {
        ASTParser parser = ASTParser.newParser(AST.JLS4);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(str.toCharArray());
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setStatementsRecovery(true);
        return (CompilationUnit) parser.createAST(null);
    }

    /**
     Reads the content of a Java file and returns it as a string.
     @param filePath the path of the file to be read
     @return the content of the file as a string
     @throws IOException if an I/O error occurs while reading the file
     */
    private String readFileToString(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        char[] buf = new char[10];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf , 0 , numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

}
