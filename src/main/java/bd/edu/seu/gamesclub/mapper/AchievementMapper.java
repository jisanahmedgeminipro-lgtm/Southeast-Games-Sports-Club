package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.AchievementRequest;
import bd.edu.seu.gamesclub.dto.AchievementResponse;
import bd.edu.seu.gamesclub.entity.Achievement;
import bd.edu.seu.gamesclub.util.MediaUrls;

/** Manual mapper for {@link Achievement}. Image/sport relations resolved by the service. */
public final class AchievementMapper {

    private AchievementMapper() {
    }

    public static AchievementResponse toResponse(Achievement a) {
        if (a == null) {
            return null;
        }
        return new AchievementResponse(
                a.getId(),
                a.getTitle(),
                a.getAchievementYear(),
                a.getDescription(),
                MediaUrls.url(a.getImage()),
                a.getSport() != null ? a.getSport().getId() : null,
                a.getSport() != null ? a.getSport().getName() : null,
                a.getDisplayOrder(),
                a.getImage() != null ? a.getImage().getId() : null
        );
    }

    public static void apply(Achievement a, AchievementRequest r) {
        a.setTitle(r.title());
        a.setAchievementYear(r.achievementYear());
        a.setDescription(r.description());
        if (r.displayOrder() != null) {
            a.setDisplayOrder(r.displayOrder());
        }
    }
}
