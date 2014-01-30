//
// Copyright (C) 2009 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.template;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

// we should keep this the single dependency to jpf-core
import gov.nasa.jpf.util.FileUtils;
import gov.nasa.jpf.util.JPFSiteUtils;

import org.stringtemplate.v4.*;

/**
 * tool to create a JPF project directory with subdirs and standard files
 * 
 * the reason why we don't use gov.nasa.jpf.util.FileUtils or automatically
 * detect the jpf-core location is that this might be included in an IDE plugin
 * that doesn't have JPF in its classpath
 */
public class CreateProject {

  static final char ST_START_CHAR = '`';
  static final char ST_END_CHAR = '`';
  
  static final String JPF_CORE = "jpf-core";
  
  public static class Exception extends RuntimeException {
    Exception (String details){
      super(details);
    }
  }

  public static interface Printable {
    void printOn (PrintWriter ps);
  }

  static final String[] defaultSrcDirs = { "main", "peers", "annotations", "classes", "tests", "examples" };

  File coreDir;

  String projectName;
  File projectDir;
  
  List<String> dependentProjects = new ArrayList<String>();
  List<String> dependentJars = new ArrayList<String>();
  
  File srcDir;
  ArrayList<File> srcDirs = new ArrayList<File>();    // what we keep within srcDir

  File libDir;
  File toolsDir;
  File binDir;

  // optional dirs
  File nbDir;
  File eclipseDir;

  //--- the public API


  public static void main (String[] args){
    if (args.length < 1){
      System.out.println("usage: java " + CreateProject.class.getName() + " <new-project-path> {<dependent-project-name> | <jar-path>,..}");
      return;
    }

    File siteCoreDir = JPFSiteUtils.getSiteCoreDir();
    if (siteCoreDir == null) {
      System.err.println("Error: unable to locate jpf-core, check site.properties");
      return;
    }
    
    String corePath = siteCoreDir.getPath();
    String projectPath = args[0];

    ArrayList<String> dependentProjects = new ArrayList<String>();
    ArrayList<String> dependentJars = new ArrayList<String>();    
    
    dependentProjects.add(JPF_CORE);
    
    if (args.length > 1){
      for (int i=1; i<args.length; i++){
        String dep = args[i];
        if (!JPF_CORE.equals(dep)){ // no need to add jpf-core again
          if (dep.endsWith(".jar")){
            dependentJars.add(dep);
          } else {
            dependentProjects.add(dep);
          }
        }
      }
    }
    
    CreateProject creator = new CreateProject(corePath, projectPath, dependentProjects, dependentJars);


    creator.createProject();
  }
  
  public CreateProject (String corePath, String projectPath, List<String> depProjects, List<String> depJars){
    coreDir = new File(corePath);
    
    projectDir = new File(projectPath);
    projectName = projectDir.getName();
    
    dependentProjects = depProjects;
    dependentJars = depJars;

    srcDir = new File(projectDir, "src");
    libDir = new File(projectDir, "lib");
    toolsDir = new File(projectDir, "tools");
    binDir = new File(projectDir, "bin");

    eclipseDir = new File(projectDir, "eclipse");
    nbDir = new File(projectDir, "nbproject");
  }

  public void setCoreDir (String coreDirName){
    coreDir = new File(coreDirName);
  }

  public void addSrcDirs (String[] srcDirNames){
    for (String srcDirName : srcDirNames){
      addSrcDir(srcDirName);
    }
  }

  public void addSrcDir (String srcDirName){
    File sd = new File(srcDir, srcDirName);
    srcDirs.add(sd);
  }

  public void addEclipseDir () {
    eclipseDir = new File(projectDir, "eclipse");
  }

  public void addNetBeansDir (){
    nbDir = new File(projectDir, "nbproject");
  }

  public void createProject () throws CreateProject.Exception {

    if (!coreDir.isDirectory()){
      throw new Exception("not a valid jpf-core dir: " + coreDir.getAbsolutePath() 
              + " (check ${user.home}/.jpf/site.properties");
    } else {
      System.out.println("using jpf-core directory: " + coreDir.getAbsolutePath());
    }

    if (projectDir.isDirectory()){
      System.out.println("initializing existing project directory: " + projectDir.getAbsolutePath());
    } else {
      System.out.println("creating project directory: " + projectDir.getAbsolutePath());
    }
    
    // if we didn't set them explicitly, use the defaults
    if (srcDirs.isEmpty()){
      addSrcDirs(defaultSrcDirs);
    }

    createDirectories();
    dependentJars = copyLibs(); // we have to do this before instantiating the scripts
    
    createAntScript();
    createProjectProperties();
    createScripts();
    createEclipseFiles();
    createNbFiles();
    copyTools();
    createHgFiles();
  }

  //--- our internal creators/initializers

  void createDirectories (){
    projectDir.mkdirs();

    if (projectDir.isDirectory()){
      // create the source directories
      createDir(srcDir);
      for (File sd : srcDirs){
        createDir(sd);
      }

      // create the utility dirs
      createDir(libDir);
      createDir(toolsDir);
      createDir(binDir);

      //create and initialize the optional IDE dirs
      createDir(nbDir);
      createDir(eclipseDir);

    } else {
      throw new Exception("failed to create project dir: " + projectDir.getPath());
    }
  }

