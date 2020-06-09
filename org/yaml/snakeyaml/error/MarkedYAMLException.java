/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.error;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;

public class MarkedYAMLException
extends YAMLException {
    private static final long serialVersionUID = -9119388488683035101L;
    private String context;
    private Mark contextMark;
    private String problem;
    private Mark problemMark;
    private String note;

    protected MarkedYAMLException(String context, Mark contextMark, String problem, Mark problemMark, String note) {
        this((String)context, (Mark)contextMark, (String)problem, (Mark)problemMark, (String)note, null);
    }

    protected MarkedYAMLException(String context, Mark contextMark, String problem, Mark problemMark, String note, Throwable cause) {
        super((String)(context + "; " + problem + "; " + problemMark), (Throwable)cause);
        this.context = context;
        this.contextMark = contextMark;
        this.problem = problem;
        this.problemMark = problemMark;
        this.note = note;
    }

    protected MarkedYAMLException(String context, Mark contextMark, String problem, Mark problemMark) {
        this((String)context, (Mark)contextMark, (String)problem, (Mark)problemMark, null, null);
    }

    protected MarkedYAMLException(String context, Mark contextMark, String problem, Mark problemMark, Throwable cause) {
        this((String)context, (Mark)contextMark, (String)problem, (Mark)problemMark, null, (Throwable)cause);
    }

    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        StringBuilder lines = new StringBuilder();
        if (this.context != null) {
            lines.append((String)this.context);
            lines.append((String)"\n");
        }
        if (this.contextMark != null && (this.problem == null || this.problemMark == null || this.contextMark.getName().equals((Object)this.problemMark.getName()) || this.contextMark.getLine() != this.problemMark.getLine() || this.contextMark.getColumn() != this.problemMark.getColumn())) {
            lines.append((String)this.contextMark.toString());
            lines.append((String)"\n");
        }
        if (this.problem != null) {
            lines.append((String)this.problem);
            lines.append((String)"\n");
        }
        if (this.problemMark != null) {
            lines.append((String)this.problemMark.toString());
            lines.append((String)"\n");
        }
        if (this.note == null) return lines.toString();
        lines.append((String)this.note);
        lines.append((String)"\n");
        return lines.toString();
    }

    public String getContext() {
        return this.context;
    }

    public Mark getContextMark() {
        return this.contextMark;
    }

    public String getProblem() {
        return this.problem;
    }

    public Mark getProblemMark() {
        return this.problemMark;
    }
}

