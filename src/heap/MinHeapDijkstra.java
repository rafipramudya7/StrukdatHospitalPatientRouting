package src.heap;

import java.util.*;

public class MinHeapDijkstra {
    private List<int[]> heap;

    public MinHeapDijkstra() {
        heap = new ArrayList<>();
    }

    private void swap(int i, int j) {
        int[] tmp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, tmp);
    }

    public void insert(int nodeId, int jarak) {
        heap.add(new int[] { nodeId, jarak });
        int i = heap.size() - 1;
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (heap.get(i)[1] < heap.get(parent)[1]) {
                swap(i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }

    public int[] extractMin() {
        if (heap.isEmpty())
            return null;
        int[] top = heap.get(0);
        int[] last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }
        return top;
    }

    private void heapifyDown(int i) {
        int n = heap.size();
        while (true) {
            int kiri = 2 * i + 1, kanan = 2 * i + 2, terkecil = i;
            if (kiri < n && heap.get(kiri)[1] < heap.get(terkecil)[1])
                terkecil = kiri;
            if (kanan < n && heap.get(kanan)[1] < heap.get(terkecil)[1])
                terkecil = kanan;
            if (terkecil == i)
                break;
            swap(i, terkecil);
            i = terkecil;
        }
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }
}
