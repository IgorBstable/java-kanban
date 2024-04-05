package model;

public class Node {

    public Node prev;
    public Task data;
    public Node next;

    public Node(Node prev, Task data, Node next) {
        this.prev = prev;
        this.data = data;
        this.next = next;
    }
}
