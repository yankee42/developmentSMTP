package de.slab.testsmtp.mailhandler;

import de.slab.testsmtp.DevelopmentSMTP;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class ForwardMailHandler implements MailHandler {

  private final String recipient;
  private final String smtpServer;

  public ForwardMailHandler(String recipient, String smtpServer) {
    this.recipient = recipient;
    this.smtpServer = smtpServer;
  }

  @Override
  public void handleMail(byte[] data) throws Exception {
    Properties props = System.getProperties();
    props.put("mail.smtp.host", smtpServer);
    props.put("mail.transport.protocol", "smtp");

    Session session = Session.getDefaultInstance(props);
    Transport.send(createEmail(data, session));
  }

  private Message createEmail(byte[] data, Session session) throws MessagingException, UnsupportedEncodingException {
    Message msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(DevelopmentSMTP.FROM_EMAIL, ""));
    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient, ""));
    msg.setSubject("forwarded mail from development forwarding mail server");
    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(createEmailBody());
    multipart.addBodyPart(createEmailAttachment(data));
    msg.setContent(multipart);
    msg.saveChanges();
    return msg;
  }

  private MimeBodyPart createEmailAttachment(byte[] data) throws MessagingException {
    MimeBodyPart messageBodyPart = new MimeBodyPart();
    DataSource source = new ByteArrayDataSource(data, "message/rfc822");
    messageBodyPart.setDataHandler(new DataHandler(source));
    messageBodyPart.setFileName("original.eml");
    return messageBodyPart;
  }

  private MimeBodyPart createEmailBody() throws MessagingException {
    MimeBodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setText("Hi!\n\nI just wanted to let you know that I have received an email and I am configured to tell you about it. See the email I received in the attachment.\n\n    --development mail forwarder");
    return messageBodyPart;
  }

  @Override
  public String getTargetDescription() {
    return "email:" + recipient + " over:" + smtpServer;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ForwardMailHandler that = (ForwardMailHandler) o;

    if (recipient != null ? !recipient.equals(that.recipient) : that.recipient != null) {
      return false;
    }
    if (smtpServer != null ? !smtpServer.equals(that.smtpServer) : that.smtpServer != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = recipient != null ? recipient.hashCode() : 0;
    result = 31 * result + (smtpServer != null ? smtpServer.hashCode() : 0);
    return result;
  }
}
