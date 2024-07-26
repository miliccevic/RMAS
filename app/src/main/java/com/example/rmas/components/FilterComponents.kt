package com.example.rmas.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.rmas.presentation.filter.FilterUIEvent
import com.example.rmas.utils.formatRange
import com.example.rmas.viewmodels.FilterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Modifier.dateRangeDialogModifier(internalState: DateRangePickerState): Modifier {
    val result = if (internalState.displayMode == DisplayMode.Picker) Modifier.fillMaxSize()
    else Modifier.wrapContentSize()
    return this.then(result.padding(16.dp))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChips(selected: SnapshotStateList<String>, filterViewModel: FilterViewModel) {
    val options =
        listOf("Saobraćajna nezgoda", "Rupa na putu", "Rad na putu", "Zatvorena ulica", "Ostalo")
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        options.forEach {
            FilterChip(selected = it in selected,
                onClick = {
                    if (it in selected)
                        selected.remove(it)
                    else
                        selected.add(it)
                    filterViewModel.onEvent(FilterUIEvent.TypeChanged(selected))
                },
                label = { Text(it) },
                leadingIcon = if (it in selected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
fun DistanceSlider(
    sliderPosition: MutableState<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
    filterViewModel: FilterViewModel,
    labelMinWidth: Dp = 24.dp,
) {
    Column {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val offset = getSliderOffset(
                value = sliderPosition.value,
                valueRange = valueRange,
                boxWidth = maxWidth,
                labelWidth = labelMinWidth + 8.dp
            )

            val valueText = sliderPosition.value.toInt().toString()

            if (sliderPosition.value >= valueRange.start) {
                SliderLabel(
                    label = valueText,
                    minWidth = labelMinWidth,
                    modifier = Modifier
                        .padding(start = offset)
                )
            }
        }
        Slider(
            value = sliderPosition.value,
            onValueChange = {
                sliderPosition.value = it
            },
            valueRange = valueRange,
            steps = 50,
            onValueChangeFinished = {
                filterViewModel.onEvent(FilterUIEvent.DistanceChanged(sliderPosition.value))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SliderLabel(
    label: String,
    minWidth: Dp,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        textAlign = TextAlign.Center,
        color = Color.White,
        modifier = modifier
            .background(
                color = Color.Red,
                shape = RoundedCornerShape(40)
            )
            .padding(4.dp)
            .defaultMinSize(minWidth = minWidth)
    )
}


fun getSliderOffset(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    boxWidth: Dp,
    labelWidth: Dp
): Dp {
    val coerced = value.coerceIn(valueRange.start, valueRange.endInclusive)
    val positionFraction = calcFraction(valueRange.start, valueRange.endInclusive, coerced)

    return (boxWidth - labelWidth) * positionFraction
}

fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePicker(
    datum: MutableState<String>,
    isPickerVisible: MutableState<Boolean>,
    dateRangePickerState: DateRangePickerState,
    filterViewModel: FilterViewModel
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        value = datum.value,
        onValueChange = {},
        readOnly = true,
        leadingIcon = {
            IconButton(onClick = { isPickerVisible.value = true }) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null
                )
            }
        },
        trailingIcon = {
            if (datum.value != "Izaberite opseg") {
                IconButton(onClick = {
                    datum.value = "Izaberite opseg"
                    filterViewModel.onEvent(FilterUIEvent.StartDateChanged(null))
                    filterViewModel.onEvent(FilterUIEvent.EndDateChanged(null))
                }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null
                    )
                }
            }
        }
    )
    if (isPickerVisible.value) {
        BasicAlertDialog(properties = DialogProperties(
            usePlatformDefaultWidth = dateRangePickerState.displayMode == DisplayMode.Input
        ),
            onDismissRequest = { isPickerVisible.value = false },
            content = {
                Surface(shape = MaterialTheme.shapes.large) {
                    Column(
                        modifier = Modifier.dateRangeDialogModifier(
                            dateRangePickerState
                        ), verticalArrangement = Arrangement.Top
                    ) {
                        if (dateRangePickerState.displayMode != DisplayMode.Input) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, end = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(onClick = {
                                    isPickerVisible.value = false
                                }) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = null
                                    )
                                }
                                TextButton(
                                    onClick = {
                                        filterViewModel.onEvent(
                                            FilterUIEvent.StartDateChanged(
                                                dateRangePickerState.selectedStartDateMillis!!
                                            )
                                        )
                                        filterViewModel.onEvent(
                                            FilterUIEvent.EndDateChanged(
                                                dateRangePickerState.selectedEndDateMillis!!
                                            )
                                        )
                                        datum.value = formatRange(
                                            filterViewModel.filterUIState.value.startDate,
                                            filterViewModel.filterUIState.value.endDate
                                        )
                                        isPickerVisible.value = false
                                    },
                                    enabled = dateRangePickerState.selectedEndDateMillis != null
                                ) {
                                    Text(text = "Ok")
                                }
                            }
                        }
                        androidx.compose.material3.DateRangePicker(state = dateRangePickerState)
                        if (dateRangePickerState.displayMode != DisplayMode.Picker) {
                            Row(modifier = Modifier.align(Alignment.End)) {
                                TextButton(onClick = {
                                    isPickerVisible.value = false
                                }) {
                                    Text(text = "Otkaži")
                                }
                                TextButton(
                                    onClick = {
                                        filterViewModel.onEvent(
                                            FilterUIEvent.StartDateChanged(
                                                dateRangePickerState.selectedStartDateMillis!!
                                            )
                                        )
                                        filterViewModel.onEvent(
                                            FilterUIEvent.EndDateChanged(
                                                dateRangePickerState.selectedEndDateMillis!!
                                            )
                                        )
                                        datum.value = formatRange(
                                            filterViewModel.filterUIState.value.startDate,
                                            filterViewModel.filterUIState.value.endDate
                                        )
                                        isPickerVisible.value = false
                                    },
                                    enabled = dateRangePickerState.selectedEndDateMillis != null
                                ) {
                                    Text(text = "Potvrdi")
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}


