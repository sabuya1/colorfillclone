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

import it.unimi.dsi.fastutil.bytes.ByteList;

/**
 * this strategy results in an incomplete search.
 * it chooses the colors in two steps:
 * <p>
 * 1) colors that can be completely flooded in the next step.
 * (these are always optimal moves!?)
 * <p>
 * 2) if 1) gives no result then the colors that contain
 * the maximum number of member cells. (hence the name "greedy")
 */
public class GreedyDfsStrategy implements DfsStrategy {

    @Override
    public ByteList selectColors(final int depth,
            final byte thisColor,
            final byte[] solution,
            final ColorAreaSet flooded,
            final ColorAreaGroup notFlooded,
            final ColorAreaGroup neighbors) {
        ByteList result = neighbors.getColorsCompleted(notFlooded);
        if (result.isEmpty()) {
            result = neighbors.getColorsMaxMembers(flooded);
        }
        return result;
    }
}
