package src.graph;

import java.util.*;
import src.heap.MinHeapDijkstra;
import src.model.Ruangan;

public class Graph {
    public Map<Integer, Ruangan> nodes;
    Map<Integer, List<int[]>> adj;

    public Graph() {
        nodes = new HashMap<>();
        adj = new HashMap<>();
    }

    public void tambahRuangan(Ruangan r) {
        nodes.put(r.id, r);
        adj.put(r.id, new ArrayList<>());
    }

    public void tambahEdge(int a, int b, int waktu) {
        adj.get(a).add(new int[] { b, waktu });
        adj.get(b).add(new int[] { a, waktu });
    }

    public static class HasilBFS {
        public int nodeId, hop;
        public List<Integer> jalur;

        HasilBFS(int nodeId, int hop, List<Integer> jalur) {
            this.nodeId = nodeId;
            this.hop = hop;
            this.jalur = jalur;
        }
    }

    public List<HasilBFS> bfsCariKategori(int start, String kategori, String subKategori) {
        List<HasilBFS> hasil = new ArrayList<>();
        Map<Integer, Integer> hop = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        System.out.println("\n--- START BFS TRACING (Mencari Kategori: " + kategori + ") ---");
        visited.add(start);
        hop.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            int cur = queue.poll();
            Ruangan r = nodes.get(cur);
            System.out.println("  [BFS POLL] Mengecek Node #" + cur + " (" + r.nama + ") | Queue Sisa: " + queue);

            if (cur != start && r.kategori.equals(kategori)
                    && (subKategori == null || subKategori.equals(r.subKategori))) {
                System.out.println("    >> [FOUND] Cocok! Menemukan ruangan target: " + r.nama);
                hasil.add(new HasilBFS(cur, hop.get(cur), bangunJalur(parent, start, cur)));
            }

            for (int[] tetangga : adj.get(cur)) {
                int next = tetangga[0];
                if (!visited.contains(next)) {
                    visited.add(next);
                    hop.put(next, hop.get(cur) + 1);
                    parent.put(next, cur);
                    queue.add(next);
                    System.out.println("    -> Tetangga #" + next + " (" + nodes.get(next).nama + ") belum dikunjungi. Masuk Queue.");
                }
            }
        }
        return hasil;
    }

    private List<Integer> bangunJalur(Map<Integer, Integer> parent, int start, int tujuan) {
        List<Integer> jalur = new ArrayList<>();
        int cur = tujuan;
        while (cur != start) {
            jalur.add(cur);
            cur = parent.get(cur);
        }
        jalur.add(start);
        Collections.reverse(jalur);
        return jalur;
    }

    public void bfsTampilkanStruktur(int start) {
        Map<Integer, Integer> hop = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        visited.add(start);
        hop.put(start, 0);
        queue.add(start);
        int levelTerakhir = -1;

        while (!queue.isEmpty()) {
            int cur = queue.poll();
            int level = hop.get(cur);
            if (level != levelTerakhir) {
                levelTerakhir = level;
                System.out.println("\nLevel " + level + ":");
            }
            Ruangan r = nodes.get(cur);
            String sub = r.subKategori != null ? "/" + r.subKategori : "";
            System.out.println("  - " + r.nama + " [" + r.kategori + sub + ", lantai " + r.lantai + "]");

            for (int[] tetangga : adj.get(cur)) {
                int next = tetangga[0];
                if (!visited.contains(next)) {
                    visited.add(next);
                    hop.put(next, level + 1);
                    queue.add(next);
                }
            }
        }
    }

    public static class HasilDijkstra {
        public int[] dist;
        public int[] prev;

        HasilDijkstra(int[] dist, int[] prev) {
            this.dist = dist;
            this.prev = prev;
        }
    }

    public HasilDijkstra dijkstra(int start) {
        int n = nodes.size();
        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[start] = 0;

        System.out.println("\n--- START DIJKSTRA TRACING (Mencari Rute Tercepat dari " + nodes.get(start).nama + ") ---");
        MinHeapDijkstra pq = new MinHeapDijkstra();
        pq.insert(start, 0);

        while (!pq.isEmpty()) {
            int[] top = pq.extractMin();
            int u = top[0], d = top[1];
            System.out.println("  [DIJKSTRA POP] Node #" + u + " (" + nodes.get(u).nama + ") tereksekusi dengan jarak sementara = " + d);

            if (d > dist[u])
                continue;

            for (int[] tetangga : adj.get(u)) {
                int v = tetangga[0], w = tetangga[1];
                System.out.println("    -> Cek jalur ke tetangga #" + v + " (" + nodes.get(v).nama + ") lewat #" + u + " | Bobot lorong = " + w);
                if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                    int jarakLama = dist[v];
                    dist[v] = dist[u] + w;
                    prev[v] = u;
                    System.out.println("       [RELAXATION] Jarak ke " + nodes.get(v).nama + " BERHASIL DIPERBAIKI! " + (jarakLama == Integer.MAX_VALUE ? "∞" : jarakLama) + " -> " + dist[v]);
                    pq.insert(v, dist[v]);
                } else {
                    System.out.println("       [SKIP] Jalur lewat #" + u + " (" + (dist[u] + w) + ") tidak lebih cepat dari data saat ini (" + (dist[v] == Integer.MAX_VALUE ? "∞" : dist[v]) + ")");
                }
            }
        }
        return new HasilDijkstra(dist, prev);
    }

    public List<Integer> jalurDijkstra(int[] prev, int start, int tujuan) {
        List<Integer> jalur = new ArrayList<>();
        int cur = tujuan;
        while (cur != -1) {
            jalur.add(cur);
            if (cur == start)
                break;
            cur = prev[cur];
        }
        Collections.reverse(jalur);
        return jalur;
    }
}