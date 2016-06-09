package com.jayrave.falkon.dao.where

import com.jayrave.falkon.dao.where.WhereSection.Connector
import com.jayrave.falkon.dao.where.WhereSection.Connector.CompoundConnector
import com.jayrave.falkon.dao.where.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.dao.where.WhereSection.Predicate
import com.jayrave.falkon.dao.where.WhereSection.Predicate.*
import com.jayrave.falkon.exceptions.SQLSyntaxErrorException
import java.util.*

private const val ARG_PLACEHOLDER = '?'


internal fun <T : Any> List<WhereSection<T>>.buildWhere(): Where {
    val clause = StringBuilder()
    val args = LinkedList<Any?>()

    val lastIndex = size - 1
    forEachIndexed { index, section ->
        section.addTo(clause, args)
        if (index != lastIndex) {
            clause.append(' ')
        }
    }

    return Where(clause.toString(), args)
}


// ------------------------------- Start of add to section -----------------------------------------

private fun <T : Any> WhereSection<T>.addTo(clause: StringBuilder, args: MutableList<Any?>) {
    when (this) {
        is Predicate<T> -> {
            when (this) {
                is NoArgPredicate<T, *> -> this.addTo(clause)
                is OneArgPredicate<T, *> -> this.addTo(clause, args)
                is BetweenPredicate<T, *> -> this.addTo(clause, args)
                is LikePredicate<T, *> -> this.addTo(clause, args)
            }
        }

        is Connector<T> -> {
            when (this) {
                is SimpleConnector<T> -> this.addTo(clause)
                is CompoundConnector<T> -> this.addTo(clause, args)
            }
        }
    }
}


private fun <T : Any> NoArgPredicate<T, *>.addTo(clauseBuilder: StringBuilder) {
    clauseBuilder.appendNoArgPredicate(this)
}


private fun <T : Any, C> OneArgPredicate<T, C>.addTo(
        clause: StringBuilder, args: MutableList<Any?>) {

    clause.appendOneArgPredicate(this)
    args.add(column.computeStorageFormOf(value))
}


private fun <T : Any, C> BetweenPredicate<T, C>.addTo(
        clause: StringBuilder, args: MutableList<Any?>) {

    clause.appendBetweenPredicate(this)
    args.add(column.computeStorageFormOf(low))
    args.add(column.computeStorageFormOf(high))
}


private fun <T : Any, C> LikePredicate<T, C>.addTo(clause: StringBuilder, args: MutableList<Any?>) {
    clause.appendLikePredicate(this)
    args.add(pattern)
}


private fun <T : Any> SimpleConnector<T>.addTo(clause: StringBuilder) {
    clause.append(type.sqlText())
}


private fun <T : Any> CompoundConnector<T>.addTo(clause: StringBuilder, args: MutableList<Any?>) {
    throwIfSectionsIfEmpty(sections) // There should at least be one where section
    val connectorText = type.sqlText()

    // Open parenthesis
    clause.append('(')

    val lastSectionIndex = sections.size - 1
    sections.forEachIndexed { index, section ->
        section.addTo(clause, args)
        if (index != lastSectionIndex) {
            clause.append(" $connectorText ")
        }
    }

    // Close parenthesis
    clause.append(')')
}

// ---------------------------------- End of add to section ----------------------------------------


// ---------------------------- Start of append predicate section ----------------------------------

private fun <T : Any> StringBuilder.appendNoArgPredicate(predicate: NoArgPredicate<T, *>) {
    append("${predicate.column.name} ${predicate.type.sqlText()}")
}


private fun <T : Any> StringBuilder.appendOneArgPredicate(predicate: OneArgPredicate<T, *>) {
    append("${predicate.column.name} ${predicate.type.sqlText()} $ARG_PLACEHOLDER")
}


private fun <T : Any> StringBuilder.appendBetweenPredicate(predicate: BetweenPredicate<T, *>) {
    append("${predicate.column.name} BETWEEN $ARG_PLACEHOLDER AND $ARG_PLACEHOLDER")
}


private fun <T : Any> StringBuilder.appendLikePredicate(predicate: LikePredicate<T, *>) {
    append("${predicate.column.name} LIKE $ARG_PLACEHOLDER")
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


private fun <T: Any> throwIfSectionsIfEmpty(sections: List<WhereSection<T>>) {
    if (sections.isEmpty()) {
        throw SQLSyntaxErrorException("Sections can't be empty")
    }
}