package com.niki914.zafiro.api

/**
 * The learning store (outcomes, bandit suggestions, lessons), installed by the autonomy layer. The `outcomes` and `lessons`
 * tools (agent-runtime) forward here because agent-runtime cannot see the autonomy database. [tool] is outcomes | lessons;
 * every argument arrives as a string and is validated by the implementation. The result text is JSON or a short message.
 */
interface LearnService {
    suspend fun run(tool: String, args: Map<String, String>): TeamResult
}

/**
 * Files received through "Share to Zafiro" (outputs/inbox). The `inbox` tool forwards here so agent-runtime stays free of
 * the receiver's bookkeeping. Arguments are strings; ops are list, take and delete.
 */
interface InboxService {
    suspend fun run(args: Map<String, String>): TeamResult
}
