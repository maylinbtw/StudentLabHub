package com.slh.studentlabhub.ui.screens.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.domain.model.AccentColor
import com.slh.studentlabhub.domain.model.BackgroundStyle
import com.slh.studentlabhub.domain.model.DotShape
import com.slh.studentlabhub.domain.model.StartScreen
import com.slh.studentlabhub.ui.components.DotMatrixText
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhProgressDots
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOnAccent
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import com.slh.studentlabhub.ui.theme.color
import com.slh.studentlabhub.ui.theme.label
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.slh.studentlabhub.ui.theme.SlhSurfaceVariant
import com.slh.studentlabhub.domain.model.ThemeMode
import com.slh.studentlabhub.domain.model.ReminderTiming
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationManagerCompat
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Switch
import java.time.LocalDate
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings
import com.slh.studentlabhub.domain.model.AppLanguage
import com.slh.studentlabhub.domain.model.ScheduleSubgroup
import androidx.compose.runtime.saveable.rememberSaveable

private val syncFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")

private val BackgroundStyle.title: String
    get() = when (this) {
        BackgroundStyle.AMOLED -> "AMOLED"
        BackgroundStyle.GRAPHITE -> Strings.get(R.string.bg_graphite)
    }

private val DotShape.title: String
    get() = when (this) {
        DotShape.ROUND -> Strings.get(R.string.dots_round)
        DotShape.SQUARE -> Strings.get(R.string.dots_square)
    }

private val ThemeMode.title: String
    get() = when (this) {
        ThemeMode.DARK -> Strings.get(R.string.theme_dark)
        ThemeMode.LIGHT -> Strings.get(R.string.theme_light)
        ThemeMode.SYSTEM -> Strings.get(R.string.option_system)
    }

private val ReminderTiming.title: String
    get() = when (this) {
        ReminderTiming.DAY -> Strings.get(R.string.reminder_day)
        ReminderTiming.HOURS -> Strings.get(R.string.reminder_hours)
        ReminderTiming.BOTH -> Strings.get(R.string.reminder_both)
    }

private fun Context.notificationsAllowed(): Boolean {
    val enabled = NotificationManagerCompat.from(this).areNotificationsEnabled()
    val permitted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED
    return enabled && permitted
}

private fun Context.openNotificationSettings() {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { startActivity(intent) }
}

private val AppLanguage.title: String
    get() = when (this) {
        AppLanguage.SYSTEM -> Strings.get(R.string.language_system_short)
        AppLanguage.RU -> "Русский"
        AppLanguage.EN -> "English"
        AppLanguage.BE -> "Беларуская"
    }

private val ScheduleSubgroup.title: String
    get() = when (this) {
        ScheduleSubgroup.BOTH -> Strings.get(R.string.subgroup_both)
        ScheduleSubgroup.FIRST -> Strings.get(R.string.subgroup_n, 1)
        ScheduleSubgroup.SECOND -> Strings.get(R.string.subgroup_n, 2)
    }

private val StartScreen.title: String
    get() = when (this) {
        StartScreen.TODAY -> Strings.get(R.string.tab_today)
        StartScreen.SUBJECTS -> Strings.get(R.string.tab_subjects)
        StartScreen.REFERENCE -> Strings.get(R.string.tab_reference)
    }

private val redoOptions = listOf(0, 1, 2, 3)

