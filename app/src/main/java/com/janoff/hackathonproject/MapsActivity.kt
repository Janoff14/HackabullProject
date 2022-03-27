package com.janoff.hackathonproject

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.toObject
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.api.LogDescriptor
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.DocumentSnapshot

import com.google.firebase.firestore.QuerySnapshot

import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var latLng: LatLng

    lateinit var db: DocumentReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupLocClient()


    }

    private fun sendData(latLng: LatLng) {
        Log.d("1111", "onCreate: dfdfds")
        db = FirebaseFirestore.getInstance().document("")


        try {
            val user = hashMapOf<String, Any>()
            user["FName"] = "Boburjon"
            user["LName"] = "Usmonov"
            user["Email"] = "bobUsmonov@gmail.com"
            user["Student Major"] = "CS"
            user["Location"] = latLng
            db.collection("Students").document("ID1").set(user)
                .addOnSuccessListener { void: Void? ->
                    Toast.makeText(
                        this,
                        "Successfully uploaded to the database :)",
                        Toast.LENGTH_LONG
                    ).show()
                }.addOnFailureListener { exception: java.lang.Exception ->
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }


    private lateinit var fusedLocClient: FusedLocationProviderClient
    // use it to request location updates and get the latest location

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap //initialise map
        getCurrentLocation()
    }
    private fun setupLocClient() {
        fusedLocClient =
            LocationServices.getFusedLocationProviderClient(this)
    }

    // prompt the user to grant/deny access
    private fun requestLocPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), //permission in the manifest
            REQUEST_LOCATION)
    }

    companion object {
        private const val REQUEST_LOCATION = 1 //request code to identify specific permission request
        private const val TAG = "MapsActivity" // for debugging
    }

    private fun getCurrentLocation() {
        // Check if the ACCESS_FINE_LOCATION permission was granted before requesting a location
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            // If the permission has not been granted, then requestLocationPermissions() is called.
            requestLocPermissions()
        } else {

            fusedLocClient.lastLocation.addOnCompleteListener {
                // lastLocation is a task running in the background
                val location = it.result //obtain location
                //Get a reference to the database, so your app can perform read and write operations
                val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                val ref: DatabaseReference = database.getReference("test")
                if (location != null) {

                    latLng = LatLng(location.latitude, location.longitude)


                    sendData(latLng)

                    // create a marker at the exact location
                    map.addMarker(MarkerOptions().position(latLng)
                        .title("You are currently here!"))
                    // create an object that will specify how the camera will be updated
                    val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)

                    map.moveCamera(update)
                    //Save the location data to the database
                    ref.setValue(location)
                } else {
                    // if location is null , log an error message
                    Log.e(TAG, "No location found")
                }


            }
        }
    }

    private fun fetchData() {
            val docRef = db.collection("Students")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("sucess", "onCreate: ${document.documents}")
                } else {
                    Log.d("TAG", "onCreate: no such docs")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "onCreate: get failed with", exception)
            }

        val count = getSize(db)

        var latLngList: ArrayList<LatLng> = ArrayList()

        for (i in 0 until count){
//           latLngList.add(docRef.)
        }

    }

    private fun getSize(db: DocumentReference): Int {
        var count = 0
        db.collection("Store")
            .get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                override fun onComplete(task: Task<QuerySnapshot?>) {
                    if (task.isSuccessful()) {
                        for (document in task.getResult()!!) {
                            count++
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException())
                    }
                }
            })
        return count
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //check if the request code matches the REQUEST_LOCATION
        if (requestCode == REQUEST_LOCATION)
        {
            //check if grantResults contains PERMISSION_GRANTED.If it does, call getCurrentLocation()
            if (grantResults.size == 1 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                //if it doesn`t log an error message
                Log.e(TAG, "Location permission denied")
            }
        }
    }



}