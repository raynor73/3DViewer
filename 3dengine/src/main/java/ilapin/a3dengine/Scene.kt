package ilapin.a3dengine

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

open class Scene {

    private val sceneObjectAddedSubject = PublishSubject.create<SceneObject>()
    private val sceneObjectRemovedSubject = PublishSubject.create<SceneObject>()

    var rootObject: SceneObject? = null

    val sceneObjectAdded: Observable<SceneObject> = sceneObjectAddedSubject
    val sceneObjectRemoved: Observable<SceneObject> = sceneObjectRemovedSubject

    fun update() {
        rootObject?.update()
    }
}