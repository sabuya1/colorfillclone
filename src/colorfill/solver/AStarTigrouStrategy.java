/*  ColorFill game and solver
    Copyright (C) 2014, 2015 Michael Henke

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

import java.util.ArrayDeque;
import java.util.Queue;

import colorfill.model.Board;
import colorfill.model.ColorArea;
import colorfill.solver.AStarSolver.SolutionTree;

/**
 * a specific strategy for the AStar (A*) solver.
 * <p>
 * the idea is taken from the program by "tigrou" which won the
 * <a href="https://codegolf.stackexchange.com/questions/26232/create-a-flood-paint-ai/#26917">codegolf26232 competition</a>
 */
public class AStarTigrouStrategy implements AStarStrategy {

    private final Board board;
    private final SolutionTree solutionTree;
    private final AStarSolver solver;

    // queue is used in AStarNode.getSumDistances().
    // it exists here only for performance improvement.
    // (avoid construction / garbage collection inside that function)
    private final Queue<ColorArea> queue = new ArrayDeque<ColorArea>();

    public AStarTigrouStrategy(final Board board, final SolutionTree solutionTree, final AStarSolver solver) {
        this.board = board;
        this.solutionTree = solutionTree;
        this.solver = solver;
    }

    /* (non-Javadoc)
     * @see colorfill.solver.AStarStrategy#setEstimatedCost(colorfill.solver.AStarNode)
     */
    @Override
    public void setEstimatedCost(final AStarNode node, int nonCompletedColors) {
        AStarNode currentNode = node;
        //copy current board and play the best color until the end.
        //number of moves required to go the end is the heuristic
        //estimated cost = current cost + heuristic
        while (false == currentNode.isSolved()) {
            int minDistance = Integer.MAX_VALUE;
            AStarNode minNode = null;
            //find color which give the minimum sum of distance from root to each other node
            final long[] neighbors = currentNode.getNeighbors();
            int nextColors = this.solver.getColors(neighbors);
            while (0 != nextColors) {
                final int l1b = nextColors & -nextColors; // Integer.lowestOneBit()
                final int clz = Integer.numberOfLeadingZeros(l1b); // hopefully an intrinsic function using instruction BSR / LZCNT / CLZ
                nextColors ^= l1b; // clear lowest one bit
                final AStarNode nextNode = new AStarNode(currentNode);
                final byte color = (byte)(31 - clz);
                nextNode.play(color, this.solver.getColorAreas(neighbors, color), this.solutionTree, this.board);
                final int nextDistance = nextNode.getSumDistances(this.queue, this.board);
                if (minDistance > nextDistance) {
                    minDistance = nextDistance;
                    minNode = nextNode;
                }
            }
            currentNode = minNode;
        }
        node.setEstimatedCost(currentNode.getSolutionSize());
    }

}
