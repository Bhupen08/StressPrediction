import java.util.*;

public class RecommendationEngineImpl implements RecommendationEngine {

    @Override
    public List<String> generateRecommendations(Map<String, Object> userInput, List<Map<String, Object>> dataset) {
        int userStress = (int) userInput.get("Stress level");

        // Filter for users with better (lower) stress
        List<Map<String, Object>> betterUsers = new ArrayList<>();
        for (Map<String, Object> other : dataset) {
            int otherStress = (int) other.get("Stress level");
            if (otherStress < userStress) {
                betterUsers.add(other);
            }
        }

        // If no better users found
        if (betterUsers.isEmpty()) {
            return List.of("You're already in the lowest stress group. Keep it up!");
        }

        // Find closest N users based on feature similarity
        int k = 5;
        PriorityQueue<Map.Entry<Map<String, Object>, Double>> pq = new PriorityQueue<>(
            Map.Entry.comparingByValue()
        );

        for (Map<String, Object> other : betterUsers) {
            double distance = computeDistance(userInput, other);
            pq.offer(new AbstractMap.SimpleEntry<>(other, distance));
        }

        // Select top k
        List<Map<String, Object>> topK = new ArrayList<>();
        for (int i = 0; i < k && !pq.isEmpty(); i++) {
            topK.add(pq.poll().getKey());
        }

        // Analyze differences
        Map<String, Integer> recommendationCounts = new HashMap<>();

        for (Map<String, Object> peer : topK) {
            for (String key : userInput.keySet()) {
                if (key.equals("Stress level")) continue;
        
                Object userVal = userInput.get(key);
                Object peerVal = peer.get(key);
        
                if (userVal instanceof Number && peerVal instanceof Number) {
                    double uv = ((Number) userVal).doubleValue();
                    double pv = ((Number) peerVal).doubleValue();
                    if (Math.abs(uv - pv) > 1.0) {
                        if (!(userStress == 1 && key.equals("Age"))) {
                            recommendationCounts.put(key, recommendationCounts.getOrDefault(key, 0) + 1);
                        }
                    }
                } else if (!Objects.equals(userVal, peerVal)) {
                    if (!(userStress == 1 && key.equals("Age"))) {
                        recommendationCounts.put(key, recommendationCounts.getOrDefault(key, 0) + 1);
                    }
                }
            }
        }

        // Sort most common differing features
        List<String> recommendations = new ArrayList<>();
        recommendationCounts.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .limit(4)
            .forEach(e -> recommendations.add("Consider adjusting: " + e.getKey()));

        if (recommendations.isEmpty()) {
            recommendations.add("Your lifestyle is already similar to lower-stress users.");
        }

        return recommendations;
    }

    private double computeDistance(Map<String, Object> a, Map<String, Object> b) {
        double sum = 0.0;
        for (String key : a.keySet()) {
            if (key.equals("Stress level")) continue;
            if (a.get(key) instanceof Number && b.get(key) instanceof Number) {
                double valA = ((Number) a.get(key)).doubleValue();
                double valB = ((Number) b.get(key)).doubleValue();
                sum += Math.pow(valA - valB, 2);
            }
        }
        return Math.sqrt(sum);
    }
}
