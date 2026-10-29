package com.slh.studentlabhub.ui.screens.reference

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slh.studentlabhub.R
import com.slh.studentlabhub.domain.model.ArticleBlock
import com.slh.studentlabhub.domain.model.NoteKind
import com.slh.studentlabhub.i18n.Strings
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhSans
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhSurfaceVariant
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary

@Composable
fun ArticleBlockView(
    block: ArticleBlock,
    modifier: Modifier = Modifier,
) {
    when (block) {
        is ArticleBlock.Heading -> Text(
            text = inlineText(block.text),
            color = SlhContent,
            fontFamily = SlhSans,
            fontWeight = FontWeight.Bold,
            fontSize = if (block.level == 2) 19.sp else 16.sp,
            lineHeight = if (block.level == 2) 25.sp else 21.sp,
            modifier = modifier.padding(top = if (block.level == 2) 18.dp else 10.dp, bottom = 8.dp),
        )

        is ArticleBlock.Paragraph -> BodyText(text = block.text, modifier = modifier.padding(bottom = 12.dp))

        is ArticleBlock.Code -> CodeBlock(code = block.code, language = block.language, modifier = modifier.padding(bottom = 12.dp))

        is ArticleBlock.Output -> OutputBlock(text = block.text, modifier = modifier.padding(bottom = 12.dp))

        is ArticleBlock.Note -> NoteBlock(kind = block.kind, text = block.text, modifier = modifier.padding(bottom = 12.dp))

        is ArticleBlock.BulletList -> Column(modifier = modifier.padding(bottom = 12.dp)) {
            block.items.forEach { item -> ListItem(marker = "•", text = item) }
        }

        is ArticleBlock.NumberedList -> Column(modifier = modifier.padding(bottom = 12.dp)) {
            block.items.forEachIndexed { index, item -> ListItem(marker = "${index + 1}.", text = item) }
        }

        is ArticleBlock.Table -> TableBlock(table = block, modifier = modifier.padding(bottom = 12.dp))

        is ArticleBlock.Quiz -> QuizBlock(quiz = block, modifier = modifier.padding(bottom = 12.dp))
    }
}

@Composable
private fun QuizBlock(quiz: ArticleBlock.Quiz, modifier: Modifier = Modifier) {
    var revealed by rememberSaveable(quiz.question) { mutableStateOf(false) }
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = Strings.get(R.string.article_quiz),
            color = SlhAccent,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        quiz.question.split("\n").forEach { part ->
            BodyText(text = part)
        }
        if (quiz.answer.isNotBlank()) {
            if (revealed) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SlhOutline))
                Spacer(modifier = Modifier.height(8.dp))
                quiz.answer.split("\n").forEach { part ->
                    BodyText(text = part)
                }
            }
            Text(
                text = Strings.get(if (revealed) R.string.article_hide_answer else R.string.article_show_answer),
                color = SlhAccent,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(role = Role.Button) { revealed = !revealed }
                    .padding(vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun BodyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = inlineText(text),
        color = SlhTextSecondary,
        fontFamily = SlhSans,
        fontSize = 15.sp,
        lineHeight = 23.sp,
        modifier = modifier,
    )
}

@Composable
private fun ListItem(marker: String, text: String) {
    Row(modifier = Modifier.padding(bottom = 6.dp)) {
        Text(
            text = marker,
            color = SlhAccent,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 23.sp,
            modifier = Modifier.width(22.dp),
        )
        BodyText(text = text, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun CodeBlock(code: String, language: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(8.dp)
    val highlighted = remember(code, language, SlhAccent, SlhTextSecondary, SlhTextMuted) { highlightCode(code, language) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = languageLabel(language),
                color = SlhTextMuted,
                fontFamily = SlhMono,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = Strings.get(R.string.article_copy),
                color = SlhAccent,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .clickable(role = Role.Button) { copyCode(context, code) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SlhOutline))
        Text(
            text = highlighted,
            color = SlhContent,
            fontFamily = SlhMono,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            softWrap = false,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(12.dp),
        )
    }
}

@Composable
private fun OutputBlock(text: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, SlhOutline, shape)
            .padding(12.dp),
    ) {
        Text(
            text = Strings.get(R.string.article_output),
            color = SlhTextMuted,
            fontFamily = SlhMono,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            color = SlhTextSecondary,
            fontFamily = SlhMono,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            softWrap = false,
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        )
    }
}

