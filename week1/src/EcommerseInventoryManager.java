import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
public class EcommerseInventoryManager
{
    // 1. Thread-safe Stock Storage: ProductID -> Atomic Stock Count
    private static Map<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    // 2. Waiting List: ProductID -> Queue of UserIDs (FIFO)
    // Using LinkedBlockingQueue for thread-safe FIFO behavior
    private static Map<String, Queue<Integer>> waitingLists = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        String productId = "IPHONE15_256GB";

        // Initialize stock with 100 units
        inventory.put(productId, new AtomicInteger(100));
        waitingLists.put(productId, new ConcurrentLinkedQueue<>());

        // Simulate 105 purchase requests
        for (int i = 1; i <= 105; i++) {
            purchaseItem(productId, i);
        }
    }

    /**
     * Processes purchase requests in O(1) time while handling concurrency.
     */
    public static void purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);

        if (stock == null) {
            System.out.println("Product not found.");
            return;
        }

        // 3. Atomic Operation: Decrement only if stock is > 0
        // getAndUpdate is atomic, preventing overselling in high-traffic
        int currentStock = stock.get();

        if (currentStock > 0) {
            // Attempt to decrement
            if (stock.decrementAndGet() >= 0) {
                System.out.println("User " + userId + ": Success! Remaining: " + stock.get());
            } else {
                // If decrement pushed it below 0, fix it and move to waiting list
                stock.incrementAndGet();
                addToWaitingList(productId, userId);
            }
        } else {
            addToWaitingList(productId, userId);
        }
    }

    private static void addToWaitingList(String productId, int userId) {
        Queue<Integer> waitList = waitingLists.get(productId);
        waitList.add(userId);

        // Calculating position (this is simplified for console output)
        List<Integer> list = new ArrayList<>(waitList);
        int position = list.indexOf(userId) + 1;

        System.out.println("User " + userId + ": Added to waiting list, position #" + position);
    }

    public static void checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        System.out.println(productId + " -> " + (stock != null ? stock.get() : 0) + " units available");
    }
}
