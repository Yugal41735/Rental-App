package com.example.rentalapp.data

import androidx.lifecycle.ViewModel

class VehicleViewModel: ViewModel() {
    private var _currentVehicle: Vehicle? = null
    val currentVehicle: Vehicle?
        get() = _currentVehicle

    fun setCurrentVehicle(vehicle: Vehicle?) {
        _currentVehicle = vehicle
    }

}