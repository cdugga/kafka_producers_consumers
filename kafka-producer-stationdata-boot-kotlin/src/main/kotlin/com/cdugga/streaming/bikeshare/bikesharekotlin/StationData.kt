package com.cdugga.streaming.bikeshare.bikesharekotlin

data class StationData(val schemeId: Int, 
                        val schemeShortName: String,
                        val stationId: Int,
                        val name: String,
                        val nameIrish: String,
                        val docksCount: Int,
                        val bikesAvailable: Int,
                        val docksAvailable: Int,
                        val status: Int,
                        val lattitude: Double,
                        val longitude: Int,
                        val dateStatus: String)