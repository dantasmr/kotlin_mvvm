package com.example.animals.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.animals.model.Animal
import com.example.animals.model.ApiKey
import com.example.animals.model.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class ListViewModel(application: Application) : AndroidViewModel(application){

    val animals by lazy{ MutableLiveData<List<Animal>>() }
    val loadError by lazy {MutableLiveData<Boolean>()}
    val loading by lazy {MutableLiveData<Boolean>()}


    private val disposable = CompositeDisposable()
    private val apiService = ApiService()

    fun refresh(){
        loading.value = true
        getKey()
    }

    private fun getKey(){

        disposable.add(
            apiService.getApiKey()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ApiKey>(){
                    override fun onSuccess(key: ApiKey) {
                        if (key.key.isNullOrEmpty()){
                            loadError.value = true
                            loading.value = false
                        }else{
                            getAnimals(key.key)
                        }
                    }

                    override fun onError(e: Throwable) {
                       e.printStackTrace()
                        loading.value = false
                        loadError.value = true
                    }

                })
        )

    }

    private fun getAnimals(key : String){
        disposable.add(
            apiService.getAnimals(key)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Animal>>() {
                    override fun onSuccess(list: List<Animal>) {
                        loadError.value = false
                        animals.value = list
                        loading.value = false
                    }

                    override fun onError(e: Throwable) {
                       e.printStackTrace()
                        loading.value = false
                        animals.value = null
                        loadError.value = true
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}