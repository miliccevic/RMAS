package com.example.rmas.viewmodels

import androidx.lifecycle.ViewModel
import com.example.rmas.presentation.filter.FilterUIEvent
import com.example.rmas.presentation.filter.FilterUIState
import kotlinx.coroutines.flow.MutableStateFlow

class FilterViewModel:ViewModel() {
    var filterUIState = MutableStateFlow(FilterUIState())
    fun onEvent(event:FilterUIEvent) {
        when(event){
            is FilterUIEvent.StartDateChanged->{
                filterUIState.value=filterUIState.value.copy(
                    startDate = event.startDate
                )
            }
            is FilterUIEvent.EndDateChanged->{
                filterUIState.value=filterUIState.value.copy(
                    endDate = event.endDate
                )
            }
            is FilterUIEvent.OkButtonClicked->{

            }

            is FilterUIEvent.TypeChanged -> {
                filterUIState.value=filterUIState.value.copy(
                    type = event.type
                )
            }

            is FilterUIEvent.DistanceChanged -> {
                filterUIState.value=filterUIState.value.copy(
                    distance = event.distance
                )
            }
        }
    }
}