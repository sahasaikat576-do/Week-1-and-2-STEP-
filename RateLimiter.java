import java.util.concurrent.*;

class TokenBucket {

    int maxTokens;
    double refillRate; // tokens per second
    double tokens;
    long lastRefillTime;

    public TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // refill tokens based on elapsed time
    private void refill() {
        long now = System.currentTimeMillis();
        double elapsed = (now - lastRefillTime) / 1000.0;

        double refillTokens = elapsed * refillRate;

        tokens = Math.min(maxTokens, tokens + refillTokens);
        lastRefillTime = now;
    }

    public synchronized boolean allowRequest() {
        refill();

        if (tokens >= 1) {
            tokens--;
            return true;
        }

        return false;
    }

    public int getRemainingTokens() {
        return (int) tokens;
    }
}

public class RateLimiter {

    // clientId → TokenBucket
    private ConcurrentHashMap<String, TokenBucket> clients = new ConcurrentHashMap<>();

    private int maxRequests = 1000;
    private double refillRate = 1000.0 / 3600.0; // per second

    public boolean checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId, new TokenBucket(maxRequests, refillRate));

        TokenBucket bucket = clients.get(clientId);

        boolean allowed = bucket.allowRequest();

        if (allowed) {
            System.out.println("Allowed (" + bucket.getRemainingTokens() + " requests remaining)");
        } else {
            System.out.println("Denied (limit exceeded)");
        }

        return allowed;
    }

    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            System.out.println("Client not found");
            return;
        }

        int used = maxRequests - bucket.getRemainingTokens();

        System.out.println(
                "{used: " + used +
                ", limit: " + maxRequests +
                ", remaining: " + bucket.getRemainingTokens() + "}"
        );
    }

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String client = "abc123";

        limiter.checkRateLimit(client);
        limiter.checkRateLimit(client);
        limiter.checkRateLimit(client);

        limiter.getRateLimitStatus(client);
    }
}