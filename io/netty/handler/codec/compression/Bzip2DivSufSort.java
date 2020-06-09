/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.handler.codec.compression.Bzip2DivSufSort;

final class Bzip2DivSufSort {
    private static final int STACK_SIZE = 64;
    private static final int BUCKET_A_SIZE = 256;
    private static final int BUCKET_B_SIZE = 65536;
    private static final int SS_BLOCKSIZE = 1024;
    private static final int INSERTIONSORT_THRESHOLD = 8;
    private static final int[] LOG_2_TABLE = new int[]{-1, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7};
    private final int[] SA;
    private final byte[] T;
    private final int n;

    Bzip2DivSufSort(byte[] block, int[] bwtBlock, int blockLength) {
        this.T = block;
        this.SA = bwtBlock;
        this.n = blockLength;
    }

    private static void swapElements(int[] array1, int idx1, int[] array2, int idx2) {
        int temp = array1[idx1];
        array1[idx1] = array2[idx2];
        array2[idx2] = temp;
    }

    private int ssCompare(int p1, int p2, int depth) {
        int U2;
        int[] SA = this.SA;
        byte[] T = this.T;
        int U1n = SA[p1 + 1] + 2;
        int U2n = SA[p2 + 1] + 2;
        int U1 = depth + SA[p1];
        for (U2 = depth + SA[p2]; U1 < U1n && U2 < U2n && T[U1] == T[U2]; ++U1, ++U2) {
        }
        if (U1 < U1n) {
            if (U2 >= U2n) return 1;
            int n = (T[U1] & 255) - (T[U2] & 255);
            return n;
        }
        if (U2 >= U2n) return 0;
        return -1;
    }

    private int ssCompareLast(int pa, int p1, int p2, int depth, int size) {
        int U2;
        int[] SA = this.SA;
        byte[] T = this.T;
        int U1 = depth + SA[p1];
        int U1n = size;
        int U2n = SA[p2 + 1] + 2;
        for (U2 = depth + SA[p2]; U1 < U1n && U2 < U2n && T[U1] == T[U2]; ++U1, ++U2) {
        }
        if (U1 < U1n) {
            if (U2 >= U2n) return 1;
            int n = (T[U1] & 255) - (T[U2] & 255);
            return n;
        }
        if (U2 == U2n) {
            return 1;
        }
        U1 %= size;
        U1n = SA[pa] + 2;
        while (U1 < U1n && U2 < U2n && T[U1] == T[U2]) {
            ++U1;
            ++U2;
        }
        if (U1 < U1n) {
            if (U2 >= U2n) return 1;
            int n = (T[U1] & 255) - (T[U2] & 255);
            return n;
        }
        if (U2 >= U2n) return 0;
        return -1;
    }

    private void ssInsertionSort(int pa, int first, int last, int depth) {
        int[] SA = this.SA;
        int i = last - 2;
        while (first <= i) {
            int r;
            int t = SA[i];
            int j = i + 1;
            while (0 < (r = this.ssCompare((int)(pa + t), (int)(pa + SA[j]), (int)depth))) {
                do {
                    SA[j - 1] = SA[j];
                } while (++j < last && SA[j] < 0);
                if (last > j) continue;
            }
            if (r == 0) {
                SA[j] = ~SA[j];
            }
            SA[j - 1] = t;
            --i;
        }
    }

    private void ssFixdown(int td, int pa, int sa, int i, int size) {
        int j;
        int[] SA = this.SA;
        byte[] T = this.T;
        int v = SA[sa + i];
        int c = T[td + SA[pa + v]] & 255;
        while ((j = 2 * i + 1) < size) {
            int e;
            int d;
            int k;
            if ((d = T[td + SA[pa + SA[sa + (k = j++)]]] & 255) < (e = T[td + SA[pa + SA[sa + j]]] & 255)) {
                k = j;
                d = e;
            }
            if (d <= c) break;
            SA[sa + i] = SA[sa + k];
            i = k;
        }
        SA[sa + i] = v;
    }

    private void ssHeapSort(int td, int pa, int sa, int size) {
        int i;
        int[] SA = this.SA;
        byte[] T = this.T;
        int m = size;
        if (size % 2 == 0 && (T[td + SA[pa + SA[sa + --m / 2]]] & 255) < (T[td + SA[pa + SA[sa + m]]] & 255)) {
            Bzip2DivSufSort.swapElements((int[])SA, (int)(sa + m), (int[])SA, (int)(sa + m / 2));
        }
        for (i = m / 2 - 1; 0 <= i; --i) {
            this.ssFixdown((int)td, (int)pa, (int)sa, (int)i, (int)m);
        }
        if (size % 2 == 0) {
            Bzip2DivSufSort.swapElements((int[])SA, (int)sa, (int[])SA, (int)(sa + m));
            this.ssFixdown((int)td, (int)pa, (int)sa, (int)0, (int)m);
        }
        i = m - 1;
        while (0 < i) {
            int t = SA[sa];
            SA[sa] = SA[sa + i];
            this.ssFixdown((int)td, (int)pa, (int)sa, (int)0, (int)i);
            SA[sa + i] = t;
            --i;
        }
    }

    private int ssMedian3(int td, int pa, int v1, int v2, int v3) {
        int[] SA = this.SA;
        byte[] T = this.T;
        int T_v1 = T[td + SA[pa + SA[v1]]] & 255;
        int T_v2 = T[td + SA[pa + SA[v2]]] & 255;
        int T_v3 = T[td + SA[pa + SA[v3]]] & 255;
        if (T_v1 > T_v2) {
            int temp = v1;
            v1 = v2;
            v2 = temp;
            int T_vtemp = T_v1;
            T_v1 = T_v2;
            T_v2 = T_vtemp;
        }
        if (T_v2 <= T_v3) return v2;
        if (T_v1 <= T_v3) return v3;
        return v1;
    }

    private int ssMedian5(int td, int pa, int v1, int v2, int v3, int v4, int v5) {
        int temp;
        int T_vtemp;
        int[] SA = this.SA;
        byte[] T = this.T;
        int T_v1 = T[td + SA[pa + SA[v1]]] & 255;
        int T_v2 = T[td + SA[pa + SA[v2]]] & 255;
        int T_v3 = T[td + SA[pa + SA[v3]]] & 255;
        int T_v4 = T[td + SA[pa + SA[v4]]] & 255;
        int T_v5 = T[td + SA[pa + SA[v5]]] & 255;
        if (T_v2 > T_v3) {
            temp = v2;
            v2 = v3;
            v3 = temp;
            T_vtemp = T_v2;
            T_v2 = T_v3;
            T_v3 = T_vtemp;
        }
        if (T_v4 > T_v5) {
            temp = v4;
            v4 = v5;
            v5 = temp;
            T_vtemp = T_v4;
            T_v4 = T_v5;
            T_v5 = T_vtemp;
        }
        if (T_v2 > T_v4) {
            v4 = temp = v2;
            T_v4 = T_vtemp = T_v2;
            temp = v3;
            v3 = v5;
            v5 = temp;
            T_vtemp = T_v3;
            T_v3 = T_v5;
            T_v5 = T_vtemp;
        }
        if (T_v1 > T_v3) {
            temp = v1;
            v1 = v3;
            v3 = temp;
            T_vtemp = T_v1;
            T_v1 = T_v3;
            T_v3 = T_vtemp;
        }
        if (T_v1 > T_v4) {
            v4 = temp = v1;
            T_v4 = T_vtemp = T_v1;
            v3 = v5;
            T_v3 = T_v5;
        }
        if (T_v3 <= T_v4) return v3;
        return v4;
    }

    private int ssPivot(int td, int pa, int first, int last) {
        int t = last - first;
        int middle = first + t / 2;
        if (t > 512) return this.ssMedian3((int)td, (int)pa, (int)this.ssMedian3((int)td, (int)pa, (int)first, (int)(first + (t >>= 3)), (int)(first + (t << 1))), (int)this.ssMedian3((int)td, (int)pa, (int)(middle - t), (int)middle, (int)(middle + t)), (int)this.ssMedian3((int)td, (int)pa, (int)(last - 1 - (t << 1)), (int)(last - 1 - t), (int)(last - 1)));
        if (t > 32) return this.ssMedian5((int)td, (int)pa, (int)first, (int)(first + (t >>= 2)), (int)middle, (int)(last - 1 - t), (int)(last - 1));
        return this.ssMedian3((int)td, (int)pa, (int)first, (int)middle, (int)(last - 1));
    }

