package de.slab.testsmtp;

import de.slab.testsmtp.mailhandler.ForwardMailHandler;
import de.slab.testsmtp.mailhandler.MBoxFileAppendHandler;
import de.slab.testsmtp.mailhandler.MBoxStdoutAppendHandler;
import de.slab.testsmtp.www.HTTPInterface;
import org.apache.commons.cli.*;

public class CLI {
  private DevelopmentSMTP developmentSMTP;

  public CLI(final CommandLine arguments) {
    final int port = Integer.parseInt(arguments.getOptionValue('p', "25"));
    developmentSMTP = new DevelopmentSMTP(port);
    if (arguments.hasOption('f')) {
      parseForwardHandlers(arguments.getOptionValues('f'), arguments.getOptionValue('s', "mail"));
    }
    if (arguments.hasOption('o')) {
      parseOutputHandlers(arguments.getOptionValues('o'));
    }
    if (arguments.hasOption('c')) {
      final int httpPort = Integer.parseInt(arguments.getOptionValue('c'));
      startHTTPInterface(httpPort);
    }
    developmentSMTP.listen();
  }

  private void startHTTPInterface(int httpPort) {
    try {
      new HTTPInterface(httpPort, developmentSMTP);
    } catch (Exception e) {
      System.out.println("Sorry, could not start HTTP interface. Will continue without HTTP interface.");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws ParseException {
    final CommandLineParser parser = new GnuParser();
    final Options options = createCLIOptions();
    final CommandLine commandLine = parser.parse(options, args);
    if (commandLine.hasOption('h')) {
      new HelpFormatter().printHelp("DevelopmentSMTP", "Listens for SMTP connections and handles all emails, regardless of header or content using the configuration supplied by command line arguments", options, "");
      System.exit(0);
    }
    new CLI(commandLine);
  }

  private void parseForwardHandlers(String[] forwardAddresses, String smtpServer) {
    for (final String forwardAddress : forwardAddresses) {
      developmentSMTP.addHandler(new ForwardMailHandler(forwardAddress, smtpServer));
    }
  }

  private void parseOutputHandlers(String[] options) {
    for (final String filename : options) {
      if ("-".equals(filename)) {
        developmentSMTP.addHandler(new MBoxStdoutAppendHandler());
      } else {
        developmentSMTP.addHandler(new MBoxFileAppendHandler(filename));
      }
    }
  }

  private static Options createCLIOptions() {
    final Option portOption = new Option("p", "port", true, "port to listen on. Defaults to 25");
    final Option fileOption = new Option("o", "output", true, "file to write to. Use '-' for stdout. May be specified multiple times.");
    final Option forwardOption = new Option("f", "forward", true, "Forward emails to this address. May be specified multiple times.");
    final Option httpConfigurationOption = new Option("c", "http-port", true, "Starts a http configuration server on this port.");
    final Option smtpHostOption = new Option("s", "smtp-host", true, "Use the following SMTP server for the forward mail handler. Defaults to 'mail'");
    final Option helpOption = new Option("h", "help", false, "This page");
    final Options options = new Options();
    options.addOption(portOption);
    options.addOption(fileOption);
    options.addOption(helpOption);
    options.addOption(httpConfigurationOption);
    options.addOption(forwardOption);
    options.addOption(smtpHostOption);
    return options;
  }
}
