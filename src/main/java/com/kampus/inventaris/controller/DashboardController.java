package com.kampus.inventaris.controller;

import com.kampus.inventaris.entity.KondisiBarang;
import com.kampus.inventaris.service.BarangService;
import com.kampus.inventaris.service.RuanganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller untuk Dashboard
 * 
 * Menampilkan statistik dan chart untuk dashboard
 * URL pattern: /dashboard
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private BarangService barangService;

    @Autowired
    private RuanganService ruanganService;

    /**
     * Halaman dashboard dengan statistik dan chart
     * GET /dashboard
     */
    @GetMapping
    public String dashboard(Model model) {
        // ===== STATISTIK CARD =====
        long totalBarang = barangService.countTotal();
        long barangBaik = barangService.countBarangBaik();
        long barangRusak = barangService.countBarangRusak();
        long totalRuangan = ruanganService.countTotal();

        model.addAttribute("totalBarang", totalBarang);
        model.addAttribute("barangBaik", barangBaik);
        model.addAttribute("barangRusak", barangRusak);
        model.addAttribute("totalRuangan", totalRuangan);

        // ===== DATA UNTUK CHART BATANG (Barang per Ruangan) =====
        List<Object[]> barangPerRuangan = barangService.getCountBarangPerRuangan();
        
        // Konversi ke format yang sesuai untuk Chart.js
        StringBuilder ruanganLabels = new StringBuilder("[");
        StringBuilder ruanganData = new StringBuilder("[");
        
        for (int i = 0; i < barangPerRuangan.size(); i++) {
            Object[] row = barangPerRuangan.get(i);
            String namaRuangan = (String) row[0];
            Long count = (Long) row[1];
            
            ruanganLabels.append("'").append(namaRuangan).append("'");
            ruanganData.append(count);
            
            if (i < barangPerRuangan.size() - 1) {
                ruanganLabels.append(",");
                ruanganData.append(",");
            }
        }
        
        ruanganLabels.append("]");
        ruanganData.append("]");

        model.addAttribute("ruanganLabels", ruanganLabels.toString());
        model.addAttribute("ruanganData", ruanganData.toString());

        // ===== DATA UNTUK CHART PIE (Persentase Kondisi Barang) =====
        long maintenance = barangService.countBarangMaintenance();
        
        // Buat map untuk chart pie
        Map<String, Long> kondisiMap = new HashMap<>();
        kondisiMap.put("Baik", barangBaik);
        kondisiMap.put("Rusak", barangRusak);
        kondisiMap.put("Maintenance", maintenance);

        // Konversi ke format Chart.js
        StringBuilder kondisiLabels = new StringBuilder("[");
        StringBuilder kondisiData = new StringBuilder("[");
        
        int i = 0;
        for (Map.Entry<String, Long> entry : kondisiMap.entrySet()) {
            kondisiLabels.append("'").append(entry.getKey()).append("'");
            kondisiData.append(entry.getValue());
            
            if (i < kondisiMap.size() - 1) {
                kondisiLabels.append(",");
                kondisiData.append(",");
            }
            i++;
        }
        
        kondisiLabels.append("]");
        kondisiData.append("]");

        model.addAttribute("kondisiLabels", kondisiLabels.toString());
        model.addAttribute("kondisiData", kondisiData.toString());

        return "dashboard";
    }
}
