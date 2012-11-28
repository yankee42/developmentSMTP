developmentSMTP
===============

DevelopmentSMTP is a simple, 100% pure Java SMTP server that can be used during development to catch all emails and
forward or store them.

It was created to help with testing using real world data with real world email addresses. Emails should not really be
sent, but instead they should be caught and it should be possible to check which emails have been sent.

usage
-----

    $ java -jar DevelopmentSMTPServer.jar --help
    usage: DevelopmentSMTP
    Listens for SMTP connections and handles all emails, regardless of header
    or content using the configuration supplied by command line arguments
     -c,--http-port <arg>   Starts a http configuration server on this port.
     -f,--forward <arg>     Forward emails to this address. May be specified
                            multiple times.
     -h,--help              This page
     -o,--output <arg>      file to write to. Use '-' for stdout. May be
                            specified multiple times.
     -p,--port <arg>        port to listen on. Defaults to 25
     -s,--smtp-host <arg>   Use the following SMTP server for the forward mail
                            handler. Defaults to 'mail'

Example:

    java -jar DevelopmentSMTPServer.jar --forward myname@mydomain.tld --forward othername@somedomain.tld --output -

This starts the SMTP Server, listening for incoming connections on port 25, forwarding alle emails to
myname@mydomain.tld and othername@somedomain.tld and additionally echoing all emails in stdout in mbox format.

Reading the mbox format
-----------------------

You can use a capable tool to read the mbox format. E.g. [Mutt][Mutt] is capable of displaying
the mbox file if you call Mutt with the -f option:

    mutt -f /path/to/your/mbox/file

Thunderbird supports mbox as well (at least on Linux). To read emails with thunderbird got to
`Edit->Account Settings->Account Actions->Add other account->Unix Mailspool (Movemail)` and click next, filling in any
required options with any text that satisfies Thunderbird. It does not make any difference what you use.

Not that Thunderbird searches you mbox file at /var/mail/\`whoami`. Additionally automatic retrieval of mails does not
work in Thunderbird: You need to press "Get Mail" for new mails to show up.

The HTTP configuration server
-----------------------------

developmentSMTP ships with a small web server based on [NanoHTTPD](http://elonen.iki.fi/code/nanohttpd/) that allows you
to reconfigure the server during runtime using a minimal http interface. Just supply the `--http-port` option to enable
this service.

A word of warning
-----------------

developmentSMTP is supplied in the hope that it is useful, but without any guarantee. It is not tested for multi
threading issues which would be extremely unlikely in a development environment. Never use developmentSMTP in a
production environment!

  [Mutt]: http://en.wikipedia.org/wiki/Mutt_(e-mail_client)