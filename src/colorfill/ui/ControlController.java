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

package colorfill.ui;

import javax.swing.JPanel;

import colorfill.model.GameProgress;
import colorfill.model.GameState;

/**
 * this controller handles the control flows of ControlPanel.
 */
public class ControlController {

    private final MainController mainController;
    private final GameState gameState;
    private final ControlPanel controlPanel;

    protected ControlController(final MainController mainController, final GameState gameState) {
        this.mainController = mainController;
        this.gameState = gameState;
        this.controlPanel = new ControlPanel(this, this.gameState.getPreferences().getUiColors(), this.gameState.getPreferences().getNumColors());
    }

    protected JPanel getPanel() {
        return this.controlPanel;
    }

    /**
     * called when the colors of the board cells have changed and the
     * controls should be updated using values from the model.
     */
    protected void actionUpdateBoardColors() {
        this.controlPanel.setLabelMove(this.gameState.getSelectedProgress().getCurrentStep(), this.gameState.getSelectedProgress().isFinished());
        this.controlPanel.setButtons(this.gameState.getSelectedProgress().canUndoStep(), this.gameState.getSelectedProgress().canRedoStep());
        this.controlPanel.setButtonColors(this.gameState.getPreferences().getUiColors(), this.gameState.getPreferences().getNumColors());
    }

    /**
     * called by ControlPanel when user clicks on button "New"
     */
    protected void userButtonNew() {
        this.mainController.actionNewBoard();
    }

    /**
     * called by ControlPanel when user clicks on button "Settings"
     */
    protected void userButtonPrefs() {
        this.mainController.actionPreferences();
    }

    /**
     * called by ControlPanel when user clicks on button "Undo"
     */
    protected void userButtonUndo() {
        this.mainController.actionUndoStep();
    }

    /**
     * called by ControlPanel when user clicks on button "Redo"
     */
    protected void userButtonRedo() {
        this.mainController.actionRedoStep();
    }

    /**
     * called by ControlPanel when user clicks on one of the solution RadioButtons.
     * @param numSolution number of the selected solution (0 == user solution, other = solver solutions)
     */
    protected void userButtonSolution(final int numSolution) {
        this.mainController.actionSelectGameProgress(numSolution);
    }

    /**
     * called by ControlPanel when user clicks on a color button.
     * @param color number
     */
    protected void userButtonColor(final int color) {
        if (this.gameState.isUserProgress()) {
            this.mainController.actionAddStep(color);
        }
    }

    /**
     * remove all solver results from control panel.
     */
    protected void actionClearSolverResults() {
//        System.out.println("ControlController.actionClearSolverResults");
        this.controlPanel.clearSolverResults();
    }

    /**
     * add this solver result to control panel.
     * @param gameProgress solver result
     */
    protected void actionAddSolverResult(final GameProgress gameProgress) {
//        System.out.println("ControlController.actionAddSolverResult " + gameProgress);
        this.controlPanel.addSolverResult(gameProgress.getTotalSteps() + " " + gameProgress.getName());
    }
}
