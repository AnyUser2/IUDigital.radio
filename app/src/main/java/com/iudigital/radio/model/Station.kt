package com.iudigital.radio.model

/** Emisora de radio disponible en la lista. */
data class Station(
    val id: String,
    val name: String,
    val genre: String,
    val streamUrl: String,
)

/** Emisoras públicas de streaming MP3 (SomaFM y Radio Paradise). */
val Stations: List<Station> = listOf(
    Station("groove", "Groove Salad", "Downtempo y ambient", "https://ice1.somafm.com/groovesalad-128-mp3"),
    Station("deepspace", "Deep Space One", "Ambient espacial", "https://ice1.somafm.com/deepspaceone-128-mp3"),
    Station("lush", "Lush", "Vocales suaves y electrónica", "https://ice1.somafm.com/lush-128-mp3"),
    Station("secretagent", "Secret Agent", "Lounge y espionaje", "https://ice1.somafm.com/secretagent-128-mp3"),
    Station("defcon", "DEF CON Radio", "Música para hackers", "https://ice1.somafm.com/defcon-128-mp3"),
    Station("paradise", "Radio Paradise", "Mezcla ecléctica", "https://stream.radioparadise.com/mp3-128"),
)
