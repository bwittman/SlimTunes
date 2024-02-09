package slimtunes.view;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Adapted from a code example posted
 * @see <a href="https://www.javaspecialists.eu/archive/Issue145-TristateCheckBox-Revisited.html">here</a>.
 */
public final class TristateCheckBox extends JCheckBox {
    // Listener on model changes to maintain correct focusability
    private final ChangeListener enableListener =
            e -> TristateCheckBox.this.setFocusable(
                    getModel().isEnabled());

    private final static Icon ICON = UIManager.getIcon("CheckBox.icon");
    private final static Insets INSETS = UIManager.getInsets("CheckBox.totalInsets");

    private final static Color HIGHLIGHT = UIManager.getColor("textHighlight");

    private final static Color NORMAL = UIManager.getColor("textText");

    private final static int BORDER = 4;


    //CheckBox.shadow
    //CheckBox.textIconGap
    //CheckBox.background
    //CheckBox.light
    //CheckBox.font
    //CheckBox.highlight
    //CheckBox.darkShadow
    //CheckBox.focus
    //CheckBox.focusInputMap
    //CheckBox.textShiftOffset
    //CheckBox.totalInsets
    //CheckBox.foreground
    //CheckBox.border
    //CheckBox.interiorBackground

    private boolean isHover = false;

    public TristateCheckBox(String text) {
        this(text, TristateButtonModel.State.DESELECTED);
    }

    public TristateCheckBox(String text,
                            TristateButtonModel.State initial) {
        super(text);
        //setIcon(this);






        //Set default single model
        setModel(new TristateButtonModel(initial));

        //TODO: This stuff isn't exactly right!
        // override action behaviour

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isHover)
                    TristateCheckBox.this.iterateState();
            }


            @Override
            public void mouseEntered(MouseEvent e) {
                isHover = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHover = false;
            }
        });

/*
        ActionMap actions = SwingUtilities.getUIActionMap(this);
        actions.put("released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TristateCheckBox.this.iterateState();
            }
        });
        SwingUtilities.replaceUIActionMap(this, actions);

 */
    }
    @Override
    public boolean isSelected() {
        return getState() != TristateButtonModel.State.DESELECTED;
    }



    // Next two methods implement new API by delegation to model
    public void setIndeterminate() {
        getTristateModel().setIndeterminate();
    }

    public boolean isIndeterminate() {
        return getTristateModel().isIndeterminate();
    }

    public TristateButtonModel.State getState() {
        return getTristateModel().getState();
    }

    //Overrides superclass method
    public void setModel(ButtonModel newModel) {
        super.setModel(newModel);

        //Listen for enable changes
        if (model instanceof TristateButtonModel)
            model.addChangeListener(enableListener);
    }

    // Mostly delegates to model
    private void iterateState() {
        //Maybe do nothing at all?
        if (!getModel().isEnabled()) return;

        grabFocus();

        // Iterate state
        getTristateModel().iterateState();

        // Fire ActionEvent
        int modifiers = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            modifiers = ((InputEvent) currentEvent).getModifiers();
        } else if (currentEvent instanceof ActionEvent) {
            modifiers = ((ActionEvent) currentEvent).getModifiers();
        }
        fireActionPerformed(new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED, getText(),
                System.currentTimeMillis(), modifiers));
    }

    //Convenience cast
    public TristateButtonModel getTristateModel() {
        return (TristateButtonModel) super.getModel();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (isIndeterminate()) {
            int w = ICON.getIconWidth();
            int h = ICON.getIconHeight();
            if (isEnabled()) {
                if (isHover)
                    g.setColor(HIGHLIGHT);
                else
                    g.setColor(NORMAL);
            }
            else
                g.setColor(new Color(122, 138, 153));
            int left = INSETS == null ? 4 : INSETS.left;
            // We have to add 1 to the top or else it looks bad (tested only on Windows)
            int top = INSETS == null ? 4 + 1 : INSETS.top + 1;
            g.fillRect(left + BORDER, top + BORDER, w - BORDER * 2, h  - BORDER * 2);
        }
    }
}