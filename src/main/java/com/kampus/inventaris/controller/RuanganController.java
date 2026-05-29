package com.kampus.inventaris.controller;

import com.kampus.inventaris.entity.Ruangan;
import com.kampus.inventaris.service.RuanganService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller untuk Ruangan
 * 
 * Mengelola semua request HTTP yang berkaitan dengan data ruangan
 * URL pattern: /ruangan/*
 */
@Controller
@RequestMapping("/ruangan")
public class RuanganController {

    @Autowired
    private RuanganService ruanganService;

    /**
     * Halaman list semua ruangan
     * GET /ruangan/list
     */
    @GetMapping("/list")
    public String listRuangan(Model model) {
        List<Ruangan> ruanganList = ruanganService.getAll();
        model.addAttribute("ruanganList", ruanganList);
        return "ruangan/list";
    }

    /**
     * Form untuk tambah ruangan baru
     * GET /ruangan/form
     */
    @GetMapping("/form")
    public String formTambahRuangan(Model model) {
        model.addAttribute("ruangan", new Ruangan());
        model.addAttribute("isEdit", false);
        return "ruangan/form";
    }

    /**
     * Form untuk edit ruangan
     * GET /ruangan/form/{id}
     */
    @GetMapping("/form/{id}")
    public String formEditRuangan(@PathVariable Long id, Model model) {
        Optional<Ruangan> ruangan = ruanganService.getById(id);
        if (ruangan.isPresent()) {
            model.addAttribute("ruangan", ruangan.get());
            model.addAttribute("isEdit", true);
            return "ruangan/form";
        }
        return "redirect:/ruangan/list";
    }

    /**
     * Simpan ruangan baru atau update ruangan
     * POST /ruangan/save
     */
    @PostMapping("/save")
    public String saveRuangan(
            @Valid @ModelAttribute Ruangan ruangan,
            BindingResult bindingResult,
            Model model) {

        // Validasi input
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", ruangan.getId() != null);
            return "ruangan/form";
        }

        // Cek kode ruangan tidak duplikat (untuk tambah baru)
        if (ruangan.getId() == null) {
            Optional<Ruangan> existing = ruanganService.getByKodeRuangan(ruangan.getKodeRuangan());
            if (existing.isPresent()) {
                model.addAttribute("error", "Kode ruangan sudah digunakan!");
                model.addAttribute("isEdit", false);
                return "ruangan/form";
            }
        }

        // Simpan atau update
        if (ruangan.getId() != null) {
            ruanganService.update(ruangan.getId(), ruangan);
        } else {
            ruanganService.save(ruangan);
        }

        return "redirect:/ruangan/list";
    }

    /**
     * Hapus ruangan
     * GET /ruangan/delete/{id}
     */
    @GetMapping("/delete/{id}")
    public String deleteRuangan(@PathVariable Long id) {
        ruanganService.delete(id);
        return "redirect:/ruangan/list";
    }

    /**
     * Halaman detail ruangan
     * GET /ruangan/{id}
     */
    @GetMapping("/{id}")
    public String detailRuangan(@PathVariable Long id, Model model) {
        Optional<Ruangan> ruangan = ruanganService.getById(id);
        if (ruangan.isPresent()) {
            model.addAttribute("ruangan", ruangan.get());
            return "ruangan/detail";
        }
        return "redirect:/ruangan/list";
    }
}
