
compile:
	javac -d build src/**/*.java 

start-rmi:
	cd build && rmiregistry &

run-server:
	cd build && java server.RMIServer  

run-client:
	cd build && java client.RMIClient
