package com.example.rentalapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.rentalapp.data.Vehicle
import com.example.rentalapp.data.VehicleViewModel

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailsScreen(
    navController: NavController,
    viewModel: VehicleViewModel
) {
    val currentVehicle = viewModel.currentVehicle
    // Implement the UI for displaying vehicle details
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Vehicle Details") }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    if (currentVehicle != null) {
                        items(currentVehicle.imageUrls) { imageUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(model = imageUrl),
                                contentDescription = "Vehicle Image",
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (currentVehicle != null) {
                    Text(text = "Make: ${currentVehicle.make}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Rental Price: ${currentVehicle.dailyRentalPrice}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Owner: ${currentVehicle.ownerName}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Phone: ${currentVehicle.ownerPhone}", style = MaterialTheme.typography.bodyLarge)
                } else {
                    Text(text = "No vehicle details available.")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        // it redirects the user to the window where user could store the number
//                        val intent = Intent(Intent.ACTION_INSERT).apply {
//                            type = ContactsContract.Contacts.CONTENT_TYPE
//                            putExtra(ContactsContract.Intents.Insert.NAME, currentVehicle?.ownerName)
//                            putExtra(ContactsContract.Intents.Insert.PHONE, currentVehicle?.ownerPhone)
//                        }

                        // directing the user to the phone window
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${currentVehicle?.ownerPhone}")
                        }

                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            showError = true
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text("Contact Owner", style = MaterialTheme.typography.bodyLarge)
                }

                if (showError) {
                    LaunchedEffect(Unit) {
                        snackbarHostState.showSnackbar(
                            message = "Unable to contact owner. Please check the phone number",
                            duration = SnackbarDuration.Short
                        )
                        showError = false
                    }
                }

            }
        }
    )
}