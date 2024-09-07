package com.example.rentalapp.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract.Contacts.Photo
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentalapp.R
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.example.rentalapp.data.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID


@SuppressLint("UnusedMaterialScaffoldPaddingParameter",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
@Composable
fun CarListingScreen(
    navController: NavController,
    viewModel: UserViewModel
) {
    var model by remember { mutableStateOf("") }
    var make by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }
    val location = GeoPoint(0.0, 0.0)
    var dailyRentalPrice by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
//    var photo by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val vehicleTypes = listOf("2 Wheeler", "4 Wheeler")
    var expanded by remember { mutableStateOf(false) }
    val datePickerState =
        rememberDatePickerState(
            initialDisplayedMonthMillis = System.currentTimeMillis(),
            yearRange = 2000..2025
        )

    var photos by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val context = LocalContext.current


    // Launcher for selecting an image from the device
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris: List<Uri?>? ->
            if(uris != null) {
                photos = photos + uris.filterNotNull()
            }
        }
    )
    val storage = FirebaseStorage.getInstance()
    val firestore = FirebaseFirestore.getInstance()


    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    val currentUser = viewModel.currentUser

    var userData by remember { mutableStateOf<HashMap<String, String>?>(null) }
    val uid = currentUser?.uid
    val db = FirebaseDatabase.getInstance().getReference("users").child(uid!!)

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
//                        showError = true
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseDatabase", "Failed to read data for user: $uid", error.toException())
                }
            }
            db.addValueEventListener(valueEventListener)
        }
    }

    val name = userData?.get("name")
    val phone = userData?.get("phone")



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("List your car") },
                navigationIcon = {
                    IconButton(onClick = {navController.navigateUp()}) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = vehicleType,
                    onValueChange = { vehicleType = it },
                    label = { Text("Vehicle Type") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) ImageVector.vectorResource(id = R.drawable.baseline_arrow_drop_up_24) else ImageVector.vectorResource(id = R.drawable.baseline_arrow_drop_down_24),
                                contentDescription = "Expand"
                            )
                        }
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    vehicleTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                vehicleType = type
                                expanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Model") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = make,
                onValueChange = { make = it },
                label = { Text("Make/Company/Manufacturer") },
                modifier = Modifier.fillMaxWidth()
            )
//                OutlinedTextField(
//                    value = location,
//                    onValueChange = { location = it },
//                    label = { Text("Location") },
//                    modifier = Modifier.fillMaxWidth(),
//                    trailingIcon = {
//                        Icon(
//                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_location_on_24),
//                            contentDescription = "Location",
//                        )
//                    }
//                )
            OutlinedTextField(
                value = dailyRentalPrice,
                onValueChange = { dailyRentalPrice = it },
                label = { Text("Daily rental price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start date") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_date_range_24),
                                contentDescription = "Calendar",
                            )
                        }
                    }
                )
                if (showStartDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = {
                            startDate = selectedDate
                            showStartDatePicker = false
                        },
                        content = { DatePicker(state = datePickerState)},
                        confirmButton = {
                            Button(onClick = {
                                showStartDatePicker = false
                                startDate = selectedDate
                            } ) {
                                Text("confirm")
                            }

                        },
                        dismissButton = {
                            Button(onClick = { showStartDatePicker = false }) {
                                Text("cancel")
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End date") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_date_range_24),
                                contentDescription = "Calendar",
                            )
                        }
                    }
                )
                if (showEndDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = {
                            showEndDatePicker = false
                        },
                        content = { DatePicker(state = datePickerState)},
                        confirmButton = {
                            Button(onClick = {
                                showEndDatePicker = false
                                endDate = selectedDate
                            } ) {
                                Text("confirm")
                            }

                        },
                        dismissButton = {
                            Button(onClick = { showEndDatePicker = false }) {
                                Text("cancel")
                            }
                        }
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(photos.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                        ) {
                            items(photos.take(photos.size)) { uri ->
                                Image(
                                    bitmap = BitmapFactory.decodeStream(
                                        context.contentResolver.openInputStream(uri)
                                    ).asImageBitmap(),
                                    contentDescription = "Uploaded Image",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                            }
                        }

                    } else {
                        // Placeholder text if no photos are selected
                        Text(
                            text = "Upload photos",
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        )
                    }

                    // Upload icon
                    IconButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Upload photo",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }


            Text(
                text = "Photos should show the vehicle from all angles",
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = {
//                    uploadMultipleImages(photos)
                    if (phone != null) {
                        if (name != null) {
                            addVehicle(
                                photos = photos,
                                storage = storage,
                                firestore = firestore,
                                vehicleType = vehicleType,
                                make = make,
                                model = model,
                                location = location,
                                dailyRentalPrice = dailyRentalPrice,
                                startDate = startDate,
                                endDate = endDate,
                                owner = name,
                                ownerPhone = phone
                            )
                        }
                    }
                    navController.navigate("dashboard")
                          },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Post my listing")
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(Date(millis))
}

//@Composable
fun addVehicle(
    photos: List<Uri>,
    storage: FirebaseStorage,
    firestore: FirebaseFirestore,
    vehicleType: String = "",
    make: String = "",
    model: String = "",
    location: GeoPoint? = null,
    dailyRentalPrice: String = "",
    startDate: String = "",
    endDate: String = "",
    owner: String = "",
    ownerPhone: String = ""
) {
    val imageUrls = mutableListOf<String>()
    for(photo in photos) {
        val imageRef = storage.reference.child("vehicles/${UUID.randomUUID()}")
        val uploadTask = imageRef.putFile(photo)

        uploadTask.continueWithTask{ task ->
            if(!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val downloadUri = task.result.toString()
                imageUrls.add(downloadUri)

                if(imageUrls.size == photos.size) {
                    // All images have been uploaded, you can now add the vehicle to Firestore
                    val vehicle = hashMapOf(
                        "vehicleType" to vehicleType,
                        "make" to make,
                        "model" to model,
                        "location" to location,
                        "dailyRentalPrice" to dailyRentalPrice.toDouble(),
                        "startDate" to startDate,
                        "endDate" to endDate,
                        "imageUrls" to imageUrls,
                        "owner" to owner,
                        "ownerPhone" to ownerPhone
                    )
                    firestore.collection("vehicles")
                        .add(vehicle)
                        .addOnSuccessListener {
                            Log.d("Success", "Vehicle added to Firestore")
                        }
                        .addOnFailureListener {
                            Log.d("Error", "Failed to add vehicle to Firestore")
                        }
                }
            }
        }
    }
}

