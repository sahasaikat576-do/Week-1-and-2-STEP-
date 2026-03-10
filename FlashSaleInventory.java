import java.util.*;

public class FlashSaleInventory {

    // productId -> stockCount
    private HashMap<String, Integer> stock = new HashMap<>();

    // productId -> waiting list (FIFO)
    private HashMap<String, LinkedHashMap<Integer, Boolean>> waitingList = new HashMap<>();


    // add product with stock
    public void addProduct(String productId, int quantity) {
        stock.put(productId, quantity);
        waitingList.put(productId, new LinkedHashMap<>());
    }


    // check stock availability
    public int checkStock(String productId) {
        return stock.getOrDefault(productId, 0);
    }


    // purchase item (thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {

        int currentStock = stock.getOrDefault(productId, 0);

        if (currentStock > 0) {
            stock.put(productId, currentStock - 1);
            return "Success, " + (currentStock - 1) + " units remaining";
        }

        // add to waiting list
        LinkedHashMap<Integer, Boolean> queue = waitingList.get(productId);
        queue.put(userId, true);

        return "Added to waiting list, position #" + queue.size();
    }


    public static void main(String[] args) {

        FlashSaleInventory system = new FlashSaleInventory();

        system.addProduct("IPHONE15_256GB", 100);

        System.out.println("Stock: " + system.checkStock("IPHONE15_256GB"));

        System.out.println(system.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 67890));

        // simulate stock running out
        for (int i = 0; i < 98; i++) {
            system.purchaseItem("IPHONE15_256GB", 10000 + i);
        }

        // now waiting list
        System.out.println(system.purchaseItem("IPHONE15_256GB", 99999));
    }
}