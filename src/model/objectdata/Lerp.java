package model.objectdata;

public class Lerp<T extends Vectorizable<T>> {

    public T lerp(T a, T b, double t) {
        return a.mul(1.0 - t).add(b.mul(t));
    }
}
