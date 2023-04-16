package ca.concordia.soen6591.antipattern_detector.visitors;

import java.util.List;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import java.util.ArrayList;

import ca.concordia.soen6591.antipattern_detector.detecters.StaticAnalyzer;

public class LogAndThrowDetector extends ASTVisitor {

    private int numAntiPatternsDetected = 0;
    private String compilationUnitName;
    private CompilationUnit compilationUnit;
    private List<LogDetection> detectionList = new ArrayList<>();

    public LogAndThrowDetector(String unitName, CompilationUnit compilationUnit) {
        this.compilationUnitName = unitName;
        this.compilationUnit = compilationUnit;
    }
    
    /**
    * This method visits the CatchClause nodes in the Abstract Syntax Tree (AST) and detects any instances of Log and Throw anti-pattern. This method updates the code to add a throw statement with the caught exception, so that it is properly handled and rethrown.
    * @param node the CatchClause node being visited
    * @return true if the visitor should continue visiting the AST, false otherwise
    */


    
    
    @SuppressWarnings("unchecked") 
    @Override
    public boolean visit(CatchClause node) {
        List<Statement> statements = node.getBody().statements();
        boolean logDetected = false;
        boolean throwDetected = false;
        for (Statement statement : statements) {
            if (statement.getNodeType() == Statement.EXPRESSION_STATEMENT) {
                MethodInvocation invocation = (MethodInvocation) ((org.eclipse.jdt.core.dom.ExpressionStatement) statement).getExpression();
                if (invocation.getName().toString().equals("printStackTrace")) {
                    int lineNumber = compilationUnit.getLineNumber(invocation.getStartPosition());
                    detectionList.add(new LogDetection(compilationUnitName, lineNumber));
                    logDetected = true;
                    numAntiPatternsDetected++;
                }
            } else if (statement.getNodeType() == Statement.THROW_STATEMENT) {
                int lineNumber = compilationUnit.getLineNumber(statement.getStartPosition());
                detectionList.add(new LogDetection(compilationUnitName, lineNumber));
                throwDetected = true;
                numAntiPatternsDetected++;
                
            }
        }
        //It replaces the log statement with a throw statement that rethrows the caught exception. 
        //This way, the exception is being properly handled and thrown again to the calling method for further handling or reporting.
        if (logDetected && throwDetected) {
            // Replace the log with a throw statement
            AST ast = node.getAST();
            ThrowStatement throwStatement = ast.newThrowStatement();
            throwStatement.setExpression(ast.newSimpleName(node.getException().getName().getIdentifier()));
            node.getBody().statements().clear();
            node.getBody().statements().add(throwStatement);
            
        }
        return super.visit(node);
    }
    
    



    public int getNumAntiPatternsDetected() {
        return numAntiPatternsDetected;
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




