package ilapin.a3dviewer.domain.meshloading

import de.javagl.obj.Obj
import ilapin.a3dengine.MeshComponent
import org.joml.Vector3f

private class ObjIndex(
    val vertexIndex: Int,
    val normalIndex: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObjIndex

        if (vertexIndex != other.vertexIndex) return false
        if (normalIndex != other.normalIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertexIndex
        result = 31 * result + normalIndex
        return result
    }
}

fun Obj.toMesh(): MeshComponent {
    val vertices = ArrayList<Vector3f>()
    val normals = ArrayList<Vector3f>()
    val indices = ArrayList<Int>()

    val indexMap = HashMap<ObjIndex, Int>()

    var currentMeshIndex = 0
    for (i in 0 until numFaces) {
        val face = getFace(i)
        if (face.numVertices != 3) {
            throw RuntimeException("numVertices != 3: ${face.numVertices}")
        }
        for (j in 0 until face.numVertices) {
            val objVertexIndex = face.getVertexIndex(j)
            val objNormalIndex = face.getNormalIndex(j)
            val objVertex = getVertex(objVertexIndex)
            val objNormal = getNormal(objNormalIndex)
            val objIndex = ObjIndex(objVertexIndex, objNormalIndex)
            val meshIndex = indexMap[objIndex]
            if (meshIndex != null) {
                indices += meshIndex
            } else {
                vertices += Vector3f(objVertex.x, objVertex.y, objVertex.z)
                normals += Vector3f(objNormal.x, objNormal.y, objNormal.z)
                indexMap[objIndex] = currentMeshIndex
                indices += currentMeshIndex
                currentMeshIndex++
            }
        }
    }

    return MeshComponent(vertices, normals, indices)
}
