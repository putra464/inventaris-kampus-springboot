package com.kampus.inventaris.controller;

import com.kampus.inventaris.entity.Barang;
import com.kampus.inventaris.entity.KondisiBarang;
import com.kampus.inventaris.entity.Ruangan;
import com.kampus.inventaris.service.BarangService;
import com.kampus.inventaris.service.RuanganService;
import com.kampus.inventaris.util.ExcelExporter;
import com.kampus.inventaris.util.FileUploadUtil;
import com.kampus.inventaris.util.QRCodeGenerator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller untuk Barang
 * 
 * Mengelola semua request HTTP yang berkaitan dengan data barang
 * URL pattern: /barang/*
 */
@Controller
@RequestMapping("/barang")
public class BarangController {

    @Autowired
    private BarangService barangService;

    @Autowired
    private RuanganService ruanganService;

    @Autowired
    private QRCodeGenerator qrCodeGenerator;

    @Autowired
    private ExcelExporter excelExporter;

    /**
     * Halaman list barang dengan pagination, search, dan filter
     * GET /barang/list
     */
    @GetMapping("/list")
    public String listBarang(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String kategori,
            @RequestParam(required = false) String kondisi,
            Model model) {

        Page<Barang> barangPage;

        // Logic untuk filtering dan searching
        if (keyword != null && !keyword.isEmpty()) {
            // Jika ada keyword, lakukan search
            barangPage = barangService.search(keyword, page, size);
        } else if (kategori != null && !kategori.isEmpty() && kondisi != null && !kondisi.isEmpty()) {
            // Jika ada kategori dan kondisi, lakukan filter keduanya
            try {
                KondisiBarang kondisiBarang = KondisiBarang.valueOf(kondisi);
                barangPage = barangService.filterByKategoriAndKondisi(kategori, kondisiBarang, page, size);
            } catch (IllegalArgumentException e) {
                barangPage = barangService.getAll(page, size);
            }
        } else if (kategori != null && !kategori.isEmpty()) {
            // Jika hanya ada kategori, filter berdasarkan kategori
            barangPage = barangService.filterByKategori(kategori, page, size);
        } else if (kondisi != null && !kondisi.isEmpty()) {
            // Jika hanya ada kondisi, filter berdasarkan kondisi
            try {
                KondisiBarang kondisiBarang = KondisiBarang.valueOf(kondisi);
                barangPage = barangService.filterByKondisi(kondisiBarang, page, size);
            } catch (IllegalArgumentException e) {
                barangPage = barangService.getAll(page, size);
            }
        } else {
            // Tidak ada filter, tampilkan semua barang
            barangPage = barangService.getAll(page, size);
        }

        // Tambah data ke model untuk ditampilkan di view
        model.addAttribute("barangPage", barangPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("kategori", kategori);
        model.addAttribute("kondisi", kondisi);
        model.addAttribute("kategoriList", barangService.getKategoriList());
        model.addAttribute("kondisiList", KondisiBarang.values());

        return "barang/list";
    }

    /**
     * Form untuk tambah barang baru
     * GET /barang/form
     */
    @GetMapping("/form")
    public String formTambahBarang(Model model) {
        model.addAttribute("barang", new Barang());
        model.addAttribute("ruanganList", ruanganService.getAll());
        model.addAttribute("kondisiList", KondisiBarang.values());
        model.addAttribute("isEdit", false);
        return "barang/form";
    }

    /**
     * Form untuk edit barang
     * GET /barang/form/{id}
     */
    @GetMapping("/form/{id}")
    public String formEditBarang(@PathVariable Long id, Model model) {
        Optional<Barang> barang = barangService.getById(id);
        if (barang.isPresent()) {
            model.addAttribute("barang", barang.get());
            model.addAttribute("ruanganList", ruanganService.getAll());
            model.addAttribute("kondisiList", KondisiBarang.values());
            model.addAttribute("isEdit", true);
            return "barang/form";
        }
        return "redirect:/barang/list";
    }

    /**
     * Simpan barang baru
     * POST /barang/save
     */
    @PostMapping("/save")
    public String saveBarang(
            @Valid @ModelAttribute Barang barang,
            BindingResult bindingResult,
            @RequestParam(required = false) MultipartFile fotoFile,
            @RequestParam(required = false) Long ruanganId,
            Model model) throws IOException {

        // Validasi input
        if (bindingResult.hasErrors()) {
            model.addAttribute("ruanganList", ruanganService.getAll());
            model.addAttribute("kondisiList", KondisiBarang.values());
            model.addAttribute("isEdit", false);
            return "barang/form";
        }

        // Validasi kode barang tidak boleh duplikat
        if (barang.getId() == null) {
            Optional<Barang> existing = barangService.getByKodeBarang(barang.getKodeBarang());
            if (existing.isPresent()) {
                model.addAttribute("error", "Kode barang sudah digunakan!");
                model.addAttribute("ruanganList", ruanganService.getAll());
                model.addAttribute("kondisiList", KondisiBarang.values());
                model.addAttribute("isEdit", false);
                return "barang/form";
            }
        }

        // Set ruangan
        if (ruanganId != null) {
            Optional<Ruangan> ruangan = ruanganService.getById(ruanganId);
            ruangan.ifPresent(barang::setLokasiRuangan);
        }

        // Upload foto jika ada
        if (fotoFile != null && !fotoFile.isEmpty()) {
            String namaFile = FileUploadUtil.uploadFile(fotoFile);
            barang.setFoto(namaFile);
        }

        // Simpan atau update barang
        if (barang.getId() != null) {
            barangService.update(barang.getId(), barang);
        } else {
            barangService.save(barang);
        }

        return "redirect:/barang/list";
    }

    /**
     * Halaman detail barang
     * GET /barang/{id}
     */
    @GetMapping("/{id}")
    public String detailBarang(@PathVariable Long id, Model model) throws IOException {
        Optional<Barang> barang = barangService.getById(id);
        if (barang.isPresent()) {
            // Generate QR Code
            String qrCode = qrCodeGenerator.generateQRCode(barang.get().getKodeBarang());
            model.addAttribute("barang", barang.get());
            model.addAttribute("qrCode", qrCode);
            return "barang/detail";
        }
        return "redirect:/barang/list";
    }

    /**
     * Hapus barang
     * GET /barang/delete/{id}
     */
    @GetMapping("/delete/{id}")
    public String deleteBarang(@PathVariable Long id) {
        barangService.delete(id);
        return "redirect:/barang/list";
    }

    /**
     * Cetak label QR Code
     * GET /barang/print-qr/{id}
     */
    @GetMapping("/print-qr/{id}")
    public String printQRCode(@PathVariable Long id, Model model) throws IOException {
        Optional<Barang> barang = barangService.getById(id);
        if (barang.isPresent()) {
            String qrCode = qrCodeGenerator.generateQRCode(barang.get().getKodeBarang());
            model.addAttribute("barang", barang.get());
            model.addAttribute("qrCode", qrCode);
            return "barang/print-qr";
        }
        return "redirect:/barang/list";
    }

    /**
     * Export semua barang ke Excel
     * GET /barang/export
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        List<Barang> allBarang = barangService.getAllForStatistics();
        byte[] excelData = excelExporter.exportToExcel(allBarang);

        // Set HTTP headers untuk download file
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Inventaris_Barang_" + System.currentTimeMillis() + ".xlsx");

        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }
}
