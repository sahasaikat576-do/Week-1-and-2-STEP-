import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCache {

    private int capacity;
    private int hits = 0;
    private int misses = 0;

    // LRU cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache;

    public DNSCache(int capacity) {
        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };
    }

    // Simulate upstream DNS lookup
    private String queryUpstreamDNS(String domain) {
        return "192.168." + new Random().nextInt(255) + "." + new Random().nextInt(255);
    }

    public String resolve(String domain) {

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                System.out.println("Cache HIT → " + entry.ipAddress);
                return entry.ipAddress;
            }

            // expired entry
            cache.remove(domain);
            System.out.println("Cache EXPIRED → querying upstream");
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        DNSEntry newEntry = new DNSEntry(domain, ip, 5); // TTL = 5 seconds
        cache.put(domain, newEntry);

        System.out.println("Cache MISS → Query upstream → " + ip);

        return ip;
    }

    public void getCacheStats() {

        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;

        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws Exception {

        DNSCache dns = new DNSCache(3);

        dns.resolve("google.com");
        dns.resolve("google.com");

        Thread.sleep(6000); // simulate TTL expiration

        dns.resolve("google.com");

        dns.getCacheStats();
    }
}