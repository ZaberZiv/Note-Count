package com.zivapp.notes.views.mainmenu;

import com.zivapp.notes.model.MainMenuNote;

import java.util.ArrayList;

public interface MenuContract {

    interface Firebase {
        void updateNoteUI(ArrayList<MainMenuNote> list);
    }

    interface Presenter {
        void progressbarOn();
        void progressbarOff();
    }
}
