package com.example.panalsuite.ui.Navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val TICKET_LIST = "ticketList"
    const val TICKET_DETAIL = "ticketDetail/{ticketId}"
    const val CREATE_TICKET = "createTicket"
    const val DASHBOARD = "dashboard"

    fun ticketDetail(ticketId: Int) = "ticketDetail/$ticketId"
}