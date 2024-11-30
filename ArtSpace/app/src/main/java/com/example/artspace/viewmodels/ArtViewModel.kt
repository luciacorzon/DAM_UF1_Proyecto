package com.example.artspace.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artspace.core.Constants
import com.example.artspace.model.ArtModel
import com.example.artspace.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtViewModel: ViewModel() {
    //singular
    private val artList = MutableLiveData<List<ArtModel>>()
    //plural
    val artworksList: MutableLiveData<List<ArtModel>> = artList

    fun getAllPaintings(){
        viewModelScope.launch(Dispatchers.IO){
            val response = RetrofitClient.webService.getArtworks(
                apiKey = Constants.API_KEY,
                type = "painting", // Tipo de obra
                pageSize = 10      // Número de resultados por página
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
                type = "sculpture", // Tipo de obra
                pageSize = 10      // Número de resultados por página
            )
            withContext(Dispatchers.Main){
                artworksList.value = response.body()!!.artObjects.sortedBy {it.title}
            }
        }
    }

    fun getAllPhotos(){
        viewModelScope.launch(Dispatchers.IO){
            // Realiza la consulta a la API
            val response = RetrofitClient.webService.getArtworks(
                apiKey = Constants.API_KEY,
                type = "photograph",  // Tipo de obra
                pageSize = 100  // Número de resultados por página
            )

            // Filtrar los objetos artísticos en el hilo principal
            withContext(Dispatchers.Main){
                // Filtrar solo las fotografías artísticas basadas en el título o la descripción
                /*val filteredArtworks = response.body()?.artObjects?.filter { artObject ->
                    // Filtra por título, descripción o cualquier otro campo relevante
                    artObject.title.contains("artistic", ignoreCase = true) ||
                            artObject.description?.contains("art", ignoreCase = true) == true ||
                            artObject.objectTypes?.contains("photograph") ?: false// Puedes refinar según el tipo
                }

                // Actualiza la lista de resultados filtrados
                artworksList.value = filteredArtworks?.sortedBy { it.title }*/
                artworksList.value = response.body()!!.artObjects.sortedBy {it.title}
            }
        }
    }
}