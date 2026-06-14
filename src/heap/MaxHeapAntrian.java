package src.heap;

import java.util.*;

import src.model.Pasien;

public class MaxHeapAntrian {
    private List<Pasien> heap;

    public MaxHeapAntrian() {
        heap = new ArrayList<>();
    }

    private boolean lebihUtama(Pasien a, Pasien b) {
        if (a.prioritas != b.prioritas)
            return a.prioritas < b.prioritas;
        return a.waktuMasuk < b.waktuMasuk;
    }

    private void swap(int i, int j) {
        Pasien tmp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, tmp);
    }

    public void insert(Pasien p) {
        heap.add(p);
        int i = heap.size() - 1;
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (lebihUtama(heap.get(i), heap.get(parent))) {
                swap(i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }

    public Pasien extractMax() {
        if (heap.isEmpty())
            return null;
        Pasien top = heap.get(0);
        Pasien last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }
        return top;
    }

    private void heapifyDown(int i) {
        int n = heap.size();
        while (true) {
            int kiri = 2 * i + 1, kanan = 2 * i + 2, terbesar = i;
            if (kiri < n && lebihUtama(heap.get(kiri), heap.get(terbesar)))
                terbesar = kiri;
            if (kanan < n && lebihUtama(heap.get(kanan), heap.get(terbesar)))
                terbesar = kanan;
            if (terbesar == i)
                break;
            swap(i, terbesar);
            i = terbesar;
        }
    }

    public boolean tingkatkanPrioritas(int idPasien) {
        for (int i = 0; i < heap.size(); i++) {
            if (heap.get(i).id == idPasien && heap.get(i).prioritas > 1) {
                heap.get(i).prioritas--;
                int idx = i;
                while (idx > 0) {
                    int parent = (idx - 1) / 2;
                    if (lebihUtama(heap.get(idx), heap.get(parent))) {
                        swap(idx, parent);
                        idx = parent;
                    } else {
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int size() {
        return heap.size();
    }

    public List<Pasien> snapshotUrutan() {
        List<Pasien> hasil = new ArrayList<>(heap);
        hasil.sort((a, b) -> {
            if (lebihUtama(a, b))
                return -1;
            if (lebihUtama(b, a))
                return 1;
            return 0;
        });
        return hasil;
    }
}
