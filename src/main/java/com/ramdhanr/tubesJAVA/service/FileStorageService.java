package com.ramdhanr.tubesJAVA.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Menyimpan file yang diupload ke lokasi penyimpanan.
     * @param file File yang diupload.
     * @param subDirectory Nama sub-direktori di dalam folder upload utama (misal "payment_proofs").
     * @return Path relatif yang bisa diakses web ke file yang disimpan (misal "/uploads/payment_proofs/namafileunik.jpg").
     * @throws RuntimeException jika gagal menyimpan file.
     */
    String storeFile(MultipartFile file, String subDirectory);

}