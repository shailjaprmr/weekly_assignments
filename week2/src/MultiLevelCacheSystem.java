import java.util.*;

public class MultiLevelCacheSystem {
    // Configuration
    private static final int L1_SIZE = 10000;
    private static final int L2_SIZE = 100000;
    private static final int PROMOTION_THRESHOLD = 5;

    // Cache Tiers
    private final LinkedHashMap<String, String> l1Cache;
    private final LinkedHashMap<String, String> l2Cache;
    private final Map<String, Integer> accessCounts = new HashMap<>();

    // Stats
    private long l1Hits = 0, l2Hits = 0, l3Hits = 0, totalRequests = 0;

    public MultiLevelCacheSystem() {
        // L1 Cache with LRU enabled (accessOrder = true)
        this.l1Cache = new LinkedHashMap<>(L1_SIZE, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > L1_SIZE;
            }
        };

        // L2 Cache with LRU enabled
        this.l2Cache = new LinkedHashMap<>(L2_SIZE, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > L2_SIZE;
            }
        };
    }

    public String getVideo(String videoId) {
        totalRequests++;

        // 1. Check L1 (In-Memory)
        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            return l1Cache.get(videoId);
        }

        // 2. Check L2 (SSD-Backed Simulation)
        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            String data = l2Cache.get(videoId);
            trackAccess(videoId, data);
            return data;
        }

        // 3. Check L3 (Database Simulation)
        l3Hits++;
        String data = fetchFromDatabase(videoId);

        // Always add new items to L2 first
        l2Cache.put(videoId, data);
        trackAccess(videoId, data);

        return data;
    }

    private void trackAccess(String videoId, String data) {
        int count = accessCounts.getOrDefault(videoId, 0) + 1;
        accessCounts.put(videoId, count);

        // Promotion Logic: L2 -> L1
        if (count >= PROMOTION_THRESHOLD && !l1Cache.containsKey(videoId)) {
            l1Cache.put(videoId, data);
            l2Cache.remove(videoId); // Move up to L1
        }
    }

    private String fetchFromDatabase(String id) {
        return "VideoData_for_" + id; // Simulated DB result
    }

    public void getStatistics() {
        System.out.println("L1 Hit Rate: " + (l1Hits * 100.0 / totalRequests) + "%");
        System.out.println("L2 Hit Rate: " + (l2Hits * 100.0 / totalRequests) + "%");
        System.out.println("L3 (DB) Access Rate: " + (l3Hits * 100.0 / totalRequests) + "%");
    }
}
