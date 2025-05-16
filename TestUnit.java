import java.util.*;

public class TestUnit {
    public static void main(String[] args) {
        testDataProcessor();
        testStressPredictor();
        testRecommendationEngine();
        System.out.println("\nAll unit tests completed.");
    }

    public static void testDataProcessor() {
        System.out.println("Testing DataProcessorImpl...");

        DataProcessorImpl dp = new DataProcessorImpl();
        List<Map<String, Object>> raw = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("Gender", "Male");
        row.put("Occupation", "Doctor");
        row.put("Stress level", 9);
        raw.add(row);

        List<Map<String, Object>> filtered = dp.filterData(raw);

        System.out.println("Expected: True, Evaluating: filtered.size() == 1");
        assert filtered.size() == 1 : "Filtered row missing";

        System.out.println("Expected: True, Evaluating: filtered.get(0).get(\"Stress level\").equals(2)");
        assert filtered.get(0).get("Stress level").equals(2) : "Incorrect stress level category";

        System.out.println("Expected: True, Evaluating: filtered.get(0).get(\"Gender\").equals(0)");
        assert filtered.get(0).get("Gender").equals(0) : "Gender conversion failed";

        System.out.println("Expected: True, Evaluating: filtered.get(0).get(\"Occupation\").equals(0)");
        assert filtered.get(0).get("Occupation").equals(0) : "Occupation conversion failed";

        System.out.println("✅ DataProcessorImpl passed.");
    }

    public static void testStressPredictor() {
        System.out.println("Testing StressPredictorImpl...");

        List<Map<String, Object>> trainData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("Age", 20 + i);
            row.put("Gender", 0);
            row.put("Occupation", 0);
            row.put("Stress level", i % 3);
            trainData.add(row);
        }

        StressPredictorImpl sp = new StressPredictorImpl();
        RandomForestModel forest = sp.createRandomForest(trainData, 3, 5, 10);

        Map<String, Object> input = new HashMap<>();
        input.put("Age", 23);
        input.put("Gender", 0);
        input.put("Occupation", 0);

        int prediction = sp.predictStressLevel(forest, input);

        System.out.println("Expected: True, Evaluating: prediction >= 0 && prediction <= 2");
        assert prediction >= 0 && prediction <= 2 : "Prediction out of expected range";

        System.out.println("✅ StressPredictorImpl passed.");
    }

    public static void testRecommendationEngine() {
        System.out.println("Testing RecommendationEngineImpl...");

        RecommendationEngineImpl re = new RecommendationEngineImpl();

        Map<String, Object> user = new HashMap<>();
        user.put("Stress level", 2);
        user.put("Age", 30);
        user.put("SleepHours", 5.0);
        user.put("Gender", 1);
        user.put("Occupation", 1);

        List<Map<String, Object>> dataset = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Map<String, Object> peer = new HashMap<>();
            peer.put("Stress level", 1); // better
            peer.put("Age", 30);
            peer.put("SleepHours", 8.0);
            peer.put("Gender", 1);
            peer.put("Occupation", 1);
            dataset.add(peer);
        }

        List<String> recs = re.generateRecommendations(user, dataset);

        System.out.println("Expected: True, Evaluating: !recs.isEmpty()");
        assert !recs.isEmpty() : "Recommendations missing";

        System.out.println("✅ RecommendationEngineImpl passed.");
    }
}