@Composable
fun SettingsRoute(
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var notificationsAllowed by remember { mutableStateOf(context.notificationsAllowed()) }

    LifecycleResumeEffect(Unit) {
        notificationsAllowed = context.notificationsAllowed()
        onPauseOrDispose { }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        notificationsAllowed = granted && context.notificationsAllowed()
        if (!granted) context.openNotificationSettings()
    }

    val requestNotifications = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            context.openNotificationSettings()
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri -> uri?.let(viewModel::exportBackup) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri -> uri?.let(viewModel::importBackup) }

    LaunchedEffect(state.isSignedOut) {
        if (state.isSignedOut) {
            onSignedOut()
        }
    }

    SettingsScreen(
        state = state,
        notificationsAllowed = notificationsAllowed,
        onThemeModeSelected = viewModel::onThemeModeSelected,
        onLanguageSelected = viewModel::onLanguageSelected,
        onLoadScheduleGroups = viewModel::loadScheduleGroups,
        onScheduleGroupSelected = viewModel::onScheduleGroupSelected,
        onScheduleSubgroupSelected = viewModel::onScheduleSubgroupSelected,
        onScheduleUrlChanged = viewModel::onScheduleUrlChanged,
        onScheduleAlertsChanged = viewModel::onScheduleAlertsChanged,
        onRefreshSchedule = viewModel::refreshSchedule,
        onDisconnectSchedule = viewModel::disconnectSchedule,
        onRemindersChanged = { enabled ->
            viewModel.onRemindersChanged(enabled)
            if (enabled && !notificationsAllowed) requestNotifications()
        },
        onReminderTimingSelected = viewModel::onReminderTimingSelected,
        onRedoAlertsChanged = { enabled ->
            viewModel.onRedoAlertsChanged(enabled)
            if (enabled && !notificationsAllowed) requestNotifications()
        },
        onBackgroundSyncChanged = viewModel::onBackgroundSyncChanged,
        onAllowNotifications = requestNotifications,
        onLargeTextChanged = viewModel::onLargeTextChanged,
        onExportBackup = {
            exportLauncher.launch("studentlabhub-${LocalDate.now()}.json")
        },
        onImportBackup = {
            importLauncher.launch(arrayOf("application/json", "text/plain", "application/octet-stream"))
        },
        onAccentSelected = viewModel::onAccentSelected,
        onBackgroundSelected = viewModel::onBackgroundSelected,
        onDotShapeSelected = viewModel::onDotShapeSelected,
        onStartScreenSelected = viewModel::onStartScreenSelected,
        onRedoThresholdSelected = viewModel::onRedoThresholdSelected,
        onClearTldr = viewModel::clearTldrDownloads,
        onSignOut = viewModel::signOut,
        onInsertDemo = viewModel::insertDemoData,
        modifier = modifier,
    )
}

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onAccentSelected: (AccentColor) -> Unit,
    onBackgroundSelected: (BackgroundStyle) -> Unit,
    onDotShapeSelected: (DotShape) -> Unit,
    onStartScreenSelected: (StartScreen) -> Unit,
    onRedoThresholdSelected: (Int) -> Unit,
    onClearTldr: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    onInsertDemo: () -> Unit = {},
    notificationsAllowed: Boolean = true,
    onThemeModeSelected: (ThemeMode) -> Unit = {},
    onRemindersChanged: (Boolean) -> Unit = {},
    onReminderTimingSelected: (ReminderTiming) -> Unit = {},
    onRedoAlertsChanged: (Boolean) -> Unit = {},
    onBackgroundSyncChanged: (Boolean) -> Unit = {},
    onAllowNotifications: () -> Unit = {},
    onLargeTextChanged: (Boolean) -> Unit = {},
    onExportBackup: () -> Unit = {},
    onImportBackup: () -> Unit = {},
    onLanguageSelected: (AppLanguage) -> Unit = {},
    onLoadScheduleGroups: () -> Unit = {},
    onScheduleGroupSelected: (String) -> Unit = {},
    onScheduleSubgroupSelected: (ScheduleSubgroup) -> Unit = {},
    onScheduleUrlChanged: (String) -> Unit = {},
    onScheduleAlertsChanged: (Boolean) -> Unit = {},
    onRefreshSchedule: () -> Unit = {},
    onDisconnectSchedule: () -> Unit = {},
) {
    var showGroupPicker by rememberSaveable { mutableStateOf(false) }
    var showUrlDialog by rememberSaveable { mutableStateOf(false) }
    val settings = state.settings

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = Strings.get(R.string.settings_overline),
            title = Strings.get(R.string.settings_title),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            SectionLabel(text = Strings.get(R.string.settings_account))
            Spacer(modifier = Modifier.height(10.dp))
            AccountCard(
                email = state.email,
                lastSyncAt = state.lastSyncAt,
                isSigningOut = state.isSigningOut,
                onSignOut = onSignOut,
            )

            Spacer(modifier = Modifier.height(28.dp))
            SectionLabel(text = Strings.get(R.string.settings_notifications))
            Spacer(modifier = Modifier.height(10.dp))
            if ((settings.remindersEnabled || settings.redoAlerts) && !notificationsAllowed) {
                WarningRow(
                    text = Strings.get(R.string.notifications_blocked),
                    actionText = Strings.get(R.string.action_allow),
                    onClick = onAllowNotifications,
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            SettingsCard {
                SwitchRow(
                    title = Strings.get(R.string.remind_deadlines),
                    subtitle = Strings.get(R.string.remind_deadlines_sub),
                    checked = settings.remindersEnabled,
                    onCheckedChange = onRemindersChanged,
                )
                HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                SwitchRow(
                    title = Strings.get(R.string.redo_alerts),
                    subtitle = Strings.get(R.string.redo_alerts_sub),
                    checked = settings.redoAlerts,
                    onCheckedChange = onRedoAlertsChanged,
                )
                HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                SwitchRow(
                    title = Strings.get(R.string.background_sync),
                    subtitle = Strings.get(R.string.background_sync_sub),
                    checked = settings.backgroundSync,
                    onCheckedChange = onBackgroundSyncChanged,
                )
            }
            if (settings.remindersEnabled) {
                Spacer(modifier = Modifier.height(18.dp))
                OptionTitle(text = Strings.get(R.string.when_remind))
                Spacer(modifier = Modifier.height(8.dp))
                SegmentedOptions(
                    options = ReminderTiming.entries,
                    selected = settings.reminderTiming,
                    label = { it.title },
                    onSelect = onReminderTimingSelected,
                )
                OptionHint(text = Strings.get(R.string.when_remind_hint))
            }

            Spacer(modifier = Modifier.height(28.dp))
            SectionLabel(text = Strings.get(R.string.settings_schedule))
            Spacer(modifier = Modifier.height(10.dp))
            SettingsCard {
                ActionRow(
                    title = Strings.get(R.string.schedule_group),
                    value = settings.scheduleGroup.ifBlank { Strings.get(R.string.schedule_group_none) },
                    actionText = Strings.get(R.string.action_choose),
                    enabled = true,
                    onConfirmed = {
                        showGroupPicker = true
                        onLoadScheduleGroups()
                    },
                    requireConfirm = false,
                )
                HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                ActionRow(
                    title = Strings.get(R.string.schedule_page),
                    value = settings.scheduleUrl.substringAfter("://"),
                    actionText = Strings.get(R.string.action_edit),
                    enabled = true,
                    onConfirmed = { showUrlDialog = true },
                    requireConfirm = false,
                )
                if (settings.scheduleGroup.isNotBlank()) {
                    HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    ActionRow(
                        title = Strings.get(R.string.schedule_refresh_now),
                        value = state.schedule.refreshError
                            ?: state.schedule.syncAt?.let { Strings.get(R.string.schedule_updated_at, formatSync(it)) }
                            ?: Strings.get(R.string.schedule_never_updated),
                        actionText = if (state.schedule.isRefreshing) "…" else Strings.get(R.string.action_refresh),
                        enabled = !state.schedule.isRefreshing,
                        onConfirmed = onRefreshSchedule,
                        requireConfirm = false,
                    )
                    HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    SwitchRow(
                        title = Strings.get(R.string.schedule_alerts),
                        subtitle = Strings.get(R.string.schedule_alerts_sub),
                        checked = settings.scheduleAlerts,
                        onCheckedChange = onScheduleAlertsChanged,
                    )
                    HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    ActionRow(
                        title = Strings.get(R.string.schedule_disconnect),
                        value = Strings.get(R.string.schedule_disconnect_sub),
                        actionText = Strings.get(R.string.action_disable),
                        enabled = true,
                        onConfirmed = onDisconnectSchedule,
                    )
                }
            }
            if (settings.scheduleGroup.isNotBlank()) {
                Spacer(modifier = Modifier.height(18.dp))
                OptionTitle(text = Strings.get(R.string.schedule_subgroup))
                Spacer(modifier = Modifier.height(8.dp))
                SegmentedOptions(
                    options = ScheduleSubgroup.entries,
                    selected = settings.scheduleSubgroup,
                    label = { it.title },
                    onSelect = onScheduleSubgroupSelected,
                )
                OptionHint(text = Strings.get(R.string.schedule_subgroup_hint))
            }

            Spacer(modifier = Modifier.height(28.dp))
            SectionLabel(text = Strings.get(R.string.settings_appearance))
            Spacer(modifier = Modifier.height(10.dp))
            AppearancePreview(accent = settings.accent)
            Spacer(modifier = Modifier.height(18.dp))
            OptionTitle(text = Strings.get(R.string.theme))
            Spacer(modifier = Modifier.height(8.dp))
            SegmentedOptions(
                options = ThemeMode.entries,
                selected = settings.themeMode,
                label = { it.title },
                onSelect = onThemeModeSelected,
            )
            Spacer(modifier = Modifier.height(18.dp))
            OptionTitle(text = Strings.get(R.string.accent_color))
            Spacer(modifier = Modifier.height(10.dp))
            AccentPicker(
                selected = settings.accent,
                onSelect = onAccentSelected,
            )
            Spacer(modifier = Modifier.height(18.dp))
            OptionTitle(text = Strings.get(R.string.dark_background))
            Spacer(modifier = Modifier.height(8.dp))
            SegmentedOptions(
                options = BackgroundStyle.entries,
                selected = settings.background,
                label = { it.title },
                onSelect = onBackgroundSelected,
            )
            OptionHint(text = Strings.get(R.string.dark_background_hint))
            Spacer(modifier = Modifier.height(18.dp))
            OptionTitle(text = Strings.get(R.string.text_size))
            Spacer(modifier = Modifier.height(8.dp))
            SegmentedOptions(
                options = listOf(false, true),
                selected = settings.largeText,
                label = { if (it) Strings.get(R.string.text_large) else Strings.get(R.string.text_normal) },
                onSelect = onLargeTextChanged,
            )
            Spacer(modifier = Modifier.height(18.dp))
            OptionTitle(text = Strings.get(R.string.dot_digits))
            Spacer(modifier = Modifier.height(8.dp))
            SegmentedOptions(
                options = DotShape.entries,
                selected = settings.dotShape,
                label = { it.title },
                onSelect = onDotShapeSelected,
            )

            Spacer(modifier = Modifier.height(28.dp))
            SectionLabel(text = Strings.get(R.string.settings_behavior))
            Spacer(modifier = Modifier.height(10.dp))
            OptionTitle(text = Strings.get(R.string.language))
            Spacer(modifier = Modifier.height(8.dp))
            SegmentedOptions(
                options = AppLanguage.entries,
                selected = settings.language,
                label = { it.title },
                onSelect = onLanguageSelected,
            )
            Spacer(modifier = Modifier.height(18.dp))
            OptionTitle(text = Strings.get(R.string.start_screen))
            Spacer(modifier = Modifier.height(8.dp))
            SegmentedOptions(
                options = StartScreen.entries,
                selected = settings.startScreen,
                label = { it.title },
                onSelect = onStartScreenSelected,
            )
            OptionHint(text = Strings.get(R.string.start_screen_hint))
            Spacer(modifier = Modifier.height(18.dp))
            OptionTitle(text = Strings.get(R.string.redo_threshold))
            Spacer(modifier = Modifier.height(8.dp))
            SegmentedOptions(
                options = redoOptions,
                selected = state.redoThreshold.takeIf { it in redoOptions } ?: 1,
                label = { if (it == 0) Strings.get(R.string.option_off) else "≤ $it" },
                onSelect = onRedoThresholdSelected,
            )
            OptionHint(
                text = if (state.redoThreshold == 0) {
                    Strings.get(R.string.redo_threshold_off)
                } else {
                    Strings.get(R.string.redo_threshold_on, state.redoThreshold)
                },
            )

            Spacer(modifier = Modifier.height(28.dp))
            SectionLabel(text = Strings.get(R.string.settings_data))
            Spacer(modifier = Modifier.height(10.dp))
            SettingsCard {
                ActionRow(
                    title = Strings.get(R.string.backup),
                    value = Strings.get(R.string.backup_sub),
                    actionText = if (state.isBackupBusy) "…" else Strings.get(R.string.action_save),
                    enabled = !state.isBackupBusy,
                    onConfirmed = onExportBackup,
                    requireConfirm = false,
                )
                HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                ActionRow(
                    title = Strings.get(R.string.restore_backup),
                    value = Strings.get(R.string.restore_backup_sub),
                    actionText = Strings.get(R.string.action_open),
                    enabled = !state.isBackupBusy,
                    onConfirmed = onImportBackup,
                )
                HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                ActionRow(
                    title = Strings.get(R.string.tldr_downloaded),
                    value = Strings.plural(R.plurals.articles_count, state.tldrCount, state.tldrCount),
                    actionText = Strings.get(R.string.action_clear),
                    enabled = state.tldrCount > 0,
                    onConfirmed = onClearTldr,
                )
                HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                InfoRow(
                    title = Strings.get(R.string.my_articles),
                    value = Strings.plural(R.plurals.articles_count, state.customCount, state.customCount),
                )
                if (state.subjectsCount == 0) {
                    HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    ActionRow(
                        title = Strings.get(R.string.demo_subjects),
                        value = Strings.get(R.string.demo_subjects_sub),
                        actionText = Strings.get(R.string.action_add),
                        enabled = true,
                        onConfirmed = onInsertDemo,
                    )
                }
            }
            state.backupMessage?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = SlhAccent,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            OptionHint(
                text = Strings.get(R.string.settings_data_hint),
            )

            Spacer(modifier = Modifier.height(28.dp))
            SectionLabel(text = Strings.get(R.string.settings_about))
            Spacer(modifier = Modifier.height(10.dp))
            SettingsCard {
                InfoRow(title = "StudentLabHub", value = state.versionName.ifBlank { "—" })
                HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                InfoRow(title = Strings.get(R.string.about_reference), value = "tldr-pages · CC BY 4.0")
                HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                InfoRow(title = Strings.get(R.string.about_fonts), value = "JetBrains Mono, Manrope · OFL")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showGroupPicker) {
        GroupPickerDialog(
            groups = state.schedule.groups,
            selected = settings.scheduleGroup,
            isLoading = state.schedule.isLoadingGroups,
            errorMessage = state.schedule.groupsError,
            onPick = { group ->
                showGroupPicker = false
                onScheduleGroupSelected(group)
            },
            onRetry = onLoadScheduleGroups,
            onDismiss = { showGroupPicker = false },
        )
    }

    if (showUrlDialog) {
        ScheduleUrlDialog(
            initialUrl = settings.scheduleUrl,
            onConfirm = { url ->
                showUrlDialog = false
                onScheduleUrlChanged(url)
            },
            onDismiss = { showUrlDialog = false },
        )
    }
}

@Composable
private fun AccountCard(
    email: String?,
    lastSyncAt: Long?,
    isSigningOut: Boolean,
    onSignOut: () -> Unit,
) {
    var confirm by remember { mutableStateOf(false) }
    SettingsCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SlhAccent),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = email?.firstOrNull()?.uppercase() ?: "?",
                    color = SlhOnAccent,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = email ?: Strings.get(R.string.not_signed_in),
                    color = SlhContent,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = lastSyncAt?.let { Strings.get(R.string.classroom_synced_at, formatSync(it)) } ?: Strings.get(R.string.classroom_never_synced),
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            if (isSigningOut) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = SlhAccent,
                    strokeWidth = 2.dp,
                )
            } else {
                SmallButton(
                    text = if (confirm) Strings.get(R.string.action_confirm) else Strings.get(R.string.action_sign_out),
                    color = if (confirm) SlhDanger else SlhTextSecondary,
                    enabled = true,
                    onClick = {
                        if (confirm) onSignOut() else confirm = true
                    },
                )
            }
        }
    }
}

