package com.jayrave.falkon.engine

import com.jayrave.falkon.engine.WhereSection.Connector
import com.jayrave.falkon.engine.WhereSection.Connector.CompoundConnector
import com.jayrave.falkon.engine.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.engine.WhereSection.Predicate
import com.jayrave.falkon.engine.WhereSection.Predicate.*
import com.jayrave.falkon.exceptions.SQLSyntaxErrorException

/**
 * A SQL WHERE is built from the passed in [WhereSection]s with placeholder denoted by
 * [ARG_PLACEHOLDER].
 *
 * @return - `null` if the iterable is empty or a WHERE clause (along with WHERE keyword)
 */
internal fun Iterable<WhereSection>.buildWhereClause(
        argPlaceholder: String = DEFAULT_ARG_PLACEHOLDER): String? {

    val clause = StringBuilder()
    var isFirstSection = true

    forEach { section ->
        when (isFirstSection) {
            true -> isFirstSection = false
            else -> clause.append(' ')
        }

        section.addTo(clause, argPlaceholder)
    }

    return when {
        clause.isEmpty() -> null
        else -> {
            clause.insert(0, "WHERE ")
            clause.toString()
        }
    }
}


// ------------------------------- Start of add to section -----------------------------------------

private fun WhereSection.addTo(clause: StringBuilder, argPlaceholder: String) {
    when (this) {
        is Predicate -> {
            when (this) {
                is NoArgPredicate -> this.addTo(clause)
                is OneArgPredicate -> this.addTo(clause, argPlaceholder)
                is BetweenPredicate -> this.addTo(clause, argPlaceholder)
            }
        }

        is Connector -> {
            when (this) {
                is SimpleConnector -> this.addTo(clause)
                is CompoundConnector -> this.addTo(clause, argPlaceholder)
            }
        }
    }
}


private fun NoArgPredicate.addTo(clauseBuilder: StringBuilder) {
    clauseBuilder.appendNoArgPredicate(this)
}


private fun OneArgPredicate.addTo(clause: StringBuilder, argPlaceholder: String) {
    clause.appendOneArgPredicate(this, argPlaceholder)
}


private fun BetweenPredicate.addTo(clause: StringBuilder, argPlaceholder: String) {
    clause.appendBetweenPredicate(this, argPlaceholder)
}


private fun SimpleConnector.addTo(clause: StringBuilder) {
    clause.append(type.sqlText())
}


private fun CompoundConnector.addTo(clause: StringBuilder, argPlaceholder: String) {
    if (sections.isEmpty()) {
        throw SQLSyntaxErrorException(
                "There should be at least 1 predicate for " +
                        "${CompoundConnector::class.qualifiedName}'s $type"
        )
    }

    val connectorText = type.sqlText()

    // Open parenthesis
    clause.append('(')

    val lastSectionIndex = sections.size - 1
    sections.forEachIndexed { index, predicate ->
        predicate.addTo(clause, argPlaceholder)
        if (index != lastSectionIndex) {
            clause.append(" $connectorText ")
        }
    }

    // Close parenthesis
    clause.append(')')
}

// ---------------------------------- End of add to section ----------------------------------------


// ---------------------------- Start of append predicate section ----------------------------------

private fun StringBuilder.appendNoArgPredicate(predicate: NoArgPredicate) {
    append("${predicate.columnName} ${predicate.type.sqlText()}")
}


private fun StringBuilder.appendOneArgPredicate(
        predicate: OneArgPredicate, argPlaceholder: String) {
    append("${predicate.columnName} ${predicate.type.sqlText()} $argPlaceholder")
}


private fun StringBuilder.appendBetweenPredicate(
        predicate: BetweenPredicate, argPlaceholder: String) {
    append("${predicate.columnName} BETWEEN $argPlaceholder AND $argPlaceholder")
}

// ----------------------------- End of append predicate section -----------------------------------


// --------------------------------- Start of SQL text section -------------------------------------

private fun NoArgPredicate.Type.sqlText(): String {
    return when (this) {
        NoArgPredicate.Type.IS_NULL -> "IS NULL"
        NoArgPredicate.Type.IS_NOT_NULL -> "IS NOT NULL"
    }
}


private fun OneArgPredicate.Type.sqlText(): String {
    return when (this) {
        OneArgPredicate.Type.EQ -> "="
        OneArgPredicate.Type.NOT_EQ -> "!="
        OneArgPredicate.Type.GREATER_THAN -> ">"
        OneArgPredicate.Type.GREATER_THAN_OR_EQ -> ">="
        OneArgPredicate.Type.LESS_THAN -> "<"
        OneArgPredicate.Type.LESS_THAN_OR_EQ -> "<="
        OneArgPredicate.Type.LIKE -> "LIKE"
    }
}


private fun SimpleConnector.Type.sqlText(): String {
    return when (this) {
        SimpleConnector.Type.AND -> "AND"
        SimpleConnector.Type.OR -> "OR"
    }
}


private fun CompoundConnector.Type.sqlText(): String {
    return when (this) {
        CompoundConnector.Type.AND -> "AND"
        CompoundConnector.Type.OR -> "OR"
    }
}

// ----------------------------------- End of SQL text section -------------------------------------