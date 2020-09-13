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

package colorfill.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import colorfill.model.BoardColorNumbersEnum;
import colorfill.model.GridLinesEnum;
import colorfill.model.HighlightColorEnum;
import colorfill.model.StartPositionEnum;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.Tag;

public class PreferencesDialog extends JDialog {

    private static final long serialVersionUID = 5636063419915325085L;

    private static final ResourceBundle L10N = ResourceBundle.getBundle("colorfill-ui");  //L10N = Localization

    private final PreferencesController controller;
    private final MainWindow mainWindow;
    private final Color[][] allUiColors;

    private final JSpinner jspinWidth = new JSpinner();
    private final JSpinner jspinHeight = new JSpinner();
    private final JSpinner jspinNumColors = new JSpinner();
    private final JSpinner jspinCellSize = new JSpinner();
    private final JComboBox<StartPosItem> jcomboStartPos = new JComboBox<>();
    private final JButton buttonOk = new JButton();
    private final JButton buttonCancel = new JButton();
    private final JButton buttonDefaults = new JButton();
    private final JComboBox<Integer> jcomboColorSchemes = new JComboBox<>();
    private final JComboBox<GridLinesItem> jcomboGridLines = new JComboBox<>();
    private final JComboBox<BoardColorNumbersItem> jcomboBoardColorNumbers = new JComboBox<>();
    private final JComboBox<HighlightColorItem> jcomboHighlightColor = new JComboBox<>();
    private final JComboBox<String> jcomboLookAndFeel = new JComboBox<>();

    private boolean closedByOkButton = false;


