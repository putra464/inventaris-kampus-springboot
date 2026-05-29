package com.kampus.inventaris.entity;

/**
 * Enum untuk status kondisi barang
 * 
 * Nilai:
 * - BAIK: barang dalam kondisi baik
 * - RUSAK: barang rusak
 * - MAINTENANCE: barang sedang dalam perbaikan
 */
public enum KondisiBarang {
    BAIK("Baik"),
    RUSAK("Rusak"),
    MAINTENANCE("Maintenance");

    private final String label;

    KondisiBarang(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
