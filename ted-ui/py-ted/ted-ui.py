#!/usr/bin/env python

import logging
import code
import readline
import shlex

try:
    from thrift.protocol import TBinaryProtocol
    from thrift.transport import TTransport
    from thrift.transport.TTransport import TBufferedTransport, TTransportException
    from thrift.transport.TSocket import TSocket
except ImportError:
    print "Thrift not in PYTHONPATH, perhaps you want: export PYTHONPATH=$PYTHONPATH:<location to thrift"
    exit(-1)

try:
    from ted.ttypes import *
    import ted.TedService
    from ted.TedService import Iface
except ImportError:
    print "Ted not in PYTHONPATH, perhaps you want: export PYTHONPATH=$PYTHONPATH:<location>/ted/ted-api/target/generated-sources/gen-py/"
    exit(-2)

class TedUI():

    def __init__(self):
        self.connected = False
        readline.parse_and_bind('tab: complete')
        readline.set_completer(self.tab_complete)
        readline.parse_and_bind('set bell-style none')
        readline.set_completer_delims(' ')

        # Get all non-private functions
        self.funs = [x for x in Iface.__dict__.keys() if not x[0] == '_']
        self.funs.sort()

        self.builtins = {}
        self.builtins['connect'] = self.connect
        self.builtins['help'] = self.help

    def tab_complete(self, text, index):
        matches = [word for word in self.funs if word.startswith(text)]
        try:
            return matches[index]
        except IndexError:
            return None

    def _show_commands(self):
        print "Commands"
        print "--------"
        keys = [k for k in self.builtins if not k[0] == '_']
        keys.sort()
        for key in keys:
            print key
        print "--------"

    def connect(self, *args):
        self.transport = TBufferedTransport(TSocket('localhost', 9030))
        self.transport.open()
        protocol = TBinaryProtocol.TBinaryProtocol(self.transport)

        self.client = ted.TedService.Client(protocol)
        self.connected = True

        for fun in self.funs:
            self.builtins[fun] = getattr(self.client, fun)

        del self.builtins['connect']
        self.builtins['disconnect'] = self.disconnect

    def disconnect(self, *args):
        for fun in self.funs:
            del self.builtins[fun]
        self.client.logout()
        self.transport.close()
        self.client = None
        self.connected = False

        del self.builtins['disconnect']
        self.builtins['connect'] = self.connect

    def help(self, *args):
        self._show_commands()

    def main(self):

        logger = logging.getLogger('ted')
        logger.addHandler(logging.StreamHandler())


        shell = code.InteractiveConsole(self.builtins)
        shell.interact("Welcome to ted. Type 'help()' for a list of commands")

        if self.connected:
            self.disconnect()

if __name__ == "__main__":
	TedUI().main()
