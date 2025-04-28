package com.example.myapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.BuildConfig
import com.example.myapplication.MainApplication
import com.example.myapplication.data.model.AgeViewContainer
import com.example.myapplication.data.model.Player
import com.example.myapplication.data.repository.PlayerRepository
import com.example.myapplication.network.SocketManager
import com.example.myapplication.utils.events.DataEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel : ViewModel(), SocketManager.ErrorListener {

    init {
        MainApplication.appComponent.inject(this)
    }

    private val _showAgesLiveData = MutableLiveData<DataEvent<List<AgeViewContainer>>>()
    val showAgesLiveData: LiveData<DataEvent<List<AgeViewContainer>>> = _showAgesLiveData

    private val _showConnectionErrorLiveData = MutableLiveData<DataEvent<String>>()
    val showConnectionErrorLiveData: LiveData<DataEvent<String>> = _showConnectionErrorLiveData

    private val _showAllowedInfoLiveData = MutableLiveData<DataEvent<Boolean>>()
    val showAllowedInfoLiveData: LiveData<DataEvent<Boolean>> = _showAllowedInfoLiveData

    private val _enableButtonLiveData = MutableLiveData<Boolean>()
    val enableButtonLiveData: LiveData<Boolean> = _enableButtonLiveData


    private val _showSelectedAge = MutableLiveData<Int>()
    val showSelectedAge: LiveData<Int> = _showSelectedAge

    private val _showSelectedGender = MutableLiveData<String>()
    val showSelectedGender: LiveData<String> = _showSelectedGender


    @Inject
    lateinit var playerRepository: PlayerRepository

    @Inject
    lateinit var socketManager: SocketManager

    init {
        val player = playerRepository.getPlayer()
        _showSelectedGender.postValue(player.gender)
        _showSelectedAge.postValue(player.age)

        viewModelScope.launch(Dispatchers.IO) {
            socketManager.setErrorListener(this@MainViewModel)
            socketManager.connect(BuildConfig.SOCKET_URL, BuildConfig.SOCKET_PORT.toInt())
        }

    }

    fun onAgeSelectorClick() {
        _showAgesLiveData.postValue(
            DataEvent(
                playerRepository.getAges()
                    .map { AgeViewContainer(showSelectedAge.value == it, it) })
        )
    }

    fun onContinueClick(age: Int?, gender: String?) = viewModelScope.launch(Dispatchers.IO) {
        if (age != null && gender != null) {
            _enableButtonLiveData.postValue(false)
            val resultEvent = socketManager.send(Player(gender, age))
            _enableButtonLiveData.postValue(true)
            if (resultEvent != null) {
                _showAllowedInfoLiveData.postValue(DataEvent(resultEvent.allowed))
                playerRepository.setPlayer(showSelectedAge.value!!, showSelectedGender.value!!)
            }
        }
    }


    override fun onCleared() {
        viewModelScope.launch(Dispatchers.IO) {
            socketManager.close()
        }
        super.onCleared()
    }

    fun onAgeSelect(age: Int) {
        _showSelectedAge.postValue(age)
    }

    fun onMaleSelect() {
        _showSelectedGender.postValue("m")
    }

    fun onFemaleSelect() {
        _showSelectedGender.postValue("f")
    }

    override fun onError(message: String) {
        _showConnectionErrorLiveData.postValue(DataEvent(message))
    }
}