package nu.ted.client;

import nu.ted.generated.TedService.Client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class JavaClient {

	private Client client;
	private TTransport transport;

	public JavaClient(String host, int port) {
		transport = new TSocket(host, port);
		TProtocol prot = new TBinaryProtocol(transport);
		client = new Client(prot);
	}
	
	public void run(ClientAction action) {
		try {
			transport.open();
			action.run(client);
		}
		catch (TException e) {
			e.printStackTrace();
		}
		finally {
			transport.close();
		}
	}
}
