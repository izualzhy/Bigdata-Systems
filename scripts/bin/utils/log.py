#!/usr/bin/env python
# coding=utf-8


import logging
import logging.handlers
import sys
import time

try:
    import curses
except ImportError:
    curses = None


def _stderr_supports_color():
    color = False
    if curses and sys.stderr.isatty():
        try:
            curses.setupterm()
            if curses.tigetnum("colors") > 0:
                color = True
        except Exception:
            pass
    return color


def _unicode(value):
    """Converts a string argument to a unicode string.

    If the argument is already a unicode string or None, it is returned
    unchanged.  Otherwise it must be a byte string and is decoded as utf8.
    """
    # if isinstance(value, unicode):
    #     return value.decode('utf-8')
    return value


class LogFormatter(logging.Formatter):
    """Log formatter used in Tornado.

    Key features of this formatter are:

    * Color support when logging to a terminal that supports it.
    * Timestamps on every log line.
    * Robust against str/bytes encoding problems.

    This formatter is enabled automatically by
    `tornado.options.parse_command_line` (unless ``--logging=none`` is
    used).
    """
    def __init__(self, color=True, *args, **kwargs):
        logging.Formatter.__init__(self, *args, **kwargs)
        self._color = color and _stderr_supports_color()
        if self._color:
            # The curses module has some str/bytes confusion in
            # python3.  Until version 3.2.3, most methods return
            # bytes, but only accept strings.  In addition, we want to
            # output these strings with the logging module, which
            # works with unicode strings.  The explicit calls to
            # unicode() below are harmless in python2 but will do the
            # right conversion in python 3.
            fg_color = (curses.tigetstr("setaf") or
                        curses.tigetstr("setf") or "")
            self._colors = {
                logging.DEBUG: curses.tparm(fg_color, 4),  # Blue
                logging.INFO: curses.tparm(fg_color, 2),  # Green
                logging.WARNING: curses.tparm(fg_color, 3),  # Yellow
                logging.ERROR: curses.tparm(fg_color, 1),  # Red
            }
            self._normal = curses.tigetstr("sgr0")

    def format(self, record):
        try:
            record.message = record.getMessage()
        except Exception as e:
            record.message = "Bad message (%r): %r" % (e, record.__dict__)
#        assert isinstance(record.message, str)  # guaranteed by logging
        record.asctime = time.strftime(
            "%y%m%d %H:%M:%S", self.converter(record.created))
        prefix = '[%(levelname)1.1s %(asctime)s T%(thread)d %(module)s:%(lineno)d]' % \
            record.__dict__
        if self._color:
            prefix = (self._colors.get(record.levelno, self._normal) +
                      prefix + self._normal)

        # Encoding notes:  The logging module prefers to work with character
        # strings, but only enforces that log messages are instances of
        # basestring.  In python 2, non-ascii bytestrings will make
        # their way through the logging framework until they blow up with
        # an unhelpful decoding error (with this formatter it happens
        # when we attach the prefix, but there are other opportunities for
        # exceptions further along in the framework).
        #
        # If a byte string makes it this far, convert it to unicode to
        # ensure it will make it out to the logs.  Use repr() as a fallback
        # to ensure that all byte strings can be converted successfully,
        # but don't do it by default so we don't add extra quotes to ascii
        # bytestrings.  This is a bit of a hacky place to do this, but
        # it's worth it since the encoding errors that would otherwise
        # result are so useless (and tornado is fond of using utf8-encoded
        # byte strings whereever possible).
        def safe_unicode(s):
            try:
                return _unicode(s)
            except UnicodeDecodeError:
                return repr(s)

        formatted = prefix + " " + safe_unicode(record.message)
        if record.exc_info:
            if not record.exc_text:
                record.exc_text = self.formatException(record.exc_info)
        if record.exc_text:
            # exc_text contains multiple lines.  We need to safe_unicode
            # each line separately so that non-utf8 bytes don't cause
            # all the newlines to turn into '\n'.
            lines = [formatted.rstrip()]
            lines.extend(safe_unicode(ln) for ln in record.exc_text.split('\n'))
            formatted = '\n'.join(lines)
        return formatted.replace("\n", "\n    ")


def enable_pretty_logging(setting = None):
    """Turns on formatted logging output as configured.
    """
    logger = logging.getLogger()
    try:
        logger.setLevel(getattr(logging, setting['logging'].upper()))
    except:
        logger.setLevel(logging.DEBUG)

    channel = None
    if None != setting and 'cut_type' in setting and setting['cut_type'] == 'Timing':
        channel = logging.handlers.TimedRotatingFileHandler(
            filename = setting['log_file_prefix'],
            backupCount = setting['log_file_num_backups'],
            when = setting['when'])
        if 'startat' in setting:
            if not isinstance(setting['startat'], int):
                raise Exception('setting.log_param.startat must be int. curr:%s' % \
                        setting['startat'].__class__.__name__)
            channel.rolloverAt = setting['startat']
#        if 'suffix' in setting:
#            channel.suffix = setting['suffix']
#        if 'extMatch' in setting:
#            channel.extMatch = setting['extMatch']
    elif None != setting:
        channel = logging.handlers.RotatingFileHandler(
            filename = setting['log_file_prefix'],
            maxBytes = setting['log_file_max_size'],
            backupCount = setting['log_file_num_backups'])
    if None != channel:
        channel.setFormatter(LogFormatter(color=False))
        logger.addHandler(channel)

    if None == setting or (('log_to_stderr' in setting) and setting['log_to_stderr']):
        # Set up color if we are in a tty and curses is installed
        channel = logging.StreamHandler()
        channel.setFormatter(LogFormatter())
        logger.addHandler(channel)

class Logger(object):
    def __init__(self):
        self.log_str = ""

    def logging(self, **kwargs):
        for k, v in kwargs.items():
            self.log_str += "".join(["[", str(k), ":", str(v), "]"])

    def getlog(self):
        return self.log_str

    def writelog(self):
        logging.info(self.log_str)
