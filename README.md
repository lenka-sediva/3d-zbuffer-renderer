# 3D Z-BUFFER RENDERER

#### A semester project for the Computer Graphics 2 course: a 3D scene with a custom software renderer, Z-buffer and Phong lighting model.

<img src="img/screenshot.png" alt="Application screenshot" width="100%">

All rendering runs on the CPU in pure Java (Swing is used only to display the final image and to handle input). No OpenGL or any other graphics library is used. Transformations, clipping, rasterization, depth testing, interpolation and shading are all implemented in the project.

---

## Features

- **Custom rendering pipeline**: model → view → projection (MVP), fast culling of whole solids outside the view volume, clipping of triangles and lines against the near plane (`clipZ`, `clipLineZ`), dehomogenization and viewport transformation
- **Z-buffer**: per-pixel depth test (`Visibility.setPixelWithZTest`), depth in the range ⟨0, 1⟩, smaller value = closer to the camera
- **Triangle rasterization** using edge functions and barycentric coordinates, **line rasterization** using Bresenham's algorithm
- **Perspective-correct interpolation** of texture coordinates (interpolating `uv/w` and `1/w`)
- **Topologies**: `LINE_LIST`, `TRIANGLE_LIST`, `TRIANGLE_STRIP`
- **Solids**: tetrahedron, sphere, cylinder (including filled caps), cube, bicubic Bézier surface, XYZ axes with arrows, and a sphere representing the light
- **Shaders** (fragment shaders):
  - constant color
  - interpolated color (Gouraud shading)
  - texture (each solid has its own: Minecraft grass on the cube, plywood on the cylinder and tetrahedron, a UV grid on the sphere and bicubic surface)
  - **Phong lighting model** with a point light, combinable with any of the shaders above (ambient, diffuse and specular components, plus distance attenuation)
- **Light animation**: the light orbits around the Z axis
- **Perspective / orthographic projection**
- **Solid / wireframe model**
- **Camera** in 1st or 3rd person view, controlled by keyboard and mouse
- **Transforming the selected object**: translation, rotation, scaling around its center
- **Window resizing**: both the color buffer and the Z-buffer adapt to the new window size

---

## Requirements and running

- **JDK 25 or newer**: the application itself needs JDK 22+ (`List.getFirst()` and unnamed variables `_` in lambdas); `TestLerp` additionally uses the `IO` class from `java.lang`, available since JDK 25
- No external dependencies and no build tool (Maven/Gradle). The project is set up for **IntelliJ IDEA** (source folder `src`)

### IntelliJ IDEA
1. Open the project folder.
2. Set the Project SDK to JDK 22+.
3. Run `app.App`.

### Command line
Run from the project root, because textures are loaded via the relative path `res/...`.

```bash
javac -d out $(find src -name "*.java")
java -cp out app.App
```

---

## Controls

A help overlay is also shown directly in the application window.

### Observer (camera)
| Key | Action |
|---|---|
| `W` `A` `S` `D` | move forward / left / backward / right |
| `Q` / `E` | turn left / right |
| `Shift` / `Ctrl` | look up / down |
| mouse drag | look around |
| `Space` | toggle between 1st and 3rd person |

### Object selection
| Key | Object |
|---|---|
| `T` | tetrahedron |
| `U` | sphere |
| `V` | cylinder |
| `K` | cube |
| `B` | bicubic surface |
| `L` | light |

### Transforming the selected object
| Key | Action |
|---|---|
| arrow keys | move in the XY plane |
| `X` / `Y` / `Z` | rotate 90° around the given axis |
| `+` / `-` | enlarge / shrink |

### Display and lighting
| Key | Action |
|---|---|
| `1` | constant color |
| `2` | interpolated color |
| `3` | texture |
| `M` | solid / wireframe model |
| `P` | perspective / orthographic projection |
| `R` | start / stop light animation |
| `O` | reset all objects and the light to their initial state |
| `C` | clear the scene |
| sliders at the top | intensity of the ambient, diffuse and specular components |

Shaders `1`–`3` are applied only to the currently selected object, and Phong lighting stays applied on top of the texture as well.

---

## Project structure

```
src/
├── app/            entry point (App), Z-buffer test (ZBufferTest) and interpolation test (TestLerp)
├── controller/     Controller: scene setup, keyboard, mouse and slider handling
├── model/
│   ├── objectdata/ Vertex, Part, Solid (vertex/index/part buffer), TopologyType
│   ├── objects/    solids (Cube, Sphere, Cylinder, Tetrahedron, BicubicSurface, Axis, Arrow) and Light
│   ├── rasterdata/ ColorBuffer, DepthBuffer, Visibility (Z-test)
│   ├── rasterops/  TriangleRasterizer, LineRasterizerBresenham
│   └── renderer/   Renderer3D: transformation, clipping and passing data to the rasterizers
├── shader/         FragmentShader and its implementations (ConstantColor, SmoothColor, TextureColor, PhongColor)
├── transforms/     library for matrices, vectors, camera, colors, cubics and bicubics (Mat4, Vec3D, Camera, Col, Bicubic, …)
├── utilities/      Lerp (linear interpolation), Vectorizable
└── view/           Window (JFrame), Canvas (raster, UI and slider rendering)
res/                textures
img/                images for the assignment (Z-test pseudocode, test example)
```

### A vertex's path through the pipeline

1. The solid's **vertex buffer** is transformed by the `model · view · projection` matrix into clip space, and also by the model matrix into world space, because the Phong model needs world-space positions and normals.
2. **Fast culling**: if all vertices lie outside the same face of the view volume, the solid is not rendered at all.
3. **Near-plane clipping**: a triangle is discarded, cut down to a smaller triangle, or split into two.
4. **Dehomogenization** (division by `w`) and **viewport transformation** (the Y axis points down).
5. **Rasterization**: for each pixel in the bounding rectangle, barycentric coordinates are computed and depth, color, UV, normal and world position are interpolated. The result is processed by the shader and written through the Z-test.

> Matrices in the `transforms` library use the row-vector convention, so they are multiplied in the order `model.mul(view).mul(proj)`.


---

## Textures

| File | Used on |
|---|---|
| `res/mc_grass.jpg` | cube |
| `res/plywood_diff_4k.jpg` | cylinder, tetrahedron |
| `res/uv_grid.jpg` | sphere, bicubic surface |
