package ru.daru_jo.dto;

import org.jspecify.annotations.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class JavaFileToMultipartFile implements MultipartFile {

    private final File file;

    public JavaFileToMultipartFile(File file) {
        this.file = file;
    }

    @Override
    public @NonNull String getName() {
        return file.getName();
    }

    @Override
    public String getOriginalFilename() {
        return file.getName();
    }

    @Override
    public String getContentType() {
        try {
            return Files.probeContentType(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Error while extracting MIME type of file", e);
        }
    }

    @Override
    public boolean isEmpty() {
        return file.length() == 0;
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public byte @NonNull [] getBytes() throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    @Override
    public @NonNull InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public void transferTo(@NonNull File dest) throws IllegalStateException {
        throw new UnsupportedOperationException();
    }
}