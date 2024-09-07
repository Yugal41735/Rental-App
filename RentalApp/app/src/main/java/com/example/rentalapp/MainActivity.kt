package com.example.rentalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rentalapp.data.UserViewModel
import com.example.rentalapp.data.Vehicle
import com.example.rentalapp.data.VehicleViewModel
import com.example.rentalapp.ui.CarListingScreen
import com.example.rentalapp.ui.DashBoardScreen
import com.example.rentalapp.ui.ProfileScreen1
import com.example.rentalapp.ui.ProfileScreen2
import com.example.rentalapp.ui.RentalApp
import com.example.rentalapp.ui.SignInScreen
import com.example.rentalapp.ui.VehicleDetailsScreen
import com.example.rentalapp.ui.theme.RentalAppTheme

class MainActivity : ComponentActivity() {
//    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
//        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentalAppTheme {
                val navController = rememberNavController()
                val userViewModel = viewModel<UserViewModel>()
                val vehicleViewModel = viewModel<VehicleViewModel>()

                NavHost(navController = navController, startDestination = "startingPage") {
                    composable("startingPage") { RentalApp(navController) }
                    composable("dashboard") {
                        DashBoardScreen(
                            navController = navController,
                            viewModel = userViewModel,
                            vehicleViewModel = vehicleViewModel
                        )
                    }
                    composable("posting") {
                        CarListingScreen(
                            navController = navController,
                            viewModel = userViewModel
                        )
                    }
                    composable("New User 1") { ProfileScreen1(navController)}
                    composable("New User 2/{name}/{phone}") {
                        ProfileScreen2(
                            navController=navController,
                            name = it.arguments?.getString("name") ?: "",
                            phone = it.arguments?.getString("phone") ?: "",
                            viewModel = userViewModel
                        )
                    }
                    composable("Sign In") {
                        SignInScreen(
                            navController = navController,
                            viewModel = userViewModel
                        )
                    }
                    composable("Vehicle Detailed Info") {
                        VehicleDetailsScreen(
                            navController = navController,
                            viewModel = vehicleViewModel
                        )
                    }
                }
            }
        }
    }
}
