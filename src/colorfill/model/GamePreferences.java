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

package colorfill.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

public class GamePreferences {

    // keys in the persistent preferences store
    private static final String PREFS_NODE_NAME = "smack42ColorFill";
    private static final String PREFS_WIDTH     = "width";
    private static final String PREFS_HEIGHT    = "height";
    private static final String PREFS_NUMCOLORS = "numColors";
    private static final String PREFS_STARTPOS  = "startPos";
    private static final String PREFS_GRIDLINES = "gridLines";
    private static final String PREFS_LAFNAME   = "lafName";
    private static final String PREFS_BOARD_COLOR_NUMBERS = "boardColorNumbers";
    private static final String PREFS_HIGHLIGHT_COLOR = "highlightColor";
    private static final String PREFS_CELLSIZE  = "cellSize";
    private static final String PREFS_COLSCHEME = "colorScheme";
    private static final String PREFS_RUNSOLVER = "runSolver";
    private static final String PREFS_DONT_RUN_STRATEGIES = "dontRunSolverStrategies";
    private static final String PREFS_GAMESTATE_BOARD    = "gameStateBoard";
    private static final String PREFS_GAMESTATE_SOLUTION = "gameStateSolution";
    private static final char   PREFS_GAMESTATE_SEPARATOR = ';';

    private static final Preferences PREFS = Preferences.userRoot().node(PREFS_NODE_NAME);

    // hard-coded default values
    public static final int DEFAULT_BOARD_WIDTH  = 14;
    public static final int DEFAULT_BOARD_HEIGHT = 14;
    public static final int DEFAULT_BOARD_NUM_COLORS = 6;
    public static final StartPositionEnum DEFAULT_BOARD_STARTPOS = StartPositionEnum.TOP_LEFT;
    public static final GridLinesEnum DEFAULT_UI_GRIDLINES = GridLinesEnum.NONE;
    public static final String DEFAULT_UI_LAFNAME = "FlatLaf Light";
    public static final BoardColorNumbersEnum DEFAULT_UI_BOARD_COLOR_NUMBERS = BoardColorNumbersEnum.NONE;
    public static final HighlightColorEnum DEFAULT_UI_HIGHLIGHT_COLOR = HighlightColorEnum.WHITE;
    public static final int DEFAULT_UI_CELLSIZE = 36;
    public static final int DEFAULT_UI_COLSCHEME = 0;
    public static final int DEFAULT_UI_RUNSOLVER = 1;
    public static final String DEFAULT_UI_DONT_RUN_STRATEGIES = GameState.getSolverNames()[GameState.getSolverNames().length - 1]; // DfsExhaustiveStrategy
    private static final Color[][] DEFAULT_UI_COLORS = {
        { // Flood-It scheme
            new Color(0xDC4A20), // Color.RED
            new Color(0x7E9D1E), // Color.GREEN
            new Color(0x605CA8), // Color.BLUE
            new Color(0xF3F61D), // Color.YELLOW
            new Color(0x46B1E2), // Color.CYAN
            new Color(0xED70A1)  // Color.MAGENTA
        },
        { // Color Flood (Android) scheme 1
            new Color(0x6261A8),
            new Color(0x6AAECC),
            new Color(0x5EDD67),
            new Color(0xF66A61),
            new Color(0xF6BF61),
            new Color(0xF0F461)
        },
        { // Color Flood (Android) scheme 2
            new Color(0x707B87),
            new Color(0x6AD5CD),
            new Color(0xD0F67D),
            new Color(0xFF8383),
            new Color(0xB35F8D),
            new Color(0xFFC27A)
        },
        { // Color Flood (Android) scheme 3
            new Color(0x875E7F),
            new Color(0xDF759A),
            new Color(0xF1AB58),
            new Color(0xFADC57),
            new Color(0xA8BB61),
            new Color(0x5E9496)
        },
        { // Color Flood (Android) scheme 4
            new Color(0x24ADBB),
            new Color(0x7F6458),
            new Color(0xD3505A),
            new Color(0xEE8E5C),
            new Color(0xF0D16A),
            new Color(0x7BD15F)
        },
        { // Color Flood (Android) scheme 5
            new Color(0x6D5D52),
            new Color(0xF88B44),
            new Color(0xA1CC55),
            new Color(0xF3F1B2),
            new Color(0x76C3A8),
            new Color(0xEB6476)
        },
        { // Color Flood (Android) scheme 6
            new Color(0xDF5162),
            new Color(0x38322F),
            new Color(0x247E86),
            new Color(0x1BC4C1),
            new Color(0xFCF8C9),
            new Color(0xD19C2D)
        },
        { // Grayscale
            new Color(0x181818),
            new Color(0xF0F0F0),
            new Color(0x909090),
            new Color(0x585858),
            new Color(0xC8C8C8),
            new Color(0x383838)
        }
    };

