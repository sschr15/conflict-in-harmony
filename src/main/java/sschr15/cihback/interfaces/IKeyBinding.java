package sschr15.cihback.interfaces;

/**
 * Basic "duck interface" to change values
 */
public interface IKeyBinding {
    void increasePressTime();
    void press(boolean state);
}
