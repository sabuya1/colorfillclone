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

import colorfill.model.Board;
import colorfill.model.ColorArea;

/**
 * this class contains a collection for ColorAreas, grouped by their colors.
 * provides some helper functions for the search strategies.
 */
public class ColorAreaGroup {

    private final ColorAreaSet[] theArray;
    
    private int colorsNotEmptyBits;

    /**
     * the standard constructor
     */
    public ColorAreaGroup(final Board board) {
        this.theArray = new ColorAreaSet[board.getNumColors()];
        for (int color = 0;  color < this.theArray.length;  ++color) {
            this.theArray[color] = new ColorAreaSet(board);
        }
        this.colorsNotEmptyBits = 0;
    }

    /**
     * copy constructor
     * @param other
     */
    public ColorAreaGroup(final ColorAreaGroup other) {
        this.theArray = other.theArray.clone();
        for (int color = 0;  color < this.theArray.length;  ++color) {
            this.theArray[color] = new ColorAreaSet(this.theArray[color]);
        }
        this.colorsNotEmptyBits = other.colorsNotEmptyBits;
    }

    /**
     * copy the contents of the other color area group into this one,
     * except for the specified color which will be empty.
     */
    public void copyFrom(final ColorAreaGroup other, final int exceptColor) {
        for (int color = 0;  color < this.theArray.length;  ++color) {
            if (color != exceptColor) {
                this.theArray[color].copyFrom(other.theArray[color]);
            } else {
                this.theArray[color].clear();
            }
        }
        final int mask = ~(1 << exceptColor);
        this.colorsNotEmptyBits = other.colorsNotEmptyBits & mask;
    }

    /**
     * add all color areas that are not members of the "exclude" set.
     * @param addColorAreas the color areas to be added
     * @param excludeColorAreas color areas that are also members of this set will not be added
     */
    public void addAll(final ColorArea[] addColorAreas, final ColorAreaSet excludeColorAreas) {
        for (final ColorArea ca : addColorAreas) {
            if (false == excludeColorAreas.contains(ca)) {
                final int color = ca.getColor();
                this.theArray[color].add(ca);
                this.colorsNotEmptyBits |= 1 << color;
            }
        }
    }

    /**
     * add all color areas into the specified color.
     * warning: does not check or update the color areas for consistency.
     * @param addColorAreas the color areas to be added
     * @param color the color
     */
    public void addAllColor(final ColorAreaSet addColorAreas, final byte color) {
        assert addColorAreas.size() > 0;
        this.theArray[color].addAll(addColorAreas);
        this.colorsNotEmptyBits |= 1 << color;
    }

    /**
     * remove all color areas from the specified color.
     * warning: does not check or update the color areas for consistency.
     * @param removeColorAreas the color areas to be removed
     * @param color the color
     */
    public void removeAllColor(final ColorAreaSet removeColorAreas, final byte color) {
        final int sz = this.theArray[color].removeAll(removeColorAreas);
        assert sz >= 0;
        // conditionally set/clear a bit without branching
        final int mask = ~(1 << color);
        final int newBit = ((-sz) >>> 31) << color;
        this.colorsNotEmptyBits = this.colorsNotEmptyBits & mask | newBit;
    }

    /**
     * return "true" if there are no color areas stored here.
     * @return
     */
    public boolean isEmpty() {
        return 0 == this.colorsNotEmptyBits;
    }

    /**
     * count the number of colors that have at least one color area.
     * @return number of occupied colors
     */
    public int countColorsNotEmpty() {
        return Integer.bitCount(this.colorsNotEmptyBits);  // hopefully an intrinsic function using instruction POPCNT
    }

    /**
     * get the colors that have at least one color area.
     * @return bitfield of occupied colors
     */
    public int getColorsNotEmptyBits() {
        return this.colorsNotEmptyBits;
    }

    /**
     * get the colors that have at least one color area.
     * @return list of occupied colors, not expected to be empty
     */
    public int getColorsNotEmpty() {
        int result = 0;
        for (byte color = 0;  color < this.theArray.length;  ++color) {
            if (false == this.theArray[color].isEmpty()) {
                result |= 1 << color;
            }
        }
        return result;
    }

