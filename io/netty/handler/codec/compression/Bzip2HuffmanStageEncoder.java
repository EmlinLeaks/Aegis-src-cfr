/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.compression.Bzip2BitWriter;
import io.netty.handler.codec.compression.Bzip2HuffmanAllocator;
import io.netty.handler.codec.compression.Bzip2MoveToFrontTable;
import java.util.Arrays;

final class Bzip2HuffmanStageEncoder {
    private static final int HUFFMAN_HIGH_SYMBOL_COST = 15;
    private final Bzip2BitWriter writer;
    private final char[] mtfBlock;
    private final int mtfLength;
    private final int mtfAlphabetSize;
    private final int[] mtfSymbolFrequencies;
    private final int[][] huffmanCodeLengths;
    private final int[][] huffmanMergedCodeSymbols;
    private final byte[] selectors;

    Bzip2HuffmanStageEncoder(Bzip2BitWriter writer, char[] mtfBlock, int mtfLength, int mtfAlphabetSize, int[] mtfSymbolFrequencies) {
        this.writer = writer;
        this.mtfBlock = mtfBlock;
        this.mtfLength = mtfLength;
        this.mtfAlphabetSize = mtfAlphabetSize;
        this.mtfSymbolFrequencies = mtfSymbolFrequencies;
        int totalTables = Bzip2HuffmanStageEncoder.selectTableCount((int)mtfLength);
        this.huffmanCodeLengths = new int[totalTables][mtfAlphabetSize];
        this.huffmanMergedCodeSymbols = new int[totalTables][mtfAlphabetSize];
        this.selectors = new byte[(mtfLength + 50 - 1) / 50];
    }

    private static int selectTableCount(int mtfLength) {
        if (mtfLength >= 2400) {
            return 6;
        }
        if (mtfLength >= 1200) {
            return 5;
        }
        if (mtfLength >= 600) {
            return 4;
        }
        if (mtfLength < 200) return 2;
        return 3;
    }

    private static void generateHuffmanCodeLengths(int alphabetSize, int[] symbolFrequencies, int[] codeLengths) {
        int i;
        int[] mergedFrequenciesAndIndices = new int[alphabetSize];
        int[] sortedFrequencies = new int[alphabetSize];
        for (i = 0; i < alphabetSize; ++i) {
            mergedFrequenciesAndIndices[i] = symbolFrequencies[i] << 9 | i;
        }
        Arrays.sort((int[])mergedFrequenciesAndIndices);
        for (i = 0; i < alphabetSize; ++i) {
            sortedFrequencies[i] = mergedFrequenciesAndIndices[i] >>> 9;
        }
        Bzip2HuffmanAllocator.allocateHuffmanCodeLengths((int[])sortedFrequencies, (int)20);
        i = 0;
        while (i < alphabetSize) {
            codeLengths[mergedFrequenciesAndIndices[i] & 511] = sortedFrequencies[i];
            ++i;
        }
    }

    private void generateHuffmanOptimisationSeeds() {
        int[][] huffmanCodeLengths = this.huffmanCodeLengths;
        int[] mtfSymbolFrequencies = this.mtfSymbolFrequencies;
        int mtfAlphabetSize = this.mtfAlphabetSize;
        int totalTables = huffmanCodeLengths.length;
        int remainingLength = this.mtfLength;
        int lowCostEnd = -1;
        int i = 0;
        while (i < totalTables) {
            int actualCumulativeFrequency;
            int targetCumulativeFrequency = remainingLength / (totalTables - i);
            int lowCostStart = lowCostEnd + 1;
            for (actualCumulativeFrequency = 0; actualCumulativeFrequency < targetCumulativeFrequency && lowCostEnd < mtfAlphabetSize - 1; actualCumulativeFrequency += mtfSymbolFrequencies[++lowCostEnd]) {
            }
            if (lowCostEnd > lowCostStart && i != 0 && i != totalTables - 1 && (totalTables - i & 1) == 0) {
                actualCumulativeFrequency -= mtfSymbolFrequencies[lowCostEnd--];
            }
            int[] tableCodeLengths = huffmanCodeLengths[i];
            for (int j = 0; j < mtfAlphabetSize; ++j) {
                if (j >= lowCostStart && j <= lowCostEnd) continue;
                tableCodeLengths[j] = 15;
            }
            remainingLength -= actualCumulativeFrequency;
            ++i;
        }
    }

