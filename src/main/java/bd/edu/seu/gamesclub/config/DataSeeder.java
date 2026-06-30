package bd.edu.seu.gamesclub.config;

import bd.edu.seu.gamesclub.dto.AchievementRequest;
import bd.edu.seu.gamesclub.dto.ClubSettingsRequest;
import bd.edu.seu.gamesclub.dto.CommitteeMemberRequest;
import bd.edu.seu.gamesclub.dto.EventRequest;
import bd.edu.seu.gamesclub.dto.FaqRequest;
import bd.edu.seu.gamesclub.dto.GalleryCategoryRequest;
import bd.edu.seu.gamesclub.dto.MembershipPeriodRequest;
import bd.edu.seu.gamesclub.dto.NewsRequest;
import bd.edu.seu.gamesclub.dto.SocialLinkRequest;
import bd.edu.seu.gamesclub.dto.SponsorRequest;
import bd.edu.seu.gamesclub.dto.SportRequest;
import bd.edu.seu.gamesclub.entity.MediaAsset;
import bd.edu.seu.gamesclub.entity.enums.MediaType;
import bd.edu.seu.gamesclub.repository.MediaAssetRepository;
import bd.edu.seu.gamesclub.service.AchievementService;
import bd.edu.seu.gamesclub.service.ClubSettingsService;
import bd.edu.seu.gamesclub.service.CommitteeService;
import bd.edu.seu.gamesclub.service.EventService;
import bd.edu.seu.gamesclub.service.FaqService;
import bd.edu.seu.gamesclub.service.GalleryService;
import bd.edu.seu.gamesclub.service.MembershipService;
import bd.edu.seu.gamesclub.service.NewsService;
import bd.edu.seu.gamesclub.service.SocialLinkService;
import bd.edu.seu.gamesclub.service.SponsorService;
import bd.edu.seu.gamesclub.service.SportService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * One-time demo content seeder. On first startup (when no sports exist yet) it
 * populates every public section with realistic sample data - club identity,
 * social links, sports, committee, events, news, achievements, gallery, FAQs,
 * sponsors and an open membership drive - so the site looks complete out of the
 * box. Branded SVG "cover" images are generated on the fly and written to the
 * upload directory, so there are no broken images and no external files needed.
 *
 * <p>It is fully idempotent: once content exists, it does nothing.
 */
@Slf4j
@Component
@Order(20)
public class DataSeeder implements CommandLineRunner {

    private final ClubSettingsService clubSettingsService;
    private final SocialLinkService socialLinkService;
    private final SportService sportService;
    private final CommitteeService committeeService;
    private final EventService eventService;
    private final NewsService newsService;
    private final AchievementService achievementService;
    private final FaqService faqService;
    private final SponsorService sponsorService;
    private final MembershipService membershipService;
    private final GalleryService galleryService;
    private final MediaAssetRepository mediaAssetRepository;
    private final Path seedDir;

    public DataSeeder(ClubSettingsService clubSettingsService, SocialLinkService socialLinkService,
                      SportService sportService, CommitteeService committeeService, EventService eventService,
                      NewsService newsService, AchievementService achievementService, FaqService faqService,
                      SponsorService sponsorService, MembershipService membershipService,
                      GalleryService galleryService, MediaAssetRepository mediaAssetRepository,
                      @Value("${app.upload.base-dir:uploads}") String uploadBaseDir) {
        this.clubSettingsService = clubSettingsService;
        this.socialLinkService = socialLinkService;
        this.sportService = sportService;
        this.committeeService = committeeService;
        this.eventService = eventService;
        this.newsService = newsService;
        this.achievementService = achievementService;
        this.faqService = faqService;
        this.sponsorService = sponsorService;
        this.membershipService = membershipService;
        this.galleryService = galleryService;
        this.mediaAssetRepository = mediaAssetRepository;
        this.seedDir = Paths.get(uploadBaseDir).toAbsolutePath().normalize().resolve("seed");
    }

    @Override
    public void run(String... args) {
        if (!sportService.getAll().isEmpty()) {
            return; // already seeded
        }
        log.info("Seeding demo content...");
        try {
            Files.createDirectories(seedDir);
            seedSports();
            seedClubSettings();
            seedSocialLinks();
            seedCommittee();
            seedEvents();
            seedNews();
            seedAchievements();
            seedGallery();
            seedFaqs();
            seedSponsors();
            seedMembership();
            log.info("Demo content seeding complete.");
        } catch (Exception ex) {
            log.warn("Demo content seeding stopped early: {}", ex.getMessage(), ex);
        }
    }

    /* ------------------------------------------------------------------ */

