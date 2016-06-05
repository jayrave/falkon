package com.jayrave.falkon.engine

/**
 * A class to mock a compiled statement and binding args
 */
class CompiledStatementForTest(val sql: String) {

    private val mutableBoundArgs = arrayOfNulls<Any?>(sql.count { it == '?' }).toMutableList()
    val boundArgs: List<Any?> = mutableBoundArgs

    fun bindArg(index: Int, arg: Any?) {
        mutableBoundArgs[index] = arg
    }
}


/**
 * Function to create [CompiledStatementForTest]
 */
val statementCompiler: (String) -> CompiledStatementForTest = {
    CompiledStatementForTest(it)
}


/**
 * Function to bind args to [CompiledStatementForTest]
 */
val argsBinder: (compiledStatement: CompiledStatementForTest, index: Int, arg: Any?) -> Any? = {
    cs, index, arg -> cs.bindArg(index, arg)
}


/**
 * If string doesn't start with the passed in [prefix], an exception will be thrown;
 * otherwise the prefix removed string will be returned
 */
fun String.removePrefixOrThrow(prefix: String): String {
    val prefixRemovedCs = this.removePrefix(prefix)
    return when (prefixRemovedCs) {
        this -> throw RuntimeException("\"$this\" doesn't start with \"$prefix\"")
        else -> prefixRemovedCs
    }
}