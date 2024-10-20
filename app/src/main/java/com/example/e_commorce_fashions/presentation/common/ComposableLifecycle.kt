package com.example.e_commorce_fashions.presentation.common

import androidx.compose.runtime.Immutable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Immutable
class HandleLifecycle(private val lifecycle: Lifecycle) : LifecycleEventObserver{
    private var _onResume: (() -> Unit)? = null
    private var _onPause: (() -> Unit)? = null
    private var _onDestroy: (() -> Unit)? = null
    private var _onStart: (() -> Unit)? = null
    private var _onStop: (() -> Unit)? = null
    private var _onCreate: (() -> Unit)? = null
    private var _onAny: (() -> Unit)? = null

    fun onResume(action: () -> Unit): HandleLifecycle{
        _onResume = action
        return this
    }

    fun onPause(action: () -> Unit) : HandleLifecycle{
        _onPause = action
        return this
    }

    fun onDestroy(action: () -> Unit) : HandleLifecycle{
        _onDestroy = action
        return this
    }

    fun onStart(action: () -> Unit) : HandleLifecycle{
        _onStart = action
        return this
    }

    fun onStop(action: () -> Unit) : HandleLifecycle{
        _onStop = action
        return this
    }

    fun onCreate(action: () -> Unit) : HandleLifecycle{
        _onCreate = action
        return this
    }

    fun onAny(action: () -> Unit) : HandleLifecycle{
        _onAny = action
        return this
    }

    init{
        lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event){
            Lifecycle.Event.ON_CREATE -> _onCreate?.invoke()
            Lifecycle.Event.ON_START -> _onStart?.invoke()
            Lifecycle.Event.ON_RESUME -> _onResume?.invoke()
            Lifecycle.Event.ON_PAUSE -> _onPause?.invoke()
            Lifecycle.Event.ON_STOP -> _onStop?.invoke()
            Lifecycle.Event.ON_DESTROY -> {
                lifecycle.removeObserver(this)
                _onDestroy?.invoke()
            }
            Lifecycle.Event.ON_ANY -> _onAny?.invoke()
        }
    }
}