  List<String> copyLibs(){
    List<String> libs = new ArrayList<String>();
    String libPath = libDir.getName() + '/';
    
    // copy existing external jars into libDir, remember non-existing ones 
    for (String pn : dependentJars){
      File f = new File(pn);
      if (!f.getParentFile().equals(libDir)){ // we need to copy it into lib
        if (f.isFile()){
          try {
            FileUtils.copyFile( f, libDir);
          } catch (IOException iox){
            throw new Exception("copy of lib failed: " + f);
          }
        } else { // we add it anyways, but as a relative path
          libs.add( libPath + f.getName());
        }
      }
    }
    
    // now add all the jars we have in libDir
    FilenameFilter jarFilter = new FilenameFilter(){
      public boolean accept (File dir, String name){
        return name.endsWith(".jar");
      }
    };
    for (File f : libDir.listFiles(jarFilter)){
      libs.add( libPath + f.getName()); // use project-relative path and Unix separator
    }
    
    return libs;
  }

  void copyTools (){
    if (coreDir.isDirectory()){
      File buildDir = new File(coreDir, "build");
      if (buildDir.isDirectory()){
        try {
          FileUtils.copyFile(new File(buildDir, "RunJPF.jar"), toolsDir);
          FileUtils.copyFile(new File(buildDir, "RunAnt.jar"), toolsDir);
          FileUtils.copyFile(new File(buildDir, "RunTest.jar"), toolsDir);
        } catch (IOException iox){
          throw new Exception( "copy of Run*.jar into tools/ failed");
        }
      }
    }
  }


  void createAntScript() {
    createTemplateInstance("resources/antscript", projectDir, "build.xml");
  }

  void createProjectProperties() {
    createTemplateInstance("resources/jpf.properties", projectDir, "jpf.properties");
  }

  void createScripts() {
    String[] scripts = { "jpf", "jpf.bat", "ant", "ant.bat", "test", "test.bat" };

    for (String fname : scripts){
      String contents = getResourceFileContents("resources/" + fname);
      File script = new File(binDir, fname);
      writeToNewFile(script, contents);
      script.setExecutable(true);
    }
  }

  void createEclipseFiles() {
    createTemplateInstance("resources/dot-project", projectDir, ".project");
    createTemplateInstance("resources/dot-classpath", projectDir, ".classpath");

    // unfortunately we do need project specific run/test launch configs since we otherwise can't locate sources
    createTemplateInstance("resources/run-launch-config", eclipseDir, "run-" + projectName + ".launch");
    createTemplateInstance("resources/test-launch-config", eclipseDir, "test-" + projectName + ".launch");

    createTemplateInstance("resources/antbuilder", eclipseDir, "AntBuilder.launch");
  }


  void createNbFiles() {
    createTemplateInstance("resources/nb-project.xml", nbDir, "project.xml");
    createTemplateInstance("resources/ide-file-targets.xml", nbDir, "ide-file-targets.xml");
  }

  void createHgFiles() {
    createTemplateInstance("resources/dot-hgignore", projectDir, ".hgignore");
  }

  //--- various utility methods

  void createDir (File dir){
    if (!dir.isDirectory()){
      if (!dir.mkdir()){
        throw new Exception("failed to create: " + dir.getAbsolutePath());        
      }
    }
  }

  void createTemplateInstance( String resourceName, File dir, String fname){
    String templateContents = getResourceFileContents(resourceName);
    ST st = new ST(templateContents, ST_START_CHAR, ST_END_CHAR);
    
    // set the template vars
    st.add("PROJECT_NAME", projectName);
    
    // we always have jpf-core in the dependency list, so no need to add null attribute
    for (String dep : dependentProjects){
      st.add("PROJECT_DEPENDENCIES", dep);
    }
    
    if (!dependentJars.isEmpty()){
      for (String dep : dependentJars) {
        st.add("JAR_DEPENDENCIES", dep);
      }
    } else {
      st.add("JAR_DEPENDENCIES", null); // avoid annoying warning
    }
    
    // instantiate template
    String contents = st.render();

    // write file
    File newFile = new File(dir, fname);
    writeToNewFile(newFile, contents);
  }

  void printFile(File f, Printable printer) {
    PrintWriter out = null;

    try {
      f.createNewFile();
      FileWriter fw = new FileWriter(f);
      out = new PrintWriter(fw);

      printer.printOn(out);

    } catch (IOException iox){
      throw new Exception("error writing: " + f.getAbsolutePath());

    } finally {
      if (out != null){
        out.close();
      }
    }
  }


  String getResourceFileContents (String fileName){
    InputStream is = getClass().getResourceAsStream(fileName);

    if (is == null){
      throw new Exception("resource not found: " + fileName);
    }

    try {
      int size = is.available();
      if (size > 0){
        ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
        byte[] buf = new byte[1024];
        
        for (int n = is.read(buf); n>=0; n = is.read(buf)){
          bos.write(buf, 0, n);
        }
        
        return bos.toString();
        
      } else {
        throw new Exception("resource empty: " + fileName);
      }

    } catch (IOException iox){
      throw new Exception("error reading resource contents: " + fileName);

    } finally {
      try {
        is.close();
      } catch (IOException iox){
        throw new Exception("failed to close input resource stream: " + fileName);
      }
    }
  }

  void writeToNewFile (File file, String contents){
    FileWriter fw = null;

    try {
      file.createNewFile();

      if (contents != null && contents.length() > 0){
        fw = new FileWriter(file);
        fw.write(contents, 0, contents.length());
      }

    } catch (IOException iox) {
      throw new Exception("failed to write: " + file.getAbsolutePath());

    } finally {
      if (fw != null){
        try {
          fw.close();
        } catch (IOException iox){
          throw new Exception("failed to close output file: " + file.getAbsolutePath());
        }
      }
    }
  }

}
