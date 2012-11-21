java helloworld project with junit, no ide, only commandline
============================================================

dir struct
-----------

    $ mkdir helloworld-proj                   # create project dir
    $ cd helloworld-proj
    $ mkdir src/main                          # main source dir
    $ mkdir src/test                          # test (junit) source dir
    $ mkdir bin                               # shell/bat scripts to run
    $ mkdir lib                               # lib dir for jar files
    $ mkdir config                            # properties file etc
    $ mkdir build                             # compilation target dir

copy following jar files in lib dir
-----------------------------------

    $ ls lib
    junit-4.10.jar  log4j-1.2.8.jar

create a source file
--------------------

    $ mkdir src/main/foo/bar
    $ vi src/main/foo/bar/HelloWorld.java


write your code in HelloWorld.java


    package foo.bar;

    import org.apache.log4j.Logger;
    import java.io.*;
    import java.util.*;


    public class HelloWorld {
        private static final Logger log = Logger.getLogger(HelloWorld.class);

        public boolean holaMundo() throws IOException {
            Properties conf = loadConfig();

            String name = conf.getProperty("your.name");

            if ((name == null) || name.isEmpty()) {
                throw new IOException("required config missing, 'your.name'");
            }

            System.out.printf("Pronto %s%n", name);

            return true;
        }

        Properties loadConfig() throws IOException {
            String configfile = System.getProperty("configfile");

            if (configfile == null) {
                throw new IOException("no configfile");
            }

            log.info("configfile=" + configfile);

            Properties p = new Properties();
            FileReader fr = null;

            try {
                fr = new FileReader(configfile);
                p.load(fr);
            } finally {
                if (fr != null) {
                    fr.close();
                }
            }

            return p;
        }
    }


create a test case
------------------

write junit testcase for it, make sure to use <ClassName>Test pattern as it will used in build.xml

    $ mkdir src/test/foo/bar
    $ vi src/test/foo/bar/HelloWorldTest.java

  write your junit testcase in TestHelloWorld.java. We are writing junit4 testcase.

    package foo.bar;

    import static org.junit.Assert.*;

    import org.junit.Test;
    public class HelloWorldTest {
        @Test
        public void testBonjourToutLeMonde() throws Exception {
            // test function name can be anything
            // @Test annotation makes it a test fuction

            HelloWorld hw = new HelloWorld();
            boolean ok = hw.holaMundo();
            assertTrue("Hellow world failed", ok);
        }
    }


ant build.xml
-------------

    $ vi build.xml

Write a build.xml that builds main and test source code and run junit testcase.

    <project name="helloworld-proj" default="compile.all" basedir=".">

      <target name="init">
        <tstamp/>

        <property name="main.src.dir"     value="src/main"/>
        <property name="test.src.dir"     value="src/test"/>
        <property name="main.build.dir"   value="build/main"/>
        <property name="test.build.dir"   value="build/test"/>
        <property name="lib.dir"          value="lib"/>
        <property name="dist.dir"         value="dist"/>
        <property name="junit.report.dir" value="test_report"/>


        <property name="compile.debug"       value="true"/>
        <property name="compile.optimize"    value="true"/>
        <property name="compile.target"      value="1.6" />
        <property name="compile.source"      value="1.6" />
        <property name="compile.deprecation" value="true"/>

        <path id="main.compile.classpath">
          <!--
          <fileset dir="${lib.dir}">
            <include name="*.jar" />
          </fileset>
          -->
          <pathelement location="${lib.dir}/log4j-1.2.8.jar" />
        </path>

        <path id="test.compile.classpath">
          <path refid="main.compile.classpath"/>
          <!--
          <fileset dir="${lib.dir}">
            <include name="*.jar" />
          </fileset>
          -->
          <pathelement location="${lib.dir}/junit-4.10.jar" />
          <pathelement location="${main.build.dir}"/>
        </path>

        <path id="testrun.classpath">
          <path refid="test.compile.classpath"/>
          <pathelement location="${test.build.dir}"/>
        </path>

      </target>

      <target name="prepare" depends="init">
        <mkdir dir="${main.build.dir}"/>
        <mkdir dir="${test.build.dir}"/>
      </target>

      <target name="compile.main" depends="prepare" description="Compiles the main source code">
        <javac srcdir           ="${main.src.dir}"
               destdir          ="${main.build.dir}"
               debug            ="${compile.debug}"
               optimize         ="${compile.optimize}"
               target           ="${compile.target}"
               source           ="${compile.source}"
               deprecation      ="${compile.deprecation}"
               includeAntRuntime="false">
            <compilerarg value="-Xlint:all"/>
            <classpath refid="main.compile.classpath"/>
        </javac>
      </target>

      <target name="compile.test" depends="compile.main" description="Compiles the test source code">
        <javac srcdir           ="${test.src.dir}"
               destdir          ="${test.build.dir}"
               debug            ="${compile.debug}"
               optimize         ="${compile.optimize}"
               target           ="${compile.target}"
               source           ="${compile.source}"
               deprecation      ="${compile.deprecation}"
               includeAntRuntime="false">
            <compilerarg value="-Xlint:all"/>
            <classpath refid="test.compile.classpath"/>
        </javac>
      </target>

      <target name="compile.all" depends="compile.test" description="Compiles all source code"/>

      <target name="junit" depends="compile.test" description="runs junit testcases">
        <mkdir dir="${junit.report.dir}"/>
        <junit printsummary="yes" haltonfailure="yes" showoutput="yes">
          <classpath refid="testrun.classpath"/>
          <formatter type="xml" usefile="true"/> <!-- make usefile="false" if you want to see output and not just summary -->
          <batchtest fork="true"
                    todir="${junit.report.dir}"> <!-- details of the test will send to Test-classname.txt file in this dir -->
            <!-- make sure your testcases are written in <ClassName>Test format-->
            <fileset dir="${test.src.dir}" includes="**/*Test.java"/>
          </batchtest>
        </junit>
      </target>

      <target name="dist" depends="compile.main" description="generate the distribution" >
        <jar jarfile="${dist.dir}/${ant.project.name}.jar">
           <fileset dir="${main.build.dir}"/>
           <!-- <fileset dir="${src.dir}"/> uncomment if you want to distribute source with jar file -->
           <!-- <manifest>
              <attribute name="Main-Class" value="${main.class}"/>
           </manifest>
         -->
        </jar>
      </target>

      <target name="clean" depends="init" description="clean up" >
        <delete dir="${main.build.dir}"/>
        <delete dir="${test.build.dir}"/>
      </target>

    </project>

