package com.example.artspace.network.response

import com.example.artspace.model.Element


data class OverpassResponse (
    val elements: List<Element>
)