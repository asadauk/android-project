package com.app.softwarehousemanager.models


data class UserGroup(
    var  groupId: String = "",
    var  groupName: String = "",
    var  createdBy: String = "",
    var  members: List<String> = listOf()
)