    private int width;
    private int height;
    private int numColors;
    private int startPos;
    private int uiColors;
    private int gridLines;
    private int uiBoardColorNumbers;
    private int highlightColor;
    private int cellSize;
    private int runSolver;
    private final List<String> dontRunSolverStrategies;
    private String lafName;

    public GamePreferences() {
        this.width = DEFAULT_BOARD_WIDTH;
        this.height = DEFAULT_BOARD_HEIGHT;
        this.numColors = DEFAULT_BOARD_NUM_COLORS;
        this.startPos = DEFAULT_BOARD_STARTPOS.intValue;
        this.uiColors = DEFAULT_UI_COLSCHEME;
        this.gridLines = DEFAULT_UI_GRIDLINES.intValue;
        this.cellSize = DEFAULT_UI_CELLSIZE;
        this.uiBoardColorNumbers = DEFAULT_UI_BOARD_COLOR_NUMBERS.intValue;
        this.highlightColor = DEFAULT_UI_HIGHLIGHT_COLOR.intValue;
        this.lafName = DEFAULT_UI_LAFNAME;
        this.runSolver = DEFAULT_UI_RUNSOLVER;
        this.dontRunSolverStrategies = new ArrayList<String>();
        this.dontRunSolverStrategies.add(DEFAULT_UI_DONT_RUN_STRATEGIES);
        this.loadPrefs();
        this.savePrefs();
    }

    public int getWidth() {
        return this.width;
    }
    public boolean setWidth(final int width) {
        if ((this.width != width) && (width >= 2) && (width <= 100)) { // validation
            this.width = width;
            return true; // new value has been set
        }
        return false; // value not changed
    }

    public int getHeight() {
        return this.height;
    }
    public boolean setHeight(final int height) {
        if ((this.height != height) && (height >= 2) && (height <= 100)) { // validation
            this.height = height;
            return true; // new value has been set
        }
        return false; // value not changed
    }

    public int getNumColors() {
        return this.numColors;
    }
    public boolean setNumColors(final int numColors) {
        if ((this.numColors != numColors) && (numColors >= 2) && (numColors <= 6)) { // validation
            this.numColors = numColors;
            return true; // new value has been set
        }
        return false; // value not changed
    }

    public int getStartPos() {
        return this.startPos;
    }
    public int getStartPos(int width, int height) {
        return StartPositionEnum.calculatePosition(this.startPos, width, height);
    }
    public StartPositionEnum getStartPosEnum() {
        return StartPositionEnum.valueOf(this.startPos); // may be null
    }
    public boolean setStartPos(int startPos) {
        if (null != StartPositionEnum.valueOf(startPos)) {
            this.startPos = startPos;
            return true; // new value has been set
        }
        return false; // value not changed
    }
    public boolean setStartPos(StartPositionEnum spe) {
        if (this.startPos != spe.intValue) {
            this.startPos = spe.intValue;
            return true; // new value has been set
        }
        return false; // value not changed
    }

    public Color[][] getAllUiColors() {
        return DEFAULT_UI_COLORS;
    }
    public Color[] getUiColors() {
        return DEFAULT_UI_COLORS[this.uiColors];
    }
    public int getUiColorsNumber() {
        return this.uiColors;
    }
    public void setUiColorsNumber(final int num) {
        if ((num >= 0) && (num < DEFAULT_UI_COLORS.length)) {
            this.uiColors = num;
        }
    }

