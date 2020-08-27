package com.ucsm.tylersai.amsucsm.models

data class PreStudentList (
    var major: String,
    var mkpt: String,
    var name: String
)
{
    constructor() : this("","","")
}