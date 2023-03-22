package ca.concordia.soen6591.antipattern_detector.detecters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;

import ca.concordia.soen6591.antipattern_detector.utility.CUParser;
import ca.concordia.soen6591.antipattern_detector.utility.FileWalker;
import ca.concordia.soen6591.antipattern_detector.visitors.CatchClauseVisitor;
import ca.concordia.soen6591.antipattern_detector.visitors.LogAndThrowDetector;
import ca.concordia.soen6591.antipattern_detector.visitors.LogAndThrowDetector.LogDetection;

import ca.concordia.soen6591.antipattern_detector.visitors.MethodDeclarationVisitor;


public class StaticAnalyzer {
    public static final Logger LOG = LogManager.getLogger(StaticAnalyzer.class);
    static int numDestructiveWrappings = 0, numCatchClauses = 0, numThrowsKitchenSink=0,numMethods=0, numLogAndThrow = 0;
    static final CUParser COMPILATION_UNIT_PARSER = new CUParser();
    



    public static void logDetection(CompilationUnit compilationUnit , String compilationUnitName , int position) {
        LOG.warn("Anti-pattern: Destructive Wrapping Detected in the class " + compilationUnitName + " at line " + compilationUnit.getLineNumber(position));
    }

    public static void logDetection(String antipatternName, CompilationUnit compilationUnit , String compilationUnitName , int position) {
        LOG.warn("Anti-pattern: "+antipatternName+" Detected in the class " + compilationUnitName + " at line " + compilationUnit.getLineNumber(position));
    }

    public static void main(String[] args) throws IOException {

        final String dirPath = "/Users/pravallikachowdary/Desktop/Old Files/SOEN Mining/Assignment/fortune-cookie-shop";
        final FileWalker fileWalker = new FileWalker(dirPath);
        LOG.info("Program started successfully!!");
        List<Path> javaFiles = fileWalker.filewalk();
        for (Path path : javaFiles) {
            try {
                CompilationUnit parsedCU = COMPILATION_UNIT_PARSER.parseCU(path.toString());
                CatchClauseVisitor exceptionVisitor = new CatchClauseVisitor(path.toString() , parsedCU);
                parsedCU.accept(exceptionVisitor);
                numDestructiveWrappings += exceptionVisitor.getNumAntiPatternsDetected();
                numCatchClauses += exceptionVisitor.getNumCatchClauses();
            } catch (Exception e) {
                LOG.error("An exception occurred while processing file: " + path.toString() + ". Details: " + e.getMessage());
            }

        }

        LOG.info("__________Program Analysis Results__________");
        LOG.info("Number of catch clauses detected: " + numCatchClauses);
        LOG.info("Number of destructive wrapping patterns detected: " + numDestructiveWrappings);
        LOG.info("Exiting the program with status code 0");

        for (Path path : javaFiles) {
        	try {
        	CompilationUnit parsedCU = COMPILATION_UNIT_PARSER.parseCU(path.toString());
        	LogAndThrowDetector exceptionVisitor = new LogAndThrowDetector(path.toString(), parsedCU);
        	parsedCU.accept(exceptionVisitor);
        	numLogAndThrow += exceptionVisitor.getNumAntiPatternsDetected();
        	List<LogDetection> detections = exceptionVisitor.getDetectionList();
        	for (LogDetection detection : detections) {
        	LOG.warn("Anti-pattern: Log and Throw detected in the file " + path.toString() + " at line " + detection.getLineNumber());
        	}
        	} catch (Exception e) {
        	LOG.error("An exception occurred while processing file: " + path.toString() + ". Details: " + e.getMessage());
        	}
        	}
        	LOG.info("__________Log and Throw Antipatterns Results__________");
        	LOG.info("Number of log and throw patterns detected: " + numLogAndThrow);
        	LOG.info("Exiting the program with status code 0");  
    }



}
