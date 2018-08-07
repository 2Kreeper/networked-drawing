package eu.barononline.networked_drawing.ui.interaction;

import java.awt.event.KeyEvent;

public enum Shortcuts {

    DELETE(new Shortcut(KeyEvent.VK_DELETE, 0));

    private Shortcut shortcut;

    Shortcuts(Shortcut shortcut) {
        this.shortcut = shortcut;
    }

    public Shortcut getShortcut() {
        return shortcut;
    }

    public boolean matches(KeyEvent e) {
        return shortcut.matches(e);
    }

    public static Shortcuts valueOf(KeyEvent e) {
        for(Shortcuts s : Shortcuts.values()) {
            if(s.matches(e)) {
                return s;
            }
        }

        return null;
    }
}
