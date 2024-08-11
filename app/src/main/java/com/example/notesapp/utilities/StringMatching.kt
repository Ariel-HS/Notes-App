package com.example.notesapp.utilities

import com.example.notesapp.data.Note
import kotlin.math.min

private fun borderFunction(pattern: String) : IntArray {
    val border = IntArray(pattern.length)
    border[0] = 0

    val m = pattern.length
    var j = 0
    var i = 1

    while (i < m) {
        if (pattern[j] == pattern[i]) {
            border[i] = j+1
            i++
            j++
        } else if (j>0) {
            j = border[j-1]
        } else {
            border[i] = 0
            i++
        }
    }

    return border
}

private fun kmpString(string: String, pattern: String, border: IntArray) : Int {
    val n = string.length
    val m = pattern.length

    if (n < m) {
        return -1
    }

    var i = 0
    var j = 0

    while (i < n) {
        if (pattern[j] == string[i]) {
            if (j == m-1) {
                return i-m+1
            }
            i++
            j++
        } else if (j>0) {
            j = border[j-1]
        } else {
            i++
        }
    }

    return -1
}

private fun lastFunction(pattern: String) : IntArray {
    val last = IntArray(128)

    for (i in 0 until 128) {
        last[i] = -1
    }

    for (i in 0 until pattern.length) {
        last[pattern[i].code] = i
    }

    return last
}

private fun bmString(string: String, pattern: String, last: IntArray) : Int {
    val n = string.length
    val m = pattern.length

    if (m > n) {
        return -1
    }

    var i = m-1
    var j = m-1

    do {
        if (pattern[j] == string[i]) {
            if (j == 0) {
                return i
            } else {
                i--
                j--
            }
        } else {
            val lastOcc = last[string[i].code]
            i = i + m - min(j, 1+lastOcc)
            j = m-1
        }
    } while (i <= n-1)

    return -1
}

private fun kmpMatch(matchBy: String, pattern: String, list: MutableList<Note>) : MutableList<Note> {
    val newList = mutableListOf<Note>()
    val border = borderFunction(pattern)

    when (matchBy) {
        "Title and Note" -> {
            list.forEach {
                if (kmpString(it.title, pattern, border) != -1 ||
                    kmpString(it.note, pattern, border) != -1) {
                    newList.add(it)
                }
            }
        }
        "Title" -> {
            list.forEach {
                if (kmpString(it.title, pattern, border) != -1) {
                    newList.add(it)
                }
            }
        }
        "Note" -> {
            list.forEach {
                if (kmpString(it.note, pattern, border) != -1) {
                    newList.add(it)
                }
            }
        }
    }

    return newList
}

private fun bmMatch(matchBy: String, pattern: String, list: MutableList<Note>) : MutableList<Note> {
    val newList = mutableListOf<Note>()
    val last = lastFunction(pattern)

    when (matchBy) {
        "Title and Note" -> {
            list.forEach {
                if (bmString(it.title, pattern, last) != -1 ||
                    bmString(it.note, pattern, last) != -1) {
                    newList.add(it)
                }
            }
        }
        "Title" -> {
            list.forEach {
                if (bmString(it.title, pattern, last) != -1) {
                    newList.add(it)
                }
            }
        }
        "Note" -> {
            list.forEach {
                if (bmString(it.note, pattern, last) != -1) {
                    newList.add(it)
                }
            }
        }
    }

    return newList
}

private fun kmpFilter(pattern: String, list: MutableList<Note>) : MutableList<Note> {
    val newList = mutableListOf<Note>()
    val border = borderFunction(pattern)

    list.forEach {
        // if exact match
        if (it.category.length == pattern.length &&
            kmpString(it.category, pattern, border) == 0) {
            newList.add(it)
        }
    }

    return newList
}

private fun bmFilter(matchBy: String, pattern: String, list: MutableList<Note>) : MutableList<Note> {
    val newList = mutableListOf<Note>()
    val last = lastFunction(pattern)

    list.forEach {
        // if exact match
        if (it.category.length == pattern.length &&
            bmString(it.category, pattern, last) != 0) {
            newList.add(it)
        }
    }

    return newList
}

fun utilMatch(algorithm: String, matchBy: String, pattern: String, list : MutableList<Note>) : MutableList<Note> {
    if (algorithm == "KMP") {
        return kmpMatch(matchBy, pattern, list)
    } else if (algorithm == "BM") {
        return bmMatch(matchBy, pattern, list)
    }

    return list
}

fun utilFilter(algorithm: String, pattern: String, list : MutableList<Note>) : MutableList<Note> {
    if (algorithm == "KMP") {
        return kmpFilter(pattern, list)
    } else if (algorithm == "BM") {
        return kmpFilter(pattern, list)
    }

    return list
}