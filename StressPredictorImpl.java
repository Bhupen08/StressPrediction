import java.util.List;
import java.util.Map;

public class StressPredictorImpl implements StressPredictor {

    @Override
    public DecisionTreeNode createDecisionTree(List<Map<String, Object>> trainingData, int maxDepth, int maxLeaves) {
        DecisionTreeModel dt = new DecisionTreeModel(maxDepth, maxLeaves);
        return dt.buildTree(trainingData, 0);
    }

    @Override
    public RandomForestModel createRandomForest(List<Map<String, Object>> trainingData, int numTrees, int maxDepth, int maxLeaves) {
        RandomForestModel forest = new RandomForestModel(numTrees, maxDepth, maxLeaves);
        forest.train(trainingData);
        return forest;
    }

    @Override
    public int predictStressLevel(RandomForestModel forest, Map<String, Object> userInput) {
        return forest.predict(userInput);
    }
}
