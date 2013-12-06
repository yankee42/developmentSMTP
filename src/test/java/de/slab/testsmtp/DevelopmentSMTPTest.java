package de.slab.testsmtp;

import de.slab.testsmtp.mailhandler.MailHandler;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;

public class DevelopmentSMTPTest {

  private DevelopmentSMTP smtpServer;

  @BeforeMethod
  public void setUp() throws Exception {
    smtpServer = new DevelopmentSMTP(1678);
  }

  @Test(dataProvider = "testDeliverData")
  public void testDeliver(final byte[] dataToDeliver) throws Exception {
    smtpServer.addHandler(new MailHandler() {
      @Override
      public void handleMail(byte[] data) throws Exception {
        Assert.assertEquals(data, dataToDeliver);
      }

      @Override
      public String getTargetDescription() {
        return null;
      }
    });
    smtpServer.deliver("", "", new ByteArrayInputStream(dataToDeliver));
  }

  @DataProvider
  public Object[][] testDeliverData() {
    return new Object[][]{
        {testByteArray(0)},
        {testByteArray(1)},
        {testByteArray(8191)},
        {testByteArray(8192)},
        {testByteArray(8193)},
        {testByteArray(50000)},
    };
  }

  private byte[] testByteArray(int size) {
    final byte[] result = new byte[size];
    for (int i = 0; i < size; ++i) {
      result[i] = (byte) (i % 256);
    }
    return result;
  }
}
