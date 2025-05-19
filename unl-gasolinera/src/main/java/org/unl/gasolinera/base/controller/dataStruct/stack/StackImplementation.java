package org.unl.gasolinera.base.controller.dataStruct.stack;

import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;

public class StackImplementation<E> extends LinkedList<E>{
    private Integer top;

    public Integer getTop() {
        return this.top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public StackImplementation(Integer top){
        this.top=top;
    }

    protected Boolean isFullStack(){
        return this.top >=getLength();
    }

    protected void push(E info) throws Exception{
        if(!isFullStack()){
            add(info, 0);
        }else{
            throw new ArrayIndexOutOfBoundsException("Stack full");
        }
    }

    protected E pop() throws Exception{
        return deleteFirst();
    }

}