    public int getGridLines() {
        return this.gridLines;
    }
    public GridLinesEnum getGridLinesEnum() {
        return GridLinesEnum.valueOf(this.gridLines);
    }
    public void setGridLines(final int gridLines) {
        if (null != GridLinesEnum.valueOf(gridLines)) {
            this.gridLines = gridLines;
        }
    }
    public void setGridLines(final GridLinesEnum gle) {
        this.gridLines = gle.intValue;
    }

    public int getBoardColorNumbers() {
        return this.uiBoardColorNumbers;
    }
    public BoardColorNumbersEnum getBoardColorNumbersEnum() {
        return BoardColorNumbersEnum.valueOf(this.uiBoardColorNumbers);
    }
    public void setBoardColorNumbers(final int uiBoardColorNumbers) {
        if (null != BoardColorNumbersEnum.valueOf(uiBoardColorNumbers)) {
            this.uiBoardColorNumbers = uiBoardColorNumbers;
        }
    }
    public void setBoardColorNumbers(final BoardColorNumbersEnum bcne) {
        this.uiBoardColorNumbers = bcne.intValue;
    }

    public int getHighlightColor() {
        return this.highlightColor;
    }
    public HighlightColorEnum getHighlightColorEnum() {
        return HighlightColorEnum.valueOf(this.highlightColor);
    }
    public void setHighlightColor(final int highlightColor) {
        if (null != HighlightColorEnum.valueOf(highlightColor)) {
            this.highlightColor = highlightColor;
        }
    }
    public void setHighlightColor(final HighlightColorEnum hce) {
        this.highlightColor = hce.intValue;
    }

    public int getCellSize() {
        return this.cellSize;
    }
    public boolean setCellSize(final int cellSize) {
        if ((this.cellSize != cellSize) && (cellSize >= 3) && (cellSize <= 300)) { // validation
            this.cellSize = cellSize;
            return true; // new value has been set
        }
        return false; // value not changed
    }

    public void setRunSolver(final boolean runSolver) {
        this.runSolver = (runSolver ? 1 : 0);
    }
    public boolean isRunSolver() {
        return 0 != this.runSolver;
    }

    public void setRunSolverStrategy(final String strategy, final boolean runSolver) {
        if (runSolver) {
            this.dontRunSolverStrategies.remove(strategy);
        } else {
            this.dontRunSolverStrategies.add(strategy);
        }
    }
    public List<String> getDontRunSolverStrategies() {
        return Collections.unmodifiableList(this.dontRunSolverStrategies);
    }

    public boolean setLafName(final String lafName) {
        if ((lafName != null) && ((this.lafName == null) || !this.lafName.contentEquals(lafName))) {
            this.lafName = lafName;
            return true;
        }
        return false;
    }
    public String getLafName() {
        return this.lafName;
    }

    private void loadPrefs() {
        this.setWidth            (PREFS.getInt(PREFS_WIDTH,              DEFAULT_BOARD_WIDTH));
        this.setHeight           (PREFS.getInt(PREFS_HEIGHT,             DEFAULT_BOARD_HEIGHT));
        this.setNumColors        (PREFS.getInt(PREFS_NUMCOLORS,          DEFAULT_BOARD_NUM_COLORS));
        this.setStartPos         (PREFS.getInt(PREFS_STARTPOS,           DEFAULT_BOARD_STARTPOS.intValue));
        this.setUiColorsNumber   (PREFS.getInt(PREFS_COLSCHEME,          DEFAULT_UI_COLSCHEME));
        this.setGridLines        (PREFS.getInt(PREFS_GRIDLINES,          DEFAULT_UI_GRIDLINES.intValue));
        this.setBoardColorNumbers(PREFS.getInt(PREFS_BOARD_COLOR_NUMBERS,DEFAULT_UI_BOARD_COLOR_NUMBERS.intValue));
        this.setHighlightColor   (PREFS.getInt(PREFS_HIGHLIGHT_COLOR,    DEFAULT_UI_HIGHLIGHT_COLOR.intValue));
        this.setCellSize         (PREFS.getInt(PREFS_CELLSIZE,           DEFAULT_UI_CELLSIZE));
        this.runSolver          = PREFS.getInt(PREFS_RUNSOLVER,          DEFAULT_UI_RUNSOLVER);
        final StringTokenizer tokDontRun = new StringTokenizer(PREFS.get(PREFS_DONT_RUN_STRATEGIES, DEFAULT_UI_DONT_RUN_STRATEGIES), ",");
        this.dontRunSolverStrategies.clear();
        while (tokDontRun.hasMoreTokens()) {
            this.dontRunSolverStrategies.add(tokDontRun.nextToken());
        }
        this.lafName            = PREFS.get(PREFS_LAFNAME, DEFAULT_UI_LAFNAME);
    }

