import java.util.*;
import java.util.stream.Collectors;

public class TestComponent {
    public static void main(String[] args) {
        System.out.println("Component Test: Full Flow\n");

        DataProcessorImpl dp = new DataProcessorImpl();
        StressPredictorImpl sp = new StressPredictorImpl();
        RecommendationEngineImpl re = new RecommendationEngineImpl();

        // Load dataset
        List<Map<String, Object>> data = dp.loadDataset("trimmed_anxiety_dataset.csv");
        assert !data.isEmpty() : "Data failed to load.";
        System.out.println("Raw dataset size: " + data.size());

        // Print header info
        System.out.println("Keys in first row: " + data.get(0).keySet());

        // Filter and clean data
        List<Map<String, Object>> filtered = dp.filterData(data);
        System.out.println("Filtered dataset size: " + filtered.size());

        // Check valid labels
        long validLabels = filtered.stream()
            .filter(r -> r.get("Stress level") instanceof Integer)
            .count();
        System.out.println("Rows with valid numeric 'Stress level': " + validLabels);

        // Print class distribution
        System.out.println("Class distribution:");
        Map<Integer, Long> classCounts = filtered.stream()
            .collect(Collectors.groupingBy(
                r -> (Integer) r.get("Stress level"),
                Collectors.counting()
            ));
        classCounts.forEach((label, count) ->
            System.out.println(" - Class " + label + ": " + count + " instances")
        );

        // Split into training and test sets
        Map<String, List<Map<String, Object>>> split = dp.splitTrainTest(filtered, 0.8);
        List<Map<String, Object>> train = split.get("train");
        List<Map<String, Object>> test = split.get("test");

        System.out.println("Train set size: " + train.size());
        System.out.println("Test set size: " + test.size());
        assert !train.isEmpty() : "Training data missing.";

        // Train Random Forest
        RandomForestModel forest = sp.createRandomForest(train, 25, 10, 30);

        // Train Decision Tree
        DecisionTreeModel treeModel = new DecisionTreeModel(20, 100);
        DecisionTreeNode treeRoot = treeModel.buildTree(train, 0);

        // === RANDOM FOREST EVALUATION ===
int[][] rfCM = new int[2][2]; // [actual][predicted]
for (Map<String, Object> row : test) {
    int actual = (Integer) row.get("Stress level");
    int predicted = sp.predictStressLevel(forest, row);
    rfCM[actual][predicted]++;
}

int rfTP = rfCM[1][1];
int rfFP = rfCM[0][1];
int rfFN = rfCM[1][0];
int rfTN = rfCM[0][0];

double rfAcc = (double) (rfTP + rfTN) / test.size();
double rfPrec0 = (rfTN + rfFN) == 0 ? 0 : (double) rfTN / (rfTN + rfFN);
double rfRec0  = (rfTN + rfFP) == 0 ? 0 : (double) rfTN / (rfTN + rfFP);
double rfF10   = (rfPrec0 + rfRec0) == 0 ? 0 : 2 * rfPrec0 * rfRec0 / (rfPrec0 + rfRec0);

double rfPrec1 = (rfTP + rfFP) == 0 ? 0 : (double) rfTP / (rfTP + rfFP);
double rfRec1  = (rfTP + rfFN) == 0 ? 0 : (double) rfTP / (rfTP + rfFN);
double rfF11   = (rfPrec1 + rfRec1) == 0 ? 0 : 2 * rfPrec1 * rfRec1 / (rfPrec1 + rfRec1);

// Confusion Matrix
System.out.println("\n📊 Random Forest Confusion Matrix:");
System.out.println("           Predicted");
System.out.println("           0     1");
System.out.printf("Actual 0 | %5d %5d\n", rfTN, rfFP);
System.out.printf("Actual 1 | %5d %5d\n", rfFN, rfTP);

// Accuracy + Table
System.out.printf("\n🔍 Random Forest Accuracy: %.2f%%\n", rfAcc * 100);
System.out.println("\nClass | Precision | Recall | F1-score");
System.out.printf("  0   |   %.3f    | %.3f  |  %.3f\n", rfPrec0, rfRec0, rfF10);
System.out.printf("  1   |   %.3f    | %.3f  |  %.3f\n", rfPrec1, rfRec1, rfF11);

        // Evaluate Decision Tree
        int dtTP = 0, dtTN = 0, dtFP = 0, dtFN = 0;
        for (Map<String, Object> row : test) {
            int actual = (Integer) row.get("Stress level");
            int predicted = treeModel.predict(treeRoot, row);
            if (predicted == 1 && actual == 1) dtTP++;
            else if (predicted == 0 && actual == 0) dtTN++;
            else if (predicted == 1 && actual == 0) dtFP++;
            else if (predicted == 0 && actual == 1) dtFN++;
        }
        double dtAcc = (double) (dtTP + dtTN) / test.size();
        double dtPrecision = dtTP + dtFP == 0 ? 0 : (double) dtTP / (dtTP + dtFP);
        double dtRecall = dtTP + dtFN == 0 ? 0 : (double) dtTP / (dtTP + dtFN);
        double dtF1 = dtPrecision + dtRecall == 0 ? 0 : 2 * dtPrecision * dtRecall / (dtPrecision + dtRecall);

        System.out.printf("\n🌳 Decision Tree Accuracy: %.2f%%\n", dtAcc * 100);
        // HIGH STRESS sample input
        Map<String, Object> input = new HashMap<>();
        input.put("Caffeine intake", 400);         // high caffeine
        input.put("Heart Rate", 190);              // elevated HR
        input.put("Physical Activity", 1.0);       // low activity
        input.put("SleepHours", 2.0);              // poor sleep
        input.put("Age", 28);
        input.put("Breathing Rate", 16);           // fast breathing
        input.put("Alcohol Consumption", 8);       // high alcohol
        input.put("Severity of Anxiety Attack", 9);// severe attack
        input.put("Therapy Session", 1);           // no therapy
        input.put("Diet Quality", 2);              // poor diet

        // Predict for user input
        int predicted = sp.predictStressLevel(forest, input);
        System.out.println("\n📊 Predicted Stress Level for High-Stress Profile: " + predicted);
        input.put("Stress level", predicted); // Needed for recommendations

        // Recommendations
        List<String> recs = re.generateRecommendations(input, filtered);
        System.out.println("🧠 Recommendations:");
        recs.forEach(r -> System.out.println("- " + r));
    }
}
