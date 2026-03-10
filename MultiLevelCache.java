import java.util.*;

class VideoData {
    String videoId;
    String content;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

// LRU Cache using LinkedHashMap
class LRUCache extends LinkedHashMap<String, VideoData> {

    private int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true); // access-order
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
        return size() > capacity;
    }
}

public class MultiLevelCache {

    // L1 Cache (memory)
    private LRUCache L1 = new LRUCache(10000);

    // L2 Cache (simulated SSD)
    private LRUCache L2 = new LRUCache(100000);

    // L3 Database
    private HashMap<String, VideoData> database = new HashMap<>();

    // Access count tracking
    private HashMap<String, Integer> accessCount = new HashMap<>();

    // Statistics
    int l1Hits = 0;
    int l2Hits = 0;
    int l3Hits = 0;

    public MultiLevelCache() {

        // Simulated database content
        for (int i = 1; i <= 200000; i++) {
            database.put("video_" + i,
                    new VideoData("video_" + i, "Content of video " + i));
        }
    }

    public VideoData getVideo(String videoId) {

        // 1️⃣ Check L1
        if (L1.containsKey(videoId)) {
            l1Hits++;
            System.out.println("L1 Cache HIT (0.5ms)");
            return L1.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        // 2️⃣ Check L2
        if (L2.containsKey(videoId)) {

            l2Hits++;

            System.out.println("L2 Cache HIT (5ms)");

            VideoData video = L2.get(videoId);

            promoteToL1(video);

            return video;
        }

        System.out.println("L2 Cache MISS");

        // 3️⃣ Check L3 Database
        if (database.containsKey(videoId)) {

            l3Hits++;

            System.out.println("L3 Database HIT (150ms)");

            VideoData video = database.get(videoId);

            promoteToL2(video);

            return video;
        }

        return null;
    }

    // Promote video from L2 → L1
    private void promoteToL1(VideoData video) {

        L1.put(video.videoId, video);

        accessCount.put(video.videoId,
                accessCount.getOrDefault(video.videoId, 0) + 1);

        System.out.println("Promoted to L1");
    }

    // Add video to L2
    private void promoteToL2(VideoData video) {

        L2.put(video.videoId, video);

        accessCount.put(video.videoId,
                accessCount.getOrDefault(video.videoId, 0) + 1);

        System.out.println("Added to L2 (access count: "
                + accessCount.get(video.videoId) + ")");
    }

    // Cache statistics
    public void getStatistics() {

        int total = l1Hits + l2Hits + l3Hits;

        double l1Rate = (l1Hits * 100.0) / total;
        double l2Rate = (l2Hits * 100.0) / total;
        double l3Rate = (l3Hits * 100.0) / total;

        System.out.println("\nCache Statistics:");

        System.out.printf("L1 Hit Rate: %.2f%%\n", l1Rate);
        System.out.printf("L2 Hit Rate: %.2f%%\n", l2Rate);
        System.out.printf("L3 Hit Rate: %.2f%%\n", l3Rate);

        double overall = ((l1Hits + l2Hits) * 100.0) / total;

        System.out.printf("Overall Cache Hit Rate: %.2f%%\n", overall);
    }

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.getVideo("video_123");
        System.out.println();

        cache.getVideo("video_123");
        System.out.println();

        cache.getVideo("video_999");
        System.out.println();

        cache.getStatistics();
    }
}