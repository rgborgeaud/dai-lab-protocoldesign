package ch.heig.dai.lab.protocoldesign;

public class IllegalCharacter extends RuntimeException{
    public IllegalCharacter(String message) {
        super(message);
    }
}
