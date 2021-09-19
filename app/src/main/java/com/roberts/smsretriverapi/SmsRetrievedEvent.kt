package com.roberts.smsretriverapi

data class SmsRetrievedEvent (
    val timedOut: Boolean,
    val message: String
    )
