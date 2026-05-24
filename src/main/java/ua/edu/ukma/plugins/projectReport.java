package ua.edu.ukma.plugins;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.IOException;
import java.util.List;
import java.nio.file.*;
import java.util.stream.Stream;

@Mojo(name = "project-report")
public class projectReport extends AbstractMojo{

    @Parameter(defaultValue = "${project.basedir}/src/main/java")
    private String source;
    @Parameter(defaultValue = "${project.build.directory}")
    private String output;
    @Parameter(defaultValue = "report.txt")
    private String file;
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        Path start = Paths.get(source);
        StringBuilder content = new StringBuilder();
        List<Path> ls = List.of();

        int countJava = 0;
        try(Stream<Path> stream = Files.walk(start)){
            ls = stream.filter(p -> p.toString().endsWith(".java") && Files.isRegularFile(p)).toList();
            countJava = ls.size();
        } catch(IOException e){
            throw new RuntimeException(e);
        }

        int countClass = 0;
        for(int i = 0; i < ls.size(); i++){
            try{
                if((Files.readString(ls.get(i))).contains("class")){
                    countClass++;
                }
            } catch (IOException e){
                throw new RuntimeException();
            }
        }

        String groupId = project.getGroupId();
        String ArtifactId = project.getArtifactId();
        String Version = project.getVersion();
        String Name = project.getName();
        String Description = project.getDescription();
        String Dependencies = project.getDependencies().toString();

        content.append("Project report: \n" +
                     "----Project info----\n" +
                     "Name: " + Name + "\n" +
                     "Description: " + Description + "\n" +
                     "Version: " + Version + "\n" +
                     "Group Id: " + groupId + "\n" +
                     "Artifact Id: " + ArtifactId + "\n" +
                     "Dependencies: " + Dependencies + "\n" +
                     "----Code info----\n" +
                     "Java files: " + countJava + "\n" +
                     "Java classes: " + countClass + "\n"
        );

        try{
            Path outputPath = Paths.get(output, file);
            Files.writeString(outputPath, content.toString());
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
