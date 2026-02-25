package model.renderer;

import model.objectdata.Part;
import model.objectdata.Solid;
import model.objectdata.Vertex;
import model.rasterops.TriangleRasterizer;

import java.util.List;

public class Renderer3D {

    TriangleRasterizer triangleRasterizer;

    public void setTriangleRasterizer(TriangleRasterizer triangleRasterizer) {
        this.triangleRasterizer = triangleRasterizer;
    }

    public void renderSolid(Solid solid) {
        if(solid == null) return;

        List<Vertex> vertices = solid.getVertexBuffer();
        List<Integer> indices = solid.getIndexBuffer();
        List<Part> parts = solid.getPartBuffer();

        for(Part part : parts) {
            switch (part.getTopologyType()) {
                case LINE_LIST:
                    // TODO Slide 6
                    break;
                case TRIANGLE_LIST:
                    int start = part.getStartIndex();
                    int count = part.getCount();

                    // TODO Slide 7

                    // triangleRasterizer.rasterize(v1, v2, v3);
                    break;
                // TODO
            }
        }
    }

}

