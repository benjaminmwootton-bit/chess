package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import model.UserData;
import server.AlreadyTakenException;
import server.BadRequestException;
import server.UnauthorizedException;
import service.requestsandresults.*;

public class Service
{
    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }
    //clear
    public DeleteResult delete()
    {
        dataAccess.clear();
        return new DeleteResult(true);
    }
    //register. Return registerResult object
    public RegisterResult register(RegisterRequest request) throws Exception
    {
        if(request == null || request.username() == null || request.password() == null || request.email() == null){
            throw new BadRequestException("bad request");}
        if(dataAccess.userExists(request.username())){
            throw new AlreadyTakenException("already taken");}
        var authToken = dataAccess.createUser(new UserData(request.username(), request.password(), request.email()));
        return new RegisterResult(request.username(), authToken);
    }
    //logIn. Return logInResult object
    public LogInResult logIn(LogInRequest request) throws Exception
    {
        if(request == null || request.username() == null || request.password() == null){
            throw new BadRequestException("bad request");}
        if(!dataAccess.checkPassword(request.username(),request.password())){
            throw new UnauthorizedException("unauthorized");}
        var authToken = dataAccess.logInUser(dataAccess.getUserWithUsername(request.username()));
        return new LogInResult(request.username(), authToken);
    }
    //logOut. Return logOutResult object
    public LogOutResult logOut(LogOutRequest request) throws Exception
    {
        if(request == null || request.authToken() == null){
            throw new BadRequestException("bad request");}
        if(!dataAccess.getAuth(request.authToken())){
            throw new UnauthorizedException("unauthorized");}
        dataAccess.deleteAuth(request.authToken());
        return new LogOutResult(true);
    }
    //list games. Return listGamesResult object
    public ListGamesResult listGames(ListGamesRequest request)
    {
        if(request == null || request.authToken() == null){
            throw new BadRequestException("bad request");}
        if(!dataAccess.getAuth(request.authToken())){
            throw new UnauthorizedException("unauthorized");}
        var games = dataAccess.listGames();
        return new ListGamesResult(games);
    }
    //create game. Returns a createGameResult object
    public CreateGameResult createGame(CreateGameRequest request) throws Exception
    {
        if(request == null || request.authToken() == null || request.gameName() == null){
            throw new BadRequestException("bad request");}
        if(!dataAccess.getAuth(request.authToken())){
            throw new UnauthorizedException("unauthorized");}
        return new CreateGameResult(dataAccess.createGame(request.gameName()));
    }
    //join game. Returns a joinGameResult object
    public JoinGameResult joinGame(JoinGameRequest request) throws Exception
    {
        if(request == null || request.authToken() == null || request.playerColor() == null || !dataAccess.gameExists(request.gameID())){
            throw new BadRequestException("bad request");}
        if(!dataAccess.getAuth(request.authToken())){
            throw new UnauthorizedException("unauthorized");}
        if(request.playerColor() == ChessGame.TeamColor.WHITE && dataAccess.getGame(request.gameID()).whiteUsername() != null){
            throw new AlreadyTakenException("already taken");}
        else if (request.playerColor() == ChessGame.TeamColor.BLACK && dataAccess.getGame(request.gameID()).blackUsername() != null){
            throw new AlreadyTakenException("already taken");}
        dataAccess.joinGame(request.gameID(),dataAccess.getUsernameWithAuthToken(request.authToken()),request.playerColor());
        return new JoinGameResult(true);
    }
}