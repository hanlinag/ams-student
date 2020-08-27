package com.ucsm.tylersai.amsucsm.models

data class Student(
     var name :String,
     var major :String,
     var subject:String,
     var phone: String,
     var email:String,
     var address:String,
     var password:String,
     var profileurl: String,
     var mkpt:String
){
     constructor(): this("","","","","","","","",""){

     }
}