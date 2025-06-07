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

        
        Path targetDirectory = this.baseUploadLocation.resolve(subDirectory);
        try {
            Files.createDirectories(targetDirectory); 
        } catch (Exception ex) {
            throw new RuntimeException("Tidak bisa membuat sub-direktori upload: " + subDirectory, ex);
        }

        // Ambil nama file asli
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFileName.contains("..")) {
            throw new RuntimeException("Nama file mengandung karakter path yang tidak valid: " + originalFileName);
        }

        
        String fileExtension = "";
        try {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        } catch (Exception e) {
            fileExtension = "";
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Path lengkap ke file tujuan
            Path targetLocation = targetDirectory.resolve(uniqueFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/" + subDirectory + "/" + uniqueFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Gagal menyimpan file " + uniqueFileName + ". Silakan coba lagi!", ex);
        }
    }
}