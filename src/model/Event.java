package src.model;

public class Event {
    public int waktuSelesai;
    public Pasien pasien;
    public int ruanganId;
    public boolean valid;

    public Event(int waktuSelesai, Pasien pasien, int ruanganId) {
        this.waktuSelesai = waktuSelesai;
        this.pasien = pasien;
        this.ruanganId = ruanganId;
        this.valid = true;
    }
}