package com.ericmyval.users.model

sealed class Progress

object EmptyProgress: Progress()

data class PercentProgress(
    val percentage: Int
): Progress() {

    companion object {
        val START = PercentProgress(percentage = 0)
    }
}

fun Progress.isInProgress() = this !is EmptyProgress

fun Progress.getPercentage() = (this as? PercentProgress)?.percentage ?: PercentProgress.START.percentage