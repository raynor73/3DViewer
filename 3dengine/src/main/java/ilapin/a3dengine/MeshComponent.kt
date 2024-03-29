package ilapin.a3dengine

import org.joml.Vector3f

class MeshComponent(
    val vertices: List<Vector3f>,
    val normals: List<Vector3f>,
    val indices: List<Int>
) : SceneObjectComponent()