@Composable
private fun NoteBlock(kind: NoteKind, text: String, modifier: Modifier = Modifier) {
    val color = if (kind == NoteKind.WARNING) SlhDanger else SlhAccent
    val shape = RoundedCornerShape(6.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(SlhSurface),
    ) {
        Box(modifier = Modifier.width(3.dp).fillMaxHeight().background(color))
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = Strings.get(if (kind == NoteKind.WARNING) R.string.article_warning else R.string.article_note),
                color = color,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            text.split("\n").forEach { part ->
                BodyText(text = part)
            }
        }
    }
}

@Composable
private fun TableBlock(table: ArticleBlock.Table, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, SlhOutline, shape),
    ) {
        TableRow(cells = table.header, isHeader = true)
        table.rows.forEach { row ->
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SlhOutline))
            TableRow(cells = row, isHeader = false)
        }
    }
}

@Composable
private fun TableRow(cells: List<String>, isHeader: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isHeader) SlhSurface else Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        cells.forEachIndexed { index, cell ->
            if (index > 0) Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = inlineText(cell),
                color = if (isHeader) SlhContent else SlhTextSecondary,
                fontFamily = SlhSans,
                fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun inlineText(text: String): AnnotatedString {
    val codeBackground = SlhSurfaceVariant
    val codeColor = SlhContent
    val boldColor = SlhContent
    return remember(text, codeBackground, codeColor, boldColor) {
        buildAnnotatedString {
            var index = 0
            while (index < text.length) {
                when {
                    text[index] == '`' -> {
                        val end = text.indexOf('`', index + 1)
                        if (end < 0) {
                            append(text.substring(index))
                            index = text.length
                        } else {
                            withStyle(SpanStyle(fontFamily = SlhMono, background = codeBackground, color = codeColor, fontSize = 13.sp)) {
                                append(" ")
                                append(text.substring(index + 1, end))
                                append(" ")
                            }
                            index = end + 1
                        }
                    }

                    text.startsWith("**", index) -> {
                        val end = text.indexOf("**", index + 2)
                        if (end < 0) {
                            append(text.substring(index))
                            index = text.length
                        } else {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = boldColor)) {
                                append(text.substring(index + 2, end))
                            }
                            index = end + 2
                        }
                    }

                    else -> {
                        append(text[index])
                        index++
                    }
                }
            }
        }
    }
}

private fun languageLabel(language: String): String = when (language) {
    "csharp", "cs", "c#" -> "C#"
    "cpp", "c++" -> "C++"
    "bash", "sh", "shell" -> "TERMINAL"
    "" -> "CODE"
    else -> language.uppercase()
}

private val keywordsByLanguage: Map<String, Set<String>> = run {
    val csharp = setOf(
        "abstract", "as", "base", "bool", "break", "byte", "case", "catch", "char", "checked", "class",
        "const", "continue", "decimal", "default", "delegate", "do", "double", "else", "enum", "event",
        "explicit", "extern", "false", "finally", "fixed", "float", "for", "foreach", "goto", "if",
        "implicit", "in", "int", "interface", "internal", "is", "lock", "long", "namespace", "new", "null",
        "object", "operator", "out", "override", "params", "private", "protected", "public", "readonly",
        "ref", "return", "sbyte", "sealed", "short", "sizeof", "static", "string", "struct", "switch",
        "this", "throw", "true", "try", "typeof", "uint", "ulong", "unchecked", "unsafe", "ushort",
        "using", "var", "virtual", "void", "volatile", "while", "async", "await", "record", "nint", "nuint",
        "dynamic", "get", "set", "init", "required", "when", "with", "and", "or", "not",
    )
    val cpp = setOf(
        "alignas", "auto", "bool", "break", "case", "catch", "char", "class", "const", "constexpr",
        "continue", "default", "delete", "do", "double", "else", "enum", "explicit", "extern", "false",
        "float", "for", "friend", "goto", "if", "inline", "int", "long", "mutable", "namespace", "new",
        "noexcept", "nullptr", "operator", "override", "private", "protected", "public", "return",
        "short", "signed", "sizeof", "static", "struct", "switch", "template", "this", "throw", "true",
        "try", "typedef", "typename", "unsigned", "using", "virtual", "void", "volatile", "while",
        "std", "string", "size_t",
    )
    val java = setOf(
        "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
        "default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float", "for",
        "if", "implements", "import", "instanceof", "int", "interface", "long", "new", "null", "package",
        "private", "protected", "public", "record", "return", "short", "static", "super", "switch",
        "this", "throw", "throws", "true", "try", "var", "void", "while", "yield", "String",
    )
    val python = setOf(
        "and", "as", "assert", "async", "await", "break", "class", "continue", "def", "del", "elif",
        "else", "except", "False", "finally", "for", "from", "global", "if", "import", "in", "is",
        "lambda", "None", "nonlocal", "not", "or", "pass", "raise", "return", "True", "try", "while",
        "with", "yield", "match", "case", "print", "len", "range", "int", "float", "str", "bool",
        "list", "dict", "set", "tuple", "input",
    )
    mapOf(
        "csharp" to csharp, "cs" to csharp,
        "cpp" to cpp, "c++" to cpp, "c" to cpp,
        "java" to java,
        "python" to python, "py" to python,
    )
}

