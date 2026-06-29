package bd.edu.seu.gamesclub.util;

import bd.edu.seu.gamesclub.entity.MediaAsset;

/**
 * Builds public URLs for stored {@link MediaAsset}s. Files are served under
 * {@code /uploads/**}.
 */
public final class MediaUrls {

    private MediaUrls() {
    }

    /**
     * @param media the media asset (may be {@code null})
     * @return the public URL, or {@code null} when no media is present
     */
    public static String url(MediaAsset media) {
        return media == null ? null : "/uploads/" + media.getFilePath();
    }
}
