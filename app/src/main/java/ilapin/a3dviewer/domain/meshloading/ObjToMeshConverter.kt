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

        val i0 = face.getVertexIndex(0)
        val i1 = face.getVertexIndex(1)
        val i2 = face.getVertexIndex(2)

        val objVertex0 = getVertex(i0)
        val objVertex1 = getVertex(i1)
        val objVertex2 = getVertex(i2)
        val v0 = Vector3f(objVertex0.x, objVertex0.y, objVertex0.z)
        val v1 = Vector3f(objVertex1.x, objVertex1.y, objVertex1.z).sub(v0)// vertices[i1].GetPos().Sub(vertices[i0].GetPos())
        val v2 = Vector3f(objVertex2.x, objVertex2.y, objVertex2.z).sub(v0)//vertices[i2].GetPos().Sub(vertices[i0].GetPos())

        val defaultNormal = Vector3f()
        v1.cross(v2).normalize(defaultNormal)

        for (j in 0 until face.numVertices) {
            if (face.containsNormalIndices()) {
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
            } else {
                val objVertex = getVertex(face.getVertexIndex(j))
                vertices += Vector3f(objVertex.x, objVertex.y, objVertex.z)
                normals += defaultNormal
                indices += currentMeshIndex
                currentMeshIndex++
            }
        }
    }

    return MeshComponent(vertices, normals, indices)
}
