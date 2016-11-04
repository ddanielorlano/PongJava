package pong2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class HighValues<T extends Serializable & Comparable<T>>
        implements Serializable, Iterable<T> {

   private List<T> internalList;

    public HighValues(int size) {
        internalList = new ArrayList<>(size);
    }

    public void add(T val) {
        internalList.add(val);
    }

    public void sortDescending() {

        Collections.sort(internalList); //Sorts the list into Ascending order

        Object[] tempArr = internalList.toArray();

        for (int highVal = tempArr.length - 1, lowVal = 0;
                highVal >= 0; highVal--, lowVal++) {

            internalList.set(lowVal, (T) tempArr[highVal]);// Reverses the ascending sort
        }

    }

    public T get(int index) {
        return internalList.get(index);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < internalList.size();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new IndexOutOfBoundsException();
                }
                return internalList.get(index++);
            }
        };
    }
}
