package com.example.panalsuite.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val TICKET_LIST = "ticketList"
    const val TICKET_DETAIL = "ticketDetail/{ticketId}"
    const val CREATE_TICKET = "createTicket"
    const val DASHBOARD = "dashboard"

    const val MANAGEMENT = "Management"
    const val  PROFILE = "profile"

    fun ticketDetail(ticketId: Int) = "ticketDetail/$ticketId"
}