package de.slab.testsmtp.mailhandler;

import de.slab.testsmtp.DevelopmentSMTP;
import de.slab.testsmtp.MBoxAppender;

import java.io.ByteArrayInputStream;

public class MBoxStdoutAppendHandler implements MailHandler{

  @Override
  public void handleMail(byte[] data) throws Exception {
    MBoxAppender.writeMBox(new ByteArrayInputStream(data), System.out, DevelopmentSMTP.FROM_EMAIL);
  }

  @Override
  public String getTargetDescription() {
    return "stdout";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return obj.getClass() == getClass();
  }
}
