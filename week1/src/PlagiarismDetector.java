import java.util.*;

/**
 * Problem 4: Plagiarism Detection System
 * Uses N-Grams and HashMaps to detect document similarity.
 */
public class PlagiarismDetector {

    // 1. Data Structure: N-Gram -> Set of Document IDs that contain it
    private static Map<String, Set<String>> ngramIndex = new HashMap<>();

    // Total n-grams per document to calculate percentage
    private static Map<String, Integer> docSizeTracker = new HashMap<>();

    public static void main(String[] args) {
        // Pre-populating database with existing essays
        indexDocument("essay_089.txt", "The quick brown fox jumps over the lazy dog in the forest.");
        indexDocument("essay_092.txt", "Artificial intelligence is the future of computer science and engineering.");

        // Testing a new submission
        String submissionId = "student_submission_001.txt";
        String submissionContent = "Artificial intelligence is clearly the future of computer science fields.";

        analyzeDocument(submissionId, submissionContent);
    }

    /**
     * Breaks document into 5-grams and indexes them.
     */
    public static void indexDocument(String docId, String content) {
        List<String> ngrams = generateNGrams(content, 5);
        docSizeTracker.put(docId, ngrams.size());

        for (String gram : ngrams) {
            ngramIndex.computeIfAbsent(gram, k -> new HashSet<>()).add(docId);
        }
    }

    /**
     * Analyzes a new document against the database.
     */
    public static void analyzeDocument(String docId, String content) {
        List<String> submissionNGrams = generateNGrams(content, 5);
        int totalNGrams = submissionNGrams.size();

        // Track match counts: OtherDocID -> Count of matching n-grams
        Map<String, Integer> matchCounts = new HashMap<>();

        for (String gram : submissionNGrams) {
            if (ngramIndex.containsKey(gram)) {
                for (String otherDocId : ngramIndex.get(gram)) {
                    matchCounts.put(otherDocId, matchCounts.getOrDefault(otherDocId, 0) + 1);
                }
            }
        }

        System.out.println("--- Plagiarism Report for " + docId + " ---");
        System.out.println("Extracted " + totalNGrams + " n-grams.");

        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            double similarity = (entry.getValue() * 100.0) / totalNGrams;
            String status = similarity > 50 ? "PLAGIARISM DETECTED" : (similarity > 10 ? "SUSPICIOUS" : "CLEAN");

            System.out.printf("→ Found %d matching n-grams with \"%s\"%n", entry.getValue(), entry.getKey());
            System.out.printf("→ Similarity: %.1f%% (%s)%n", similarity, status);
        }
    }

    /**
     * Generates a list of n-word sequences.
     */
    private static List<String> generateNGrams(String text, int n) {
        String[] words = text.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(words[i + j]).append(j < n - 1 ? " " : "");
            }
            ngrams.add(sb.toString());
        }
        return ngrams;
    }
}
