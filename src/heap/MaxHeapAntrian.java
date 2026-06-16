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
            return a.prioritas < b.prioritas; // Angka prioritas lebih kecil = lebih utama (e.g. 1 < 2)
        return a.waktuMasuk < b.waktuMasuk;   // Waktu masuk lebih awal = lebih utama
    }

    private void swap(int i, int j) {
        Pasien tmp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, tmp);
    }

    public void insert(Pasien p) {
        heap.add(p);
        int i = heap.size() - 1;
        
        System.out.println("  [INSERT] " + p.nama + " (Prio: " + p.prioritas + ", Masuk: " + p.waktuMasuk + ") ditempatkan di indeks [" + i + "]");
        
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (lebihUtama(heap.get(i), heap.get(parent))) {
                System.out.println("    --> HEAPIFY-UP: " + heap.get(i).nama + " [indeks " + i + "] LEBIH UTAMA dari " + 
                                   heap.get(parent).nama + " [indeks " + parent + "]. Tukar posisi!");
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
        
        System.out.println("\n  [EXTRACT] Melayani pasien teratas: " + top.nama);
        
        if (!heap.isEmpty()) {
            System.out.println("  [REPLACE] Memindahkan elemen terakhir (" + last.nama + ") sementara ke indeks [0]");
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
                
            if (terbesar == i) {
                System.out.println("    [HEAPIFY-DOWN] selesai. Posisi " + heap.get(i).nama + " sudah pas di indeks [" + i + "].");
                break;
            }
            System.out.println("    [HEAPIFY-DOWN] " + heap.get(terbesar).nama + " di indeks [" + terbesar + "] menyalip " + 
                               heap.get(i).nama + " di indeks [" + i + "]. Tukar posisi");
            swap(i, terbesar);
            i = terbesar;
        }
    }

    public int size() {
        return heap.size();
    }

    public List<Pasien> snapshotUrutan() {
        List<Pasien> hasil = new ArrayList<>(heap);
        hasil.sort((a, b) -> {
            if (lebihUtama(a, b)) return -1;
            if (lebihUtama(b, a)) return 1;
            return 0;
        });
        return hasil;
    }
}