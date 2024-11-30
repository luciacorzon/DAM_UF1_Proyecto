package com.example.artspace.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artspace.R
import com.example.artspace.core.Constants
import com.example.artspace.model.ArtModel
import com.example.artspace.model.DetailedArtworkModel
import com.example.artspace.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtViewModel: ViewModel() {
    private val artList = MutableLiveData<List<ArtModel>>()
    val artworksList: MutableLiveData<List<ArtModel>> = artList

    private val _artworkDetails = MutableLiveData<DetailedArtworkModel?>()
    val artworkDetails: LiveData<DetailedArtworkModel?> get() = _artworkDetails




    fun getAllPaintings(){
        viewModelScope.launch(Dispatchers.IO){
            val response = RetrofitClient.webService.getArtworks(
                apiKey = Constants.API_KEY,
                type = "painting",
                pageSize = 300
            )
            withContext(Dispatchers.Main){
                artworksList.value = response.body()!!.artObjects.sortedBy {it.title}
            }
        }
    }

    fun getAllSculptures(){
        viewModelScope.launch(Dispatchers.IO){
            val response = RetrofitClient.webService.getArtworks(
                apiKey = Constants.API_KEY,
                type = "sculpture",
                pageSize = 300
            )
            withContext(Dispatchers.Main){
                artworksList.value = response.body()!!.artObjects.sortedBy {it.title}
            }
        }
    }

    fun getAllPhotos(){
        viewModelScope.launch(Dispatchers.IO){
            val response = RetrofitClient.webService.getArtworks(
                apiKey = Constants.API_KEY,
                type = "photograph",
                pageSize = 300
            )

            withContext(Dispatchers.Main){
                artworksList.value = response.body()!!.artObjects.sortedBy {it.title}
            }
        }
    }

    fun getArtworkDetails(artId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.getArtworkDetails(
                    apiKey = Constants.API_KEY,
                    artId = artId
                )

                if (response.isSuccessful) {
                    val artwork = response.body()?.artObject
                    if (artwork != null) {
                        val year = artwork.dating?.year ?: R.string.unknown_year
                        val country = artwork.productionPlaces?.firstOrNull() ?: R.string.unknown_country

                        withContext(Dispatchers.Main) {
                            _artworkDetails.value = artwork
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _artworkDetails.value = null
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _artworkDetails.value = null
                }
            }
        }
    }




}