private fun highlightCode(code: String, language: String): AnnotatedString {
    val keywords = keywordsByLanguage[language].orEmpty()
    val keywordStyle = SpanStyle(color = SlhAccent, fontWeight = FontWeight.Bold)
    val stringStyle = SpanStyle(color = SlhTextSecondary)
    val commentStyle = SpanStyle(color = SlhTextMuted, fontStyle = FontStyle.Italic)
    val numberStyle = SpanStyle(color = SlhAccent)
    val isShell = language in setOf("bash", "sh", "shell")
    val hashComments = isShell || language in setOf("python", "py")
    val isCpp = language in setOf("cpp", "c++", "c")

    return buildAnnotatedString {
        var index = 0
        while (index < code.length) {
            val char = code[index]
            when {
                !hashComments && code.startsWith("//", index) -> {
                    val end = code.indexOf('\n', index).let { if (it < 0) code.length else it }
                    withStyle(commentStyle) { append(code.substring(index, end)) }
                    index = end
                }

                hashComments && char == '#' -> {
                    val end = code.indexOf('\n', index).let { if (it < 0) code.length else it }
                    withStyle(commentStyle) { append(code.substring(index, end)) }
                    index = end
                }

                isCpp && char == '#' -> {
                    var end = index + 1
                    while (end < code.length && code[end].isLetter()) end++
                    withStyle(keywordStyle) { append(code.substring(index, end)) }
                    index = end
                }

                !hashComments && code.startsWith("/*", index) -> {
                    val end = code.indexOf("*/", index + 2).let { if (it < 0) code.length else it + 2 }
                    withStyle(commentStyle) { append(code.substring(index, end)) }
                    index = end
                }

                char == '"' || char == '\'' -> {
                    var end = index + 1
                    while (end < code.length && code[end] != char && code[end] != '\n') {
                        if (code[end] == '\\' && end + 1 < code.length) end++
                        end++
                    }
                    end = (end + 1).coerceAtMost(code.length)
                    withStyle(stringStyle) { append(code.substring(index, end)) }
                    index = end
                }

                char.isDigit() && (index == 0 || !code[index - 1].isLetterOrDigit() && code[index - 1] != '_') -> {
                    var end = index
                    while (end < code.length && (code[end].isLetterOrDigit() || code[end] == '.' || code[end] == '_')) {
                        if (code[end] == '.' && (end + 1 >= code.length || !code[end + 1].isDigit())) break
                        end++
                    }
                    withStyle(numberStyle) { append(code.substring(index, end)) }
                    index = end
                }

                char.isLetter() || char == '_' -> {
                    var end = index
                    while (end < code.length && (code[end].isLetterOrDigit() || code[end] == '_')) end++
                    val word = code.substring(index, end)
                    if (word in keywords) {
                        withStyle(keywordStyle) { append(word) }
                    } else {
                        append(word)
                    }
                    index = end
                }

                else -> {
                    append(char)
                    index++
                }
            }
        }
    }
}

private fun copyCode(context: Context, code: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java) ?: return
    clipboard.setPrimaryClip(ClipData.newPlainText("code", code))
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        Toast.makeText(context, Strings.get(R.string.code_copied), Toast.LENGTH_SHORT).show()
    }
}
