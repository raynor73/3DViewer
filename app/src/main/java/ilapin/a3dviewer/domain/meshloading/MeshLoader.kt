package ilapin.a3dviewer.domain.meshloading

import de.javagl.obj.ObjReader
import ilapin.a3dengine.MeshComponent
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.io.InputStream

class MeshLoader {

    private val stateSubject: BehaviorSubject<State> = BehaviorSubject.createDefault(State.NoMesh())

    private var loadingSubscription: Disposable? = null

    val state: Observable<State> = stateSubject

    fun loadMesh(inputStream: InputStream) {
        if (stateSubject.value == State.Loading) {
            cancelLoadingSubscription()
        }

        loadingSubscription = Observable
            .fromCallable { ObjReader.read(inputStream).toMesh() }
            .map<State> { mesh -> State.MeshReady(mesh) }
            .startWith(State.Loading)
            .onErrorReturn { t -> State.NoMesh(t) }
            .subscribeOn(Schedulers.io())
            .subscribe { state ->
                inputStream.close()
                stateSubject.onNext(state)
            }
    }


    fun onCleared() {
        cancelLoadingSubscription()
    }

    private fun cancelLoadingSubscription() {
        loadingSubscription?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        loadingSubscription = null
    }

    sealed class State {
        class NoMesh(val error: Throwable? = null) : State()
        object Loading : State()
        class MeshReady(val mesh: MeshComponent) : State()
    }
}