package de.slab.testsmtp.mailhandler;

public interface MailHandler {
  public void handleMail(byte[] data) throws Exception;
  public String getTargetDescription();
}
