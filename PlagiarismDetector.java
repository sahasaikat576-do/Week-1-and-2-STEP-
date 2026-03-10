import java.util.*;

public class PlagiarismDetector {

    // n-gram → documents containing it
    private HashMap<String, Set<String>> index = new HashMap<>();

    private int N = 5; // 5-gram


    // break document into n-grams
    private List<String> getNGrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }


    // add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = getNGrams(text);

        for (String gram : ngrams) {

            index.putIfAbsent(gram, new HashSet<>());
            index.get(gram).add(docId);
        }
    }


    // analyze new document
    public void analyzeDocument(String docId, String text) {

        List<String> ngrams = getNGrams(text);

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (index.containsKey(gram)) {

                for (String otherDoc : index.get(gram)) {

                    matchCount.put(otherDoc,
                            matchCount.getOrDefault(otherDoc, 0) + 1);
                }
            }
        }

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        for (String otherDoc : matchCount.keySet()) {

            int matches = matchCount.get(otherDoc);

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println(
                    "Found " + matches +
                    " matching n-grams with \"" + otherDoc + "\""
            );

            System.out.println(
                    "Similarity: " + String.format("%.2f", similarity) + "%"
            );

            if (similarity > 60)
                System.out.println("PLAGIARISM DETECTED");
            else if (similarity > 10)
                System.out.println("Suspicious similarity");

            System.out.println();
        }
    }


    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String doc1 = "data structures and algorithms are important for computer science students";
        String doc2 = "learning data structures and algorithms is important for coding interviews";

        detector.addDocument("essay_089.txt", doc1);
        detector.addDocument("essay_092.txt", doc2);

        String newEssay = "data structures and algorithms are important for coding interviews and students";

        detector.analyzeDocument("essay_123.txt", newEssay);
    }
}