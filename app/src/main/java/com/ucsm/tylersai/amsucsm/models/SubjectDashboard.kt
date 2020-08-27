package com.ucsm.tylersai.amsucsm.models

data class SubjectDashboard(
    var subjectCode: String,
    var name: String,
    var day: String,
    var teacherId: String,
    var room:String,
    var time:String,
    var percentage:String
){
    constructor() : this("","","","","","","")
}
