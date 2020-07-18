package com.shpakovskiy.itunestops.parser

interface Parser {

    fun parse(xmlData: String?): Boolean
}