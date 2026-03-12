package shader;

import model.objectdata.Vertex;
import transforms.Col;

// interpolovaná barva (Gouradovo stínování)
public class SmoothColor implements FragmentShader {
    @Override
    public Col getColor(Vertex v) {
        return v.getColor();
    }
}
