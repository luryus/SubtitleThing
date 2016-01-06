package tk.luryus.subtitlething;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;

public class SubtitleThing {

    private final String movieFileName, movieFileWoExt;
    private String sub2srtFile;
    private final SubtitleChooseDialog dialog;

    public static void main(String[] args) {
        System.out.println("SubtitleThing v0.0.3");
        if (args.length != 1) {
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
        movieFileName = args[0];

        // load sub2srt to a temp file
        try {
            File tempSub2SrtFile = File.createTempFile("sub2str", "pl"); // create a temp file
            tempSub2SrtFile.deleteOnExit();                              // which will be deleted when JVM exists

            // load sub2srt from resources
            InputStream sub2srtStream = SubtitleThing.class.getClassLoader().getResourceAsStream("sub2srt/sub2srt");
            Files.copy(sub2srtStream, tempSub2SrtFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            sub2srtFile = tempSub2SrtFile.getAbsolutePath();
        } catch (IOException e) {
            System.err.println("sub2srt could not be loaded");
            System.exit(1);
        }

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
            int n = JOptionPane.showOptionDialog(dialog, "Subtitle file exists. Overwrite?", "SubtitleThing",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
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
