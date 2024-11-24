package com.example.artspace

import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.artspace.adapter.GalleryAdapter
import com.example.artspace.data.ArtData
import com.example.artspace.databinding.FragmentGalleryBinding
import com.example.artspace.model.ArtworkItem

class GalleryFragment : Fragment(R.layout.fragment_gallery) {
    // Declaramos el binding
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var artworkList:ArrayList<ArtworkItem>
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout con ViewBinding
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init(){
        artworkList = ArrayList()
        recyclerView = binding.galleryRecycler
       // recyclerView.hasFixedSize(true)
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)


        val itemDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)

                // Márgenes horizontales para centrar el contenido
                outRect.left = 20
                outRect.right = 20

                // Márgenes verticales (si es necesario)
                outRect.top = 20
                outRect.bottom = 20
            }
        }
        recyclerView.addItemDecoration(itemDecoration)


        addToList()
        galleryAdapter = GalleryAdapter(artworkList)
        recyclerView.adapter = galleryAdapter
    }

    private fun addToList(){
        artworkList.addAll(ArtData().LoadArtItem())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}