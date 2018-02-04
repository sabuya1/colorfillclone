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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

import colorfill.model.Board;
import colorfill.model.ColorArea;

/**
 * the node used by the AStar (A*) solver.
 */
public class AStarNode {

    private final ColorAreaSet flooded;
    private final ColorAreaSet neighbors;
    private final byte[] solution;
    private byte solutionSize;
    private byte estimatedCost;

    /**
     * initial constructor.
     * @param startCa
     */
    public AStarNode(final Board board, final ColorArea startCa) {
        this.flooded = new ColorAreaSet(board);
        this.flooded.add(startCa);
        this.neighbors = new ColorAreaSet(board);
        this.neighbors.addAll(startCa.getNeighborsIdArray());
        this.solution = new byte[AbstractSolver.MAX_SEARCH_DEPTH];
        this.solution[0] = startCa.getColor();
        this.solutionSize = 0;
        this.estimatedCost = Byte.MAX_VALUE;
        assert AbstractSolver.MAX_SEARCH_DEPTH < Byte.MAX_VALUE;
    }

    /**
     * copy constructor.
     * @param other
     */
    public AStarNode(final AStarNode other) {
        this.flooded = new ColorAreaSet(other.flooded);
        this.neighbors = new ColorAreaSet(other.neighbors);
        this.solution = other.solution.clone();
        this.solutionSize = other.solutionSize;
        //this.estimatedCost = other.estimatedCost;  // not necessary to copy
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
        return Arrays.copyOfRange(this.solution, 1, this.solutionSize + 1);
    }

    /**
     * get the number of steps in the solution of this node.
     * @return
     */
    public int getSolutionSize() {
        return this.solutionSize;
    }

    /**
     * get the list of neighbor colors.
     * @return
     */
    public int getNeighborColors(final Board board) {
        int result = 0;
        final ColorAreaSet.IteratorColorAreaId iter = this.neighbors.iteratorColorAreaId();
        while (iter.hasNext()) {
            result |= 1 << board.getColor4Id(iter.next());
        }
        return result;
    }

    /**
     * copy contents of "flooded" set to this one.
     * @param other
     */
    public void copyFloodedTo(final ColorAreaSet other) {
        other.copyFrom(this.flooded);
    }

    /**
     * copy contents of "neighbors" set to this one.
     * @param other
     */
    public void copyNeighborsTo(final ColorAreaSet other) {
        other.copyFrom(this.neighbors);
    }

