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

import java.util.Queue;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;

import colorfill.model.Board;
import colorfill.model.ColorArea;

/**
 * the node used by the AStar (A*) solver.
 */
public class AStarNode implements Comparable<AStarNode> {

    private final ColorAreaSet flooded;
    private final ColorAreaGroup neighbors;
    private final ByteList solution;
    private int estimatedCost;

    /**
     * initial constructor.
     * @param startCa
     */
    public AStarNode(final Board board, final ColorArea startCa) {
        this.flooded = new ColorAreaSet(board);
        this.flooded.add(startCa);
        this.neighbors = new ColorAreaGroup(board);
        this.neighbors.addAll(startCa.getNeighborsArray(), this.flooded);
        this.solution = new ByteArrayList();
        this.estimatedCost = Integer.MAX_VALUE;
    }

    /**
     * copy constructor.
     * @param other
     */
    public AStarNode(final AStarNode other) {
        this.flooded = new ColorAreaSet(other.flooded);
        this.neighbors = new ColorAreaGroup(other.neighbors);
        this.solution = new ByteArrayList(other.solution);
        this.estimatedCost = other.estimatedCost;
    }

    /**
     * is this a final node?
     * @return
     */
    public boolean isSolved() {
        return this.neighbors.isEmpty();
    }

    /**
     * get the solution stored in this node.
     * @return
     */
    public byte[] getSolution() {
        return this.solution.toByteArray();
    }

    /**
     * get the number of steps in the solution of this node.
     * @return
     */
    public int getSolutionSize() {
        return this.solution.size();
    }

    /**
     * get the list of neighbor colors.
     * @return
     */
    public ByteList getNeighborColors() {
        return this.neighbors.getColorsNotEmpty();
    }

    /**
     * play the given color.
     * @param nextColor
     */
    public void play(final byte nextColor) {
        final ColorAreaSet tmpFlooded = new ColorAreaSet(this.neighbors.getColor(nextColor));
        this.flooded.addAll(tmpFlooded);
        this.neighbors.clearColor(nextColor);
        for (final ColorArea tmpCa : tmpFlooded) {
            this.neighbors.addAll(tmpCa.getNeighborsArray(), this.flooded);
        }
        this.solution.add(nextColor);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final AStarNode other) {
        return Integer.signum(this.estimatedCost - other.estimatedCost);
    }

    /**
     * set estimated cost, which is used for natural ordering (in function compareTo)
     * @param estimatedCost
     */
    public void setEstimatedCost(final int estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
    public int getEstimatedCost() {
        return this.estimatedCost;
    }

    /**
     * calculate the sum of distances from current flooded area to all remaining areas.
     * @param queue an empty Queue; used inside this function; will be empty on return.
     * @return
     */
    public int getSumDistances(final Queue<ColorArea> queue) {
        final int NO_DEPTH = -1;
        for (final ColorArea ca : this.flooded.getBoard().getColorAreasArray()) {
            if (this.flooded.contains(ca)) {
                ca.dynamicDepth = 0;  // start
                queue.offer(ca);
            } else {
                ca.dynamicDepth = NO_DEPTH;  // reset
            }
        }
        int sumDistances = 0;
        ColorArea current;
        while (null != (current = queue.poll())) { // while queue is not empty
            final int nextDepth = current.dynamicDepth + 1;
            for (final ColorArea next : current.getNeighborsArray()) {
                if (next.dynamicDepth == NO_DEPTH) {
                    next.dynamicDepth = nextDepth;
                    sumDistances += nextDepth;
                    queue.offer(next);
                }
            }
        }
        // queue is empty now
        return sumDistances;
    }
}
