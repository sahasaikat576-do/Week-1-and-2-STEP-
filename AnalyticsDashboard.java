import java.util.*;

class Event {
    String url;
    String userId;
    String source;

    Event(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class AnalyticsDashboard {

    // page → total visits
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // page → unique visitors
    private HashMap<String, HashSet<String>> uniqueVisitors = new HashMap<>();

    // traffic source → count
    private HashMap<String, Integer> trafficSource = new HashMap<>();


    // process page view event
    public void processEvent(Event e) {

        // count page views
        pageViews.put(e.url, pageViews.getOrDefault(e.url, 0) + 1);

        // track unique visitors
        uniqueVisitors.putIfAbsent(e.url, new HashSet<>());
        uniqueVisitors.get(e.url).add(e.userId);

        // track traffic source
        trafficSource.put(e.source, trafficSource.getOrDefault(e.source, 0) + 1);
    }


    // get dashboard stats
    public void getDashboard() {

        System.out.println("Top Pages:");

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        int count = 0;

        while (!pq.isEmpty() && count < 10) {

            Map.Entry<String, Integer> entry = pq.poll();
            String url = entry.getKey();
            int visits = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println(
                    (count + 1) + ". " + url +
                    " - " + visits + " views (" + unique + " unique)"
            );

            count++;
        }

        System.out.println("\nTraffic Sources:");
        for (String src : trafficSource.keySet()) {
            System.out.println(src + " → " + trafficSource.get(src));
        }
    }


    public static void main(String[] args) {

        AnalyticsDashboard dashboard = new AnalyticsDashboard();

        dashboard.processEvent(new Event("/article/breaking-news", "user_123", "google"));
        dashboard.processEvent(new Event("/article/breaking-news", "user_456", "facebook"));
        dashboard.processEvent(new Event("/sports/championship", "user_789", "direct"));
        dashboard.processEvent(new Event("/sports/championship", "user_123", "google"));
        dashboard.processEvent(new Event("/article/breaking-news", "user_123", "google"));

        dashboard.getDashboard();
    }
}