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

package colorfill.solver;

import java.util.ArrayList;
import java.util.List;

import colorfill.model.Board;

/**
 * an abstract implementation of interface Solver.
 */
public abstract class AbstractSolver implements Solver {

    protected final Board board;
    protected final List<List<Integer>> solutions = new ArrayList<>();
    protected int solutionSize = 0;

    /**
     * store the Board reference.
     * @param board to be solved
     */
    protected AbstractSolver(final Board board) {
        this.board = board;
    }

    /**
     * the actual solver main method, to be implemented by descendants of this class.
     * should call {@link #addSolution(List)} to collect the solution(s).
     * 
     * @param startPos position of the board cell where the color flood starts (0 == top left)
     */
    protected abstract void executeInternal(int startPos);

    /* (non-Javadoc)
     * @see colorfill.solver.Solver#execute(int)
     */
    @Override
    public int execute(final int startPos) {
        this.solutions.clear();
        this.solutionSize = Integer.MAX_VALUE;

        this.executeInternal(startPos);

        return this.solutionSize;
    }

    /* (non-Javadoc)
     * @see colorfill.solver.Solver#getSolutionString()
     */
    @Override
    public String getSolutionString() {
        final StringBuilder result = new StringBuilder();
        if (this.solutions.size() > 0) {
            for (final Integer color : this.solutions.get(0)) {
                result.append(color.intValue() + 1);
            }
        }
        return result.toString();
    }

    /**
     * add this solution to the list of solutions if it's shorter than
     * or same length as the current best solution(s).
     * the first element of this solution is ignored because it's the initial
     * color at the start position and therefore not a solution step.
     * in the list of solutions only the best (shortest) solution(s)
     * will be stored, longer solutions will be removed when a shorter solution
     * is added.
     * 
     * @param solution to be added
     * @return true if this solution was added
     */
    protected boolean addSolution(final List<Integer> solution) {
        if (this.solutionSize > solution.size() - 1) {
            this.solutionSize = solution.size() - 1;
            this.solutions.clear();
        }
        if (this.solutionSize == solution.size() - 1) {
            this.solutions.add(new ArrayList<>(solution.subList(1, solution.size())));
            return true;
        }
        return false;
    }
}
