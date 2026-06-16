package src;

import src.graph.Graph;
import src.graph.Graph.HasilDijkstra;
import src.model.Ruangan;
import java.util.List;

public class TestDisconnectedGraph {
    public static void main(String[] args) {
        Graph rsMap = new Graph();

        rsMap.tambahRuangan(new Ruangan(0, "Ruang Tunggu Utama", "UTAMA", null, 0, 99, 1));
        rsMap.tambahRuangan(new Ruangan(1, "IGD 1", "IGD", null, 5, 2, 1));
        rsMap.tambahEdge(0, 1, 2); 
        rsMap.tambahRuangan(new Ruangan(2, "Poli VIP Lantai 3", "POLI", "VIP", 15, 1, 3));


        HasilDijkstra rute = rsMap.dijkstra(0);
        int jarakKePoliVIP = rute.dist[2];
        System.out.println("Nilai  dist[2] di dalam Array Jarak: " + jarakKePoliVIP);

        try {
            List<Integer> jalur = rsMap.jalurDijkstra(rute.prev, 0, 2);
            System.out.print("Hasil cetak jalur: ");
            for (int id : jalur) {
                System.out.print(rsMap.nodes.get(id).nama + " -> ");
            }
            System.out.println("Selesai.");
        } catch (Exception e) {
            System.out.println("[CRASH DETECTED] Program error: " + e.getMessage());
        }
    }
}