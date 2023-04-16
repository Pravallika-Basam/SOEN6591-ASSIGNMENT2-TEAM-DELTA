package ca.concordia.soen6591.antipattern_detector.visitors;

import java.util.List;

import org.eclipse.jdt.core.dom.*;

import ca.concordia.soen6591.antipattern_detector.detecters.StaticAnalyzer;

public class CatchClauseVisitor extends ASTVisitor {
    private int numAntiPatternsDetected = 0;
    private int numCatchClauses = 0;
    private String compilationUnitName;
    private CompilationUnit compilationUnit;

    public CatchClauseVisitor(String unitName , CompilationUnit compilationUnit) {
        this.compilationUnitName = unitName;
        this.compilationUnit = compilationUnit;
    }

    /**
     * This method visits the CatchClause nodes in the Abstract Syntax Tree (AST) and detects any instances of destructive wrapping.
     *
     * @param node the CatchClause node being visited
     * @return true if the visitor should continue visiting the AST, false otherwise
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean visit(CatchClause node) {
        numCatchClauses++;
        if (!node.getBody().statements().isEmpty()) {
            List<Statement> statements = node.getBody().statements();
            statements.forEach((s) -> {
                if (s instanceof ThrowStatement) {
                    SimpleName exceptionName = node.getException().getName();
                    s.accept(new ASTVisitor() {
                        @Override
                        public boolean visit(SimpleName node) {
                            if (exceptionName.toString().equals(node.toString())) {
                                printDetected(node.getStartPosition());
                                numAntiPatternsDetected++;
                            }
                            return super.visit(node);
                        }
                    });
                }
            });

        }

        return super.visit(node);
    }

    public int getNumAntiPatternsDetected() {
        return numAntiPatternsDetected;
    }

    public int getNumCatchClauses() {
        return numCatchClauses;
    }

    private void printDetected(int position) {
        StaticAnalyzer.logDetection("Destructive Wrapping",compilationUnit , compilationUnitName , position);
    }

}
