import java.util.*;
public class SocialMedia {
    // 1. Data Structure: Store taken usernames (username -> userId)
    private static Map<String, Integer> userDatabase = new HashMap<>();

    // 2. Data Structure: Track attempt frequency for popularity analysis
    private static Map<String, Integer> attemptTracker = new HashMap<>();

    public static void main(String[] args) {
        // Pre-populating with some "taken" users
        userDatabase.put("john_doe", 101);
        userDatabase.put("jane_doe", 102);
        userDatabase.put("admin", 103);

        // Sample Test Cases
        testUsername("john_doe");
        testUsername("jane_smith");

        System.out.println("Suggestions for 'john_doe': " + suggestAlternatives("john_doe"));
        System.out.println("Most Attempted Username: " + getMostAttempted());
    }

    /**
     * UC: Check availability in O(1)
     */
    public static boolean checkAvailability(String username) {
        // Track the attempt regardless of availability
        attemptTracker.put(username, attemptTracker.getOrDefault(username, 0) + 1);

        // HashMap.containsKey() provides O(1) average time complexity
        return !userDatabase.containsKey(username);
    }

    /**
     * UC: Suggest similar usernames if taken
     */
    public static List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int count = 1;

        while (suggestions.size() < 3) {
            String candidate = username + count;
            if (!userDatabase.containsKey(candidate)) {
                suggestions.add(candidate);
            }
            count++;
        }
        return suggestions;
    }

    /**
     * UC: Track popularity of attempted usernames
     */
    public static String getMostAttempted() {
        String mostAttempted = "None";
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptTracker.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }
        return mostAttempted + " (" + max + " attempts)";
    }

    private static void testUsername(String username) {
        System.out.println("Checking '" + username + "': " +
                (checkAvailability(username) ? "Available" : "Already Taken"));
    }
}
