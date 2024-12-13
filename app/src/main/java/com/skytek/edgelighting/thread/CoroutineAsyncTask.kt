package com.skytek.edgelighting

import kotlinx.coroutines.*

abstract class CoroutinesAsyncTask<String, Void, List>(val taskName: String) {

    val TAG by lazy {
        CoroutinesAsyncTask::class.java.simpleName
    }

    var status: Constant.Status = Constant.Status.PENDING
    var preJob: Job? = null
    var bgJob: Deferred<kotlin.collections.List<Any?>?>? = null
    abstract fun doInBackground(vararg params: String?): kotlin.collections.List<Any?>?
//    open fun onProgressUpdate(vararg values: Progress?) {}
    open fun onPostExecute(result: kotlin.collections.List<Any?>?) {}
    open fun onPreExecute() {}
    open fun onCancelled(result: Boolean?) {}
    protected var isCancelled = false

    fun execute(vararg params: String?) {
        execute(Dispatchers.Default, *params)
    }

    private fun execute(dispatcher: CoroutineDispatcher, vararg params: String?) {

        if (status != Constant.Status.PENDING) {
            when (status) {
                Constant.Status.RUNNING -> throw IllegalStateException("Cannot execute task:" + " the task is already running.")
                Constant.Status.FINISHED -> throw IllegalStateException("Cannot execute task:"
                        + " the task has already been executed "
                        + "(a task can be executed only once)")
                else -> {}
            }
        }

        status = Constant.Status.RUNNING

        GlobalScope.launch(Dispatchers.Main) {
            preJob = launch(Dispatchers.Main) {
                onPreExecute()
                bgJob = async(dispatcher) {
                    doInBackground(*params)
                }
            }
            preJob!!.join()
            if (!isCancelled) {
                withContext(Dispatchers.Main) {
                    onPostExecute(bgJob!!.await())
                    status = Constant.Status.FINISHED
                }
            }
        }
    }

    class Constant{
        enum class Status {
            PENDING,
            RUNNING,
            FINISHED
        }
    }

}