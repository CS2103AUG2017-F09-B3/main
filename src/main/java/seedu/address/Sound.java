package seedu.address;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import seedu.address.commons.core.LogsCenter;

/**
 * The Playlist for the background music.
 */
public class Sound {
    public static final int FIRSTSONG = 0;
    public static final int ONE_LESS = 1;
    private static String name = "Sound";
    private static final Logger logger = LogsCenter.getLogger(name);
    private static ArrayList<String> musicList = new ArrayList<String>(Arrays.asList("FurElise.mp3",
            "KissTheRain.mp3"));
    private static int curr = 0;
    public static final int NEXTSONG = curr + 1;
    private static String bip;
    private static Media hit;
    private static MediaPlayer mediaPlayer;

    public static String currSong() {
        return musicList.get(curr);
    }

    /**
     * Plays the next Song on the List
     */

    public static void next() {
        if (curr < musicList.size() - ONE_LESS) {
            curr = NEXTSONG;
        }else {
            curr = FIRSTSONG;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            bip = musicList.get(curr);
            hit = new Media(new File(bip).toURI().toString());
            mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.play();
        }
    }

    /**
     * start playing the first music on the playlist.
     */

    public static void music() {
        try {
            bip = musicList.get(curr);
            hit = new Media(new File(bip).toURI().toString());
            mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.play();
        } catch (Exception ex) {
            logger.info("Error with playing sound.");
            ex.printStackTrace();
        }
    }
}
