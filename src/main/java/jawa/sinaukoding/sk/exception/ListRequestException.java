package jawa.sinaukoding.sk.exception;

public class ListRequestException extends RuntimeException {
    public ListRequestException (String message){
        super(message);
    }

    public ListRequestException (String message, Throwable cause){

        super(message,cause);
    }


    


}