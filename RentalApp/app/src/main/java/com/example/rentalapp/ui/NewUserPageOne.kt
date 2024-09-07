package com.example.rentalapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rentalapp.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
@Composable
fun ProfileScreen1(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var phoneNo by remember { mutableStateOf("") }
    val selectedGender = remember { mutableStateOf("") }
    val currentStep = remember { mutableIntStateOf(1) }
    var dateOfBirth by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState =
        rememberDatePickerState(
            initialDisplayedMonthMillis = System.currentTimeMillis(),
            yearRange = 1900..2025
        )
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .padding(top = 20.dp)
                ,
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
//                Spacer(modifier = Modifier.height(0.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(currentStep.intValue / 2f) // Adjust width based on step
                        .height(4.dp)
                        .background(Color(0xFF35C770))
                )
//                Spacer(modifier = Modifier.height(8.dp))
                CenterAlignedTopAppBar(
                    title = { Text(text = "${currentStep.intValue.toString()}/2 steps completed") },
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
                .padding(16.dp)
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create your profile",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = {fullName = it},
                label = { Text(text = "Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    label = { Text("Date of Birth") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_date_range_24),
                                contentDescription = "Calendar",
                            )
                        }
                    }
                )
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = {
                            dateOfBirth = selectedDate
                            showDatePicker = false
                        },
                        content = { DatePicker(state = datePickerState)},
                        confirmButton = {
                            Button(onClick = {
                                showDatePicker = false
                                dateOfBirth = selectedDate
                            } ) {
                                Text("confirm")
                            }

                        },
                        dismissButton = {
                            Button(onClick = { showDatePicker = false }) {
                                Text("cancel")
                            }
                        }
                    )
                }
            }
            
            OutlinedTextField(
                value = phoneNo,
                onValueChange = {phoneNo = it},
                label = { Text(text = "Phone no...") },
                modifier = Modifier
                    .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Text(text = "Gender", modifier = Modifier.padding(end = 300.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedGender.value == "Male",
                    onClick = { selectedGender.value = "Male" }
                )
                Text("Male")

                RadioButton(
                    selected = selectedGender.value == "Female",
                    onClick = { selectedGender.value = "Female" }
                )
                Text("Female")

                RadioButton(
                    selected = selectedGender.value == "Prefer not to",
                    onClick = { selectedGender.value = "Prefer not to" }
                )
                Text("Prefer not to")
            }

            Button(
                onClick = {navController.navigate("New User 2/${fullName}/${phoneNo}") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Continue")
            }
        }
    }
}



@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(Date(millis))
}