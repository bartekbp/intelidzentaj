package faceRecognition;

import java.sql.Timestamp;
import java.util.Calendar;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class FaceDetection {

	// private static String fileToRecognize = "dataf/b14.JPG";

	private static final int SCALE = 2;

	public String detectAndRecognize(String fileToRecognize) {

		// public static void main(String[] args){

		String CASCADE_FILE = "haarcascade_frontalface_alt.xml";

		IplImage origImg = cvLoadImage(fileToRecognize, 1);

		// convert to grayscale
		IplImage grayImg = IplImage.create(origImg.width(), origImg.height(),
				IPL_DEPTH_8U, 1);
		cvCvtColor(origImg, grayImg, CV_BGR2GRAY);

		// scale the grayscale (to speed up face detection)
		IplImage smallImg = IplImage.create(grayImg.width() / SCALE,
				grayImg.height() / SCALE, IPL_DEPTH_8U, 1);
		cvResize(grayImg, smallImg, CV_INTER_LINEAR);

		// equalize the small grayscale
		IplImage equImg = IplImage.create(smallImg.width(), smallImg.height(),
				IPL_DEPTH_8U, 1);
		cvEqualizeHist(smallImg, equImg);

		// create temp storage, used during object detection
		CvMemStorage storage = CvMemStorage.create();

		// instantiate a classifier cascade for face detection

		CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(
				cvLoad(CASCADE_FILE));
		System.out.println("Detecting faces...");

		CvSeq faces = cvHaarDetectObjects(equImg, cascade, storage, 1.1, 3,
				CV_HAAR_DO_CANNY_PRUNING);

		cvClearMemStorage(storage);

		int total = faces.total();
		if(total > 1){
			return "Found more than one face";
		}
		System.out.println("Found " + total + " face(s)");
		try {
			CvRect rc = new CvRect(cvGetSeqElem(faces, 0));
			int x = rc.x(), y = rc.y(), w = rc.width(), h = rc.height();
			IplImage im;

			// wytnij pierwsza znaleziona twarz i rozpoznaj ja
			IplImage fromthis = cvLoadImage(fileToRecognize,
					CV_LOAD_IMAGE_GRAYSCALE);
			im = IplImage.create(100, 100, IPL_DEPTH_8U, 1);
			cvSetImageROI(fromthis,
					cvRect(x * SCALE, y * SCALE, w * SCALE, h * SCALE));
			System.out.println(origImg);
			System.out.println(im);
			cvResize(fromthis, im);
			System.out.println(im);

			FaceRecognition fr = new FaceRecognition();
			String output = fr.faceRecognize(im);
			long epoch = System.currentTimeMillis()/1000;
			String imagename = "recognized/face"+epoch+".jpg";
			cvSaveImage(imagename, im);
			// wyswietl twarz na ekran
			// final CanvasFrame canvas = new CanvasFrame(output, 1);
			// canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			// canvas.showImage(im);
			return output;

		} catch (NullPointerException e) {
			return "FaceUnrecognized";
		}

	}

}