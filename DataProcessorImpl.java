import java.io.*;
import java.util.*;

public class DataProcessorImpl implements DataProcessor {

    @Override
    public List<Map<String, Object>> loadDataset(String filePath) {
        List<Map<String, Object>> dataset = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine(); // first line
            if (headerLine == null) return dataset;

            String[] headers = headerLine.split(",");
            String line;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, Object> row = new HashMap<>();

                for (int i = 0; i < headers.length && i < values.length; i++) {
                    String key = headers[i].trim();
                    String value = values[i].trim();
                    row.put(key, parseValue(value));
                }

                dataset.add(row);
            }

        } catch (IOException e) {
            System.err.println("Failed to load dataset: " + e.getMessage());
        }

        return dataset;
    }

    @Override
public List<Map<String, Object>> filterData(List<Map<String, Object>> rawData) {
    List<Map<String, Object>> filtered = new ArrayList<>();

    for (Map<String, Object> row : rawData) {
        Object rawStress = row.get("Stress level");
        if (!(rawStress instanceof Integer)) continue;
        int stress = (Integer) rawStress;

        // Binary stress categorization: 0 = low/moderate, 1 = high
        int mappedLabel;
        if (stress >= 1 && stress <= 5) mappedLabel = 0;
        else if (stress >= 6 && stress <= 10) mappedLabel = 1;
        else continue;

        Map<String, Object> newRow = new HashMap<>();
        newRow.put("Stress level", mappedLabel);

        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Keep only selected features
            switch (key) {
                case "Caffeine intake":
                case "Heart Rate":
                case "Physical Activity":
                case "SleepHours":
                case "Age":
                case "Breathing Rate":
                case "Alcohol Consumption":
                case "Severity of Anxiety Attack":
                case "Therapy Session":
                case "Diet Quality":
                    newRow.put(key, value);
                    break;

                case "Stress level":
                    // already handled
                    break;

                default:
                    // ignore unused features
                    break;
            }
        }

        filtered.add(newRow);
    }

    return filtered;
}

    



    @Override
    public Map<String, List<Map<String, Object>>> splitTrainTest(List<Map<String, Object>> filteredData, double trainRatio) {
        Collections.shuffle(filteredData, new Random());

        int trainSize = (int) (filteredData.size() * trainRatio);
        List<Map<String, Object>> trainSet = new ArrayList<>(filteredData.subList(0, trainSize));
        List<Map<String, Object>> testSet = new ArrayList<>(filteredData.subList(trainSize, filteredData.size()));

        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("train", trainSet);
        result.put("test", testSet);
        return result;
    }

    @Override
    public List<List<Map<String, Object>>> bootstrapData(List<Map<String, Object>> trainData, int numberOfSamples) {
        List<List<Map<String, Object>>> bootstrappedSets = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < numberOfSamples; i++) {
            List<Map<String, Object>> sample = new ArrayList<>();
            for (int j = 0; j < trainData.size(); j++) {
                int index = rand.nextInt(trainData.size());
                sample.add(new HashMap<>(trainData.get(index))); // deep copy
            }
            bootstrappedSets.add(sample);
        }

        return bootstrappedSets;
    }

    // Helper method to infer value types
    private Object parseValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
    
        value = value.trim().toLowerCase();
    
        // Convert yes/no to boolean
        if (value.equals("yes")) return true;
        if (value.equals("no")) return false;
    
        // Try integer first
        try {
            if (!value.contains(".")) return Integer.parseInt(value);
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return value; // fallback
        }
    }
    
}
