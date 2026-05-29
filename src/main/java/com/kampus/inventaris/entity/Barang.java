package com.kampus.inventaris.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity Barang - Mewakili data barang inventaris di kampus
 * 
 * Atribut:
 * - kodeBarang: kode unik untuk setiap barang
 * - namaBarang: nama barang
 * - kategori: kategori/jenis barang
 * - merek: merek/merk barang
 * - tahunBeli: tahun barang dibeli
 * - kondisi: status kondisi barang (BAIK, RUSAK, MAINTENANCE)
 * - lokasiRuangan: relasi many-to-one dengan tabel ruangan
 * - foto: path file foto barang
 * - keterangan: deskripsi atau catatan tambahan
 */
@Entity
@Table(name = "barang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Barang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Kode barang tidak boleh kosong")
    @Column(unique = true, length = 50)
    private String kodeBarang;

    @NotBlank(message = "Nama barang tidak boleh kosong")
    @Column(length = 255)
    private String namaBarang;

    @NotBlank(message = "Kategori tidak boleh kosong")
    @Column(length = 100)
    private String kategori;

    @Column(length = 100)
    private String merek;

    private Integer tahunBeli;

    @NotNull(message = "Kondisi tidak boleh kosong")
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private KondisiBarang kondisi;

    // Relasi many-to-one: banyak barang dalam satu ruangan
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lokasi_ruangan_id")
    private Ruangan lokasiRuangan;

    @Column(length = 500)
    private String foto;

    @Column(columnDefinition = "TEXT")
    private String keterangan;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
