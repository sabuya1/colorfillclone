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
 * it chooses the colors in three steps:
 * <p>
 * 1) colors that can be completely flooded in the next step.
 * (these are always optimal moves!?)
 * <p>
 * 2 a) if 1) gives no result and current depth is less than maxDepth
 * then the colors that are situated at depth + 1.
 * (hence the name "deep")
 * <p>
 * 2 b) if 1) gives no result and current depth is equal or larger than maxDepth
 * then all available colors.
 * (complete coverage of the outer branches of the search tree)
 */
public class DfsDeepStrategy implements DfsStrategy {

    private final int maxDepth;

    public DfsDeepStrategy(final Board board, final int startPos) {
        this.maxDepth = board.getDepth(startPos);
    }

    @Override
    public byte[] selectColors(final int depth,
            final byte thisColor,
            final byte[] solution,
            final ColorAreaSet flooded,
            final ColorAreaGroup notFlooded,
            final ColorAreaGroup neighbors) {
        byte[] result = neighbors.getColorsCompleted(notFlooded);
        if (null == result) {
            if (depth < this.maxDepth) {
                result = neighbors.getColorsDepth(depth + 1);
            } else  {
                result = neighbors.getColorsNotEmpty();
            }
        }
        return result;
    }
}
