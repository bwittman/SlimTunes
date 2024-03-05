package slimtunes.controller;

import slimtunes.model.Library;

public interface Action {
    void doAction(Controller controller);
    void undoAction(Controller controller);
}
