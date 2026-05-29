package com.kampus.inventaris.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Utility untuk upload file
 * 
 * Menangani upload foto barang ke folder /src/main/resources/static/uploads
 */
public class FileUploadUtil {

    // Path untuk folder upload: src/main/resources/static/uploads
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads";

    /**
     * Upload file dari multipart request
     * 
     * @param file file yang akan di-upload
     * @return nama file yang telah di-save
     * @throws IOException jika ada error saat upload
     */
    public static String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File kosong!");
        }

        // Buat folder jika belum ada
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generate nama file unik menggunakan UUID
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String namaFile = UUID.randomUUID().toString() + fileExtension;

        // Save file ke disk
        Path filePath = Paths.get(UPLOAD_DIR, namaFile);
        Files.write(filePath, file.getBytes());

        return namaFile;
    }

    /**
     * Delete file dari folder upload
     * 
     * @param namaFile nama file yang akan dihapus
     * @throws IOException jika ada error saat delete
     */
    public static void deleteFile(String namaFile) throws IOException {
        if (namaFile == null || namaFile.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(UPLOAD_DIR, namaFile);
        Files.deleteIfExists(filePath);
    }

    /**
     * Get file path untuk ditampilkan di browser
     * 
     * @param namaFile nama file
     * @return path relatif untuk browser
     */
    public static String getFilePath(String namaFile) {
        return "/uploads/" + namaFile;
    }
}
