package com.slh.studentlabhub.domain.repository

import com.slh.studentlabhub.domain.model.ArticleHit
import com.slh.studentlabhub.domain.model.RefArticle
import com.slh.studentlabhub.domain.model.RefChapter
import com.slh.studentlabhub.domain.model.RefTech

interface ArticleRepository {
    suspend fun chapters(tech: RefTech): List<RefChapter>

    suspend fun article(tech: RefTech, articleId: String): RefArticle?

    suspend fun search(query: String): List<ArticleHit>
}
