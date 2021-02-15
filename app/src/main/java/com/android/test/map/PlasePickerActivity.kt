package com.android.test.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.test.R
import com.android.test.db.DatabaseBuilder
import com.android.test.models.Bookmark
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_location_picker.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

class PlasePickerActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = PlasePickerActivity::class.java.simpleName
    private var userAddress: String? = ""
    private var userState: String? = ""
    private var userCity: String? = ""
    private var userPostalCode: String? = ""
    private var userCountry: String? = ""
    private var userAddressline2: String? = ""
    private val userAddressline1 = ""
    private var addressBundle: Bundle? = null
    private val addressdetails: List<*>? = null
    private var mLatitude = 0.0
    private var mLongitude = 0.0
    private var place_id: String? = ""
    private var place_url = " "
    private var mMap: GoogleMap? = null
    private var mLocationPermissionGranted = false
   /* private var imgSearch: TextView? = null
    private var citydetail: TextView? = null
    private val addressline1: EditText? = null
    private var addressline2: EditText? = null*/
    var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1

    //inital zoom
    private val previousZoomLevel = -1.0f
    private var isZooming = false

    //Declaration of FusedLocationProviderClient
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val filterTaskList: MutableList<AsyncTask<*, *, *>> = ArrayList()
    var regex = "^(-?\\d+(\\.\\d+)?),\\s*(-?\\d+(\\.\\d+)?)$"
    var latLongPattern = Pattern.compile(regex)
    private var doAfterPermissionProvided = 0
    private var doAfterLocationSwitchedOn = 1
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        setContentView(R.layout.activity_location_picker)
        if (supportActionBar != null) supportActionBar!!.hide()
        val imgCurrentloc = findViewById<ImageView>(R.id.imgCurrentloc)
        val txtSelectLocation = findViewById<Button>(R.id.fab_select_location)
        val directionTool = findViewById<ImageView>(R.id.direction_tool)
        val googleMapTool = findViewById<ImageView>(R.id.google_maps_tool)
     /*   imgSearch = findViewById(R.id.imgSearch)
        addressline2 = findViewById(R.id.addressline2)
        citydetail = findViewById(R.id.citydetails)*/


        // Initialize bundle
        addressBundle = Bundle()

        //intitalization of FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //Prepare for Request for current location
        getLocationRequest()

        //define callback of location request
        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                Log.d(TAG, "onLocationAvailability: isLocationAvailable =  " + locationAvailability.isLocationAvailable)
            }

            override fun onLocationResult(locationResult: LocationResult) {
                Log.d(TAG, "onLocationResult: $locationResult")
                if (locationResult == null) {
                    return
                }
                when (doAfterLocationSwitchedOn) {
                    1 -> startParsingAddressToShow()
                    2 ->                         //on click of imgCurrent
                        showCurrentLocationOnMap(false)
                    3 ->                         //on Click of Direction Tool
                        showCurrentLocationOnMap(true)
                }

                //Location fetched, update listener can be removed
                fusedLocationProviderClient?.let {
                    it.removeLocationUpdates(locationCallback)
                }

            }
        }

        // Try to obtain the map from the SupportMapFragment.
        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
        //if you want to open the location on the LocationPickerActivity through intent
        val i = intent
        if (i != null) {
            val extras = i.extras
            if (extras != null) {
                userAddress = extras.getString(MapHelper.ADDRESS)
                //temp -> get lat , log from db
                mLatitude = intent.getDoubleExtra(MapHelper.LATITUDE, 0.0)
                mLongitude = intent.getDoubleExtra(MapHelper.LONGITUDE, 0.0)
            }
        }
        if (savedInstanceState != null) {
            mLatitude = savedInstanceState.getDouble("latitude")
            mLongitude = savedInstanceState.getDouble("longitude")
            userAddress = savedInstanceState.getString("userAddress")
            currentLatitude = savedInstanceState.getDouble("currentLatitude")
            currentLongitude = savedInstanceState.getDouble("currentLongitude")
        }
        if (!MapHelper.isNetworkAvailable(this)) {
            MapHelper.showToast(this, "Please Connect to Internet")
        }
        imgSearch.setOnClickListener(View.OnClickListener {
            if (!Places.isInitialized()) {
                Places.initialize(this@PlasePickerActivity.applicationContext, MapHelper.apiKey)
            }

            // Set the fields to specify which types of place data to return.
            val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)


            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this@PlasePickerActivity)
            this@PlasePickerActivity.startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
        })
        txtSelectLocation.setOnClickListener {
            val intent = Intent()
            // add data into intent and send back to Parent Activity
            intent.putExtra(MapHelper.ADDRESS, imgSearch.getText().toString().trim { it <= ' ' })
            intent.putExtra(MapHelper.LATITUDE, mLatitude)
            intent.putExtra(MapHelper.LONGITUDE, mLongitude)
            intent.putExtra("fullAddress", addressBundle)
            intent.putExtra("id", place_id) //if you want place id
            intent.putExtra("url", place_url) //if you want place url
            this@PlasePickerActivity.setResult(Activity.RESULT_OK, intent)

            val bookmark = Bookmark()
            val SPACE = " , "
            bookmark.address = userAddressline2!!+userPostalCode + SPACE + userState + SPACE + userCountry
            bookmark.name = userCity!!
            bookmark.longitude = mLongitude
            bookmark.latitude = mLatitude

            CoroutineScope(Dispatchers.IO).launch {
                DatabaseBuilder.getInstance(this@PlasePickerActivity).bookmarkDao().insertBookmark(bookmark)

                withContext(Main){
                    finish()
                }
            }

        }
        imgCurrentloc.setOnClickListener {
            showCurrentLocationOnMap(false)
            doAfterPermissionProvided = 2
            doAfterLocationSwitchedOn = 2
        }

        // google maps tools
        directionTool.setOnClickListener {
            showCurrentLocationOnMap(true)
            doAfterPermissionProvided = 3
            doAfterLocationSwitchedOn = 3
        }
        googleMapTool.setOnClickListener { // Default google map
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(
                    "http://maps.google.com/maps?q=loc:$mLatitude, $mLongitude"))
            this@PlasePickerActivity.startActivity(intent)
        }
        try {
            Toast.makeText(applicationContext, this.resources.getString(R.string.edittext_hint), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, this.resources.getString(R.string.edittext_hint), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //after a place is searched
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                userAddress = place.address
                //  addressdetails=place.getAddressComponents();
                imgSearch!!.text = "" + userAddress
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
                place_id = place.id
                place_url = place.websiteUri.toString()
                addMarker()
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i(TAG, status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS) {
            //after location switch on dialog shown
            if (resultCode != Activity.RESULT_OK) {
                //Location not switched ON
                Toast.makeText(this@PlasePickerActivity, "Location Not Available..", Toast.LENGTH_SHORT).show()
            } else {
                // Start location request listener.
                //Location will be received onLocationResult()
                //Once loc recvd, updateListener will be turned OFF.
                Toast.makeText(this, "Fetching Location...", Toast.LENGTH_LONG).show()
                startLocationUpdates()
            }
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarsePermision = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (coarsePermision != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }

        //getSettingsLocation();
        return true
    }

    private fun showCurrentLocationOnMap(isDirectionClicked: Boolean) {
        if (checkAndRequestPermissions()) {
            @SuppressLint("MissingPermission") val lastLocation = fusedLocationProviderClient!!.lastLocation
            lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    mMap!!.clear()
                    if (isDirectionClicked) {
                        currentLatitude = location.latitude
                        currentLongitude = location.longitude
                        //Go to Map for Directions
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(
                                "http://maps.google.com/maps?saddr=$currentLatitude, $currentLongitude&daddr=$mLatitude, $mLongitude"))
                        this@PlasePickerActivity.startActivity(intent)
                    } else {
                        //Go to Current Location
                        mLatitude = location.latitude
                        mLongitude = location.longitude
                        addressByGeoCodingLatLng
                    }
                } else {
                    //Gps not enabled if loc is null
                    settingsLocation
                    Toast.makeText(this@PlasePickerActivity, "Location not Available", Toast.LENGTH_SHORT).show()
                }
            }
            lastLocation.addOnFailureListener { //If perm provided then gps not enabled
//                getSettingsLocation();
                Toast.makeText(this@PlasePickerActivity, "Location Not Availabe", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun resizeMapIcons(iconName: String?, width: Int, height: Int): Bitmap {
        val imageBitmap = BitmapFactory.decodeResource(resources, resources.getIdentifier(iconName, "drawable", packageName))
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }

    private fun addMarker() {
//        userAddress = ""
//        userAddressline2 = ""
//        userCity = ""
//        userPostalCode = ""
//        userCountry = ""
//        userState = ""

        val cameraUpdate: CameraUpdate
        val SPACE = " , "
        val coordinate = LatLng(mLatitude, mLongitude)
        if (mMap != null) {
            val markerOptions: MarkerOptions
            try {
                mMap!!.clear()
                imgSearch!!.text = "" + userAddress
                markerOptions = MarkerOptions().position(coordinate).title(userAddress).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_pointer", 100, 100)))
                cameraUpdate = if (isZooming) {
                    //  camera will not Update
                    CameraUpdateFactory.newLatLngZoom(coordinate, mMap!!.cameraPosition.zoom)
                } else {
                    // camera will Update zoom
                    CameraUpdateFactory.newLatLngZoom(coordinate, 18f)
                }
                mMap!!.animateCamera(cameraUpdate)
                mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                val marker = mMap!!.addMarker(markerOptions)
                //marker.showInfoWindow();
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        try {
            userAddressline2 = userAddressline2!!.substring(0, userAddressline2!!.indexOf(userCity!!))
            // userAddressline.replace(userCity,"");
            //  userAddressline.replace(userPostalCode,"");
            //   userAddressline.replace(userState,"");
            //  userAddressline.replace(userCountry,"");
        } catch (ex: Exception) {
            Log.d(TAG, "address error $ex")
        }
        try {
            addressline2!!.setText(userAddressline2)
            citydetails!!.text = userCity + SPACE + userPostalCode + SPACE + userState + SPACE + userCountry
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.clear()
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.uiSettings.isMapToolbarEnabled = false
        if (mMap!!.isIndoorEnabled) {
            mMap!!.isIndoorEnabled = false
        }
        mMap!!.setInfoWindowAdapter(object : InfoWindowAdapter {
            // Use default InfoWindow frame
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            // Defines the contents of the InfoWindow
            override fun getInfoContents(arg0: Marker): View {
                val v = layoutInflater.inflate(R.layout.info_window_layout, null)

                // Getting the position from the marker
                val latLng = arg0.position
                mLatitude = latLng.latitude
                mLongitude = latLng.longitude
                val tvLat = v.findViewById<TextView>(R.id.address)
                tvLat.text = userAddress
                return v
            }
        })
        mMap!!.uiSettings.isZoomControlsEnabled = true

        // Setting a click event handler for the map
        mMap!!.setOnMapClickListener { latLng ->
            mMap!!.clear()
            mLatitude = latLng.latitude
            mLongitude = latLng.longitude
            Log.e("latlng", latLng.toString() + "")
            isZooming = true
            addMarker()
            if (!MapHelper.isNetworkAvailable(this@PlasePickerActivity)) {
                MapHelper.showToast(this@PlasePickerActivity, "Please Connect to Internet")
            }
            addressByGeoCodingLatLng
        }
        if (checkAndRequestPermissions()) {
            startParsingAddressToShow()
        } else {
            doAfterPermissionProvided = 1
        }
    }// Ignore, should be an impossible error.// Ignore the error.// Cast to a resolvable exception.
    // Show the dialog by calling startResolutionForResult(),
    // and check the result in onActivityResult().
// Location settings are not satisfied. But could be fixed by showing the
    // user a dialog.

    // All location settings are satisfied. The client can initialize location
    // requests here.
    //...
    private val settingsLocation: Unit
        private get() {
            val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest!!)
            val result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
            result.addOnCompleteListener { task ->
                try {
                    val response = task.getResult(ApiException::class.java)
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    //...
                    if (response != null) {
                        val locationSettingsStates = response.locationSettingsStates
                        Log.d(TAG, "getSettingsLocation: $locationSettingsStates")
                        startLocationUpdates()
                    }
                } catch (exception: ApiException) {
                    Log.d(TAG, "getSettingsLocation: $exception")
                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                             // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                val resolvable = exception as ResolvableApiException
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        this@PlasePickerActivity,
                                        REQUEST_CHECK_SETTINGS)
                            } catch (e: SendIntentException) {
                                // Ignore the error.
                            } catch (e: ClassCastException) {
                                // Ignore, should be an impossible error.
                            }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        }
                    }
                }
            }
        }

    /**
     * Show location from intent
     */
    private fun startParsingAddressToShow() {
        //get address from intent to show on map
        if (userAddress == null || userAddress!!.isEmpty()) {

            //if intent does not have address,
            //cell is blank
            showCurrentLocationOnMap(false)
        } else  //check if address contains lat long, then extract
        //format will be lat,lng i.e 19.23234,72.65465
            if (latLongPattern.matcher(userAddress).matches()) {
                val p = Pattern.compile("(-?\\d+(\\.\\d+)?)") // the pattern to search for
                val m = p.matcher(userAddress)

                // if we find a match, get the group
                var i = 0
                while (m.find()) {
                    // we're only looking for 2s group, so get it
                    if (i == 0) mLatitude = m.group().toDouble()
                    if (i == 1) mLongitude = m.group().toDouble()
                    i++
                }
                //show on map
                addressByGeoCodingLatLng
                addMarker()
            } else {
                //get  latlong from String address via reverse geo coding
                //Since lat long not present in db
                if (mLatitude == 0.0 && mLongitude == 0.0) {
                    latLngByRevGeoCodeFromAdd
                } else {
                    // Latlong is more accurate to get exact point on map ,
                    // String address might not be sufficient (i.e Mumbai, Mah..etc)
                    addMarker()
                }
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("latitude", mLatitude)
        outState.putDouble("longitude", mLongitude)
        outState.putString("userAddress", userAddress)
        outState.putBundle("addressBundle", addressBundle)
        outState.putDouble("currentLatitude", currentLatitude)
        outState.putDouble("currentLongitude", currentLongitude)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // app icon in action bar clicked; goto parent activity.
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        finish()
    }//Cancel previous tasks and launch this one

    //Get string address by geo coding from lat long
    private val addressByGeoCodingLatLng: Unit
        private get() {

            //Get string address by geo coding from lat long
            if (mLatitude != 0.0 && mLongitude != 0.0) {
                if (MapHelper.popupWindow != null && MapHelper.popupWindow.isShowing) {
                    MapHelper.hideProgress()
                }
                Log.d(TAG, "getAddressByGeoCodingLatLng: START")
                //Cancel previous tasks and launch this one
                for (prevTask in filterTaskList) {
                    prevTask.cancel(true)
                }
                filterTaskList.clear()
                val asyncTask = GetAddressFromLatLng()
                filterTaskList.add(asyncTask)
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mLatitude, mLongitude)
            }
        }//Cancel previous tasks and launch this one

    //Get string address by geo coding from lat long
    private val latLngByRevGeoCodeFromAdd: Unit
        private get() {

            //Get string address by geo coding from lat long
            if (mLatitude == 0.0 && mLongitude == 0.0) {
                if (MapHelper.popupWindow != null && MapHelper.popupWindow.isShowing) {
                    MapHelper.hideProgress()
                }
                Log.d(TAG, "getLatLngByRevGeoCodeFromAdd: START")
                //Cancel previous tasks and launch this one
                for (prevTask in filterTaskList) {
                    prevTask.cancel(true)
                }
                filterTaskList.clear()
                val asyncTask = GetLatLngFromAddress()
                filterTaskList.add(asyncTask)
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userAddress)
            }
        }

    @SuppressLint("StaticFieldLeak")
    private inner class GetAddressFromLatLng : AsyncTask<Double?, Void?, Bundle?>() {
        var latitude: Double? = null
        var longitude: Double? = null
        override fun onPreExecute() {
            super.onPreExecute()
            MapHelper.showProgress(this@PlasePickerActivity)
        }

         override fun doInBackground(vararg p0: Double?): Bundle? {
            return try {
                latitude = p0[0]
                longitude = p0[1]
                val geocoder: Geocoder
                val addresses: List<Address>?
                geocoder = Geocoder(this@PlasePickerActivity, Locale.getDefault())
                val sb = StringBuilder()

                //get location from lat long if address string is null
                addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
                if (addresses != null && addresses.size > 0) {
                    val address = addresses[0].getAddressLine(0)
                    if (address != null) addressBundle!!.putString("addressline2", address)
                    sb.append(address).append(" ")
                    val city = addresses[0].locality
                    if (city != null) addressBundle!!.putString("city", city)
                    sb.append(city).append(" ")
                    val state = addresses[0].adminArea
                    if (state != null) addressBundle!!.putString("state", state)
                    sb.append(state).append(" ")
                    val country = addresses[0].countryName
                    if (country != null) addressBundle!!.putString("country", country)
                    sb.append(country).append(" ")
                    val postalCode = addresses[0].postalCode
                    if (postalCode != null) addressBundle!!.putString("postalcode", postalCode)
                    sb.append(postalCode).append(" ")
                    // return sb.toString();
                    addressBundle!!.putString("fulladdress", sb.toString())
                    addressBundle
                } else {
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                addressBundle!!.putBoolean("error", true)
                addressBundle
                //return roundAvoid(latitude) + "," + roundAvoid(longitude);
            }

            // return bu;
        }

        // setting address into different components
        override fun onPostExecute(userAddress: Bundle?) {
            super.onPostExecute(userAddress)
            this@PlasePickerActivity.userAddress = userAddress!!.getString("fulladdress")
            userCity = userAddress.getString("city")
            userState = userAddress.getString("state")
            userPostalCode = userAddress.getString("postalcode")
            userCountry = userAddress.getString("country")
            userAddressline2 = userAddress.getString("addressline2")
            MapHelper.hideProgress()
            addMarker()
        }
    }

    private inner class GetLatLngFromAddress : AsyncTask<String?, Void?, LatLng>() {
        override fun onPreExecute() {
            super.onPreExecute()
            MapHelper.showProgress(this@PlasePickerActivity)
        }

        protected override fun doInBackground(vararg p0: String?): LatLng? {
            var latLng = LatLng(0.0, 0.0)
            try {
                val geocoder: Geocoder
                val addresses: List<Address>?
                geocoder = Geocoder(this@PlasePickerActivity, Locale.getDefault())

                //get location from lat long if address string is null
                addresses = geocoder.getFromLocationName(userAddress?.get(0).toString(), 1)
                if (addresses != null && addresses.size > 0) {
                    latLng = LatLng(addresses[0].latitude, addresses[0].longitude)
                }
            } catch (ignored: Exception) {
            }
            return latLng
        }

        override fun onPostExecute(latLng: LatLng) {
            super.onPostExecute(latLng)
            mLatitude = latLng.latitude
            mLongitude = latLng.longitude
            MapHelper.hideProgress()
            addMarker()
        }
    }

    fun roundAvoid(value: Double): Double {
        val scale = Math.pow(10.0, 6.0)
        return Math.round(value * scale) / scale
    }

    override fun onDestroy() {
        super.onDestroy()
        for (task in filterTaskList) {
            task.cancel(true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        mLocationPermissionGranted = false
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //Do tasks for which permission was granted by user in onRequestPermission()
        if (!isFinishing && mLocationPermissionGranted) {
            // perform action required b4 asking permission
            mLocationPermissionGranted = false
            when (doAfterPermissionProvided) {
                1 -> startParsingAddressToShow()
                2 -> showCurrentLocationOnMap(false)
                3 -> showCurrentLocationOnMap(true)
            }
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this@PlasePickerActivity, "Location not Available", Toast.LENGTH_SHORT).show()
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */)
                .addOnSuccessListener { Log.d(TAG, "startLocationUpdates: onSuccess: ") }
                .addOnFailureListener { e ->
                    if (e is ApiException) {
                        Log.d(TAG, "startLocationUpdates: " + e.message)
                    } else {
                        Log.d(TAG, "startLocationUpdates: " + e.message)
                    }
                }
    }

    private fun getLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = 10000
        locationRequest!!.fastestInterval = 3000
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    companion object {
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 2
    }
}