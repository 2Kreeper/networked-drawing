package eu.barononline.networked_drawing.ui.interaction;

import java.awt.event.KeyEvent;

public class Shortcut {

    private int charCode, modifiers;

    public Shortcut(int charCode, int modifiers) {
        this.charCode = charCode;
        this.modifiers = modifiers;
    }

    public boolean matches(KeyEvent e) {
        return e.getKeyCode() == charCode && e.getModifiers() == modifiers;
    }
}
