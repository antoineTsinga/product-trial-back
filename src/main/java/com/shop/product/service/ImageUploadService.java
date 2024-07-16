package com.shop.product.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shop.product.exception.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class ImageUploadService {

    @Autowired(required = false)
    private Cloudinary cloudinary;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${server.port}")
    private String serverPort;

    @Value("${cloudinary.name}")
    private String cloud;

    public String uploadImage(MultipartFile file) throws IOException {

        if (!cloud.equals("local")) {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("url");
        } else {
            return uploadImageLocal(file);
        }
    }

    public void deleteImageByUrl(String imageUrl) {
        if (!cloud.equals("local")) {
                String publicId = extractPublicIdFromUrl(imageUrl);
                if(publicId == null) return;
                try {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        } else {
            String publicId = extractIdFromUrl(imageUrl);
            if(publicId == null) return;
            deleteLocalImage(publicId);
        }

    }

    private String uploadImageLocal(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessException("File is Empty", HttpStatus.BAD_REQUEST);
        }

        Path directoryPath = Paths.get(uploadPath);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        String fileName =  file.getOriginalFilename();
        assert fileName != null;
        Path filePath = directoryPath.resolve(fileName);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        Files.copy(file.getInputStream(), filePath);

        return "http://localhost:"+serverPort + "/api/v1/products/images/" + fileName;
    }

    public Resource loadImageAsResource(String fileName) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();
            Resource resource = new FileSystemResource(filePath);
            if (resource.exists()) {
                return resource;
            } else {
                throw new BusinessException("File not found " + fileName, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new BusinessException("File not found " + fileName, HttpStatus.NOT_FOUND);
        }
    }

    public  void deleteLocalImage(String fileName) {
        Path directoryPath = Paths.get(uploadPath);
        Path filePath = directoryPath.resolve(fileName);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        String[] urlParts = imageUrl.split("/");
        if (urlParts.length > 0) {
            String fileName = urlParts[urlParts.length - 1];
            return fileName.substring(0, fileName.lastIndexOf('.'));
        }
        return null;
    }

    private String extractIdFromUrl(String imageUrl) {
        String[] urlParts = imageUrl.split("/");
        if (urlParts.length > 0) {
            return urlParts[urlParts.length - 1];
        }
        return null;
    }
}