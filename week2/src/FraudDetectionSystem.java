import java.util.*;

public class FraudDetectionSystem {

    class Transaction {
        int id;
        double amount;
        String merchant;
        long timestamp; // epoch millis
        String accountId;

        public Transaction(int id, double amount, String merchant, long timestamp, String accountId) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.timestamp = timestamp;
            this.accountId = accountId;
        }
    }

    // 1. Classic Two-Sum: Finds pairs summing to target
    public List<int[]> findTwoSum(List<Transaction> transactions, double target) {
        Map<Double, Integer> seen = new HashMap<>(); // amount -> transactionId
        List<int[]> results = new ArrayList<>();

        for (Transaction tx : transactions) {
            double complement = target - tx.amount;
            if (seen.containsKey(complement)) {
                results.add(new int[]{seen.get(complement), tx.id});
            }
            seen.put(tx.amount, tx.id);
        }
        return results;
    }

    // 2. Duplicate Detection: Same amount + same merchant + different accounts
    public List<Transaction> detectDuplicates(List<Transaction> transactions) {
        // Key: amount + merchant name
        Map<String, Transaction> registry = new HashMap<>();
        List<Transaction> duplicates = new ArrayList<>();

        for (Transaction tx : transactions) {
            String key = tx.amount + "|" + tx.merchant;
            if (registry.containsKey(key)) {
                Transaction original = registry.get(key);
                if (!original.accountId.equals(tx.accountId)) {
                    duplicates.add(tx);
                }
            } else {
                registry.put(key, tx);
            }
        }
        return duplicates;
    }

    // 3. Two-Sum with Time Window (1 Hour = 3,600,000 ms)
    public void findTwoSumInWindow(List<Transaction> transactions, double target, long windowMillis) {
        // Sorted by time for sliding window optimization
        transactions.sort(Comparator.comparingLong(t -> t.timestamp));

        Map<Double, Long> map = new HashMap<>();
        for (Transaction tx : transactions) {
            double complement = target - tx.amount;
            if (map.containsKey(complement)) {
                if (tx.timestamp - map.get(complement) <= windowMillis) {
                    System.out.println("Suspicious Pair: " + tx.id);
                }
            }
            map.put(tx.amount, tx.timestamp);
        }
    }
}