package io.gumil.kaskade.sample.todo

import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.State
import io.gumil.kaskade.sample.todo.data.TodoItem
import io.gumil.kaskade.sample.todo.data.TodoRepository
import io.gumil.kaskade.stateFlow

internal class TodoKaskade(
    private val todoRepository: TodoRepository
) {

    private val kaskade = Kaskade.create<TodoAction, TodoState>(TodoState.OnLoaded(listOf())) {
        on<TodoAction.Refresh> {
            TodoState.OnLoaded(todoRepository.getToDoItems())
        }

        on<TodoAction.Delete> {
            todoRepository.removeItem(action.todoItem)
            TodoState.OnDeleted(action.position)
        }

        on<TodoAction.Add> {
            todoRepository.addItem(action.todoItem)
            TodoState.OnAdded(action.todoItem)
        }

        on<TodoAction.Update> {
            todoRepository.updateItem(action.todoItem)
            TodoState.OnUpdated(action.position, action.todoItem)
        }
    }

    val state = kaskade.stateFlow()

    fun process(action: TodoAction) {
        kaskade.process(action)
    }

    fun unsubscribe() {
        state.unsubscribe()
        kaskade.unsubscribe()
    }
}

sealed class TodoState : State {
    data class OnLoaded(val list: List<TodoItem>) : TodoState()
    data class OnDeleted(val position: Int) : TodoState()
    data class OnAdded(val item: TodoItem) : TodoState()
    data class OnUpdated(val position: Int, val item: TodoItem) : TodoState()
}

sealed class TodoAction : Action {
    object Refresh : TodoAction()
    data class Delete(val position: Int, val todoItem: TodoItem) : TodoAction()
    data class Add(val todoItem: TodoItem) : TodoAction()
    data class Update(val position: Int, val todoItem: TodoItem) : TodoAction()
}
