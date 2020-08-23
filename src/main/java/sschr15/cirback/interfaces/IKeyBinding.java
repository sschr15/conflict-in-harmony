package sschr15.cirback.interfaces;

/**
 * Basic "duck interface" to change values
 */
public interface IKeyBinding {
    void increasePressTime();
    void press(boolean state);
}
