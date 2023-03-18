package ca.concordia.soen6591.antipattern_detector.visitors;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import java.util.ArrayList;
import java.util.List;

import ca.concordia.soen6591.antipattern_detector.detecters.StaticAnalyzer;

public class LogAndThrowDetector extends ASTVisitor {

    private int numAntiPatternsDetected = 0;
    private int numCatchClauses = 0;
    private String compilationUnitName;
    private CompilationUnit compilationUnit;
    private List<LogDetection> detectionList = new ArrayList<>();

    public LogAndThrowDetector(String unitName, CompilationUnit compilationUnit) {
        this.compilationUnitName = unitName;
        this.compilationUnit = compilationUnit;
    }

    @Override
    public boolean visit(CatchClause node) {
        numCatchClauses++;
        List<Statement> statements = node.getBody().statements();
        for (Statement statement : statements) {
            if (statement.getNodeType() == Statement.EXPRESSION_STATEMENT) {
                MethodInvocation invocation = (MethodInvocation) ((org.eclipse.jdt.core.dom.ExpressionStatement) statement).getExpression();
                if (invocation.getName().toString().equals("printStackTrace")) {
                    int lineNumber = compilationUnit.getLineNumber(invocation.getStartPosition());
                    detectionList.add(new LogDetection(compilationUnitName, lineNumber));
                    StaticAnalyzer.logDetection(compilationUnit, compilationUnitName, invocation.getStartPosition());
                    numAntiPatternsDetected++;
                }
            }
        }
        return super.visit(node);
    }

    public int getNumAntiPatternsDetected() {
        return numAntiPatternsDetected;
    }

    public int getNumCatchClauses() {
        return numCatchClauses;
    }

    public List<LogDetection> getDetectionList() {
        return detectionList;
    }

    public class LogDetection {
        private String fileName;
        private int lineNumber;

        public LogDetection(String fileName, int lineNumber) {
            this.fileName = fileName;
            this.lineNumber = lineNumber;
        }

        public String getFileName() {
            return fileName;
        }

        public int getLineNumber() {
            return lineNumber;
        }
    }
    
}


