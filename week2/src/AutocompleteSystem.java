import java.util.*;

class AutocompleteSystem {

    class Node {
        Map<Character, Node> children = new HashMap<>();
        // Stores the top 10 search queries passing through this prefix
        PriorityQueue<String> topSuggestions = new PriorityQueue<>(
                (a, b) -> counts.get(a).equals(counts.get(b)) ?
                        b.compareTo(a) : counts.get(a) - counts.get(b)
        );
    }

    private final Node root = new Node();
    private final Map<String, Integer> counts = new HashMap<>();
    private static final int K = 10;

    // 1. Update frequency and rebuild path metadata
    public void updateFrequency(String query) {
        counts.put(query, counts.getOrDefault(query, 0) + 1);

        Node curr = root;
        for (char c : query.toCharArray()) {
            curr.children.putIfAbsent(c, new Node());
            curr = curr.children.get(c);
            updateTopK(curr, query);
        }
    }

    private void updateTopK(Node node, String query) {
        if (!node.topSuggestions.contains(query)) {
            node.topSuggestions.add(query);
        }
        if (node.topSuggestions.size() > K) {
            node.topSuggestions.poll(); // Remove least frequent
        }
    }

    // 2. Search suggestions in O(L) time
    public List<String> search(String prefix) {
        Node curr = root;
        for (char c : prefix.toCharArray()) {
            if (!curr.children.containsKey(c)) return Collections.emptyList();
            curr = curr.children.get(c);
        }

        List<String> results = new ArrayList<>(curr.topSuggestions);
        results.sort((a, b) -> counts.get(b) - counts.get(a)); // Sort by frequency
        return results;
    }
}