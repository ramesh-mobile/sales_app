package com.sr.salesmanapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sr.salesmanapp.data.model.pojo.ShopModel
import com.sr.salesmanapp.data.model.pojo.ShopModelResponse
import com.sr.salesmanapp.data.network.ResultStatus
import com.sr.salesmanapp.data.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ShopListViewModel @Inject constructor(private val shopRepository: ShopRepository) :
    ViewModel() {

    private val _fetchResponse = MutableLiveData<ResultStatus<List<ShopModelResponse>>>()
    val fetchResponse = _fetchResponse as LiveData<ResultStatus<List<ShopModelResponse>>>

    private val _insertResponse = MutableLiveData<ResultStatus<String>>()
    val insertResponse = _insertResponse as LiveData<ResultStatus<String>>

    private val _updateResponse = MutableLiveData<ResultStatus<String>>()
    val updateResponse = _updateResponse as LiveData<ResultStatus<String>>

    private val _deleteResponse = MutableLiveData<ResultStatus<String>>()
    val deleteResponse = _deleteResponse as LiveData<ResultStatus<String>>

    fun getData() {
        CoroutineScope(Dispatchers.IO).launch {
            shopRepository.getShopList().collect {
                when (it) {
                    is ResultStatus.Success -> {
                        withContext(Dispatchers.Main) {
                            _fetchResponse.value = ResultStatus.Success(it.data)
                        }
                    }
                    is ResultStatus.Failure -> {
                        withContext(Dispatchers.Main) {
                            _fetchResponse.value = ResultStatus.Failure(it.t)
                        }
                    }
                    is ResultStatus.Loading -> {
                        withContext(Dispatchers.Main) {
                            _fetchResponse.value = ResultStatus.Loading
                        }
                    }
                }
            }
        }
    }

    fun insertShop(items: ShopModel) {
        CoroutineScope(Dispatchers.IO).launch {
            shopRepository.putShopDetails(items).collect {
                sendDataToUi(it,_insertResponse)
            }
        }
    }

    fun deleteShop(key: String) {
        CoroutineScope(Dispatchers.IO).launch {
            shopRepository.deleteShopDetails(key).collect {
                sendDataToUi(it,_deleteResponse)
            }
        }
    }

    fun updateShop(items: ShopModelResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            shopRepository.updateShopDetails(items).collect {
                sendDataToUi(it,_updateResponse)
            }
        }
    }

    private suspend fun sendDataToUi(
        it: ResultStatus<String>,
        liveData: MutableLiveData<ResultStatus<String>>
    ) {
        when (it) {
            is ResultStatus.Success -> {
                withContext(Dispatchers.Main) {
                    liveData.value = ResultStatus.Success(it.data)
                }
            }
            is ResultStatus.Failure -> {
                withContext(Dispatchers.Main) {
                    liveData.value = ResultStatus.Failure(it.t)
                }
            }
            is ResultStatus.Loading -> {
                withContext(Dispatchers.Main) {
                    liveData.value = ResultStatus.Loading
                }
            }
        }
    }


}