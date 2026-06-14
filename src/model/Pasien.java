package src.model;

import java.util.List;

public class Pasien {
    public int id;
    public String nama;
    public int prioritas;
    public int waktuMasuk;
    public int waktuMulai;
    public int waktuSelesai;
    public int currentNode;
    public int tahapSaatIni;
    public List<String[]> sop;
    public Event eventAktif;

    public Pasien(int id, String nama, int prioritas, int waktuMasuk) {
        this.id = id;
        this.nama = nama;
        this.prioritas = prioritas;
        this.waktuMasuk = waktuMasuk;
    }

    public String toString() {
        String label = prioritas == 1 ? "DARURAT" : "Biasa";
        return nama + " (" + label + ", t_masuk=" + waktuMasuk + ")";
    }
}
