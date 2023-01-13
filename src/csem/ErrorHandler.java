package csem;

import org.antlr.v4.runtime.ParserRuleContext;

public class ErrorHandler {
    private CSemErrorFormatter formatter;
    private int errorCount = 0;
    private int warningCount = 0;

    public ErrorHandler() {
        this.formatter = new CSemErrorFormatter();
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void error(ParserRuleContext ctx, String error) {
        formatter.printError(ctx, error);
        errorCount++;
    }

    public void error(ParserRuleContext ctx, String error, Integer offset) {
        formatter.printError(ctx, error, offset);
        errorCount++;
    }

    public void warning(ParserRuleContext ctx, String warning) {
        formatter.printWarning(ctx, warning);
        warningCount++;
    }

    public void warning(ParserRuleContext ctx, String warning, Integer offset) {
        formatter.printWarning(ctx, warning, offset);
        warningCount++;
    }
}
