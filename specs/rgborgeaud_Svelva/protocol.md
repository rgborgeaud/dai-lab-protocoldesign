##1. Overview :
This protocol is a client-server protocol. The client connects to a server and asks the solution to a simple mathematical equation. The server then computes the answer and sends it to the client, or sends an error message.

##2. Transport layer protocol :
This protocol uses TCP. The client establishes the connection. It has to know the server's ip address. The server listens on TCP port 9876. The client closes the connection on input of the quit token ('q' without quotes).
Regarding the server, the design choice has been taken to have it shut down once all clients have disconnected, although this can be easily swapped around albeit this would require to implement server-side manual shutdown in the same fashion as the clients do (and a reply to all clients that the server is closing the connection).

##3. Messages :

	*ASK <equation> : 
		The client requests an answer. Possible operators include :
			- +
			- -
			- *
			- /
		The server only accepts parenthesised inputs (e.g. (1+2*(3+3)))

    *BYE :
        Message used by the client to signal disconnection from it.

	*RES :
		Message used by the server to send the result. 

    *ERR :
        The server replies with a message in case an exception got thrown during the processing


	All messages use UTF-8 encoding, and '\n' as end-of-line character. 

##4. Error handling :

	Errors are caught in a best-effort possible using general Exception clauses rather than
    complicated specifics (which would not be required in scope of such a simple tool).
    Computation errors from the server are replied and logged for the client in a user-friendly 
    manner by a simple display of the exception's message.
    Those errors are also logged in the server's console. Add to this, each entity's console log
    their own specific issues (I/O errors for example).

##5. Example dialogs :

	1. ASK 1 + 2 //->
	2. <-// RES 3

	1. ASK 25 / 0 //->
	2. <-// ERR : Division by 0

	1. ASK p!{l //->
	2. <-// ERR : Expression must be entirely enclosed in parentheses.

    1. ASK (dgf) //->
    2. <-// ERR : Unknown characters in expression
