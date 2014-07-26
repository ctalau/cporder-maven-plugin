package ro.ctalau;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Verifies whether there is a class that is present in more than one
 * jar dependencies of the project. 
 * 
 * The presence of such a class may produce different runtime behaviors,
 * depending on the order of the jars in the classpath of the project.
 */
@Mojo(name = "verify",
  requiresDependencyResolution = ResolutionScope.RUNTIME,
  defaultPhase = LifecyclePhase.VERIFY,
  requiresProject = true)
public class CheckClasspathOrderMojo extends AbstractMojo {

  /**
   * The artifact type of the dependencies that we want to check.
   */
  private static final String JAR_ARTIFACT_TYPE = "jar";

  /**
   * The project in which the plugin is executing.
   */
  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  /**
   * Executes the plugin.
   */
  public void execute() throws MojoExecutionException {
    @SuppressWarnings("unchecked")
    Set<Artifact> dependencies = project.getArtifacts();

    // A mapping from classes to the dependency which they come from. 
    Map<String, Artifact> allClasses = new HashMap<String, Artifact>();

    for (Artifact dependency : dependencies) {
      if (JAR_ARTIFACT_TYPE.equals(dependency.getType())) {
        // Analyze jar dependencies with scope runtime and compile.
        if (Artifact.SCOPE_COMPILE.equals(dependency.getScope())
                || Artifact.SCOPE_RUNTIME.equals(dependency.getScope())) {
          getLog().debug("Analyzing dependency: " + dependency.getArtifactId());
          try {
            checkDuplicates(allClasses, dependency);
          } catch (IOException e) {
            throw new MojoExecutionException(
                    "Error reading dependency artifact: "
                            + dependency.getArtifactId(), e);
          }
        } else {
          getLog().debug(
                  "Dependency " + dependency.getArtifactId() + " with scope: "
                          + dependency.getScope());
        }
      }
    }
  }

  /**
   * Checks whether the class files from the given dependency occur also in 
   * other dependencies.
   * 
   * @param allClasses All the classes collected so far from other dependencies.
   * @param dependency The current dependency.
   * 
   * @throws MojoExecutionException If there are duplicates or the dependency 
   * cannot be found locally.
   * 
   * @throws IOException If the dependency file cannot be read.
   */
  private void checkDuplicates(Map<String, Artifact> allClasses, Artifact dependency)
          throws MojoExecutionException, IOException {
    File dependencyFile = dependency.getFile();
    if (dependencyFile == null) {
      throw new MojoExecutionException("Dependency "
              + dependency.getArtifactId()
              + " cannot be found in the local repository.");
    }
    
    ZipInputStream zip = new ZipInputStream(new FileInputStream(dependencyFile));
    try {
      while (true) {
        ZipEntry e = null;
        e = zip.getNextEntry();
        if (e == null)
          break;
        String className = e.getName();
        // We look only for class files.
        if (className.endsWith(".class")) {
          getLog().debug("Found class: " + e);
          Artifact otherDependency = allClasses.put(className, dependency);
          if (otherDependency != null) {
            // The class already appears in another dependency.
            throw new MojoExecutionException("Class :" + className + 
                    " appears in two dependencies: " + otherDependency + " and " + 
                    dependency);
          }
        }
      }
    } finally {
      zip.close();
    }
  }

}