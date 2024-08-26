package com.example.rmas.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.rmas.data.User
import com.example.rmas.database.Firebase
import com.example.rmas.presentation.filter.FilterUIEvent
import com.example.rmas.presentation.filter.FilterUIState
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
fun FilterChips(state: State<FilterUIState>, filterViewModel: FilterViewModel) {
    val options =
        listOf("Saobraćajna nezgoda", "Rupa na putu", "Rad na putu", "Zatvorena ulica", "Ostalo")
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        options.forEach {
            FilterChip(selected = it in state.value.types,
                onClick = {
                    filterViewModel.onEvent(FilterUIEvent.TypeChanged(it))
                },
                label = { Text(it) },
                leadingIcon = if (it in state.value.types) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "",
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
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val offset = getSliderOffset(
                value = sliderPosition.value,
                valueRange = valueRange,
                boxWidth = maxWidth,
                labelWidth = labelMinWidth + 16.dp
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
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
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
fun DatePicker(
    isPickerVisible: MutableState<Boolean>,
    dateRangePickerState: DateRangePickerState,
    filterViewModel: FilterViewModel,
    state: State<FilterUIState>
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        value = state.value.datum,
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
            if (state.value.datum != "Izaberite opseg") {
                IconButton(onClick = {
                    filterViewModel.onEvent(FilterUIEvent.DatumChanged("Izaberite opseg"))
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
                                        filterViewModel.onEvent(
                                            FilterUIEvent.DatumChanged(
                                                formatRange(
                                                    filterViewModel.filterUIState.value.startDate,
                                                    filterViewModel.filterUIState.value.endDate
                                                )
                                            )
                                        )
                                        isPickerVisible.value = false
                                    },
                                    enabled = dateRangePickerState.selectedEndDateMillis != null
                                ) {
                                    Text(text = "Ok")
                                }
                            }
                        }
                        DateRangePicker(state = dateRangePickerState)
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
                                        filterViewModel.onEvent(
                                            FilterUIEvent.DatumChanged(
                                                formatRange(
                                                    filterViewModel.filterUIState.value.startDate,
                                                    filterViewModel.filterUIState.value.endDate
                                                )
                                            )
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FilterBottomSheet(
    isPickerVisible: MutableState<Boolean>,
    dateRangePickerState: DateRangePickerState,
    sliderPosition: MutableState<Float>,
    filterViewModel: FilterViewModel,
    state: State<FilterUIState>,
    showSecondSheet: MutableState<Boolean>
) {
    LazyColumn {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 2.dp)
                ) {
                    TextButton(onClick = {
                        filterViewModel.onEvent(FilterUIEvent.ResetButtonClicked)
                        sliderPosition.value = 0f
                    }) {
                        Text("Resetuj")
                    }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        "Filteri",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                }
            }
        }
        item {
            Text(
                text = "Naziv",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
            SearchBar(state, filterViewModel)
        }
        item {
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
            )
            Text(
                text = "Datum",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
            DatePicker(
                isPickerVisible,
                dateRangePickerState,
                filterViewModel,
                state
            )
        }
        item {
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
            )
            Text(
                "Korisnici",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        showSecondSheet.value = true
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                Text(
                    "Izaberite korisnike",
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
        item {
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
            )
            Text(
                "Tip objekta",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
            FilterChips(state, filterViewModel)
        }
        item {
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
            )
            Text(
                text = "Rastojanje u kilometrima",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
            DistanceSlider(sliderPosition, 0f..999f, filterViewModel)
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SecondBottomSheet(
    isSheetOpen: MutableState<Boolean>,
    filterViewModel: FilterViewModel,
    state: State<FilterUIState>
) {
    var users by remember { mutableStateOf(emptyList<User>()) }
    Firebase.getAllUsers {
        users = it
    }

    Column(
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { isSheetOpen.value = false }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            TextButton(onClick = { filterViewModel.onEvent(FilterUIEvent.ResetUsersClicked) }) {
                Text("Resetuj")
            }
        }
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(users) { user ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                filterViewModel.onEvent(FilterUIEvent.UsersChanged(user.id))
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user.username,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(15.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (user.id in state.value.users) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.padding(15.dp)
                            )
                        }
                    }
                }
                HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    isSheetOpen: MutableState<Boolean>,
    sheetState: SheetState,
    isPickerVisible: MutableState<Boolean>,
    dateRangePickerState: DateRangePickerState,
    sliderPosition: MutableState<Float>,
    showSecondSheet: MutableState<Boolean>,
    filterViewModel: FilterViewModel,
    state: State<FilterUIState>
) {
    ModalBottomSheet(
        onDismissRequest = {
            isSheetOpen.value = false
            showSecondSheet.value = false
        },
        sheetState = sheetState,
    ) {
        if (!showSecondSheet.value) {
            FilterBottomSheet(
                isPickerVisible = isPickerVisible,
                dateRangePickerState = dateRangePickerState,
                sliderPosition = sliderPosition,
                filterViewModel = filterViewModel,
                state = state,
                showSecondSheet = showSecondSheet
            )
        } else {
            SecondBottomSheet(
                showSecondSheet,
                filterViewModel,
                state
            )
        }
    }
}

@Composable
fun SearchBar(state: State<FilterUIState>, filterViewModel: FilterViewModel) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        label = { Text(text = "Pretraga") },
        value = state.value.searchText,
        onValueChange = {
            filterViewModel.onEvent(FilterUIEvent.SearchTextChanged(it))
        },
        readOnly = false,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (state.value.searchText != "") {
                IconButton(onClick = {
                    filterViewModel.onEvent(FilterUIEvent.SearchTextChanged(""))
                }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null
                    )
                }
            }
        }
    )
}