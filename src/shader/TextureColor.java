package shader;

import model.objectdata.Vertex;
import transforms.Col;
import transforms.Vec2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureColor implements FragmentShader {
    private BufferedImage texture;

    public TextureColor(String path) {
        try {
            texture = ImageIO.read(new File(path));
        } catch (IOException e) {
            texture = new BufferedImage(1,1, BufferedImage.TYPE_INT_RGB);
            texture.setRGB(0, 0, Color.MAGENTA.getRGB());
            System.out.println("ERROR loading texture: " + e.getMessage());
        }
    }

    @Override
    public Col getColor(Vertex vertex) {
        Vec2D textureCoord = vertex.getTextureCoordinates();

        double u = textureCoord.getX();
        double v = textureCoord.getY();

        // clamp
        u = Math.max(0, Math.min(1, u));
        v = Math.max(0, Math.min(1, v));

        // otočení Y
        v = 1 - v;

        int texX = (int)(u * (texture.getWidth()  - 1));
        int texY = (int)(v * (texture.getHeight() - 1));

        return new Col(texture.getRGB(texX, texY));
    }
}
