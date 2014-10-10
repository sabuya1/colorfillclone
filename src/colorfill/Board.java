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

package colorfill;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * The Board class represents the board (or game problem)
 */
public class Board {

    private final byte[] cells;
    private final int width;
    private final Set<ColorArea> colorAreas;

    /**
     * construct a new Board from a text representation:
     * each character represents the color (number 1...n) of a cell in a square grid.
     * the length of the string must therefore be a square number (like 14x14=196).
     * 
     * an example file with 1000 boards in a text file can be found here:
     * http://cplus.about.com/od/programmingchallenges/a/challenge19.htm
     * 
     * @param str board in text form, any whitespace characters will be ignored
     */
    public Board(String str) {
        str = str.replaceAll("\\s", ""); // remove whitespace
        final int len = str.length(); // TODO check if "len" is a square number
        this.cells = new byte[len];
        this.width = (int)Math.sqrt(len);
        for (int i = 0;  i < len;  ++i) {
            final char c = str.charAt(i);
            this.cells[i] = Byte.parseByte(String.valueOf(c));
        }
        this.colorAreas = this.createColorAreas();
    }

    private Set<ColorArea> createColorAreas() {
        final Set<ColorArea> result = new HashSet<ColorArea>();
        // build ColorAreas, fill with them with members: adjacent cells of the same color
        for (int index = 0;  index < this.cells.length;  ++index) {
            final int color = this.cells[index];
            boolean isAdded = false;
            for (final ColorArea ca : result) {
                if (ca.addMember(index, color)) {
                    isAdded = true;
                    break; // for()
                }
            }
            if (false == isAdded) {
                final ColorArea ca = new ColorArea(color);
                ca.addMember(index, color);
                result.add(ca);
            }
        }
        // connect all ColorAreas to all of their neighbors
        for (final ColorArea ca1 : result) {
            for (final ColorArea ca2 : result) {
                ca1.addNeighbor(ca2);
            }
        }
        return new TreeSet<ColorArea>(result); // sorted set
    }


    /**
     * parse the text representation of the solution
     * and check if it solves this board.
     * 
     * @param str characters 1...n (color values), any whitespace characters will be ignored
     * @param startPos position of the board cell where the color flood starts (0 == top left)
     * @return error message in case the check fails, empty string if check is successful
     */
    public String checkSolution(String str, final int startPos) {
        // parse the solution text
        str = str.replaceAll("\\s", ""); // remove whitespace
        final int len = str.length();
        final byte[] solution = new byte[len];
        for (int i = 0;  i < len;  ++i) {
            final char c = str.charAt(i);
            solution[i] = Byte.parseByte(String.valueOf(c));
        }
        final Set<ColorArea> floodAreas = new TreeSet<ColorArea>();
        final Set<ColorArea> floodNeighbors = new TreeSet<ColorArea>();
        int floodColor = 0;
        // start with the ColorArea that contains cell startPos
        for (final ColorArea ca : this.colorAreas) {
            if (ca.members.contains(Integer.valueOf(startPos))) {
                floodColor = ca.color;
                floodAreas.add(ca);
                floodNeighbors.addAll(ca.neighbors);
                break; // for()
            }
        }
        // apply all colors from solution
        for (final byte solutionColor : solution) {
            if (floodColor == solutionColor) {
                return "error in solution: duplicate color " + solutionColor;
            }
            floodColor = solutionColor;
            // add all floodNeighbors of matching color to floodAreas
            final Set<ColorArea> newFloodAreas = new TreeSet<ColorArea>();
            for (final ColorArea ca : floodNeighbors) {
                if (ca.color == floodColor) {
                    newFloodAreas.add(ca);
                }
            }
            floodAreas.addAll(newFloodAreas);
            // add new neighbors to floodNeighbors
            for (final ColorArea ca : newFloodAreas) {
                for (final ColorArea caN : ca.neighbors) {
                    if (false == floodAreas.contains(caN)) {
                        floodNeighbors.add(caN);
                    }
                }
            }
            // remove the newly flooded areas from floodNeighbors
            floodNeighbors.removeAll(newFloodAreas);
        }
        // solution finished, check if board is completely flooded
        if ((floodAreas.size() != this.colorAreas.size()) ||
                (false == floodAreas.containsAll(this.colorAreas)) ||
                (false == floodNeighbors.isEmpty())) {
            return "error in solution: board is not completely flooded"
                    + "\n floodAreas:     " + floodAreas
                    + "\n floodNeighbors: " + floodNeighbors
                    ;
        } else {
            return ""; // check OK
        }
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0;  i < this.cells.length;  ++i) {
            sb.append(this.cells[i]);
            if (0 == (i + 1) % width) {
                sb.append('\n');
            }
        }
        sb.append(this.colorAreas);
        return sb.toString();
    }


    /**
     * ColorArea represents a connected area of cells that have the same color.
     */
    private class ColorArea implements Comparable<ColorArea> {
        private final int color;
        private final Set<Integer> members = new TreeSet<Integer>(); // sorted set - used by compareTo!
        private final Set<ColorArea> neighbors = new TreeSet<ColorArea>();

        private ColorArea(final int color) {
            this.color = color;
        }

        private boolean addMember(final int index, final int color) {
            if (this.color != color) {
                return false; // wrong (different) color
            }
            final Integer ind = Integer.valueOf(index);
            if (this.members.isEmpty()) {
                return this.members.add(ind); // added
            }
            // is the new number a neighbor of an existing member?
            for (final Integer mem : this.members) {
                final int member = mem.intValue();
                if ((((index == member - 1) || (index == member + 1))
                        && (index / Board.this.width == member / Board.this.width)) ||
                    (index == member - Board.this.width) ||
                    (index == member + Board.this.width)) {
                    return this.members.add(ind); // added
                }
            }
            return false; // not added
        }

        private boolean addNeighbor(final ColorArea other) {
            if (this.color == other.color) {
                return false; // wrong (same) color
            }
            for (final Integer thisMem : this.members) {
                final int thisMember = thisMem.intValue();
                for (final Integer otherMem : other.members) {
                    final int otherMember = otherMem.intValue();
                    if ((((thisMember == otherMember - 1) || (thisMember == otherMember + 1))
                            && (thisMember / Board.this.width == otherMember / Board.this.width)) ||
                        (thisMember == otherMember - Board.this.width) ||
                        (thisMember == otherMember + Board.this.width)) {
                        return this.neighbors.add(other); // added
                    }
                }
            }
            return false; // not added
        }

        @Override
        public String toString() {
            return this.color + "_" + this.members.toString() + "-" + this.neighbors.size();
        }

        // sorted by color, number of members, first (smallest) member
        @Override
        public int compareTo(final ColorArea other) {
            if (this.color < other.color) {
                return -1;
            } else if (this.color > other.color) {
                return 1;
            } else { // equal color
                if (this.members.size() < other.members.size()) {
                    return -1;
                } else if (this.members.size() > other.members.size()) {
                    return 1;
                } else { // equal number of members
                    if (this.members.isEmpty()) {
                        return 0; // no members
                    } else {
                        final int thisMember = this.members.iterator().next().intValue();
                        final int otherMember = other.members.iterator().next().intValue();
                        if (thisMember < otherMember) {
                            return -1;
                        } else if (thisMember > otherMember) {
                            return 1;
                        } else {
                            return 0; // equal first member
                        }
                    }
                }
            }
        }
    }
}
