package service;

import model.Task;
import model.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Node> nodeList = new ArrayList<>();
    private final Map<Integer, Node> history = new HashMap<>();
    private Node head = new Node(null, null, null);
    private Node tail = new Node(null, null, null);

    private Node linkLast(Task task) {
        if (head.data == null) {
            head.data = task;
            head.next = tail;
            getTasks(head);
            return head;
        } else if (tail.data == null) {
            tail.prev = head;
            tail.data = task;
            getTasks(tail);
            return tail;
        } else {
            Node oldTail = tail;
            tail = new Node(oldTail, task, null);
            oldTail.next = tail;
            getTasks(tail);
            return tail;
        }
    }

    private void getTasks(Node node) {
        nodeList.add(node);
    }

    private void removeNode(Node node) {
        if (node == head) {
            head = node.next;
            head.prev = null;
            nodeList.remove(node);
        } else if (node == tail) {
            tail = node.prev;
            tail.next = null;
            nodeList.remove(node);
        } else {
            Node prevNode = node.prev;
            Node nextNode = node.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
            nodeList.remove(node);
        }
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
        List<Task> tasks = new ArrayList<>();
        for (Node node : nodeList) {
            tasks.add(node.data);
        }
        return tasks;
    }


}

