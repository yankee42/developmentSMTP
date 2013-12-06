package de.slab.testsmtp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MBoxAppender {
  public static void writeMBox(InputStream data, Appendable mbox, String from) throws IOException {
    mbox.append("From ").append(from).append(" ").append(mboxFormatedDate()).append(String.valueOf('\n'));
    final BufferedReader buffered = new BufferedReader(new InputStreamReader(data));
    String line;
    boolean inHeaders = true;
    while ((line = buffered.readLine()) != null) {
      if (!inHeaders && line.startsWith("From ")) {
        // RFC 4155 explicitly does not specify how to escape line beginning with "From ".
        // However postfix escapes these lines by prepending a '>'.
        // Unfortunately Thunderbird does not understand this escape sequence.
        mbox.append('>');
      }
      if (line.isEmpty()) {
        inHeaders = false;
      }
      mbox.append(line);
      mbox.append('\n');
    }
    mbox.append('\n'); // RFC 4155: Each message in the database MUST be terminated by an empty line, containing a single end-of-line marker
  }

  /**
   * <p>returns the current time formatted according to RFC 4155 Appendix A:</p>
   *
   * <p>a timestamp indicating the UTC date and time [..], conformant with the syntax of the
   * traditional UNIX 'ctime' output sans timezone (note that the
   * use of UTC precludes the need for a timezone indicator);</p>

   * @return formatted timestamp
   */
  private static String mboxFormatedDate() {
    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy"); //e.g. Mon Nov 26 16:33:50 2012
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    return formatter.format(new Date());
  }
}
