package com.example.e_commorce_fashions.data.datasrouce.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.e_commorce_fashions.app.utils.Constants
import kotlinx.coroutines.flow.Flow


@Dao
interface NotesDao {
//    @Upsert
//    suspend fun upsertNote(note: NoteDetailsEntity): Long
//
//    @Query("SELECT * FROM ${Constants.NOTES_TABLE} ORDER BY ${Constants.TIMESTAMP_COLUMN} DESC")
//    fun getNotes(): Flow<List<NoteDetailsEntity>>
//
//    @Update
//    suspend fun updateNote(note: NoteDetailsEntity)
//
//    @Delete
//    suspend fun deleteNote(note: NoteDetailsEntity)
//
//    @Query("DELETE FROM ${Constants.NOTES_TABLE} WHERE id = :noteId")
//    suspend fun deleteNoteById(noteId: Long)

}