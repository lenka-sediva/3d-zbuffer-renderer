package shader;

import model.objects.Light;
import model.objectdata.Vertex;
import transforms.Col;
import transforms.Vec3D;

public class PhongColor implements FragmentShader {
    private final Light light;
    private FragmentShader surfaceShader;
    private double ambient = 0.5; // intenzita ambientní složky (0.0 - 1.0)

    public PhongColor(Light light) {
        this.light = light;
        this.surfaceShader = null;
    }

    public void setSurfaceShader(FragmentShader surfaceShader) {
        this.surfaceShader = surfaceShader;
    }

    public void setAmbient(double ambient) {
        this.ambient = ambient;
    }

    @Override
    public Col getColor(Vertex v) {
        // barva povrchu = textura nebo barva vertexu
        Col surfaceCol = (surfaceShader != null)
                ? surfaceShader.getColor(v)
                : v.getColor();

        Vec3D normal = v.getNormal().normalized().orElse(new Vec3D(0, 0, 1));

        // vzdálenost světla od fragmentu
        double dx = light.getPosition().getX() - v.getWorldPos().getX();
        double dy = light.getPosition().getY() - v.getWorldPos().getY();
        double dz = light.getPosition().getZ() - v.getWorldPos().getZ();
        double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);

        // point light = směr od fragmentu ke světlu
        Vec3D toLight = new Vec3D(dx, dy, dz)
                .normalized()
                .orElse(new Vec3D(0, 1, 0));

        // ambientní složka
        double ambient = this.ambient; // může být nastavena zvenčí (ve slideru)

        // difúzní složka = max(0, N · L)
        double diff = Math.max(0.0, normal.dot(toLight));

        // atenuace = intenzita klesá se vzdáleností
        double attenuation = 1.0 / (1.0 + 0.2 * distance + 0.05 * distance * distance);

        // výsledná intenzita
        double intensity = ambient + (1.0 - ambient) * diff * attenuation;
        intensity = Math.min(1.0, intensity);

        Col lightCol = light.getColor();

        return new Col(
                surfaceCol.getR() * lightCol.getR() * intensity,
                surfaceCol.getG() * lightCol.getG() * intensity,
                surfaceCol.getB() * lightCol.getB() * intensity
        ).saturate();
    }
}