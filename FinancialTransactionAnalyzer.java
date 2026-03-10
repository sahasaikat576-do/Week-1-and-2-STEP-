import java.util.*;

class Transaction {

    int id;
    int amount;
    String merchant;
    String account;
    long time; // epoch minutes

    public Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class FinancialTransactionAnalyzer {

    List<Transaction> transactions;

    public FinancialTransactionAnalyzer(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // 1️⃣ Classic Two Sum
    public void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                System.out.println("TwoSum Pair → (" + other.id + ", " + t.id + ")");
            }

            map.put(t.amount, t);
        }
    }


    // 2️⃣ Two Sum within 1 hour
    public void twoSumTimeWindow(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                if (Math.abs(t.time - other.time) <= 60) {

                    System.out.println("TimeWindow Pair → (" + other.id + ", " + t.id + ")");
                }
            }

            map.put(t.amount, t);
        }
    }


    // 3️⃣ Duplicate Detection
    public void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());

            map.get(key).add(t);
        }

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                System.out.println("Duplicate Found → " + key);

                for (Transaction t : list) {
                    System.out.println("Transaction ID: " + t.id + " Account: " + t.account);
                }
            }
        }
    }


    // 4️⃣ K-Sum (recursive)
    public void findKSum(int k, int target) {

        List<Integer> current = new ArrayList<>();

        kSumHelper(0, k, target, current);
    }

    private void kSumHelper(int index, int k, int target, List<Integer> current) {

        if (k == 0 && target == 0) {

            System.out.println("KSum Match → " + current);
            return;
        }

        if (index >= transactions.size() || k < 0 || target < 0)
            return;

        Transaction t = transactions.get(index);

        current.add(t.id);

        kSumHelper(index + 1, k - 1, target - t.amount, current);

        current.remove(current.size() - 1);

        kSumHelper(index + 1, k, target, current);
    }


    public static void main(String[] args) {

        List<Transaction> list = new ArrayList<>();

        list.add(new Transaction(1, 500, "StoreA", "acc1", 600));
        list.add(new Transaction(2, 300, "StoreB", "acc2", 615));
        list.add(new Transaction(3, 200, "StoreC", "acc3", 630));
        list.add(new Transaction(4, 500, "StoreA", "acc4", 700));

        FinancialTransactionAnalyzer analyzer = new FinancialTransactionAnalyzer(list);

        System.out.println("Two Sum Target 500");
        analyzer.findTwoSum(500);

        System.out.println("\nTwo Sum within 1 hour");
        analyzer.twoSumTimeWindow(500);

        System.out.println("\nDuplicate Detection");
        analyzer.detectDuplicates();

        System.out.println("\nK Sum (k=3, target=1000)");
        analyzer.findKSum(3, 1000);
    }
}