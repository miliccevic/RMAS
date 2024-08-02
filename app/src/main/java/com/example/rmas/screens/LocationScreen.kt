package com.example.rmas.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.rmas.components.LocationBottomSheet
import com.example.rmas.data.Like
import com.example.rmas.data.Location
import com.example.rmas.database.Firebase
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen() {
    var locations by remember { mutableStateOf(emptyList<Location>()) }
    Firebase.getLocations {
        locations = it
    }
    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    var favorites by remember { mutableStateOf(emptyList<Like>()) }
    Firebase.userLikes(userId) {
        favorites = it
    }
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen = rememberSaveable { mutableStateOf(false) }
    val clickedLocation = remember { mutableStateOf(Location()) }

    LazyColumn {
        items(locations) { location ->
            Card(
                onClick = {
                    isSheetOpen.value = true
                    clickedLocation.value=location
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp),
                border = BorderStroke(0.5.dp, Color.Gray),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White /*TODO*/
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = location.image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(130.dp)
                            .width(130.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        Text(
                            text = location.title,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = location.description,
                            color = Color.Black.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (location.userId != userId) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.BottomEnd /*TODO na kraj*/
                            ) {
                                IconToggleButton(
                                    checked = favorites.any {
                                        it.locationId == location.id
                                    },
                                    onCheckedChange = {
                                        if (favorites.any {
                                                it.locationId == location.id
                                            }) {
                                            Firebase.removeLikeFromDb(userId, location.id)
                                        } else {
                                            Firebase.addLikeToDb(userId, location.id)
                                        }
                                    }
                                ) {
                                    Icon(
                                        tint = Color(0xffE91E63),
                                        modifier = Modifier
                                            .graphicsLayer {
                                                scaleX = 1.3f
                                                scaleY = 1.3f
                                            },
                                        imageVector = if (favorites.any {
                                                it.locationId == location.id
                                            }) {
                                            Icons.Filled.Favorite
                                        } else {
                                            Icons.Default.FavoriteBorder
                                        },
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if(isSheetOpen.value){
        LocationBottomSheet(sheetState,isSheetOpen, clickedLocation.value)
    }
}

