package model.objectdata;

public interface Vectorizable<T> {
    T mul (double scalar);
    T add (T other);
}
