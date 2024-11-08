##1. Overview :
This protocol is a client-server protocol. The client connects to a server and asks the solution to a simple mathematical equation. The server then computes the answer and sends it to the client, or sends an error message.

##2. Transport layer protocol :
This protocol uses TCP. The client establishes the connection. It has to know the server's ip address. The server listens on TCP port 9876. The client closes the connection when it doesn't need to use it anymore.

##3. Messages :

	*ASK <equation> : 
		The client requests an answer. Possible operators include :
			- +
			- -
			- *
			- /
		The server should also be able to consider parenthesis <()>
	
	*ERROR :
		The equation has no solution, a division by 0 occurs, the expression is malformed, a character is not recognized...
		Multiple error messages should allow the client to determine which type of error occured.
		- "E0" for a division by 0 error
		- "E1" for malformed expression
		- "E2" for unknown character

	*RES :
		Message used by the server to send the result. 

	*EXIT :
		Whenever the user wants to quit the client app, the client sends this message to the server. The server responds
		with a goodbye message and the connection in closed. 

	

	All messages only use ASCII characters encoded in UTF-8. They all use '\n' as end-of-line character. 

##4. Error handling :

	Only two kinds of errors are handled : 
		* Input error : the client sends an unknown character or the expression doesn't conform to standard syntax.
		* Mathematical errors : Either expression has no solution, or division by zero occurs. 

##5. Example dialogs :

	1. ASK 1 + 2 //->
	2. <-// RES 3

	1. ASK 25 / 0 //->
	2. <-// E0

	1. ASK p!{l //->
	2. <-// E1

