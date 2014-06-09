package server;

import faceRecognition.FaceDetection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pawel on 26.05.14.
 *
 * Simple server.
 */
public class PostImageServer {

    private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
    public static final String OUTPUT_DIR = "./inputImages/";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH-mm-ss-SS");
    public static final String IN_SUFFIX = "input.jpg";
    public static final String RECO_SUFFIX = "reco.jpg";
    public static final String RECO_FACES_FILE = OUTPUT_DIR + "recognized.txt";

    static class Handler extends AbstractHandler{

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
                baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
                baseRequest.setHandled(true);
            }else{
                return;
            }
            Part part = request.getPart("img");
            System.out.println(part.getName());

            String fileName = generateFileName();
            String outputFileName = fileName + IN_SUFFIX;
            String recognizedFacePath = fileName + RECO_SUFFIX;
            writeStream(outputFileName, part.getInputStream());
            FaceDetection fd = new FaceDetection(RECO_FACES_FILE);
            String name;
            try {
                name = fd.detectAndRecognize(outputFileName, recognizedFacePath);
            }catch ( Exception e){
                name = "recognizer error";
                e.printStackTrace();
            }
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(name);

        }

        private String generateFileName() {
            Date date = new Date();
            return OUTPUT_DIR + DATE_FORMAT.format(date);
        }

        public void writeStream(String path, InputStream stream) throws IOException {
            File outputFile = new File(path);
            Files.copy(stream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void main(String[] args) throws Exception {
        File file = new File(OUTPUT_DIR);
        if(!file.exists())
            Files.createDirectory(file.toPath());

        Server server = new Server(8081);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newResource(file));



        HandlerList handlers = new HandlerList();
        handlers.addHandler(new Handler());
        handlers.addHandler(new ShowFilesHandler(OUTPUT_DIR));
        handlers.addHandler(resourceHandler);
        server.setHandler(handlers);

        server.start();
        server.join();
    }
}