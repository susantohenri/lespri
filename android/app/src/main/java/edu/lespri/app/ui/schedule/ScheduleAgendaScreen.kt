package edu.lespri.app.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.domain.model.AgendaSessionItem
import edu.lespri.app.ui.components.CompactRow
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.components.LoadingOverlay
import edu.lespri.app.ui.components.SessionStatusChip
import edu.lespri.app.ui.navigation.NavRoutes
import edu.lespri.app.util.DateTimeUtil

/**
 * Layar Jadwal & Agenda (P12).
 * Menampilkan sesi dalam mode Hari / Minggu / Agenda, dengan navigasi tanggal,
 * filter kelas & tutor, serta aksi cepat membuat sesi baru.
 */
@Composable
fun ScheduleAgendaScreen(
    onNavigateToRoute: (String) -> Unit,
    onNavigateToSession: (String) -> Unit,
    viewModel: ScheduleAgendaViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LespriScaffold(
        currentRoute = NavRoutes.Schedule.route,
        onNavigateToRoute = onNavigateToRoute,
        title = "Jadwal",
        subtitle = "Agenda & Kehadiran Sesi",
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToRoute(NavRoutes.SessionForm.createRoute()) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Buat Sesi") }
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ── Mode tampilan: Hari | Minggu | Agenda ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AgendaViewMode.entries.forEach { mode ->
                    FilterChip(
                        selected = uiState.viewMode == mode,
                        onClick = { viewModel.onViewModeSelected(mode) },
                        label = { Text(mode.label) }
                    )
                }
            }

            PeriodNavigator(
                label = periodLabel(uiState),
                showNavigation = uiState.viewMode != AgendaViewMode.AGENDA,
                onPrevious = viewModel::onPreviousPeriod,
                onNext = viewModel::onNextPeriod,
                onToday = viewModel::onGoToToday
            )

            // Weekday strip (mode Minggu)
            if (uiState.viewMode == AgendaViewMode.WEEK) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(uiState.weekDates, key = { it }) { date ->
                        FilterChip(
                            selected = uiState.selectedDate == date,
                            onClick = { viewModel.onDateSelected(date) },
                            label = { Text(DateTimeUtil.formatShortDayDate(date)) }
                        )
                    }
                }
            }

            FilterSection(
                uiState = uiState,
                onClassSelected = viewModel::onClassFilterSelected,
                onTutorSelected = viewModel::onTutorFilterSelected,
                onClearFilters = viewModel::onClearFilters
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> LoadingOverlay()

                    uiState.sessions.isEmpty() -> {
                        EmptyStateView(
                            title = if (uiState.isFiltered) "Tidak ada sesi yang cocok" else "Belum ada sesi",
                            description = if (uiState.isFiltered) {
                                "Ubah filter kelas/tutor atau pilih tanggal lain."
                            } else {
                                "Buat sesi baru atau atur jadwal rutin pada form sesi."
                            },
                            actionLabel = if (uiState.isFiltered) "Reset Filter" else "Buat Sesi",
                            onActionClick = {
                                if (uiState.isFiltered) {
                                    viewModel.onClearFilters()
                                } else {
                                    onNavigateToRoute(NavRoutes.SessionForm.createRoute())
                                }
                            }
                        )
                    }

                    else -> {
                        AgendaList(
                            uiState = uiState,
                            onNavigateToSession = onNavigateToSession
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodNavigator(
    label: String,
    showNavigation: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showNavigation) {
            IconButton(onClick = onPrevious) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Periode sebelumnya")
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (showNavigation) {
            IconButton(onClick = onNext) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Periode berikutnya")
            }
        }

        TextButton(onClick = onToday) {
            Text("Hari ini")
        }
    }
}

@Composable
private fun FilterSection(
    uiState: ScheduleAgendaUiState,
    onClassSelected: (String?) -> Unit,
    onTutorSelected: (String?) -> Unit,
    onClearFilters: () -> Unit
) {
    if (uiState.classes.isEmpty() && uiState.tutors.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedClassId == null,
                    onClick = { if (uiState.isFiltered) onClearFilters() else onClassSelected(null) },
                    label = { Text("Semua Kelas") }
                )
            }
            items(uiState.classes, key = { it.id }) { classGroup ->
                FilterChip(
                    selected = uiState.selectedClassId == classGroup.id,
                    onClick = { onClassSelected(classGroup.id) },
                    label = { Text(classGroup.name) }
                )
            }
        }

        if (uiState.tutors.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedTutorId == null,
                        onClick = { onTutorSelected(null) },
                        label = { Text("Semua Tutor") }
                    )
                }
                items(uiState.tutors, key = { it.id }) { tutor ->
                    FilterChip(
                        selected = uiState.selectedTutorId == tutor.id,
                        onClick = { onTutorSelected(tutor.id) },
                        label = { Text(tutor.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AgendaList(
    uiState: ScheduleAgendaUiState,
    onNavigateToSession: (String) -> Unit
) {
    val groupedSessions = uiState.sessions.groupBy { it.session.sessionDate }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (uiState.viewMode == AgendaViewMode.DAY) {
            items(uiState.sessions, key = { it.session.id }) { item ->
                AgendaSessionRow(item = item, onClick = { onNavigateToSession(item.session.id) })
            }
        } else {
            groupedSessions.forEach { (date, sessions) ->
                item(key = "header_$date") {
                    Text(
                        text = DateTimeUtil.formatDisplayDayDate(date),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                items(sessions, key = { it.session.id }) { item ->
                    AgendaSessionRow(item = item, onClick = { onNavigateToSession(item.session.id) })
                }
            }
        }
    }
}

@Composable
private fun AgendaSessionRow(
    item: AgendaSessionItem,
    onClick: () -> Unit
) {
    CompactRow(
        title = item.className,
        subtitle = "${item.session.startTime} • ${item.session.durationMinutes} menit" +
            (item.tutorName?.let { " • $it" } ?: ""),
        extraInfo = DateTimeUtil.formatDisplayDate(item.session.sessionDate),
        trailingContent = { SessionStatusChip(status = item.session.status) },
        onClick = onClick
    )
}

private fun periodLabel(uiState: ScheduleAgendaUiState): String {
    return when (uiState.viewMode) {
        AgendaViewMode.DAY -> DateTimeUtil.formatDisplayDayDate(uiState.rangeStartDate)
        AgendaViewMode.WEEK ->
            "${DateTimeUtil.formatShortDayDate(uiState.rangeStartDate)} – " +
                DateTimeUtil.formatShortDayDate(uiState.rangeEndDate)
        AgendaViewMode.AGENDA -> "Agenda 30 hari ke depan"
    }
}
