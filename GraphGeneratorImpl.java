import java.io.*;
import java.util.*;

public class GraphGeneratorImpl implements GraphGenerator {

    private String csvFile = "radar_data.csv";

    @Override
    public void generateRadarChart(Map<String, Object> userInput, List<Map<String, Object>> dataset) {
        // Compute average values for each input feature
        Map<String, Double> featureSums = new HashMap<>();
        int count = dataset.size();

        for (Map<String, Object> row : dataset) {
            for (String key : userInput.keySet()) {
                if (key.equals("Stress level")) continue;
                Object val = row.get(key);
                if (val instanceof Number) {
                    featureSums.put(key, featureSums.getOrDefault(key, 0.0) + ((Number) val).doubleValue());
                }
            }
        }

        Map<String, Double> featureAverages = new HashMap<>();
        for (String key : featureSums.keySet()) {
            featureAverages.put(key, featureSums.get(key) / count);
        }

        // Write radar_data.csv
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            writer.println("Attribute,UserValue,AverageValue");

            for (String key : userInput.keySet()) {
                if (key.equals("Stress level")) continue;

                Object userVal = userInput.get(key);
                double avgVal = featureAverages.getOrDefault(key, 0.0);
                writer.printf("%s,%.2f,%.2f\n", key,
                        ((Number) userVal).doubleValue(), avgVal);
            }

            System.out.println("✅ Radar data written to " + csvFile);
        } catch (IOException e) {
            System.err.println("❌ Failed to write radar data CSV: " + e.getMessage());
        }
    }

    @Override
    public void displayGraph() {
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "radar_chart.py");
            pb.inheritIO(); // To display matplotlib output in terminal
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            System.err.println("❌ Error running radar_chart.py: " + e.getMessage());
        }
    }
}
