import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;

public class TestAudio extends Application{

    boolean play = false;
    String bip = "src/audio.wav";

    int clipCounter = 0;
    float startTime = 0;

    int fileCounter = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {


        //Needed to initialize the toolkit.
        //Application.launch();

        Media hit = new Media(new File(bip).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.setVolume(0.05);

        //mediaPlayer.play();

        Button playbutton = new Button("Play");

        playbutton.setOnAction(event -> {
            clipCounter++;

            if (clipCounter == 1){
                startTime = (float) mediaPlayer.getCurrentTime().toSeconds();
            } else if (clipCounter >= 2){
                clipCounter = 0;
                getContents((int)startTime,(int) (mediaPlayer.getCurrentTime().toSeconds()-startTime)+1);
                fileCounter++;
            }


            play = !play;
            if (play){
                playbutton.setText("Pause");
                mediaPlayer.play();
                System.out.println(mediaPlayer.currentTimeProperty().getValue().toMillis());
            }else{
                playbutton.setText("Play");
                mediaPlayer.pause();
            }
        });

        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(playbutton);

        Scene secondScene = new Scene(secondaryLayout, 230, 100);

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Play Audio");
        newWindow.setScene(secondScene);

        // Set position of second window, related to primary window.
        newWindow.setX(primaryStage.getX() + 200);
        newWindow.setY(primaryStage.getY() + 100);

        newWindow.show();

    }

    public void getContents(int startTime, int endTime){
        copyAudio("src/audio.wav","src/zander"+ fileCounter +".wav",startTime,endTime);
    }

    public void copyAudio(String sourceFileName, String destinationFileName, int startSecond, int secondsToCopy) {
        AudioInputStream inputStream = null;
        AudioInputStream shortenedStream = null;
        try {
            File file = new File(sourceFileName);
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
            AudioFormat format = fileFormat.getFormat();
            inputStream = AudioSystem.getAudioInputStream(file);
            int bytesPerSecond = format.getFrameSize() * (int)format.getFrameRate();
            inputStream.skip(startSecond * bytesPerSecond);
            long framesOfAudioToCopy = secondsToCopy * (int)format.getFrameRate();
            shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
            File destinationFile = new File(destinationFileName);
            AudioSystem.write(shortenedStream, fileFormat.getType(), destinationFile);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (inputStream != null) try { inputStream.close(); } catch (Exception e) { System.out.println(e); }
            if (shortenedStream != null) try { shortenedStream.close(); } catch (Exception e) { System.out.println(e); }
        }
    }

}
