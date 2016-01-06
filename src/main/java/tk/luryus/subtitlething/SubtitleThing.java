package tk.luryus.subtitlething;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class SubtitleThing {

    private final String movieFileName, movieFileWoExt;
    private final String sub2srtFile;
    private final SubtitleChooseDialog dialog;

    public static void main(String[] args) {
        System.out.println("SubtitleThing v0.0.2");
        if (args.length != 2) {
            System.out.println("Usage: SubtitleThing <movie file>");
            return;
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        new SubtitleThing(args);
    }

    public SubtitleThing(String[] args) {
        // get movie file from arguments, and get the name without extension
        String sub2srtRelative = args[0];
        movieFileName = args[1];

        final File f = new File(sub2srtRelative);
        sub2srtFile = f.getAbsolutePath();

        // get full movie path for dialog
        final File movieFile = new File(movieFileName);
        final String movieFullPath = movieFile.getAbsolutePath();

        // generate movie path without ext
        movieFileWoExt = FilenameUtils.getFullPath(movieFullPath) + FilenameUtils.getBaseName(movieFullPath);

        // show the main window (Dialog)
        dialog = new SubtitleChooseDialog(this, movieFullPath);
        dialog.showDialog();
    }

    public boolean doTheMagic(String fullSubPath, double fps) {
        final String outputFilePath = movieFileWoExt + ".srt";
        final File outputFile = new File(outputFilePath);
        if (outputFile.exists()) {
            int n = JOptionPane.showOptionDialog(dialog, "Subtitle file exists. Overwrite?", "SubtitleThing",  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (n == JOptionPane.YES_OPTION) {
                System.out.println("Overwrite");
                outputFile.delete();
            } else if (n == JOptionPane.NO_OPTION) {
                System.out.println("Don't overwrite");
                return false;
            }
        }

        final File inputSrt, inputSub;

        // if we made it here, we're overwriting

        if (FilenameUtils.isExtension(fullSubPath, "srt")) {
            inputSrt = new File(fullSubPath);
            inputSub = null;
        } else if (FilenameUtils.isExtension(fullSubPath, "sub")) {
            inputSub = new File(fullSubPath);
            try {
                inputSrt = File.createTempFile("temp_srt", ".srt");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(dialog, "Error in creating temp file");
                e.printStackTrace();
                return false;
            }

            try {
                // Get sub2srt binary with classpath


                ProcessBuilder builder = new ProcessBuilder("perl", sub2srtFile,
                        "--force",
                        "-f=" +  Double.toString(fps),
                        inputSub.getAbsolutePath(),
                        inputSrt.getAbsolutePath());
                builder.redirectErrorStream(true);
                builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                for (String s : builder.command()) {
                    System.out.print(s + " ");
                }
                System.out.println();
                Process proc = builder.start();

                if (0 != proc.waitFor()) {
                    JOptionPane.showMessageDialog(dialog, "Error in converting");
                    inputSrt.delete();
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else
            return false;

        try {
            FileUtils.copyFile(inputSrt, outputFile);
            // gotta remove the temp file
            if (inputSub != null) inputSrt.delete();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        return true;
    }
}
