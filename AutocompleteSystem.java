import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd = false;
}

public class AutocompleteSystem {

    private TrieNode root = new TrieNode();

    // query → frequency
    private HashMap<String, Integer> frequencyMap = new HashMap<>();


    // insert query into Trie
    public void insert(String query) {

        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEnd = true;

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);
    }


    // find node for prefix
    private TrieNode findPrefix(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c))
                return null;

            node = node.children.get(c);
        }

        return node;
    }


    // collect queries under prefix
    private void dfs(TrieNode node, String prefix, List<String> results) {

        if (node.isEnd)
            results.add(prefix);

        for (char c : node.children.keySet()) {
            dfs(node.children.get(c), prefix + c, results);
        }
    }


    // get top suggestions
    public List<String> search(String prefix) {

        TrieNode node = findPrefix(prefix);

        List<String> results = new ArrayList<>();

        if (node == null)
            return results;

        dfs(node, prefix, results);

        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) -> frequencyMap.get(a) - frequencyMap.get(b));

        for (String query : results) {

            pq.add(query);

            if (pq.size() > 10)
                pq.poll();
        }

        List<String> top = new ArrayList<>();

        while (!pq.isEmpty())
            top.add(pq.poll());

        Collections.reverse(top);

        return top;
    }


    // update frequency
    public void updateFrequency(String query) {
        insert(query);
    }


    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.insert("java tutorial");
        system.insert("javascript");
        system.insert("java download");
        system.insert("java tutorial");
        system.insert("java tutorial");
        system.insert("java 21 features");

        System.out.println("Suggestions for 'jav':");

        List<String> suggestions = system.search("jav");

        int rank = 1;

        for (String s : suggestions) {
            System.out.println(rank++ + ". " + s + " (" + system.frequencyMap.get(s) + " searches)");
        }

        system.updateFrequency("java 21 features");

        System.out.println("\nUpdated frequency of 'java 21 features': " +
                system.frequencyMap.get("java 21 features"));
    }
}