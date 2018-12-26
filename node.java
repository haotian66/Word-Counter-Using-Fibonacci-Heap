public class node{
    // this is a class for a node in the fibonacci heap, label is the corresponding word of the node, value is the frequency of the word.
    // Other parameters are defined according to the feature of fibonacci heap which are easy to understand
    node children, parent, left, right;
    int degree;
    int value;
    String label;
    boolean childcut;
    public node(String label, int value){
        this.label = label;
        this.value = value;
        this.parent = null;
        this.children = null;
        this.left = this;
        this.right = this;
        this.childcut = false;
        this.degree = 0;
    }
}