package com.kelompok.rebook.data.local

import androidx.room.TypeConverter
import com.kelompok.rebook.data.BookStatus

class Converters {
    @TypeConverter
    fun fromBookStatus(status: BookStatus): String {
        return status.name
    }

    @TypeConverter
    fun toBookStatus(status: String): BookStatus {
        return BookStatus.valueOf(status)
    }
}
