package edu.lespri.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.lespri.app.domain.model.AttendanceStatus
import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.ui.theme.StatusError
import edu.lespri.app.ui.theme.StatusErrorContainer
import edu.lespri.app.ui.theme.StatusInfo
import edu.lespri.app.ui.theme.StatusInfoContainer
import edu.lespri.app.ui.theme.StatusSuccess
import edu.lespri.app.ui.theme.StatusSuccessContainer
import edu.lespri.app.ui.theme.StatusWarning
import edu.lespri.app.ui.theme.StatusWarningContainer

/**
 * Chip status visual dengan teks dan warna sesuai standar Material 3.
 */
@Composable
fun StatusBadge(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun AttendanceStatusChip(
    status: AttendanceStatus,
    modifier: Modifier = Modifier
) {
    val (label, bg, content) = when (status) {
        AttendanceStatus.PRESENT -> Triple("Hadir", StatusSuccessContainer, StatusSuccess)
        AttendanceStatus.EXCUSED -> Triple("Izin", StatusInfoContainer, StatusInfo)
        AttendanceStatus.SICK -> Triple("Sakit", StatusWarningContainer, StatusWarning)
        AttendanceStatus.ABSENT -> Triple("Alpa", StatusErrorContainer, StatusError)
        AttendanceStatus.UNRECORDED -> Triple("Belum Diabsen", Color(0xFFF3F4F6), Color(0xFF6B7280))
    }
    StatusBadge(text = label, backgroundColor = bg, contentColor = content, modifier = modifier)
}

@Composable
fun ChargeStatusChip(
    status: ChargeStatus,
    modifier: Modifier = Modifier
) {
    val (label, bg, content) = when (status) {
        ChargeStatus.PAID -> Triple("Lunas", StatusSuccessContainer, StatusSuccess)
        ChargeStatus.PARTIALLY_PAID -> Triple("Cicil", StatusWarningContainer, StatusWarning)
        ChargeStatus.UNPAID -> Triple("Belum Lunas", StatusErrorContainer, StatusError)
        ChargeStatus.DRAFT -> Triple("Draf", Color(0xFFF3F4F6), Color(0xFF6B7280))
        ChargeStatus.CANCELLED -> Triple("Batal", Color(0xFFE5E7EB), Color(0xFF9CA3AF))
    }
    StatusBadge(text = label, backgroundColor = bg, contentColor = content, modifier = modifier)
}
