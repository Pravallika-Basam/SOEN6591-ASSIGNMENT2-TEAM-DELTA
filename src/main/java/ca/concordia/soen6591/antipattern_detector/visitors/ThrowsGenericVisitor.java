package ca.concordia.soen6591.antipattern_detector.visitors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.*;

import ca.concordia.soen6591.antipattern_detector.detecters.StaticAnalyzer;

public class ThrowsGenericVisitor extends ASTVisitor {
    private int numAntiPatternsDetected = 0;
    private int numMethods = 0;
    private String compilationUnitName;
    private CompilationUnit compilationUnit;

    public ThrowsGenericVisitor(String unitName, CompilationUnit compilationUnit) {
        this.compilationUnitName = unitName;
        this.compilationUnit = compilationUnit;
    }

    /**
     * This method visits the MethodDeclaration nodes in the Abstract Syntax Tree (AST) and detects any instances of the "Throws Generic" anti-pattern.
     *
     * @param node the MethodDeclaration node being visited
     * @return true if the visitor should continue visiting the AST, false otherwise
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean visit(MethodDeclaration node) {
        numMethods++;
        List<Name> thrownExceptions = node.thrownExceptions();
        Set<String> uniqueExceptionTypes = new HashSet<>();
        for (Name exception : thrownExceptions) {
            String exceptionType = exception.toString();
            uniqueExceptionTypes.add(exceptionType);
        }

        if (uniqueExceptionTypes.size() > 1) { // Change 1 to any other number depending on the threshold of your choice
            printDetected(node.getStartPosition());
            numAntiPatternsDetected++;
        }

        // Visit all statements in the method body to check for throws in finally blocks
        node.getBody().accept(new ASTVisitor() {
            @Override
            public boolean visit(TryStatement tryStatement) {
                Block finallyBlock = tryStatement.getFinally();
                if (finallyBlock != null) {
                    finallyBlock.accept(new ASTVisitor() {
                        @Override
                        public boolean visit(ThrowStatement throwStatement) {
                            printDetected(throwStatement.getStartPosition());
                            numAntiPatternsDetected++;
                            return super.visit(throwStatement);
                        }
                    });
                }
                return super.visit(tryStatement);
            }
        });

        return super.visit(node);
    }
    

    public int getNumAntiPatternsDetected() {
        return numAntiPatternsDetected;
    }

    public int getNumMethods() {
        return numMethods;
    }

    private void printDetected(int startPosition) {
        StaticAnalyzer.logDetection("Throws Generic anti-pattern" ,compilationUnit , compilationUnitName , startPosition);
    }
}
