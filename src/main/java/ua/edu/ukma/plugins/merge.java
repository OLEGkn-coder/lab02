package ua.edu.ukma.plugins;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.IOException;
import java.util.List;
import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Mojo(name = "merge-sources")
public class merge extends AbstractMojo {

     @Parameter(defaultValue = "${project.basedir}/src/main/java")
     private String sourse;
     @Parameter(defaultValue = "${project.build.directory}")
     private String output;
     @Parameter(defaultValue = "all-sources.java")
     private String file;

     @Override
     public void execute() throws MojoExecutionException, MojoFailureException {

         Path start = Paths.get(sourse);
         List<Path> ls = List.of();
         try(Stream<Path> stream = Files.walk(start)) {
             ls = stream.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".java")).collect(Collectors.toList());
         } catch (IOException e) {
             throw new RuntimeException(e);
         }

         StringBuilder content = new StringBuilder();
         for(int i = 0; i < ls.size(); i++){
             try {
                 content.append("File name: " + ls.get(i).toString());
                 content.append(Files.readString(ls.get(i)));
                 content.append("\n----------------------------------");
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }
         }
         try {
             Path outputPath = Paths.get(output, file);
             Files.writeString(outputPath, content.toString());
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
     }
 }