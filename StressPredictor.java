import java.util.List;
import java.util.Map;

public interface StressPredictor {
    DecisionTreeNode createDecisionTree(List<Map<String, Object>> trainingData, int maxDepth, int maxLeaves);
    RandomForestModel createRandomForest(List<Map<String, Object>> trainingData, int numTrees, int maxDepth, int maxLeaves);
    int predictStressLevel(RandomForestModel forest, Map<String, Object> userInput);
}
