package tk.luryus.subtitlething;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

public class SubtitleChooseDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel labelMovieFileName;
    private JPanel panelDragArea;
    private JButton buttonBrowse;
    private JLabel labelSubFileName;
    private JComboBox comboBoxFPS;
    private JPanel fpsPanel;

    private SubtitleThing subtitleThing;

    private String subFullPath;

    private static final String PREF_SUB_FOLDER_PATH = "pref_sub_folder_path";
    private static final String PREF_FPS_SELECTION = "pref_fps_selection";

    public SubtitleChooseDialog(SubtitleThing subtitleThing, String movieFullPath) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.subtitleThing = subtitleThing;
        labelMovieFileName.setText(movieFullPath);

        panelDragArea.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    return false;
                }
                return true;
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }

                Transferable t = support.getTransferable();
                List<File> list = null;
                try {
                    list = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }

                return list != null && list.size() == 1 && dropSubtitle(list.get(0));
            }
        });

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        buttonBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                fc.setMultiSelectionEnabled(false);

                fc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory() ||
                                FilenameUtils.isExtension(f.getAbsolutePath(), new String[]{"srt", "sub"}))
                            return true;
                        //else
                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return "SRT and SUB subtitles";
                    }
                });


                String openDialogPath = prefs.get(PREF_SUB_FOLDER_PATH, "");
                File openDirectory = null;

                if (!openDialogPath.equals("")) {
                    openDirectory = new File(openDialogPath);
                    if (openDirectory.isDirectory())
                        fc.setCurrentDirectory(openDirectory);
                }

                int returnVal = fc.showOpenDialog(SubtitleChooseDialog.this);

                if (!fc.getCurrentDirectory().equals(openDirectory)) {
                    // save new current dir
                    prefs.put(PREF_SUB_FOLDER_PATH, fc.getCurrentDirectory().getAbsolutePath());
                }

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    dropSubtitle(fc.getSelectedFile());
                }
            }
        });

        // set FPS selection
        String oldFPS = prefs.get(PREF_FPS_SELECTION, "");
        if (!oldFPS.equals("")) {
            comboBoxFPS.setSelectedItem(oldFPS);
        }

        comboBoxFPS.addActionListener(e -> {
            String newFPS = ((String) comboBoxFPS.getSelectedItem());
            prefs.put(PREF_FPS_SELECTION, newFPS);
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private boolean dropSubtitle(File file) {
        subFullPath = file.getAbsolutePath();

        if (FilenameUtils.isExtension(subFullPath, "srt")) {
            labelSubFileName.setText(subFullPath);
            fpsPanel.setVisible(false);
            pack();
            return true;
        } else if (FilenameUtils.isExtension(subFullPath, "sub")) {
            labelSubFileName.setText(subFullPath);
            fpsPanel.setVisible(true);
            pack();
            return true;
        } else {
            subFullPath = "";
            return false;
        }
    }

    private void onOK() {
        double fps = -1;
        if (fpsPanel.isVisible()) {
            fps = Double.parseDouble((String) comboBoxFPS.getSelectedItem());
        }
        if (subtitleThing.doTheMagic(subFullPath, fps)) {
            dispose();
            System.exit(0);
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
        System.exit(0);
    }

    public void showDialog() {
        pack();
        setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        panel3.add(panel4, BorderLayout.NORTH);
        final JLabel label1 = new JLabel();
        label1.setText("Movie File:");
        panel4.add(label1, BorderLayout.WEST);
        labelMovieFileName = new JLabel();
        labelMovieFileName.setText("<filename>");
        panel4.add(labelMovieFileName, BorderLayout.CENTER);
        panelDragArea = new JPanel();
        panelDragArea.setLayout(new BorderLayout(0, 0));
        panel3.add(panelDragArea, BorderLayout.CENTER);
        panelDragArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "  ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.ABOVE_TOP));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelDragArea.add(panel5, BorderLayout.NORTH);
        final JLabel label2 = new JLabel();
        label2.setText("Drag SRT/SUB here or ");
        panel5.add(label2);
        buttonBrowse = new JButton();
        buttonBrowse.setText("Browse...");
        panel5.add(buttonBrowse);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, -1, false, true));
        panelDragArea.add(panel6, BorderLayout.CENTER);
        fpsPanel = new JPanel();
        fpsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        fpsPanel.setEnabled(true);
        fpsPanel.setVisible(false);
        panel6.add(fpsPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("FPS:");
        fpsPanel.add(label3);
        comboBoxFPS = new JComboBox();
        comboBoxFPS.setEditable(true);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("23.976");
        defaultComboBoxModel1.addElement("24");
        defaultComboBoxModel1.addElement("25");
        defaultComboBoxModel1.addElement("29.97");
        defaultComboBoxModel1.addElement("30");
        comboBoxFPS.setModel(defaultComboBoxModel1);
        fpsPanel.add(comboBoxFPS);
        labelSubFileName = new JLabel();
        labelSubFileName.setHorizontalAlignment(0);
        labelSubFileName.setHorizontalTextPosition(0);
        labelSubFileName.setText(" ");
        panel6.add(labelSubFileName, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