create config file
------------------

    $ cat config/helloworld-proj.conf
    your.name=Asia Argento


build and test
--------------

    $ ant -projecthelp
    Main targets:

     clean         clean up
     compile.all   Compiles all source code
     compile.main  Compiles the main source code
     compile.test  Compiles the test source code
     dist          generate the distribution
     junit         runs junit testcases
    Default target: compile.all


    $ ant

    init:

    prepare:
        [mkdir] Created dir: /helloworld-proj/build/main
        [mkdir] Created dir: /helloworld-proj/build/test

    compile.main:
        [javac] Compiling 1 source file to /helloworld-proj/build/main

    compile.test:
        [javac] Compiling 1 source file to /helloworld-proj/build/test

    compile.all:

    BUILD SUCCESSFUL
    Total time: 0 seconds


    $ ant junit

    init:

    prepare:

    compile.main:

    compile.test:

    junit:
        [mkdir] Created dir: /helloworld-proj/test_report
        [junit] Running foo.bar.HelloWorldTest
        [junit] Tests run: 1, Failures: 0, Errors: 1, Time elapsed: 0.073 sec

    BUILD FAILED
    /helloworld-proj/build.xml:85: Test foo.bar.HelloWorldTest failed

    Total time: 1 second

    $ cat test_report/TEST-foo.bar.HelloWorldTest.xml|
    <testcase classname="foo.bar.HelloWorldTest" name="testBonjourToutLeMonde" time="0.015">
      <error message="no configfile" type="java.io.IOException">java.io.IOException: no configfile
          at foo.bar.HelloWorld.loadConfig(HelloWorld.java:29)
          at foo.bar.HelloWorld.holaMundo(HelloWorld.java:12)
          at foo.bar.HelloWorldTest.testBonjourToutLeMonde(HelloWorldTest.java:13)

We will have to cofigfile system property, update build.xml to add sysproperty to junit target

    <target name="junit" depends="compile.test" description="runs junit testcases">
      <mkdir dir="${junit.report.dir}"/>
      <junit printsummary="yes" haltonfailure="yes" showoutput="yes">
        <sysproperty key="configfile" value="${basedir}/config/helloworld-proj.conf"/>
        <sysproperty key=" log4j.configuration" value="file:///${basedir}/config/helloworld-proj.log4j"/>
        <sysproperty key=" log4j.debug" value="false"/> <!-- make it true to dig in log4j -->
        <classpath refid="testrun.classpath"/>
        <formatter type="xml" usefile="true"/> <!-- make usefile="false" if you want to see output and not just summary -->
        <batchtest fork="true"
                  todir="${junit.report.dir}"> <!-- details of the test will send to Test-classname.txt file in this dir -->
          <!-- make sure your testcases are written in <ClassName>Test format-->
          <fileset dir="${test.src.dir}" includes="**/*Test.java"/>
        </batchtest>
      </junit>
    </target>



    $ ant junit
    init:

    prepare:

    compile.main:

    compile.test:

    junit:
        [junit] Running foo.bar.HelloWorldTest
        [junit] Pronto Asia Argento
        [junit] Tests run: 1, Failures: 0, Errors: 0, Time elapsed: 0.102 sec

    BUILD SUCCESSFUL
    Total time: 1 second


finally we will have dir struct like this
-----------------------------------------

      |   build.xml
      |
      +---bin
      +---build
      |   +---main
      |   |   \---foo
      |   |       \---bar
      |   |               HelloWorld.class
      |   |
      |   \---test
      |       \---foo
      |           \---bar
      |                   HelloWorldTest.class
      |
      +---config
      |       helloworld-proj.conf
      |       helloworld-proj.log4j
      |
      +---lib
      |       junit-4.10.jar
      |       log4j-1.2.8.jar
      |
      +---src
      |   +---main
      |   |   \---foo
      |   |       \---bar
      |   |               HelloWorld.java
      |   |
      |   \---test
      |       \---foo
      |           \---bar
      |                   HelloWorldTest.java
      |
      \---test_report
              TEST-foo.bar.HelloWorldTest.xml
