/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.serializer;

import java.text.NumberFormat;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.serializer.AnchorGenerator;

public class NumberAnchorGenerator
implements AnchorGenerator {
    private int lastAnchorId = 0;

    public NumberAnchorGenerator(int lastAnchorId) {
        this.lastAnchorId = lastAnchorId;
    }

    @Override
    public String nextAnchor(Node node) {
        ++this.lastAnchorId;
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumIntegerDigits((int)3);
        format.setMaximumFractionDigits((int)0);
        format.setGroupingUsed((boolean)false);
        String anchorId = format.format((long)((long)this.lastAnchorId));
        return "id" + anchorId;
    }
}

