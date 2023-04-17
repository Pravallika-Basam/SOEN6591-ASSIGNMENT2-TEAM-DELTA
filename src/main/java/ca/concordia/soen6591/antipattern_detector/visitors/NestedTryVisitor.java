package ca.concordia.soen6591.antipattern_detector.visitors;
import java.util.List;
import org.eclipse.jdt.core.dom.*;
import ca.concordia.soen6591.antipattern_detector.detecters.StaticAnalyzer;

/**
 * @author: Aniket Tailor
 * This class extends ASTVisitor class and implements the logic to traverse the code for detecting Nested try.
 */
public class NestedTryVisitor extends ASTVisitor {
    private int numAntiPatternsDetected = 0;
    private String compilationUnitName;
    private CompilationUnit compilationUnit;

    public NestedTryVisitor(String unitName , CompilationUnit compilationUnit) {
        this.compilationUnitName = unitName;
        this.compilationUnit = compilationUnit;
    }

    /**
     *This code overrides the visit method to detect TryStatement anti-patterns by visiting
     * each statement in the body of the provided node and
     * incrementing a counter while calling another method.
     */
    @Override
    public boolean visit(TryStatement node) {

        boolean nestedTryFound = false;
        List<Statement> statements = node.getBody().statements();
        for (Statement statement : statements) {
            if (statement instanceof TryStatement) {
                if (!nestedTryFound) {
                    printDetected(node.getStartPosition());
                    numAntiPatternsDetected++;
                    nestedTryFound = true;
                }
            }
        }
        return super.visit(node);
    }

    /**
     * This method prints the total number of Nested try anti-patterns detected
     */
    public int getNumAntiPatternsDetected() {
        return numAntiPatternsDetected;
    }

    /**
     *
     * @param position It shows the position where the Anti pattern is detected.
     */
    private void printDetected(int position) {
        StaticAnalyzer.logDetection("Nested Try", compilationUnit, compilationUnitName, position);
    }
}