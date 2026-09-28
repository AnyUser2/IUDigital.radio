package com.iudigital.radio.ui

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.iudigital.radio.model.Stations
import com.iudigital.radio.util.vibrateShort

/**
 * Pantalla raíz. Aquí vive el estado (RF-04), el permiso de cámara (RF-02/RF-03)
 * y el reproductor ExoPlayer (RF-07).
 */
@Composable
fun RadioScreen() {
    val context = LocalContext.current

    // ---- RF-04: estado que sobrevive a rotaciones ----
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    var isMuted by rememberSaveable { mutableStateOf(false) }
    var selectedStationId by rememberSaveable { mutableStateOf(Stations.first().id) }
    var photo by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    var status by remember { mutableStateOf("Detenida") }

    val selectedIndex = Stations.indexOfFirst { it.id == selectedStationId }.coerceAtLeast(0)
    val selectedStation = Stations[selectedIndex]

    // ---- RF-07: ExoPlayer ----
    val player = remember { ExoPlayer.Builder(context).build() }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                status = when (state) {
                    Player.STATE_BUFFERING -> "Conectando…"
                    Player.STATE_READY -> if (player.playWhenReady) "En el aire" else "En pausa"
                    Player.STATE_ENDED -> "Transmisión finalizada"
                    else -> if (player.playWhenReady) "Conectando…" else "Detenida"
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (player.playbackState == Player.STATE_READY) {
                    status = if (playWhenReady) "En el aire" else "En pausa"
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                isPlaying = false
                status = "Sin conexión. Revisa tu internet y vuelve a intentar."
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    // Cambia de emisora cuando se selecciona otra en la lista
    LaunchedEffect(selectedStation.id) {
        player.setMediaItem(MediaItem.fromUri(selectedStation.streamUrl))
        player.prepare()
        player.playWhenReady = isPlaying
    }
    LaunchedEffect(isPlaying) {
        player.playWhenReady = isPlaying
        if (!isPlaying && player.playbackState == Player.STATE_IDLE) status = "Detenida"
    }
    LaunchedEffect(isMuted) { player.volume = if (isMuted) 0f else 1f }

    // ---- RF-02 / RF-03: cámara con permiso en tiempo de ejecución ----
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> if (bitmap != null) photo = bitmap }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(null)
        else Toast.makeText(
            context,
            "Activa el permiso de cámara en Ajustes para tomar tu foto.",
            Toast.LENGTH_LONG
        ).show()
    }

    val onTakePhoto: () -> Unit = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) cameraLauncher.launch(null)
        else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // ---- Acciones del reproductor (con vibración: RF-05) ----
    val onPlay: () -> Unit = { context.vibrateShort(); isPlaying = true }
    val onPause: () -> Unit = { context.vibrateShort(); isPlaying = false }
    val onMute: () -> Unit = { context.vibrateShort(); isMuted = !isMuted }
    val onSelectStation: (String) -> Unit = { id ->
        selectedStationId = id
        isPlaying = true      // al elegir una emisora empieza a sonar
    }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val header: @Composable () -> Unit = {
        ProfileHeader(photo = photo, onTakePhoto = onTakePhoto)
    }
    val playerCard: @Composable () -> Unit = {
        PlayerCard(
            station = selectedStation,
            stationIndex = selectedIndex,
            stationCount = Stations.size,
            isPlaying = isPlaying,
            isMuted = isMuted,
            status = status,
            onPlay = onPlay,
            onPause = onPause,
            onMute = onMute,
        )
    }

    if (isLandscape) {
        Row(Modifier.fillMaxSize().padding(16.dp)) {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                header()
                Spacer(Modifier.height(12.dp))
                playerCard()
            }
            Spacer(Modifier.width(16.dp))
            StationList(
                stations = Stations,
                selectedId = selectedStationId,
                isPlaying = isPlaying,
                onSelect = onSelectStation,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            header()
            Spacer(Modifier.height(12.dp))
            playerCard()
            Spacer(Modifier.height(12.dp))
            StationList(
                stations = Stations,
                selectedId = selectedStationId,
                isPlaying = isPlaying,
                onSelect = onSelectStation,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}
