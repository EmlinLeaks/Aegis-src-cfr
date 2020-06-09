/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.handler.codec.compression.Bzip2MoveToFrontTable;

final class Bzip2MTFAndRLE2StageEncoder {
    private final int[] bwtBlock;
    private final int bwtLength;
    private final boolean[] bwtValuesPresent;
    private final char[] mtfBlock;
    private int mtfLength;
    private final int[] mtfSymbolFrequencies = new int[258];
    private int alphabetSize;

    Bzip2MTFAndRLE2StageEncoder(int[] bwtBlock, int bwtLength, boolean[] bwtValuesPresent) {
        this.bwtBlock = bwtBlock;
        this.bwtLength = bwtLength;
        this.bwtValuesPresent = bwtValuesPresent;
        this.mtfBlock = new char[bwtLength + 1];
    }

    void encode() {
        int totalRunAs;
        int endOfBlockSymbol;
        int[] mtfSymbolFrequencies;
        int mtfIndex;
        int totalRunBs;
        block10 : {
            int bwtLength = this.bwtLength;
            boolean[] bwtValuesPresent = this.bwtValuesPresent;
            int[] bwtBlock = this.bwtBlock;
            char[] mtfBlock = this.mtfBlock;
            mtfSymbolFrequencies = this.mtfSymbolFrequencies;
            byte[] huffmanSymbolMap = new byte[256];
            Bzip2MoveToFrontTable symbolMTF = new Bzip2MoveToFrontTable();
            int totalUniqueValues = 0;
            for (int i = 0; i < huffmanSymbolMap.length; ++i) {
                if (!bwtValuesPresent[i]) continue;
                huffmanSymbolMap[i] = (byte)totalUniqueValues++;
            }
            endOfBlockSymbol = totalUniqueValues + 1;
            mtfIndex = 0;
            int repeatCount = 0;
            totalRunAs = 0;
            totalRunBs = 0;
            int i = 0;
            do {
                block13 : {
                    int mtfPosition;
                    block14 : {
                        block15 : {
                            block11 : {
                                block12 : {
                                    if (i >= bwtLength) break block11;
                                    mtfPosition = symbolMTF.valueToFront((byte)huffmanSymbolMap[bwtBlock[i] & 255]);
                                    if (mtfPosition != 0) break block12;
                                    ++repeatCount;
                                    break block13;
                                }
                                if (repeatCount <= 0) break block14;
                                --repeatCount;
                                break block15;
                            }
                            if (repeatCount > 0) {
                                --repeatCount;
                                break;
                            }
                            break block10;
                        }
                        do {
                            if ((repeatCount & 1) == 0) {
                                mtfBlock[mtfIndex++] = '\u0000';
                                ++totalRunAs;
                            } else {
                                mtfBlock[mtfIndex++] = '\u0001';
                                ++totalRunBs;
                            }
                            if (repeatCount <= 1) {
                                repeatCount = 0;
                                break;
                            }
                            repeatCount = repeatCount - 2 >>> 1;
                        } while (true);
                    }
                    mtfBlock[mtfIndex++] = (char)(mtfPosition + 1);
                    int[] arrn = mtfSymbolFrequencies;
                    int n = mtfPosition + 1;
                    arrn[n] = arrn[n] + 1;
                }
                ++i;
            } while (true);
            do {
                if ((repeatCount & 1) == 0) {
                    mtfBlock[mtfIndex++] = '\u0000';
                    ++totalRunAs;
                } else {
                    mtfBlock[mtfIndex++] = '\u0001';
                    ++totalRunBs;
                }
                if (repeatCount <= 1) break;
                repeatCount = repeatCount - 2 >>> 1;
            } while (true);
        }
        mtfBlock[mtfIndex] = (char)endOfBlockSymbol;
        int[] arrn = mtfSymbolFrequencies;
        int n = endOfBlockSymbol;
        arrn[n] = arrn[n] + 1;
        int[] arrn2 = mtfSymbolFrequencies;
        arrn2[0] = arrn2[0] + totalRunAs;
        int[] arrn3 = mtfSymbolFrequencies;
        arrn3[1] = arrn3[1] + totalRunBs;
        this.mtfLength = mtfIndex + 1;
        this.alphabetSize = endOfBlockSymbol + 1;
    }

    char[] mtfBlock() {
        return this.mtfBlock;
    }

    int mtfLength() {
        return this.mtfLength;
    }

    int mtfAlphabetSize() {
        return this.alphabetSize;
    }

    int[] mtfSymbolFrequencies() {
        return this.mtfSymbolFrequencies;
    }
}

