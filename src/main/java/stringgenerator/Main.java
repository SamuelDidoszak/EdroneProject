package stringgenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stringgenerator.Database.DatabaseHandler;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@SpringBootApplication
@ComponentScan(basePackageClasses = Main.class)
public class Main {
    @Autowired
    DatabaseHandler databaseHandler;

    @RequestMapping("/")
    String home() {
        return "Home sweet home";
    }

    private static final StringThreads threads = new StringThreads();

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @PostMapping("/new")
    @ResponseBody
    /** Starts a thread which generates a file with provided parameters or fetches it from the database */
    public String startStringThread(@RequestParam int amount, @RequestParam int minLength, @RequestParam int maxLength, @RequestParam String chars) {
        try {
            threads.startThread(amount, minLength, maxLength, chars, databaseHandler);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Generating strings";
    }

    @GetMapping("/threads")
    /** Returns the number of currently working threads */
    public int getThreadAmount() {
        return threads.getThreadAmount();
    }

    @GetMapping("/files")
    /** Returns all files as a multipart */
    public @ResponseBody void getFiles(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("multipart/x-mixed-replace;boundary=END");

        String contentType = "Content-type: text/plain";
        try {
            ServletOutputStream out = response.getOutputStream();

            out.println();
            out.println("--END");

            while (!threads.getFiles().isEmpty()) {
                Pair<String, File> pair = threads.getFiles().pop();

                File file = pair.getSecond();
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

                out.println(contentType);
                out.println("Content-Disposition: attachment; filename=" + file.getName());
                out.println();

                int data = 0;
                while ((data = bufferedInputStream.read()) != -1) {
                    out.write(data);
                }
                bufferedInputStream.close();

                out.println();
                out.println("--END");
                out.flush();

                // Delete file or mark for deletion
                file.delete();
            }

            out.println("--END--");
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("File not found");
        }
    }

    @GetMapping("/file")
    /** Returns an earliest generated file as a file */
    public ResponseEntity<InputStreamResource> getFile() {
        InputStreamResource resource;
        try {
            Pair<String, File> pair = threads.getFiles().pop();
            resource = new InputStreamResource(new FileInputStream(pair.getSecond()));
            pair.getSecond().delete();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("File not found");
        }
        return (ResponseEntity<InputStreamResource>) ResponseEntity.ok();
    }
}
