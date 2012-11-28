package de.slab.testsmtp.www;

import de.slab.testsmtp.DevelopmentSMTP;
import de.slab.testsmtp.mailhandler.ForwardMailHandler;
import de.slab.testsmtp.mailhandler.MBoxFileAppendHandler;
import de.slab.testsmtp.mailhandler.MBoxStdoutAppendHandler;
import de.slab.testsmtp.mailhandler.MailHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class HTTPInterface extends NanoHTTPD{
  private final DevelopmentSMTP developmentSMTP;

  public HTTPInterface(int port, DevelopmentSMTP developmentSMTP) throws IOException {
    super(port, null);
    this.developmentSMTP = developmentSMTP;
  }

  @Override
  public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
    final Response response = serveWithoutCacheHeader(parms);
    response.addHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "no-cache, must-revalidate");
    response.addHeader("Expires", "Sat, 26 Jul 1997 05:00:00 GMT");
    return response;
  }

  private Response serveWithoutCacheHeader(Properties parms) {
    try {
      if (parms.containsKey("delete")) {
        return delete(Integer.parseInt((String) parms.get("delete")));
      }
      else if (parms.containsKey("create")) {
        create((String)parms.get("create"), (String) parms.get("target"), (String) parms.get("SMTPServer"));
        return new Response(NanoHTTPD.HTTP_OK, "text/plain", "Mail handler created. Use the back button of your browser.");
      }
      else {
        return configurationForm();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(NanoHTTPD.HTTP_INTERNALERROR, "text/plain", "Sorry, an internal error occured. Message=" + e.getMessage());
    }
  }

  private void create(String type, String target, String smtpServer) {
    if ("stdout".equals(type)) {
      developmentSMTP.addHandler(new MBoxStdoutAppendHandler());
    } else if ("file".equals(type)) {
      developmentSMTP.addHandler(new MBoxFileAppendHandler(target));
    } else if ("forward".equals(type)) {
      if (smtpServer == null || smtpServer.trim().isEmpty()) {
        smtpServer = "mail";
      }
      developmentSMTP.addHandler(new ForwardMailHandler(target, smtpServer));
    } else {
      throw new IllegalArgumentException("Unknown type: " + type);
    }
  }

  private Response delete(final int deleteId) {
    Iterator<MailHandler> i = developmentSMTP.getHandlers().iterator();
    while(i.hasNext()) {
      if (System.identityHashCode(i.next()) == deleteId) {
        i.remove();
        return new Response(NanoHTTPD.HTTP_OK, "text/plain", "Mail Handler deleted. Use the back-button of your browser");
      }
    }
    return new Response(NanoHTTPD.HTTP_OK, "text/plain", "No handler with the supplied ID found. Use the back button of your browser to go back.");
  }

  private Response configurationForm() throws Exception{
    final byte[] form = createConfigurationForm();
    final Response response = new Response(NanoHTTPD.HTTP_OK, "application/xhtml+xml", new ByteArrayInputStream(form));
    response.addHeader("Content-Length", Integer.toString(form.length));
    return response;
  }

  private byte[] createConfigurationForm() throws Exception {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    new ConfigurationPageBuilder(out, developmentSMTP).run();
    return out.toByteArray();
  }
}
