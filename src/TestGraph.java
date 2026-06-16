package src;

import src.graph.Graph;
import src.graph.Graph.HasilBFS;
import src.graph.Graph.HasilDijkstra;
import src.model.Ruangan;
import java.util.List;

public class TestGraph {
    public static void main(String[] args) {
        Graph rsMap = new Graph();

        rsMap.tambahRuangan(new Ruangan(0, "Ruang Tunggu", "UTAMA", null, 0, 99, 1));
        rsMap.tambahRuangan(new Ruangan(1, "IGD 1", "IGD", null, 5, 2, 1));
        rsMap.tambahRuangan(new Ruangan(2, "Poli Umum", "POLI", "UMUM", 10, 3, 1));
        rsMap.tambahRuangan(new Ruangan(3, "Lab Darah", "LAB", "DARAH", 7, 1, 2));

        rsMap.tambahEdge(0, 1, 2);  
        rsMap.tambahEdge(0, 2, 8);  
        rsMap.tambahEdge(1, 3, 3);  
        rsMap.tambahEdge(2, 3, 1);  

        System.out.println("            SIMULASI TRACING MANUAL PROSES GRAPH (BFS & DIJKSTRA)    ");

        List<HasilBFS> kandidatLab = rsMap.bfsCariKategori(0, "LAB", null);
        HasilDijkstra ruteTercepat = rsMap.dijkstra(0);


        List<Integer> jalurKeLab = rsMap.jalurDijkstra(ruteTercepat.prev, 0, 3);
        
        System.out.print("Rute terbaik ke Lab Darah: ");
        for(int i = 0; i < jalurKeLab.size(); i++) {
            System.out.print(rsMap.nodes.get(jalurKeLab.get(i)).nama + (i < jalurKeLab.size() - 1 ? " -> " : ""));
        }
        System.out.println("\nTotal Waktu Tempuh Tercepat: " + ruteTercepat.dist[3] + " Detik");
    }
}