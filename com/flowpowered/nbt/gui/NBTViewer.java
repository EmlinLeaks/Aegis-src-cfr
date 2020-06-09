/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.gui;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntArrayTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.ShortArrayTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.gui.NBTViewer;
import com.flowpowered.nbt.itemmap.StringMapReader;
import com.flowpowered.nbt.regionfile.SimpleRegionFileReader;
import com.flowpowered.nbt.stream.NBTInputStream;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class NBTViewer
extends JFrame
implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static final int MAX_WIDTH = 32;
    private String format = "";
    private JTree tree;
    private DefaultMutableTreeNode top;

    public NBTViewer() {
        JMenuBar menu = new JMenuBar();
        this.setJMenuBar((JMenuBar)menu);
        JMenu file = new JMenu((String)"File");
        JMenuItem open = new JMenuItem((String)"Open");
        open.addActionListener((ActionListener)this);
        JMenuItem exit = new JMenuItem((String)"Exit");
        exit.addActionListener((ActionListener)this);
        file.add((JMenuItem)open);
        file.addSeparator();
        file.add((JMenuItem)exit);
        menu.add((JMenu)file);
        this.top = new DefaultMutableTreeNode((Object)"NBT Contents");
        this.tree = new JTree((TreeNode)this.top);
        JScrollPane treeView = new JScrollPane((Component)this.tree);
        this.add((Component)treeView);
        this.setTitle((String)"SimpleNBT Viewer");
        this.setSize((int)300, (int)600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation((int)3);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater((Runnable)new Runnable(){

            public void run() {
                try {
                    javax.swing.UIManager.setLookAndFeel((String)javax.swing.UIManager.getSystemLookAndFeelClassName());
                }
                catch (java.lang.ClassNotFoundException e) {
                }
                catch (java.lang.InstantiationException e) {
                }
                catch (java.lang.IllegalAccessException e) {
                }
                catch (javax.swing.UnsupportedLookAndFeelException e) {
                    // empty catch block
                }
                NBTViewer viewer = new NBTViewer();
                viewer.setVisible((boolean)true);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command == null) {
            return;
        }
        if (command.equals((Object)"Open")) {
            this.openFile();
            return;
        }
        if (!command.equals((Object)"Exit")) return;
        System.exit((int)0);
    }

    private void openFile() {
        FileDialog d = new FileDialog((Frame)this, (String)"Open File", (int)0);
        d.setVisible((boolean)true);
        if (d.getDirectory() == null) return;
        if (d.getFile() == null) {
            return;
        }
        File dir = new File((String)d.getDirectory());
        File f = new File((File)dir, (String)d.getFile());
        List<Tag<?>> tags = this.readFile((File)f);
        this.updateTree(tags);
        this.top.setUserObject((Object)("NBT Contents [" + this.format + "]"));
        ((DefaultTreeModel)this.tree.getModel()).nodeChanged((TreeNode)this.top);
    }

    private List<Tag<?>> readFile(File f) {
        List<Tag<?>> tags = this.readRawNBT((File)f, (boolean)true);
        if (tags != null) {
            this.format = "Compressed NBT";
            return tags;
        }
        tags = this.readRawNBT((File)f, (boolean)false);
        if (tags != null) {
            this.format = "Uncompressed NBT";
            return tags;
        }
        tags = SimpleRegionFileReader.readFile((File)f);
        if (tags != null) {
            this.format = "SimpleRegionFile";
            return tags;
        }
        tags = StringMapReader.readFile((File)f);
        if (tags != null) {
            this.format = "StringMap";
            return tags;
        }
        this.format = "Unknown";
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<Tag<?>> readRawNBT(File f, boolean compressed) {
        ArrayList<Tag<?>> tags = new ArrayList<Tag<?>>();
        try {
            FileInputStream is = new FileInputStream((File)f);
            NBTInputStream ns = new NBTInputStream((InputStream)is, (boolean)compressed);
            try {
                boolean eof = false;
                while (!eof) {
                    try {
                        tags.add(ns.readTag());
                    }
                    catch (EOFException e) {
                        eof = true;
                    }
                }
                return tags;
            }
            finally {
                try {
                    ns.close();
                }
                catch (IOException e) {
                    JOptionPane.showMessageDialog((Component)this, (Object)"Unable to close file", (String)"File Read Error", (int)0);
                }
            }
        }
        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog((Component)this, (Object)"Unable to open file", (String)"File Read Error", (int)0);
            return tags;
        }
        catch (IOException e) {
            return null;
        }
    }

    private void updateTree(List<Tag<?>> tags) {
        int i;
        DefaultTreeModel model = (DefaultTreeModel)this.tree.getModel();
        this.top.removeAllChildren();
        model.nodeStructureChanged((TreeNode)this.top);
        if (tags == null) {
            return;
        }
        if (tags.size() == 1) {
            model.insertNodeInto((MutableTreeNode)NBTViewer.getNode(tags.get((int)0)), (MutableTreeNode)this.top, (int)0);
        } else {
            i = 0;
            for (Tag<?> t : tags) {
                model.insertNodeInto((MutableTreeNode)NBTViewer.getNode(t), (MutableTreeNode)this.top, (int)i);
                ++i;
            }
        }
        i = 0;
        do {
            if (i >= this.tree.getRowCount()) {
                this.tree.expandRow((int)0);
                if (tags.size() != 1) return;
                this.tree.expandRow((int)1);
                return;
            }
            this.tree.collapseRow((int)i);
            ++i;
        } while (true);
    }

    private static DefaultMutableTreeNode getNode(Tag<?> tag) {
        return NBTViewer.getNode(tag, (boolean)true);
    }

    private static DefaultMutableTreeNode getNode(Tag<?> tag, boolean includeName) {
        if (tag == null) {
            return new DefaultMutableTreeNode((Object)"Empty");
        }
        if (tag instanceof CompoundTag) {
            return NBTViewer.getNode((CompoundTag)((CompoundTag)tag));
        }
        if (tag instanceof ListTag) {
            try {
                return NBTViewer.getNode((ListTag)tag);
            }
            catch (ClassCastException e) {
            }
        } else {
            if (tag instanceof ByteArrayTag) {
                return NBTViewer.getNode((ByteArrayTag)((ByteArrayTag)tag));
            }
            if (tag instanceof ShortArrayTag) {
                return NBTViewer.getNode((ShortArrayTag)((ShortArrayTag)tag));
            }
            if (tag instanceof IntArrayTag) {
                return NBTViewer.getNode((IntArrayTag)((IntArrayTag)tag));
            }
        }
        String message = includeName ? tag.getName() + ":" + tag.getValue() : tag.getValue().toString();
        return new DefaultMutableTreeNode((Object)message);
    }

    private static DefaultMutableTreeNode getNode(CompoundTag tag) {
        CompoundMap map = tag.getValue();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode((Object)(tag.getName() + " [Map]"));
        Iterator<Tag<?>> iterator = map.values().iterator();
        while (iterator.hasNext()) {
            Tag<?> t = iterator.next();
            DefaultMutableTreeNode child = NBTViewer.getNode(t);
            root.add((MutableTreeNode)child);
        }
        return root;
    }

    private static DefaultMutableTreeNode getNode(ListTag<Tag<?>> tag) {
        Object values = tag.getValue();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode((Object)(tag.getName() + " [List]"));
        Iterator<E> iterator = values.iterator();
        while (iterator.hasNext()) {
            Tag t = (Tag)iterator.next();
            DefaultMutableTreeNode child = NBTViewer.getNode(t, (boolean)false);
            root.add((MutableTreeNode)child);
        }
        return root;
    }

    private static DefaultMutableTreeNode getNode(ByteArrayTag tag) {
        byte[] values = tag.getValue();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode((Object)(tag.getName() + " [byte[" + values.length + "]"));
        StringBuilder sb = new StringBuilder((String)"{");
        boolean first = true;
        byte[] arrby = values;
        int n = arrby.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                sb.append((String)"}");
                DefaultMutableTreeNode child = new DefaultMutableTreeNode((Object)sb.toString());
                root.add((MutableTreeNode)child);
                return root;
            }
            byte v = arrby[n2];
            if (!first) {
                sb.append((String)", ");
            } else {
                first = false;
            }
            String s = Byte.toString((byte)v);
            if (sb.length() + s.length() > 32) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode((Object)sb.toString());
                root.add((MutableTreeNode)child);
                sb.setLength((int)0);
            }
            sb.append((String)Integer.toHexString((int)(v & 255)));
            ++n2;
        } while (true);
    }

    private static DefaultMutableTreeNode getNode(ShortArrayTag tag) {
        short[] values = tag.getValue();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode((Object)(tag.getName() + " [short[" + values.length + "]]"));
        StringBuilder sb = new StringBuilder((String)"{");
        boolean first = true;
        short[] arrs = values;
        int n = arrs.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                sb.append((String)"}");
                DefaultMutableTreeNode child = new DefaultMutableTreeNode((Object)sb.toString());
                root.add((MutableTreeNode)child);
                return root;
            }
            short v = arrs[n2];
            if (!first) {
                sb.append((String)", ");
            } else {
                first = false;
            }
            String s = Short.toString((short)v);
            if (sb.length() + s.length() > 32) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode((Object)sb.toString());
                root.add((MutableTreeNode)child);
                sb.setLength((int)0);
            }
            sb.append((int)v);
            ++n2;
        } while (true);
    }

    private static DefaultMutableTreeNode getNode(IntArrayTag tag) {
        int[] values = tag.getValue();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode((Object)(tag.getName() + " [int[" + values.length + "]]"));
        StringBuilder sb = new StringBuilder((String)"{");
        boolean first = true;
        int[] arrn = values;
        int n = arrn.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                sb.append((String)"}");
                DefaultMutableTreeNode child = new DefaultMutableTreeNode((Object)sb.toString());
                root.add((MutableTreeNode)child);
                return root;
            }
            int v = arrn[n2];
            if (!first) {
                sb.append((String)", ");
            } else {
                first = false;
            }
            String s = Integer.toString((int)v);
            if (sb.length() + s.length() > 32) {
                sb.append((String)"<br>");
                DefaultMutableTreeNode child = new DefaultMutableTreeNode((Object)sb.toString());
                root.add((MutableTreeNode)child);
                sb.setLength((int)0);
            }
            sb.append((int)v);
            ++n2;
        } while (true);
    }
}

