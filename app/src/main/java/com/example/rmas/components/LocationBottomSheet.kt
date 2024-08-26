package com.example.rmas.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.rmas.data.Like
import com.example.rmas.data.Location
import com.example.rmas.data.User
import com.example.rmas.database.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationBottomSheet(
    sheetState: SheetState,
    isSheetOpen: MutableState<Boolean>,
    locationId: String
) {
    var user by remember { mutableStateOf(User()) }
    var location by remember { mutableStateOf(Location()) }

    Firebase.getLocation(locationId) {
        if (it != null) {
            location = it
            Firebase.getUser(location.userId) { us ->
                if (us != null) {
                    user = us
                }
            }
        }
    }
    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    var favorites by remember { mutableStateOf(emptyList<Like>()) }
    Firebase.userLikes(userId) {
        favorites = it
    }
    ModalBottomSheet(
        onDismissRequest = {
            isSheetOpen.value = false
        },
        sheetState = sheetState,
    ) {
        LazyColumn {
            item {
                Row(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(location.title, style = MaterialTheme.typography.headlineMedium)
                    if (location.userId != userId) {
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
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = user.image),
                        contentDescription = "",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(30.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(user.username, style = MaterialTheme.typography.titleMedium)
                }
            }
            item {
                Image(
                    painter = rememberAsyncImagePainter(model = location.image),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .width(500.dp)
                        .padding(top = 20.dp, end = 20.dp, start = 20.dp),
                    contentScale = ContentScale.Crop
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = convertTimestampToDate(location.date),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                }
            }
            item {
                Text(
                    text = location.description,
                    modifier = Modifier
                        .padding(20.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertTimestampToDate(timestamp: Timestamp): String {
    val date = timestamp
        .toDate()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'u' HH:mm")

    return date.format(formatter)
}