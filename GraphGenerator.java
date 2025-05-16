import java.util.List;
import java.util.Map;

public interface GraphGenerator {
    void generateRadarChart(Map<String, Object> userInput, List<Map<String, Object>> dataset);
    void displayGraph();
}
