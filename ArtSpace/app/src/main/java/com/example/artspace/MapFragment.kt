package com.example.artspace

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.artspace.network.OverPassRetrofitClient
import com.example.artspace.network.RetrofitClient
import com.example.artspace.network.response.OverpassResponse
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment() {

    private lateinit var map: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        map = view.findViewById(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(15.0)

        // Overlay para la ubicación actual
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)

        // Centrar el mapa automáticamente en cuanto haya una ubicación
        // En el callback de onLocationChanged, asegúrate de actualizar el mapa en el hilo principal
        locationOverlay?.runOnFirstFix {
            val myLocation = locationOverlay?.myLocation
            if (myLocation != null) {
                // Esto asegura que el código de actualización del mapa se ejecute en el hilo principal
                requireActivity().runOnUiThread {
                    map.controller.setCenter(GeoPoint(myLocation.latitude, myLocation.longitude))

                    // Aquí también puedes agregar la llamada para cargar museos cercanos si lo necesitas
                    getNearbyMuseums(myLocation.latitude, myLocation.longitude)
                }
            }
        }


        return view
    }

    // Función para obtener museos cercanos usando Overpass API
    private fun getNearbyMuseums(latitude: Double, longitude: Double) {
        // Distancia en kilómetros
        val searchRadiusKm = 100

        // Calcular el área en grados
        val latDelta = searchRadiusKm / 111.32 // 1 grado de latitud ≈ 111.32 km
        val lonDelta = searchRadiusKm / (111.32 * Math.cos(Math.toRadians(latitude))) // Ajuste según latitud

        // Definir el área de búsqueda con el centro en la ubicación actual
        val latMin = latitude - latDelta
        val latMax = latitude + latDelta
        val lonMin = longitude - lonDelta
        val lonMax = longitude + lonDelta

        // Crear la consulta para Overpass API
        val query = """
        [out:json];
        (
            node["tourism"="museum"]($latMin,$lonMin,$latMax,$lonMax);
            way["tourism"="museum"]($latMin,$lonMin,$latMax,$lonMax);
            relation["tourism"="museum"]($latMin,$lonMin,$latMax,$lonMax);
        );
        out center;
    """.trimIndent()

        // Llamar a la API de Overpass para obtener los museos cercanos
        OverPassRetrofitClient.apiService.getMuseumsNearby(query).enqueue(object : Callback<OverpassResponse> {
            override fun onResponse(call: Call<OverpassResponse>, response: Response<OverpassResponse>) {
                if (response.isSuccessful) {
                    val museums = response.body()?.elements
                    // Verificar si hay elementos
                    if (museums.isNullOrEmpty()) {
                        Log.w("MapFragment", "No se encontraron museos cercanos.")
                    }

                    museums?.forEach { museum ->
                        // Verificar si tiene lat y lon antes de añadir al mapa
                        if (museum.lat != null && museum.lon != null) {
                            val geoPoint = GeoPoint(museum.lat, museum.lon)
                            val marker = Marker(map)
                            marker.position = geoPoint
                            marker.title = museum.tags?.name ?: "Museo desconocido"
                            map.overlays.add(marker)
                        } else {
                            Log.w("MapFragment", "Museo sin coordenadas válidas: ${museum.tags?.name}")
                        }
                    }
                } else {
                    Log.e("MapFragment", "Error en la respuesta de Overpass API: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OverpassResponse>, t: Throwable) {
                // Manejo de errores en caso de que la solicitud falle
                Log.e("MapFragment", "Error al obtener museos: ${t.message}")
            }
        })
    }




    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
