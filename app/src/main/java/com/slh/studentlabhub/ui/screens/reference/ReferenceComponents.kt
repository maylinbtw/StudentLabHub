package com.slh.studentlabhub.ui.screens.reference

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slh.studentlabhub.domain.model.RefCategory
import com.slh.studentlabhub.domain.model.RefEntry
import com.slh.studentlabhub.domain.model.RefSource
import com.slh.studentlabhub.domain.model.RefTech
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.SlhIconView
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOnAccent
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhSans
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextDisabled
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings

@Composable
fun ReferenceSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(50)
    val isActive = isFocused || query.isNotEmpty()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, if (isActive) SlhAccent else SlhOutline, shape)
            .padding(start = 16.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SlhIconView(
            icon = SlhIcon.Search,
            color = if (isActive) SlhAccent else SlhTextMuted,
            size = 16.dp,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    color = SlhTextMuted,
                    fontFamily = SlhMono,
                    fontSize = 13.sp,
                    maxLines = 1,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = { onQueryChange(it.take(60)) },
                singleLine = true,
                interactionSource = interactionSource,
                textStyle = TextStyle(
                    color = SlhContent,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                ),
                cursorBrush = SolidColor(SlhAccent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (query.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = { onQueryChange("") }),
                contentAlignment = Alignment.Center,
            ) {
                SlhIconView(icon = SlhIcon.Close, color = SlhTextSecondary, size = 14.dp)
            }
        } else {
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
fun TechTabs(
    selected: RefTech,
    onSelect: (RefTech) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            RefTech.entries.forEach { tech ->
                val isSelected = tech == selected
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(role = Role.Tab, onClick = { onSelect(tech) }),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = tech.label,
                        color = if (isSelected) SlhContent else SlhTextMuted,
                        fontFamily = SlhMono,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(if (isSelected) SlhAccent else SlhOutline),
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: RefCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = category.title,
                color = SlhContent,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.entryTitles.take(5).joinToString(" · "),
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "${category.entryTitles.size} ›",
            color = SlhAccent,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )
    }
}

@Composable
fun EntryCard(
    entry: RefEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showPath: Boolean = false,
    onEdit: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(6.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 14.dp, end = 6.dp, top = 10.dp, bottom = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = entry.title,
                color = SlhContent,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f, fill = false),
            )
            SourceBadge(source = entry.source)
            Spacer(modifier = Modifier.weight(1f))
            if (onEdit != null && entry.isEditable) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onEdit),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "✎",
                        color = SlhTextSecondary,
                        fontSize = 15.sp,
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(32.dp))
            }
        }
        Column(modifier = Modifier.padding(end = 8.dp)) {
            if (entry.description.isNotBlank()) {
                Text(
                    text = entry.description,
                    color = SlhTextSecondary,
                    fontFamily = SlhSans,
                    fontSize = 13.sp,
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            if (entry.example.isNotBlank()) {
                Text(
                    text = entry.example,
                    color = SlhTextMuted,
                    fontFamily = SlhMono,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                )
            }
            if (showPath) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${entry.tech.label} › ${entry.categoryTitle}",
                    color = SlhTextDisabled,
                    fontFamily = SlhMono,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                )
            }
            if (entry.source == RefSource.TLDR) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Strings.get(R.string.source_tldr),
                    color = SlhTextDisabled,
                    fontFamily = SlhMono,
                    fontSize = 10.sp,
                )
            }
        }
    }
}

@Composable
private fun SourceBadge(source: RefSource) {
    val label = when (source) {
        RefSource.BUILTIN -> return
        RefSource.CUSTOM -> Strings.get(R.string.badge_mine)
        RefSource.TLDR -> "TLDR"
    }
    Spacer(modifier = Modifier.width(8.dp))
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .border(1.dp, if (source == RefSource.CUSTOM) SlhAccent else SlhOutline, RoundedCornerShape(3.dp))
            .padding(horizontal = 5.dp, vertical = 1.dp),
    ) {
        Text(
            text = label,
            color = if (source == RefSource.CUSTOM) SlhAccent else SlhTextMuted,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
fun SelectChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) SlhAccent else SlhSurface)
            .border(1.dp, if (selected) SlhAccent else SlhOutline, shape)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (selected) SlhOnAccent else SlhContent,
            fontFamily = SlhMono,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp,
            maxLines = 1,
        )
    }
}

@Composable
fun OfflineFooter(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(SlhAccent),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = Strings.get(R.string.offline_badge),
            color = SlhTextDisabled,
            fontFamily = SlhMono,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
        )
    }
}

@Composable
fun ReferenceDivider() {
    HorizontalDivider(thickness = 1.dp, color = SlhOutline)
}

@Composable
fun rememberCopyExample(): (RefEntry) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { entry -> copyToClipboard(context, entry) }
    }
}

private fun copyToClipboard(context: Context, entry: RefEntry) {
    val clipboard = context.getSystemService(ClipboardManager::class.java) ?: return
    clipboard.setPrimaryClip(ClipData.newPlainText(entry.title, entry.example))
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        Toast.makeText(context, Strings.get(R.string.example_copied), Toast.LENGTH_SHORT).show()
    }
}
