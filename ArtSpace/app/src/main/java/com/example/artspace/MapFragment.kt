package com.example.artspace

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.artspace.R.drawable.ic_red_map_pin
import com.example.artspace.network.OverPassRetrofitClient
import com.example.artspace.network.response.OverpassResponse
import com.google.android.material.card.MaterialCardView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ImageButton


class MapFragment : Fragment() {

    private lateinit var map: MapView

    private var showMuseums = false
    private var showPlaces = false

    private lateinit var museumCard: View
    private lateinit var placesCard: View

    private lateinit var infoPanel: MaterialCardView
    private lateinit var panelTitle: TextView
    private lateinit var panelDescription: TextView

    private lateinit var locationOverlay: MyLocationNewOverlay

    private var customLocation: GeoPoint? = null
    private var customLocationMarker: Marker? = null


    private var selectingNewLocation = false


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

        panelTitle = view.findViewById(R.id.panelTitle)
        panelDescription = view.findViewById(R.id.panelDescription)
        infoPanel = view.findViewById(R.id.bottomCard)
        setupPanelTouchListener()

        map = view.findViewById(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        //Quitar panel si se clica en mapa
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                Log.d("MapFragment", "Single tap detected at: $p")
                if (selectingNewLocation && p != null) {
                    customLocation = p

                    customLocationMarker?.let { map.overlays.remove(it) }

                    // Crear un nuevo marker
                    val marker = Marker(map)
                    marker.position = p
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down)
                    customLocationMarker = marker
                    map.overlays.add(marker)

                    map.controller.setCenter(p)
                    refreshMap()

                    return true
                }



                // Cerrar info panel
                val hitRadiusMeters = 50.0

                val touchedMarker = map.overlays.filterIsInstance<Marker>().any { marker ->
                    val distance = marker.position.distanceToAsDouble(p)
                    distance < hitRadiusMeters
                }

                if (!touchedMarker && infoPanel.visibility == View.VISIBLE) {
                    hideInfoPanel()
                }

