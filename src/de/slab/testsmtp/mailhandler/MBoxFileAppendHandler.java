package de.slab.testsmtp.mailhandler;

import de.slab.testsmtp.DevelopmentSMTP;
import de.slab.testsmtp.MBoxAppender;
import de.slab.testsmtp.RandomAccessFileAppender;

import java.io.ByteArrayInputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class MBoxFileAppendHandler implements MailHandler {

  private final String mboxFileName;

  public MBoxFileAppendHandler(final String mboxFileName) {
    this.mboxFileName = mboxFileName;
  }

  @Override
  public void handleMail(final byte[] data) throws Exception {
    final RandomAccessFile mbox = new RandomAccessFile(mboxFileName, "rw");
    final FileLock lock = mbox.getChannel().lock();
    mbox.seek(mbox.length());

    MBoxAppender.writeMBox(
        new ByteArrayInputStream(data),
        new RandomAccessFileAppender(mbox),
        DevelopmentSMTP.FROM_EMAIL);

    lock.release();
    mbox.close();
  }

  @Override
  public String getTargetDescription() {
    return "file:" + mboxFileName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MBoxFileAppendHandler that = (MBoxFileAppendHandler) o;

    return !(mboxFileName != null ? !mboxFileName.equals(that.mboxFileName) : that.mboxFileName != null);
  }

  @Override
  public int hashCode() {
    return mboxFileName != null ? mboxFileName.hashCode() : 0;
  }
}
