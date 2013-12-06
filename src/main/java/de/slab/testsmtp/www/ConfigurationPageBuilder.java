package de.slab.testsmtp.www;

import de.slab.testsmtp.DevelopmentSMTP;
import de.slab.testsmtp.mailhandler.MailHandler;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import java.io.OutputStream;

public class ConfigurationPageBuilder implements Runnable {
  private final XMLStreamWriter out;
  private final DevelopmentSMTP developmentSMTP;

  public ConfigurationPageBuilder(OutputStream out, DevelopmentSMTP developmentSMTP) {
    try {
		this.out = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
	} catch (XMLStreamException | FactoryConfigurationError e) {
		throw new Error(e);
	}
    this.developmentSMTP = developmentSMTP;
  }

  @Override
  public void run() {
    try {
      writeHeader();
      writeConfigurationTable();
      writeNewHandlerForm();
      writeFooter();
      out.close();
    } catch (XMLStreamException e) {
      e.printStackTrace();
    }
  }

  private void writeNewHandlerForm() throws XMLStreamException {
    writeSimpleElement("h1", "Add handler");
    out.writeStartElement("form");
    out.writeStartElement("p");
    writeNewHandlerFormTable();
    writeSubmitButton("stdout");
    writeSubmitButton("forward");
    writeSubmitButton("file");
    out.writeEndElement(); // p
    out.writeEndElement(); // form
  }

  private void writeNewHandlerFormTable() throws XMLStreamException {
    out.writeStartElement("table");
    out.writeStartElement("tr");

    writeSimpleElement("td", "Target (email address for forward, file path for file, ignored for stdout):");

    out.writeStartElement("td");
    writeInput("text", "target");
    out.writeEndElement(); // td
    out.writeEndElement(); // tr
    out.writeStartElement("tr");
    writeSimpleElement("td", "SMTP Server (only for forward):");
    out.writeStartElement("td");
    writeInput("text", "SMTPServer");
    out.writeEndElement(); // td
    out.writeEndElement(); // tr
    out.writeEndElement(); // table
  }

  private void writeInput(String type, String name) throws XMLStreamException {
    out.writeStartElement("input");
    out.writeAttribute("type", type);
    out.writeAttribute("name", name);
    out.writeEndElement();
  }

  private void writeSubmitButton(final String name) throws XMLStreamException {
    out.writeStartElement("button");
    out.writeAttribute("type", "submit");
    out.writeAttribute("name", "create");
    out.writeAttribute("value", name);
    out.writeCharacters("Create " + name);
    out.writeEndElement();
  }

  private void writeConfigurationTable() throws XMLStreamException {
    writeSimpleElement("h1", "Current configuration");
    out.writeStartElement("table");
    out.writeAttribute("border", "1");
    out.writeStartElement("tr");

    writeSimpleElement("th", "handler");
    writeSimpleElement("th", "target");
    writeSimpleElement("th", "action");

    out.writeEndElement(); // tr

    for(MailHandler handler : developmentSMTP.getHandlers()) {
      generateRow(handler);
    }
    out.writeEndElement(); // table
  }

  private void writeSimpleElement(final String tagName, final String content) throws XMLStreamException {
    out.writeStartElement(tagName);
    out.writeCharacters(content);
    out.writeEndElement();
  }

  private void writeFooter() throws XMLStreamException {
    out.writeEndElement(); // body
    out.writeEndElement(); // html
    out.writeEndDocument();
  }

  private void writeHeader() throws XMLStreamException {
    out.writeStartDocument();
    out.writeDTD("<!DOCTYPE html>");
    out.writeStartElement("html");
    out.writeAttribute("xmlns", "http://www.w3.org/1999/xhtml");
    out.writeStartElement("head");
    out.writeStartElement("title");
    out.writeCharacters("DevelopmentSMTP configuration");
    out.writeEndElement(); // title
    out.writeEndElement(); // head
    out.writeStartElement("body");
    writeSimpleElement("h1", "DevelopmentSMTP configuration");
    writeSimpleElement("p", "This is some really basic mechanism to reconfigure the development server while it is running. It is not thread-safe and could loose a mail if you reconfigure in the same millisecond an email is received.");
    writeSimpleElement("p", "If you change something, go back to the current configuration table and wonder that nothing has changed: Your browser has cached this page. Just reload.");
  }

  private void generateRow(MailHandler mailHandler) throws XMLStreamException {
    out.writeStartElement("tr");
    out.writeStartElement("td");
    out.writeCharacters(mailHandler.getClass().getSimpleName());
    out.writeEndElement();
    out.writeStartElement("td");
    out.writeCharacters(mailHandler.getTargetDescription());
    out.writeEndElement();
    out.writeStartElement("td");
    writeDeleteLink(mailHandler);
    out.writeEndElement();
    out.writeEndElement();
  }

  private void writeDeleteLink(MailHandler mailHandler) throws XMLStreamException {
    out.writeStartElement("a");
    out.writeAttribute("href", "?delete=" + System.identityHashCode(mailHandler));
    out.writeCharacters("delete");
    out.writeEndElement();
  }
}
