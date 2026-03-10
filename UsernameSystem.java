import java.util.*;

public class UsernameSystem {

    // username -> userId
    private HashMap<String, Integer> users = new HashMap<>();

    // username -> attempt count
    private HashMap<String, Integer> attempts = new HashMap<>();


    // check username availability
    public boolean checkAvailability(String username) {

        // track attempts
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);

        return !users.containsKey(username);
    }


    // register username
    public void register(String username, int userId) {
        users.put(username, userId);
    }


    // suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        suggestions.add(username + "1");
        suggestions.add(username + "2");
        suggestions.add(username.replace("_", "."));

        return suggestions;
    }


    // find most attempted username
    public String getMostAttempted() {

        String result = "";
        int max = 0;

        for (String key : attempts.keySet()) {
            if (attempts.get(key) > max) {
                max = attempts.get(key);
                result = key;
            }
        }

        return result + " (" + max + " attempts)";
    }


    public static void main(String[] args) {

        UsernameSystem system = new UsernameSystem();

        // existing users
        system.register("john_doe", 1);
        system.register("admin", 2);

        System.out.println(system.checkAvailability("john_doe"));     // false
        System.out.println(system.checkAvailability("jane_smith"));   // true

        System.out.println(system.suggestAlternatives("john_doe"));

        // simulate attempts
        system.checkAvailability("admin");
        system.checkAvailability("admin");
        system.checkAvailability("admin");

        System.out.println(system.getMostAttempted());
    }
}