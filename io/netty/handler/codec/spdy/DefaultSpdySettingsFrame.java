/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.DefaultSpdySettingsFrame;
import io.netty.handler.codec.spdy.SpdySettingsFrame;
import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DefaultSpdySettingsFrame
implements SpdySettingsFrame {
    private boolean clear;
    private final Map<Integer, Setting> settingsMap = new TreeMap<Integer, Setting>();

    @Override
    public Set<Integer> ids() {
        return this.settingsMap.keySet();
    }

    @Override
    public boolean isSet(int id) {
        return this.settingsMap.containsKey((Object)Integer.valueOf((int)id));
    }

    @Override
    public int getValue(int id) {
        Setting setting = this.settingsMap.get((Object)Integer.valueOf((int)id));
        if (setting == null) return -1;
        int n = setting.getValue();
        return n;
    }

    @Override
    public SpdySettingsFrame setValue(int id, int value) {
        return this.setValue((int)id, (int)value, (boolean)false, (boolean)false);
    }

    @Override
    public SpdySettingsFrame setValue(int id, int value, boolean persistValue, boolean persisted) {
        if (id < 0) throw new IllegalArgumentException((String)("Setting ID is not valid: " + id));
        if (id > 16777215) {
            throw new IllegalArgumentException((String)("Setting ID is not valid: " + id));
        }
        Integer key = Integer.valueOf((int)id);
        Setting setting = this.settingsMap.get((Object)key);
        if (setting != null) {
            setting.setValue((int)value);
            setting.setPersist((boolean)persistValue);
            setting.setPersisted((boolean)persisted);
            return this;
        }
        this.settingsMap.put((Integer)key, (Setting)new Setting((int)value, (boolean)persistValue, (boolean)persisted));
        return this;
    }

    @Override
    public SpdySettingsFrame removeValue(int id) {
        this.settingsMap.remove((Object)Integer.valueOf((int)id));
        return this;
    }

    @Override
    public boolean isPersistValue(int id) {
        Setting setting = this.settingsMap.get((Object)Integer.valueOf((int)id));
        if (setting == null) return false;
        if (!setting.isPersist()) return false;
        return true;
    }

    @Override
    public SpdySettingsFrame setPersistValue(int id, boolean persistValue) {
        Setting setting = this.settingsMap.get((Object)Integer.valueOf((int)id));
        if (setting == null) return this;
        setting.setPersist((boolean)persistValue);
        return this;
    }

    @Override
    public boolean isPersisted(int id) {
        Setting setting = this.settingsMap.get((Object)Integer.valueOf((int)id));
        if (setting == null) return false;
        if (!setting.isPersisted()) return false;
        return true;
    }

    @Override
    public SpdySettingsFrame setPersisted(int id, boolean persisted) {
        Setting setting = this.settingsMap.get((Object)Integer.valueOf((int)id));
        if (setting == null) return this;
        setting.setPersisted((boolean)persisted);
        return this;
    }

    @Override
    public boolean clearPreviouslyPersistedSettings() {
        return this.clear;
    }

    @Override
    public SpdySettingsFrame setClearPreviouslyPersistedSettings(boolean clear) {
        this.clear = clear;
        return this;
    }

    private Set<Map.Entry<Integer, Setting>> getSettings() {
        return this.settingsMap.entrySet();
    }

    private void appendSettings(StringBuilder buf) {
        Iterator<Map.Entry<Integer, Setting>> iterator = this.getSettings().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Setting> e = iterator.next();
            Setting setting = e.getValue();
            buf.append((String)"--> ");
            buf.append((Object)e.getKey());
            buf.append((char)':');
            buf.append((int)setting.getValue());
            buf.append((String)" (persist value: ");
            buf.append((boolean)setting.isPersist());
            buf.append((String)"; persisted: ");
            buf.append((boolean)setting.isPersisted());
            buf.append((char)')');
            buf.append((String)StringUtil.NEWLINE);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append((String)StringUtil.simpleClassName((Object)this)).append((String)StringUtil.NEWLINE);
        this.appendSettings((StringBuilder)buf);
        buf.setLength((int)(buf.length() - StringUtil.NEWLINE.length()));
        return buf.toString();
    }
}

