package faceRecognition;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class FaceDetectionTest {

    public static final String RECOGNIZED_TXT = "recognized.txt";

    @Test
    public void testLogRecognizedPerson() throws Exception {
        File reco = new File(RECOGNIZED_TXT);
        if(reco.exists()){
            assertTrue(reco.delete());

        }
        FaceDetection detection = new FaceDetection(RECOGNIZED_TXT);
        detection.logRecognizedPerson("imagePath.jpg", "name");

        assertTrue(reco.exists());

        detection.logRecognizedPerson("imagePath.jpg", "name");
    }
}