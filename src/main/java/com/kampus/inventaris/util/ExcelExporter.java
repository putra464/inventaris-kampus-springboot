package com.kampus.inventaris.util;

import com.kampus.inventaris.entity.Barang;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility untuk export data ke Excel
 * 
 * Menggunakan library Apache POI untuk membuat file Excel (.xlsx)
 * dengan format profesional dan styling
 */
@Component
public class ExcelExporter {

    private static final String[] HEADERS = {"No", "Kode Barang", "Nama Barang", "Kategori", 
                                             "Merek", "Tahun Beli", "Kondisi", "Ruangan", "Keterangan"};

    /**
     * Export data barang ke format Excel
     * 
     * @param barangList daftar barang yang akan di-export
     * @return byte array dari file Excel
     * @throws IOException jika ada error saat membuat file
     */
    public byte[] exportToExcel(List<Barang> barangList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventaris Barang");

            // Set column width
            sheet.setColumnWidth(0, 3000);  // No
            sheet.setColumnWidth(1, 4000);  // Kode Barang
            sheet.setColumnWidth(2, 5000);  // Nama Barang
            sheet.setColumnWidth(3, 4000);  // Kategori
            sheet.setColumnWidth(4, 4000);  // Merek
            sheet.setColumnWidth(5, 3500);  // Tahun Beli
            sheet.setColumnWidth(6, 3500);  // Kondisi
            sheet.setColumnWidth(7, 4500);  // Ruangan
            sheet.setColumnWidth(8, 5000);  // Keterangan

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);

            // Create center style
            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            centerStyle.setBorderBottom(BorderStyle.THIN);
            centerStyle.setBorderTop(BorderStyle.THIN);
            centerStyle.setBorderRight(BorderStyle.THIN);
            centerStyle.setBorderLeft(BorderStyle.THIN);

            // Create title
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("LAPORAN INVENTARIS BARANG KAMPUS");
            titleCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADERS.length - 1));

            // Create header row
            Row headerRow = sheet.createRow(2);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Add data rows
            int rowNum = 3;
            int no = 1;
            for (Barang barang : barangList) {
                Row row = sheet.createRow(rowNum++);

                // No
                Cell noCell = row.createCell(0);
                noCell.setCellValue(no++);
                noCell.setCellStyle(centerStyle);

                // Kode Barang
                Cell kodeCell = row.createCell(1);
                kodeCell.setCellValue(barang.getKodeBarang());
                kodeCell.setCellStyle(dataStyle);

                // Nama Barang
                Cell namaCell = row.createCell(2);
                namaCell.setCellValue(barang.getNamaBarang());
                namaCell.setCellStyle(dataStyle);

                // Kategori
                Cell kategoriCell = row.createCell(3);
                kategoriCell.setCellValue(barang.getKategori());
                kategoriCell.setCellStyle(dataStyle);

                // Merek
                Cell merekCell = row.createCell(4);
                merekCell.setCellValue(barang.getMerek() != null ? barang.getMerek() : "-");
                merekCell.setCellStyle(dataStyle);

                // Tahun Beli
                Cell tahunCell = row.createCell(5);
                tahunCell.setCellValue(barang.getTahunBeli() != null ? barang.getTahunBeli().toString() : "-");
                tahunCell.setCellStyle(centerStyle);

                // Kondisi
                Cell kondisiCell = row.createCell(6);
                kondisiCell.setCellValue(barang.getKondisi().getLabel());
                kondisiCell.setCellStyle(centerStyle);

                // Ruangan
                Cell ruanganCell = row.createCell(7);
                ruanganCell.setCellValue(barang.getLokasiRuangan() != null ? barang.getLokasiRuangan().getNamaRuangan() : "-");
                ruanganCell.setCellStyle(dataStyle);

                // Keterangan
                Cell keteranganCell = row.createCell(8);
                keteranganCell.setCellValue(barang.getKeterangan() != null ? barang.getKeterangan() : "-");
                keteranganCell.setCellStyle(dataStyle);
            }

            // Add footer with export date
            Row footerRow = sheet.createRow(rowNum + 2);
            Cell footerCell = footerRow.createCell(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            footerCell.setCellValue("Diekspor pada: " + java.time.LocalDateTime.now().format(formatter));

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new IOException("Error exporting to Excel: " + e.getMessage(), e);
        }
    }
}
