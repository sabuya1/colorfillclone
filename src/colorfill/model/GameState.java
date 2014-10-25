/*  ColorFill game and solver
    Copyright (C) 2014 Michael Henke

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package colorfill.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * this class represents the current state of the game.
 * it is the model of the GUI.
 */
public class GameState {

    public static final int DEFAULT_BOARD_WIDTH  = 14;
    public static final int DEFAULT_BOARD_HEIGHT = 14;
    public static final int DEFAULT_BOARD_COLORS = 6;
    public static final int DEFAULT_BOARD_STARTPOS = 0; // 0 == top left corner

    private int prefWidth;
    private int prefHeight;
    private int prefColors;
    private int prefStartPos;

    private Board board;
    private int startPos;
    private int numSteps;
    private final List<Integer> stepColor = new ArrayList<>();
    private final List<HashSet<ColorArea>> stepFlooded = new ArrayList<>();
    private final List<HashSet<ColorArea>> stepFloodNext = new ArrayList<>();

    public GameState() {
        this.prefWidth = DEFAULT_BOARD_WIDTH;
        this.prefHeight = DEFAULT_BOARD_HEIGHT;
        this.prefColors = DEFAULT_BOARD_COLORS;
        this.prefStartPos = DEFAULT_BOARD_STARTPOS;
        this.initBoard();
    }

    private void initBoard() {
        this.board = new Board(this.prefWidth, this.prefHeight, this.prefColors);
        this.startPos = this.prefStartPos;
        this.board.determineColorAreasDepth(this.startPos);
        this.numSteps = 0;
        this.stepColor.clear();
        this.stepColor.add(Integer.valueOf(this.board.getColor(this.startPos)));
        this.stepFlooded.clear();
        this.stepFlooded.add(new HashSet<ColorArea>(Collections.singleton(this.board.getColorArea(this.startPos))));
        this.stepFloodNext.clear();
        this.stepFloodNext.add(new HashSet<ColorArea>(this.board.getColorArea(this.startPos).getNeighbors()));
    }

    /**
     * get the current color of the cell at the specified index.
     * the color can either be the current flood color (if cell is
     * already flooded) or the original color of the board cell.
     * @param index of cell on the board
     * @return color number
     */
    public int getColor(final int index) {
        final Integer cell = Integer.valueOf(index);
        for (final ColorArea ca : this.stepFlooded.get(this.numSteps)) {
            if (ca.getMembers().contains(cell)) {
                return this.stepColor.get(this.numSteps).intValue();
            }
        }
        return this.board.getColor(index);
    }

    public Board getBoard() {
        return this.board;
    }
    public void setBoard(Board board) {
        this.board = board;
    }

    public int getPrefWidth() {
        return this.prefWidth;
    }
    public void setPrefWidth(int width) {
        this.prefWidth = width;
    }

    public int getPrefHeight() {
        return this.prefHeight;
    }
    public void setPrefHeight(int height) {
        this.prefHeight = height;
    }

    public int getPrefColors() {
        return this.prefColors;
    }
    public void setPrefColors(int colors) {
        this.prefColors = colors;
    }

    public int getPrefStartPos() {
        return this.prefStartPos;
    }
    public void setPrefStartPos(int startPos) {
        this.prefStartPos = startPos;
    }

    public int getNumSteps() {
        return this.numSteps;
    }
    public boolean isFinished() {
        return this.stepFloodNext.get(this.numSteps).isEmpty();
    }

    /**
     * try to append a new step to the progress of the game.
     * this may fail if the specified color is the current
     * flood color (no color change) or if no unflooded cells
     * are left on the board (puzzle finished).
     * 
     * @param color
     * @return true if the step was actually added
     */
    public boolean addStep(int color) {
        final Integer col = Integer.valueOf(color);
        // check if same color as before or nothing to be flooded
        if (this.stepColor.get(this.numSteps).equals(col)
                || this.stepFloodNext.get(this.numSteps).isEmpty()) {
            return false;
        }
        // current lists are too long (because of undo) - remove the future moves
        if (this.stepColor.size() > this.numSteps + 1) {
            this.stepColor.subList(this.numSteps + 1, this.stepColor.size()).clear();
            this.stepFlooded.subList(this.numSteps + 1, this.stepFlooded.size()).clear();
            this.stepFloodNext.subList(this.numSteps + 1, this.stepFloodNext.size()).clear();
        }
        // add stepColor
        this.stepColor.add(col);
        final Set<ColorArea> newFlood = new HashSet<>();
        for (final ColorArea ca : this.stepFloodNext.get(this.numSteps)) {
            if (ca.getColor().equals(col)) {
                newFlood.add(ca);
            }
        }
        // add stepFlooded
        @SuppressWarnings("unchecked")
        final HashSet<ColorArea> flooded = (HashSet<ColorArea>) this.stepFlooded.get(this.numSteps).clone();
        flooded.addAll(newFlood);
        this.stepFlooded.add(flooded);
        // add stepFloodNext
        @SuppressWarnings("unchecked")
        final HashSet<ColorArea> floodNext = (HashSet<ColorArea>) this.stepFloodNext.get(this.numSteps).clone();
        floodNext.removeAll(newFlood);
        for (final ColorArea ca : newFlood) {
            for (final ColorArea caN : ca.getNeighbors()) {
                if (false == flooded.contains(caN)) {
                    floodNext.add(caN);
                }
            }
        }
        this.stepFloodNext.add(floodNext);
        // next step
        ++ this.numSteps;
        return true;
    }

    /**
     * check if undo is possible
     * @return true if undo is possible
     */
    public boolean canUndoStep() {
        return this.numSteps > 0;
    }

    /**
     * undo a color step.
     * @return true if step undo was successful
     */
    public boolean undoStep() {
        if (this.canUndoStep()) {
            -- this.numSteps;
            return true;
        }
        return false;
    }

    /**
     * check if redo is possible
     * @return true if redo is possible
     */
    public boolean canRedoStep() {
        return this.numSteps < this.stepColor.size() - 1;
    }

    /**
     * redo a color step.
     * @return true if step redo was successful
     */
    public boolean redoStep() {
        if (this.canRedoStep()) {
            ++ this.numSteps;
            return true;
        }
        return false;
    }

    /**
     * create a new board with random cell color values.
     */
    public void setNewRandomBoard() {
        this.initBoard();
    }

}