    private void seedClubSettings() {
        Long logo = svgMedia("logo.svg", "SEU club logo",
                squareSvg("SEU", "#0f4c8f", "#1e6fc4"));
        clubSettingsService.update(new ClubSettingsRequest(
                "Southeast University",
                "Games & Sports Club",
                logo,
                (short) 2010,
                "Fostering sportsmanship, teamwork and excellence.",
                "The Southeast University Games & Sports Club is the central hub for athletic life on "
                        + "campus. We organize inter-department tournaments, represent the university in "
                        + "national competitions, and create a community where every student can compete, "
                        + "connect and grow through sport across more than seven disciplines.",
                "Southeast University, 251/A & 252 Tejgaon I/A, Dhaka 1208, Bangladesh",
                "+880 9617 901234",
                "sportsclub@seu.edu.bd",
                "https://maps.google.com/?q=Southeast+University+Dhaka"));
    }

    private void seedSocialLinks() {
        socialLinkService.create(new SocialLinkRequest("Facebook", "https://facebook.com/seu.edu.bd", "bi-facebook", 1, true));
        socialLinkService.create(new SocialLinkRequest("Instagram", "https://instagram.com", "bi-instagram", 2, true));
        socialLinkService.create(new SocialLinkRequest("LinkedIn", "https://linkedin.com", "bi-linkedin", 3, true));
        socialLinkService.create(new SocialLinkRequest("YouTube", "https://youtube.com", "bi-youtube", 4, true));
    }

    private Long footballId;

    private void seedSports() {
        String[][] sports = {
                {"Football", "The flagship sport of the club, with an inter-department league every semester."},
                {"Cricket", "From tape-tennis to leather-ball tournaments, cricket draws the biggest crowds."},
                {"Basketball", "Fast-paced 5v5 action on our indoor and outdoor courts."},
                {"Volleyball", "A campus favourite, played competitively and recreationally year-round."},
                {"Badminton", "Singles and doubles tournaments hosted in the indoor sports hall."},
                {"Table Tennis", "Quick reflexes and skill on display in our TT championship."},
                {"Chess", "The mind sport of the club - strategy meets concentration."}
        };
        String[] colors = {"#0f4c8f", "#15528f", "#0d3f7d", "#1560b3", "#1466b8", "#10518f", "#0e4a86"};
        int order = 1;
        for (String[] s : sports) {
            Long img = svgMedia("sport-" + order + ".svg", s[0],
                    coverSvg(s[0], "SEU Sports", colors[(order - 1) % colors.length], "#1e6fc4"));
            var resp = sportService.create(new SportRequest(s[0], s[1], null, img, order, true));
            if ("Football".equals(s[0])) {
                footballId = resp.id();
            }
            order++;
        }
    }

    private void seedCommittee() {
        Object[][] exec = {
                {"Tanvir Hasan", "President", "CSE", "62", "TH"},
                {"Sadia Rahman", "Vice President", "BBA", "63", "SR"},
                {"Rakibul Islam", "General Secretary", "EEE", "63", "RI"},
                {"Nusrat Jahan", "Treasurer", "Law", "64", "NJ"},
                {"Arif Mahmud", "Sports Secretary", "CSE", "64", "AM"},
                {"Mitu Akter", "Organizing Secretary", "English", "65", "MA"}
        };
        Object[][] sub = {
                {"Fahim Chowdhury", "Joint Secretary", "CSE", "65", "FC"},
                {"Priya Das", "Event Coordinator", "Pharmacy", "65", "PD"},
                {"Sabbir Ahmed", "Media Lead", "BBA", "66", "SA"},
                {"Lamia Haque", "Member", "Architecture", "66", "LH"}
        };
        int order = 1;
        for (Object[] m : exec) {
            Long photo = svgMedia("exec-" + order + ".svg", (String) m[0],
                    avatarSvg((String) m[4], "#0f4c8f", "#1e6fc4"));
            committeeService.create(new CommitteeMemberRequest("EXECUTIVE", (String) m[0], (String) m[2],
                    (String) m[3], (String) m[1], photo, "https://facebook.com", "https://linkedin.com",
                    "2025-26", order, true));
            order++;
        }
        order = 1;
        for (Object[] m : sub) {
            Long photo = svgMedia("sub-" + order + ".svg", (String) m[0],
                    avatarSvg((String) m[4], "#15528f", "#3d86d6"));
            committeeService.create(new CommitteeMemberRequest("SUB_EXECUTIVE", (String) m[0], (String) m[2],
                    (String) m[3], (String) m[1], photo, "https://facebook.com", "https://linkedin.com",
                    "2025-26", order, true));
            order++;
        }
    }

