package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.GalleryCategoryResponse;
import bd.edu.seu.gamesclub.dto.GalleryImageResponse;
import bd.edu.seu.gamesclub.entity.GalleryCategory;
import bd.edu.seu.gamesclub.entity.GalleryImage;
import bd.edu.seu.gamesclub.util.MediaUrls;

/** Manual mapper for gallery categories and images. */
public final class GalleryMapper {

    private GalleryMapper() {
    }

    public static GalleryCategoryResponse toCategoryResponse(GalleryCategory c, long imageCount) {
        if (c == null) {
            return null;
        }
        return new GalleryCategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getDescription(), imageCount);
    }

    public static GalleryImageResponse toImageResponse(GalleryImage i) {
        if (i == null) {
            return null;
        }
        return new GalleryImageResponse(
                i.getId(),
                i.getCategory() != null ? i.getCategory().getId() : null,
                i.getCategory() != null ? i.getCategory().getSlug() : null,
                MediaUrls.url(i.getMedia()),
                i.getCaption(),
                i.getDisplayOrder(),
                i.getMedia() != null ? i.getMedia().getId() : null
        );
    }
}
