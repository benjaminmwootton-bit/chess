package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.UserData;

public class service
{
    private final DataAccess dataAccess;

    public service()
    {
        this.dataAccess = new MemoryDataAccess();
    }

    public registerResult register(registerRequest request) throws Exception
    {
        if(request == null || request.username() == null || request.password() == null || request.email() == null)
            throw new Exception("bad request");
        if(dataAccess.userExists(request.username()))
            throw new Exception("already taken");
        var authToken = dataAccess.createUser(new UserData(request.username(), request.password(), request.email()));
        return new registerResult(request.username(), authToken);
    }
}