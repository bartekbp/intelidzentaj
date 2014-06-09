package server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by pawel on 10.06.14.
 *
 * Show files in table. Try to find recognized person for each input image in RECO_FACES_FILE.
 */
public class ShowFilesHandler extends AbstractHandler{


    private String filesDirectory;

    public ShowFilesHandler(String filesDirectory) {
        this.filesDirectory = filesDirectory;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if(target.equals("/photos")){
            File filesDir = new File(filesDirectory);
            Properties imgToName = new Properties();
            try {
                imgToName.load(new FileInputStream(PostImageServer.RECO_FACES_FILE));
            }catch (Exception e){
                e.printStackTrace();
            }

            response.getWriter().print("<!DOCTYPE html>\n<table border=\"1\"><tr><th>input file</th><th>detected face</th><th>recognized person</th></tr>");
            for(File imageFile: filesDir.listFiles()){
                if(imageFile.getName().endsWith(PostImageServer.IN_SUFFIX)){
                    String name = imageFile.getName().replace(PostImageServer.IN_SUFFIX, "");
                    String detectedName = name + PostImageServer.RECO_SUFFIX;
                    String recoName = imgToName.getProperty(detectedName);
                    response.getWriter().print(String.format(
                            "<tr><td><img src=\"%s\" alt=\"img\" /></td><td><img src=\"%s\" alt=\"face not detected\" /></td><td>%s</td></tr>",
                            imageFile.getName(),
                            detectedName,
                            recoName != null ? recoName : "not recognized"));
                }
            }
            response.getWriter().print("</table>");
            baseRequest.setHandled(true);
        }
    }
}
