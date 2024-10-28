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
			- ^
		The server should also be able to consider parenthesis <()>

	*EXIT : 
		The client informs the server it doesn't need the connection anymore. The server closes the connection.

	*UNKNOWN <char> :
		The server doesn't recognize a character (either because it is a letter, an unused symbol etc...).
	
	*ERROR :
		The equation has no solution, or a 0 division occurs.

	All messages only use ASCII characters (I think). They all use '\n' as end-of-line character. 

##4. Error handling :

	Only two kinds of errors are handled : 
		* Input error : the client sends an unknown character or the expression doesn't conform to standard syntax.
		* Mathematical errors : Either expression has no solution, or division by zero occurs. 

##5. Example dialogs : 

	1. ASK 1 + 2 //->
	2. <-// 3

	1. ASK 25 / 0 //->
	2. <-// ERROR 

	1. ASK p!{l //->
	2. UNKNOWN p
