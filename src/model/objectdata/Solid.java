package model.objectdata;

import java.util.ArrayList;

public abstract class Solid {
    protected ArrayList<Vertex> vertexBuffer = new ArrayList<>();
    protected ArrayList<Integer> indexBuffer = new ArrayList<>();
    protected ArrayList<Part> partBuffer = new ArrayList<>();

    public ArrayList<Vertex> getVertexBuffer() {
        return vertexBuffer;
    }

    public ArrayList<Integer> getIndexBuffer() {
        return indexBuffer;
    }

    public ArrayList<Part> getPartBuffer() {
        return partBuffer;
    }
}
