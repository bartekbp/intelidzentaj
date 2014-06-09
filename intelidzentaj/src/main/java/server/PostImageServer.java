package server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import faceRecognition.FaceDetection;

/**
 * Created by pawel on 26.05.14.
 *
 * Simple server.
 */
public class PostImageServer {

    private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

    static class Handler extends AbstractHandler{

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
                baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
            }
            Part part = request.getPart("img");
            System.out.println(part.getName());
            writeStream("output.jpg", part.getInputStream());
            FaceDetection fd = new FaceDetection();
            String name;
            name = fd.detectAndRecognize("output.jpg");
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getWriter().println(name);
        }

        public void writeStream(String path, InputStream stream) throws IOException {
            File outputFile = new File(path);
            Files.copy(stream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8081);
        server.setHandler(new Handler());
        server.start();
        server.join();
    }
}