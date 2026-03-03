package utilities;

import model.objectdata.Vertex;

public class Lerp {
    public static Vertex computeVertex(Vertex v1, Vertex v2, double t) {
        return v1.mul(1 - t).add(v2.mul(t));
    }

    public static <T extends Vectorizable<T>> T compute(T v1, T v2, double t) {
        return v1.mul(1 - t).add(v2.mul(t));
    }
}
