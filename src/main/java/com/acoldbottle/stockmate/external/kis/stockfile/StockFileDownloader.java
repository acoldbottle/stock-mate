package com.acoldbottle.stockmate.external.kis.stockfile;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.kis.KisFileDownloadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
public class StockFileDownloader {

    @Value("${kis.download.nasdaq}")
    private String nasdaqUrl;
    @Value("${kis.download.nyse}")
    private String nyseUrl;
    @Value("${kis.download.amex}")
    private String amexUrl;

    @Value("${kis.download.dir}")
    private String downloadDir;

    public void downloadAll() {
        deleteOldStockFile();

        try {
            downloadAndExtract(nasdaqUrl);
            downloadAndExtract(nyseUrl);
            downloadAndExtract(amexUrl);
        } catch (IOException e) {
            throw new KisFileDownloadException(ErrorCode.KIS_FILE_DOWNLOAD_ERROR);
        }
    }

    private void deleteOldStockFile() {
        File dir = new File(downloadDir);
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("다운로드 디렉토리가 존재하지 않거나 디렉토리가 아닙니다: {}", downloadDir);
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            log.warn("다운로드 디렉토리 접근 불가: {}", downloadDir);
            return;
        }

        log.debug("===== 기존 주식 종목 정보 파일 삭제 시작 =====");

        for (File f : files) {
            if (f.isFile()) {
                if (f.delete()) {
                    log.debug("삭제 성공: {}", f.getName());
                } else {
                    log.warn("삭제 실패: {}", f.getName());
                }
            }
        }

        log.debug("===== 기존 주식 종목 정보 파일 삭제 완료 =====");
    }

    private void downloadAndExtract(String url) throws IOException {
        String fileName = Paths.get(new URL(url).getPath()).getFileName().toString();
        Path downloadPath = Paths.get(downloadDir, fileName);

        log.debug("다운로드 시작: {}", url);
        downloadFile(url, downloadPath);
        log.debug("다운로드 완료: {}", url);

        unzipFile(downloadPath, Paths.get(downloadDir));
        Files.deleteIfExists(downloadPath);
        log.debug("압축파일 삭제 완료: {}", downloadPath);
    }

    private void downloadFile(String fileUrl, Path targetPath) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(10_000);

        try (InputStream in = connection.getInputStream();
             OutputStream out = Files.newOutputStream(targetPath, StandardOpenOption.CREATE)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            connection.disconnect();
        }
    }

    private void unzipFile(Path zipFilePath, Path destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                Path newFilePath = destDir.resolve(entry.getName()).normalize();

                if (!newFilePath.startsWith(destDir)) {
                    throw new IOException("압축 해제 경로가 유효하지 않습니다: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(newFilePath);
                } else {
                    Files.createDirectories(newFilePath.getParent());
                    try (OutputStream out = Files.newOutputStream(newFilePath)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}
