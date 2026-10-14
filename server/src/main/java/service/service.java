package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.UserData;
import server.alreadyTakenException;
import server.badRequestException;
import server.unauthorizedException;

public class service
{
    private final DataAccess dataAccess;

    public service()
    {
        this.dataAccess = new MemoryDataAccess();
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
}