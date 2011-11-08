/* Copyright 2003-4 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/* 
    Class for undo/redo.
    Extracted from TextComponentDemo.java in java.sun.com.
*/
package eui;
import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import java.awt.event.*;

class UndoRedo {
  UndoAction undoAction = new UndoAction();
  RedoAction redoAction = new RedoAction();
  UndoManager undo = new UndoManager();
  MyUndoableEditListener myundoable = new MyUndoableEditListener();

  class UndoAction extends AbstractAction {
    public UndoAction() {
      super(Config.Undo);
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
      try {
        undo.undo();
      } catch (CannotUndoException ex) {
        System.out.println("Unable to undo: " + ex);
        ex.printStackTrace();
      }
      updateUndoState();
      redoAction.updateRedoState();
    }

    protected void updateUndoState() {
      if (undo.canUndo()) {
        setEnabled(true);
        putValue(Action.NAME, undo.getUndoPresentationName());
      } else {
        setEnabled(false);
        putValue(Action.NAME, Config.Undo);
      }
    }
  }

  class RedoAction extends AbstractAction {
    public RedoAction() {
      super(Config.Redo);
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
      try {
        undo.redo();
      } catch (CannotRedoException ex) {
        System.out.println("Unable to redo: " + ex);
        ex.printStackTrace();
      }
      updateRedoState();
      undoAction.updateUndoState();
    }

    protected void updateRedoState() {
      if (undo.canRedo()) {
        setEnabled(true);
        putValue(Action.NAME, undo.getRedoPresentationName());
      } else {
        setEnabled(false);
        putValue(Action.NAME, Config.Redo);
      }
    }
  }

  protected class MyUndoableEditListener
                  implements UndoableEditListener {
    public void undoableEditHappened(UndoableEditEvent e) {
      //Remember the edit and update the menus.
      undo.addEdit(e.getEdit());
      undoAction.updateUndoState();
      redoAction.updateRedoState();
    }
  }
}
