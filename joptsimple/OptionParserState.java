/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import joptsimple.ArgumentList;
import joptsimple.OptionParser;
import joptsimple.OptionParserState;
import joptsimple.OptionSet;

abstract class OptionParserState {
    OptionParserState() {
    }

    static OptionParserState noMoreOptions() {
        return new OptionParserState(){

            protected void handleArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
                parser.handleNonOptionArgument((java.lang.String)arguments.next(), (ArgumentList)arguments, (OptionSet)detectedOptions);
            }
        };
    }

    static OptionParserState moreOptions(boolean posixlyCorrect) {
        return new OptionParserState((boolean)posixlyCorrect){
            final /* synthetic */ boolean val$posixlyCorrect;
            {
                this.val$posixlyCorrect = bl;
            }

            protected void handleArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
                java.lang.String candidate;
                block6 : {
                    candidate = arguments.next();
                    try {
                        if (joptsimple.ParserRules.isOptionTerminator((java.lang.String)candidate)) {
                            parser.noMoreOptions();
                            return;
                        }
                        if (joptsimple.ParserRules.isLongOptionToken((java.lang.String)candidate)) {
                            parser.handleLongOptionToken((java.lang.String)candidate, (ArgumentList)arguments, (OptionSet)detectedOptions);
                            return;
                        }
                        if (joptsimple.ParserRules.isShortOptionToken((java.lang.String)candidate)) {
                            parser.handleShortOptionToken((java.lang.String)candidate, (ArgumentList)arguments, (OptionSet)detectedOptions);
                            return;
                        }
                    }
                    catch (joptsimple.UnrecognizedOptionException e) {
                        if (parser.doesAllowsUnrecognizedOptions()) break block6;
                        throw e;
                    }
                }
                if (this.val$posixlyCorrect) {
                    parser.noMoreOptions();
                }
                parser.handleNonOptionArgument((java.lang.String)candidate, (ArgumentList)arguments, (OptionSet)detectedOptions);
            }
        };
    }

    protected abstract void handleArgument(OptionParser var1, ArgumentList var2, OptionSet var3);
}

