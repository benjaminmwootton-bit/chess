package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;

public class service
{
    public static void main(String[] args)
    {
        try
        {
            DataAccess dataAccess = new MemoryDataAccess();
        }
        catch (Throwable ex)
        {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}