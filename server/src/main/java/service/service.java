package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import model.UserData;
import server.alreadyTakenException;
import server.badRequestException;
import server.unauthorizedException;
import service.requestsAndResults.*;

public class service
{
    private final DataAccess dataAccess;

    public service(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }
    //clear
    public deleteResult delete(deleteRequest request)
    {
        dataAccess.clear();
        return new deleteResult(true);
    }
    //register. Return registerResult object
    public registerResult register(registerRequest request) throws Exception
    {
        if(request == null || request.username() == null || request.password() == null || request.email() == null)
            throw new badRequestException("bad request");
        if(dataAccess.userExists(request.username()))
            throw new alreadyTakenException("already taken");
        var authToken = dataAccess.createUser(new UserData(request.username(), request.password(), request.email()));
        return new registerResult(request.username(), authToken);
    }
    //logIn. Return logInResult object
    public logInResult logIn(logInRequest request) throws Exception
    {
        if(request == null || request.username() == null || request.password() == null)
            throw new badRequestException("bad request");
        if(!dataAccess.checkPassword(request.username(),request.password()))
            throw new unauthorizedException("unauthorized");
        var authToken = dataAccess.logInUser(dataAccess.getUser(request.username()));
        return new logInResult(request.username(), authToken);
    }
    //logOut. Return logOutResult object
    public logOutResult logOut(logOutRequest request) throws Exception
    {
        if(request == null || request.authToken() == null)
            throw new badRequestException("bad request");
        if(!dataAccess.getAuth(request.authToken()))
            throw new unauthorizedException("unauthorized");
        dataAccess.deleteAuth(request.authToken());
        return new logOutResult(true);
    }
    //list games. Return listGamesResult object
    public listGamesResult listGames(listGamesRequest request)
    {
        if(request == null || request.authToken() == null)
            throw new badRequestException("bad request");
        if(!dataAccess.getAuth(request.authToken()))
            throw new unauthorizedException("unauthorized");
        var games = dataAccess.listGames();
        return new listGamesResult(games);
    }
    //create game. Returns a createGameResult object
    public createGameResult createGame(createGameRequest request) throws Exception
    {
        if(request == null || request.authToken() == null || request.gameName() == null)
            throw new badRequestException("bad request");
        if(!dataAccess.getAuth(request.authToken()))
            throw new unauthorizedException("unauthorized");
        return new createGameResult(dataAccess.createGame(request.gameName()));
    }
    //join game. Returns a joinGameResult object
    public joinGameResult joinGame(joinGameRequest request) throws Exception
    {
        if(request == null || request.authToken() == null || request.color() == null || !dataAccess.gameExists(request.gameID()))
            throw new badRequestException("bad request");
        if(!dataAccess.getAuth(request.authToken()))
            throw new unauthorizedException("unauthorized");
        if(request.color() == ChessGame.TeamColor.WHITE && dataAccess.getGame(request.gameID()).whiteUsername() == null)
            throw new alreadyTakenException("already taken");
        else if (request.color() == ChessGame.TeamColor.BLACK && dataAccess.getGame(request.gameID()).blackUsername() == null)
            throw new alreadyTakenException("already taken");
        dataAccess.joinGame(request.gameID(),dataAccess.getUsernameWithAuthToken(request.authToken()),request.color());
        return new joinGameResult(true);
    }
}