                return false
            }


            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }
        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(0, mapEventsOverlay)


        val mapController = map.controller
        mapController.setZoom(15.0)


        // Overlay para ubicación actual
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)

        // Centrar el mapa automáticamente
        locationOverlay?.runOnFirstFix {
            val myLocation = locationOverlay?.myLocation
            if (myLocation != null) {
                // Esto asegura que el código de actualización del mapa se ejecute en el hilo principal
                requireActivity().runOnUiThread {
                    map.controller.setCenter(GeoPoint(myLocation.latitude, myLocation.longitude))
                }
            }
        }

        museumCard = view.findViewById(R.id.museumCardView) // ponle este ID en el XML
        placesCard = view.findViewById(R.id.placesCardView) // idem

        museumCard.isSelected = false
        placesCard.isSelected = false
        showMuseums = museumCard.isSelected
        showPlaces = placesCard.isSelected


        museumCard.setOnClickListener {
            museumCard.isSelected = !museumCard.isSelected
            showMuseums = museumCard.isSelected
            updateCardSelection()
            refreshMap()
        }

        placesCard.setOnClickListener {
            placesCard.isSelected = !placesCard.isSelected
            showPlaces = placesCard.isSelected
            updateCardSelection()
            refreshMap()
        }


        // Cambiar localizacion usuario
        val locationPickerButton = view.findViewById<ImageButton>(R.id.locationPickerButton)
        locationPickerButton.setOnClickListener {
            selectingNewLocation = !selectingNewLocation
            updateLocationPickerButtonColor(locationPickerButton)
            if (!selectingNewLocation) {
                customLocation = null

                // Eliminar el marcador si existe
                customLocationMarker?.let {
                    map.overlays.remove(it)
                    customLocationMarker = null
                }

                // Volver a la localización real
                locationOverlay.myLocation?.let {
                    val realLocation = GeoPoint(it.latitude, it.longitude)
                    map.controller.animateTo(realLocation)
                }
                refreshMap()
            }
        }


        return view
    }

    private fun createOverpassQuery(latitude: Double, longitude: Double, tags: List<String>): String {
        val searchRadiusKm = 100

        val latDelta = searchRadiusKm / 111.32
        val lonDelta = searchRadiusKm / (111.32 * Math.cos(Math.toRadians(latitude)))

        // Definir área de búsqueda
        val latMin = latitude - latDelta
        val latMax = latitude + latDelta
        val lonMin = longitude - lonDelta
        val lonMax = longitude + lonDelta


        val query = """
        [out:json];
        (
            ${tags.joinToString("\n") { "node[\"tourism\"=\"$it\"]($latMin,$lonMin,$latMax,$lonMax);" }}
            ${tags.joinToString("\n") { "way[\"tourism\"=\"$it\"]($latMin,$lonMin,$latMax,$lonMax);" }}
            ${tags.joinToString("\n") { "relation[\"tourism\"=\"$it\"]($latMin,$lonMin,$latMax,$lonMax);" }}
        );
        out center;
        """.trimIndent()

        return query
    }

    private fun handleApiResponse(response: Response<OverpassResponse>, map: MapView, type: String) {
        if (response.isSuccessful) {
            val elements = response.body()?.elements

            if (elements.isNullOrEmpty()) {
                Log.w("MapFragment", "No se encontraron sitios cercanos.")
            }

            elements?.forEach { site ->
                if (site.lat != null && site.lon != null) {
                    val geoPoint = GeoPoint(site.lat, site.lon)
                    val marker = Marker(map)
                    marker.position = geoPoint

                    val name = site.tags?.name ?: "Sitio desconocido"
                    val city = site.tags?.city ?: "Ciudad desconocida"

                    marker.title = name

                    if (type == "museum") {
                        marker.icon = resources.getDrawable(R.drawable.ic_blue_map_pin)
                    } else {
                        marker.icon = resources.getDrawable(R.drawable.ic_red_map_pin)
                    }

                    marker.setOnMarkerClickListener { _, _ ->
                        Log.d("MapFragment", "Marker clicked: $name, $city") // Agrega este log
                        showInfoPanel(name, city)
                        true
                    }


                    map.overlays.add(marker)
                } else {
                    //Log.w("MapFragment", "Sitio sin coordenadas válidas: ${site.tags?.get("name")}")
                }
            }
        } else {
            Log.e("MapFragment", "Error en la respuesta de Overpass API: ${response.code()}")
        }
    }



    private fun getNearbyMuseums(latitude: Double, longitude: Double) {
        val query = createOverpassQuery(latitude, longitude, listOf("museum"))

        OverPassRetrofitClient.apiService.getMuseumsNearby(query).enqueue(object : Callback<OverpassResponse> {
            override fun onResponse(call: Call<OverpassResponse>, response: Response<OverpassResponse>) {
                handleApiResponse(response, map, "museum")
            }

            override fun onFailure(call: Call<OverpassResponse>, t: Throwable) {
                Log.e("MapFragment", "Error al obtener museos: ${t.message}")
            }
        })
    }

    private fun getNearbyCulturalSites(latitude: Double, longitude: Double) {
        val query = createOverpassQuery(latitude, longitude, listOf("cathedral", "castle", "palace", "attraction"))

        OverPassRetrofitClient.apiService.getCulturalSitesNearby(query).enqueue(object : Callback<OverpassResponse> {
            override fun onResponse(call: Call<OverpassResponse>, response: Response<OverpassResponse>) {
                handleApiResponse(response, map, "cultural_site")
            }

            override fun onFailure(call: Call<OverpassResponse>, t: Throwable) {
                Log.e("MapFragment", "Error al obtener sitios culturales: ${t.message}")
            }
        })
    }

    private fun refreshMap() {
        // Guardar los overlays que queremos conservar
        val locationOverlays = map.overlays.filterIsInstance<MyLocationNewOverlay>()
        val eventsOverlays = map.overlays.filterIsInstance<MapEventsOverlay>()

        map.overlays.clear()

        // Volver a añadir overlays importantes
        map.overlays.addAll(eventsOverlays)
        map.overlays.addAll(locationOverlays)

        customLocationMarker?.let { map.overlays.add(it) }

        if (showMuseums || showPlaces) {
            val loc = customLocation ?: locationOverlay.myLocation ?: map.mapCenter
            if (showMuseums) getNearbyMuseums(loc.latitude, loc.longitude)
            if (showPlaces) getNearbyCulturalSites(loc.latitude, loc.longitude)
        } else {
            Log.d("MapFragment", "Ningún filtro activo.")
        }
    }


    private fun updateCardSelection() {
        val selectedStrokeWidth = (2 * resources.displayMetrics.density).toInt() // 2dp en px
        val inactiveStrokeWidth = 0

        val activeColor = ContextCompat.getColor(requireContext(), R.color.white)
        val inactiveColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)

        (museumCard as? com.google.android.material.card.MaterialCardView)?.apply {
            strokeWidth = if (isSelected) selectedStrokeWidth else inactiveStrokeWidth
            strokeColor = if (isSelected) activeColor else inactiveColor
        }

        (placesCard as? com.google.android.material.card.MaterialCardView)?.apply {
            strokeWidth = if (isSelected) selectedStrokeWidth else inactiveStrokeWidth
            strokeColor = if (isSelected) activeColor else inactiveColor
        }
    }

    private fun showInfoPanel(title: String, description: String) {
        Log.d("MapFragment", "Showing info panel: $title, $description")
        panelTitle.text = title
        panelDescription.text = description

        if (infoPanel.visibility != View.VISIBLE) {
            infoPanel.translationY = infoPanel.height.toFloat()
            infoPanel.alpha = 0f

            infoPanel.visibility = View.VISIBLE

            infoPanel.post {
                ObjectAnimator.ofFloat(infoPanel, "translationY", infoPanel.height.toFloat(), 0f).apply {
                    duration = 300
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
                ObjectAnimator.ofFloat(infoPanel, "alpha", 0f, 1f).apply {
                    duration = 300
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPanelTouchListener() {
        var initialY = 0f
        var isDragging = false

        infoPanel.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialY = event.rawY
                    isDragging = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaY = event.rawY - initialY
                    if (deltaY > 50) {
                        isDragging = true
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isDragging) {
                        hideInfoPanel()
                    } else {
                        v.performClick()
                    }
                    isDragging = false
                    true
                }
                else -> false
            }
        }
    }



    private fun hideInfoPanel() {
        infoPanel.post {
            val animator = ObjectAnimator.ofFloat(infoPanel, "translationY", 0f, infoPanel.height.toFloat())
            animator.duration = 300
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    infoPanel.visibility = View.GONE
                }
            })
            animator.start()
        }
    }

    private fun updateLocationPickerButtonColor(button: ImageButton) {
        if (selectingNewLocation) {
            button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
        } else {
            button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
        }
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