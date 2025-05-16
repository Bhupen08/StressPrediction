import java.util.List;
import java.util.Map;

public interface RecommendationEngine {
    List<String> generateRecommendations(Map<String, Object> userInput, List<Map<String, Object>> dataset);
}
