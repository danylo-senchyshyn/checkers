package sk.tuke.gamestudio.service.gamestudio;

public class GameStudioException extends RuntimeException {
    public GameStudioException() {
    }

    public GameStudioException(String message) {
        super(message);
    }

    public GameStudioException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameStudioException(Throwable cause) {
        super(cause);
    }
}
