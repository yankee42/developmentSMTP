package de.slab.testsmtp;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileAppender implements Appendable {

  private final RandomAccessFile mbox;

  public RandomAccessFileAppender(RandomAccessFile mbox) {
    this.mbox = mbox;
  }

  @Override
  public Appendable append(CharSequence charSequence) throws IOException {
    mbox.writeBytes(charSequence.toString());
    return this;
  }

  @Override
  public Appendable append(CharSequence charSequence, int start, int end) throws IOException {
    mbox.writeBytes(charSequence.subSequence(start, end).toString());
    return this;
  }

  @Override
  public Appendable append(char c) throws IOException {
    mbox.writeByte(c);
    return this;
  }
}
