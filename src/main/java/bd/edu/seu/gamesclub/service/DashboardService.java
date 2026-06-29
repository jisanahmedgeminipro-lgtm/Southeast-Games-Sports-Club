package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.DashboardStatsResponse;

/**
 * Aggregates the counters shown on the admin dashboard cards. Kept as a dedicated
 * service so the dashboard's read model is computed in one place.
 */
public interface DashboardService {

    /** Compute all dashboard statistics in a single read-only transaction. */
    DashboardStatsResponse getStats();
}