    /**
     * constructor
     * @param controller
     * @param mainWindow
     */
    protected PreferencesDialog(final PreferencesController controller, final MainWindow mainWindow,
            final String progname, final String version, final String author, final Color[][] allUiColors) {
        super(mainWindow, true); // modal
        this.controller = controller;
        this.mainWindow = mainWindow;
        this.allUiColors = allUiColors;
        this.setTitle(L10N.getString("pref.Title.txt"));
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        final JPanel panel = new JPanel();
        final DesignGridLayout layout = new DesignGridLayout(panel);
        layout.withoutConsistentWidthAcrossNonGridRows();
        layout.row().left().addMulti(new JLabel(progname + " " + version));
        layout.row().left().addMulti(new JLabel(author));
        layout.row().left().fill().add(new JSeparator());
        layout.row().grid(new JLabel(L10N.getString("pref.lbl.Width.txt"))).addMulti(this.makeJspinWidth());
        layout.row().grid(new JLabel(L10N.getString("pref.lbl.Height.txt"))).addMulti(this.makeJspinHeight());
        layout.row().grid(new JLabel(L10N.getString("pref.lbl.NumColors.txt"))).addMulti(this.makeJspinNumColors());
        layout.row().grid(new JLabel(L10N.getString("pref.lbl.StartPos.txt"))).addMulti(this.makeJcomboStartPos());
        layout.row().left().fill().add(new JSeparator());
        layout.row().grid(new JLabel(L10N.getString("pref.lbl.LookAndFeel.txt"))).addMulti(this.makeJcomboLookAndFeel());
        layout.row().grid(new JLabel(L10N.getString("pref.lbl.GridLines.txt"))).addMulti(this.makeJcomboGridLines());
        layout.row().grid(new JLabel(L10N.getString("pref.lbl.BoardColorNumbers.txt"))).addMulti(this.makeJcomboBoardColorNumbers());
        layout.row().grid(new JLabel(L10N.getString("pref.lbl.HighlightColor.txt"))).addMulti(this.makeJcomboHighlightColor());
        layout.row().grid(new JLabel(L10N.getString("pref.lbl.ColorScheme.txt"))).addMulti(this.makeJcomboColorSchemes());
        layout.row().grid(new JLabel(L10N.getString("pref.lbl.CellSize.txt"))).addMulti(this.makeJspinCellSize());
        layout.row().left().fill().add(new JSeparator());
        layout.row().left().addMulti(this.makeButtonDefaults());
        layout.row().bar().add(this.makeButtonOk(), Tag.OK).add(this.makeButtonCancel(), Tag.CANCEL);

        this.add(panel);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Cancel or Close button: undo preview of color scheme
                if (false == PreferencesDialog.this.closedByOkButton) {
                    PreferencesDialog.this.controller.userPreviewUiColors(
                            PreferencesDialog.this.controller.getUiColorsNumber(),
                            PreferencesDialog.this.controller.getGridLines(),
                            PreferencesDialog.this.controller.getBoardColorNumbers(),
                            PreferencesDialog.this.controller.getNumColors(),
                            PreferencesDialog.this.controller.getHighlightColor());
                }
            }
        });
    }

    private JSpinner makeJspinWidth() {
        this.jspinWidth.setModel(new SpinnerNumberModel(2, 2, 100, 1)); // TODO preferences min/max "width"
        return this.jspinWidth;
    }

    private JSpinner makeJspinHeight() {
        this.jspinHeight.setModel(new SpinnerNumberModel(2, 2, 100, 1)); // TODO preferences min/max "height"
        return this.jspinHeight;
    }

    private JSpinner makeJspinNumColors() {
        this.jspinNumColors.setModel(new SpinnerNumberModel(2, 2, 6, 1)); // TODO preferences min/max "numColors"
        return this.jspinNumColors;
    }

    private JSpinner makeJspinCellSize() {
        this.jspinCellSize.setModel(new SpinnerNumberModel(3, 3, 300, 1)); // TODO preferences min/max "cellSize"
        return this.jspinCellSize;
    }

    private JComboBox<StartPosItem> makeJcomboStartPos() {
        for (final StartPositionEnum spe : StartPositionEnum.values()) {
            this.jcomboStartPos.addItem(new StartPosItem(spe));
        }
        return this.jcomboStartPos;
    }

    private JComboBox<String> makeJcomboLookAndFeel() {
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            this.jcomboLookAndFeel.addItem(info.getName());
        }
        return this.jcomboLookAndFeel;
    }

    private JComboBox<GridLinesItem> makeJcomboGridLines() {
        for (final GridLinesEnum gle : GridLinesEnum.values()) {
            this.jcomboGridLines.addItem(new GridLinesItem(gle));
        }
        this.jcomboGridLines.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PreferencesDialog.this.controller.userPreviewUiColors(
                        PreferencesDialog.this.getSelectedColorSchemeNumber(),
                        PreferencesDialog.this.getSelectedGridLinesEnum(),
                        PreferencesDialog.this.getSelectedBoardColorNumbersEnum(),
                        PreferencesDialog.this.getNumColors(),
                        PreferencesDialog.this.getHighlightColorEnum());
            }
        });
        return this.jcomboGridLines;
    }

    private JComboBox<BoardColorNumbersItem> makeJcomboBoardColorNumbers() {
        for (final BoardColorNumbersEnum bcne : BoardColorNumbersEnum.values()) {
            this.jcomboBoardColorNumbers.addItem(new BoardColorNumbersItem(bcne));
        }
        this.jcomboBoardColorNumbers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PreferencesDialog.this.controller.userPreviewUiColors(
                        PreferencesDialog.this.getSelectedColorSchemeNumber(),
                        PreferencesDialog.this.getSelectedGridLinesEnum(),
                        PreferencesDialog.this.getSelectedBoardColorNumbersEnum(),
                        PreferencesDialog.this.getNumColors(),
                        PreferencesDialog.this.getHighlightColorEnum());
            }
        });
        return this.jcomboBoardColorNumbers;
    }

    private JComboBox<HighlightColorItem> makeJcomboHighlightColor() {
        for (final HighlightColorEnum hce : HighlightColorEnum.values()) {
            this.jcomboHighlightColor.addItem(new HighlightColorItem(hce));
        }
        this.jcomboHighlightColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PreferencesDialog.this.controller.userPreviewUiColors(
                        PreferencesDialog.this.getSelectedColorSchemeNumber(),
                        PreferencesDialog.this.getSelectedGridLinesEnum(),
                        PreferencesDialog.this.getSelectedBoardColorNumbersEnum(),
                        PreferencesDialog.this.getNumColors(),
                        PreferencesDialog.this.getHighlightColorEnum());
            }
        });
        return this.jcomboHighlightColor;
    }

    private JComboBox<Integer> makeJcomboColorSchemes() {
        for (int i = 1;  i <= this.allUiColors.length;  ++i) {
            this.jcomboColorSchemes.addItem(Integer.valueOf(i));
        }
        this.jcomboColorSchemes.setRenderer(new ColorSchemeComboBoxRenderer());
        this.jcomboColorSchemes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PreferencesDialog.this.controller.userPreviewUiColors(
                        PreferencesDialog.this.getSelectedColorSchemeNumber(),
                        PreferencesDialog.this.getSelectedGridLinesEnum(),
                        PreferencesDialog.this.getSelectedBoardColorNumbersEnum(),
                        PreferencesDialog.this.getNumColors(),
                        PreferencesDialog.this.getHighlightColorEnum());
            }
        });
        return this.jcomboColorSchemes;
    }


    private class ColorSchemeComboBoxRenderer extends JLabel implements ListCellRenderer<Object> {
        private static final long serialVersionUID = -5760355417428060551L;
        private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(final JList<?> list, final Object value,
                final int index, final boolean isSelected, final boolean cellHasFocus) {
            final JLabel renderer = (JLabel)this.defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            final int uiColorsIndex = ((Integer)value).intValue() - 1;
            renderer.setIcon(new ColorSchemeIcon(uiColorsIndex));
            renderer.setHorizontalTextPosition(JLabel.LEADING);
            return renderer;
        }

        private class ColorSchemeIcon implements Icon {
            private final int uiColorsIndex;
            private static final int SIZE = 24; // TODO icon size adapting to JLabel text height
            public ColorSchemeIcon(final int uiColorsIndex) {
                this.uiColorsIndex = uiColorsIndex;
            }
            @Override
            public int getIconWidth() {
                return SIZE * PreferencesDialog.this.allUiColors[this.uiColorsIndex].length;
            }
            @Override
            public int getIconHeight() {
                return SIZE;
            }
            @Override
            public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
                int i = 0;
                for (final Color color : PreferencesDialog.this.allUiColors[this.uiColorsIndex]) {
                    g.setColor(color);
                    g.fillRect(x + i*SIZE, y, SIZE, SIZE);
                    ++i;
                }
            }
        }
    }


    private JButton makeButtonOk() {
        this.buttonOk.setText(L10N.getString("pref.btn.OK.txt"));
        this.buttonOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PreferencesDialog.this.controller.userPrefsOK(
                        ((Number)PreferencesDialog.this.jspinWidth.getValue()).intValue(),
                        ((Number)PreferencesDialog.this.jspinHeight.getValue()).intValue(),
                        PreferencesDialog.this.getNumColors(),
                        PreferencesDialog.this.jcomboStartPos.getItemAt(PreferencesDialog.this.jcomboStartPos.getSelectedIndex()).spe,
                        PreferencesDialog.this.getSelectedGridLinesEnum(),
                        PreferencesDialog.this.getSelectedBoardColorNumbersEnum(),
                        PreferencesDialog.this.getSelectedColorSchemeNumber(),
                        ((Number)PreferencesDialog.this.jspinCellSize.getValue()).intValue(),
                        PreferencesDialog.this.getHighlightColorEnum(),
                        PreferencesDialog.this.jcomboLookAndFeel.getItemAt(PreferencesDialog.this.jcomboLookAndFeel.getSelectedIndex()) );
                PreferencesDialog.this.closedByOkButton = true;
                PreferencesDialog.this.dispose();
            }
        });
        return this.buttonOk;
    }

    private JButton makeButtonCancel() {
        this.buttonCancel.setText(L10N.getString("pref.btn.Cancel.txt"));
        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PreferencesDialog.this.dispose();
            }
        };
        this.buttonCancel.addActionListener(actionListener);
        this.getRootPane().registerKeyboardAction(actionListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        return this.buttonCancel;
    }

    private JButton makeButtonDefaults() {
        this.buttonDefaults.setText(L10N.getString("pref.btn.Defaults.txt"));
        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PreferencesDialog.this.controller.userDefaults();
            }
        };
        this.buttonDefaults.addActionListener(actionListener);
        return this.buttonDefaults;
    }

    private int getSelectedColorSchemeNumber() {
        return this.jcomboColorSchemes.getSelectedIndex();
    }

    private GridLinesEnum getSelectedGridLinesEnum() {
        return this.jcomboGridLines.getItemAt(this.jcomboGridLines.getSelectedIndex()).gle;
    }

    private BoardColorNumbersEnum getSelectedBoardColorNumbersEnum() {
        return this.jcomboBoardColorNumbers.getItemAt(this.jcomboBoardColorNumbers.getSelectedIndex()).bcne;
    }

    private HighlightColorEnum getHighlightColorEnum() {
        return this.jcomboHighlightColor.getItemAt(this.jcomboHighlightColor.getSelectedIndex()).hce;
    }

    private int getNumColors() {
        return ((Number)PreferencesDialog.this.jspinNumColors.getValue()).intValue();
    }

    /**
     * show this modal dialog.
     */
    protected void showDialog() {
        if (SwingUtilities.isEventDispatchThread()) {                        showDialogInternal(); }
        else SwingUtilities.invokeLater(new Runnable() { public void run() { showDialogInternal(); } });
    }
    private void showDialogInternal() {
        this.closedByOkButton = false;
        this.jspinWidth.setValue(Integer.valueOf(this.controller.getWidth()));
        this.jspinHeight.setValue(Integer.valueOf(this.controller.getHeight()));
        this.jspinNumColors.setValue(Integer.valueOf(this.controller.getNumColors()));
        this.jspinCellSize.setValue(Integer.valueOf(this.controller.getCellSize()));
        this.jcomboStartPos.setSelectedIndex(this.controller.getStartPos().ordinal());
        this.jcomboGridLines.setSelectedIndex(this.controller.getGridLines().ordinal());
        this.jcomboBoardColorNumbers.setSelectedIndex(this.controller.getBoardColorNumbers().ordinal());
        this.jcomboHighlightColor.setSelectedIndex(this.controller.getHighlightColor().ordinal());
        this.jcomboColorSchemes.setSelectedIndex(this.controller.getUiColorsNumber());
        this.jcomboLookAndFeel.setSelectedItem(this.controller.getLafName());
        this.getRootPane().setDefaultButton(this.buttonOk);
        this.pack();
        this.setLocationRelativeTo(this.mainWindow);
        this.setVisible(true);
    }

    protected void setValues(
            final int width,
            final int height,
            final int numColors,
            final StartPositionEnum spe,
            final GridLinesEnum gle,
            final BoardColorNumbersEnum bcne,
            final HighlightColorEnum hce,
            final int uiColorsNumber,
            final int cellSize,
            final String lafName ) {
        this.jspinWidth.setValue(Integer.valueOf(width));
        this.jspinHeight.setValue(Integer.valueOf(height));
        this.jspinNumColors.setValue(Integer.valueOf(numColors));
        this.jspinCellSize.setValue(Integer.valueOf(cellSize));
        this.jcomboStartPos.setSelectedIndex(spe.ordinal());
        this.jcomboGridLines.setSelectedIndex(gle.ordinal());
        this.jcomboColorSchemes.setSelectedIndex(uiColorsNumber);
        this.jcomboBoardColorNumbers.setSelectedIndex(bcne.ordinal());
        this.jcomboHighlightColor.setSelectedIndex(hce.ordinal());
        this.jcomboLookAndFeel.setSelectedItem(lafName);
    }

    private static abstract class EnumItem {
        private final String l10nString;
        private EnumItem(final String l10nKey) {
            this.l10nString = PreferencesDialog.L10N.getString(l10nKey);
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return this.l10nString;
        }
    }

    private static class StartPosItem extends EnumItem {
        private final StartPositionEnum spe;
        private StartPosItem(StartPositionEnum spe) {
            super(spe.l10nKey);
            this.spe = spe;
        }
    }

    private static class GridLinesItem extends EnumItem {
        private final GridLinesEnum gle;
        private GridLinesItem(GridLinesEnum gle) {
            super(gle.l10nKey);
            this.gle = gle;
        }
    }

    private static class BoardColorNumbersItem extends EnumItem {
        private final BoardColorNumbersEnum bcne;
        private BoardColorNumbersItem(BoardColorNumbersEnum bcne) {
            super(bcne.l10nKey);
            this.bcne = bcne;
        }
    }

    private static class HighlightColorItem extends EnumItem {
        private final HighlightColorEnum hce;
        private HighlightColorItem(HighlightColorEnum hce) {
            super(hce.l10nKey);
            this.hce = hce;
        }
    }
}