    /**
     * get the colors that are situated at the specified depth.
     * @return list of colors at depth, may be empty
     */
    public int getColorsDepth(final int depth) {
        int result = 0;
        for (final ColorAreaSet caSet : this.theArray) {
            for (final ColorArea ca : caSet) {
                if (ca.getDepth() == depth) {
                    result |= 1 << ca.getColor();
                    break; // for (ca)
                }
            }
        }
        return result;
    }

    /**
     * get the colors that are situated at the specified depth or lower,
     * but only the colors at the maximum depth level.
     * @return list of colors at depth or lower, not expected to be empty
     */
    public int getColorsDepthOrLower(final int depth) {
        int result = 0;
        int depthMax = -1;
        for (final ColorAreaSet caSet : this.theArray) {
            byte color = Byte.MIN_VALUE;
            int depthColor = -2;
            for (final ColorArea ca : caSet) {
                final int d = ca.getDepth();
                if (d == depth) {
                    color = ca.getColor();
                    depthColor = d;
                    break; // for (ca)
                } else if ((d > depthColor) && (d < depth)) {
                    color = ca.getColor();
                    depthColor = d;
                }
            }
            if (depthMax < depthColor) {
                depthMax = depthColor;
                result = 1 << color;
            } else if (depthMax == depthColor) {
                result |= 1 << color;
            }
        }
        return result;
    }

    /**
     * get the colors that are contained completely in other.
     * @param other
     * @return list of completed colors or 0 (zero)
     */
    public int getColorsCompleted(final ColorAreaGroup other) {
        for (byte color = 0;  color < this.theArray.length;  ++color) {
            final ColorAreaSet thisSet = this.theArray[color];
            if (thisSet.size() > 0) {
                final ColorAreaSet otherSet = other.theArray[color];
                if ((thisSet.size() == otherSet.size())&& (thisSet.containsAll(otherSet))) {
                    return 1 << color;
                }
            }
        }
        return 0;
    }

    /**
     * get the colors that have the maximum number of member cells.
     * the color areas are counted only if their neighbors are not contained in excludeNeighbors.
     * @param excludeNeighbors exclude color areas if their neighbors are contained here
     * @return list of colors, not expected to be empty
     */
    public int getColorsMaxMembers(final ColorAreaSet excludeNeighbors) {
        int result = 0;
        int maxCount = 1; // return empty collection if all colors are empty. not expected!
        for (byte color = 0;  color < this.theArray.length;  ++color) {
            int count = 0;
            for (final ColorArea ca : this.theArray[color]) {
                if (false == excludeNeighbors.containsAll(ca.getNeighborsArray())) {
                    count += ca.getMemberSize();
                }
            }
            if (maxCount < count) {
                maxCount = count;
                result = 1 << color;
            } else if (maxCount == count) {
                result |= 1 << color;
            }
        }
        return result;
    }

    /**
     * get the colors that have the maximum number of new neighbor cells,
     * with the neighbors and all of their neighbors are not contained in excludeNeighbors.
     * @param excludeNeighbors exclude color areas if their neighbors or their next neighbors are contained here
     * @return list of colors, not expected to be empty
     */
    public int getColorsMaxNextNeighbors(final ColorAreaSet excludeNeighbors) {
        int result = 0;
        int maxCount = -1; // include colors that have zero or more next new neighbors
        for (byte color = 0;  color < this.theArray.length;  ++color) {
            int count = 0;
            for (final ColorArea ca : this.theArray[color]) {
                for (final ColorArea caNext : ca.getNeighborsArray()) {
                    if ((false == excludeNeighbors.contains(caNext))
                            && excludeNeighbors.containsNone(caNext.getNeighborsArray())) {
                        count += caNext.getMemberSize();
                    }
                }
            }
            if (maxCount < count) {
                maxCount = count;
                result = 1 << color;
            } else if (maxCount == count) {
                result |= 1 << color;
            }
        }
        return result;
    }

    /**
     * return the areas of this color.
     * @param color
     * @return the areas
     */
    public ColorAreaSet getColor(final byte color) {
        return this.theArray[color];
    }

    /**
     * remove the areas of this color.
     * @param color
     */
    public void clearColor(final byte color) {
        this.theArray[color].clear();
        final int mask = ~(1 << color);
        this.colorsNotEmptyBits &= mask;
    }
}
