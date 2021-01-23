package com.zivapp.notes.views.notes;

import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.model.Note;

import java.util.ArrayList;

public interface NoteContract {

    interface Firebase {
        void updateNoteUI(ArrayList<Note> noteList);
        void workWithTotalData(MainMenuNote mainMenuNote);
    }
}
