package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.MediaAssetResponse;
import bd.edu.seu.gamesclub.entity.MediaAsset;
import bd.edu.seu.gamesclub.entity.enums.MediaType;
import bd.edu.seu.gamesclub.exception.FileStorageException;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.MediaMapper;
import bd.edu.seu.gamesclub.repository.MediaAssetRepository;
import bd.edu.seu.gamesclub.service.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Filesystem-backed {@link MediaService}. Files are written under the configured
 * upload directory (date-partitioned), and a {@link MediaAsset} row captures the
 * relative path plus metadata.
 */
@Slf4j
@Service
public class MediaServiceImpl implements MediaService {

    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5MB

    private final MediaAssetRepository mediaAssetRepository;
    private final Path baseDir;

    public MediaServiceImpl(MediaAssetRepository mediaAssetRepository,
                            @Value("${app.upload.base-dir:uploads}") String baseDir) {
        this.mediaAssetRepository = mediaAssetRepository;
        this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
    }

    @Override
    @Transactional
    public MediaAssetResponse store(MultipartFile file, String altText) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("No file was provided.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileStorageException("Only image uploads are allowed.");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new FileStorageException("File exceeds the 5MB limit.");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
        String ext = StringUtils.getFilenameExtension(original);
        String relativeDir = LocalDate.now().toString().replace("-", "/");
        String fileName = UUID.randomUUID() + (ext != null ? "." + ext.toLowerCase() : "");
        String relativePath = relativeDir + "/" + fileName;

        Integer width = null;
        Integer height = null;
        try {
            Path targetDir = baseDir.resolve(relativeDir);
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(fileName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            try (InputStream dim = file.getInputStream()) {
                BufferedImage img = ImageIO.read(dim);
                if (img != null) {
                    width = img.getWidth();
                    height = img.getHeight();
                }
            }
        } catch (IOException ex) {
            throw new FileStorageException("Failed to store file " + original, ex);
        }

        MediaAsset asset = new MediaAsset();
        asset.setFilePath(relativePath);
        asset.setOriginalName(original);
        asset.setAltText(altText);
        asset.setMimeType(contentType);
        asset.setFileSize(file.getSize());
        asset.setWidth(width);
        asset.setHeight(height);
        asset.setMediaType(MediaType.IMAGE);
        return MediaMapper.toResponse(mediaAssetRepository.save(asset));
    }

    @Override
    @Transactional(readOnly = true)
    public MediaAsset getReference(Long id) {
        if (id == null) {
            return null;
        }
        return mediaAssetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MediaAsset", "id", id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MediaAsset asset = mediaAssetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MediaAsset", "id", id));
        try {
            Files.deleteIfExists(baseDir.resolve(asset.getFilePath()));
        } catch (IOException ex) {
            log.warn("Could not delete file for media {}: {}", id, ex.getMessage());
        }
        mediaAssetRepository.delete(asset);
    }
}
