# Penerapan Algoritma Dijkstra dan BFS pada Hospital Patient Routing

<div align="center">

<img src="https://img.shields.io/badge/Struktur_Data-2026-1a1a2e?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/Paradigm-Event--Driven-FF6B35?style=for-the-badge&logo=diagram&logoColor=white"/>
<img src="https://img.shields.io/badge/Algorithm-Dijkstra-007ACC?style=for-the-badge&logo=diagram&logoColor=white"/>
<img src="https://img.shields.io/badge/Algorithm-BFS-4B0082?style=for-the-badge&logo=diagram&logoColor=white"/>
<img src="https://img.shields.io/badge/Data_Structure-Graph-2E8B57?style=for-the-badge&logo=network-wired&logoColor=white"/>
<img src="https://img.shields.io/badge/Data_Structure-Min--Max_Heap-D1A153?style=for-the-badge&logo=gitkraken&logoColor=white"/>

</div>

---

<div align="center">

|          Nama           |     NRP      |
| :---------------------: | :----------: |
| **Adi Satria Pangestu** | `5027231043` |
| **Muhammad Rafi Pramudya Putra** | `5027251024` |
| **Daffa Ulhaq Fadhlurrahman** | `5027251033` |
| **Muhammad Nadhif Pasya Ikhsan** | `5027251084` |
| **Michiko Artika Satriyo** | `5027251105` |

</div>

## Run Code

masuk folder lalu run code dibawah ini run
```powershell
javac src/Main.java src/graph/*.java src/model/*.java src/heap/*.java ; java -cp . src.Main
```
![alt text](image.png)

--- 

# Deskripsi Singkat
> **Description:** Sebuah program untuk simulasi Hospital Patient Routing menggunakan algoritma Bfs dan Dijsktra. Disini kami menggunakna Bfs untuk mencari kandidat node node selanjutnya sesuai kategori yang diinginkan dan untuk algoritma dijkstra kami gunakan unutkm mencari rute tercepat menuju route yang dituju.

---

##  Code Fitur Utama 

####  tambahPasien() :
fungsi untuk membuat pasien berdasarkan input user dan membangun sop yang tepat untuk runtutuan ruangan yang harus dituju pasien

#### prosesNextTahap() : 
fungsi untuk menggerakan pasien ke node selanjutnya. Selanjutnya untuk memperoleh node yang sesuai fungsi tersebut menjalankan bfsCariKategori() dan untuk mencari rute terbaik dijkstra()

#### bfsCariKategori() : 
fungsi untuk mencari node dengan kategori yang sesuai dan membangun jalur menuju node tersebut. lalu menyimpannya di sebuah list kandidat.

#### dijkstra() :
Setelah mendapatkan list kandidat program mencari rute terpendek yang dapat dijangkau menggunakan algoritma dijkstra. Algoritma ini bekerja dengan cara mencoba membandingkan semua rute dan menyimpan hasil rute terbaik didalam sebuah class HasilDijstra yang berisi array distance terbaik dan prev node.

#### refresh() :
Dikarenakan didalam real case pasien dalam sebuah node itu bukan hanya anri namun juga melakukan sebuah proses dengan kata lain harus real time. untuk mengakali hal tersebut kita menggunakna sistem refresh yang dilakukan ketika user melakukan input , sehingga menciptakan data seolah olah realtime. 
```java
    static void refresh(int now) {
        while (!eventQueue.isEmpty() && eventQueue.peek().waktuSelesai <= now) {
            Event ev = eventQueue.poll();

            if (!ev.valid) {
                System.out.println("[SKIP t=" + now + "] Event lama " + ev.pasien.nama
                        + " di ruangan #" + ev.ruanganId + " diabaikan (sudah di reschedule).");
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
```
untuk isi dari fungsi tersebut kita menggunakna list event. list event adalah sebuah list yang digunakan untuk mengetrack semua event didalam node. lalu kita cek apakah ada event yang sudah kadaluarsa atau belum  jika ada event yang kadaluarsa maka gerakan orang tersebut dan buat event baru

#### prosesNextTahap()

sebuah fungsi yang digunakan untuk menggerakan sebuah pasien dengan cara mengeksekusi bfs dan dijstra dan melakukan kalkulasi ulang untuk tujuan node selanjutnya.

#### recalculateSchedule()

sebuah fungsi yang digunakan untk mengkalkulasi ulang antrian. fungsi tersebut digunakan ketika terjadi penyerobotan antrian yang dilakukan oleh pasien darurat. jadi jika ruang tersebut sudah antrian si pasien darurat dapat menyerobot antrian tersebut dan menjadi pengantri yang pertama. 