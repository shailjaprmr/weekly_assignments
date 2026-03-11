import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Problem 3: DNS Cache with TTL (Time To Live)
 * Demonstrates time-based expiration, cache metrics, and LRU logic.
 */
public class DNSCacheSystem {

    // Custom Entry Class to store metadata
    static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime; // System time in ms when this expires

        public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final Map<String, DNSEntry> cache = new ConcurrentHashMap<>();
    private int hits = 0;
    private int misses = 0;
    private static final int MAX_CACHE_SIZE = 1000;

    public static void main(String[] args) throws InterruptedException {
        DNSCacheSystem dns = new DNSCacheSystem();

        // Simulate lookups
        dns.resolve("google.com"); // Miss
        dns.resolve("google.com"); // Hit

        // Simulating a short TTL for demonstration
        dns.putInCache("test.com", "1.1.1.1", 2);
        System.out.println("Resolving test.com immediately...");
        dns.resolve("test.com"); // Hit

        System.out.println("Waiting for TTL to expire (3s)...");
        Thread.sleep(3000);
        dns.resolve("test.com"); // Expired -> Miss

        dns.getCacheStats();
    }

    public String resolve(String domain) {
        long startTime = System.nanoTime();
        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            long duration = System.nanoTime() - startTime;
            System.out.printf("resolve(\"%s\") → Cache HIT → %s (%.3fms)%n",
                    domain, entry.ipAddress, duration / 1_000_000.0);
            return entry.ipAddress;
        }

        // Cache Miss or Expired
        misses++;
        if (entry != null && entry.isExpired()) {
            System.out.print("resolve(\"" + domain + "\") → Cache EXPIRED → ");
            cache.remove(domain);
        } else {
            System.out.print("resolve(\"" + domain + "\") → Cache MISS → ");
        }

        String ip = queryUpstreamDNS(domain);
        putInCache(domain, ip, 300); // Default 300s TTL
        System.out.println("Query upstream → " + ip);
        return ip;
    }

    private void putInCache(String domain, String ip, int ttl) {
        // Simple LRU: if cache is full, clear it (or remove oldest)
        if (cache.size() >= MAX_CACHE_SIZE) {
            cache.clear();
        }
        cache.put(domain, new DNSEntry(domain, ip, ttl));
    }

    private String queryUpstreamDNS(String domain) {
        // Mocking an upstream DNS query
        return "172.217.14." + (int)(Math.random() * 255);
    }

    public void getCacheStats() {
        double hitRate = (hits + misses == 0) ? 0 : (double) hits / (hits + misses) * 100;
        System.out.println("\n--- DNS Cache Stats ---");
        System.out.printf("Total Requests: %d | Hits: %d | Misses: %d%n", (hits + misses), hits, misses);
        System.out.printf("Hit Rate: %.1f%%%n", hitRate);
    }
}