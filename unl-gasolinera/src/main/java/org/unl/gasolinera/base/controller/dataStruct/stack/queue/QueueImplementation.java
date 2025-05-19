package org.unl.gasolinera.base.controller.dataStruct.stack.queue;

import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;

public class QueueImplementation<E> extends LinkedList<E> {
    private Integer top;

    public Integer getTop() {
        return this.top;
    }

    public QueueImplementation(Integer top){
        this.top = top;
    }

    protected Boolean isFullQueque() {
        return this.top >= getLength();
    }

    protected void queue(E info) throws Exception {
        if(!isFullQueque()) {
            add(info);
        } else {
            throw new ArrayIndexOutOfBoundsException("Queque full");
        }
    }
    protected E dequeue() throws Exception {       
        return deleteFirst();
        
    }
}