package slimtunes.view;

import javax.swing.JToggleButton.ToggleButtonModel;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

/**
 * Adapted from a code example posted
 * @see <a href="https://www.javaspecialists.eu/archive/Issue145-TristateCheckBox-Revisited.html">here</a>.
 */

public class TristateButtonModel extends ToggleButtonModel {

    public enum State {
        SELECTED,
        INDETERMINATE,
        DESELECTED;

        public State next() {
            switch(this){
                default:
                case SELECTED: return DESELECTED;
                case INDETERMINATE: return SELECTED;
                case DESELECTED: return INDETERMINATE;
            }
        }
    }

    private State state = State.DESELECTED;

    public TristateButtonModel(State state) {
        setState(state);
    }

    public TristateButtonModel() {
        this(State.DESELECTED);
    }

    public void setIndeterminate() {
        setState(State.INDETERMINATE);
    }

    public boolean isIndeterminate() {
        return state == State.INDETERMINATE;
    }

    // Overrides of superclass methods
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        // Restore state display
        displayState();
    }

    public void setSelected(boolean selected) {
        setState(selected ?
                State.SELECTED : State.DESELECTED);
    }

    // Empty overrides of superclass methods
    /*
    public void setArmed(boolean b) {
    }

    public void setPressed(boolean b) {
    }
    */

    @Override

    public void setPressed(boolean p) {
        // cannot change PRESSED state unless button is enabled
        if (!isEnabled())
            return;

        // if this call does not represent a CHANGE in state, then return
        if ((p && isPressed()) || (!p && !isPressed()))
            return;

        // The JDK first fires events in the following order:
        // 1. ChangeEvent for selected
        // 2. ChangeEvent for pressed
        // 3. ActionEvent
        // So do we.

        // setPressed(false) == mouse release on us,
        // if we were armed, we flip the selected state.
        if (!p && isArmed())
        {
            iterateState();
        }

        // make the change
        if (p)
            stateMask = stateMask | PRESSED;
        else
            stateMask = stateMask & (~PRESSED);

        // notify interested ChangeListeners
        fireStateChanged();

        if (!p && isArmed())
        {
            fireActionPerformed(new ActionEvent(this,
                    ActionEvent.ACTION_PERFORMED,
                    actionCommand));
        }
    }


    void iterateState() {
        setState(state.next());
    }

    private void setState(State state) {
        //Set internal state
        this.state = state;
        displayState();
        if (state == State.INDETERMINATE && isEnabled()) {
            // force the events to fire

            // Send ChangeEvent
            fireStateChanged();

            // Send ItemEvent
            int indeterminate = 3;
            fireItemStateChanged(new ItemEvent(
                    this, ItemEvent.ITEM_STATE_CHANGED, this,
                    indeterminate));
        }
    }

    private void displayState() {
        super.setSelected(state == State.SELECTED);
        //super.setArmed(state == State.INDETERMINATE);
        //super.setPressed(state == State.INDETERMINATE);

    }

    public State getState() {
        return state;
    }
}
