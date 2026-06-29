package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.FaqRequest;
import bd.edu.seu.gamesclub.dto.FaqResponse;
import bd.edu.seu.gamesclub.entity.Faq;

/** Manual mapper for {@link Faq}. */
public final class FaqMapper {

    private FaqMapper() {
    }

    public static FaqResponse toResponse(Faq f) {
        if (f == null) {
            return null;
        }
        return new FaqResponse(f.getId(), f.getQuestion(), f.getAnswer(), f.getDisplayOrder(), f.isActive());
    }

    public static void apply(Faq f, FaqRequest r) {
        f.setQuestion(r.question());
        f.setAnswer(r.answer());
        if (r.displayOrder() != null) {
            f.setDisplayOrder(r.displayOrder());
        }
        if (r.active() != null) {
            f.setActive(r.active());
        }
    }
}
