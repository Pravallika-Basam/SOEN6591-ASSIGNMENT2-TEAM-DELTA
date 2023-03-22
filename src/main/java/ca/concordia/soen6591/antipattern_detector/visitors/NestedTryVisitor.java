package ca.concordia.soen6591.antipattern_detector.visitors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jdt.core.dom.*;
import ca.concordia.soen6591.antipattern_detector.detecters.StaticAnalyzer;

public class NestedTryVisitor extends ASTVisitor {
    private int numAntiPatternsDetected = 0;
    private String compilationUnitName;
    private CompilationUnit compilationUnit;

    public NestedTryVisitor(String unitName , CompilationUnit compilationUnit) {
        this.compilationUnitName = unitName;
        this.compilationUnit = compilationUnit;
    }

    private final String REGEX = "(\\s)*try(\\s|\\n|\\r){0,}\\{";
    @Override
    public boolean visit(TryStatement node) {

        String body = node.getBody().toString();
        if(hasNestedTry(body)){
            printDetected(node.getStartPosition());
            numAntiPatternsDetected = numAntiPatternsDetected + 1;
        }
        return super.visit(node);
    }

    private boolean hasNestedTry(String body) {
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(body);
        while(matcher.find())
            return true;
        return false;
    }

    public int getNumAntiPatternsDetected() {
        return numAntiPatternsDetected;
    }

    private void printDetected(int position) {
        StaticAnalyzer.logDetection("Nested Try", compilationUnit, compilationUnitName, position);
    }
}
