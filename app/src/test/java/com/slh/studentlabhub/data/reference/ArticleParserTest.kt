package com.slh.studentlabhub.data.reference

import com.slh.studentlabhub.domain.model.ArticleBlock
import com.slh.studentlabhub.domain.model.NoteKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ArticleParserTest {

    @Test
    fun quizBlockIsSplitIntoQuestionAndAnswer() {
        val parsed = ArticleParser.parse(
            """
            # Title

            > [!QUIZ]
            > What is `7 / 2`?
            > ---
            > 3 — integer division.
            > The fraction is dropped.
            """.trimIndent(),
        )

        assertEquals(
            listOf(ArticleBlock.Quiz("What is `7 / 2`?", "3 — integer division. The fraction is dropped.")),
            parsed.blocks,
        )
    }

    @Test
    fun quizWithoutSeparatorHasEmptyAnswer() {
        val parsed = ArticleParser.parse("> [!QUIZ]\n> Question only")

        assertEquals(listOf(ArticleBlock.Quiz("Question only", "")), parsed.blocks)
    }

    @Test
    fun consecutiveQuizzesAndNotesStaySeparate() {
        val parsed = ArticleParser.parse(
            """
            > [!QUIZ]
            > Q1
            > ---
            > A1

            > [!QUIZ]
            > Q2
            > ---
            > A2

            > [!WARNING]
            > Careful
            """.trimIndent(),
        )

        assertEquals(
            listOf(
                ArticleBlock.Quiz("Q1", "A1"),
                ArticleBlock.Quiz("Q2", "A2"),
                ArticleBlock.Note(NoteKind.WARNING, "Careful"),
            ),
            parsed.blocks,
        )
    }

    @Test
    fun everyBundledArticleHasAnsweredQuizzes() {
        val articles = File("src/main/assets/reference").walkTopDown().filter { it.extension == "md" }.toList()
        assertTrue(articles.isNotEmpty())
        articles.forEach { file ->
            val parsed = ArticleParser.parse(file.readText())
            val quizzes = parsed.blocks.filterIsInstance<ArticleBlock.Quiz>()
            assertTrue("no title: $file", parsed.title.isNotBlank())
            assertTrue("no quiz: $file", quizzes.isNotEmpty())
            quizzes.forEach { quiz ->
                assertTrue("empty question: $file", quiz.question.isNotBlank())
                assertTrue("empty answer: $file", quiz.answer.isNotBlank())
            }
        }
    }
}