    private static int ssLog(int n) {
        int n2;
        if ((n & 65280) != 0) {
            n2 = 8 + LOG_2_TABLE[n >> 8 & 255];
            return n2;
        }
        n2 = LOG_2_TABLE[n & 255];
        return n2;
    }

    private int ssSubstringPartition(int pa, int first, int last, int depth) {
        int[] SA = this.SA;
        int a = first - 1;
        int b = last;
        do {
            if (++a < b && SA[pa + SA[a]] + depth >= SA[pa + SA[a] + 1] + 1) {
                SA[a] = ~SA[a];
                continue;
            }
            --b;
            while (a < b && SA[pa + SA[b]] + depth < SA[pa + SA[b] + 1] + 1) {
                --b;
            }
            if (b <= a) {
                if (first >= a) return a;
                SA[first] = ~SA[first];
                return a;
            }
            int t = ~SA[b];
            SA[b] = SA[a];
            SA[a] = t;
        } while (true);
    }

    private void ssMultiKeyIntroSort(int pa, int first, int last, int depth) {
        int[] SA = this.SA;
        byte[] T = this.T;
        StackEntry[] stack = new StackEntry[64];
        int x = 0;
        int ssize = 0;
        int limit = Bzip2DivSufSort.ssLog((int)(last - first));
        do {
            int b;
            int v;
            int c;
            int a;
            int d;
            if (last - first <= 8) {
                if (1 < last - first) {
                    this.ssInsertionSort((int)pa, (int)first, (int)last, (int)depth);
                }
                if (ssize == 0) {
                    return;
                }
                StackEntry entry = stack[--ssize];
                first = entry.a;
                last = entry.b;
                depth = entry.c;
                limit = entry.d;
                continue;
            }
            int Td = depth;
            if (limit-- == 0) {
                this.ssHeapSort((int)Td, (int)pa, (int)first, (int)(last - first));
            }
            if (limit >= 0) {
                a = this.ssPivot((int)Td, (int)pa, (int)first, (int)last);
                v = T[Td + SA[pa + SA[a]]] & 255;
                Bzip2DivSufSort.swapElements((int[])SA, (int)first, (int[])SA, (int)a);
                for (b = first + 1; b < last && (x = T[Td + SA[pa + SA[b]]] & 255) == v; ++b) {
                }
                a = b;
                if (a < last && x < v) {
                    while (++b < last && (x = T[Td + SA[pa + SA[b]]] & 255) <= v) {
                        if (x != v) continue;
                        Bzip2DivSufSort.swapElements((int[])SA, (int)b, (int[])SA, (int)a);
                        ++a;
                    }
                }
                for (c = last - 1; b < c && (x = T[Td + SA[pa + SA[c]]] & 255) == v; --c) {
                }
                d = c;
                if (b < d && x > v) {
                    while (b < --c && (x = T[Td + SA[pa + SA[c]]] & 255) >= v) {
                        if (x != v) continue;
                        Bzip2DivSufSort.swapElements((int[])SA, (int)c, (int[])SA, (int)d);
                        --d;
                    }
                }
            } else {
                v = T[Td + SA[pa + SA[first]]] & 255;
                for (a = first + 1; a < last; ++a) {
                    x = T[Td + SA[pa + SA[a]]] & 255;
                    if (x == v) continue;
                    if (1 < a - first) break;
                    v = x;
                    first = a;
                }
                if ((T[Td + SA[pa + SA[first]] - 1] & 255) < v) {
                    first = this.ssSubstringPartition((int)pa, (int)first, (int)a, (int)depth);
                }
                if (a - first <= last - a) {
                    if (1 < a - first) {
                        stack[ssize++] = new StackEntry((int)a, (int)last, (int)depth, (int)-1);
                        last = a;
                        ++depth;
                        limit = Bzip2DivSufSort.ssLog((int)(a - first));
                        continue;
                    }
                    first = a;
                    limit = -1;
                    continue;
                }
                if (1 < last - a) {
                    stack[ssize++] = new StackEntry((int)first, (int)a, (int)(depth + 1), (int)Bzip2DivSufSort.ssLog((int)(a - first)));
                    first = a;
                    limit = -1;
                    continue;
                }
                last = a;
                ++depth;
                limit = Bzip2DivSufSort.ssLog((int)(a - first));
                continue;
            }
            while (b < c) {
                Bzip2DivSufSort.swapElements((int[])SA, (int)b, (int[])SA, (int)c);
                while (++b < c && (x = T[Td + SA[pa + SA[b]]] & 255) <= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements((int[])SA, (int)b, (int[])SA, (int)a);
                    ++a;
                }
                while (b < --c && (x = T[Td + SA[pa + SA[c]]] & 255) >= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements((int[])SA, (int)c, (int[])SA, (int)d);
                    --d;
                }
            }
            if (a <= d) {
                c = b - 1;
                int s = a - first;
                int t = b - a;
                if (s > t) {
                    s = t;
                }
                int e = first;
                int f = b - s;
                while (0 < s) {
                    Bzip2DivSufSort.swapElements((int[])SA, (int)e, (int[])SA, (int)f);
                    --s;
                    ++e;
                    ++f;
                }
                s = d - c;
                t = last - d - 1;
                if (s > t) {
                    s = t;
                }
                e = b;
                f = last - s;
                while (0 < s) {
                    Bzip2DivSufSort.swapElements((int[])SA, (int)e, (int[])SA, (int)f);
                    --s;
                    ++e;
                    ++f;
                }
                a = first + (b - a);
                c = last - (d - c);
                int n = b = v <= (T[Td + SA[pa + SA[a]] - 1] & 255) ? a : this.ssSubstringPartition((int)pa, (int)a, (int)c, (int)depth);
                if (a - first <= last - c) {
                    if (last - c <= c - b) {
                        stack[ssize++] = new StackEntry((int)b, (int)c, (int)(depth + 1), (int)Bzip2DivSufSort.ssLog((int)(c - b)));
                        stack[ssize++] = new StackEntry((int)c, (int)last, (int)depth, (int)limit);
                        last = a;
                        continue;
                    }
                    if (a - first <= c - b) {
                        stack[ssize++] = new StackEntry((int)c, (int)last, (int)depth, (int)limit);
                        stack[ssize++] = new StackEntry((int)b, (int)c, (int)(depth + 1), (int)Bzip2DivSufSort.ssLog((int)(c - b)));
                        last = a;
                        continue;
                    }
                    stack[ssize++] = new StackEntry((int)c, (int)last, (int)depth, (int)limit);
                    stack[ssize++] = new StackEntry((int)first, (int)a, (int)depth, (int)limit);
                    first = b;
                    last = c;
                    ++depth;
                    limit = Bzip2DivSufSort.ssLog((int)(c - b));
                    continue;
                }
                if (a - first <= c - b) {
                    stack[ssize++] = new StackEntry((int)b, (int)c, (int)(depth + 1), (int)Bzip2DivSufSort.ssLog((int)(c - b)));
                    stack[ssize++] = new StackEntry((int)first, (int)a, (int)depth, (int)limit);
                    first = c;
                    continue;
                }
                if (last - c <= c - b) {
                    stack[ssize++] = new StackEntry((int)first, (int)a, (int)depth, (int)limit);
                    stack[ssize++] = new StackEntry((int)b, (int)c, (int)(depth + 1), (int)Bzip2DivSufSort.ssLog((int)(c - b)));
                    first = c;
                    continue;
                }
                stack[ssize++] = new StackEntry((int)first, (int)a, (int)depth, (int)limit);
                stack[ssize++] = new StackEntry((int)c, (int)last, (int)depth, (int)limit);
                first = b;
                last = c;
                ++depth;
                limit = Bzip2DivSufSort.ssLog((int)(c - b));
                continue;
            }
            ++limit;
            if ((T[Td + SA[pa + SA[first]] - 1] & 255) < v) {
                first = this.ssSubstringPartition((int)pa, (int)first, (int)last, (int)depth);
                limit = Bzip2DivSufSort.ssLog((int)(last - first));
            }
            ++depth;
        } while (true);
    }

    private static void ssBlockSwap(int[] array1, int first1, int[] array2, int first2, int size) {
        int i = size;
        int a = first1;
        int b = first2;
        while (0 < i) {
            Bzip2DivSufSort.swapElements((int[])array1, (int)a, (int[])array2, (int)b);
            --i;
            ++a;
            ++b;
        }
    }

