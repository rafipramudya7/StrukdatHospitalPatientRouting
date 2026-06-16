package src;

import src.heap.MaxHeapAntrian;
import src.model.Pasien;
import java.util.List;

public class TestMaxHeap {
    public static void main(String[] args) {
        MaxHeapAntrian antrianRS = new MaxHeapAntrian();

        System.out.println("      TRACING MAXHEAP DENGAN MEMASUKAN PASIEN       ");

        System.out.println("\n[Kasus 1] Memasukkan 3 Pasien Normal");
        antrianRS.insert(new Pasien(101, "Pasien_A_Normal", 2, 5));
        antrianRS.insert(new Pasien(102, "Pasien_B_Normal", 2, 8));
        antrianRS.insert(new Pasien(103, "Pasien_C_Normal", 2, 10));

        System.out.println("\n[Kasus 2] Datang Pasien Baru Kategori Darurat");
        antrianRS.insert(new Pasien(104, "Pasien_D_DARURAT", 1, 12));

        while (antrianRS.size() > 0) {
            antrianRS.extractMax();
        }
    }

}