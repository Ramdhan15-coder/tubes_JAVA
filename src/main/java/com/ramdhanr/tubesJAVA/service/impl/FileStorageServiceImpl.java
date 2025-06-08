package com.ramdhanr.tubesJAVA.service.impl;

import com.ramdhanr.tubesJAVA.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path baseUploadLocation;

    public FileStorageServiceImpl() {
        // Tentukan path ke "src/main/resources/static/uploads"
        
        String userDir = System.getProperty("user.dir");
        this.baseUploadLocation = Paths.get(userDir, "src", "main", "resources", "static", "uploads");

        try {
            Files.createDirectories(this.baseUploadLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Tidak bisa membuat direktori upload dasar!", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        if (file.isEmpty()) {
            throw new RuntimeException("Gagal menyimpan file kosong.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new RuntimeException("Gagal menyimpan file dengan nama kosong.");
        }

       
        String cleanedFileName = StringUtils.cleanPath(originalFileName);

        
        if (cleanedFileName.contains("..")) {
            throw new RuntimeException("Nama file mengandung karakter path yang tidak valid: " + cleanedFileName);
        }

       
        Path targetDirectory = this.baseUploadLocation.resolve(subDirectory);
        try {
            Files.createDirectories(targetDirectory);
        } catch (Exception ex) {
            throw new RuntimeException("Tidak bisa membuat sub-direktori upload: " + subDirectory, ex);
        }

        // Buat nama file unik untuk menghindari konflik
        String fileExtension = "";
        try {
            fileExtension = cleanedFileName.substring(cleanedFileName.lastIndexOf("."));
        } catch (Exception e) {
            fileExtension = "";
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = targetDirectory.resolve(uniqueFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // Kembalikan path yang bisa diakses via web
            return "/uploads/" + subDirectory + "/" + uniqueFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Gagal menyimpan file " + uniqueFileName + ". Silakan coba lagi!", ex);
        }
    }
}