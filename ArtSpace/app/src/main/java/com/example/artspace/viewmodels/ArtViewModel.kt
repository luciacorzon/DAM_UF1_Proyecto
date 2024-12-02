package com.example.artspace.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artspace.R
import com.example.artspace.core.Constants
import com.example.artspace.data.javaClasses.UserDAO
import com.example.artspace.model.ArtModel
import com.example.artspace.model.DetailedArtworkModel
import com.example.artspace.model.WebImage
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

    fun getFavoritesForUser(context: Context) {
        val username = getUserFromPreferences(context)
        if (username == null) {
            artworksList.value = emptyList()
            return
        }

        // Crea el UserDAO con el contexto adecuado
        val userDAO = UserDAO(context) // Usa el contexto pasado como parámetro
        val favorites = userDAO.returnFavoritesForUser(userDAO.getFavoritesFile(), username)

        if (favorites.isEmpty()) {
            artworksList.value = emptyList()
            return
        }

        // Crear una lista para almacenar las obras de arte favoritas
        val favoriteArtworks = mutableListOf<ArtModel>()

        viewModelScope.launch(Dispatchers.IO) {
            favorites.forEach { artId ->
                // Llamada a la API para obtener los detalles de la obra de arte
                val response = RetrofitClient.webService.getArtworkDetails(
                    apiKey = Constants.API_KEY,
                    artId = artId
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.artObject != null) {
                        val artwork = response.body()!!.artObject

                        // Asegúrate de que los valores sean nulos cuando no estén disponibles
                        val artModel = ArtModel(
                            objectNumber = artwork?.id ?: "Unknown", // Valor por defecto si id es nulo
                            title = artwork?.title ?: "Untitled", // Título por defecto si es nulo
                            author = artwork?.author ?: "Unknown", // Autor por defecto si es nulo
                            webImage = artwork?.webImage?.let { WebImage(url = it.url) } ?: null, // Asumimos que la URL está en imageUrl
                            description = artwork?.description,
                            materials = null,
                            techniques = null,
                            objectTypes = null ?: emptyList()
                        )

                        favoriteArtworks.add(artModel) // Agrega el ArtModel a la lista
                    }
                    // Actualizar la lista de artworks en el LiveData
                    artworksList.value = favoriteArtworks.sortedBy { it.title }
                }
            }
        }
    }


    private fun getUserFromPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("username", null)
    }


}