    private void ssMergeForward(int pa, int[] buf, int bufoffset, int first, int middle, int last, int depth) {
        int[] SA = this.SA;
        int bufend = bufoffset + (middle - first) - 1;
        Bzip2DivSufSort.ssBlockSwap((int[])buf, (int)bufoffset, (int[])SA, (int)first, (int)(middle - first));
        int t = SA[first];
        int i = first;
        int j = bufoffset;
        int k = middle;
        do {
            block13 : {
                block12 : {
                    block11 : {
                        int r;
                        if ((r = this.ssCompare((int)(pa + buf[j]), (int)(pa + SA[k]), (int)depth)) < 0) break block11;
                        if (r > 0) break block12;
                        break block13;
                    }
                    do {
                        SA[i++] = buf[j];
                        if (bufend <= j) {
                            buf[j] = t;
                            return;
                        }
                        buf[j++] = SA[i];
                    } while (buf[j] < 0);
                    continue;
                }
                do {
                    SA[i++] = SA[k];
                    SA[k++] = SA[i];
                    if (last > k) continue;
                    do {
                        if (j >= bufend) {
                            SA[i] = buf[j];
                            buf[j] = t;
                            return;
                        }
                        SA[i++] = buf[j];
                        buf[j++] = SA[i];
                    } while (true);
                } while (SA[k] < 0);
                continue;
            }
            SA[k] = ~SA[k];
            do {
                SA[i++] = buf[j];
                if (bufend <= j) {
                    buf[j] = t;
                    return;
                }
                buf[j++] = SA[i];
            } while (buf[j] < 0);
            do {
                SA[i++] = SA[k];
                SA[k++] = SA[i];
                if (last > k) continue;
                do {
                    if (j >= bufend) {
                        SA[i] = buf[j];
                        buf[j] = t;
                        return;
                    }
                    SA[i++] = buf[j];
                    buf[j++] = SA[i];
                } while (true);
            } while (SA[k] < 0);
        } while (true);
    }

    private void ssMergeBackward(int pa, int[] buf, int bufoffset, int first, int middle, int last, int depth) {
        int p1;
        int p2;
        int[] SA = this.SA;
        int bufend = bufoffset + (last - middle);
        Bzip2DivSufSort.ssBlockSwap((int[])buf, (int)bufoffset, (int[])SA, (int)middle, (int)(last - middle));
        int x = 0;
        if (buf[bufend - 1] < 0) {
            x |= true;
            p1 = pa + ~buf[bufend - 1];
        } else {
            p1 = pa + buf[bufend - 1];
        }
        if (SA[middle - 1] < 0) {
            x |= 2;
            p2 = pa + ~SA[middle - 1];
        } else {
            p2 = pa + SA[middle - 1];
        }
        int t = SA[last - 1];
        int i = last - 1;
        int j = bufend - 1;
        int k = middle - 1;
        do {
            int r;
            if ((r = this.ssCompare((int)p1, (int)p2, (int)depth)) > 0) {
                if ((x & 1) != 0) {
                    do {
                        SA[i--] = buf[j];
                        buf[j--] = SA[i];
                    } while (buf[j] < 0);
                    x ^= 1;
                }
                SA[i--] = buf[j];
                if (j <= bufoffset) {
                    buf[j] = t;
                    return;
                }
                buf[j--] = SA[i];
                if (buf[j] < 0) {
                    x |= 1;
                    p1 = pa + ~buf[j];
                    continue;
                }
                p1 = pa + buf[j];
                continue;
            }
            if (r < 0) {
                if ((x & 2) != 0) {
                    do {
                        SA[i--] = SA[k];
                        SA[k--] = SA[i];
                    } while (SA[k] < 0);
                    x ^= 2;
                }
                SA[i--] = SA[k];
                SA[k--] = SA[i];
                if (k < first) {
                    do {
                        if (bufoffset >= j) {
                            SA[i] = buf[j];
                            buf[j] = t;
                            return;
                        }
                        SA[i--] = buf[j];
                        buf[j--] = SA[i];
                    } while (true);
                }
                if (SA[k] < 0) {
                    x |= 2;
                    p2 = pa + ~SA[k];
                    continue;
                }
                p2 = pa + SA[k];
                continue;
            }
            if ((x & 1) != 0) {
                do {
                    SA[i--] = buf[j];
                    buf[j--] = SA[i];
                } while (buf[j] < 0);
                x ^= 1;
            }
            SA[i--] = ~buf[j];
            if (j <= bufoffset) {
                buf[j] = t;
                return;
            }
            buf[j--] = SA[i];
            if ((x & 2) != 0) {
                do {
                    SA[i--] = SA[k];
                    SA[k--] = SA[i];
                } while (SA[k] < 0);
                x ^= 2;
            }
            SA[i--] = SA[k];
            SA[k--] = SA[i];
            if (k < first) {
                do {
                    if (bufoffset >= j) {
                        SA[i] = buf[j];
                        buf[j] = t;
                        return;
                    }
                    SA[i--] = buf[j];
                    buf[j--] = SA[i];
                } while (true);
            }
            if (buf[j] < 0) {
                x |= 1;
                p1 = pa + ~buf[j];
            } else {
                p1 = pa + buf[j];
            }
            if (SA[k] < 0) {
                x |= 2;
                p2 = pa + ~SA[k];
                continue;
            }
            p2 = pa + SA[k];
        } while (true);
    }

    private static int getIDX(int a) {
        int n;
        if (0 <= a) {
            n = a;
            return n;
        }
        n = ~a;
        return n;
    }

    private void ssMergeCheckEqual(int pa, int depth, int a) {
        int[] SA = this.SA;
        if (0 > SA[a]) return;
        if (this.ssCompare((int)(pa + Bzip2DivSufSort.getIDX((int)SA[a - 1])), (int)(pa + SA[a]), (int)depth) != 0) return;
        SA[a] = ~SA[a];
    }

    private void ssMerge(int pa, int first, int middle, int last, int[] buf, int bufoffset, int bufsize, int depth) {
        int[] SA = this.SA;
        StackEntry[] stack = new StackEntry[64];
        int check = 0;
        int ssize = 0;
        do {
            StackEntry entry;
            if (last - middle <= bufsize) {
                if (first < middle && middle < last) {
                    this.ssMergeBackward((int)pa, (int[])buf, (int)bufoffset, (int)first, (int)middle, (int)last, (int)depth);
                }
                if (check & true) {
                    this.ssMergeCheckEqual((int)pa, (int)depth, (int)first);
                }
                if ((check & 2) != 0) {
                    this.ssMergeCheckEqual((int)pa, (int)depth, (int)last);
                }
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                first = entry.a;
                middle = entry.b;
                last = entry.c;
                check = entry.d;
                continue;
            }
            if (middle - first <= bufsize) {
                if (first < middle) {
                    this.ssMergeForward((int)pa, (int[])buf, (int)bufoffset, (int)first, (int)middle, (int)last, (int)depth);
                }
                if ((check & 1) != 0) {
                    this.ssMergeCheckEqual((int)pa, (int)depth, (int)first);
                }
                if ((check & 2) != 0) {
                    this.ssMergeCheckEqual((int)pa, (int)depth, (int)last);
                }
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                first = entry.a;
                middle = entry.b;
                last = entry.c;
                check = entry.d;
                continue;
            }
            int m = 0;
            int len = Math.min((int)(middle - first), (int)(last - middle));
            int half = len >> 1;
            while (0 < len) {
                if (this.ssCompare((int)(pa + Bzip2DivSufSort.getIDX((int)SA[middle + m + half])), (int)(pa + Bzip2DivSufSort.getIDX((int)SA[middle - m - half - 1])), (int)depth) < 0) {
                    m += half + 1;
                    half -= len & 1 ^ 1;
                }
                len = half;
                half >>= 1;
            }
            if (0 < m) {
                int j;
                Bzip2DivSufSort.ssBlockSwap((int[])SA, (int)(middle - m), (int[])SA, (int)middle, (int)m);
                int i = j = middle;
                int next = 0;
                if (middle + m < last) {
                    if (SA[middle + m] < 0) {
                        while (SA[i - 1] < 0) {
                            --i;
                        }
                        SA[middle + m] = ~SA[middle + m];
                    }
                    j = middle;
                    while (SA[j] < 0) {
                        ++j;
                    }
                    next = 1;
                }
                if (i - first <= last - j) {
                    stack[ssize++] = new StackEntry((int)j, (int)(middle + m), (int)last, (int)(check & 2 | next & 1));
                    middle -= m;
                    last = i;
                    check &= 1;
                    continue;
                }
                if (i == middle && middle == j) {
                    next <<= 1;
                }
                stack[ssize++] = new StackEntry((int)first, (int)(middle - m), (int)i, (int)(check & 1 | next & 2));
                first = j;
                middle += m;
                check = check & 2 | next & 1;
                continue;
            }
            if ((check & 1) != 0) {
                this.ssMergeCheckEqual((int)pa, (int)depth, (int)first);
            }
            this.ssMergeCheckEqual((int)pa, (int)depth, (int)middle);
            if ((check & 2) != 0) {
                this.ssMergeCheckEqual((int)pa, (int)depth, (int)last);
            }
            if (ssize == 0) {
                return;
            }
            entry = stack[--ssize];
            first = entry.a;
            middle = entry.b;
            last = entry.c;
            check = entry.d;
        } while (true);
    }

