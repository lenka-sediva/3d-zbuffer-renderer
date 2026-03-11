package shader;

import model.objectdata.Vertex;
import transforms.Col;
import transforms.Vec2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.IO;

public class TextureColor implements FragmentShader {
    private BufferedImage texture;

    public TextureColor(String path) {
        try {
            texture = ImageIO.read(new File(path));
        } catch (IOException e) {
            texture = new BufferedImage(1,1, BufferedImage.TYPE_INT_RGB);
            texture.setRGB(0, 0, Color.MAGENTA.getRGB());
            IO.println(e.getMessage());
        }
    }

    @Override
    public Col getColor(Vertex vertex) {
        Vec2D textureCoord = vertex.getTextureCoordinates();

        // 0-1
        double u = textureCoord.getX();
        double v = textureCoord.getY();

        // 0-255 * 0-255
        u = u * (texture.getWidth() - 1);
        v = v * (texture.getHeight() - 1);

        // int texX = (int) Math.max(0, Math.min(texture.getWidth() - 1, u));
        // int texY = (int) Math.max(0, Math.min(texture.getHeight() - 1, v));

        return new Col(texture.getRGB((int)u, (int)v));
    }
}
