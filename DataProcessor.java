import java.util.List;
import java.util.Map;

public interface DataProcessor {
    List<Map<String, Object>> loadDataset(String filePath);
    List<Map<String, Object>> filterData(List<Map<String, Object>> rawData);
    Map<String, List<Map<String, Object>>> splitTrainTest(List<Map<String, Object>> filteredData, double trainRatio);
    List<List<Map<String, Object>>> bootstrapData(List<Map<String, Object>> trainData, int numberOfSamples);
}
