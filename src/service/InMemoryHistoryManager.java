package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> history = new HashMap<>();
    private Node first;
    private Node last;

    private Node linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        return newNode;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.data = null;
    }

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            Node node = history.get(task.getId());
            removeNode(node);
            history.put(task.getId(), linkLast(task));
        } else {
            history.put(task.getId(), linkLast(task));
        }
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            Node node = history.get(id);
            removeNode(node);
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (Node x = first; x != null; x = x.next) {
            taskArrayList.add(x.data);
        }
        return taskArrayList;
    }

    private static class Node {

        public Node prev;
        public Task data;
        public Node next;

        public Node(Node prev, Task data, Node next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }
}
