package src;

import java.util.*;

import src.graph.Graph;
import src.model.Event;
import src.model.Pasien;
import src.model.Ruangan;

public class Main {

    static long waktuMulaiSimulasi = System.currentTimeMillis();
    static Graph graph = new Graph();
    static int idPasienCounter = 1;
    static Scanner sc = new Scanner(System.in);
    static PriorityQueue<Event> eventQueue = new PriorityQueue<>(
            Comparator.comparingInt(e -> e.waktuSelesai));

    static int waktuSekarang() {
        return (int) ((System.currentTimeMillis() - waktuMulaiSimulasi) / 1000);
    }

    public static void main(String[] args) {
        setupDataset();
        int pilihan;
        do {
            refresh(waktuSekarang());
            System.out.println("\n----------- SISTEM ROUTING PASIEN RS ---------------");
            System.out.println("1. Tambahkan Pasien");
            System.out.println("2. Lihat antrian semua ruangan");
            System.out.println("3. Tampilkan struktur graph");
            System.out.println("0. Keluar");
            System.out.print("Pilih: ");
            pilihan = bacaInt();
            switch (pilihan) {
                case 1:
                    tambahPasien();
                    break;
                case 2:
                    lihatAntrian();
                    break;
                case 3:
                    graph.bfsTampilkanStruktur(0);
                    break;
                case 0:
                    System.out.println("Keluar...");
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);
    }

    static void refresh(int now) {
        while (!eventQueue.isEmpty() && eventQueue.peek().waktuSelesai <= now) {
            Event ev = eventQueue.poll();

            if (!ev.valid) {
                System.out.println("[SKIP t=" + now + "] Event lama " + ev.pasien.nama
                        + " di ruangan #" + ev.ruanganId + " diabaikan (sudah di-reschedule).");
                continue;
            }

            Ruangan r = graph.nodes.get(ev.ruanganId);
            Pasien p = ev.pasien;
            if (r.pasienSedangDilayani != null && r.pasienSedangDilayani.id == p.id) {
                r.pasienSedangDilayani = null;
            }
            keluarkanDariAntrian(r, p);

            System.out.println("\n[EVENT t=" + now + "] " + p.nama
                    + " selesai di " + r.nama + ".");
            p.eventAktif = null;
            p.tahapSaatIni++;
            prosesNextTahap(p, now);
        }
    }

    static void keluarkanDariAntrian(Ruangan r, Pasien target) {
        List<Pasien> sementara = new ArrayList<>();
        while (r.antrian.size() > 0) {
            Pasien x = r.antrian.extractMax();
            if (x.id != target.id)
                sementara.add(x);
        }
        for (Pasien x : sementara)
            r.antrian.insert(x);
    }

    static void prosesNextTahap(Pasien p, int now) {
        if (p.sop == null || p.tahapSaatIni >= p.sop.size()) {
            System.out.println("\n[SELESAI t=" + now + "] " + p.nama
                    + " telah menyelesaikan seluruh layanan. Terima kasih!");
            return;
        }

        String[] tahap = p.sop.get(p.tahapSaatIni);
        String kategori = tahap[0];
        String subKategori = tahap[1];

        System.out.println("\n[t=" + now + "] " + p.nama
                + " → mencari " + kategori
                + (subKategori != null ? " (" + subKategori + ")" : "")
                + " dari posisi node #" + p.currentNode + " ("
                + graph.nodes.get(p.currentNode).nama + ")");
        List<Graph.HasilBFS> kandidat = graph.bfsCariKategori(p.currentNode, kategori, subKategori);
        if (kandidat.isEmpty()) {
            System.out.println("  [!] Tidak ditemukan ruangan " + kategori
                    + (subKategori != null ? "/" + subKategori : "")
                    + " yang terhubung. Proses pasien ini dibatalkan.");
            return;
        }
        Graph.HasilDijkstra hd = graph.dijkstra(p.currentNode);

        System.out.println("  Kandidat ruangan:");
        int terbaikId = -1;
        int terbaikTotal = Integer.MAX_VALUE;

        for (Graph.HasilBFS hb : kandidat) {
            Ruangan r = graph.nodes.get(hb.nodeId);
            int waktuTempuh = hd.dist[hb.nodeId];
            int waktuTiba = now + waktuTempuh;
            int estTunggu = Math.max(0, r.tersediaPada - waktuTiba);
            int total = waktuTempuh + estTunggu;

            System.out.println("    - " + r.nama
                    + " | tempuh=" + waktuTempuh
                    + " | antrian=" + r.antrian.size() + " org"
                    + " | tunggu≈" + estTunggu
                    + " | total=" + total);

            if (total < terbaikTotal) {
                terbaikTotal = total;
                terbaikId = hb.nodeId;
            }
        }

        Ruangan tujuan = graph.nodes.get(terbaikId);
        List<Integer> jalur = graph.jalurDijkstra(hd.prev, p.currentNode, terbaikId);
        int waktuTempuh = hd.dist[terbaikId];
        int waktuTiba = now + waktuTempuh;
        int waktuMulai = Math.max(waktuTiba, tujuan.tersediaPada);
        int waktuSelesai = waktuMulai + tujuan.rataRataLayanan;

        p.waktuMulai = waktuMulai;
        p.waktuSelesai = waktuSelesai;
        p.currentNode = terbaikId;

        tujuan.antrian.insert(p);

        recalculateSchedule(tujuan, now);

        Event ev = new Event(p.waktuSelesai, p, tujuan.id);
        p.eventAktif = ev;
        eventQueue.add(ev);

        System.out.println("  [Dipilih] " + tujuan.nama
                + " | rute: " + jalurToNama(jalur)
                + " | mulai t=" + p.waktuMulai
                + " | selesai t=" + p.waktuSelesai);
    }

    static void recalculateSchedule(Ruangan r, int now) {
        List<Pasien> urutan = r.antrian.snapshotUrutan();

        int waktu;
        if (r.pasienSedangDilayani != null) {
            waktu = r.pasienSedangDilayani.waktuSelesai;
        } else if (!urutan.isEmpty() && urutan.get(0).eventAktif != null) {
            waktu = urutan.get(0).waktuMulai;
        } else {
            waktu = Math.max(now, r.tersediaPada);
        }

        for (Pasien p : urutan) {
            int waktuMulaiBaru = waktu;
            int waktuSelesaiBaru = waktu + r.rataRataLayanan;

            boolean jadwalBerubah = (p.waktuMulai != waktuMulaiBaru)
                    || (p.waktuSelesai != waktuSelesaiBaru);

            if (jadwalBerubah && p.eventAktif != null) {
                p.eventAktif.valid = false;
                System.out.println("    [RESCHEDULE] " + p.nama
                        + " : jadwal lama selesai t=" + p.waktuSelesai
                        + " -> jadwal baru selesai t=" + waktuSelesaiBaru
                        + " (tergeser pasien prioritas lebih tinggi)");
                p.waktuMulai = waktuMulaiBaru;
                p.waktuSelesai = waktuSelesaiBaru;
                Event evBaru = new Event(p.waktuSelesai, p, r.id);
                p.eventAktif = evBaru;
                eventQueue.add(evBaru);

            } else if (jadwalBerubah) {

                p.waktuMulai = waktuMulaiBaru;
                p.waktuSelesai = waktuSelesaiBaru;
            }

            waktu = waktuSelesaiBaru;
        }

        r.tersediaPada = waktu;
    }

    static void tambahPasien() {
        refresh(waktuSekarang());

        System.out.print("\nNama pasien: ");
        String nama = sc.nextLine().trim();
        if (nama.isEmpty()) {
            System.out.println("Nama tidak boleh kosong.");
            return;
        }

        System.out.println("Apakah pasien dalam kondisi darurat?");
        System.out.println("1. Ya (Darurat)");
        System.out.println("2. Tidak");
        System.out.print("Pilih: ");
        int darurat = bacaInt();

        int prioritas = (darurat == 1) ? 1 : 2;
        String subKategoriPoli = null;
        boolean butuhLab = false;

        if (darurat != 1) {
            System.out.println("Apa keperluan pasien?");
            System.out.println("1. Cek kesehatan umum");
            System.out.println("2. Ada keluhan / sakit tertentu");
            System.out.print("Pilih: ");
            int keperluan = bacaInt();

            if (keperluan == 1) {
                subKategoriPoli = "UMUM";
            } else {
                System.out.println("Sakit apa?");
                System.out.println("1. Pencernaan (maag, sakit perut)");
                System.out.println("2. Jantung (sesak, sakit dada)");
                System.out.println("3. Anak (demam pada anak)");
                System.out.println("4. Mata");
                System.out.println("5. Gigi");
                System.out.println("6. THT (telinga, hidung, tenggorokan)");
                System.out.println("7. Saraf (pusing, migrain)");
                System.out.print("Pilih: ");
                int sakit = bacaInt();
                switch (sakit) {
                    case 1:
                        subKategoriPoli = "PENCERNAAN";
                        butuhLab = true;
                        break;
                    case 2:
                        subKategoriPoli = "JANTUNG";
                        butuhLab = true;
                        break;
                    case 3:
                        subKategoriPoli = "ANAK";
                        butuhLab = false;
                        break;
                    case 4:
                        subKategoriPoli = "MATA";
                        butuhLab = false;
                        break;
                    case 5:
                        subKategoriPoli = "GIGI";
                        butuhLab = false;
                        break;
                    case 6:
                        subKategoriPoli = "THT";
                        butuhLab = false;
                        break;
                    case 7:
                        subKategoriPoli = "SARAF";
                        butuhLab = true;
                        break;
                    default:
                        subKategoriPoli = "UMUM";
                        butuhLab = false;
                }
            }
        }

        List<String[]> sop = new ArrayList<>();

        if (darurat == 1) {
            sop.add(new String[] { "IGD", null });

            System.out.println("\nSetelah stabil di IGD, pasien dirujuk ke:");
            System.out.println("1. Jantung");
            System.out.println("2. Saraf");
            System.out.println("3. THT");
            System.out.println("4. Anak");
            System.out.println("5. Mata");
            System.out.print("Pilih: ");
            int rujukan = bacaInt();
            switch (rujukan) {
                case 1:
                    sop.add(new String[] { "POLI", "JANTUNG" });
                    break;
                case 2:
                    sop.add(new String[] { "POLI", "SARAF" });
                    break;
                case 3:
                    sop.add(new String[] { "POLI", "THT" });
                    break;
                case 4:
                    sop.add(new String[] { "POLI", "ANAK" });
                    break;
                case 5:
                    sop.add(new String[] { "POLI", "MATA" });
                    break;
                default:
                    sop.add(new String[] { "POLI", "UMUM" });
            }
            sop.add(new String[] { "APOTEK", null });
        } else {
            sop.add(new String[] { "POLI", subKategoriPoli });
            if (butuhLab)
                sop.add(new String[] { "LAB", null });
            sop.add(new String[] { "APOTEK", null });
        }
        sop.add(new String[] { "KASIR", null });
        System.out.println("\nRencana alur layanan untuk " + nama + ":");
        for (int i = 0; i < sop.size(); i++) {
            String[] s = sop.get(i);
            System.out.println("  " + (i + 1) + ". " + s[0]
                    + (s[1] != null ? " (" + s[1] + ")" : ""));
        }

        int now = waktuSekarang();
        Pasien p = new Pasien(idPasienCounter++, nama, prioritas, now);
        p.sop = sop;
        p.tahapSaatIni = 0;
        p.currentNode = 0;

        System.out.println("\nPasien " + p.nama + " (ID=" + p.id + ") didaftarkan pada t=" + now);

        prosesNextTahap(p, now);
    }

    static void lihatAntrian() {
        int now = waktuSekarang();
        System.out.println("\n===== ANTRIAN SEMUA RUANGAN (t=" + now + ") =====");
        boolean adaIsi = false;
        for (Ruangan r : graph.nodes.values()) {
            boolean adaPasienDilayani = (r.pasienSedangDilayani != null);
            boolean adaAntrian = (r.antrian.size() > 0);
            if (!adaPasienDilayani && !adaAntrian)
                continue;

            adaIsi = true;
            System.out.println("\n" + r.nama + ":");

            if (adaPasienDilayani) {
                Pasien ps = r.pasienSedangDilayani;
                System.out.println("  [DILAYANI] " + ps.nama
                        + " | selesai t=" + ps.waktuSelesai);
            }

            if (adaAntrian) {
                System.out.println("  [MENUNGGU] " + r.antrian.size() + " pasien:");
                for (Pasien p : r.antrian.snapshotUrutan()) {
                    String label = p.prioritas == 1 ? "DARURAT" : "Normal";
                    System.out.println("    - " + p.nama
                            + " (" + label + ")"
                            + " | mulai t=" + p.waktuMulai
                            + " | selesai t=" + p.waktuSelesai);
                }
            }
        }
        if (!adaIsi)
            System.out.println("Belum ada antrian di ruangan manapun.");
    }

    static String jalurToNama(List<Integer> jalur) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jalur.size(); i++) {
            sb.append(graph.nodes.get(jalur.get(i)).nama);
            if (i < jalur.size() - 1)
                sb.append(" → ");
        }
        return sb.toString();
    }

    static int bacaInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Input harus angka, ulangi: ");
            }
        }
    }

    static void setupDataset() {

        graph.tambahRuangan(new Ruangan(0, "Ruang Tunggu Utama", "RUANG_TUNGGU", null, 0, 100, 1));
        graph.tambahRuangan(new Ruangan(1, "IGD 1", "IGD", null, 60, 2, 1));
        graph.tambahRuangan(new Ruangan(2, "IGD 2", "IGD", null, 60, 2, 1));
        graph.tambahRuangan(new Ruangan(3, "IGD 3", "IGD", null, 60, 2, 1));
        graph.tambahRuangan(new Ruangan(4, "Kasir 1", "KASIR", null, 20, 2, 1));
        graph.tambahRuangan(new Ruangan(5, "Kasir 2", "KASIR", null, 20, 2, 1));
        graph.tambahRuangan(new Ruangan(6, "Apotek 1", "APOTEK", null, 25, 2, 1));
        graph.tambahRuangan(new Ruangan(7, "Apotek 2", "APOTEK", null, 25, 2, 1));
        graph.tambahRuangan(new Ruangan(8, "Lab Darah", "LAB", null, 45, 2, 1));
        graph.tambahRuangan(new Ruangan(9, "Lab Radiologi", "LAB", null, 60, 1, 1));
        graph.tambahRuangan(new Ruangan(10, "Lab PA", "LAB", null, 70, 1, 1));

        graph.tambahRuangan(new Ruangan(11, "Poli Umum 1", "POLI", "UMUM", 30, 2, 2));
        graph.tambahRuangan(new Ruangan(12, "Poli Umum 2", "POLI", "UMUM", 30, 2, 2));
        graph.tambahRuangan(new Ruangan(13, "Poli Pencernaan 1", "POLI", "PENCERNAAN", 35, 2, 2));
        graph.tambahRuangan(new Ruangan(14, "Poli Pencernaan 2", "POLI", "PENCERNAAN", 35, 2, 2));
        graph.tambahRuangan(new Ruangan(15, "Poli Anak 1", "POLI", "ANAK", 40, 2, 2));
        graph.tambahRuangan(new Ruangan(16, "Poli Anak 2", "POLI", "ANAK", 40, 2, 2));
        graph.tambahRuangan(new Ruangan(17, "Poli THT 1", "POLI", "THT", 30, 2, 2));
        graph.tambahRuangan(new Ruangan(18, "Poli THT 2", "POLI", "THT", 30, 2, 2));
        graph.tambahRuangan(new Ruangan(19, "Poli Jantung 1", "POLI", "JANTUNG", 40, 2, 3));
        graph.tambahRuangan(new Ruangan(20, "Poli Jantung 2", "POLI", "JANTUNG", 40, 2, 3));
        graph.tambahRuangan(new Ruangan(21, "Poli Mata 1", "POLI", "MATA", 35, 2, 3));
        graph.tambahRuangan(new Ruangan(22, "Poli Mata 2", "POLI", "MATA", 35, 2, 3));
        graph.tambahRuangan(new Ruangan(23, "Poli Gigi 1", "POLI", "GIGI", 40, 2, 3));
        graph.tambahRuangan(new Ruangan(24, "Poli Gigi 2", "POLI", "GIGI", 40, 2, 3));
        graph.tambahRuangan(new Ruangan(25, "Poli Saraf 1", "POLI", "SARAF", 45, 2, 3));
        graph.tambahRuangan(new Ruangan(26, "Poli Saraf 2", "POLI", "SARAF", 45, 2, 3));

        graph.tambahEdge(0, 1, 2);
        graph.tambahEdge(0, 2, 3);
        graph.tambahEdge(0, 3, 4);
        graph.tambahEdge(0, 4, 3);
        graph.tambahEdge(0, 5, 4);
        graph.tambahEdge(0, 6, 3);
        graph.tambahEdge(0, 7, 4);
        graph.tambahEdge(0, 8, 5);
        graph.tambahEdge(0, 9, 6);
        graph.tambahEdge(0, 10, 6);
        graph.tambahEdge(1, 8, 3);
        graph.tambahEdge(2, 8, 3);
        graph.tambahEdge(4, 6, 2);
        graph.tambahEdge(5, 7, 2);
        graph.tambahEdge(6, 8, 4);
        graph.tambahEdge(8, 9, 3);
        graph.tambahEdge(9, 10, 3);
        graph.tambahEdge(3, 9, 4);
        graph.tambahEdge(0, 11, 7);
        graph.tambahEdge(0, 12, 8);
        graph.tambahEdge(0, 13, 8);
        graph.tambahEdge(0, 14, 9);
        graph.tambahEdge(0, 15, 9);
        graph.tambahEdge(0, 16, 10);
        graph.tambahEdge(0, 17, 8);
        graph.tambahEdge(0, 18, 9);
        graph.tambahEdge(0, 19, 10);
        graph.tambahEdge(0, 20, 11);
        graph.tambahEdge(0, 21, 10);
        graph.tambahEdge(0, 22, 11);
        graph.tambahEdge(0, 23, 10);
        graph.tambahEdge(0, 24, 11);
        graph.tambahEdge(0, 25, 12);
        graph.tambahEdge(0, 26, 13);
        graph.tambahEdge(11, 12, 2);
        graph.tambahEdge(13, 14, 2);
        graph.tambahEdge(15, 16, 2);
        graph.tambahEdge(17, 18, 2);
        graph.tambahEdge(11, 13, 3);
        graph.tambahEdge(13, 15, 3);
        graph.tambahEdge(15, 17, 3);
        graph.tambahEdge(19, 20, 2);
        graph.tambahEdge(21, 22, 2);
        graph.tambahEdge(23, 24, 2);
        graph.tambahEdge(25, 26, 2);
        graph.tambahEdge(19, 21, 3);
        graph.tambahEdge(21, 23, 3);
        graph.tambahEdge(23, 25, 3);
    }
}