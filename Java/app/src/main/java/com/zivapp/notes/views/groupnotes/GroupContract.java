package com.zivapp.notes.views.groupnotes;

import com.zivapp.notes.model.GroupNote;
import com.zivapp.notes.model.MainMenuNote;

import java.util.ArrayList;

public interface GroupContract {

    interface Firebase {
        void updateGroupNoteUI(ArrayList<GroupNote> list);
        void updateTotalDataUI(MainMenuNote mainMenuNote);
    }

    interface Adapter {
        String getCurrentNoteID();
    }
}
