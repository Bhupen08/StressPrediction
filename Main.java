import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Load and process dataset once
        DataProcessorImpl dp = new DataProcessorImpl();
        StressPredictorImpl sp = new StressPredictorImpl();
        RecommendationEngineImpl re = new RecommendationEngineImpl();
        GraphGeneratorImpl graphGen = new GraphGeneratorImpl();

        List<Map<String, Object>> data = dp.loadDataset("trimmed_anxiety_dataset.csv");
        List<Map<String, Object>> filtered = dp.filterData(data);
        Map<String, List<Map<String, Object>>> split = dp.splitTrainTest(filtered, 0.8);
        List<Map<String, Object>> train = split.get("train");

        RandomForestModel forest = sp.createRandomForest(train, 10, 10, 30);
        System.out.println("📊 Model trained on dataset. Ready to predict!\n");

        boolean continuePrediction = true;
        while (continuePrediction) {
            Map<String, Object> input = new HashMap<>();

            System.out.println("🔍 Enter your health and lifestyle info:");

            System.out.print("Caffeine intake (mg/day) — e.g. ~90-110 for 1 cup, ~200 for 2 cups: ");
            input.put("Caffeine intake", scanner.nextInt());

            System.out.print("Heart Rate (bpm during stress/anxiety): ");
            input.put("Heart Rate", scanner.nextInt());

            System.out.print("Physical Activity (hours/week): ");
            input.put("Physical Activity", scanner.nextDouble());

            System.out.print("SleepHours (average hours/night): ");
            input.put("SleepHours", scanner.nextDouble());

            System.out.print("Age: ");
            input.put("Age", scanner.nextInt());

            System.out.print("Breathing Rate (breaths/min during attack): ");
            input.put("Breathing Rate", scanner.nextInt());

            System.out.print("Alcohol Consumption (drinks/week): ");
            input.put("Alcohol Consumption", scanner.nextInt());

            System.out.print("Severity of Anxiety Attack (1–10): ");
            input.put("Severity of Anxiety Attack", scanner.nextInt());

            System.out.print("Therapy Session (per month): ");
            input.put("Therapy Session", scanner.nextInt());

            // Prediction
            int predicted = sp.predictStressLevel(forest, input);
            input.put("Stress level", predicted);

            System.out.println("\n🧠 Predicted Stress Level: " + predicted);

            // Recommendations
            List<String> recs = re.generateRecommendations(input, filtered);
            System.out.println("\n💡 Recommendations:");
            recs.forEach(r -> System.out.println("- " + r));

            // Ask to continue
            System.out.print("\n🔁 Would you like to predict again? (y/n): ");
            String again = scanner.next();
            continuePrediction = again.equalsIgnoreCase("y");
            System.out.println();
        }
System.out.println("To generate a radar chart. Type python3 radar_chart.py");
        System.out.println("✅ Session ended. Thank you!");
    }
}
