import java.util.*;
import java.util.stream.Collectors;

public class DecisionTreeModel {

    private int maxDepth;
    private int maxLeaves;

    public DecisionTreeModel(int maxDepth, int maxLeaves) {
        this.maxDepth = maxDepth;
        this.maxLeaves = maxLeaves;
    }

    public DecisionTreeNode buildTree(List<Map<String, Object>> data, int depth) {
        DecisionTreeNode node = new DecisionTreeNode();

        if (data.isEmpty()) {
            node.is_leaf = true;
            node.predicted_class = -1;
            return node;
        }

        Object firstVal = data.get(0).get("Stress level");
        if (!(firstVal instanceof Integer)) {
            node.is_leaf = true;
            node.predicted_class = -1;
            return node;
        }

        int firstLabel = (Integer) firstVal;
        boolean allSame = data.stream()
            .allMatch(row -> Objects.equals(row.get("Stress level"), firstLabel));

        if (allSame) {
            node.is_leaf = true;
            node.predicted_class = firstLabel;
            return node;
        }

        if (depth >= maxDepth) {
            node.is_leaf = true;
            node.predicted_class = majorityClass(data);
            return node;
        }

        SplitResult split = findBestSplit(data);

        if (split == null || split.left.isEmpty() || split.right.isEmpty()) {
            node.is_leaf = true;
            node.predicted_class = majorityClass(data);
            return node;
        }

        node.split_feature = split.feature;
        node.threshold = split.threshold;
        node.left = buildTree(split.left, depth + 1);
        node.right = buildTree(split.right, depth + 1);
        return node;
    }

    public int predict(DecisionTreeNode root, Map<String, Object> input) {
        if (root.isLeaf()) return root.predicted_class;

        Object obj = input.get(root.split_feature);
        if (!(obj instanceof Number)) return root.predicted_class; // fallback

        double value = ((Number) obj).doubleValue();
        return (value <= root.threshold)
            ? predict(root.left, input)
            : predict(root.right, input);
    }

    private int majorityClass(List<Map<String, Object>> data) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (Map<String, Object> row : data) {
            Object label = row.get("Stress level");
            if (label instanceof Integer) {
                int val = (Integer) label;
                freq.put(val, freq.getOrDefault(val, 0) + 1);
            }
        }
        return freq.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(-1);
    }

    private SplitResult findBestSplit(List<Map<String, Object>> data) {
        double bestGini = Double.MAX_VALUE;
        String bestFeature = null;
        double bestThreshold = 0;

        Set<String> features = data.get(0).keySet().stream()
            .filter(f -> !f.equals("Stress level") && data.get(0).get(f) instanceof Number)
            .collect(Collectors.toSet());

        for (String feature : features) {
            Set<Double> thresholds = data.stream()
                .map(row -> (Number) row.get(feature))
                .filter(Objects::nonNull)
                .map(Number::doubleValue)
                .collect(Collectors.toSet());

            List<Double> sorted = new ArrayList<>(thresholds);
            Collections.sort(sorted);

            for (int i = 1; i < sorted.size(); i++) {
                double threshold = (sorted.get(i - 1) + sorted.get(i)) / 2.0;
                double gini = computeSplitGini(data, feature, threshold);

                if (gini < bestGini) {
                    bestGini = gini;
                    bestFeature = feature;
                    bestThreshold = threshold;
                }
            }
        }

        if (bestFeature == null) return null;

        // Now split the data
        List<Map<String, Object>> left = new ArrayList<>();
        List<Map<String, Object>> right = new ArrayList<>();

        for (Map<String, Object> row : data) {
            Object val = row.get(bestFeature);
            if (!(val instanceof Number)) continue;

            double num = ((Number) val).doubleValue();
            if (num <= bestThreshold) left.add(row);
            else right.add(row);
        }

        if (left.isEmpty() || right.isEmpty()) return null;
        return new SplitResult(bestFeature, bestThreshold, left, right);
    }

    private double computeSplitGini(List<Map<String, Object>> data, String feature, double threshold) {
        List<Map<String, Object>> left = new ArrayList<>();
        List<Map<String, Object>> right = new ArrayList<>();

        for (Map<String, Object> row : data) {
            Object val = row.get(feature);
            if (!(val instanceof Number)) continue;

            double num = ((Number) val).doubleValue();
            if (num <= threshold) left.add(row);
            else right.add(row);
        }

        double giniLeft = computeGini(left);
        double giniRight = computeGini(right);
        int total = left.size() + right.size();

        return ((double) left.size() / total) * giniLeft +
               ((double) right.size() / total) * giniRight;
    }

    private double computeGini(List<Map<String, Object>> subset) {
        if (subset.isEmpty()) return 0.0;

        Map<Integer, Integer> freq = new HashMap<>();
        int total = 0;

        for (Map<String, Object> row : subset) {
            Object val = row.get("Stress level");

            if (!(val instanceof Integer)) {
                System.out.println("Warning: Invalid or missing 'Stress level': " + val);
                continue;
            }

            int label = (Integer) val;
            freq.put(label, freq.getOrDefault(label, 0) + 1);
            total++;
        }

        if (total == 0) {
            throw new IllegalStateException("computeGini(): no valid 'Stress level' found.");
        }

        double gini = 1.0;
        for (int count : freq.values()) {
            double prob = (double) count / total;
            gini -= prob * prob;
        }

        return gini;
    }

    private static class SplitResult {
        String feature;
        double threshold;
        List<Map<String, Object>> left;
        List<Map<String, Object>> right;

        SplitResult(String feature, double threshold, List<Map<String, Object>> left, List<Map<String, Object>> right) {
            this.feature = feature;
            this.threshold = threshold;
            this.left = left;
            this.right = right;
        }
    }
}
