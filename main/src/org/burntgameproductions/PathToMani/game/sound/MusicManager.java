
package org.burntgameproductions.PathToMani.game.sound;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.files.FileManager;

import java.util.ArrayList;

/**
 * Singleton class that is responsible for playing all music throughout the game.
 */
public final class MusicManager {
    private static MusicManager instance = null;
    private static final String DIR = "res/sounds/";
    private final Music menuMusic;
    private ArrayList<Music> gameMusic = new ArrayList<Music>();;
    private Music currentlyPlaying = null;

    /**
     * Returns the singleton instance of this class.
     * @return The instance.
     */
    public static MusicManager getInstance() {
        if(instance == null) {
            instance = new MusicManager();
        }

        return instance;
    }

    /**
     * Initalise the MusicManager class.
     */
    private MusicManager() {//TODO: Replace this music with some of our own so then we can bypass the copyrights on it.
        menuMusic = Gdx.audio.newMusic(FileManager.getInstance().getStaticFile("res/sounds/music/menu.ogg"));
        //TODO: No copyright sound on YouTube would probably be the go.
        gameMusic.add(Gdx.audio.newMusic(FileManager.getInstance().getStaticFile("res/sounds/music/cartoon-on-off.ogg")));
        gameMusic.add(Gdx.audio.newMusic(FileManager.getInstance().getStaticFile("res/sounds/music/AW-force.ogg")));
        menuMusic.setLooping(true);
    }

    /**
     * Start playing the music menu from the beginning of the track. The menu music loops continuously.
     */
    public void PlayMenuMusic(GameOptions options) {
        if(currentlyPlaying != null )
        {
            if(currentlyPlaying != menuMusic || (currentlyPlaying == menuMusic && !currentlyPlaying.isPlaying()))
            {
                    StopMusic();
                    playMusic(menuMusic, options);
            }
        }else
        {
            StopMusic();
            playMusic(menuMusic, options);
        }

    }

    public void PlayGameMusic(final GameOptions options) {
        StopMusic();
        if(currentlyPlaying != null && gameMusic.contains(currentlyPlaying))
        {
            int index = gameMusic.indexOf(currentlyPlaying) +1;
            if(gameMusic.size()-1 >= index)
            {
                playMusic(gameMusic.get(index), options);
                currentlyPlaying.setOnCompletionListener(new Music.OnCompletionListener() {
                    @Override
                    public void onCompletion(Music music) {
                        PlayGameMusic(options);
                    }
                });

            }else
            {
                playMusic(gameMusic.get(0), options);
            }
        }else
        {
           playMusic(gameMusic.get(0), options);
        }
    }

    public void playMusic(Music music, GameOptions options)
    {
        currentlyPlaying = music;
        currentlyPlaying.setVolume(options.volMul);
        currentlyPlaying.play();
    }
    /**
     * Stop playing all music.
     */
    public void StopMusic() {
        if(currentlyPlaying != null)
        {
            currentlyPlaying.stop();
        }
    }

    public void resetVolume(GameOptions options)
    {
        currentlyPlaying.setVolume(options.volMul);
    }
}