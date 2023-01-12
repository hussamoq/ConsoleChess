package Observer;
import Game.ChessGame;

public class CheckMateObserver implements Observer {
    private ChessGame game;
    public CheckMateObserver(ChessGame game){
        this.game = game;
    }

    @Override
    public void update() {
        game.setCheckMate();
    }
}
