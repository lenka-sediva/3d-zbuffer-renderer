package shader;

import model.objectdata.Vertex;
import transforms.Col;

public class ConstantColor implements FragmentShader {
    private final Col color;

    public ConstantColor() {
        this.color = new Col(0xFF0000); // červená
    }

    public ConstantColor(Col color) {
        this.color = color;
    }

    @Override
    public Col getColor(Vertex v) {
        return color;
    }
}