    private void seedEvents() {
        Object[][] events = {
                {"Inter-Department Football Championship 2026", "The biggest football event of the year - 12 departments battle for the trophy.", "Central Playground", LocalDate.now().plusDays(14), LocalTime.of(15, 30)},
                {"SEU Cricket Premier League", "A T20-style tournament featuring eight student-formed franchises.", "University Cricket Ground", LocalDate.now().plusDays(28), LocalTime.of(9, 0)},
                {"Indoor Badminton Open", "Singles and doubles knockout tournament open to all enrolled students.", "Indoor Sports Hall", LocalDate.now().plusDays(40), LocalTime.of(10, 0)},
                {"Annual Sports Day", "A full day of athletics, fun games and the grand prize-giving ceremony.", "Main Campus", LocalDate.now().plusDays(55), LocalTime.of(8, 30)}
        };
        int i = 1;
        for (Object[] e : events) {
            Long banner = svgMedia("event-" + i + ".svg", (String) e[0],
                    coverSvg("Event", (String) e[0], "#0d3f7d", "#1e6fc4"));
            eventService.create(new EventRequest((String) e[0], (String) e[1], banner, (String) e[2],
                    (LocalDate) e[3], (LocalTime) e[4], null, "UPCOMING", null));
            i++;
        }
    }

    private void seedNews() {
        Object[][] news = {
                {"SEU clinches the inter-university futsal title", "Our futsal squad delivered a stunning final performance to bring home the inter-university trophy, capping off a remarkable unbeaten run this season."},
                {"New indoor sports complex inaugurated", "The university has unveiled a state-of-the-art indoor complex featuring badminton, table tennis and basketball facilities for all students."},
                {"Registration open for the spring tournaments", "Sign-ups for the upcoming football, cricket and badminton tournaments are now live. Form your teams and represent your department."},
                {"Club welcomes the 2025-26 executive committee", "A new student committee takes charge with an ambitious calendar of events, training camps and community sports initiatives."}
        };
        int i = 1;
        for (Object[] n : news) {
            Long img = svgMedia("news-" + i + ".svg", (String) n[0],
                    coverSvg("News", (String) n[0], "#10518f", "#1466b8"));
            newsService.create(new NewsRequest((String) n[0], (String) n[1], img,
                    LocalDate.now().minusDays(i * 3L), "PUBLISHED"));
            i++;
        }
    }

    private void seedAchievements() {
        Object[][] ach = {
                {"Champions - National University Football", (short) 2025, "Defeated 16 universities to lift the national title."},
                {"Runners-up - Inter-University Cricket Cup", (short) 2024, "A hard-fought final that went down to the last over."},
                {"Best Sports Club Award", (short) 2024, "Recognized for outstanding contribution to campus athletics."},
                {"Gold - National Badminton Singles", (short) 2023, "Our player topped the podium at the national meet."}
        };
        int i = 1;
        for (Object[] a : ach) {
            Long img = svgMedia("ach-" + i + ".svg", (String) a[0],
                    coverSvg("Achievement", String.valueOf(a[1]), "#0f4c8f", "#f5b301"));
            achievementService.create(new AchievementRequest((String) a[0], (Short) a[1], (String) a[2],
                    img, null, i));
            i++;
        }
    }

    private void seedGallery() {
        var matches = galleryService.createCategory(new GalleryCategoryRequest("Matches", "Action from our tournaments and games."));
        var events = galleryService.createCategory(new GalleryCategoryRequest("Events", "Ceremonies, sports days and gatherings."));
        var training = galleryService.createCategory(new GalleryCategoryRequest("Training", "Practice sessions and team camps."));
        String[][] imgs = {
                {"Final whistle celebration", "Matches"},
                {"Cricket league opener", "Matches"},
                {"Prize-giving ceremony", "Events"},
                {"Annual sports day", "Events"},
                {"Morning training camp", "Training"},
                {"Badminton practice", "Training"}
        };
        int i = 1;
        for (String[] g : imgs) {
            Long cat = switch (g[1]) {
                case "Events" -> events.id();
                case "Training" -> training.id();
                default -> matches.id();
            };
            Long media = svgMedia("gallery-" + i + ".svg", g[0],
                    coverSvg("Gallery", g[0], "#0d3f7d", "#1e6fc4"));
            galleryService.addImage(cat, media, g[0], i);
            i++;
        }
    }

    private void seedFaqs() {
        faqService.create(new FaqRequest("Who can join the Games & Sports Club?",
                "Any currently enrolled Southeast University student with a valid @seu.edu.bd email can register and apply when membership opens.", 1, true));
        faqService.create(new FaqRequest("When does membership open?",
                "Membership opens at announced times during the year. Watch the membership section and your email for the opening dates.", 2, true));
        faqService.create(new FaqRequest("Is there a membership fee?",
                "Membership is free for enrolled students. Some tournaments may have a small per-team participation fee.", 3, true));
        faqService.create(new FaqRequest("Which sports can I take part in?",
                "Football, Cricket, Basketball, Volleyball, Badminton, Table Tennis and Chess, with more added regularly.", 4, true));
        faqService.create(new FaqRequest("How do I represent the university in tournaments?",
                "Become a member, attend the open trials announced by the club, and get selected by the respective sport's captain.", 5, true));
    }

