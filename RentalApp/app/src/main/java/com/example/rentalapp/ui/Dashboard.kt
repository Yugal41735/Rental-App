package com.example.rentalapp.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.rentalapp.R
import com.example.rentalapp.data.UserViewModel
import com.example.rentalapp.data.VehicleViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.firestore.FirebaseFirestore


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
//@Preview(showBackground = true)
fun DashBoardScreen(
    navController: NavController,
    viewModel: UserViewModel,
    vehicleViewModel: VehicleViewModel
) {

    var userData by remember { mutableStateOf<HashMap<String, String>?>(null) }
    var showError by remember { mutableStateOf(false) }
    val currentUser = viewModel.currentUser
    var selectedVehicleType by remember { mutableStateOf("2 Wheeler") }

    val uid = currentUser?.uid
    val db = FirebaseDatabase.getInstance().getReference("users").child(uid!!)
    val firestore = FirebaseFirestore.getInstance()

    val profileUrl = remember { mutableStateOf<Uri?>(null) }

    Log.d(TAG, "Uid: $uid")
    
    LaunchedEffect(uid) {
        if(uid.isNotBlank()) {
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()) {
                        try{
//                            val typeIndicator = object : GenericTypeIndicator<HashMap<String, S>>
                            userData = dataSnapshot.value as HashMap<String, String>?
                        } catch (e: Exception) {
                            Log.e("FirebaseDatabase", "Failed to convert data to HashMap", e)
                        }
                    } else {
                        Log.d("FirebaseDatabase", "Data not found for user: $uid")
                        showError = true
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseDatabase", "Failed to read data for user: $uid", error.toException())
                }
            }
            db.addValueEventListener(valueEventListener)
        }
    }

    LaunchedEffect(uid) {
        if(uid.isNotBlank()) {
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if(document != null && document.exists()) {
                        profileUrl.value = Uri.parse(document.getString("photoUrl"))
                        Log.d("Success", "Retrieved user photo of user : $uid")
                    } else {
                        Log.d("Error", "Failed to retrieve user photo of user : $uid")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Error", "Failed to retrieve user photo", exception)
                }

        }
    }




    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedVehicleType == "2 Wheeler",
                    onClick = { selectedVehicleType = "2 Wheeler" },
                    icon = { R.drawable.baseline_directions_bike_24 },
                    label = { Text("2 Wheeler") }
                )
                NavigationBarItem(
                    selected = selectedVehicleType == "4 Wheeler",
                    onClick = { selectedVehicleType = "4 Wheeler" },
                    icon = { R.drawable.baseline_directions_car_24 },
                    label = { Text("4 Wheeler") }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rental App",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Image(
                    painter = rememberAsyncImagePainter(profileUrl.value),
                    contentDescription = "User Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            if (userData != null) {
                userData?.forEach { (key, value) ->
                    if(key == "name") {
                        Text(
                            text = "Welcome !! $value :)",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            } else if (showError) {
                Text("Failed to retrieve user data.", color = MaterialTheme.colorScheme.error)
            } else {
                // Display loading indicator or message
                Text("Loading user data...")
                CircularProgressIndicator()
            }

            Button(
                onClick = {navController.navigate("posting")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "List your vehicle",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }



            VehicleList(
                onVehicleTypeSelected = selectedVehicleType,
                firestore = firestore,
                navController = navController,
                viewModel = vehicleViewModel
            )

            // ... rest of your layout
        }
    }


    @Composable
    fun ListingScreen() {
        // Dummy composable for the screen you navigate to
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("List Your Vehicle Screen")
        }
    }


}