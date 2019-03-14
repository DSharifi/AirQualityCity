package com.example.myapplication
import com.github.salomonbrys.kotson.*
import com.google.gson.Gson



var restaurantJson =
    "{ 'name':'Future Studio Steak House', 'owner':{ 'name':'Christian', 'address':{ 'city':'Magdeburg', 'country':'Germany', 'houseNumber':'42', 'street':'Main Street'}},'cook':{ 'age':18, 'name': 'Marcus', 'salary': 1500 }, 'waiter':{ 'age':18, 'name': 'Norman', 'salary': 1000}}"


data class Restaurant (
    internal var name: String? = null,

    internal var owner: Owner? = null,
    internal var cook: Cook? = null,
    internal var waiter: Waiter? = null
    )

data class Owner (
    internal var name: String? = null,
    internal var address: UserAddress? = null
    )

data class Cook (
    internal var name: String? = null,
    internal var age: Int = 0,
    internal var salary: Int = 0
    )

data class Waiter (
    internal var name: String? = null,
    internal var age: Int = 0,
    internal var salary: Int = 0
)

data class UserAddress (
    internal var street: String? = null,
    internal var houseNumber: String? = null,
    internal var city: String? = null,
    internal var country: String? = null
)


fun main() {
    val restaurantJson =
        "{ 'name':'Future Studio Steak House', 'owner':{ 'name':'Christian', 'address':{ 'city':'Magdeburg', 'country':'Germany', 'houseNumber':'42', 'street':'Main Street'}},'cook':{ 'age':18, 'name': 'Marcus', 'salary': 1500 }, 'waiter':{ 'age':18, 'name': 'Norman', 'salary': 1000}}"

    val gson = Gson()
    val restaurantObject = gson.fromJson<Restaurant>(restaurantJson)

    println(restaurantObject)
}