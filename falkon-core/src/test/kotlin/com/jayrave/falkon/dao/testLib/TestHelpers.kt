package com.jayrave.falkon.dao.testLib

import com.jayrave.falkon.engine.WhereSection
import com.jayrave.falkon.engine.WhereSection.Connector.CompoundConnector
import com.jayrave.falkon.engine.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.engine.WhereSection.Predicate.*


/**
 * Stringifies the passed in list of [WhereSection]s. Mostly used for assertions as
 * [WhereSection] doesn't override #toString
 */
internal fun buildWhereClauseWithPlaceholders(whereSections: Iterable<WhereSection>?): String? {
    var firstSection = true
    val sb = whereSections?.foldIndexed(StringBuilder()) { index, sb, section ->
        when {
            firstSection -> firstSection = false
            else -> sb.append(", ")
        }

        when (section) {
            is NoArgPredicate -> sb.append("${section.type} ${section.columnName}")
            is OneArgPredicate -> sb.append("${section.type} ${section.columnName}")
            is MultiArgPredicate -> sb.append(
                    "${section.type} ${section.columnName} ${section.numberOfArgs}"
            )

            is BetweenPredicate -> sb.append("BETWEEN ${section.columnName}")
            is SimpleConnector -> sb.append("${section.type}")
            is CompoundConnector -> sb.append(
                    "${section.type} ${buildWhereClauseWithPlaceholders(section.sections)}"
            )

            else -> throw IllegalArgumentException("Don't know to handle: $section")
        }

        sb
    }

    return sb?.toString()
}