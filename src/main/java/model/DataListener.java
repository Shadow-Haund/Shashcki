package model;

import java.util.EventListener;

public interface DataListener extends EventListener {

    void updateTextGuiLanguageInfo(UpdateGuiEvent e);

    void updateGUI(UpdateGuiEvent e);

    void noBlackCkeckersLeft(UpdateGuiEvent e);

    void noWhiteCkeckersLeft(UpdateGuiEvent e);

    void blackIsBlocked(UpdateGuiEvent e);

    void whiteIsBlocked(UpdateGuiEvent e);
}