package com.example.rentalapp.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.rentalapp.data.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
@Composable
fun ProfileScreen2(
    navController: NavController,
    name: String,
    phone: String,
    viewModel: UserViewModel
) {
    val database = Firebase.database.reference
    val auth = Firebase.auth
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val currentStep = 2
    var selectedImageUri by remember { mutableStateOf<Uri>(Uri.parse("android.resource://com.example.rentalapp/drawable/baseline_face_24")) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if(uri != null) {
                selectedImageUri = uri
            } else {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val storage = FirebaseStorage.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
//                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(currentStep / 2f) // Adjust width based on step
                        .height(4.dp)
                        .background(Color(0xFF35C770))
                )
//                Spacer(modifier = Modifier.height(8.dp))
                CenterAlignedTopAppBar(
                    title = { Text(text = "$currentStep/2 steps completed") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }

        },
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create your profile",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Image Placeholder or Selected Image
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(selectedImageUri)
                                    .crossfade(true)
                                    .build()
                            ),
                            contentDescription = "Selected profile picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default profile picture",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { launcher.launch("image/*")}) {
                    Text("Change Profile Picture")
                }
                // You can add a Text below to indicate "Change Profile Picture"
            }

            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                label = { Text(text = "Email Address") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = { Text(text = "Password") },
                modifier = Modifier.fillMaxWidth()
            )
//            OutlinedTextField(
//                value = password,
//                onValueChange = {password = it},
//                label = { Text(text = "Confirm Password") },
//                modifier = Modifier.fillMaxWidth()
//            )

            Button(
                onClick = {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
//                                Log.d(TAG, "Name: $name")
//                                Log.d(TAG, "Phone: $phone")
                                Log.d(TAG, "createUserWithEmail:success")
                                val user = auth.currentUser
                                database.child("users").child(user!!.uid).setValue(
                                    mapOf(
                                        "name" to name,
                                        "phone" to phone
                                    )
                                )

                                uploadUserPhoto(navController,user.uid, selectedImageUri, storage, firestore)
                                viewModel.setCurrentUser(user)
//                                navController.navigate("dashboard")
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                                Toast.makeText(
//                                    baseContext,
//                                    "Authentication failed.",
//                                    Toast.LENGTH_SHORT,
//                                ).show()
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Continue")
            }
        }
    }
}

fun uploadUserPhoto(
    navController: NavController,
    uid: String,
    selectedImageUri: Uri,
    storage: FirebaseStorage,
    firestore: FirebaseFirestore
) {
//    if(selectedImageUri == null) {
//        selectedImageUri = Uri.parse("android.resource://com.example.rentalapp/drawable/baseline_face_24")
//    }
//    val userDocRef = firestore.collection("users").document(uid)
    val imageRef = storage.reference.child("users/$uid")
    val uploadTask = imageRef.putFile(selectedImageUri)

    uploadTask.continueWithTask { task ->
        if (!task.isSuccessful) {
            task.exception?.let { throw it }
        }
        imageRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result.toString()

            val user = hashMapOf(
                "photoUrl" to downloadUri
            )

            firestore.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener {
                    Log.d("Success", "Photo added to Firestore : $uid")
                    navController.navigate("dashboard")
                }
                .addOnFailureListener {
                    Log.d("Error", "Failed to add user photo to Firestore")
                }

        }

    }
}