    public void savePrefs() {
        PREFS.putInt(PREFS_WIDTH,              this.getWidth());
        PREFS.putInt(PREFS_HEIGHT,             this.getHeight());
        PREFS.putInt(PREFS_NUMCOLORS,          this.getNumColors());
        PREFS.putInt(PREFS_STARTPOS,           this.getStartPos());
        PREFS.putInt(PREFS_COLSCHEME,          this.getUiColorsNumber());
        PREFS.putInt(PREFS_GRIDLINES,          this.getGridLines());
        PREFS.putInt(PREFS_BOARD_COLOR_NUMBERS,this.getBoardColorNumbers());
        PREFS.putInt(PREFS_HIGHLIGHT_COLOR,    this.getHighlightColor());
        PREFS.putInt(PREFS_CELLSIZE,           this.getCellSize());
        PREFS.putInt(PREFS_RUNSOLVER,          this.runSolver);
        final StringBuffer sbDontRun = new StringBuffer();
        for (final String dontRun : this.dontRunSolverStrategies) {
            sbDontRun.append(',').append(dontRun);
        }
        PREFS.put(PREFS_DONT_RUN_STRATEGIES, sbDontRun.substring(Math.min(sbDontRun.length(), 1)));
        PREFS.put(PREFS_LAFNAME, this.lafName);
    }

    public static void saveBoard(final Board board) {
        final StringBuilder sb = new StringBuilder()
            .append(board.getWidth())       .append(PREFS_GAMESTATE_SEPARATOR)
            .append(board.getHeight())      .append(PREFS_GAMESTATE_SEPARATOR)
            .append(board.getNumColors())   .append(PREFS_GAMESTATE_SEPARATOR)
            .append(board.getStartPos())    .append(PREFS_GAMESTATE_SEPARATOR)
            .append(board.toStringCells());
        PREFS.put(PREFS_GAMESTATE_BOARD, sb.toString());
    }

    public static Board loadBoard() {
        Board result = null;
        try {
            final String str = PREFS.get(PREFS_GAMESTATE_BOARD, "");
            final String[] strSplit = str.split(String.valueOf(PREFS_GAMESTATE_SEPARATOR));
            final int width = Integer.parseInt(strSplit[0]);
            final int height = Integer.parseInt(strSplit[1]);
            final int numColors = Integer.parseInt(strSplit[2]);
            final int startPos = Integer.parseInt(strSplit[3]);
            result = new Board(width, height, numColors, strSplit[4], startPos);
        } catch (Exception e) {
            System.out.println("loadBoard() failed: " + e.toString());
        }
        return result;
    }

    public static void saveSolution(final GameProgress progress) {
        final StringBuilder sb = new StringBuilder()
            .append(progress.getCurrentStep())  .append(PREFS_GAMESTATE_SEPARATOR)
            .append(progress.toStringSteps());
        PREFS.put(PREFS_GAMESTATE_SOLUTION, sb.toString());
    }

    public static GameProgress loadSolution(final Board board, final int startPos) {
        GameProgress result = null;
        try {
            final String str = PREFS.get(PREFS_GAMESTATE_SOLUTION, "");
            final String[] strSplit = str.split(String.valueOf(PREFS_GAMESTATE_SEPARATOR));
            final int step = Integer.parseInt(strSplit[0]);
            result = new GameProgress(board, startPos, step, strSplit[1]);
        } catch (Exception e) {
            System.out.println("loadSolution() failed: " + e.toString());
        }
        return result;
    }
}
