package src.model;

import src.heap.MaxHeapAntrian;

public class Ruangan {
    public int id;
    public String nama;
    public String kategori;
    public String subKategori;
    public int rataRataLayanan;
    public int kapasitas;
    public int lantai;
    public int tersediaPada;
    public Pasien pasienSedangDilayani;
    public MaxHeapAntrian antrian;

    public Ruangan(int id, String nama, String kategori, String subKategori,
            int rataRataLayanan, int kapasitas, int lantai) {
        this.tersediaPada = 0;
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
        this.subKategori = subKategori;
        this.rataRataLayanan = rataRataLayanan;
        this.kapasitas = kapasitas;
        this.lantai = lantai;
        this.antrian = new MaxHeapAntrian();
    }
}
