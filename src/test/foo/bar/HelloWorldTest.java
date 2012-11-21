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
