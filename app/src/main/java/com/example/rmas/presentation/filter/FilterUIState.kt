package com.example.rmas.presentation.filter


data class FilterUIState(
    var startDate: Long? = null,
    var endDate: Long? = null,
    var type: List<String> = emptyList(),
    var distance: Float? = null,
)
