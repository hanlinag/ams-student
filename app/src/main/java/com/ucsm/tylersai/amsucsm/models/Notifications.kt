package com.ucsm.tylersai.amsucsm.models

data class Notifications (val title: String,
                          val body: String,
                          val date: String,
                          val key: String,
                          var targetUser: String
                         ){
    constructor():this("","","","","")
}