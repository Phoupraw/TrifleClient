package phoupraw.mcmod.trifleclient.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class NewOnThrowIterator<T> implements Iterator<T> {
    private Iterator<T> iterator;
    
    @Override
    public boolean hasNext() {
        for (int i = 0; i < 10; i++) {
            try {
                return getIterator().hasNext();
            } catch (ConcurrentModificationException e) {
                setIterator(null);
            }
        }
        return false;
    }
    @Override
    public T next() {
        for (int i = 0; i < 10; i++) {
            try {
                return getIterator().next();
            } catch (ConcurrentModificationException e) {
                setIterator(null);
            }
        }
        throw new NoSuchElementException("NewOnThrowIterator " + getIterator());
    }
    public Iterator<T> getIterator() {
        if (iterator == null) {
            setIterator(newIterator());
        }
        return iterator;
    }
    public void setIterator(Iterator<T> iterator) {
        this.iterator = iterator;
    }
    public abstract Iterator<T> newIterator();
}
