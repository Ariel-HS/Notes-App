package com.example.notesapp.utilities

import android.util.Log
import com.example.notesapp.data.Note

private fun quickSort(sortBy: String, ascending: Boolean, list: MutableList<Note>, left: Int = 0, right: Int = list.size-1) : MutableList<Note> {
    var start = left
    var end = right
    val pivot = list[(start+end)/2]

    while (start <= end) {
        when (sortBy) {
            "title" -> if (ascending) {
                while (compareString(list[start].title, pivot.title) < 0) {
                    start++
                }
            } else {
                while (compareString(list[start].title, pivot.title) > 0) {
                    start++
                }
            }
            "created" -> if (ascending) {
                while (list[start].createdAt < pivot.createdAt) {
                    start++
                }
            } else {
                while (list[start].createdAt > pivot.createdAt) {
                    start++
                }
            }
            "updated" -> if (ascending) {
                while (list[start].updatedAt < pivot.updatedAt) {
                    start++
                }
            } else {
                while (list[start].updatedAt > pivot.updatedAt) {
                    start++
                }
            }
        }

        when (sortBy) {
            "title" -> if (ascending) {
                while (compareString(list[end].title, pivot.title) > 0) {
                    end--
                }
            } else {
                while (compareString(list[end].title, pivot.title) < 0) {
                    end--
                }
            }
            "created" -> if (ascending) {
                while (list[end].createdAt > pivot.createdAt) {
                    end--
                }
            } else {
                while (list[end].createdAt < pivot.createdAt) {
                    end--
                }
            }
            "updated" -> if (ascending) {
                while (list[end].updatedAt > pivot.updatedAt) {
                    end--
                }
            } else {
                while (list[end].updatedAt < pivot.updatedAt) {
                    end--
                }
            }
        }

        if (start <= end) {
            val temp = list[start]
            list[start] = list[end]
            list[end] = temp
            start++
            end--
        }
    }

    if (left < end) {
        quickSort(sortBy, ascending, list, left, end)
    }

    if (start < right) {
        quickSort(sortBy, ascending, list, start, right)
    }

    return list
}

private fun quickSortHelper(sortOption: String, list: MutableList<Note>) : MutableList<Note> {
    when (sortOption) {
        "Title (A-Z)" -> {
            return quickSort("title", true, list)
        }
        "Title (Z-A)" -> {
            return quickSort("title", false, list)
        }
        "Created-at (Newest)" -> {
            return quickSort("created", false, list)
        }
        "Created-at (Oldest)" -> {
            return quickSort("created", true, list)
        }
        "Updated-at (Most Recent)" -> {
            return quickSort("updated", false, list)
        }
        "Updated-at (Least Recent)" -> {
            return quickSort("updated", true, list)
        }
        else -> {
            Log.d("Sorting", "Non Sorting Helper")
            return list
        }
    }
}

private fun selectionSort(sortBy: String, ascending: Boolean, list: MutableList<Note>) : MutableList<Note> {
    val n = list.size

    for (i in 0 until n-1) {
        var startIdx = i

        for (j in i + 1 until n) {
            when (sortBy) {
                "title" -> {
                    if (ascending) {
                        if (compareString(list[j].title, list[startIdx].title) < 0) {
                            startIdx = j
                        }
                    } else {
                        if (compareString(list[j].title, list[startIdx].title) >= 0) {
                            startIdx = j
                        }
                    }
                }
                "created" -> {
                    if (ascending) {
                        if (list[j].createdAt < list[startIdx].createdAt) {
                            startIdx = j
                        }
                    } else {
                        if (list[j].createdAt >= list[startIdx].createdAt) {
                            startIdx = j
                        }
                    }
                }
                "updated" -> {
                    if (ascending) {
                        if (list[j].updatedAt < list[startIdx].updatedAt) {
                            startIdx = j
                        }
                    } else {
                        if (list[j].updatedAt >= list[startIdx].updatedAt) {
                            startIdx = j
                        }
                    }
                }
            }
        }

        val temp = list[startIdx]
        list[startIdx] = list[i]
        list[i] = temp
    }

    return list
}

private fun selectionSortHelper(sortOption: String, list: MutableList<Note>) : MutableList<Note> {
    when (sortOption) {
        "Title (A-Z)" -> {
            return selectionSort("title", true, list)
        }
        "Title (Z-A)" -> {
            return selectionSort("title", false, list)
        }
        "Created-at (Newest)" -> {
            return selectionSort("created", false, list)
        }
        "Created-at (Oldest)" -> {
            return selectionSort("created", true, list)
        }
        "Updated-at (Most Recent)" -> {
            return selectionSort("updated", false, list)
        }
        "Updated-at (Least Recent)" -> {
            return selectionSort("updated", true, list)
        }
        else -> {
            return list
        }
    }
}

fun utilSort(algorithm: String, sortBy: String, list : MutableList<Note>) : MutableList<Note> {
    if (list.size < 2) {
        return list
    }

    if (algorithm == "Quick Sort") {
        return quickSortHelper(sortBy, list)
    }

    if (algorithm == "Selection Sort") {
        return selectionSortHelper(sortBy, list)
    }

    return list
}

private fun compareString(str1: String, str2: String): Int {
    var i = 0
    var j = 0

    while (i < str1.length && j < str2.length) {
        if (str1[i].lowercase() < str2[j].lowercase())
            return -1
        else if (str1[i].lowercase() > str2[j].lowercase())
            return 1
        // charAt(i) == charAt(j)
        else if (str1[i] > str2[i]) // charAt(i) is lowercase while charAt(j) is not
            return -1
        else if (str1[i] < str2[i]) // charAt(j) is lowercase while charAt(i) is not
            return 1
        i++
        j++
    }

    // str1.length > str2.length
    if (i < str1.length)
        return 1
    else if (j < str2.length) // str1.length < str2.length
        return -1

    return 0
}