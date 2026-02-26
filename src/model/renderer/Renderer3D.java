package model.renderer;

import model.objectdata.Part;
import model.objectdata.Solid;
import model.objectdata.Vertex;
import model.rasterops.LineRasterizer;
import model.rasterops.TriangleRasterizer;

import java.util.List;

public class Renderer3D {

    TriangleRasterizer triangleRasterizer;
    LineRasterizer lineRasterizer;

    public void setTriangleRasterizer(TriangleRasterizer triangleRasterizer, LineRasterizer lineRasterizer) {
        this.triangleRasterizer = triangleRasterizer;
        this.lineRasterizer = lineRasterizer;
    }

    public void renderSolid(Solid solid) {
        if(solid == null) return;

        List<Vertex> vertices = solid.getVertexBuffer();
        List<Integer> indices = solid.getIndexBuffer();
        List<Part> parts = solid.getPartBuffer();

        for(Part part : parts) {
            switch (part.getTopologyType()) {
                case LINE_LIST:
                    for (int i = 0; i < part.getCount(); i += 2) {
                        int index1 = indices.get(part.getStartIndex() + i);
                        int index2 = indices.get(part.getStartIndex() + i + 1);

                        lineRasterizer.rasterize(
                                vertices.get(index1),
                                vertices.get(index2)
                        );
                    }
                    break;

                case TRIANGLE_LIST:
                    for (int i = 0; i < part.getCount(); i += 3) {
                        int index1 = indices.get(part.getStartIndex() + i);
                        int index2 = indices.get(part.getStartIndex() + i + 1);
                        int index3 = indices.get(part.getStartIndex() + i + 2);

                        triangleRasterizer.rasterize(
                                vertices.get(index1),
                                vertices.get(index2),
                                vertices.get(index3)
                        );
                    }
                    break;

                case TRIANGLE_STRIP:
                    for (int i = 0; i < part.getCount() - 2; i++) {
                        int index1 = indices.get(part.getStartIndex() + i);
                        int index2 = indices.get(part.getStartIndex() + i + 1);
                        int index3 = indices.get(part.getStartIndex() + i + 2);

                        Vertex v1 = vertices.get(index1);
                        Vertex v2 = vertices.get(index2);
                        Vertex v3 = vertices.get(index3);

                        if (i % 2 == 0) {
                            Vertex temp = v2;
                            v2 = v3;
                            v3 = temp;
                        }

                        triangleRasterizer.rasterize(v1, v2, v3);
                    }
                    break;
            }
        }
    }
}