    /**
     * check if this color can be played. (avoid duplicate moves)
     * the idea is taken from the program "floodit" by Aaron and Simon Puchert,
     * which can be found at <a>https://github.com/aaronpuchert/floodit</a>
     * @param nextColor
     * @return
     */
    private boolean canPlay(final byte nextColor, final List<ColorArea> nextColorNeighbors) {
        final byte currColor = this.solution[this.solutionSize];
        // did the previous move add any new "nextColor" neighbors?
        boolean newNext = false;
next:   for (final ColorArea nextColorNeighbor : nextColorNeighbors) {
            for (final ColorArea prevNeighbor : nextColorNeighbor.getNeighborsArray()) {
                if ((prevNeighbor.getColor() != currColor) && this.flooded.contains(prevNeighbor)) {
                    continue next;
                }
            }
            newNext = true;
            break next;
        }
        if (!newNext) {
            if (nextColor < currColor) {
                return false;
            } else {
                // should nextColor have been played before currColor?
                for (final ColorArea nextColorNeighbor : nextColorNeighbors) {
                    for (final ColorArea prevNeighbor : nextColorNeighbor.getNeighborsArray()) {
                        if ((prevNeighbor.getColor() == currColor) && !this.flooded.contains(prevNeighbor)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * play the given color.
     * @param nextColor
     */
    public void play(final byte nextColor, final Board board) {
        final List<ColorArea> nextColorNeighbors = new ArrayList<ColorArea>(128);  // constant, arbitrary initial capacity
        final ColorAreaSet.IteratorColorAreaId iter = this.neighbors.iteratorColorAreaId();
        while (iter.hasNext()) {
            final ColorArea nextColorNeighbor = board.getColorArea4Id(iter.next());
            if (nextColorNeighbor.getColor() == nextColor) {
                nextColorNeighbors.add(nextColorNeighbor);
            }
        }
        for (final ColorArea nextColorNeighbor : nextColorNeighbors) {
            this.flooded.add(nextColorNeighbor);
            this.neighbors.addAll(nextColorNeighbor.getNeighborsIdArray());
        }
        this.neighbors.removeAll(this.flooded);
        this.solution[++this.solutionSize] = nextColor;
    }

    /**
     * try to re-use the given node or create a new one
     * and then play the given color in the result node.
     * @param nextColor
     * @param recycleNode
     * @return
     */
    public AStarNode copyAndPlay(final byte nextColor, final AStarNode recycleNode, final Board board) {
        final List<ColorArea> nextColorNeighbors = new ArrayList<ColorArea>(128);  // constant, arbitrary initial capacity
        final ColorAreaSet.IteratorColorAreaId iter = this.neighbors.iteratorColorAreaId();
        while (iter.hasNext()) {
            final ColorArea nextColorNeighbor = board.getColorArea4Id(iter.next());
            if (nextColorNeighbor.getColor() == nextColor) {
                nextColorNeighbors.add(nextColorNeighbor);
            }
        }
        if (!this.canPlay(nextColor, nextColorNeighbors)) {
            return null;
        } else {
            final AStarNode result;
            if (null == recycleNode) {
                result = new AStarNode(this);
            } else {
                // copy - compare copy constructor
                result = recycleNode;
                result.flooded.copyFrom(this.flooded);
                result.neighbors.copyFrom(this.neighbors);
                System.arraycopy(this.solution, 0, result.solution, 0, this.solutionSize + 1);
                result.solutionSize = this.solutionSize;
                //result.estimatedCost = this.estimatedCost;  // not necessary to copy
            }
            // play - compare method play()
            for (final ColorArea nextColorNeighbor : nextColorNeighbors) {
                result.flooded.add(nextColorNeighbor);
                result.neighbors.addAll(nextColorNeighbor.getNeighborsIdArray());
            }
            result.neighbors.removeAll(result.flooded);
            result.solution[++result.solutionSize] = nextColor;
            return result;
        }
    }

    /**
     * create a "simple" comparator for use in PriorityQueue
     * @return
     */
    public static Comparator<AStarNode> simpleComparator() {
        return new Comparator<AStarNode>() {
            @Override
            public int compare(AStarNode o1, AStarNode o2) {
                return o1.estimatedCost - o2.estimatedCost;
            }
        };
    }

    /**
     * create a "stronger" comparator for use in PriorityQueue
     * @return
     */
    public static Comparator<AStarNode> strongerComparator() {
        return new Comparator<AStarNode>() {
            @Override
            public int compare(AStarNode o1, AStarNode o2) {
                if (o1.estimatedCost != o2.estimatedCost) {
                    return o1.estimatedCost - o2.estimatedCost;
                } else {
                    return o2.solutionSize - o1.solutionSize;
                }
            }
        };
    }

    /**
     * set estimated cost, which is used for natural ordering (in function compareTo)
     * @param estimatedCost
     */
    public void setEstimatedCost(final int estimatedCost) {
        this.estimatedCost = (byte)estimatedCost;
    }
    public int getEstimatedCost() {
        return this.estimatedCost;
    }

    /**
     * calculate the sum of distances from current flooded area to all remaining areas.
     * @param queue an empty Queue; used inside this function; will be empty on return.
     * @param depths an array of int; must be large enough to store a value for each ColorArea on the Board.
     * @return
     */
    public int getSumDistances(final Queue<ColorArea> queue, final Board board) {
        final int NO_DEPTH = -1;
        for (final ColorArea ca : board.getColorAreasArray()) {
            if (this.flooded.contains(ca)) {
                ca.tmpAStarDepth = 0;  // start
                queue.offer(ca);
            } else {
                ca.tmpAStarDepth = NO_DEPTH;  // reset
            }
        }
        int sumDistances = 0;
        ColorArea currentCa;
        while (null != (currentCa = queue.poll())) { // while queue is not empty
            final int nextDepth = currentCa.tmpAStarDepth + 1;
            for (final ColorArea nextCa : currentCa.getNeighborsArray()) {
                if (nextCa.tmpAStarDepth == NO_DEPTH) {
                    nextCa.tmpAStarDepth = nextDepth;
                    sumDistances += nextDepth;
                    queue.offer(nextCa);
                }
            }
        }
        // queue is empty now
        return sumDistances;
    }
}
