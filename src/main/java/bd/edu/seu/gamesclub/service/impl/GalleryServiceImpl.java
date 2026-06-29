package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.GalleryCategoryRequest;
import bd.edu.seu.gamesclub.dto.GalleryCategoryResponse;
import bd.edu.seu.gamesclub.dto.GalleryImageResponse;
import bd.edu.seu.gamesclub.entity.GalleryCategory;
import bd.edu.seu.gamesclub.entity.GalleryImage;
import bd.edu.seu.gamesclub.exception.DuplicateResourceException;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.GalleryMapper;
import bd.edu.seu.gamesclub.repository.GalleryCategoryRepository;
import bd.edu.seu.gamesclub.repository.GalleryImageRepository;
import bd.edu.seu.gamesclub.service.GalleryService;
import bd.edu.seu.gamesclub.service.MediaService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link GalleryService}. */
@Service
@Transactional(readOnly = true)
public class GalleryServiceImpl implements GalleryService {

    private final GalleryCategoryRepository categoryRepository;
    private final GalleryImageRepository imageRepository;
    private final MediaService mediaService;

    public GalleryServiceImpl(GalleryCategoryRepository categoryRepository,
                              GalleryImageRepository imageRepository,
                              MediaService mediaService) {
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
        this.mediaService = mediaService;
    }

    @Override
    public List<GalleryCategoryResponse> getCategories() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(c -> GalleryMapper.toCategoryResponse(c, c.getImages().size()))
                .toList();
    }

    @Override
    @Transactional
    public GalleryCategoryResponse createCategory(GalleryCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("A gallery category with this name already exists.");
        }
        GalleryCategory category = new GalleryCategory();
        category.setName(request.name());
        category.setSlug(bd.edu.seu.gamesclub.util.SlugUtil.uniqueSlug(request.name(),
                s -> categoryRepository.findBySlug(s).isPresent()));
        category.setDescription(request.description());
        return GalleryMapper.toCategoryResponse(categoryRepository.save(category), 0);
    }

    @Override
    @Transactional
    public GalleryCategoryResponse updateCategory(Long id, GalleryCategoryRequest request) {
        GalleryCategory category = findCategory(id);
        if (!category.getName().equalsIgnoreCase(request.name()) && categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("A gallery category with this name already exists.");
        }
        category.setName(request.name());
        category.setDescription(request.description());
        return GalleryMapper.toCategoryResponse(categoryRepository.save(category), category.getImages().size());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.delete(findCategory(id));
    }

    @Override
    public Page<GalleryImageResponse> getImagesPage(Pageable pageable) {
        return imageRepository.findAll(pageable).map(GalleryMapper::toImageResponse);
    }

    @Override
    public List<GalleryImageResponse> getImagesByCategory(Long categoryId) {
        return imageRepository.findByCategoryIdOrderByDisplayOrderAsc(categoryId).stream()
                .map(GalleryMapper::toImageResponse).toList();
    }

    @Override
    @Transactional
    public GalleryImageResponse addImage(Long categoryId, Long mediaId, String caption, Integer displayOrder) {
        GalleryCategory category = findCategory(categoryId);
        GalleryImage image = new GalleryImage();
        image.setCategory(category);
        image.setMedia(mediaService.getReference(mediaId));
        image.setCaption(caption);
        if (displayOrder != null) {
            image.setDisplayOrder(displayOrder);
        }
        return GalleryMapper.toImageResponse(imageRepository.save(image));
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        GalleryImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery image", "id", imageId));
        imageRepository.delete(image);
    }

    private GalleryCategory findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery category", "id", id));
    }
}
