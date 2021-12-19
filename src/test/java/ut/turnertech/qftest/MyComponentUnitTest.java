package ut.turnertech.qftest;

import org.junit.Test;
import turnertech.qftest.api.MyPluginComponent;
import turnertech.qftest.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}