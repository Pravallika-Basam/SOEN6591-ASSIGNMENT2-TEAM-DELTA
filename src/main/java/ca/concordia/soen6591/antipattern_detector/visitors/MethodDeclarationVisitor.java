package ca.concordia.soen6591.antipattern_detector.visitors;

import java.util.List;

import org.eclipse.jdt.core.dom.*;

import ca.concordia.soen6591.antipattern_detector.detecters.StaticAnalyzer;

public class MethodDeclarationVisitor extends ASTVisitor {
    private int numAntiPatternsDetected = 0;
    private int numMethods = 0;
    private String compilationUnitName;
    private CompilationUnit compilationUnit;

    public MethodDeclarationVisitor(String unitName, CompilationUnit compilationUnit) {
        this.compilationUnitName = unitName;
        this.compilationUnit = compilationUnit;
    }

    /**
     * This method visits the MethodDeclaration nodes in the Abstract Syntax Tree (AST) and detects any instances of the "throws kitchen sink" antipattern.
     *
     * @param node the MethodDeclaration node being visited
     * @return true if the visitor should continue visiting the AST, false otherwise
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean visit(MethodDeclaration node) {
        numMethods++;
        List<Name> thrownExceptions = node.thrownExceptions();
        if (thrownExceptions.size() > 2) { // Change 2 to any other number depending on the threshold of your choice
            printDetected(node.getStartPosition());
            numAntiPatternsDetected++;
        }

        // Visit all statements in the method body to check for throws in loop statements
        node.getBody().accept(new ASTVisitor() {
            @Override
            public boolean visit(ThrowStatement throwStatement) {
                // Check if the throw statement is inside a loop statement or a try block
                ASTNode parent = throwStatement.getParent();
                while (parent != null && !(parent instanceof MethodDeclaration)) {
                    if (parent instanceof EnhancedForStatement || parent instanceof ForStatement
                            || parent instanceof WhileStatement || parent instanceof DoStatement
                            || parent instanceof TryStatement) {
                        printDetected(throwStatement.getStartPosition());
                        numAntiPatternsDetected++;
                        break;
                    }
                    parent = parent.getParent();
                }
                return super.visit(throwStatement);
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

    private void printDetected(int position) {
        StaticAnalyzer.logDetection("Throws Kitchen Sink", compilationUnit, compilationUnitName, position);
    }
}