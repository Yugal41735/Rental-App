package com.example.rentalapp.ui

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rentalapp.data.Vehicle
import coil.compose.rememberAsyncImagePainter
import com.example.rentalapp.data.VehicleViewModel

@Composable
fun VehicleCard(
    navController: NavController,
    vehicle: Vehicle,
    viewModel: VehicleViewModel
) {
    // Implement the UI for a single vehicle card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable {
                // Handle card click
                viewModel.setCurrentVehicle(vehicle)
                navController.navigate("Vehicle Detailed Info")
//                VehicleDetailsScreen(vehicle = vehicle)
            }
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(vehicle.imageUrls[0]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
            Text(text = "${vehicle.make} ${vehicle.model}", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Daily Rent: ${vehicle.dailyRentalPrice}", style = MaterialTheme.typography.bodyMedium)

        }
    }
}