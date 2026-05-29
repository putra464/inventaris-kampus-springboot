package com.kampus.inventaris.service;

import com.kampus.inventaris.dto.BarangDTO;
import com.kampus.inventaris.entity.Barang;
import com.kampus.inventaris.entity.KondisiBarang;
import com.kampus.inventaris.repository.BarangRepository;
import com.kampus.inventaris.repository.RuanganRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service untuk Barang
 * 
 * Menyediakan business logic untuk operasi CRUD barang dan query khusus
 * Menggunakan @Transactional untuk mengelola transaksi database
 */
@Service
@Transactional
public class BarangService {

    @Autowired
    private BarangRepository barangRepository;

    @Autowired
    private RuanganRepository ruanganRepository;

    /**
     * Ambil semua barang dengan pagination
     */
    public Page<Barang> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("namaBarang").ascending());
        return barangRepository.findAll(pageable);
    }

    /**
     * Cari barang berdasarkan ID
     */
    public Optional<Barang> getById(Long id) {
        return barangRepository.findById(id);
    }

    /**
     * Cari barang berdasarkan kode barang
     */
    public Optional<Barang> getByKodeBarang(String kodeBarang) {
        return barangRepository.findByKodeBarang(kodeBarang);
    }

    /**
     * Search barang berdasarkan keyword (nama atau kode) dengan pagination
     */
    public Page<Barang> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("namaBarang").ascending());
        return barangRepository.searchByNamaOrKode(keyword, pageable);
    }

    /**
     * Filter barang berdasarkan kategori dengan pagination
     */
    public Page<Barang> filterByKategori(String kategori, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("namaBarang").ascending());
        return barangRepository.findByKategori(kategori, pageable);
    }

    /**
     * Filter barang berdasarkan kondisi dengan pagination
     */
    public Page<Barang> filterByKondisi(KondisiBarang kondisi, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("namaBarang").ascending());
        return barangRepository.findByKondisi(kondisi, pageable);
    }

    /**
     * Filter barang berdasarkan kategori dan kondisi dengan pagination
     */
    public Page<Barang> filterByKategoriAndKondisi(String kategori, KondisiBarang kondisi, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("namaBarang").ascending());
        return barangRepository.findByKategoriAndKondisi(kategori, kondisi, pageable);
    }

    /**
     * Simpan barang baru
     */
    public Barang save(Barang barang) {
        return barangRepository.save(barang);
    }

    /**
     * Update barang
     */
    public Barang update(Long id, Barang barang) {
        Optional<Barang> existing = barangRepository.findById(id);
        if (existing.isPresent()) {
            Barang b = existing.get();
            b.setNamaBarang(barang.getNamaBarang());
            b.setKategori(barang.getKategori());
            b.setMerek(barang.getMerek());
            b.setTahunBeli(barang.getTahunBeli());
            b.setKondisi(barang.getKondisi());
            b.setLokasiRuangan(barang.getLokasiRuangan());
            if (barang.getFoto() != null && !barang.getFoto().isEmpty()) {
                b.setFoto(barang.getFoto());
            }
            b.setKeterangan(barang.getKeterangan());
            return barangRepository.save(b);
        }
        return null;
    }

    /**
     * Hapus barang berdasarkan ID
     */
    public void delete(Long id) {
        barangRepository.deleteById(id);
    }

    /**
     * Hitung total barang
     */
    public long countTotal() {
        return barangRepository.count();
    }

    /**
     * Hitung barang dalam kondisi BAIK
     */
    public long countBarangBaik() {
        return barangRepository.countByKondisi(KondisiBarang.BAIK);
    }

    /**
     * Hitung barang dalam kondisi RUSAK
     */
    public long countBarangRusak() {
        return barangRepository.countByKondisi(KondisiBarang.RUSAK);
    }

    /**
     * Hitung barang dalam kondisi MAINTENANCE
     */
    public long countBarangMaintenance() {
        return barangRepository.countByKondisi(KondisiBarang.MAINTENANCE);
    }

    /**
     * Ambil semua barang untuk statistik
     */
    public List<Barang> getAllForStatistics() {
        return barangRepository.findAllForStatistics();
    }

    /**
     * Hitung barang per ruangan untuk chart
     */
    public List<Object[]> getCountBarangPerRuangan() {
        return barangRepository.countBarangPerRuangan();
    }

    /**
     * Ambil daftar kategori unik
     */
    public List<String> getKategoriList() {
        List<Barang> allBarang = barangRepository.findAll();
        return allBarang.stream()
                .map(Barang::getKategori)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Konversi entity ke DTO
     */
    public BarangDTO convertToDTO(Barang barang) {
        return BarangDTO.builder()
                .id(barang.getId())
                .kodeBarang(barang.getKodeBarang())
                .namaBarang(barang.getNamaBarang())
                .kategori(barang.getKategori())
                .merek(barang.getMerek())
                .tahunBeli(barang.getTahunBeli())
                .kondisi(barang.getKondisi())
                .ruanganId(barang.getLokasiRuangan() != null ? barang.getLokasiRuangan().getId() : null)
                .namaRuangan(barang.getLokasiRuangan() != null ? barang.getLokasiRuangan().getNamaRuangan() : null)
                .foto(barang.getFoto())
                .keterangan(barang.getKeterangan())
                .build();
    }

    /**
     * Konversi DTO ke entity
     */
    public Barang convertToEntity(BarangDTO dto) {
        Barang barang = new Barang();
        barang.setKodeBarang(dto.getKodeBarang());
        barang.setNamaBarang(dto.getNamaBarang());
        barang.setKategori(dto.getKategori());
        barang.setMerek(dto.getMerek());
        barang.setTahunBeli(dto.getTahunBeli());
        barang.setKondisi(dto.getKondisi());
        if (dto.getRuanganId() != null) {
            barang.setLokasiRuangan(ruanganRepository.findById(dto.getRuanganId()).orElse(null));
        }
        barang.setFoto(dto.getFoto());
        barang.setKeterangan(dto.getKeterangan());
        return barang;
    }
}
