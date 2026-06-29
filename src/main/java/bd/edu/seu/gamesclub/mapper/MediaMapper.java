package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.MediaAssetResponse;
import bd.edu.seu.gamesclub.entity.MediaAsset;
import bd.edu.seu.gamesclub.util.MediaUrls;

/** Manual mapper for {@link MediaAsset}. */
public final class MediaMapper {

    private MediaMapper() {
    }

    public static MediaAssetResponse toResponse(MediaAsset m) {
        if (m == null) {
            return null;
        }
        return new MediaAssetResponse(
                m.getId(),
                MediaUrls.url(m),
                m.getAltText(),
                m.getMimeType(),
                m.getFileSize(),
                m.getWidth(),
                m.getHeight(),
                m.getMediaType() != null ? m.getMediaType().name() : null
        );
    }
}