    private void seedSponsors() {
        String[] names = {"Aamra Networks", "Pran-RFL", "bKash", "Daraz"};
        int i = 1;
        for (String n : names) {
            Long logo = svgMedia("sponsor-" + i + ".svg", n, squareSvg(initials(n), "#1560b3", "#3d86d6"));
            sponsorService.create(new SponsorRequest(n, logo, "https://example.com", i, true));
            i++;
        }
    }

    private void seedMembership() {
        var period = membershipService.createPeriod(new MembershipPeriodRequest(
                "Membership Drive 2026",
                "Membership is now OPEN! Join the SEU Games & Sports Club and be part of every tournament, "
                        + "training camp and event this year. Apply from your student dashboard.",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(20)));
        try {
            membershipService.open(period.id(), "seed@seu.edu.bd");
        } catch (Exception ex) {
            log.warn("Could not auto-open the seeded membership drive: {}", ex.getMessage());
        }
    }

    /* ----------------------------- helpers ----------------------------- */

    private Long svgMedia(String fileName, String alt, String svg) {
        try {
            byte[] bytes = svg.getBytes(StandardCharsets.UTF_8);
            Files.write(seedDir.resolve(fileName), bytes);
            MediaAsset m = new MediaAsset();
            m.setFilePath("seed/" + fileName);
            m.setOriginalName(fileName);
            m.setAltText(alt);
            m.setMimeType("image/svg+xml");
            m.setFileSize((long) bytes.length);
            m.setMediaType(MediaType.IMAGE);
            return mediaAssetRepository.save(m).getId();
        } catch (IOException ex) {
            log.warn("Could not write seed image {}: {}", fileName, ex.getMessage());
            return null;
        }
    }

    private static String coverSvg(String tag, String title, String c1, String c2) {
        String t = escape(title.length() > 34 ? title.substring(0, 33) + "\u2026" : title);
        return "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1200 675'>"
                + "<defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'>"
                + "<stop offset='0' stop-color='" + c1 + "'/><stop offset='1' stop-color='" + c2 + "'/>"
                + "</linearGradient></defs>"
                + "<rect width='1200' height='675' fill='url(#g)'/>"
                + "<circle cx='1000' cy='140' r='240' fill='rgba(255,255,255,0.07)'/>"
                + "<circle cx='160' cy='600' r='180' fill='rgba(255,255,255,0.05)'/>"
                + "<text x='70' y='300' font-family='Arial,Helvetica,sans-serif' font-size='30' font-weight='bold' "
                + "fill='#f5b301' letter-spacing='3'>" + escape(tag.toUpperCase()) + "</text>"
                + "<text x='70' y='380' font-family='Arial,Helvetica,sans-serif' font-size='64' font-weight='bold' "
                + "fill='#ffffff'>" + t + "</text>"
                + "<text x='72' y='620' font-family='Arial,Helvetica,sans-serif' font-size='28' "
                + "fill='rgba(255,255,255,0.8)'>Southeast University Games &amp; Sports Club</text>"
                + "</svg>";
    }

    private static String avatarSvg(String initials, String c1, String c2) {
        return "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 400 400'>"
                + "<defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'>"
                + "<stop offset='0' stop-color='" + c1 + "'/><stop offset='1' stop-color='" + c2 + "'/>"
                + "</linearGradient></defs>"
                + "<rect width='400' height='400' fill='url(#g)'/>"
                + "<text x='200' y='258' font-family='Arial,Helvetica,sans-serif' font-size='170' font-weight='bold' "
                + "fill='#ffffff' text-anchor='middle'>" + escape(initials) + "</text>"
                + "</svg>";
    }

    private static String squareSvg(String label, String c1, String c2) {
        return "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 400 400'>"
                + "<defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'>"
                + "<stop offset='0' stop-color='" + c1 + "'/><stop offset='1' stop-color='" + c2 + "'/>"
                + "</linearGradient></defs>"
                + "<rect width='400' height='400' rx='40' fill='url(#g)'/>"
                + "<text x='200' y='235' font-family='Arial,Helvetica,sans-serif' font-size='110' font-weight='bold' "
                + "fill='#ffffff' text-anchor='middle'>" + escape(label) + "</text>"
                + "</svg>";
    }

    private static String initials(String name) {
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty() && sb.length() < 2) {
                sb.append(Character.toUpperCase(p.charAt(0)));
            }
        }
        return sb.length() == 0 ? "S" : sb.toString();
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
