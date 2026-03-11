import java.util.*;
import java.util.concurrent.*;

/**
 * Problem 5: Real-Time Analytics Dashboard
 * Demonstrates multi-dimensional frequency counting and Top-N optimization.
 */
public class RealTimeAnalyticsDashboard {

    // 1. Storage for different dimensions
    private static Map<String, Integer> pageViews = new ConcurrentHashMap<>();
    private static Map<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    private static Map<String, Integer> trafficSources = new ConcurrentHashMap<>();

    private static long totalEvents = 0;

    public static void main(String[] args) {
        // Simulating high-speed event processing
        processEvent("/article/breaking-news", "user_123", "google");
        processEvent("/article/breaking-news", "user_456", "facebook");
        processEvent("/article/breaking-news", "user_123", "google"); // Repeat user
        processEvent("/sports/championship", "user_789", "direct");
        processEvent("/tech/new-gadget", "user_001", "google");

        // Displaying Dashboard
        getDashboard();
    }

    /**
     * Processes incoming page view events in real-time (O(1) average).
     */
    public static void processEvent(String url, String userId, String source) {
        totalEvents++;

        // Update Total Views
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // Update Unique Visitors (Set handles duplicates)
        uniqueVisitors.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet()).add(userId);

        // Update Traffic Sources
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    /**
     * Calculates and displays top 10 pages and source percentages.
     */
    public static void getDashboard() {
        System.out.println("--- REAL-TIME ANALYTICS DASHBOARD ---");

        // Use a PriorityQueue to find Top 10 efficiently
        PriorityQueue<Map.Entry<String, Integer>> topPages = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry::getValue)
        );

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            topPages.offer(entry);
            if (topPages.size() > 10) {
                topPages.poll(); // Remove the page with the lowest views
            }
        }

        System.out.println("Top Pages:");
        List<Map.Entry<String, Integer>> sortedTop = new ArrayList<>(topPages);
        sortedTop.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (int i = 0; i < sortedTop.size(); i++) {
            String url = sortedTop.get(i).getKey();
            int views = sortedTop.get(i).getValue();
            int uniques = uniqueVisitors.get(url).size();
            System.out.printf("%d. %s - %d views (%d unique)%n", (i+1), url, views, uniques);
        }

        System.out.println("\nTraffic Sources:");
        trafficSources.forEach((source, count) -> {
            double percentage = (count * 100.0) / totalEvents;
            System.out.printf("%s: %.1f%%%n", source, percentage);
        });
    }
}