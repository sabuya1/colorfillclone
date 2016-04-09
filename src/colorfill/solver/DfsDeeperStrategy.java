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

/**
 * this strategy results in an incomplete search.
 * it chooses the colors in two steps:
 * <p>
 * 1) colors that can be completely flooded in the next step.
 * (these are always optimal moves!?)
 * <p>
 * 2 a) if 1) gives no result then the colors that are situated at depth + 1
 * or lower. (hence the name "deeper")
 */
public class DfsDeeperStrategy implements DfsStrategy {

    private final int maxDepth;

    public DfsDeeperStrategy(final Board board, final int startPos) {
        this.maxDepth = board.getDepth(startPos);
    }

    @Override
    public String getInfo() {
        return null; // no info available
    }

    @Override
    public void setPreviousNumSteps(int previousNumSteps) {
        // not used here
    }

    @Override
    public byte[] selectColors(final int depth,
            final ColorAreaSet flooded,
            final ColorAreaGroup notFlooded,
            final ColorAreaGroup neighbors) {
        byte[] result = neighbors.getColorsCompleted(notFlooded);
        if (null == result) {
            result = neighbors.getColorsDepthOrLower(Math.min(depth + 1, this.maxDepth));
        }
        return result;
    }
}
