package bd.edu.seu.gamesclub.dto;

/**
 * Aggregated counters for the admin dashboard cards, produced by the
 * {@code DashboardService}.
 */
public record DashboardStatsResponse(
        long totalStudents,
        long totalEvents,
        long totalNews,
        long totalSports,
        long totalGalleryImages,
        long totalMessages,
        long unreadMessages,
        long totalApplications,
        long pendingApplications,
        long emailSubscribers,
        boolean membershipOpen
) {}