    private void subStringSort(int pa, int first, int last, int[] buf, int bufoffset, int bufsize, int depth, boolean lastsuffix, int size) {
        int k;
        int[] SA = this.SA;
        if (lastsuffix) {
            ++first;
        }
        int a = first;
        int i = 0;
        while (a + 1024 < last) {
            this.ssMultiKeyIntroSort((int)pa, (int)a, (int)(a + 1024), (int)depth);
            int[] curbuf = SA;
            int curbufoffset = a + 1024;
            int curbufsize = last - (a + 1024);
            if (curbufsize <= bufsize) {
                curbufsize = bufsize;
                curbuf = buf;
                curbufoffset = bufoffset;
            }
            int b = a;
            k = 1024;
            int j = i;
            while ((j & 1) != 0) {
                this.ssMerge((int)pa, (int)(b - k), (int)b, (int)(b + k), (int[])curbuf, (int)curbufoffset, (int)curbufsize, (int)depth);
                b -= k;
                k <<= 1;
                j >>>= 1;
            }
            a += 1024;
            ++i;
        }
        this.ssMultiKeyIntroSort((int)pa, (int)a, (int)last, (int)depth);
        k = 1024;
        while (i != 0) {
            if (i & true) {
                this.ssMerge((int)pa, (int)(a - k), (int)a, (int)last, (int[])buf, (int)bufoffset, (int)bufsize, (int)depth);
                a -= k;
            }
            k <<= 1;
            i >>= 1;
        }
        if (!lastsuffix) return;
        i = SA[first - 1];
        int r = 1;
        for (a = first; a < last && (SA[a] < 0 || 0 < (r = this.ssCompareLast((int)pa, (int)(pa + i), (int)(pa + SA[a]), (int)depth, (int)size))); ++a) {
            SA[a - 1] = SA[a];
        }
        if (r == 0) {
            SA[a] = ~SA[a];
        }
        SA[a - 1] = i;
    }

    private int trGetC(int isa, int isaD, int isaN, int p) {
        int n;
        if (isaD + p < isaN) {
            n = this.SA[isaD + p];
            return n;
        }
        n = this.SA[isa + (isaD - isa + p) % (isaN - isa)];
        return n;
    }

