package com.example.artspace

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.artspace.adapter.GalleryAdapter
import com.example.artspace.data.ArtData
import com.example.artspace.databinding.FragmentGalleryBinding
import com.example.artspace.decorations.BottomBorderDecoration
import com.example.artspace.model.ArtModel
import com.example.artspace.model.ArtworkItem
import com.example.artspace.viewmodels.ArtViewModel

class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var galleryAdapter: GalleryAdapter
    private val artViewModel: ArtViewModel by viewModels() // Instanciar el ViewModel

    private var category: String? = null // Variable para almacenar la categoría

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)

        // Recuperamos el argumento de la categoría pasado desde el SecondaryMenuFragment
        category = arguments?.getString("category")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        observeViewModel()

        when (category) {
            getString(R.string.paint) -> artViewModel.getAllPaintings()
            getString(R.string.sculpture) -> artViewModel.getAllSculptures()
            getString(R.string.photography) -> artViewModel.getAllPhotos()
            else -> {
                // Si no se pasa una categoría válida, se puede manejar de forma predeterminada
                artViewModel.getAllPaintings() // Cargar pinturas como predeterminado
            }
        }
    }

    private fun initRecyclerView() {
        binding.galleryRecycler.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            // Crear un decorador para espaciado
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.left = 20
                    outRect.right = 20
                    outRect.top = 20
                    outRect.bottom = 20
                }
            })
        }
    }

    private fun observeViewModel() {
        // Observa la lista de artworks en el ViewModel
        artViewModel.artworksList.observe(viewLifecycleOwner, Observer { artworks ->
            updateRecyclerView(artworks)
        })
    }

    private fun updateRecyclerView(artList: List<ArtModel>) {
        // Configurar el adaptador con la lista actualizada
        galleryAdapter = GalleryAdapter(artList) { artItem ->
            // Crear un Bundle y poner el 'artId' como argumento
            val bundle = Bundle()
            bundle.putString("artId", artItem.objectNumber) // Pasa el 'artId' al Bundle

            // Navegar al ArtworkFragment pasando el Bundle
            findNavController().navigate(R.id.action_galleryFragment_to_artworkFragment, bundle)
        }
        binding.galleryRecycler.adapter = galleryAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
