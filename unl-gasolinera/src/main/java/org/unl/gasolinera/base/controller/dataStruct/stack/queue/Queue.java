package org.unl.gasolinera.base.controller.dataStruct.stack.queue;

public class Queue <E> {
    private QueueImplementation<E> queue;
    public Queue(Integer top) {
        queue = new QueueImplementation<>(top);
    }
    public Boolean queue(E data) {
        try {
            queue.queue(data);
            return true;
        } catch (Exception e) {
            return false;
            // TODO: handle exception
        }
    }

    public E dequeue() {
        try {
            return queue.dequeue();
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    public Boolean isFullQueue() {
        return queue.isFullQueque();
    }

    public Integer top() {
        return queue.getTop();
    }

    public Integer size() {
        return queue.getLength();
    }
}