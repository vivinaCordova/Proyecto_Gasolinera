package org.unl.gasolinera.base.controller.dataStruct.stack;

public class Stack <E>{
    private StackImplementation<E> stack;
    public Stack(Integer top){
        stack= new StackImplementation<E>(top);
    }

    public Boolean push(E data){
        try {
            stack.push(data);
            return true;
        } catch (Exception e) {
            return false;
            // TODO: handle exception
        }
    }

    public E pop(){
        try {
            return pop();
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }

    public Boolean isFullStack(){
        return stack.isFullStack();
    }

    public Integer top(){
        return stack.getTop();
    }

    public Integer size(){
        return stack.getLength();
    }
    
}
