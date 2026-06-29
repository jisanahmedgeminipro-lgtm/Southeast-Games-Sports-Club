package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.MediaAssetResponse;
import bd.edu.seu.gamesclub.entity.MediaAsset;
import org.springframework.web.multipart.MultipartFile;

/**
 * Stores uploaded files in the centralized media library and records their
 * metadata (alt text, size, MIME type, dimensions).
 */
public interface MediaService {

    /**
     * Validate and store an uploaded image, persisting a {@link MediaAsset}.
     *
     * @param file    the uploaded file (must be a non-empty image)
     * @param altText optional accessibility text
     * @return the stored media as a response DTO
     */
    MediaAssetResponse store(MultipartFile file, String altText);

    /**
     * Resolve a persisted media asset entity by id, for other services that need
     * to attach it to their aggregate.
     *
     * @param id media id (may be {@code null} -> returns {@code null})
     * @return the entity, or {@code null} when id is null
     */
    MediaAsset getReference(Long id);

    /** Delete a media asset and its underlying file. */
    void delete(Long id);
}
