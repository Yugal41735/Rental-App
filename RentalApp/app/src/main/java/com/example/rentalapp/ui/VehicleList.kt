package com.example.rentalapp.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentalapp.data.Vehicle
import com.example.rentalapp.data.VehicleViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun VehicleList(
//    vehicles: List<Vehicle>,
    navController: NavController,
    onVehicleTypeSelected: String = "",
    firestore: FirebaseFirestore,
    viewModel: VehicleViewModel
//    storage: FirebaseStorage
) {
    // Fetch logic for vehicles

    var vehicles by remember { mutableStateOf(emptyList<Vehicle>()) }

    LaunchedEffect(onVehicleTypeSelected) {
        firestore.collection("vehicles")
            .whereEqualTo("vehicleType", onVehicleTypeSelected)
            .get()
            .addOnSuccessListener { querySnapshot ->
                vehicles = querySnapshot.documents.mapNotNull { document ->
                    Vehicle(
                        id = document.id,
                        type = document.getString("vehicleType") ?: "",
                        make = document.getString("make") ?: "",
                        model = document.getString("model") ?: "",
                        year = document.getLong("year")?.toInt() ?: 0,
                        location = document.getGeoPoint("location"),
                        dailyRentalPrice = document.getDouble("dailyRentalPrice") ?: 0.0,
                        startDate = document.getString("startDate") ?: "",
                        endDate = document.getString("endDate") ?: "",
                        ownerName = document.getString("owner") ?: "",
                        ownerPhone = document.getString("ownerPhone") ?: "",
                        imageUrls = (document.get("imageUrls") as? List<String>) ?: emptyList()
                    )
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("Error: ", exception.toString())
            }
    }

    if(vehicles.isEmpty()) {
        // Show loading indicator or empty state
        Text("Loading vehicles...")
        CircularProgressIndicator()
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            items(vehicles) {vehicle ->
                VehicleCard(
                    navController,
                    vehicle,
                    viewModel
                )
            }
        }
    }


}

