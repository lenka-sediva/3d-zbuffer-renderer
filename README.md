# Repository for ut13-c04

### TODO 3

### Progress checklist

- [ ] Create data structures: - `Vertex`, `Part`, `TopologyType` enum (min. 3 types), `Solid` abstract class
- [ ] Create an Arrow object
- [ ] Renderer implementation - Support rendering of all 3 topologies
- [ ] Viewport transformation in both rasterizers
- [ ] Arrow displays correctly with all parts visible
- [ ] (Additional) Vertex linear interpolation

Your task is to draw an Arrow object composed of three distinct parts. Each part must use a different topology type (`TRIANGLES`, `LINES`, `TRIANGLE_STRIP`).

**Vertex coordinates** must be defined in NDC (Normalized Device Coordinates), not in screen space. The _viewport transformation_ to screen space should be handled by the rasterizer.

Implement a _Renderer_ that correctly processes each topology, (additionally) interpolating vertex attributes and drawing the geometry to the screen. The Arrow should be visible with all three parts rendered correctly according to their respective topology rules.

### Expected result

![topologyTest](img/topology-test.png)

### TODO 2

### Progress checklist

- [ ] Complete triangle rasterizer
- [ ] Triangles have passed the visibility test
- [ ] (Additional) Viewport transformation
- [ ] (Additional) Vertex implementation
- [ ] (Additional) Complete line rasterizer _(for wireframe model)_

Your task is to draw two triangles with different depths at their vertices. Correctly interpolate the individual edges and fill in the area of the triangle.

Use these two triangles to verify correct depth interpolation:

```java
// first triangle
Point3D t1a = new Point3D(0., ((double) canvas.getHeight() /2) - 1, 0.5);
Point3D t1b = new Point3D(((double) canvas.getWidth() /2)-1, 0., 0.5);
Point3D t1c = new Point3D(canvas.getWidth() - 1, canvas.getHeight() - 1, 0.5);

// second triangle
Point3D t2a = new Point3D(canvas.getWidth()- 1, ((double) canvas.getHeight() /2) - 1, 0.8);
Point3D t2b = new Point3D(((double) canvas.getWidth() /2)-1, canvas.getHeight() - 1, 0.8);
Point3D t2c = new Point3D(0., 0., 0.1);
```

_Additionally, you can create a viewport transformation, but it is necessary to enter the coordinates of the triangles in NDC coordinates._

_Also you can start implementing a vertex that will carry all the attributes._

### Homework


![Triangle-RasterizerTest](img/trianglesTest.png)


## TODO 1

### Progress checklist

- [ ] Z-Buffer
- [ ] Passed visibility test

Two types of buffers should be created in this repository:
- **ColorBuffer** for storing color information about the canvas
- **DepthBuffer** for storing depth information about the canvas

We work with both buffers in the VisibilityBuffer class, which tests and updates _ImageBuffer_ based on the depth from _DepthBuffer_.

Furthermore, a test class called ```ZBufferTest.java``` was created, which contains a method for drawing squares and a static method for creating them.

### Homework

![Z-Buffer test](img/Z-Test.jpg)
