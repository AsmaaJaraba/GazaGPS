package com.gazagps;

public class MyList <T> {
    T list[];
    int index = -1;

    public MyList(int capacity){
        list = (T[])(new Object[capacity]);
    }
    public void clear() {
        list = (T[])(new Object[list.length]);
        index = -1;
    }


    public void add(T data) {
        if (index < list.length-1) {
            list[index + 1] = data;
            index++;
        }
        else
            System.out.println("List is Full");


    }


    public int size() {
        return list.length;
    }


    public T remove(int i) {
        if (i >= 0 && i <= index) {
            T data = list[i];
            for (int k = i; k <= index - 1; k++) {
                list[k] = list[k + 1];
            }
        }
        list[index] = null;
        index--;
        return (T) list;

    }

    public boolean find(T data) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(data)) {
                return true;
            }
        }

        return false;

    }


    public void set(int i, T data) {
        if (i>=0 && i<=index) {
            list[i] = data;
        }
        else
            System.out.println("error in setting");


    }


    public void print() {
        for (int i = 0; i < list.length; i++) {
            System.out.print(list[i]+" ");
        }

    }


    public T get(int i) {
        if (i>=0 && i<=index) {
            return list[i];
        }
        System.out.println("error in get");
        return null;
    }


    public int count() {
        return index + 1;
    }

    public void insertAt(int i, T data) {
        if (i >= 0 && i <= index + 1 && index + 1 < list.length) {
            for (int j = index; j >= i; j--) {
                list[j + 1] = list[j];
            }
            list[i] = data;
            index++;
        }
        else
            System.out.println("error in insertAt, list may be full");

    }
}
