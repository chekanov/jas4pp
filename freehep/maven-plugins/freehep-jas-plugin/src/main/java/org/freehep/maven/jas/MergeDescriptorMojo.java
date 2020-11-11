package org.freehep.maven.jas;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.freehep.xml.util.ClassPathEntityResolver;

/**
 * @description Merges contents of two plugin descriptor files.
 * @goal merge-descriptor
 * @phase prepare-package
 * @requiresProject
 * @requiresDependencyResolution
 * @author onoprien
 */
public class MergeDescriptorMojo extends AbstractMojo {
    
    private final String FILE_NAME = "PLUGIN-inf/plugins.xml";

    /**
     * Skip execution if true.
     * 
     * @parameter (defaultValue="false")
     * @readonly
     */
    protected boolean skip;

    /**
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    protected MavenProject project;    

    /**
     * @parameter expression="${project.build.directory}/generated-sources/annotations"
     * @readonly
     */
    protected File inputDirectory;

    /**
     * @parameter expression="${project.build.directory}/classes"
     * @readonly
     */
    protected File outputDirectory;
    
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    
      if (skip) return;
    
      try {
          
          SAXReader reader = new SAXReader(false);
          reader.setEntityResolver(new ClassPathEntityResolver("plugin.dtd", MergeDescriptorMojo.class));
          
          Document sourceDocument;
          File file = new File(inputDirectory, FILE_NAME);
          if (!file.exists()) {
              getLog().info("Skipping non-existing file "+ file);
              return;
          }
          try {
              sourceDocument = reader.read(file);
          } catch (DocumentException x) {
              throw new MojoFailureException(x, "Error reading plugin descriptor file " + file, "Error reading plugin descriptor file " + file +": "+ x);
          }
          
          file = new File(outputDirectory, FILE_NAME);
          Document targetDocument;
          if (file.exists()) {
              try {
                  targetDocument = reader.read(file);
              } catch (DocumentException x) {
                  throw new MojoFailureException(x, "Error reading plugin descriptor file " + file, "Error reading plugin descriptor file " + file +": "+ x);
              }
          } else {
              targetDocument = DocumentHelper.createDocument();
              targetDocument.addElement("plugins");              
          }
                    
          for (Iterator<Element> i = sourceDocument.getRootElement().elementIterator(); i.hasNext();) {
              Element e = i.next();
              i.remove();
              targetDocument.getRootElement().add(e);
          }

          OutputFormat format = OutputFormat.createPrettyPrint();
          XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
          writer.write(targetDocument);

      } catch (Exception x) {
          throw new MojoFailureException("Error merging plugin descriptors: " + x);
      }

  }

}
