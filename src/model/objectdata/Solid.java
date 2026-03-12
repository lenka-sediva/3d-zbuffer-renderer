package model.objectdata;

import shader.FragmentShader;
import shader.SmoothColor;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Vec3D;

import java.util.ArrayList;

public abstract class Solid {
    protected ArrayList<Vertex> vertexBuffer = new ArrayList<>();
    protected ArrayList<Integer> indexBuffer = new ArrayList<>();
    protected ArrayList<Part> partBuffer = new ArrayList<>();
    private Mat4 modelMatrix = new Mat4Identity();
    private final String name, shortcut;
    private boolean isSelected;
    private FragmentShader shader = new SmoothColor();

    public Solid(String name, String shortcut) {
        this.name = name;
        this.shortcut = shortcut;
    }

    public ArrayList<Vertex> getVertexBuffer() {
        return vertexBuffer;
    }

    public ArrayList<Integer> getIndexBuffer() {
        return indexBuffer;
    }

    public ArrayList<Part> getPartBuffer() {
        return partBuffer;
    }

    public Mat4 getModelMatrix() {
        return modelMatrix;
    }

    public void setModelMatrix(Mat4 modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    public String getName() {
        return name;
    }

    public String getShortcut() {
        return shortcut;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setShader(FragmentShader shader) {
        this.shader = shader;
    }

    public FragmentShader getShader() {
        return shader;
    }

    // vypočítá střed objektu pro správný scale
    public Vec3D getCenter() {
        double x = 0, y = 0, z = 0;

        for (Vertex v : vertexBuffer) {
            x += v.getX();
            y += v.getY();
            z += v.getZ();
        }

        int size = vertexBuffer.size();
        return new Vec3D(x / size, y / size, z / size);
    }
}