    private void optimiseSelectorsAndHuffmanTables(boolean storeSelectors) {
        char[] mtfBlock = this.mtfBlock;
        byte[] selectors = this.selectors;
        int[][] huffmanCodeLengths = this.huffmanCodeLengths;
        int mtfLength = this.mtfLength;
        int mtfAlphabetSize = this.mtfAlphabetSize;
        int totalTables = huffmanCodeLengths.length;
        int[][] tableFrequencies = new int[totalTables][mtfAlphabetSize];
        int selectorIndex = 0;
        int groupStart = 0;
        while (groupStart < mtfLength) {
            int groupEnd = Math.min((int)(groupStart + 50), (int)mtfLength) - 1;
            short[] cost = new short[totalTables];
            for (int i = groupStart; i <= groupEnd; ++i) {
                char value = mtfBlock[i];
                for (int j = 0; j < totalTables; ++j) {
                    short[] arrs = cost;
                    int n = j;
                    arrs[n] = (short)(arrs[n] + huffmanCodeLengths[j][value]);
                }
            }
            int bestTable = 0;
            short bestCost = cost[0];
            for (int i = 1; i < totalTables; i = (int)((byte)(i + 1))) {
                short tableCost = cost[i];
                if (tableCost >= bestCost) continue;
                bestCost = tableCost;
                bestTable = i;
            }
            int[] bestGroupFrequencies = tableFrequencies[bestTable];
            for (int i = groupStart; i <= groupEnd; ++i) {
                int[] arrn = bestGroupFrequencies;
                char c = mtfBlock[i];
                arrn[c] = arrn[c] + 1;
            }
            if (storeSelectors) {
                selectors[selectorIndex++] = bestTable;
            }
            groupStart = groupEnd + 1;
        }
        int i = 0;
        while (i < totalTables) {
            Bzip2HuffmanStageEncoder.generateHuffmanCodeLengths((int)mtfAlphabetSize, (int[])tableFrequencies[i], (int[])huffmanCodeLengths[i]);
            ++i;
        }
    }

    private void assignHuffmanCodeSymbols() {
        int[][] huffmanMergedCodeSymbols = this.huffmanMergedCodeSymbols;
        int[][] huffmanCodeLengths = this.huffmanCodeLengths;
        int mtfAlphabetSize = this.mtfAlphabetSize;
        int totalTables = huffmanCodeLengths.length;
        int i = 0;
        while (i < totalTables) {
            int[] tableLengths = huffmanCodeLengths[i];
            int minimumLength = 32;
            int maximumLength = 0;
            for (int j = 0; j < mtfAlphabetSize; ++j) {
                int length = tableLengths[j];
                if (length > maximumLength) {
                    maximumLength = length;
                }
                if (length >= minimumLength) continue;
                minimumLength = length;
            }
            int code = 0;
            for (int j = minimumLength; j <= maximumLength; code <<= 1, ++j) {
                for (int k = 0; k < mtfAlphabetSize; ++k) {
                    if ((huffmanCodeLengths[i][k] & 255) != j) continue;
                    huffmanMergedCodeSymbols[i][k] = j << 24 | code;
                    ++code;
                }
            }
            ++i;
        }
    }

    private void writeSelectorsAndHuffmanTables(ByteBuf out) {
        Bzip2BitWriter writer = this.writer;
        byte[] selectors = this.selectors;
        int totalSelectors = selectors.length;
        int[][] huffmanCodeLengths = this.huffmanCodeLengths;
        int totalTables = huffmanCodeLengths.length;
        int mtfAlphabetSize = this.mtfAlphabetSize;
        writer.writeBits((ByteBuf)out, (int)3, (long)((long)totalTables));
        writer.writeBits((ByteBuf)out, (int)15, (long)((long)totalSelectors));
        Bzip2MoveToFrontTable selectorMTF = new Bzip2MoveToFrontTable();
        for (byte selector : selectors) {
            writer.writeUnary((ByteBuf)out, (int)selectorMTF.valueToFront((byte)selector));
        }
        byte[] arrby = huffmanCodeLengths;
        int n = arrby.length;
        int n2 = 0;
        while (n2 < n) {
            byte tableLengths = arrby[n2];
            void currentLength = tableLengths[0];
            writer.writeBits((ByteBuf)out, (int)5, (long)((long)currentLength));
            for (int j = 0; j < mtfAlphabetSize; ++j) {
                void codeLength = tableLengths[j];
                int value = currentLength < codeLength ? 2 : 3;
                int delta = Math.abs((int)(codeLength - currentLength));
                while (delta-- > 0) {
                    writer.writeBits((ByteBuf)out, (int)2, (long)((long)value));
                }
                writer.writeBoolean((ByteBuf)out, (boolean)false);
                currentLength = codeLength;
            }
            ++n2;
        }
    }

    private void writeBlockData(ByteBuf out) {
        Bzip2BitWriter writer = this.writer;
        int[][] huffmanMergedCodeSymbols = this.huffmanMergedCodeSymbols;
        byte[] selectors = this.selectors;
        char[] mtf = this.mtfBlock;
        int mtfLength = this.mtfLength;
        int selectorIndex = 0;
        int mtfIndex = 0;
        block0 : while (mtfIndex < mtfLength) {
            int groupEnd = Math.min((int)(mtfIndex + 50), (int)mtfLength) - 1;
            int[] tableMergedCodeSymbols = huffmanMergedCodeSymbols[selectors[selectorIndex++]];
            do {
                if (mtfIndex > groupEnd) continue block0;
                int mergedCodeSymbol = tableMergedCodeSymbols[mtf[mtfIndex++]];
                writer.writeBits((ByteBuf)out, (int)(mergedCodeSymbol >>> 24), (long)((long)mergedCodeSymbol));
            } while (true);
            break;
        }
        return;
    }

    void encode(ByteBuf out) {
        this.generateHuffmanOptimisationSeeds();
        int i = 3;
        do {
            if (i < 0) {
                this.assignHuffmanCodeSymbols();
                this.writeSelectorsAndHuffmanTables((ByteBuf)out);
                this.writeBlockData((ByteBuf)out);
                return;
            }
            this.optimiseSelectorsAndHuffmanTables((boolean)(i == 0));
            --i;
        } while (true);
    }
}

