package com.tqc.rentroom.entities

class RoomModel constructor(val name: String ? = "", val priceAft: Int = 0,
                            val priceNight: Int = 0, val priceAll: Int = 0,
                            val status: Int = 0, val idRoom: String ? = "0") {
}