@Composable
private fun AppearancePreview(accent: AccentColor) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DotMatrixText(
            text = "8.3",
            color = SlhAccent,
            dotSize = 5.dp,
            dotGap = 1.dp,
        )
        Spacer(modifier = Modifier.width(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = Strings.get(R.string.average_grade),
                color = SlhContent,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(6.dp))
            SlhProgressDots(total = 10, filled = 7)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SlhAccent)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = Strings.get(R.string.badge_defended),
                        color = SlhOnAccent,
                        fontFamily = SlhMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = accent.label,
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun AccentPicker(
    selected: AccentColor,
    onSelect: (AccentColor) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AccentColor.entries.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (isSelected) SlhContent else SlhBackground, CircleShape)
                    .clickable(role = Role.RadioButton, onClick = { onSelect(option) })
                    .semantics { contentDescription = option.label },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 22.dp else 26.dp)
                        .clip(CircleShape)
                        .background(option.color),
                )
            }
        }
    }
}

@Composable
private fun <T> SegmentedOptions(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(shape)
            .background(SlhOutline)
            .border(1.dp, SlhOutline, shape)
            .padding(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(if (isSelected) SlhAccent else SlhSurface)
                    .clickable(role = Role.RadioButton, onClick = { onSelect(option) }),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label(option),
                    color = if (isSelected) SlhOnAccent else SlhTextSecondary,
                    fontFamily = SlhMono,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape),
        content = content,
    )
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Switch, onClick = { onCheckedChange(!checked) })
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = SlhContent,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SlhOnAccent,
                checkedTrackColor = SlhAccent,
                checkedBorderColor = SlhAccent,
                uncheckedThumbColor = SlhTextMuted,
                uncheckedTrackColor = SlhSurfaceVariant,
                uncheckedBorderColor = SlhOutline,
            ),
        )
    }
}

