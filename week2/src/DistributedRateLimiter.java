import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class DistributedRateLimiter {
    // Stores bucket state for 100,000 clients
    private final ConcurrentHashMap<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();

    // Configuration: 1000 requests per hour (3600 seconds)
    private static final long MAX_TOKENS = 1000;
    private static final long REFILL_INTERVAL_MILLIS = 3600 * 1000; // 1 Hour
    private static final double REFILL_RATE = (double) MAX_TOKENS / REFILL_INTERVAL_MILLIS;

    class TokenBucket {
        private double tokens;
        private long lastRefillTime;

        public TokenBucket() {
            this.tokens = MAX_TOKENS;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // Synchronized to handle concurrency per client
        public synchronized boolean tryConsume() {
            refill();
            if (tokens >= 1) {
                tokens--;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long delta = now - lastRefillTime;
            // Calculate how many tokens should have been added since last check
            tokens = Math.min(MAX_TOKENS, tokens + (delta * REFILL_RATE));
            lastRefillTime = now;
        }

        public long getTokens() { return (long) Math.floor(tokens); }
        public long getResetTime() { return lastRefillTime + REFILL_INTERVAL_MILLIS; }
    }

    public String checkRateLimit(String clientId) {
        TokenBucket bucket = clientBuckets.computeIfAbsent(clientId, k -> new TokenBucket());

        if (bucket.tryConsume()) {
            return "Allowed (" + bucket.getTokens() + " requests remaining)";
        } else {
            long retryIn = (bucket.getResetTime() - System.currentTimeMillis()) / 1000;
            return "Denied (0 requests remaining, retry after " + retryIn + "s)";
        }
    }

    public static void main(String[] args) {
        DistributedRateLimiter limiter = new DistributedRateLimiter();
        System.out.println(limiter.checkRateLimit("abc123")); // Allowed (999...)
    }
}