package shader;

import model.objectdata.Vertex;
import transforms.Col;

@FunctionalInterface
public interface FragmentShader {
    Col getColor(Vertex v);
}