    private void trFixdown(int isa, int isaD, int isaN, int sa, int i, int size) {
        int j;
        int[] SA = this.SA;
        int v = SA[sa + i];
        int c = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)v);
        while ((j = 2 * i + 1) < size) {
            int e;
            int d;
            int k;
            if ((d = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[sa + (k = j++)])) < (e = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[sa + j]))) {
                k = j;
                d = e;
            }
            if (d <= c) break;
            SA[sa + i] = SA[sa + k];
            i = k;
        }
        SA[sa + i] = v;
    }

    private void trHeapSort(int isa, int isaD, int isaN, int sa, int size) {
        int i;
        int[] SA = this.SA;
        int m = size;
        if (size % 2 == 0 && this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[sa + --m / 2]) < this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[sa + m])) {
            Bzip2DivSufSort.swapElements((int[])SA, (int)(sa + m), (int[])SA, (int)(sa + m / 2));
        }
        for (i = m / 2 - 1; 0 <= i; --i) {
            this.trFixdown((int)isa, (int)isaD, (int)isaN, (int)sa, (int)i, (int)m);
        }
        if (size % 2 == 0) {
            Bzip2DivSufSort.swapElements((int[])SA, (int)sa, (int[])SA, (int)(sa + m));
            this.trFixdown((int)isa, (int)isaD, (int)isaN, (int)sa, (int)0, (int)m);
        }
        i = m - 1;
        while (0 < i) {
            int t = SA[sa];
            SA[sa] = SA[sa + i];
            this.trFixdown((int)isa, (int)isaD, (int)isaN, (int)sa, (int)0, (int)i);
            SA[sa + i] = t;
            --i;
        }
    }

    private void trInsertionSort(int isa, int isaD, int isaN, int first, int last) {
        int[] SA = this.SA;
        int a = first + 1;
        while (a < last) {
            int r;
            int t = SA[a];
            int b = a - 1;
            while (0 > (r = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)t) - this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[b]))) {
                do {
                    SA[b + 1] = SA[b];
                } while (first <= --b && SA[b] < 0);
                if (b >= first) continue;
            }
            if (r == 0) {
                SA[b] = ~SA[b];
            }
            SA[b + 1] = t;
            ++a;
        }
    }

    private static int trLog(int n) {
        int n2;
        if ((n & -65536) != 0) {
            if ((n & -16777216) != 0) {
                n2 = 24 + LOG_2_TABLE[n >> 24 & 255];
                return n2;
            }
            n2 = LOG_2_TABLE[n >> 16 & 271];
            return n2;
        }
        if ((n & 65280) != 0) {
            n2 = 8 + LOG_2_TABLE[n >> 8 & 255];
            return n2;
        }
        n2 = LOG_2_TABLE[n & 255];
        return n2;
    }

    private int trMedian3(int isa, int isaD, int isaN, int v1, int v2, int v3) {
        int[] SA = this.SA;
        int SA_v1 = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[v1]);
        int SA_v2 = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[v2]);
        int SA_v3 = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[v3]);
        if (SA_v1 > SA_v2) {
            int temp = v1;
            v1 = v2;
            v2 = temp;
            int SA_vtemp = SA_v1;
            SA_v1 = SA_v2;
            SA_v2 = SA_vtemp;
        }
        if (SA_v2 <= SA_v3) return v2;
        if (SA_v1 <= SA_v3) return v3;
        return v1;
    }

    private int trMedian5(int isa, int isaD, int isaN, int v1, int v2, int v3, int v4, int v5) {
        int SA_vtemp;
        int temp;
        int[] SA = this.SA;
        int SA_v1 = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[v1]);
        int SA_v2 = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[v2]);
        int SA_v3 = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[v3]);
        int SA_v4 = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[v4]);
        int SA_v5 = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[v5]);
        if (SA_v2 > SA_v3) {
            temp = v2;
            v2 = v3;
            v3 = temp;
            SA_vtemp = SA_v2;
            SA_v2 = SA_v3;
            SA_v3 = SA_vtemp;
        }
        if (SA_v4 > SA_v5) {
            temp = v4;
            v4 = v5;
            v5 = temp;
            SA_vtemp = SA_v4;
            SA_v4 = SA_v5;
            SA_v5 = SA_vtemp;
        }
        if (SA_v2 > SA_v4) {
            v4 = temp = v2;
            SA_v4 = SA_vtemp = SA_v2;
            temp = v3;
            v3 = v5;
            v5 = temp;
            SA_vtemp = SA_v3;
            SA_v3 = SA_v5;
            SA_v5 = SA_vtemp;
        }
        if (SA_v1 > SA_v3) {
            temp = v1;
            v1 = v3;
            v3 = temp;
            SA_vtemp = SA_v1;
            SA_v1 = SA_v3;
            SA_v3 = SA_vtemp;
        }
        if (SA_v1 > SA_v4) {
            v4 = temp = v1;
            SA_v4 = SA_vtemp = SA_v1;
            v3 = v5;
            SA_v3 = SA_v5;
        }
        if (SA_v3 <= SA_v4) return v3;
        return v4;
    }

    private int trPivot(int isa, int isaD, int isaN, int first, int last) {
        int t = last - first;
        int middle = first + t / 2;
        if (t > 512) return this.trMedian3((int)isa, (int)isaD, (int)isaN, (int)this.trMedian3((int)isa, (int)isaD, (int)isaN, (int)first, (int)(first + (t >>= 3)), (int)(first + (t << 1))), (int)this.trMedian3((int)isa, (int)isaD, (int)isaN, (int)(middle - t), (int)middle, (int)(middle + t)), (int)this.trMedian3((int)isa, (int)isaD, (int)isaN, (int)(last - 1 - (t << 1)), (int)(last - 1 - t), (int)(last - 1)));
        if (t > 32) return this.trMedian5((int)isa, (int)isaD, (int)isaN, (int)first, (int)(first + (t >>= 2)), (int)middle, (int)(last - 1 - t), (int)(last - 1));
        return this.trMedian3((int)isa, (int)isaD, (int)isaN, (int)first, (int)middle, (int)(last - 1));
    }

    private void lsUpdateGroup(int isa, int first, int last) {
        int[] SA = this.SA;
        int a = first;
        while (a < last) {
            int b;
            if (0 <= SA[a]) {
                b = a;
                do {
                    SA[isa + SA[a]] = a;
                } while (++a < last && 0 <= SA[a]);
                SA[b] = b - a;
                if (last <= a) {
                    return;
                }
            }
            b = a;
            do {
                SA[a] = ~SA[a];
            } while (SA[++a] < 0);
            int t = a;
            do {
                SA[isa + SA[b]] = t;
            } while (++b <= a);
            ++a;
        }
    }

    private void lsIntroSort(int isa, int isaD, int isaN, int first, int last) {
        int[] SA = this.SA;
        StackEntry[] stack = new StackEntry[64];
        int x = 0;
        int ssize = 0;
        int limit = Bzip2DivSufSort.trLog((int)(last - first));
        do {
            int b;
            int a;
            int d;
            int c;
            StackEntry entry;
            int v;
            if (last - first <= 8) {
                if (1 < last - first) {
                    this.trInsertionSort((int)isa, (int)isaD, (int)isaN, (int)first, (int)last);
                    this.lsUpdateGroup((int)isa, (int)first, (int)last);
                } else if (last - first == 1) {
                    SA[first] = -1;
                }
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                first = entry.a;
                last = entry.b;
                limit = entry.c;
                continue;
            }
            if (limit-- != 0) {
                a = this.trPivot((int)isa, (int)isaD, (int)isaN, (int)first, (int)last);
                Bzip2DivSufSort.swapElements((int[])SA, (int)first, (int[])SA, (int)a);
                v = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[first]);
                for (b = first + 1; b < last && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[b])) == v; ++b) {
                }
                a = b;
                if (a < last && x < v) {
                    while (++b < last && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[b])) <= v) {
                        if (x != v) continue;
                        Bzip2DivSufSort.swapElements((int[])SA, (int)b, (int[])SA, (int)a);
                        ++a;
                    }
                }
                for (c = last - 1; b < c && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[c])) == v; --c) {
                }
                d = c;
                if (b < d && x > v) {
                    while (b < --c && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[c])) >= v) {
                        if (x != v) continue;
                        Bzip2DivSufSort.swapElements((int[])SA, (int)c, (int[])SA, (int)d);
                        --d;
                    }
                }
            } else {
                this.trHeapSort((int)isa, (int)isaD, (int)isaN, (int)first, (int)(last - first));
                a = last - 1;
                while (first < a) {
                    x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[a]);
                    for (b = a - 1; first <= b && this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[b]) == x; --b) {
                        SA[b] = ~SA[b];
                    }
                    a = b;
                }
                this.lsUpdateGroup((int)isa, (int)first, (int)last);
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                first = entry.a;
                last = entry.b;
                limit = entry.c;
                continue;
            }
            while (b < c) {
                Bzip2DivSufSort.swapElements((int[])SA, (int)b, (int[])SA, (int)c);
                while (++b < c && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[b])) <= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements((int[])SA, (int)b, (int[])SA, (int)a);
                    ++a;
                }
                while (b < --c && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[c])) >= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements((int[])SA, (int)c, (int[])SA, (int)d);
                    --d;
                }
            }
            if (a <= d) {
                c = b - 1;
                int s = a - first;
                int t = b - a;
                if (s > t) {
                    s = t;
                }
                int e = first;
                int f = b - s;
                while (0 < s) {
                    Bzip2DivSufSort.swapElements((int[])SA, (int)e, (int[])SA, (int)f);
                    --s;
                    ++e;
                    ++f;
                }
                s = d - c;
                t = last - d - 1;
                if (s > t) {
                    s = t;
                }
                e = b;
                f = last - s;
                while (0 < s) {
                    Bzip2DivSufSort.swapElements((int[])SA, (int)e, (int[])SA, (int)f);
                    --s;
                    ++e;
                    ++f;
                }
                a = first + (b - a);
                b = last - (d - c);
                v = a - 1;
                for (c = first; c < a; ++c) {
                    SA[isa + SA[c]] = v;
                }
                if (b < last) {
                    v = b - 1;
                    for (c = a; c < b; ++c) {
                        SA[isa + SA[c]] = v;
                    }
                }
                if (b - a == 1) {
                    SA[a] = -1;
                }
                if (a - first <= last - b) {
                    if (first < a) {
                        stack[ssize++] = new StackEntry((int)b, (int)last, (int)limit, (int)0);
                        last = a;
                        continue;
                    }
                    first = b;
                    continue;
                }
                if (b < last) {
                    stack[ssize++] = new StackEntry((int)first, (int)a, (int)limit, (int)0);
                    first = b;
                    continue;
                }
                last = a;
                continue;
            }
            if (ssize == 0) {
                return;
            }
            entry = stack[--ssize];
            first = entry.a;
            last = entry.b;
            limit = entry.c;
        } while (true);
    }

    private void lsSort(int isa, int n, int depth) {
        int last;
        int[] SA;
        int t;
        int first;
        block9 : {
            SA = this.SA;
            int isaD = isa + depth;
            while (-n < SA[0]) {
                first = 0;
                int skip = 0;
                do {
                    if ((t = SA[first]) < 0) {
                        first -= t;
                        skip += t;
                        continue;
                    }
                    if (skip != 0) {
                        SA[first + skip] = skip;
                        skip = 0;
                    }
                    last = SA[isa + t] + 1;
                    this.lsIntroSort((int)isa, (int)isaD, (int)(isa + n), (int)first, (int)last);
                    first = last;
                } while (first < n);
                if (skip != 0) {
                    SA[first + skip] = skip;
                }
                if (n >= isaD - isa) {
                    isaD += isaD - isa;
                    continue;
                }
                break block9;
            }
            return;
        }
        first = 0;
        do {
            if ((t = SA[first]) < 0) {
                first -= t;
                continue;
            }
            last = SA[isa + t] + 1;
            for (int i = first; i < last; ++i) {
                SA[isa + SA[i]] = i;
            }
            first = last;
        } while (first < n);
    }

    private PartitionResult trPartition(int isa, int isaD, int isaN, int first, int last, int v) {
        int b;
        int c;
        int[] SA = this.SA;
        int x = 0;
        for (b = first; b < last && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[b])) == v; ++b) {
        }
        int a = b;
        if (a < last && x < v) {
            while (++b < last && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[b])) <= v) {
                if (x != v) continue;
                Bzip2DivSufSort.swapElements((int[])SA, (int)b, (int[])SA, (int)a);
                ++a;
            }
        }
        for (c = last - 1; b < c && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[c])) == v; --c) {
        }
        int d = c;
        if (b < d && x > v) {
            while (b < --c && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[c])) >= v) {
                if (x != v) continue;
                Bzip2DivSufSort.swapElements((int[])SA, (int)c, (int[])SA, (int)d);
                --d;
            }
        }
        while (b < c) {
            Bzip2DivSufSort.swapElements((int[])SA, (int)b, (int[])SA, (int)c);
            while (++b < c && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[b])) <= v) {
                if (x != v) continue;
                Bzip2DivSufSort.swapElements((int[])SA, (int)b, (int[])SA, (int)a);
                ++a;
            }
            while (b < --c && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[c])) >= v) {
                if (x != v) continue;
                Bzip2DivSufSort.swapElements((int[])SA, (int)c, (int[])SA, (int)d);
                --d;
            }
        }
        if (a > d) return new PartitionResult((int)first, (int)last);
        c = b - 1;
        int s = a - first;
        int t = b - a;
        if (s > t) {
            s = t;
        }
        int e = first;
        int f = b - s;
        while (0 < s) {
            Bzip2DivSufSort.swapElements((int[])SA, (int)e, (int[])SA, (int)f);
            --s;
            ++e;
            ++f;
        }
        s = d - c;
        t = last - d - 1;
        if (s > t) {
            s = t;
        }
        e = b;
        f = last - s;
        do {
            if (0 >= s) {
                first += b - a;
                last -= d - c;
                return new PartitionResult((int)first, (int)last);
            }
            Bzip2DivSufSort.swapElements((int[])SA, (int)e, (int[])SA, (int)f);
            --s;
            ++e;
            ++f;
        } while (true);
    }

    private void trCopy(int isa, int isaN, int first, int a, int b, int last, int depth) {
        int c;
        int s;
        int[] SA = this.SA;
        int v = b - 1;
        int d = a - 1;
        for (c = first; c <= d; ++c) {
            s = SA[c] - depth;
            if (s < 0) {
                s += isaN - isa;
            }
            if (SA[isa + s] != v) continue;
            SA[d] = s;
            SA[isa + s] = ++d;
        }
        c = last - 1;
        int e = d + 1;
        d = b;
        while (e < d) {
            s = SA[c] - depth;
            if (s < 0) {
                s += isaN - isa;
            }
            if (SA[isa + s] == v) {
                SA[d] = s;
                SA[isa + s] = --d;
            }
            --c;
        }
    }

    private void trIntroSort(int isa, int isaD, int isaN, int first, int last, TRBudget budget, int size) {
        int s;
        int[] SA = this.SA;
        StackEntry[] stack = new StackEntry[64];
        int x = 0;
        int ssize = 0;
        int limit = Bzip2DivSufSort.trLog((int)(last - first));
        do {
            int b;
            int v;
            int c;
            int next;
            StackEntry entry;
            int d;
            int a;
            block69 : {
                block68 : {
                    block67 : {
                        if (limit < 0) {
                            if (limit == -1) {
                                StackEntry entry2;
                                if (!budget.update((int)size, (int)(last - first))) break;
                                PartitionResult result = this.trPartition((int)isa, (int)(isaD - 1), (int)isaN, (int)first, (int)last, (int)(last - 1));
                                a = result.first;
                                b = result.last;
                                if (first < a || b < last) {
                                    if (a < last) {
                                        v = a - 1;
                                        for (c = first; c < a; ++c) {
                                            SA[isa + SA[c]] = v;
                                        }
                                    }
                                    if (b < last) {
                                        v = b - 1;
                                        for (c = a; c < b; ++c) {
                                            SA[isa + SA[c]] = v;
                                        }
                                    }
                                    stack[ssize++] = new StackEntry((int)0, (int)a, (int)b, (int)0);
                                    stack[ssize++] = new StackEntry((int)(isaD - 1), (int)first, (int)last, (int)-2);
                                    if (a - first <= last - b) {
                                        if (1 < a - first) {
                                            stack[ssize++] = new StackEntry((int)isaD, (int)b, (int)last, (int)Bzip2DivSufSort.trLog((int)(last - b)));
                                            last = a;
                                            limit = Bzip2DivSufSort.trLog((int)(a - first));
                                            continue;
                                        }
                                        if (1 < last - b) {
                                            first = b;
                                            limit = Bzip2DivSufSort.trLog((int)(last - b));
                                            continue;
                                        }
                                        if (ssize == 0) {
                                            return;
                                        }
                                        entry2 = stack[--ssize];
                                        isaD = entry2.a;
                                        first = entry2.b;
                                        last = entry2.c;
                                        limit = entry2.d;
                                        continue;
                                    }
                                    if (1 < last - b) {
                                        stack[ssize++] = new StackEntry((int)isaD, (int)first, (int)a, (int)Bzip2DivSufSort.trLog((int)(a - first)));
                                        first = b;
                                        limit = Bzip2DivSufSort.trLog((int)(last - b));
                                        continue;
                                    }
                                    if (1 < a - first) {
                                        last = a;
                                        limit = Bzip2DivSufSort.trLog((int)(a - first));
                                        continue;
                                    }
                                    if (ssize == 0) {
                                        return;
                                    }
                                    entry2 = stack[--ssize];
                                    isaD = entry2.a;
                                    first = entry2.b;
                                    last = entry2.c;
                                    limit = entry2.d;
                                    continue;
                                }
                                for (c = first; c < last; ++c) {
                                    SA[isa + SA[c]] = c;
                                }
                                if (ssize == 0) {
                                    return;
                                }
                                entry2 = stack[--ssize];
                                isaD = entry2.a;
                                first = entry2.b;
                                last = entry2.c;
                                limit = entry2.d;
                                continue;
                            }
                            if (limit == -2) {
                                a = stack[--ssize].b;
                                b = stack[ssize].c;
                                this.trCopy((int)isa, (int)isaN, (int)first, (int)a, (int)b, (int)last, (int)(isaD - isa));
                                if (ssize == 0) {
                                    return;
                                }
                                entry = stack[--ssize];
                                isaD = entry.a;
                                first = entry.b;
                                last = entry.c;
                                limit = entry.d;
                                continue;
                            }
                            if (0 <= SA[first]) {
                                a = first;
                                do {
                                    SA[isa + SA[a]] = a;
                                } while (++a < last && 0 <= SA[a]);
                                first = a;
                            }
                            if (first < last) {
                                a = first;
                                do {
                                    SA[a] = ~SA[a];
                                } while (SA[++a] < 0);
                                int n = next = SA[isa + SA[a]] != SA[isaD + SA[a]] ? Bzip2DivSufSort.trLog((int)(a - first + 1)) : -1;
                                if (++a < last) {
                                    v = a - 1;
                                    for (b = first; b < a; ++b) {
                                        SA[isa + SA[b]] = v;
                                    }
                                }
                                if (a - first <= last - a) {
                                    stack[ssize++] = new StackEntry((int)isaD, (int)a, (int)last, (int)-3);
                                    ++isaD;
                                    last = a;
                                    limit = next;
                                    continue;
                                }
                                if (1 < last - a) {
                                    stack[ssize++] = new StackEntry((int)(isaD + 1), (int)first, (int)a, (int)next);
                                    first = a;
                                    limit = -3;
                                    continue;
                                }
                                ++isaD;
                                last = a;
                                limit = next;
                                continue;
                            }
                            if (ssize == 0) {
                                return;
                            }
                            entry = stack[--ssize];
                            isaD = entry.a;
                            first = entry.b;
                            last = entry.c;
                            limit = entry.d;
                            continue;
                        }
                        if (last - first <= 8) {
                            if (!budget.update((int)size, (int)(last - first))) break;
                            this.trInsertionSort((int)isa, (int)isaD, (int)isaN, (int)first, (int)last);
                            limit = -3;
                            continue;
                        }
                        if (limit-- != 0) break block67;
                        if (!budget.update((int)size, (int)(last - first))) break;
                        this.trHeapSort((int)isa, (int)isaD, (int)isaN, (int)first, (int)(last - first));
                        a = last - 1;
                        break block68;
                    }
                    a = this.trPivot((int)isa, (int)isaD, (int)isaN, (int)first, (int)last);
                    Bzip2DivSufSort.swapElements((int[])SA, (int)first, (int[])SA, (int)a);
                    v = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[first]);
                    for (b = first + 1; b < last && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[b])) == v; ++b) {
                    }
                    a = b;
                    if (a < last && x < v) {
                        while (++b < last && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[b])) <= v) {
                            if (x != v) continue;
                            Bzip2DivSufSort.swapElements((int[])SA, (int)b, (int[])SA, (int)a);
                            ++a;
                        }
                    }
                    for (c = last - 1; b < c && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[c])) == v; --c) {
                    }
                    d = c;
                    if (b >= d || x <= v) break block69;
                    while (b < --c && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[c])) >= v) {
                        if (x != v) continue;
                        Bzip2DivSufSort.swapElements((int[])SA, (int)c, (int[])SA, (int)d);
                        --d;
                    }
                    break block69;
                }
                while (first < a) {
                    x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[a]);
                    for (b = a - 1; first <= b && this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[b]) == x; --b) {
                        SA[b] = ~SA[b];
                    }
                    a = b;
                }
                limit = -3;
                continue;
            }
            while (b < c) {
                Bzip2DivSufSort.swapElements((int[])SA, (int)b, (int[])SA, (int)c);
                while (++b < c && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[b])) <= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements((int[])SA, (int)b, (int[])SA, (int)a);
                    ++a;
                }
                while (b < --c && (x = this.trGetC((int)isa, (int)isaD, (int)isaN, (int)SA[c])) >= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements((int[])SA, (int)c, (int[])SA, (int)d);
                    --d;
                }
            }
            if (a <= d) {
                c = b - 1;
                s = a - first;
                int t = b - a;
                if (s > t) {
                    s = t;
                }
                int e = first;
                int f = b - s;
                while (0 < s) {
                    Bzip2DivSufSort.swapElements((int[])SA, (int)e, (int[])SA, (int)f);
                    --s;
                    ++e;
                    ++f;
                }
                s = d - c;
                t = last - d - 1;
                if (s > t) {
                    s = t;
                }
                e = b;
                f = last - s;
                while (0 < s) {
                    Bzip2DivSufSort.swapElements((int[])SA, (int)e, (int[])SA, (int)f);
                    --s;
                    ++e;
                    ++f;
                }
                a = first + (b - a);
                b = last - (d - c);
                next = SA[isa + SA[a]] != v ? Bzip2DivSufSort.trLog((int)(b - a)) : -1;
                v = a - 1;
                for (c = first; c < a; ++c) {
                    SA[isa + SA[c]] = v;
                }
                if (b < last) {
                    v = b - 1;
                    for (c = a; c < b; ++c) {
                        SA[isa + SA[c]] = v;
                    }
                }
                if (a - first <= last - b) {
                    if (last - b <= b - a) {
                        if (1 < a - first) {
                            stack[ssize++] = new StackEntry((int)(isaD + 1), (int)a, (int)b, (int)next);
                            stack[ssize++] = new StackEntry((int)isaD, (int)b, (int)last, (int)limit);
                            last = a;
                            continue;
                        }
                        if (1 < last - b) {
                            stack[ssize++] = new StackEntry((int)(isaD + 1), (int)a, (int)b, (int)next);
                            first = b;
                            continue;
                        }
                        if (1 < b - a) {
                            ++isaD;
                            first = a;
                            last = b;
                            limit = next;
                            continue;
                        }
                        if (ssize == 0) {
                            return;
                        }
                        entry = stack[--ssize];
                        isaD = entry.a;
                        first = entry.b;
                        last = entry.c;
                        limit = entry.d;
                        continue;
                    }
                    if (a - first <= b - a) {
                        if (1 < a - first) {
                            stack[ssize++] = new StackEntry((int)isaD, (int)b, (int)last, (int)limit);
                            stack[ssize++] = new StackEntry((int)(isaD + 1), (int)a, (int)b, (int)next);
                            last = a;
                            continue;
                        }
                        if (1 < b - a) {
                            stack[ssize++] = new StackEntry((int)isaD, (int)b, (int)last, (int)limit);
                            ++isaD;
                            first = a;
                            last = b;
                            limit = next;
                            continue;
                        }
                        first = b;
                        continue;
                    }
                    if (1 < b - a) {
                        stack[ssize++] = new StackEntry((int)isaD, (int)b, (int)last, (int)limit);
                        stack[ssize++] = new StackEntry((int)isaD, (int)first, (int)a, (int)limit);
                        ++isaD;
                        first = a;
                        last = b;
                        limit = next;
                        continue;
                    }
                    stack[ssize++] = new StackEntry((int)isaD, (int)b, (int)last, (int)limit);
                    last = a;
                    continue;
                }
                if (a - first <= b - a) {
                    if (1 < last - b) {
                        stack[ssize++] = new StackEntry((int)(isaD + 1), (int)a, (int)b, (int)next);
                        stack[ssize++] = new StackEntry((int)isaD, (int)first, (int)a, (int)limit);
                        first = b;
                        continue;
                    }
                    if (1 < a - first) {
                        stack[ssize++] = new StackEntry((int)(isaD + 1), (int)a, (int)b, (int)next);
                        last = a;
                        continue;
                    }
                    if (1 < b - a) {
                        ++isaD;
                        first = a;
                        last = b;
                        limit = next;
                        continue;
                    }
                    stack[ssize++] = new StackEntry((int)isaD, (int)first, (int)last, (int)limit);
                    continue;
                }
                if (last - b <= b - a) {
                    if (1 < last - b) {
                        stack[ssize++] = new StackEntry((int)isaD, (int)first, (int)a, (int)limit);
                        stack[ssize++] = new StackEntry((int)(isaD + 1), (int)a, (int)b, (int)next);
                        first = b;
                        continue;
                    }
                    if (1 < b - a) {
                        stack[ssize++] = new StackEntry((int)isaD, (int)first, (int)a, (int)limit);
                        ++isaD;
                        first = a;
                        last = b;
                        limit = next;
                        continue;
                    }
                    last = a;
                    continue;
                }
                if (1 < b - a) {
                    stack[ssize++] = new StackEntry((int)isaD, (int)first, (int)a, (int)limit);
                    stack[ssize++] = new StackEntry((int)isaD, (int)b, (int)last, (int)limit);
                    ++isaD;
                    first = a;
                    last = b;
                    limit = next;
                    continue;
                }
                stack[ssize++] = new StackEntry((int)isaD, (int)first, (int)a, (int)limit);
                first = b;
                continue;
            }
            if (!budget.update((int)size, (int)(last - first))) break;
            ++limit;
            ++isaD;
        } while (true);
        s = 0;
        while (s < ssize) {
            if (stack[s].d == -3) {
                this.lsUpdateGroup((int)isa, (int)stack[s].b, (int)stack[s].c);
            }
            ++s;
        }
    }

    private void trSort(int isa, int n, int depth) {
        int[] SA = this.SA;
        int first = 0;
        if (-n >= SA[0]) return;
        TRBudget budget = new TRBudget((int)n, (int)(Bzip2DivSufSort.trLog((int)n) * 2 / 3 + 1));
        do {
            int t;
            if ((t = SA[first]) < 0) {
                first -= t;
                continue;
            }
            int last = SA[isa + t] + 1;
            if (1 < last - first) {
                this.trIntroSort((int)isa, (int)(isa + depth), (int)(isa + n), (int)first, (int)last, (TRBudget)budget, (int)n);
                if (budget.chance == 0) {
                    if (0 < first) {
                        SA[0] = -first;
                    }
                    this.lsSort((int)isa, (int)n, (int)depth);
                    return;
                }
            }
            first = last;
        } while (first < n);
    }

    private static int BUCKET_B(int c0, int c1) {
        return c1 << 8 | c0;
    }

    private static int BUCKET_BSTAR(int c0, int c1) {
        return c0 << 8 | c1;
    }

    private int sortTypeBstar(int[] bucketA, int[] bucketB) {
        int c1;
        int t;
        int i;
        int ti1;
        int c0;
        byte[] T = this.T;
        int[] SA = this.SA;
        int n = this.n;
        int[] tempbuf = new int[256];
        boolean flag = true;
        for (i = 1; i < n; ++i) {
            if (T[i - 1] == T[i]) continue;
            if ((T[i - 1] & 255) <= (T[i] & 255)) break;
            flag = false;
            break;
        }
        i = n - 1;
        int m = n;
        int ti = T[i] & 255;
        int t0 = T[0] & 255;
        if (ti < t0 || T[i] == T[0] && flag) {
            if (!flag) {
                int[] arrn = bucketB;
                int n2 = Bzip2DivSufSort.BUCKET_BSTAR((int)ti, (int)t0);
                arrn[n2] = arrn[n2] + 1;
                SA[--m] = i;
            } else {
                int[] arrn = bucketB;
                int n3 = Bzip2DivSufSort.BUCKET_B((int)ti, (int)t0);
                arrn[n3] = arrn[n3] + 1;
            }
            --i;
            while (0 <= i && (ti = T[i] & 255) <= (ti1 = T[i + 1] & 255)) {
                int[] arrn = bucketB;
                int n4 = Bzip2DivSufSort.BUCKET_B((int)ti, (int)ti1);
                arrn[n4] = arrn[n4] + 1;
                --i;
            }
        }
        while (0 <= i) {
            do {
                int[] arrn = bucketA;
                int n5 = T[i] & 255;
                arrn[n5] = arrn[n5] + 1;
            } while (0 <= --i && (T[i] & 255) >= (T[i + 1] & 255));
            if (0 > i) continue;
            int[] arrn = bucketB;
            int n6 = Bzip2DivSufSort.BUCKET_BSTAR((int)(T[i] & 255), (int)(T[i + 1] & 255));
            arrn[n6] = arrn[n6] + 1;
            SA[--m] = i--;
            while (0 <= i && (ti = T[i] & 255) <= (ti1 = T[i + 1] & 255)) {
                int[] arrn2 = bucketB;
                int n7 = Bzip2DivSufSort.BUCKET_B((int)ti, (int)ti1);
                arrn2[n7] = arrn2[n7] + 1;
                --i;
            }
        }
        if ((m = n - m) == 0) {
            i = 0;
            while (i < n) {
                SA[i] = i;
                ++i;
            }
            return 0;
        }
        i = -1;
        int j = 0;
        for (c0 = 0; c0 < 256; ++c0) {
            t = i + bucketA[c0];
            bucketA[c0] = i + j;
            i = t + bucketB[Bzip2DivSufSort.BUCKET_B((int)c0, (int)c0)];
            for (c1 = c0 + 1; c1 < 256; i += bucketB[Bzip2DivSufSort.BUCKET_B((int)c0, (int)c1)], ++c1) {
                bucketB[c0 << 8 | c1] = j += bucketB[Bzip2DivSufSort.BUCKET_BSTAR((int)c0, (int)c1)];
            }
        }
        int PAb = n - m;
        int ISAb = m;
        i = m - 2;
        while (0 <= i) {
            t = SA[PAb + i];
            c0 = T[t] & 255;
            c1 = T[t + 1] & 255;
            int[] arrn = bucketB;
            int n8 = Bzip2DivSufSort.BUCKET_BSTAR((int)c0, (int)c1);
            int n9 = arrn[n8] - 1;
            arrn[n8] = n9;
            SA[n9] = i--;
        }
        t = SA[PAb + m - 1];
        c0 = T[t] & 255;
        c1 = T[t + 1] & 255;
        int[] arrn = bucketB;
        int n10 = Bzip2DivSufSort.BUCKET_BSTAR((int)c0, (int)c1);
        int n11 = arrn[n10] - 1;
        arrn[n10] = n11;
        SA[n11] = m - 1;
        int[] buf = SA;
        int bufoffset = m;
        int bufsize = n - 2 * m;
        if (bufsize <= 256) {
            buf = tempbuf;
            bufoffset = 0;
            bufsize = 256;
        }
        c0 = 255;
        j = m;
        do {
            if (0 >= j) break;
            for (c1 = 255; c0 < c1; --c1) {
                i = bucketB[Bzip2DivSufSort.BUCKET_BSTAR((int)c0, (int)c1)];
                if (1 < j - i) {
                    this.subStringSort((int)PAb, (int)i, (int)j, (int[])buf, (int)bufoffset, (int)bufsize, (int)2, (boolean)(SA[i] == m - 1), (int)n);
                }
                j = i;
            }
            --c0;
        } while (true);
        for (i = m - 1; 0 <= i; --i) {
            if (0 <= SA[i]) {
                j = i;
                do {
                    SA[ISAb + SA[i]] = i;
                } while (0 <= --i && 0 <= SA[i]);
                SA[i + 1] = i - j;
                if (i <= 0) break;
            }
            j = i;
            do {
                SA[i] = ~SA[i];
                SA[ISAb + SA[i]] = j;
            } while (SA[--i] < 0);
            SA[ISAb + SA[i]] = j;
        }
        this.trSort((int)ISAb, (int)m, (int)1);
        i = n - 1;
        j = m;
        if ((T[i] & 255) < (T[0] & 255) || T[i] == T[0] && flag) {
            if (!flag) {
                SA[SA[ISAb + --j]] = i;
            }
            --i;
            while (0 <= i && (T[i] & 255) <= (T[i + 1] & 255)) {
                --i;
            }
        }
        while (0 <= i) {
            --i;
            while (0 <= i && (T[i] & 255) >= (T[i + 1] & 255)) {
                --i;
            }
            if (0 > i) continue;
            SA[SA[ISAb + --j]] = i--;
            while (0 <= i && (T[i] & 255) <= (T[i + 1] & 255)) {
                --i;
            }
        }
        c0 = 255;
        i = n - 1;
        int k = m - 1;
        while (0 <= c0) {
            for (c1 = 255; c0 < c1; --c1) {
                t = i - bucketB[Bzip2DivSufSort.BUCKET_B((int)c0, (int)c1)];
                bucketB[Bzip2DivSufSort.BUCKET_B((int)c0, (int)c1)] = i + 1;
                i = t;
                j = bucketB[Bzip2DivSufSort.BUCKET_BSTAR((int)c0, (int)c1)];
                while (j <= k) {
                    SA[i] = SA[k];
                    --i;
                    --k;
                }
            }
            t = i - bucketB[Bzip2DivSufSort.BUCKET_B((int)c0, (int)c0)];
            bucketB[Bzip2DivSufSort.BUCKET_B((int)c0, (int)c0)] = i + 1;
            if (c0 < 255) {
                bucketB[Bzip2DivSufSort.BUCKET_BSTAR((int)c0, (int)(c0 + 1))] = t + 1;
            }
            i = bucketA[c0];
            --c0;
        }
        return m;
    }

    private int constructBWT(int[] bucketA, int[] bucketB) {
        int s;
        int c0;
        int s1;
        int i;
        byte[] T = this.T;
        int[] SA = this.SA;
        int n = this.n;
        int t = 0;
        int c2 = 0;
        int orig = -1;
        int c1 = 254;
        do {
            if (0 > c1) break;
            i = bucketB[Bzip2DivSufSort.BUCKET_BSTAR((int)c1, (int)(c1 + 1))];
            t = 0;
            c2 = -1;
            for (int j = bucketA[c1 + 1]; i <= j; --j) {
                s1 = s = SA[j];
                if (0 <= s) {
                    if (--s < 0) {
                        s = n - 1;
                    }
                    if ((c0 = T[s] & 255) > c1) continue;
                    SA[j] = ~s1;
                    if (0 < s && (T[s - 1] & 255) > c0) {
                        s ^= -1;
                    }
                    if (c2 == c0) {
                        SA[--t] = s;
                        continue;
                    }
                    if (0 <= c2) {
                        bucketB[Bzip2DivSufSort.BUCKET_B((int)c2, (int)c1)] = t;
                    }
                    c2 = c0;
                    t = bucketB[Bzip2DivSufSort.BUCKET_B((int)c2, (int)c1)] - 1;
                    SA[t] = s;
                    continue;
                }
                SA[j] = ~s;
            }
            --c1;
        } while (true);
        i = 0;
        while (i < n) {
            s1 = s = SA[i];
            if (0 <= s) {
                if (--s < 0) {
                    s = n - 1;
                }
                if ((c0 = T[s] & 255) >= (T[s + 1] & 255)) {
                    if (0 < s && (T[s - 1] & 255) < c0) {
                        s ^= -1;
                    }
                    if (c0 == c2) {
                        SA[++t] = s;
                    } else {
                        if (c2 != -1) {
                            bucketA[c2] = t;
                        }
                        c2 = c0;
                        t = bucketA[c2] + 1;
                        SA[t] = s;
                    }
                }
            } else {
                s1 ^= -1;
            }
            if (s1 == 0) {
                SA[i] = T[n - 1];
                orig = i;
            } else {
                SA[i] = T[s1 - 1];
            }
            ++i;
        }
        return orig;
    }

    public int bwt() {
        int[] SA = this.SA;
        byte[] T = this.T;
        int n = this.n;
        int[] bucketA = new int[256];
        int[] bucketB = new int[65536];
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            SA[0] = T[0];
            return 0;
        }
        int m = this.sortTypeBstar((int[])bucketA, (int[])bucketB);
        if (0 >= m) return 0;
        return this.constructBWT((int[])bucketA, (int[])bucketB);
    }
}

