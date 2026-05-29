package com.kampus.inventaris.service;

import com.kampus.inventaris.dto.RuanganDTO;
import com.kampus.inventaris.entity.Ruangan;
import com.kampus.inventaris.repository.RuanganRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service untuk Ruangan
 * 
 * Menyediakan business logic untuk operasi CRUD ruangan
 * Menggunakan @Transactional untuk mengelola transaksi database
 */
@Service
@Transactional
public class RuanganService {

    @Autowired
    private RuanganRepository ruanganRepository;

    /**
     * Ambil semua ruangan
     */
    public List<Ruangan> getAll() {
        return ruanganRepository.findAll();
    }

    /**
     * Cari ruangan berdasarkan ID
     */
    public Optional<Ruangan> getById(Long id) {
        return ruanganRepository.findById(id);
    }

    /**
     * Cari ruangan berdasarkan kode ruangan
     */
    public Optional<Ruangan> getByKodeRuangan(String kodeRuangan) {
        return ruanganRepository.findByKodeRuangan(kodeRuangan);
    }

    /**
     * Simpan ruangan baru
     */
    public Ruangan save(Ruangan ruangan) {
        return ruanganRepository.save(ruangan);
    }

    /**
     * Update ruangan
     */
    public Ruangan update(Long id, Ruangan ruangan) {
        Optional<Ruangan> existing = ruanganRepository.findById(id);
        if (existing.isPresent()) {
            Ruangan r = existing.get();
            r.setNamaRuangan(ruangan.getNamaRuangan());
            r.setGedung(ruangan.getGedung());
            r.setLantai(ruangan.getLantai());
            r.setPenanggungJawab(ruangan.getPenanggungJawab());
            return ruanganRepository.save(r);
        }
        return null;
    }

    /**
     * Hapus ruangan berdasarkan ID
     */
    public void delete(Long id) {
        ruanganRepository.deleteById(id);
    }

    /**
     * Hitung total ruangan
     */
    public long countTotal() {
        return ruanganRepository.count();
    }

    /**
     * Konversi entity ke DTO
     */
    public RuanganDTO convertToDTO(Ruangan ruangan) {
        return RuanganDTO.builder()
                .id(ruangan.getId())
                .kodeRuangan(ruangan.getKodeRuangan())
                .namaRuangan(ruangan.getNamaRuangan())
                .gedung(ruangan.getGedung())
                .lantai(ruangan.getLantai())
                .penanggungJawab(ruangan.getPenanggungJawab())
                .build();
    }

    /**
     * Konversi DTO ke entity
     */
    public Ruangan convertToEntity(RuanganDTO dto) {
        Ruangan ruangan = new Ruangan();
        ruangan.setKodeRuangan(dto.getKodeRuangan());
        ruangan.setNamaRuangan(dto.getNamaRuangan());
        ruangan.setGedung(dto.getGedung());
        ruangan.setLantai(dto.getLantai());
        ruangan.setPenanggungJawab(dto.getPenanggungJawab());
        return ruangan;
    }
}
