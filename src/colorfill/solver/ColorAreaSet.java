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

import java.util.Arrays;
import java.util.Iterator;

import colorfill.model.Board;
import colorfill.model.ColorArea;

/**
 * this class is a bespoke implementation of a Set of ColorArea. 
 */
public class ColorAreaSet implements Iterable<ColorArea> {

    private final Board board;
    private final int[] array;
    private int size;

    /**
     * the constructor
     */
    public ColorAreaSet(final Board board) {
        this.board = board;
        this.array = new int[(board.getSizeColorAreas8() + 3) >> 2];
        this.size = 0;
    }

    /**
     * copy constructor
     * @param other
     */
    public ColorAreaSet(final ColorAreaSet other) {
        this.board = other.board;
        this.array = other.array.clone();
        this.size = other.size;
    }

    public Board getBoard() {
        return this.board;
    }

    /**
     * copy the contents of the other set into this set
     */
    public void copyFrom(final ColorAreaSet other) {
        System.arraycopy(other.array, 0, this.array, 0, this.array.length);
        this.size = other.size;
    }

    /**
     * remove all ColorAreas from this set
     */
    public void clear() {
        Arrays.fill(this.array, 0);
        this.size = 0;
    }

    /**
     * get the reference of the internal array
     */
    public int[] getArray() {
        return this.array;
    }

    /**
     * add the ColorArea to this set
     */
    public void add(final ColorArea ca) {
        final int id = ca.getId();
        final int i = id >> 5;
        final int a = this.array[i];
        final int b = a | 1 << id;  // implicit shift distance (id & 0x1f)
        this.array[i] = b;
        this.size += (a == b ? 0 : 1);
    }

    /**
     * return true if the ColorArea is in this set
     */
    public boolean contains(final ColorArea ca) {
        final int id = ca.getId();
        final int bit = this.array[id >> 5] & (1 << id);  // implicit shift distance (id & 0x1f)
        return 0 != bit;
    }

    /**
     * return true if this set contains all ColorAreas in the other set
     */
    public boolean containsAll(final ColorAreaSet other) {
        for (int i = 0;  i < this.array.length;  ++i) {
            final int thisInt = this.array[i];
            final int otherInt = other.array[i];
            if ((thisInt & otherInt) != otherInt) {
                return false;
            }
        }
        return true;
    }

    /**
     * return true if this set contains all ColorAreas in the array
     */
    public boolean containsAll(final ColorArea[] others) {
        for (final ColorArea other : others) {
            if (false == this.contains(other)) {
                return false;
            }
        }
        return true;
    }

    /**
     * return true if this set contains none of the ColorAreas in the array
     */
    public boolean containsNone(final ColorArea[] others) {
        for (final ColorArea other : others) {
            if (true == this.contains(other)) {
                return false;
            }
        }
        return true;
    }

    /**
     * return the number of ColorAreas in this set
     */
    public int size() {
        return this.size;
    }

    /**
     * return true is this set is empty
     */
    public boolean isEmpty() {
        return 0 == this.size;
    }

    /**
     * add all ColorAreas in the other set to this set
     */
    public void addAll(final ColorAreaSet other) {
        int sz = 0;
        for (int i = 0;  i < this.array.length;  ++i) {
            final int a = (this.array[i] |= other.array[i]);
            sz += Integer.bitCount(a);  // hopefully an intrinsic function using instruction POPCNT
        }
        this.size = sz;
    }

    /**
     * remove all ColorAreas in the other set from this set
     */
    public void removeAll(final ColorAreaSet other) {
        int sz = 0;
        for (int i = 0;  i < this.array.length;  ++i) {
            final int a = (this.array[i] &= ~(other.array[i]));
            sz += Integer.bitCount(a);  // hopefully an intrinsic function using instruction POPCNT
        }
        this.size = sz;
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<ColorArea> iterator() {
        return new ColorAreaSetIterator();
    }

    private class ColorAreaSetIterator implements Iterator<ColorArea> {
        private int count = 0;
        private final int countLimit = ColorAreaSet.this.size();
        private int intIdx = 0;
        private int buf = ColorAreaSet.this.array[0];

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return this.count < this.countLimit;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public ColorArea next() {
            // note: if (false == this.hasNext())
            // then it throws ArrayIndexOutOfBoundsException
            // instead of NoSuchElementException
            while (0 == this.buf) {
                this.buf = ColorAreaSet.this.array[++this.intIdx];
            }
            final int l1b = this.buf & -this.buf; // Integer.lowestOneBit(this.buf)
            ++this.count;
            final int clz = Integer.numberOfLeadingZeros(l1b); // hopefully an intrinsic function using instruction BSR / LZCNT / CLZ
            final int caId = (this.intIdx << 5) + 31 - clz;
            this.buf ^= l1b;
            return ColorAreaSet.this.board.getColorArea4Id(caId);
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
