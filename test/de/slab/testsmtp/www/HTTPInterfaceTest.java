package de.slab.testsmtp.www;

import de.slab.testsmtp.DevelopmentSMTP;
import de.slab.testsmtp.mailhandler.ForwardMailHandler;
import de.slab.testsmtp.mailhandler.MBoxFileAppendHandler;
import de.slab.testsmtp.mailhandler.MBoxStdoutAppendHandler;
import de.slab.testsmtp.mailhandler.MailHandler;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

public class HTTPInterfaceTest {
  private static final Properties NO_HEADERS = null;
  private static final Properties NO_FILES = null;
  
  private DevelopmentSMTP smtpServer;
  private HTTPInterface httpInterface;

  @BeforeMethod
  public void setUp() throws Exception {
    smtpServer = new DevelopmentSMTP(1678);
    httpInterface = new HTTPInterface(1677, smtpServer);
  }

  @AfterMethod
  public void tearDown() throws Exception {
    httpInterface.stop();
    smtpServer.shutdown();
  }

  @Test
  public void testCreateStdout() throws Exception {
    httpInterface.serve("", "POST", NO_HEADERS, createParameters("stdout", "", ""), NO_FILES);
    assertEquals(smtpServer.getHandlers(), Arrays.asList(new MBoxStdoutAppendHandler()));
  }

  @Test
  public void testCreateFile() throws Exception {
    httpInterface.serve("", "POST", NO_HEADERS, createParameters("file", "/path/to/file", ""), NO_FILES);
    assertEquals(smtpServer.getHandlers(), Arrays.asList(new MBoxFileAppendHandler("/path/to/file")));
  }

  @Test
  public void testCreateForward() throws Exception {
    httpInterface.serve("", "POST", NO_HEADERS, createParameters("forward", "foo@bar.com", ""), NO_FILES);
    httpInterface.serve("", "POST", NO_HEADERS, createParameters("forward", "foo2@bar.com", "someServer"), NO_FILES);
    assertEquals(smtpServer.getHandlers(), Arrays.asList(new ForwardMailHandler("foo@bar.com", "mail"), new ForwardMailHandler("foo2@bar.com", "someServer")));
  }

  @Test
  public void testDelete() throws Exception {
    final MailHandler[] handlers = new MailHandler[]{new ForwardMailHandler("a", "b"), new MBoxFileAppendHandler("x"), new MBoxStdoutAppendHandler()};
    for(MailHandler handler : handlers) {
      smtpServer.addHandler(handler);
    }
    httpInterface.serve("", "GET", NO_HEADERS, deleteParameters(Integer.toString(System.identityHashCode(handlers[1]))), NO_FILES);
    assertEquals(smtpServer.getHandlers(), Arrays.asList(handlers[0], handlers[2]));
  }

  private Properties deleteParameters(String hashCode) {
    final Properties result = new Properties();
    result.put("delete", hashCode);
    return result;
  }

  private Properties createParameters(String create, String target, String smtpServer) {
    final Properties result = new Properties();
    result.put("create", create);
    result.put("target", target);
    result.put("SMTPServer", smtpServer);
    return result;
  }
}
