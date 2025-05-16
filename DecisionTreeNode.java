public class DecisionTreeNode {
    public boolean is_leaf;
    public int predicted_class;

    public String split_feature;
    public double threshold;

    public DecisionTreeNode left;
    public DecisionTreeNode right;

    public boolean isLeaf() {
        return is_leaf;
    }
}
