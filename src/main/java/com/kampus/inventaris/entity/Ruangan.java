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
import java.util.List;

/**
 * Entity Ruangan - Mewakili data ruangan di kampus
 * 
 * Atribut:
 * - kode_ruangan: kode unik untuk setiap ruangan
 * - nama_ruangan: nama ruangan
 * - gedung: nama gedung tempat ruangan berada
 * - lantai: nomor lantai
 * - penanggung_jawab: nama orang yang bertanggung jawab
 * - barang: relasi one-to-many dengan tabel barang
 */
@Entity
@Table(name = "ruangan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ruangan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Kode ruangan tidak boleh kosong")
    @Column(unique = true, length = 50)
    private String kodeRuangan;

    @NotBlank(message = "Nama ruangan tidak boleh kosong")
    @Column(length = 255)
    private String namaRuangan;

    @NotBlank(message = "Gedung tidak boleh kosong")
    @Column(length = 100)
    private String gedung;

    @NotNull(message = "Lantai tidak boleh kosong")
    private Integer lantai;

    @Column(length = 255)
    private String penanggungJawab;

    // Relasi one-to-many: satu ruangan memiliki banyak barang
    @OneToMany(mappedBy = "lokasiRuangan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Barang> barangList;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
