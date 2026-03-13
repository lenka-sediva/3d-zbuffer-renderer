package shader;

import model.objects.Light;
import model.objectdata.Vertex;
import transforms.Col;
import transforms.Vec3D;

public class PhongColor implements FragmentShader {
    private final Light light;
    private FragmentShader surfaceShader;
    private double ambient = 0.5; // intenzita ambientní složky (0.0 - 1.0)
    private double specularStrength = 0.5; // intenzita spekulární složky (0.0 - 1.0)
    private final int shininess = 32; // lesklost povrchu (vyšší = menší a ostřejší spekulární odlesk)
    private Vec3D cameraPos = new Vec3D(0, 0, 5); // fallback = pozice kamery (pro výpočet spekulární složky)
    private double diffuseStrength = 1.0; // intenzita difúzní složky (0.0 - 1.0)

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

    public void setSpecularStrength(double specularStrength) {
        this.specularStrength = specularStrength;
    }

    public void setCameraPos(Vec3D cameraPos) {
        this.cameraPos = cameraPos;
    }

    public void setDiffuseStrength(double diffuseStrength) {
        this.diffuseStrength = diffuseStrength;
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

        // spekulární složka = max(0, R · V)^shininess
        Vec3D fragPos = new Vec3D(
                v.getWorldPos().getX(),
                v.getWorldPos().getY(),
                v.getWorldPos().getZ()
        );

        Vec3D viewDir = cameraPos.sub(fragPos)
                .normalized()
                .orElse(new Vec3D(0, 0, 1));

        Vec3D reflectDir = normal.mul(2 * normal.dot(toLight)).sub(toLight)
                .normalized()
                .orElse(new Vec3D(0,0,1));

        double spec = Math.pow(
                Math.max(viewDir.dot(reflectDir), 0.0),
                shininess
        );

        double specular = specularStrength * spec;

        // výsledná intenzita
        double diffuse = diff * attenuation * diffuseStrength;

        double intensity = ambient + diffuse + specular;
        intensity = Math.min(1.0, intensity);

        Col lightCol = light.getColor();

        return new Col(
                surfaceCol.getR() * lightCol.getR() * intensity,
                surfaceCol.getG() * lightCol.getG() * intensity,
                surfaceCol.getB() * lightCol.getB() * intensity
        ).saturate();
    }
}