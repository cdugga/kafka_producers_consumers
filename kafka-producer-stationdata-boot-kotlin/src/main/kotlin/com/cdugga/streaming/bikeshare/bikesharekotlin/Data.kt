package com.cdugga.streaming.bikeshare.bikesharekotlin

class Data(val responseCode: Int, 
                val responseText: String, 
                val responseDate: String,
                val data: Array<StationData>)