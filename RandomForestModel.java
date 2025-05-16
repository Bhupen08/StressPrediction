import java.util.*;
import java.util.stream.Collectors;

public class RandomForestModel {

    private List<DecisionTreeNode> trees = new ArrayList<>();
    private int numTrees;
    private int maxDepth;
    private int maxLeaves;

    public RandomForestModel(int numTrees, int maxDepth, int maxLeaves) {
        this.numTrees = numTrees;
        this.maxDepth = maxDepth;
        this.maxLeaves = maxLeaves;
    }

    public void train(List<Map<String, Object>> trainingData) {
        for (int i = 0; i < numTrees; i++) {
            List<Map<String, Object>> bootstrapSample = bootstrap(trainingData);
            DecisionTreeModel treeBuilder = new DecisionTreeModel(maxDepth, maxLeaves);
            DecisionTreeNode root = treeBuilder.buildTree(bootstrapSample, 0);
            trees.add(root);

            // Progress bar
            int percent = (int) ((i + 1) / (double) numTrees * 100);
            System.out.print("\rTraining trees: [" +
                "=".repeat(percent / 2) +
                " ".repeat(50 - percent / 2) +
                "] " + percent + "%");
        }
        System.out.println("\nRandom Forest training complete.");
    }

    public int predict(Map<String, Object> input) {
        Map<Integer, Integer> voteCounts = new HashMap<>();

        for (DecisionTreeNode root : trees) {
            DecisionTreeModel treeModel = new DecisionTreeModel(maxDepth, maxLeaves);
            int prediction = treeModel.predict(root, input);
            voteCounts.put(prediction, voteCounts.getOrDefault(prediction, 0) + 1);
        }

        return voteCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .get()
            .getKey();
    }

    private List<Map<String, Object>> bootstrap(List<Map<String, Object>> data) {
        List<Map<String, Object>> validData = data.stream()
            .filter(r -> r.containsKey("Stress level") && r.get("Stress level") instanceof Integer)
            .collect(Collectors.toList());

        if (validData.isEmpty()) {
            throw new IllegalStateException("No valid rows with 'Stress level' found during bootstrapping.");
        }

        List<Map<String, Object>> sample = new ArrayList<>();
        Random rand = new Random();
        int size = data.size();

        while (sample.size() < size) {
            int index = rand.nextInt(validData.size());
            sample.add(new HashMap<>(validData.get(index)));
        }

        return sample;
    }

    public List<DecisionTreeNode> getTrees() {
        return trees;
    }
}
