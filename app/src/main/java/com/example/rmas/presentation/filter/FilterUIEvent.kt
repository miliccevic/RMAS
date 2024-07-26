package com.example.rmas.presentation.filter


sealed class FilterUIEvent {
    data class StartDateChanged(val startDate: Long?) : FilterUIEvent()
    data class EndDateChanged(val endDate: Long?) : FilterUIEvent()
    data class TypeChanged(val type: List<String>) : FilterUIEvent()
    data class DistanceChanged(val distance: Float?) : FilterUIEvent()
    object OkButtonClicked : FilterUIEvent()
}