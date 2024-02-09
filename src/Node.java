import java.io.Serializable;

public class Node implements Comparable<Node>, Serializable {
    private int frequency;
    private Node left;
    private Node right;

    public Node(int frequency) {
        this.frequency = frequency;
    }

    public Node(int frequency, Node left, Node right) {
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }
    public Node(Node left, Node right) {
        this.left = left;
        this.right = right;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    @Override
    public int compareTo(Node o) {
        return (this.frequency - o.getFrequency());
    }


}
