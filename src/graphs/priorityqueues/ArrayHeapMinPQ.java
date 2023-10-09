package graphs.priorityqueues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * @see ExtrinsicMinPQ
 */
public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code.
    static final int START_INDEX = 0; // changed from one to zero
    List<PriorityNode<T>> items; // list of items in order of priority
    Map<T, Integer> map; // storage for easy checking if the item is contained in our list
    int size; // size of PQ

    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        map = new HashMap<>();
        size = 0;
    }

    // Here's a method stub that may be useful. Feel free to change or remove it, if you wish.
    // You'll probably want to add more helper methods like this one to make your code easier to read.
    /**
     * A helper method for swapping the items at two indices of the array heap.
     */
    private void swap(int a, int b) {
        PriorityNode<T> item1 = items.get(a);
        PriorityNode<T> item2 = items.get(b);
        map.put(item1.getItem(), b);
        map.put(item2.getItem(), a);
        items.set(a, item2);
        items.set(b, item1);
    }

    private void percolatingUp(int index) {
        if (index >= 1 && items.get(index).getPriority() < items.get((index -1) / 2).getPriority()) {
            swap(index, (index - 1) / 2);
            percolatingUp((index - 1) / 2);
        }
    }

    private void percolatingDown(int index) {
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        if (leftChild < items.size() && rightChild > items.size() - 1) { // only left child
            double parentPrio = items.get(index).getPriority();
            double leftPrio = items.get(leftChild).getPriority();
            if (leftPrio < parentPrio) {
                swap(leftChild, index);
                percolatingDown(leftChild);
            }
        } else if (leftChild < items.size() && rightChild < items.size()) { // we have both child
            double parentPrio = items.get(index).getPriority();
            double leftPrio = items.get(leftChild).getPriority();
            double rightPrio = items.get(rightChild).getPriority();
            if (parentPrio < leftPrio && parentPrio < rightPrio) {
                return;
            } else if (leftPrio < rightPrio) {
                swap(leftChild, index);
                percolatingDown(leftChild);
            } else {
                swap(rightChild, index);
                percolatingDown(rightChild);
            }
        }
    }

    @Override
    public void add(T item, double priority) {
        if (contains(item) || item == null) {
            throw new IllegalArgumentException("The item already exists in the priority queue");
        }
        PriorityNode<T> newItem = new PriorityNode<>(item, priority);
        items.add(newItem);
        map.put(item, size);
        percolatingUp(size);
        size++;
    }

    @Override
    public boolean contains(T item) {
        return map.containsKey(item);
    }

    @Override
    public T peekMin() {
        if (items.isEmpty()) {
            throw new NoSuchElementException("there is no item to peek");
        }
        return items.get(START_INDEX).getItem();
    }

    @Override
    public T removeMin() {
        if (items.isEmpty()) {
            throw new NoSuchElementException("it is already empty");
        }
        PriorityNode<T> removedItem = items.get(START_INDEX);
        swap(0, items.size() - 1);
        items.remove(size - 1);
        map.remove(removedItem.getItem());
        percolatingDown(0);
        size--;
        return removedItem.getItem();
    }

    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException("The item does not exist in the priority queue");
        }
        int index = map.get(item);
        PriorityNode<T> newItem = new PriorityNode<>(item, priority);
        items.set(index, newItem);


        if (index != 0) { // if there is a parent
            double parentPrio = items.get((index - 1) / 2).getPriority();
            if (parentPrio > priority) {
                percolatingUp(index); // percolate up
            }
        }

        if (2*index+1 < items.size() && 2*index+2 < items.size()) { // if both children present
            double currentPrio = items.get(index).getPriority();
            double leftPrio = items.get(2 * index + 1).getPriority();
            double rightPrio = items.get(2 * index + 2).getPriority();
            if (leftPrio < currentPrio || rightPrio < currentPrio) {
                percolatingDown(index);
            }
        } else if (2*index+1 < items.size()) { // if only left is present
            double currentPrio = items.get(index).getPriority();
            double leftPrio = items.get(2 * index + 1).getPriority();
            if (leftPrio < currentPrio) {
                percolatingDown(index);
            }
        }
    }

    @Override
    public int size() {
        return this.size;
    }
}
