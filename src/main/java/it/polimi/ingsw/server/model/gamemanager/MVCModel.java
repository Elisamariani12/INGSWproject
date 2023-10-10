package it.polimi.ingsw.server.model.gamemanager;

import it.polimi.ingsw.common.util.CardRepository;
import it.polimi.ingsw.common.util.Observable;
import it.polimi.ingsw.common.util.Observer;
import it.polimi.ingsw.server.model.gamemanager.GameSession;

import java.util.ArrayList;
import java.util.List;

/**
 * MVC Model component
 */
public class MVCModel implements Observable<GameSession>
{
    private GameSession gameSession;
    private List<Observer<GameSession>> registeredObservers;

    /**
     * Instantiates a new Mvc model.
     */
    public MVCModel() {
        this.gameSession = new GameSession(CardRepository.getInstance().getAllActionTokens(), CardRepository.getInstance().getAllLeaderCards());
        this.registeredObservers = new ArrayList<>();
    }

    /**
     * Return the game session
     *
     * @return gamesession game session
     */
    public GameSession getGameSession(){
        return gameSession;
    }

    /**
     * Return the observers of the current gamesession
     *
     * @return the list of observers
     */
    public List<Observer<GameSession>> getRegisteredObservers() {
        return registeredObservers;
    }

    @Override
    public void addObserver(Observer<GameSession> observer) {
        this.registeredObservers.add(observer);
    }

    @Override
    public void removeObserver(Observer<GameSession> observer) { this.registeredObservers.remove(observer);}

    @Override
    public void notify(GameSession gameSession) {
        for (Observer<GameSession> observer : registeredObservers) {
            observer.update(gameSession);
        }
    }
}
