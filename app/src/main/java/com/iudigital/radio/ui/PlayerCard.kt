package com.iudigital.radio.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.iudigital.radio.model.Station
import com.iudigital.radio.ui.theme.DialIvory
import com.iudigital.radio.ui.theme.DialMuted
import com.iudigital.radio.ui.theme.DialNeedle

/** RF-04 / RF-05: reproductor central con Play, Pause y Mute. */
@Composable
fun PlayerCard(
    station: Station,
    stationIndex: Int,
    stationCount: Int,
    isPlaying: Boolean,
    isMuted: Boolean,
    status: String,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onMute: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FrequencyDial(
                stationIndex = stationIndex,
                stationCount = stationCount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(station.name, style = MaterialTheme.typography.headlineMedium)
            Text(
                station.genre,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                status,
                style = MaterialTheme.typography.labelMedium,
                color = if (isPlaying) DialNeedle else DialMuted
            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlButton(
                    icon = Icons.Rounded.PlayArrow,
                    label = "Reproducir",
                    highlighted = isPlaying,
                    onClick = onPlay,
                )
                ControlButton(
                    icon = Icons.Rounded.Pause,
                    label = "Pausar",
                    highlighted = !isPlaying,
                    onClick = onPause,
                )
                ControlButton(
                    icon = if (isMuted) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
                    label = if (isMuted) "Activar sonido" else "Silenciar",
                    highlighted = isMuted,
                    onClick = onMute,
                )
            }
        }
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    label: String,
    highlighted: Boolean,
    onClick: () -> Unit,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier
            .size(60.dp)
            .semantics { contentDescription = label },
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = if (highlighted) DialNeedle else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (highlighted) MaterialTheme.colorScheme.onPrimary else DialIvory,
        )
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(30.dp))
    }
}

/**
 * Dial de frecuencia: una regla de marcas y una aguja que se desliza
 * hasta la posición de la emisora activa.
 */
@Composable
private fun FrequencyDial(
    stationIndex: Int,
    stationCount: Int,
    modifier: Modifier = Modifier,
) {
    val fraction by animateFloatAsState(
        targetValue = (stationIndex + 0.5f) / stationCount,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label = "needle"
    )

    Canvas(modifier) {
        val ticks = 41
        val step = size.width / (ticks - 1)
        for (i in 0 until ticks) {
            val x = i * step
            val major = i % 5 == 0
            val h = if (major) size.height * 0.55f else size.height * 0.3f
            drawLine(
                color = if (major) DialIvory else DialMuted,
                start = Offset(x, size.height),
                end = Offset(x, size.height - h),
                strokeWidth = if (major) 3f else 2f,
                cap = StrokeCap.Round
            )
        }
        val needleX = size.width * fraction
        drawLine(
            color = DialNeedle,
            start = Offset(needleX, 0f),
            end = Offset(needleX, size.height),
            strokeWidth = 5f,
            cap = StrokeCap.Round
        )
    }
}
