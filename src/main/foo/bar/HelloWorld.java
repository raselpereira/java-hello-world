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
