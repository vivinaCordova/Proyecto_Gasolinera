package org.unl.gasolinera.base.controller.dao;

import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;

public interface InterfaceDao <T>{
    public LinkedList<T> listAll();
    public void persist(T obj) throws Exception;
    public void update(T obj, Integer pos) throws Exception;
    public void update_by_id(T obj, Integer id) throws Exception;
    public T get(Integer id) throws Exception;
    public void delete(Integer id) throws Exception;
}