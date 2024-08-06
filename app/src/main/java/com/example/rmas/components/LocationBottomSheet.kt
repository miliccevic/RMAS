package com.example.rmas.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.rmas.data.Like
import com.example.rmas.data.Location
import com.example.rmas.data.User
import com.example.rmas.database.Firebase
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationBottomSheet(
    sheetState: SheetState,
    isSheetOpen: MutableState<Boolean>,
    location: Location
) {
    var user by remember { mutableStateOf(User()) }
    Firebase.getUser(location.userId) {
        if (it != null) {
            user = it
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
        Column(
            /*TODO scroll*/
        ) {
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
            Image( /*TODO*/
                painter = rememberAsyncImagePainter(model = location.image),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, end = 20.dp, start = 20.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = convertTimestampToDate(location.date.seconds * 1000 + location.date.nanoseconds / 1000000),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            Text(
                text = location.description,
                modifier = Modifier.padding(20.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

fun convertTimestampToDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = Date(timestamp)
    return sdf.format(date)
}