package de.slab.testsmtp;

import de.slab.testsmtp.mailhandler.MailHandler;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class DevelopmentSMTP implements SimpleMessageListener {

  public static final String FROM_EMAIL = "development-forward@slab.de";

  private final Collection<MailHandler> handlers = new ArrayList<MailHandler>();
  private final SMTPServer smtpServer;

  public DevelopmentSMTP(final int port) {
    smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(this));
    smtpServer.setPort(port);
  }

  public void listen() {
    smtpServer.start();
  }

  @Override
  public boolean accept(String from, String recipient) {
    return true;
  }

  @Override
  public void deliver(String from, String recipient, InputStream data) throws IOException {
    final byte[] email = readFully(data);
    for (final MailHandler handler : handlers) {
      try {
        handler.handleMail(email);
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
    }
  }

  private static byte[] readFully(InputStream data) throws IOException {
    byte[] result = new byte[8192];
    int resultPos = 0;
    int bytesRead;
    while ((bytesRead = data.read(result, resultPos, result.length - resultPos)) != -1) {
      resultPos += bytesRead;
      if (resultPos == result.length) {
        result = Arrays.copyOf(result, result.length + 8192);
      }
    }
    return Arrays.copyOf(result, resultPos);
  }

  public void addHandler(MailHandler handler) {
    handlers.add(handler);
  }

  public Collection<MailHandler> getHandlers() {
    return handlers;
  }

  public void shutdown() {
    smtpServer.stop();
  }
}