@Composable
private fun WarningRow(
    text: String,
    actionText: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, SlhDanger, shape)
            .padding(start = 14.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = SlhContent,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(10.dp))
        SmallButton(
            text = actionText,
            color = SlhDanger,
            enabled = true,
            onClick = onClick,
        )
    }
}

@Composable
private fun InfoRow(
    title: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = SlhContent,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            color = SlhTextSecondary,
            fontFamily = SlhMono,
            fontSize = 12.sp,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun ActionRow(
    title: String,
    value: String,
    actionText: String,
    enabled: Boolean,
    onConfirmed: () -> Unit,
    requireConfirm: Boolean = true,
) {
    var confirm by remember(enabled) { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = SlhContent,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = SlhTextMuted,
                fontFamily = SlhMono,
                fontSize = 12.sp,
            )
        }
        SmallButton(
            text = if (confirm) Strings.get(R.string.action_confirm) else actionText,
            color = if (confirm) SlhDanger else SlhTextSecondary,
            enabled = enabled,
            onClick = {
                if (confirm || !requireConfirm) {
                    confirm = false
                    onConfirmed()
                } else {
                    confirm = true
                }
            },
        )
    }
}

@Composable
private fun SmallButton(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(shape)
            .border(1.dp, if (enabled) color else SlhOutline, shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) color else SlhTextMuted,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun OptionTitle(text: String) {
    Text(
        text = text,
        color = SlhContent,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun OptionHint(text: String) {
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = text,
        color = SlhTextMuted,
        style = MaterialTheme.typography.bodySmall,
    )
}

private fun formatSync(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(syncFormatter)

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 1400)
@Composable
private fun SettingsScreenPreview() {
    StudentLabHubTheme {
        SettingsScreen(
            state = SettingsUiState(
                email = "student@gmail.com",
                lastSyncAt = System.currentTimeMillis(),
                tldrCount = 3,
                customCount = 1,
                versionName = "1.0",
            ),
            onAccentSelected = {},
            onBackgroundSelected = {},
            onDotShapeSelected = {},
            onStartScreenSelected = {},
            onRedoThresholdSelected = {},
            onClearTldr = {},
            onSignOut = {},
        )
    }
}
